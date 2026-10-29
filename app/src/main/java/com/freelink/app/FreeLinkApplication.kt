package com.freelink.app

import android.app.Application
import com.freelink.app.data.local.FreeLinkDatabase
import com.freelink.app.data.local.repositories.RoomContactsRepository
import com.freelink.app.data.local.repositories.RoomMessageRepository
import com.freelink.app.data.local.repositories.RoomProfileRepository
import com.freelink.app.data.local.repositories.RoomTravelKitRepository
import com.freelink.app.identity.IdentityManager
import com.freelink.app.nearby.BluetoothLeTransport
import com.freelink.app.nearby.NearbyConnectionManager
import com.freelink.app.nearby.SimulationTransport
import com.freelink.app.online.ConnectivityMonitor

class FreeLinkApplication : Application() {

    lateinit var database: FreeLinkDatabase
        private set

    lateinit var travelKitRepository: RoomTravelKitRepository
        private set

    lateinit var contactsRepository: RoomContactsRepository
        private set

    lateinit var messageRepository: RoomMessageRepository
        private set

    lateinit var profileRepository: RoomProfileRepository
        private set

    lateinit var identityManager: IdentityManager
        private set

    lateinit var connectivityMonitor: ConnectivityMonitor
        private set

    lateinit var nearbyConnectionManager: NearbyConnectionManager
        private set

    override fun onCreate() {
        super.onCreate()

        database = FreeLinkDatabase.getInstance(this)

        travelKitRepository = RoomTravelKitRepository(database.travelKitDao())
        contactsRepository = RoomContactsRepository(database.contactDao())
        messageRepository = RoomMessageRepository(database.messageDao())
        profileRepository = RoomProfileRepository(database.profileDao())

        identityManager = IdentityManager(this)
        connectivityMonitor = ConnectivityMonitor(this)

        val bleTransport = BluetoothLeTransport(this)
        val simulationTransport = SimulationTransport()

        nearbyConnectionManager = NearbyConnectionManager(
            bleTransport = bleTransport,
            simulationTransport = simulationTransport,
            identityManager = identityManager,
            contactsRepository = contactsRepository,
            messageRepository = messageRepository
        )
    }
}
