package com.freelink.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.freelink.app.domain.models.Contact
import com.freelink.app.domain.models.DeliveryState
import com.freelink.app.domain.models.LocalProfile
import com.freelink.app.domain.models.Message
import com.freelink.app.domain.models.TransportType
import com.freelink.app.domain.models.TravelKit
import com.freelink.app.domain.models.TrustStatus

@Entity(tableName = "local_profile")
data class ProfileEntity(
    @PrimaryKey val uuid: String,
    val flId: String,
    val displayName: String,
    val username: String,
    val publicKeyFingerprint: String,
    val isProvisional: Boolean,
    val createdAt: Long
) {
    fun toDomain() = LocalProfile(
        uuid = uuid,
        flId = flId,
        displayName = displayName,
        username = username,
        publicKeyFingerprint = publicKeyFingerprint,
        isProvisional = isProvisional,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(p: LocalProfile) = ProfileEntity(
            uuid = p.uuid,
            flId = p.flId,
            displayName = p.displayName,
            username = p.username,
            publicKeyFingerprint = p.publicKeyFingerprint,
            isProvisional = p.isProvisional,
            createdAt = p.createdAt
        )
    }
}

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val id: String,
    val flId: String,
    val displayName: String,
    val username: String,
    val publicKeyFingerprint: String,
    val trustStatus: String,
    val isBlocked: Boolean,
    val lastSeenTimestamp: Long,
    val notes: String
) {
    fun toDomain() = Contact(
        id = id,
        flId = flId,
        displayName = displayName,
        username = username,
        publicKeyFingerprint = publicKeyFingerprint,
        trustStatus = try { TrustStatus.valueOf(trustStatus) } catch (_: Exception) { TrustStatus.UNVERIFIED },
        isBlocked = isBlocked,
        lastSeenTimestamp = lastSeenTimestamp,
        notes = notes
    )

    companion object {
        fun fromDomain(c: Contact) = ContactEntity(
            id = c.id,
            flId = c.flId,
            displayName = c.displayName,
            username = c.username,
            publicKeyFingerprint = c.publicKeyFingerprint,
            trustStatus = c.trustStatus.name,
            isBlocked = c.isBlocked,
            lastSeenTimestamp = c.lastSeenTimestamp,
            notes = c.notes
        )
    }
}

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationContactId: String,
    val senderFlId: String,
    val recipientFlId: String,
    val textContent: String,
    val timestamp: Long,
    val expiryTimestamp: Long,
    val deliveryState: String,
    val transportType: String,
    val retryCount: Int,
    val errorMessage: String?
) {
    fun toDomain() = Message(
        id = id,
        conversationContactId = conversationContactId,
        senderFlId = senderFlId,
        recipientFlId = recipientFlId,
        textContent = textContent,
        timestamp = timestamp,
        expiryTimestamp = expiryTimestamp,
        deliveryState = try { DeliveryState.valueOf(deliveryState) } catch (_: Exception) { DeliveryState.FAILED },
        transportType = try { TransportType.valueOf(transportType) } catch (_: Exception) { TransportType.BLUETOOTH_LE },
        retryCount = retryCount,
        errorMessage = errorMessage
    )

    companion object {
        fun fromDomain(m: Message) = MessageEntity(
            id = m.id,
            conversationContactId = m.conversationContactId,
            senderFlId = m.senderFlId,
            recipientFlId = m.recipientFlId,
            textContent = m.textContent,
            timestamp = m.timestamp,
            expiryTimestamp = m.expiryTimestamp,
            deliveryState = m.deliveryState.name,
            transportType = m.transportType.name,
            retryCount = m.retryCount,
            errorMessage = m.errorMessage
        )
    }
}

@Entity(tableName = "travel_kit")
data class TravelKitEntity(
    @PrimaryKey val id: Int = 1,
    val primaryOperator: String,
    val operatorUssdHelp: String,
    val userRechargeNotes: String,
    val meetingPoint: String,
    val destinationAddress: String,
    val emergencyMedicalNotes: String,
    val bloodGroup: String,
    val emergencyContactName1: String,
    val emergencyContactPhone1: String,
    val emergencyContactName2: String,
    val emergencyContactPhone2: String,
    val offlineSosTemplate: String,
    val lastUpdated: Long
) {
    fun toDomain() = TravelKit(
        id = id,
        primaryOperator = primaryOperator,
        operatorUssdHelp = operatorUssdHelp,
        userRechargeNotes = userRechargeNotes,
        meetingPoint = meetingPoint,
        destinationAddress = destinationAddress,
        emergencyMedicalNotes = emergencyMedicalNotes,
        bloodGroup = bloodGroup,
        emergencyContactName1 = emergencyContactName1,
        emergencyContactPhone1 = emergencyContactPhone1,
        emergencyContactName2 = emergencyContactName2,
        emergencyContactPhone2 = emergencyContactPhone2,
        offlineSosTemplate = offlineSosTemplate,
        lastUpdated = lastUpdated
    )

    companion object {
        fun fromDomain(k: TravelKit) = TravelKitEntity(
            id = k.id,
            primaryOperator = k.primaryOperator,
            operatorUssdHelp = k.operatorUssdHelp,
            userRechargeNotes = k.userRechargeNotes,
            meetingPoint = k.meetingPoint,
            destinationAddress = k.destinationAddress,
            emergencyMedicalNotes = k.emergencyMedicalNotes,
            bloodGroup = k.bloodGroup,
            emergencyContactName1 = k.emergencyContactName1,
            emergencyContactPhone1 = k.emergencyContactPhone1,
            emergencyContactName2 = k.emergencyContactName2,
            emergencyContactPhone2 = k.emergencyContactPhone2,
            offlineSosTemplate = k.offlineSosTemplate,
            lastUpdated = k.lastUpdated
        )
    }
}
