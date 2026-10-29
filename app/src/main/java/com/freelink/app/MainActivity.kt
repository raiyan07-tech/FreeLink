package com.freelink.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.freelink.app.ui.navigation.FreeLinkNavGraph
import com.freelink.app.ui.theme.FreeLinkTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as FreeLinkApplication

        setContent {
            FreeLinkTheme {
                val navController = rememberNavController()
                FreeLinkNavGraph(
                    navController = navController,
                    connectivityMonitor = app.connectivityMonitor,
                    nearbyManager = app.nearbyConnectionManager,
                    identityManager = app.identityManager,
                    travelKitRepository = app.travelKitRepository,
                    contactsRepository = app.contactsRepository,
                    messageRepository = app.messageRepository
                )
            }
        }
    }
}
