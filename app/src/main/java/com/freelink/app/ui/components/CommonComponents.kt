package com.freelink.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freelink.app.domain.models.DeliveryState
import com.freelink.app.domain.models.NetworkState
import com.freelink.app.domain.models.TransportType
import com.freelink.app.ui.theme.DarkBorder
import com.freelink.app.ui.theme.DarkSurface
import com.freelink.app.ui.theme.DarkSurfaceVariant
import com.freelink.app.ui.theme.ElectricBlue
import com.freelink.app.ui.theme.ElectricCyan
import com.freelink.app.ui.theme.EmergencyRed
import com.freelink.app.ui.theme.StateError
import com.freelink.app.ui.theme.StateOffline
import com.freelink.app.ui.theme.StateSuccess
import com.freelink.app.ui.theme.StateWarning
import com.freelink.app.ui.theme.TextPrimary
import com.freelink.app.ui.theme.TextSecondary

@Composable
fun StatusCard(
    networkState: NetworkState,
    isDemoMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val (statusTitle, statusDesc, dotColor) = when (networkState) {
        NetworkState.INTERNET_AVAILABLE -> Triple(
            "Internet Connected",
            "Online directory & carrier websites accessible.",
            StateSuccess
        )
        NetworkState.CELLULAR_NO_DATA -> Triple(
            "Cellular Signal • No Data Link",
            "Prepaid validity or data exhausted. Carrier calls may be barred.",
            StateWarning
        )
        NetworkState.LOCAL_RADIO_ONLY -> Triple(
            "Local Offline Mode",
            "Bluetooth active. Ready for nearby device exchange.",
            ElectricCyan
        )
        NetworkState.OFFLINE -> Triple(
            "Offline Mode Active",
            "No internet detected. Local travel kit and 112 emergency dialler are ready.",
            StateOffline
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = statusTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = statusDesc,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            if (isDemoMode) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ElectricBlue.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "DEVELOPER DEMO SIMULATION ACTIVE",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun EmergencyDialButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = EmergencyRed,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "Emergency Call 112",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "EMERGENCY CALL — 112",
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Opens native dialler • No balance needed",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
fun DeliveryStateBadge(
    state: DeliveryState,
    modifier: Modifier = Modifier
) {
    val (label, color, icon) = when (state) {
        DeliveryState.QUEUED_LOCALLY -> Triple("Queued locally", StateWarning, Icons.Default.Schedule)
        DeliveryState.SENDING -> Triple("Sending...", ElectricBlue, Icons.Default.Schedule)
        DeliveryState.SENT_TO_PEER -> Triple("Sent to peer", ElectricCyan, Icons.Default.Check)
        DeliveryState.ACKNOWLEDGED_BY_PEER -> Triple("Peer Acknowledged", StateSuccess, Icons.Default.Check)
        DeliveryState.FAILED -> Triple("Failed", StateError, Icons.Default.Close)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontSize = 10.sp
        )
    }
}

@Composable
fun TransportBadge(
    transport: TransportType,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (transport) {
        TransportType.BLUETOOTH_LE -> Pair("BLE Radio", ElectricCyan)
        TransportType.WIFI_DIRECT -> Pair("Wi-Fi Direct", ElectricBlue)
        TransportType.LOCAL_DEMO_SIMULATION -> Pair("Demo Mode", StateWarning)
        TransportType.INTERNET_RELAY -> Pair("Internet Relay", StateSuccess)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontSize = 10.sp
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(18.dp)
                .background(ElectricCyan, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}

@Composable
fun DisclaimerCallout(
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant.copy(alpha = 0.6f)),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Notice",
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun PeerConsentDialog(
    peerName: String,
    flId: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDecline,
        containerColor = DarkSurface,
        title = {
            Text(
                text = "Nearby Connection Request",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
        },
        text = {
            Column {
                Text(
                    text = "A nearby FreeLink device wants to establish a local offline communication channel with you:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Peer: $peerName",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan
                )
                Text(
                    text = "FreeLink ID: $flId",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No private data, phone numbers, or passwords will be shared.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onAccept,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text("Allow Connection", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDecline) {
                Text("Decline", color = TextSecondary)
            }
        }
    )
}
