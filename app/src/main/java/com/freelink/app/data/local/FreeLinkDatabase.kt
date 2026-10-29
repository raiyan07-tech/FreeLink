package com.freelink.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.freelink.app.data.local.daos.ContactDao
import com.freelink.app.data.local.daos.MessageDao
import com.freelink.app.data.local.daos.ProfileDao
import com.freelink.app.data.local.daos.TravelKitDao
import com.freelink.app.data.local.entities.ContactEntity
import com.freelink.app.data.local.entities.MessageEntity
import com.freelink.app.data.local.entities.ProfileEntity
import com.freelink.app.data.local.entities.TravelKitEntity

@Database(
    entities = [
        ProfileEntity::class,
        ContactEntity::class,
        MessageEntity::class,
        TravelKitEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FreeLinkDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun contactDao(): ContactDao
    abstract fun messageDao(): MessageDao
    abstract fun travelKitDao(): TravelKitDao

    companion object {
        @Volatile
        private var INSTANCE: FreeLinkDatabase? = null

        fun getInstance(context: Context): FreeLinkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FreeLinkDatabase::class.java,
                    "freelink_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
