package com.freelink.app.data.local.repositories

import com.freelink.app.data.local.daos.ContactDao
import com.freelink.app.data.local.daos.MessageDao
import com.freelink.app.data.local.daos.ProfileDao
import com.freelink.app.data.local.daos.TravelKitDao
import com.freelink.app.data.local.entities.ContactEntity
import com.freelink.app.data.local.entities.MessageEntity
import com.freelink.app.data.local.entities.ProfileEntity
import com.freelink.app.data.local.entities.TravelKitEntity
import com.freelink.app.domain.models.Contact
import com.freelink.app.domain.models.DeliveryState
import com.freelink.app.domain.models.LocalProfile
import com.freelink.app.domain.models.Message
import com.freelink.app.domain.models.TravelKit
import com.freelink.app.domain.models.TrustStatus
import com.freelink.app.domain.repository.ContactsRepository
import com.freelink.app.domain.repository.MessageRepository
import com.freelink.app.domain.repository.ProfileRepository
import com.freelink.app.domain.repository.TravelKitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomProfileRepository(private val dao: ProfileDao) : ProfileRepository {
    override fun getProfile(): Flow<LocalProfile?> =
        dao.getProfile().map { it?.toDomain() }

    override suspend fun saveProfile(profile: LocalProfile) {
        dao.saveProfile(ProfileEntity.fromDomain(profile))
    }

    override suspend fun clearProfile() {
        dao.clearProfile()
    }
}

class RoomContactsRepository(private val dao: ContactDao) : ContactsRepository {
    override fun getAllContacts(): Flow<List<Contact>> =
        dao.getAllContacts().map { list -> list.map { it.toDomain() } }

    override fun getContactByFlId(flId: String): Flow<Contact?> =
        dao.getContactByFlId(flId).map { it?.toDomain() }

    override fun getContactById(id: String): Flow<Contact?> =
        dao.getContactById(id).map { it?.toDomain() }

    override suspend fun insertOrUpdate(contact: Contact) {
        dao.insertOrUpdate(ContactEntity.fromDomain(contact))
    }

    override suspend fun deleteContact(contactId: String) {
        dao.deleteContact(contactId)
    }

    override suspend fun setBlocked(contactId: String, blocked: Boolean) {
        dao.setBlocked(contactId, blocked)
    }

    override suspend fun updateTrustStatus(contactId: String, trustStatus: TrustStatus) {
        dao.updateTrustStatus(contactId, trustStatus.name)
    }
}

class RoomMessageRepository(private val dao: MessageDao) : MessageRepository {
    override fun getMessagesForContact(contactId: String): Flow<List<Message>> =
        dao.getMessagesForContact(contactId).map { list -> list.map { it.toDomain() } }

    override fun getMessagesForPeer(peerFlId: String, localFlId: String): Flow<List<Message>> =
        dao.getMessagesForPeer(peerFlId, localFlId).map { list -> list.map { it.toDomain() } }

    override fun getAllMessages(): Flow<List<Message>> =
        dao.getAllMessages().map { list -> list.map { it.toDomain() } }

    override fun getQueuedMessages(): Flow<List<Message>> =
        dao.getQueuedMessages().map { list -> list.map { it.toDomain() } }

    override suspend fun insertMessage(message: Message) {
        dao.insertMessage(MessageEntity.fromDomain(message))
    }

    override suspend fun updateDeliveryState(
        messageId: String,
        state: DeliveryState,
        errorMessage: String?
    ) {
        dao.updateDeliveryState(messageId, state.name, errorMessage)
    }

    override suspend fun deleteMessage(messageId: String) {
        dao.deleteMessage(messageId)
    }

    override suspend fun clearMessagesForContact(contactId: String) {
        dao.clearMessagesForContact(contactId)
    }

    override suspend fun clearMessagesForPeer(peerFlId: String, localFlId: String) {
        dao.clearMessagesForPeer(peerFlId, localFlId)
    }

    override suspend fun purgeExpiredMessages(currentTimeMillis: Long) {
        dao.purgeExpiredMessages(currentTimeMillis)
    }
}

class RoomTravelKitRepository(private val dao: TravelKitDao) : TravelKitRepository {
    override fun getTravelKit(): Flow<TravelKit?> =
        dao.getTravelKit().map { it?.toDomain() }

    override suspend fun saveTravelKit(travelKit: TravelKit) {
        dao.saveTravelKit(TravelKitEntity.fromDomain(travelKit))
    }

    override suspend fun clearTravelKit() {
        dao.clearTravelKit()
    }
}
