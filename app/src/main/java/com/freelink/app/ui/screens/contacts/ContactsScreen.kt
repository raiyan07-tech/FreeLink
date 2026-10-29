package com.freelink.app.ui.screens.contacts

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.VerifiedUser
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
import com.freelink.app.domain.models.Contact
import com.freelink.app.domain.models.TrustStatus
import com.freelink.app.domain.repository.ContactsRepository
import com.freelink.app.identity.IdentityManager
import com.freelink.app.identity.PublicContactCard
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
import com.freelink.app.ui.theme.StateWarning
import com.freelink.app.ui.theme.TextPrimary
import com.freelink.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun ContactsScreen(
    contactsRepository: ContactsRepository,
    onBack: () -> Unit,
    onOpenChat: (contactId: String, flId: String, name: String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val contacts by contactsRepository.getAllContacts().collectAsState(initial = emptyList())

    var showAddDialog by remember { mutableStateOf(false) }
    var showImportQrDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddContactDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newContact ->
                scope.launch {
                    contactsRepository.insertOrUpdate(newContact)
                    showAddDialog = false
                    Toast.makeText(context, "Contact added", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    if (showImportQrDialog) {
        ImportQrCardDialog(
            onDismiss = { showImportQrDialog = false },
            onImport = { card ->
                scope.launch {
                    val contact = Contact(
                        flId = card.flId,
                        displayName = card.displayName,
                        publicKeyFingerprint = card.publicKeyFingerprint,
                        trustStatus = TrustStatus.VERIFIED_QR
                    )
                    contactsRepository.insertOrUpdate(contact)
                    showImportQrDialog = false
                    Toast.makeText(context, "Verified contact imported via QR", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
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
                    text = "Trusted Contacts",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Row {
                IconButton(onClick = { showImportQrDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Import QR Card",
                        tint = ElectricCyan
                    )
                }
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Add Contact",
                        tint = ElectricBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Contacts are stored locally. FL ID is verified by cryptographic fingerprint.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (contacts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No contacts saved yet.",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                    ) {
                        Text("Add First Contact", color = Color.White)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(contacts) { contact ->
                    ContactItemCard(
                        contact = contact,
                        onOpenChat = { onOpenChat(contact.id, contact.flId, contact.displayName) },
                        onToggleBlock = {
                            scope.launch {
                                contactsRepository.setBlocked(contact.id, !contact.isBlocked)
                            }
                        },
                        onToggleTrust = {
                            scope.launch {
                                val nextStatus = if (contact.trustStatus == TrustStatus.VERIFIED_FINGERPRINT)
                                    TrustStatus.UNVERIFIED
                                else
                                    TrustStatus.VERIFIED_FINGERPRINT
                                contactsRepository.updateTrustStatus(contact.id, nextStatus)
                            }
                        },
                        onDelete = {
                            scope.launch {
                                contactsRepository.deleteContact(contact.id)
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        DisclaimerCallout(
            text = "Security reminder: a public FL ID alone is not proof of identity. " +
                   "Always compare fingerprints in person or scan the verified QR code."
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ContactItemCard(
    contact: Contact,
    onOpenChat: () -> Unit,
    onToggleBlock: () -> Unit,
    onToggleTrust: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (contact.isBlocked) DarkSurface.copy(alpha = 0.5f) else DarkSurface
        ),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = contact.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        if (contact.isBlocked) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "[BLOCKED]", color = EmergencyRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        text = "FL ID: ${contact.flId}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ElectricCyan,
                        fontSize = 12.sp
                    )
                }
                Row {
                    IconButton(onClick = onOpenChat) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = "Chat", tint = ElectricBlue)
                    }
                    IconButton(onClick = onToggleTrust) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Verify Trust",
                            tint = if (contact.trustStatus != TrustStatus.UNVERIFIED) StateSuccess else TextSecondary
                        )
                    }
                    IconButton(onClick = onToggleBlock) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = "Block",
                            tint = if (contact.isBlocked) EmergencyRed else TextSecondary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Key Fingerprint: ${contact.publicKeyFingerprint.ifBlank { "Unverified" }}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun AddContactDialog(
    onDismiss: () -> Unit,
    onSave: (Contact) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var flId by remember { mutableStateOf("") }
    var fingerprint by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = { Text("Add Trusted Contact", color = TextPrimary) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = flId,
                    onValueChange = { flId = it },
                    label = { Text("10-Digit FreeLink ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = fingerprint,
                    onValueChange = { fingerprint = it },
                    label = { Text("Safety Number / Fingerprint (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                errorMessage?.let { err ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = err, color = EmergencyRed, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Please enter a name."
                        return@Button
                    }
                    if (!IdentityManager.isValidFlId(flId)) {
                        errorMessage = "FreeLink ID must be exactly 10 digits."
                        return@Button
                    }
                    val newContact = Contact(
                        flId = flId,
                        displayName = name,
                        publicKeyFingerprint = fingerprint.ifBlank { "Unverified manual contact" },
                        trustStatus = TrustStatus.UNVERIFIED
                    )
                    onSave(newContact)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text("Save", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun ImportQrCardDialog(
    onDismiss: () -> Unit,
    onImport: (PublicContactCard) -> Unit
) {
    var rawJson by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = { Text("Import QR Contact Card", color = TextPrimary) },
        text = {
            Column {
                Text(
                    text = "Paste the FreeLink QR contact payload string below:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = rawJson,
                    onValueChange = { rawJson = it },
                    label = { Text("QR Card Data") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
                error?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = it, color = EmergencyRed, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val card = PublicContactCard.fromJsonString(rawJson)
                    if (card == null) {
                        error = "Invalid FreeLink QR Card JSON format"
                    } else {
                        onImport(card)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text("Import Card", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
