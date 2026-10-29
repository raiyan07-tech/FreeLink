package com.freelink.app.ui.screens.travelkit

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freelink.app.domain.models.TravelKit
import com.freelink.app.domain.repository.TravelKitRepository
import com.freelink.app.ui.components.DisclaimerCallout
import com.freelink.app.ui.components.SectionHeader
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TravelKitScreen(
    repository: TravelKitRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val travelKitFlow by repository.getTravelKit().collectAsState(initial = null)
    val travelKit = travelKitFlow ?: TravelKit()

    var showEditDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val formattedDate = remember(travelKit.lastUpdated) {
        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(travelKit.lastUpdated))
    }

    if (showEditDialog) {
        EditTravelKitDialog(
            currentKit = travelKit,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                scope.launch {
                    repository.saveTravelKit(updated)
                    showEditDialog = false
                    Toast.makeText(context, "Offline Travel Kit saved", Toast.LENGTH_SHORT).show()
                }
            }
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Offline Travel Kit",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            IconButton(onClick = { showEditDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Travel Kit",
                    tint = ElectricCyan
                )
            }
        }

        Text(
            text = "Stored 100% locally on your phone. Last updated: $formattedDate",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mobile Operator Card
        SectionHeader(title = "Mobile Operator & USSD")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.SimCard, contentDescription = null, tint = ElectricCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Primary Carrier: ${travelKit.primaryOperator}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "USSD Dialler Code: ${travelKit.operatorUssdHelp}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ElectricCyan
                )
                if (travelKit.userRechargeNotes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "User Notes / Offline Steps:\n${travelKit.userRechargeNotes}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Trusted Emergency Contacts Card
        SectionHeader(title = "Trusted Emergency Contacts")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (travelKit.emergencyContactPhone1.isNotBlank()) {
                    ContactRow(
                        name = travelKit.emergencyContactName1.ifBlank { "Emergency Contact 1" },
                        phone = travelKit.emergencyContactPhone1,
                        onCall = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${travelKit.emergencyContactPhone1}"))
                            context.startActivity(intent)
                        }
                    )
                }
                if (travelKit.emergencyContactPhone2.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    ContactRow(
                        name = travelKit.emergencyContactName2.ifBlank { "Emergency Contact 2" },
                        phone = travelKit.emergencyContactPhone2,
                        onCall = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${travelKit.emergencyContactPhone2}"))
                            context.startActivity(intent)
                        }
                    )
                }
                if (travelKit.emergencyContactPhone1.isBlank() && travelKit.emergencyContactPhone2.isBlank()) {
                    Text(
                        text = "No emergency contacts configured yet. Tap the edit icon above to add family or travel buddies.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Travel Destination & Meeting Point
        SectionHeader(title = "Trip Location & Meeting Point")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = ElectricBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Designated Meeting Point:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Text(
                    text = travelKit.meetingPoint.ifBlank { "None set (e.g. Exit Gate 3, Clock Tower)" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (travelKit.meetingPoint.isNotBlank()) TextPrimary else TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Destination Address / Hotel:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = travelKit.destinationAddress.ifBlank { "None set" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (travelKit.destinationAddress.isNotBlank()) TextPrimary else TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Medical & Emergency Notes
        SectionHeader(title = "Medical & Emergency Profile")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.MedicalInformation, contentDescription = null, tint = EmergencyRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Blood Group: ${travelKit.bloodGroup.ifBlank { "Not specified" }}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Medical Conditions / Allergies / Notes:\n${travelKit.emergencyMedicalNotes.ifBlank { "None recorded." }}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        DisclaimerCallout(text = "Travel Kit data is stored exclusively in your handset's local SQLite database. It is never uploaded to external servers without explicit consent.")

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ContactRow(
    name: String,
    phone: String,
    onCall: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Text(text = phone, style = MaterialTheme.typography.bodyMedium, color = ElectricCyan)
        }
        IconButton(onClick = onCall) {
            Icon(imageVector = Icons.Default.Call, contentDescription = "Dial", tint = StateSuccess)
        }
    }
}

@Composable
fun EditTravelKitDialog(
    currentKit: TravelKit,
    onDismiss: () -> Unit,
    onSave: (TravelKit) -> Unit
) {
    var primaryOperator by remember { mutableStateOf(currentKit.primaryOperator) }
    var userRechargeNotes by remember { mutableStateOf(currentKit.userRechargeNotes) }
    var contactName1 by remember { mutableStateOf(currentKit.emergencyContactName1) }
    var contactPhone1 by remember { mutableStateOf(currentKit.emergencyContactPhone1) }
    var contactName2 by remember { mutableStateOf(currentKit.emergencyContactName2) }
    var contactPhone2 by remember { mutableStateOf(currentKit.emergencyContactPhone2) }
    var meetingPoint by remember { mutableStateOf(currentKit.meetingPoint) }
    var destinationAddress by remember { mutableStateOf(currentKit.destinationAddress) }
    var bloodGroup by remember { mutableStateOf(currentKit.bloodGroup) }
    var medicalNotes by remember { mutableStateOf(currentKit.emergencyMedicalNotes) }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = { Text("Edit Offline Travel Kit", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                OutlinedTextField(
                    value = primaryOperator,
                    onValueChange = { primaryOperator = it },
                    label = { Text("Primary Operator (Airtel, Jio, Vi, BSNL)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = userRechargeNotes,
                    onValueChange = { userRechargeNotes = it },
                    label = { Text("Recharge Steps / Voucher Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = contactName1,
                    onValueChange = { contactName1 = it },
                    label = { Text("Emergency Contact 1 Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = contactPhone1,
                    onValueChange = { contactPhone1 = it },
                    label = { Text("Emergency Contact 1 Phone") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = contactName2,
                    onValueChange = { contactName2 = it },
                    label = { Text("Emergency Contact 2 Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = contactPhone2,
                    onValueChange = { contactPhone2 = it },
                    label = { Text("Emergency Contact 2 Phone") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = meetingPoint,
                    onValueChange = { meetingPoint = it },
                    label = { Text("Meeting Point") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = destinationAddress,
                    onValueChange = { destinationAddress = it },
                    label = { Text("Destination / Hotel Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = bloodGroup,
                    onValueChange = { bloodGroup = it },
                    label = { Text("Blood Group (e.g. O+, B+)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = medicalNotes,
                    onValueChange = { medicalNotes = it },
                    label = { Text("Medical Conditions / Allergies") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = currentKit.copy(
                        primaryOperator = primaryOperator,
                        userRechargeNotes = userRechargeNotes,
                        emergencyContactName1 = contactName1,
                        emergencyContactPhone1 = contactPhone1,
                        emergencyContactName2 = contactName2,
                        emergencyContactPhone2 = contactPhone2,
                        meetingPoint = meetingPoint,
                        destinationAddress = destinationAddress,
                        bloodGroup = bloodGroup,
                        emergencyMedicalNotes = medicalNotes,
                        lastUpdated = System.currentTimeMillis()
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text("Save Locally", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
