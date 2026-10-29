package com.freelink.app.data.local.daos;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.freelink.app.data.local.entities.MessageEntity;
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
public final class MessageDao_Impl implements MessageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MessageEntity> __insertionAdapterOfMessageEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDeliveryState;

  private final SharedSQLiteStatement __preparedStmtOfDeleteMessage;

  private final SharedSQLiteStatement __preparedStmtOfClearMessagesForContact;

  private final SharedSQLiteStatement __preparedStmtOfPurgeExpiredMessages;

  public MessageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMessageEntity = new EntityInsertionAdapter<MessageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `messages` (`id`,`conversationContactId`,`senderFlId`,`recipientFlId`,`textContent`,`timestamp`,`expiryTimestamp`,`deliveryState`,`transportType`,`retryCount`,`errorMessage`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MessageEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getConversationContactId());
        statement.bindString(3, entity.getSenderFlId());
        statement.bindString(4, entity.getRecipientFlId());
        statement.bindString(5, entity.getTextContent());
        statement.bindLong(6, entity.getTimestamp());
        statement.bindLong(7, entity.getExpiryTimestamp());
        statement.bindString(8, entity.getDeliveryState());
        statement.bindString(9, entity.getTransportType());
        statement.bindLong(10, entity.getRetryCount());
        if (entity.getErrorMessage() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getErrorMessage());
        }
      }
    };
    this.__preparedStmtOfUpdateDeliveryState = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE messages SET deliveryState = ?, errorMessage = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteMessage = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM messages WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearMessagesForContact = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM messages WHERE conversationContactId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfPurgeExpiredMessages = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM messages WHERE expiryTimestamp < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertMessage(final MessageEntity message,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMessageEntity.insert(message);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDeliveryState(final String messageId, final String state,
      final String errorMessage, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDeliveryState.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, state);
        _argIndex = 2;
        if (errorMessage == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, errorMessage);
        }
        _argIndex = 3;
        _stmt.bindString(_argIndex, messageId);
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
          __preparedStmtOfUpdateDeliveryState.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteMessage(final String messageId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteMessage.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, messageId);
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
          __preparedStmtOfDeleteMessage.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearMessagesForContact(final String contactId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearMessagesForContact.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, contactId);
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
          __preparedStmtOfClearMessagesForContact.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object purgeExpiredMessages(final long currentTimeMillis,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfPurgeExpiredMessages.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, currentTimeMillis);
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
          __preparedStmtOfPurgeExpiredMessages.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MessageEntity>> getMessagesForContact(final String contactId) {
    final String _sql = "SELECT * FROM messages WHERE conversationContactId = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, contactId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"messages"}, new Callable<List<MessageEntity>>() {
      @Override
      @NonNull
      public List<MessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfConversationContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "conversationContactId");
          final int _cursorIndexOfSenderFlId = CursorUtil.getColumnIndexOrThrow(_cursor, "senderFlId");
          final int _cursorIndexOfRecipientFlId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientFlId");
          final int _cursorIndexOfTextContent = CursorUtil.getColumnIndexOrThrow(_cursor, "textContent");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfExpiryTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryTimestamp");
          final int _cursorIndexOfDeliveryState = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryState");
          final int _cursorIndexOfTransportType = CursorUtil.getColumnIndexOrThrow(_cursor, "transportType");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final List<MessageEntity> _result = new ArrayList<MessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MessageEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpConversationContactId;
            _tmpConversationContactId = _cursor.getString(_cursorIndexOfConversationContactId);
            final String _tmpSenderFlId;
            _tmpSenderFlId = _cursor.getString(_cursorIndexOfSenderFlId);
            final String _tmpRecipientFlId;
            _tmpRecipientFlId = _cursor.getString(_cursorIndexOfRecipientFlId);
            final String _tmpTextContent;
            _tmpTextContent = _cursor.getString(_cursorIndexOfTextContent);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final long _tmpExpiryTimestamp;
            _tmpExpiryTimestamp = _cursor.getLong(_cursorIndexOfExpiryTimestamp);
            final String _tmpDeliveryState;
            _tmpDeliveryState = _cursor.getString(_cursorIndexOfDeliveryState);
            final String _tmpTransportType;
            _tmpTransportType = _cursor.getString(_cursorIndexOfTransportType);
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            _item = new MessageEntity(_tmpId,_tmpConversationContactId,_tmpSenderFlId,_tmpRecipientFlId,_tmpTextContent,_tmpTimestamp,_tmpExpiryTimestamp,_tmpDeliveryState,_tmpTransportType,_tmpRetryCount,_tmpErrorMessage);
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
  public Flow<List<MessageEntity>> getAllMessages() {
    final String _sql = "SELECT * FROM messages ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"messages"}, new Callable<List<MessageEntity>>() {
      @Override
      @NonNull
      public List<MessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfConversationContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "conversationContactId");
          final int _cursorIndexOfSenderFlId = CursorUtil.getColumnIndexOrThrow(_cursor, "senderFlId");
          final int _cursorIndexOfRecipientFlId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientFlId");
          final int _cursorIndexOfTextContent = CursorUtil.getColumnIndexOrThrow(_cursor, "textContent");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfExpiryTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryTimestamp");
          final int _cursorIndexOfDeliveryState = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryState");
          final int _cursorIndexOfTransportType = CursorUtil.getColumnIndexOrThrow(_cursor, "transportType");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final List<MessageEntity> _result = new ArrayList<MessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MessageEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpConversationContactId;
            _tmpConversationContactId = _cursor.getString(_cursorIndexOfConversationContactId);
            final String _tmpSenderFlId;
            _tmpSenderFlId = _cursor.getString(_cursorIndexOfSenderFlId);
            final String _tmpRecipientFlId;
            _tmpRecipientFlId = _cursor.getString(_cursorIndexOfRecipientFlId);
            final String _tmpTextContent;
            _tmpTextContent = _cursor.getString(_cursorIndexOfTextContent);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final long _tmpExpiryTimestamp;
            _tmpExpiryTimestamp = _cursor.getLong(_cursorIndexOfExpiryTimestamp);
            final String _tmpDeliveryState;
            _tmpDeliveryState = _cursor.getString(_cursorIndexOfDeliveryState);
            final String _tmpTransportType;
            _tmpTransportType = _cursor.getString(_cursorIndexOfTransportType);
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            _item = new MessageEntity(_tmpId,_tmpConversationContactId,_tmpSenderFlId,_tmpRecipientFlId,_tmpTextContent,_tmpTimestamp,_tmpExpiryTimestamp,_tmpDeliveryState,_tmpTransportType,_tmpRetryCount,_tmpErrorMessage);
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
  public Flow<List<MessageEntity>> getQueuedMessages() {
    final String _sql = "SELECT * FROM messages WHERE deliveryState = 'QUEUED_LOCALLY' ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"messages"}, new Callable<List<MessageEntity>>() {
      @Override
      @NonNull
      public List<MessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfConversationContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "conversationContactId");
          final int _cursorIndexOfSenderFlId = CursorUtil.getColumnIndexOrThrow(_cursor, "senderFlId");
          final int _cursorIndexOfRecipientFlId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientFlId");
          final int _cursorIndexOfTextContent = CursorUtil.getColumnIndexOrThrow(_cursor, "textContent");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfExpiryTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryTimestamp");
          final int _cursorIndexOfDeliveryState = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryState");
          final int _cursorIndexOfTransportType = CursorUtil.getColumnIndexOrThrow(_cursor, "transportType");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final int _cursorIndexOfErrorMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "errorMessage");
          final List<MessageEntity> _result = new ArrayList<MessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MessageEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpConversationContactId;
            _tmpConversationContactId = _cursor.getString(_cursorIndexOfConversationContactId);
            final String _tmpSenderFlId;
            _tmpSenderFlId = _cursor.getString(_cursorIndexOfSenderFlId);
            final String _tmpRecipientFlId;
            _tmpRecipientFlId = _cursor.getString(_cursorIndexOfRecipientFlId);
            final String _tmpTextContent;
            _tmpTextContent = _cursor.getString(_cursorIndexOfTextContent);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final long _tmpExpiryTimestamp;
            _tmpExpiryTimestamp = _cursor.getLong(_cursorIndexOfExpiryTimestamp);
            final String _tmpDeliveryState;
            _tmpDeliveryState = _cursor.getString(_cursorIndexOfDeliveryState);
            final String _tmpTransportType;
            _tmpTransportType = _cursor.getString(_cursorIndexOfTransportType);
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            final String _tmpErrorMessage;
            if (_cursor.isNull(_cursorIndexOfErrorMessage)) {
              _tmpErrorMessage = null;
            } else {
              _tmpErrorMessage = _cursor.getString(_cursorIndexOfErrorMessage);
            }
            _item = new MessageEntity(_tmpId,_tmpConversationContactId,_tmpSenderFlId,_tmpRecipientFlId,_tmpTextContent,_tmpTimestamp,_tmpExpiryTimestamp,_tmpDeliveryState,_tmpTransportType,_tmpRetryCount,_tmpErrorMessage);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
