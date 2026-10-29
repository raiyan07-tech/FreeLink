package com.freelink.app.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.freelink.app.domain.models.NetworkState
import com.freelink.app.domain.repository.ContactsRepository
import com.freelink.app.domain.repository.MessageRepository
import com.freelink.app.domain.repository.TravelKitRepository
import com.freelink.app.identity.IdentityManager
import com.freelink.app.nearby.NearbyConnectionManager
import com.freelink.app.online.ConnectivityMonitor
import com.freelink.app.ui.screens.chat.ChatScreen
import com.freelink.app.ui.screens.contacts.ContactsScreen
import com.freelink.app.ui.screens.emergency.EmergencyScreen
import com.freelink.app.ui.screens.home.HomeScreen
import com.freelink.app.ui.screens.nearby.NearbyScreen
import com.freelink.app.ui.screens.onboarding.OnboardingScreen
import com.freelink.app.ui.screens.profile.ProfileScreen
import com.freelink.app.ui.screens.reconnect.ReconnectScreen
import com.freelink.app.ui.screens.settings.SettingsScreen
import com.freelink.app.ui.screens.travelkit.TravelKitScreen

@Composable
fun FreeLinkNavGraph(
    navController: NavHostController,
    connectivityMonitor: ConnectivityMonitor,
    nearbyManager: NearbyConnectionManager,
    identityManager: IdentityManager,
    travelKitRepository: TravelKitRepository,
    contactsRepository: ContactsRepository,
    messageRepository: MessageRepository
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("freelink_prefs", Context.MODE_PRIVATE)
    val hasCompletedOnboarding = prefs.getBoolean("onboarding_complete", false)
    val startDest = if (hasCompletedOnboarding) Screen.Home.route else Screen.Onboarding.route

    val networkState by connectivityMonitor.networkState.collectAsState()
    val localFlId = identityManager.getOrCreateFlId()

    NavHost(
        navController = navController,
        startDestination = startDest
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                networkState = networkState,
                nearbyManager = nearbyManager,
                localFlId = localFlId,
                onNavigateToEmergency = { navController.navigate(Screen.Emergency.route) },
                onNavigateToTravelKit = { navController.navigate(Screen.TravelKit.route) },
                onNavigateToNearby = { navController.navigate(Screen.Nearby.route) },
                onNavigateToReconnect = { navController.navigate(Screen.Reconnect.route) },
                onNavigateToContacts = { navController.navigate(Screen.Contacts.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToChat = { contactId, flId, name ->
                    navController.navigate(Screen.Chat.createRoute(contactId, flId, name))
                }
            )
        }

        composable(Screen.Emergency.route) {
            EmergencyScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TravelKit.route) {
            TravelKitScreen(
                repository = travelKitRepository,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Reconnect.route) {
            ReconnectScreen(
                networkState = networkState,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Nearby.route) {
            NearbyScreen(
                nearbyManager = nearbyManager,
                onBack = { navController.popBackStack() },
                onOpenChat = { contactId, flId, name ->
                    navController.navigate(Screen.Chat.createRoute(contactId, flId, name))
                }
            )
        }

        composable(Screen.Contacts.route) {
            ContactsScreen(
                contactsRepository = contactsRepository,
                onBack = { navController.popBackStack() },
                onOpenChat = { contactId, flId, name ->
                    navController.navigate(Screen.Chat.createRoute(contactId, flId, name))
                }
            )
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("contactId") { type = NavType.StringType },
                navArgument("flId") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getString("contactId") ?: ""
            val flId = backStackEntry.arguments?.getString("flId") ?: ""
            val name = backStackEntry.arguments?.getString("name") ?: "Contact"

            ChatScreen(
                contactId = contactId,
                peerFlId = flId,
                peerDisplayName = name,
                localFlId = localFlId,
                messageRepository = messageRepository,
                nearbyManager = nearbyManager,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                identityManager = identityManager,
                displayName = "My FreeLink",
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                nearbyManager = nearbyManager,
                messageRepository = messageRepository,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
