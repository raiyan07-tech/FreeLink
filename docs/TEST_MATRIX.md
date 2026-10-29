# FREELINK — Test Matrix & Verification Protocols

This document outlines the testing protocols for validating FREELINK across simulated environments and physical Android hardware.

---

## 1. Supported Platform Matrix

| Android Version | API Level | Bluetooth Stack | Permissions Required | Test Status |
|---|---|---|---|---|
| Android 15 (Vanilla Ice Cream) | 35 | BLE + LE Audio | `BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`, `BLUETOOTH_ADVERTISE` | Verified |
| Android 14 (Upside Down Cake) | 34 | BLE Standard | `BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`, `BLUETOOTH_ADVERTISE` | Verified |
| Android 13 (Tiramisu) | 33 | BLE Standard | `BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`, `NEARBY_WIFI_DEVICES` | Verified |
| Android 12 / 12L (Snow Cone) | 31-32 | BLE Standard | `BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`, `BLUETOOTH_ADVERTISE` | Verified |
| Android 11 (Red Velvet Cake) | 30 | Legacy BLE | `ACCESS_FINE_LOCATION`, `BLUETOOTH`, `BLUETOOTH_ADMIN` | Verified |
| Android 10 (Queen Cake) | 29 | Legacy BLE | `ACCESS_FINE_LOCATION`, `BLUETOOTH`, `BLUETOOTH_ADMIN` | Verified |
| Android 8.0 - 9.0 (Oreo / Pie) | 26-28 | Legacy BLE | `ACCESS_COARSE_LOCATION`, `BLUETOOTH` | Verified (MinSdk) |

---

## 2. Core Functional Test Scenarios

### Test 1: Offline Startup (Airplane Mode)
* **Pre-condition**: Enable Airplane Mode; verify Wi-Fi and Cellular are off.
* **Execution**: Launch the FREELINK application from the launcher.
* **Expected Result**:
  - The app opens in under 1 second without crashing or displaying network timeouts.
  - The Status Card displays **"Offline Mode Active"**.
  - All local Travel Kit data, saved contacts, and emergency checklists are immediately viewable.

### Test 2: Emergency 112 Dialler Routing
* **Pre-condition**: App launched.
* **Execution**: Tap the **"EMERGENCY CALL — 112"** button on Home or Emergency screen.
* **Expected Result**:
  - The system phone dialler is launched with **`112`** pre-filled.
  - **No automated call is triggered without user confirmation in the dialler.**
  - Safe for compliance testing on both physical devices and emulators.

### Test 3: Offline Travel Kit Persistence
* **Pre-condition**: App launched in Airplane mode.
* **Execution**: Navigate to Travel Kit -> Tap Edit -> Enter Operator "Jio", Meeting Point "Gate 2", Contact "Mom: +919876543210", Blood Group "O+" -> Tap Save -> Force-kill app -> Relaunch.
* **Expected Result**:
  - Data is preserved intact from Room SQLite database.
  - Last updated timestamp updates accurately.

### Test 4: Developer Demo Mode Simulation
* **Pre-condition**: Single handset or Android Studio Emulator.
* **Execution**: Navigate to Nearby -> Toggle **"Developer Demo Mode"** to ON -> Tap **"Scan for Peers"**.
* **Expected Result**:
  - Simulated nearby peers appear within 800ms (`Demo Peer: Rahul`, `Demo Peer: Station Volunteer`).
  - Tapping **"Connect"** establishes a simulated session.
  - Tapping **"Chat"** and sending a text results in state transition:
    `QUEUED_LOCALLY` -> `SENDING` -> `SENT_TO_PEER` -> `ACKNOWLEDGED_BY_PEER`.
  - Peer automatically sends back an encrypted acknowledgement and simulated reply.

### Test 5: Two Physical Android Phones (Real BLE Radio)
* **Pre-condition**: Two physical Android handsets (Phone A and Phone B) with Bluetooth enabled.
* **Execution**:
  1. Phone A: Open Nearby -> Toggle Demo Mode OFF -> Tap **"Broadcast"**.
  2. Phone B: Open Nearby -> Toggle Demo Mode OFF -> Tap **"Scan for Peers"**.
  3. Phone B identifies Phone A in scan list -> Tap **"Connect"**.
  4. Phone A receives **"Nearby Connection Request"** modal -> Taps **"Allow Connection"**.
  5. Phone B sends: *"Are you near Platform 1?"*.
* **Expected Result**:
  - Phone B displays status `SENT_TO_PEER`, transitioning to `ACKNOWLEDGED_BY_PEER` upon receiving GATT write confirmation.
  - Phone A receives the decrypted text, stores it in Room DB, and displays it in the conversation thread.

### Test 6: Permission Handling & Denial
* **Execution**: Revoke Nearby Devices / Location permissions in Android App Info -> Tap **"Scan for Peers"**.
* **Expected Result**:
  - App requests permission gracefully via standard Android contract.
  - If permanently denied, app displays a clear recovery status message without crashing.

### Test 7: Data Retention & Purge
* **Execution**: Navigate to Settings -> Tap **"Delete All Local Handset Data"** -> Confirm in modal.
* **Expected Result**:
  - SQLite database is completely cleared.
  - Re-navigating to Contacts shows empty state.
