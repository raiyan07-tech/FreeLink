package com.freelink.app.nearby

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import androidx.core.content.ContextCompat
import com.freelink.app.domain.models.PeerDevice
import com.freelink.app.domain.models.TransportType
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Production Bluetooth Low Energy (BLE) implementation.
 * Strictly checks permissions and Bluetooth hardware state before attempting radio operations.
 */
class BluetoothLeTransport(private val context: Context) : NearbyTransport {

    override val transportType: TransportType = TransportType.BLUETOOTH_LE

    companion object {
        val FREELINK_SERVICE_UUID: UUID = UUID.fromString("0000FEF0-0000-1000-8000-00805F9B34FB")
        val FREELINK_CHAR_UUID: UUID = UUID.fromString("0000FEF1-0000-1000-8000-00805F9B34FB")
    }

    private val bluetoothManager: BluetoothManager? =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

    private var scanner: BluetoothLeScanner? = null
    private var advertiser: BluetoothLeAdvertiser? = null
    private var gattServer: BluetoothGattServer? = null
    private val activeGattConnections = ConcurrentHashMap<String, BluetoothGatt>()

    private var discoveryListener: DiscoveryListener? = null
    private var connectionListener: ConnectionListener? = null

    private var isScanning = false
    private var isAdvertising = false

    override fun isAvailable(): Boolean {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled
    }

