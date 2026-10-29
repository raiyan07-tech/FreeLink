package com.freelink.app.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.freelink.app.data.local.entities.ContactEntity
import com.freelink.app.data.local.entities.MessageEntity
import com.freelink.app.data.local.entities.ProfileEntity
import com.freelink.app.data.local.entities.TravelKitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM local_profile LIMIT 1")
    fun getProfile(): Flow<ProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: ProfileEntity)

    @Query("DELETE FROM local_profile")
    suspend fun clearProfile()
}

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY displayName ASC")
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE flId = :flId LIMIT 1")
    fun getContactByFlId(flId: String): Flow<ContactEntity?>

    @Query("SELECT * FROM contacts WHERE id = :id LIMIT 1")
    fun getContactById(id: String): Flow<ContactEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(contact: ContactEntity)

    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun deleteContact(id: String)

    @Query("UPDATE contacts SET isBlocked = :blocked WHERE id = :id")
    suspend fun setBlocked(id: String, blocked: Boolean)

    @Query("UPDATE contacts SET trustStatus = :trustStatus WHERE id = :id")
    suspend fun updateTrustStatus(id: String, trustStatus: String)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationContactId = :contactId ORDER BY timestamp ASC")
    fun getMessagesForContact(contactId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE deliveryState = 'QUEUED_LOCALLY' ORDER BY timestamp ASC")
    fun getQueuedMessages(): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("UPDATE messages SET deliveryState = :state, errorMessage = :errorMessage WHERE id = :messageId")
    suspend fun updateDeliveryState(messageId: String, state: String, errorMessage: String?)

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: String)

    @Query("DELETE FROM messages WHERE conversationContactId = :contactId")
    suspend fun clearMessagesForContact(contactId: String)

    @Query("DELETE FROM messages WHERE expiryTimestamp < :currentTimeMillis")
    suspend fun purgeExpiredMessages(currentTimeMillis: Long)
}

@Dao
interface TravelKitDao {
    @Query("SELECT * FROM travel_kit WHERE id = 1 LIMIT 1")
    fun getTravelKit(): Flow<TravelKitEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTravelKit(travelKit: TravelKitEntity)

    @Query("DELETE FROM travel_kit")
    suspend fun clearTravelKit()
}
