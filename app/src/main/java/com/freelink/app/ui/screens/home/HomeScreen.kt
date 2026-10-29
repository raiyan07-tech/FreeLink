package com.freelink.app.ui.screens.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freelink.app.domain.models.NetworkState
import com.freelink.app.nearby.NearbyConnectionManager
import com.freelink.app.safety.EmergencyManager
import com.freelink.app.ui.components.DisclaimerCallout
import com.freelink.app.ui.components.EmergencyDialButton
import com.freelink.app.ui.components.PeerConsentDialog
import com.freelink.app.ui.components.SectionHeader
import com.freelink.app.ui.components.StatusCard
import com.freelink.app.ui.theme.DarkBackground
import com.freelink.app.ui.theme.DarkBorder
import com.freelink.app.ui.theme.DarkSurface
import com.freelink.app.ui.theme.DarkSurfaceVariant
import com.freelink.app.ui.theme.ElectricBlue
import com.freelink.app.ui.theme.ElectricCyan
import com.freelink.app.ui.theme.EmergencyRed
import com.freelink.app.ui.theme.StateSuccess
import com.freelink.app.ui.theme.TextPrimary
import com.freelink.app.ui.theme.TextSecondary

data class HomeActionItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color,
    val onClick: () -> Unit
)

@Composable
fun HomeScreen(
    networkState: NetworkState,
    nearbyManager: NearbyConnectionManager,
    localFlId: String,
    onNavigateToEmergency: () -> Unit,
    onNavigateToTravelKit: () -> Unit,
    onNavigateToNearby: () -> Unit,
    onNavigateToReconnect: () -> Unit,
    onNavigateToContacts: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToChat: (contactId: String, flId: String, name: String) -> Unit
) {
    val context = LocalContext.current
    val nearbyState by nearbyManager.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Handle peer consent dialog if any
    nearbyState.incomingConsentPeer?.let { consentPeer ->
        PeerConsentDialog(
            peerName = consentPeer.displayName,
            flId = consentPeer.flId,
            onAccept = { nearbyManager.respondToConsent(true) },
            onDecline = { nearbyManager.respondToConsent(false) }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "FREELINK",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = ElectricCyan,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "ID: $localFlId",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            Row {
                IconButton(onClick = onNavigateToProfile) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "My QR ID",
                        tint = ElectricCyan
                    )
                }
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Live Truthful Network Status Card
        StatusCard(
            networkState = networkState,
            isDemoMode = nearbyState.isDemoModeActive
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Emergency Call Native Button
        EmergencyDialButton(
            onClick = {
                try {
                    context.startActivity(EmergencyManager.createEmergencyDialIntent())
                } catch (_: Exception) {
                    onNavigateToEmergency()
                }
            }
        )

        // Connected Peer Banner (if any)
        nearbyState.connectedPeer?.let { peer ->
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onNavigateToChat(peer.id, peer.flId, peer.displayName)
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StateSuccess))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Bluetooth, contentDescription = null, tint = StateSuccess)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Connected: ${peer.displayName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Tap to open offline encrypted chat",
                            style = MaterialTheme.typography.bodyMedium,
                            color = StateSuccess,
                            fontSize = 12.sp
                        )
                    }
                    Icon(imageVector = Icons.Default.Chat, contentDescription = "Chat", tint = ElectricCyan)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader(title = "Travel Safety & Navigation")

        val actions = listOf(
            HomeActionItem(
                title = "Emergency",
                subtitle = "Checklist, landmarks & battery saver",
                icon = Icons.Default.Warning,
                accentColor = EmergencyRed,
                onClick = onNavigateToEmergency
            ),
            HomeActionItem(
                title = "Offline Travel Kit",
                subtitle = "Emergency contacts, notes & medical",
                icon = Icons.Default.MedicalServices,
                accentColor = ElectricBlue,
                onClick = onNavigateToTravelKit
            ),
            HomeActionItem(
                title = "Nearby Discovery",
                subtitle = "Scan & message nearby devices offline",
                icon = Icons.Default.Bluetooth,
                accentColor = ElectricCyan,
                onClick = onNavigateToNearby
            ),
            HomeActionItem(
                title = "Reconnect Help",
                subtitle = "Official USSD & Wi-Fi guidelines",
                icon = Icons.Default.Wifi,
                accentColor = Color(0xFF10B981),
                onClick = onNavigateToReconnect
            ),
            HomeActionItem(
                title = "Contacts",
                subtitle = "Trusted list, QR add & block",
                icon = Icons.Default.ContactPhone,
                accentColor = Color(0xFFA855F7),
                onClick = onNavigateToContacts
            ),
            HomeActionItem(
                title = "My FreeLink ID",
                subtitle = "10-digit ID card & QR share",
                icon = Icons.Default.Person,
                accentColor = Color(0xFFF59E0B),
                onClick = onNavigateToProfile
            )
        )

        actions.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { item ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(115.dp)
                            .clickable(onClick = item.onClick),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(item.accentColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = item.accentColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = item.subtitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        DisclaimerCallout(
            text = "Notice: FreeLink operates strictly within legal and technical boundaries. " +
                   "It cannot bypass telecom operator billing to place ordinary carrier calls or send carrier SMS without service."
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
