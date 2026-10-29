package com.freelink.app.ui.screens.onboarding

import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freelink.app.ui.theme.DarkBackground
import com.freelink.app.ui.theme.DarkBorder
import com.freelink.app.ui.theme.DarkSurface
import com.freelink.app.ui.theme.ElectricBlue
import com.freelink.app.ui.theme.ElectricCyan
import com.freelink.app.ui.theme.EmergencyRed
import com.freelink.app.ui.theme.TextPrimary
import com.freelink.app.ui.theme.TextSecondary

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(20.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "FREELINK",
            style = MaterialTheme.typography.headlineLarge,
            color = ElectricCyan,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        Text(
            text = "Stay connected when your recharge runs out.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Honest Technical Boundary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Honest Technical Boundary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Signal bars ≠ Active prepaid benefits. Cellular network routing is strictly controlled by your telecom operator.\n" +
                           "• FreeLink CANNOT bypass carrier restrictions or place free remote phone calls without active service.\n" +
                           "• Emergency 112 calls are handled directly by India's Emergency Response Support System via your device's native dialler.\n" +
                           "• Nearby messaging works over local radio (Bluetooth/Wi-Fi) with other FreeLink devices in physical proximity.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Core Features & Permissions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        PermissionItem(
            icon = Icons.Default.Phone,
            title = "Emergency 112 Shortcut",
            desc = "Instant one-tap access to your device's native dialler for India 112 emergency calling without needing data or balance."
        )

        PermissionItem(
            icon = Icons.Default.Security,
            title = "Offline Travel Safety Kit",
            desc = "Saves your trusted emergency contacts, operator USSD shortcuts, and travel notes securely on your local storage."
        )

        PermissionItem(
            icon = Icons.Default.Bluetooth,
            title = "Nearby Device Discovery",
            desc = "Requires Nearby Devices / Bluetooth permission when scanning to find nearby FreeLink users for local text exchange."
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val prefs = context.getSharedPreferences("freelink_prefs", Context.MODE_PRIVATE)
                prefs.edit().putBoolean("onboarding_complete", true).apply()
                onFinished()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
        ) {
            Text(
                text = "I Understand • Get Started",
                style = MaterialTheme.typography.labelLarge,
                fontSize = 16.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun PermissionItem(
    icon: ImageVector,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(ElectricBlue.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = desc, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}