    private fun hasPermission(perm: String): Boolean {
        return ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasRequiredScanPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            hasPermission(android.Manifest.permission.BLUETOOTH_SCAN) &&
            hasPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun hasRequiredAdvertisePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            hasPermission(android.Manifest.permission.BLUETOOTH_ADVERTISE) &&
            hasPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            hasPermission(android.Manifest.permission.BLUETOOTH_ADMIN)
        }
    }

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result ?: return
            val device = result.device
            val scanRecord = result.scanRecord ?: return
            val deviceName = scanRecord.deviceName ?: device.name ?: "Unknown Peer"

            // Look for FL ID encoded in the local name or service data
            // Format: "FL-9123456789"
            val flId = if (deviceName.startsWith("FL-") && deviceName.length >= 13) {
                deviceName.substring(3, 13)
            } else {
                "Provisional"
            }

            val peer = PeerDevice(
                id = device.address,
                flId = flId,
                displayName = deviceName.removePrefix("FL-"),
                rssi = result.rssi,
                transportType = TransportType.BLUETOOTH_LE,
                lastDiscoveredTimestamp = System.currentTimeMillis()
            )
            discoveryListener?.onPeerDiscovered(peer)
        }

        override fun onScanFailed(errorCode: Int) {
            discoveryListener?.onDiscoveryFailed(errorCode, "BLE scan failed with error $errorCode")
        }
    }

    @SuppressLint("MissingPermission")
    override fun startDiscovery(listener: DiscoveryListener) {
        this.discoveryListener = listener
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            listener.onDiscoveryFailed(-1, "Bluetooth is turned off")
            return
        }
        if (!hasRequiredScanPermissions()) {
            listener.onDiscoveryFailed(-2, "Nearby Devices / Bluetooth permission not granted")
            return
        }

        try {
            scanner = bluetoothAdapter.bluetoothLeScanner
            if (scanner == null) {
                listener.onDiscoveryFailed(-3, "Bluetooth LE Scanner is unavailable")
                return
            }

            val filter = ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(FREELINK_SERVICE_UUID))
                .build()

            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()

            scanner?.startScan(listOf(filter), settings, scanCallback)
            isScanning = true
        } catch (e: Exception) {
            listener.onDiscoveryFailed(-4, e.localizedMessage ?: "Unknown scan error")
        }
    }

    @SuppressLint("MissingPermission")
    override fun stopDiscovery() {
        if (isScanning && scanner != null && hasRequiredScanPermissions()) {
            try {
                scanner?.stopScan(scanCallback)
            } catch (_: Exception) {}
        }
        isScanning = false
        discoveryListener?.onDiscoveryStopped()
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            isAdvertising = true
        }

        override fun onStartFailure(errorCode: Int) {
            isAdvertising = false
            connectionListener?.onError("Advertising failed with code: $errorCode")
        }
    }

    @SuppressLint("MissingPermission")
    override fun startAdvertising(localFlId: String, displayName: String) {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled || !hasRequiredAdvertisePermissions()) {
            return
        }

        try {
            advertiser = bluetoothAdapter.bluetoothLeAdvertiser ?: return
            initGattServer()

            val settings = AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build()

            // Service Data carries prefix: FL-<10 digit ID>
            val advertiseName = "FL-$localFlId"
            val data = AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .addServiceUuid(ParcelUuid(FREELINK_SERVICE_UUID))
                .build()

            advertiser?.startAdvertising(settings, data, advertiseCallback)
        } catch (e: Exception) {
            connectionListener?.onError("Could not start advertising: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    override fun stopAdvertising() {
        if (isAdvertising && advertiser != null && hasRequiredAdvertisePermissions()) {
            try {
                advertiser?.stopAdvertising(advertiseCallback)
            } catch (_: Exception) {}
        }
        isAdvertising = false
        gattServer?.close()
        gattServer = null
    }

    @SuppressLint("MissingPermission")
    private fun initGattServer() {
        if (gattServer != null || !hasRequiredAdvertisePermissions()) return
        gattServer = bluetoothManager?.openGattServer(context, object : BluetoothGattServerCallback() {
            override fun onCharacteristicWriteRequest(
                device: BluetoothDevice?,
                requestId: Int,
                characteristic: BluetoothGattCharacteristic?,
                preparedWrite: Boolean,
                responseNeeded: Boolean,
                offset: Int,
                value: ByteArray?
            ) {
                if (responseNeeded) {
                    gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value)
                }
                if (value != null && device != null) {
                    val payload = String(value, Charsets.UTF_8)
                    connectionListener?.onPayloadReceived(device.address, payload)
                }
            }
        })

        val service = BluetoothGattService(FREELINK_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)
        val char = BluetoothGattCharacteristic(
            FREELINK_CHAR_UUID,
            BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_WRITE,
            BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE
        )
        service.addCharacteristic(char)
        gattServer?.addService(service)
    }

    @SuppressLint("MissingPermission")
    override fun connectToPeer(peer: PeerDevice, listener: ConnectionListener) {
        this.connectionListener = listener
        if (bluetoothAdapter == null || !hasRequiredScanPermissions()) {
            listener.onError("Cannot connect: Bluetooth not ready or permissions missing")
            return
        }

        try {
            val device = bluetoothAdapter.getRemoteDevice(peer.id)
            val gatt = device.connectGatt(context, false, object : BluetoothGattCallback() {
                override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        gatt?.discoverServices()
                        activeGattConnections[peer.id] = gatt!!
                        listener.onConnected(peer.id)
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        activeGattConnections.remove(peer.id)
                        listener.onDisconnected(peer.id)
                    }
                }

                override fun onCharacteristicChanged(
                    gatt: BluetoothGatt,
                    characteristic: BluetoothGattCharacteristic,
                    value: ByteArray
                ) {
                    val payload = String(value, Charsets.UTF_8)
                    listener.onPayloadReceived(peer.id, payload)
                }
            })
            activeGattConnections[peer.id] = gatt
        } catch (e: Exception) {
            listener.onError("Connection failed: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    override fun disconnect(peerId: String) {
        val gatt = activeGattConnections.remove(peerId)
        if (hasRequiredScanPermissions()) {
            gatt?.disconnect()
            gatt?.close()
        }
    }

    @SuppressLint("MissingPermission")
    override fun sendPayload(peerId: String, payload: String, callback: (Boolean, String?) -> Unit) {
        val gatt = activeGattConnections[peerId]
        if (gatt == null) {
            callback(false, "Device not connected via BLE")
            return
        }
        val service = gatt.getService(FREELINK_SERVICE_UUID)
        val char = service?.getCharacteristic(FREELINK_CHAR_UUID)
        if (char == null) {
            callback(false, "FreeLink BLE characteristic not found on peer")
            return
        }
        char.value = payload.toByteArray(Charsets.UTF_8)
        val success = gatt.writeCharacteristic(char)
        if (success) {
            callback(true, null)
        } else {
            callback(false, "Write characteristic failed")
        }
    }

    override fun setConnectionListener(listener: ConnectionListener) {
        this.connectionListener = listener
    }
}
