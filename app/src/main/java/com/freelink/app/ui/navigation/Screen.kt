package com.freelink.app.ui.navigation

import android.net.Uri

sealed class Screen(val route: String, val title: String) {
    object Onboarding : Screen("onboarding", "Welcome")
    object Home : Screen("home", "FREELINK")
    object Emergency : Screen("emergency", "Emergency 112")
    object TravelKit : Screen("travel_kit", "Travel Kit")
    object Reconnect : Screen("reconnect", "Reconnect Help")
    object Nearby : Screen("nearby", "Nearby Offline")
    object Contacts : Screen("contacts", "Contacts")
    object Chat : Screen("chat/{contactId}/{flId}/{name}", "Chat") {
        fun createRoute(contactId: String, flId: String, name: String) =
            "chat/${Uri.encode(contactId)}/${Uri.encode(flId)}/${Uri.encode(name)}"
    }
    object Profile : Screen("profile", "My FreeLink ID")
    object Settings : Screen("settings", "Settings & Privacy")
}
