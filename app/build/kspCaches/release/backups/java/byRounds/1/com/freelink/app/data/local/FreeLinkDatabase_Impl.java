package com.freelink.app.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.freelink.app.data.local.daos.ContactDao;
import com.freelink.app.data.local.daos.ContactDao_Impl;
import com.freelink.app.data.local.daos.MessageDao;
import com.freelink.app.data.local.daos.MessageDao_Impl;
import com.freelink.app.data.local.daos.ProfileDao;
import com.freelink.app.data.local.daos.ProfileDao_Impl;
import com.freelink.app.data.local.daos.TravelKitDao;
import com.freelink.app.data.local.daos.TravelKitDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FreeLinkDatabase_Impl extends FreeLinkDatabase {
  private volatile ProfileDao _profileDao;

  private volatile ContactDao _contactDao;

  private volatile MessageDao _messageDao;

  private volatile TravelKitDao _travelKitDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `local_profile` (`uuid` TEXT NOT NULL, `flId` TEXT NOT NULL, `displayName` TEXT NOT NULL, `username` TEXT NOT NULL, `publicKeyFingerprint` TEXT NOT NULL, `isProvisional` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`uuid`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `contacts` (`id` TEXT NOT NULL, `flId` TEXT NOT NULL, `displayName` TEXT NOT NULL, `username` TEXT NOT NULL, `publicKeyFingerprint` TEXT NOT NULL, `trustStatus` TEXT NOT NULL, `isBlocked` INTEGER NOT NULL, `lastSeenTimestamp` INTEGER NOT NULL, `notes` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `messages` (`id` TEXT NOT NULL, `conversationContactId` TEXT NOT NULL, `senderFlId` TEXT NOT NULL, `recipientFlId` TEXT NOT NULL, `textContent` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `expiryTimestamp` INTEGER NOT NULL, `deliveryState` TEXT NOT NULL, `transportType` TEXT NOT NULL, `retryCount` INTEGER NOT NULL, `errorMessage` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `travel_kit` (`id` INTEGER NOT NULL, `primaryOperator` TEXT NOT NULL, `operatorUssdHelp` TEXT NOT NULL, `userRechargeNotes` TEXT NOT NULL, `meetingPoint` TEXT NOT NULL, `destinationAddress` TEXT NOT NULL, `emergencyMedicalNotes` TEXT NOT NULL, `bloodGroup` TEXT NOT NULL, `emergencyContactName1` TEXT NOT NULL, `emergencyContactPhone1` TEXT NOT NULL, `emergencyContactName2` TEXT NOT NULL, `emergencyContactPhone2` TEXT NOT NULL, `offlineSosTemplate` TEXT NOT NULL, `lastUpdated` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'efe2d3ab7cbf3b15a776dc803b66223e')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `local_profile`");
        db.execSQL("DROP TABLE IF EXISTS `contacts`");
        db.execSQL("DROP TABLE IF EXISTS `messages`");
        db.execSQL("DROP TABLE IF EXISTS `travel_kit`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsLocalProfile = new HashMap<String, TableInfo.Column>(7);
        _columnsLocalProfile.put("uuid", new TableInfo.Column("uuid", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalProfile.put("flId", new TableInfo.Column("flId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalProfile.put("displayName", new TableInfo.Column("displayName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalProfile.put("username", new TableInfo.Column("username", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalProfile.put("publicKeyFingerprint", new TableInfo.Column("publicKeyFingerprint", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalProfile.put("isProvisional", new TableInfo.Column("isProvisional", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalProfile.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLocalProfile = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLocalProfile = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLocalProfile = new TableInfo("local_profile", _columnsLocalProfile, _foreignKeysLocalProfile, _indicesLocalProfile);
        final TableInfo _existingLocalProfile = TableInfo.read(db, "local_profile");
        if (!_infoLocalProfile.equals(_existingLocalProfile)) {
          return new RoomOpenHelper.ValidationResult(false, "local_profile(com.freelink.app.data.local.entities.ProfileEntity).\n"
                  + " Expected:\n" + _infoLocalProfile + "\n"
                  + " Found:\n" + _existingLocalProfile);
        }
        final HashMap<String, TableInfo.Column> _columnsContacts = new HashMap<String, TableInfo.Column>(9);
        _columnsContacts.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("flId", new TableInfo.Column("flId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("displayName", new TableInfo.Column("displayName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("username", new TableInfo.Column("username", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("publicKeyFingerprint", new TableInfo.Column("publicKeyFingerprint", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("trustStatus", new TableInfo.Column("trustStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("isBlocked", new TableInfo.Column("isBlocked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("lastSeenTimestamp", new TableInfo.Column("lastSeenTimestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysContacts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesContacts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoContacts = new TableInfo("contacts", _columnsContacts, _foreignKeysContacts, _indicesContacts);
        final TableInfo _existingContacts = TableInfo.read(db, "contacts");
        if (!_infoContacts.equals(_existingContacts)) {
          return new RoomOpenHelper.ValidationResult(false, "contacts(com.freelink.app.data.local.entities.ContactEntity).\n"
                  + " Expected:\n" + _infoContacts + "\n"
                  + " Found:\n" + _existingContacts);
        }
        final HashMap<String, TableInfo.Column> _columnsMessages = new HashMap<String, TableInfo.Column>(11);
        _columnsMessages.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("conversationContactId", new TableInfo.Column("conversationContactId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("senderFlId", new TableInfo.Column("senderFlId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("recipientFlId", new TableInfo.Column("recipientFlId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("textContent", new TableInfo.Column("textContent", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("expiryTimestamp", new TableInfo.Column("expiryTimestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("deliveryState", new TableInfo.Column("deliveryState", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("transportType", new TableInfo.Column("transportType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("retryCount", new TableInfo.Column("retryCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("errorMessage", new TableInfo.Column("errorMessage", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMessages = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMessages = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMessages = new TableInfo("messages", _columnsMessages, _foreignKeysMessages, _indicesMessages);
        final TableInfo _existingMessages = TableInfo.read(db, "messages");
        if (!_infoMessages.equals(_existingMessages)) {
          return new RoomOpenHelper.ValidationResult(false, "messages(com.freelink.app.data.local.entities.MessageEntity).\n"
                  + " Expected:\n" + _infoMessages + "\n"
                  + " Found:\n" + _existingMessages);
        }
        final HashMap<String, TableInfo.Column> _columnsTravelKit = new HashMap<String, TableInfo.Column>(14);
        _columnsTravelKit.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTravelKit.put("primaryOperator", new TableInfo.Column("primaryOperator", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTravelKit.put("operatorUssdHelp", new TableInfo.Column("operatorUssdHelp", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTravelKit.put("userRechargeNotes", new TableInfo.Column("userRechargeNotes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTravelKit.put("meetingPoint", new TableInfo.Column("meetingPoint", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTravelKit.put("destinationAddress", new TableInfo.Column("destinationAddress", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTravelKit.put("emergencyMedicalNotes", new TableInfo.Column("emergencyMedicalNotes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTravelKit.put("bloodGroup", new TableInfo.Column("bloodGroup", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTravelKit.put("emergencyContactName1", new TableInfo.Column("emergencyContactName1", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTravelKit.put("emergencyContactPhone1", new TableInfo.Column("emergencyContactPhone1", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTravelKit.put("emergencyContactName2", new TableInfo.Column("emergencyContactName2", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTravelKit.put("emergencyContactPhone2", new TableInfo.Column("emergencyContactPhone2", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTravelKit.put("offlineSosTemplate", new TableInfo.Column("offlineSosTemplate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTravelKit.put("lastUpdated", new TableInfo.Column("lastUpdated", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTravelKit = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTravelKit = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTravelKit = new TableInfo("travel_kit", _columnsTravelKit, _foreignKeysTravelKit, _indicesTravelKit);
        final TableInfo _existingTravelKit = TableInfo.read(db, "travel_kit");
        if (!_infoTravelKit.equals(_existingTravelKit)) {
          return new RoomOpenHelper.ValidationResult(false, "travel_kit(com.freelink.app.data.local.entities.TravelKitEntity).\n"
                  + " Expected:\n" + _infoTravelKit + "\n"
                  + " Found:\n" + _existingTravelKit);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "efe2d3ab7cbf3b15a776dc803b66223e", "ba98690e6a5804c9cbb90ed69cabbcad");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "local_profile","contacts","messages","travel_kit");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `local_profile`");
      _db.execSQL("DELETE FROM `contacts`");
      _db.execSQL("DELETE FROM `messages`");
      _db.execSQL("DELETE FROM `travel_kit`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(ProfileDao.class, ProfileDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ContactDao.class, ContactDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MessageDao.class, MessageDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TravelKitDao.class, TravelKitDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public ProfileDao profileDao() {
    if (_profileDao != null) {
      return _profileDao;
    } else {
      synchronized(this) {
        if(_profileDao == null) {
          _profileDao = new ProfileDao_Impl(this);
        }
        return _profileDao;
      }
    }
  }

  @Override
  public ContactDao contactDao() {
    if (_contactDao != null) {
      return _contactDao;
    } else {
      synchronized(this) {
        if(_contactDao == null) {
          _contactDao = new ContactDao_Impl(this);
        }
        return _contactDao;
      }
    }
  }

  @Override
  public MessageDao messageDao() {
    if (_messageDao != null) {
      return _messageDao;
    } else {
      synchronized(this) {
        if(_messageDao == null) {
          _messageDao = new MessageDao_Impl(this);
        }
        return _messageDao;
      }
    }
  }

  @Override
  public TravelKitDao travelKitDao() {
    if (_travelKitDao != null) {
      return _travelKitDao;
    } else {
      synchronized(this) {
        if(_travelKitDao == null) {
          _travelKitDao = new TravelKitDao_Impl(this);
        }
        return _travelKitDao;
      }
    }
  }
}
