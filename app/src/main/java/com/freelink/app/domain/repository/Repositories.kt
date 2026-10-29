package com.freelink.app.domain.repository

import com.freelink.app.domain.models.Contact
import com.freelink.app.domain.models.LocalProfile
import com.freelink.app.domain.models.Message
import com.freelink.app.domain.models.TravelKit
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfile(): Flow<LocalProfile?>
    suspend fun saveProfile(profile: LocalProfile)
    suspend fun clearProfile()
}

interface ContactsRepository {
    fun getAllContacts(): Flow<List<Contact>>
    fun getContactByFlId(flId: String): Flow<Contact?>
    fun getContactById(id: String): Flow<Contact?>
    suspend fun insertOrUpdate(contact: Contact)
    suspend fun deleteContact(contactId: String)
    suspend fun setBlocked(contactId: String, blocked: Boolean)
    suspend fun updateTrustStatus(contactId: String, trustStatus: com.freelink.app.domain.models.TrustStatus)
}

interface MessageRepository {
    fun getMessagesForContact(contactId: String): Flow<List<Message>>
    fun getAllMessages(): Flow<List<Message>>
    fun getQueuedMessages(): Flow<List<Message>>
    suspend fun insertMessage(message: Message)
    suspend fun updateDeliveryState(messageId: String, state: com.freelink.app.domain.models.DeliveryState, errorMessage: String? = null)
    suspend fun deleteMessage(messageId: String)
    suspend fun clearMessagesForContact(contactId: String)
    suspend fun purgeExpiredMessages(currentTimeMillis: Long)
}

interface TravelKitRepository {
    fun getTravelKit(): Flow<TravelKit?>
    suspend fun saveTravelKit(travelKit: TravelKit)
    suspend fun clearTravelKit()
}
