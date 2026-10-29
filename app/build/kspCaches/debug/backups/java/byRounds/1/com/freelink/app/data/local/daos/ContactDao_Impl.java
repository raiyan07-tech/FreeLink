package com.freelink.app.data.local.daos;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.freelink.app.data.local.entities.ContactEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ContactDao_Impl implements ContactDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ContactEntity> __insertionAdapterOfContactEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteContact;

  private final SharedSQLiteStatement __preparedStmtOfSetBlocked;

  private final SharedSQLiteStatement __preparedStmtOfUpdateTrustStatus;

  public ContactDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfContactEntity = new EntityInsertionAdapter<ContactEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `contacts` (`id`,`flId`,`displayName`,`username`,`publicKeyFingerprint`,`trustStatus`,`isBlocked`,`lastSeenTimestamp`,`notes`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ContactEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getFlId());
        statement.bindString(3, entity.getDisplayName());
        statement.bindString(4, entity.getUsername());
        statement.bindString(5, entity.getPublicKeyFingerprint());
        statement.bindString(6, entity.getTrustStatus());
        final int _tmp = entity.isBlocked() ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindLong(8, entity.getLastSeenTimestamp());
        statement.bindString(9, entity.getNotes());
      }
    };
    this.__preparedStmtOfDeleteContact = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM contacts WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSetBlocked = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE contacts SET isBlocked = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateTrustStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE contacts SET trustStatus = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrUpdate(final ContactEntity contact,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfContactEntity.insert(contact);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteContact(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteContact.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteContact.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setBlocked(final String id, final boolean blocked,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetBlocked.acquire();
        int _argIndex = 1;
        final int _tmp = blocked ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSetBlocked.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTrustStatus(final String id, final String trustStatus,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateTrustStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, trustStatus);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateTrustStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ContactEntity>> getAllContacts() {
    final String _sql = "SELECT * FROM contacts ORDER BY displayName ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"contacts"}, new Callable<List<ContactEntity>>() {
      @Override
      @NonNull
      public List<ContactEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFlId = CursorUtil.getColumnIndexOrThrow(_cursor, "flId");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfPublicKeyFingerprint = CursorUtil.getColumnIndexOrThrow(_cursor, "publicKeyFingerprint");
          final int _cursorIndexOfTrustStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "trustStatus");
          final int _cursorIndexOfIsBlocked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBlocked");
          final int _cursorIndexOfLastSeenTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeenTimestamp");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<ContactEntity> _result = new ArrayList<ContactEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContactEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpFlId;
            _tmpFlId = _cursor.getString(_cursorIndexOfFlId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final String _tmpPublicKeyFingerprint;
            _tmpPublicKeyFingerprint = _cursor.getString(_cursorIndexOfPublicKeyFingerprint);
            final String _tmpTrustStatus;
            _tmpTrustStatus = _cursor.getString(_cursorIndexOfTrustStatus);
            final boolean _tmpIsBlocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBlocked);
            _tmpIsBlocked = _tmp != 0;
            final long _tmpLastSeenTimestamp;
            _tmpLastSeenTimestamp = _cursor.getLong(_cursorIndexOfLastSeenTimestamp);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            _item = new ContactEntity(_tmpId,_tmpFlId,_tmpDisplayName,_tmpUsername,_tmpPublicKeyFingerprint,_tmpTrustStatus,_tmpIsBlocked,_tmpLastSeenTimestamp,_tmpNotes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<ContactEntity> getContactByFlId(final String flId) {
    final String _sql = "SELECT * FROM contacts WHERE flId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, flId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"contacts"}, new Callable<ContactEntity>() {
      @Override
      @Nullable
      public ContactEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFlId = CursorUtil.getColumnIndexOrThrow(_cursor, "flId");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfPublicKeyFingerprint = CursorUtil.getColumnIndexOrThrow(_cursor, "publicKeyFingerprint");
          final int _cursorIndexOfTrustStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "trustStatus");
          final int _cursorIndexOfIsBlocked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBlocked");
          final int _cursorIndexOfLastSeenTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeenTimestamp");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final ContactEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpFlId;
            _tmpFlId = _cursor.getString(_cursorIndexOfFlId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final String _tmpPublicKeyFingerprint;
            _tmpPublicKeyFingerprint = _cursor.getString(_cursorIndexOfPublicKeyFingerprint);
            final String _tmpTrustStatus;
            _tmpTrustStatus = _cursor.getString(_cursorIndexOfTrustStatus);
            final boolean _tmpIsBlocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBlocked);
            _tmpIsBlocked = _tmp != 0;
            final long _tmpLastSeenTimestamp;
            _tmpLastSeenTimestamp = _cursor.getLong(_cursorIndexOfLastSeenTimestamp);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            _result = new ContactEntity(_tmpId,_tmpFlId,_tmpDisplayName,_tmpUsername,_tmpPublicKeyFingerprint,_tmpTrustStatus,_tmpIsBlocked,_tmpLastSeenTimestamp,_tmpNotes);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<ContactEntity> getContactById(final String id) {
    final String _sql = "SELECT * FROM contacts WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"contacts"}, new Callable<ContactEntity>() {
      @Override
      @Nullable
      public ContactEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFlId = CursorUtil.getColumnIndexOrThrow(_cursor, "flId");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfPublicKeyFingerprint = CursorUtil.getColumnIndexOrThrow(_cursor, "publicKeyFingerprint");
          final int _cursorIndexOfTrustStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "trustStatus");
          final int _cursorIndexOfIsBlocked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBlocked");
          final int _cursorIndexOfLastSeenTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeenTimestamp");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final ContactEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpFlId;
            _tmpFlId = _cursor.getString(_cursorIndexOfFlId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final String _tmpPublicKeyFingerprint;
            _tmpPublicKeyFingerprint = _cursor.getString(_cursorIndexOfPublicKeyFingerprint);
            final String _tmpTrustStatus;
            _tmpTrustStatus = _cursor.getString(_cursorIndexOfTrustStatus);
            final boolean _tmpIsBlocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBlocked);
            _tmpIsBlocked = _tmp != 0;
            final long _tmpLastSeenTimestamp;
            _tmpLastSeenTimestamp = _cursor.getLong(_cursorIndexOfLastSeenTimestamp);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            _result = new ContactEntity(_tmpId,_tmpFlId,_tmpDisplayName,_tmpUsername,_tmpPublicKeyFingerprint,_tmpTrustStatus,_tmpIsBlocked,_tmpLastSeenTimestamp,_tmpNotes);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
