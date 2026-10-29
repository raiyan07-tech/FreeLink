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
import com.freelink.app.data.local.entities.TravelKitEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TravelKitDao_Impl implements TravelKitDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TravelKitEntity> __insertionAdapterOfTravelKitEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearTravelKit;

  public TravelKitDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTravelKitEntity = new EntityInsertionAdapter<TravelKitEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `travel_kit` (`id`,`primaryOperator`,`operatorUssdHelp`,`userRechargeNotes`,`meetingPoint`,`destinationAddress`,`emergencyMedicalNotes`,`bloodGroup`,`emergencyContactName1`,`emergencyContactPhone1`,`emergencyContactName2`,`emergencyContactPhone2`,`offlineSosTemplate`,`lastUpdated`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TravelKitEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPrimaryOperator());
        statement.bindString(3, entity.getOperatorUssdHelp());
        statement.bindString(4, entity.getUserRechargeNotes());
        statement.bindString(5, entity.getMeetingPoint());
        statement.bindString(6, entity.getDestinationAddress());
        statement.bindString(7, entity.getEmergencyMedicalNotes());
        statement.bindString(8, entity.getBloodGroup());
        statement.bindString(9, entity.getEmergencyContactName1());
        statement.bindString(10, entity.getEmergencyContactPhone1());
        statement.bindString(11, entity.getEmergencyContactName2());
        statement.bindString(12, entity.getEmergencyContactPhone2());
        statement.bindString(13, entity.getOfflineSosTemplate());
        statement.bindLong(14, entity.getLastUpdated());
      }
    };
    this.__preparedStmtOfClearTravelKit = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM travel_kit";
        return _query;
      }
    };
  }

  @Override
  public Object saveTravelKit(final TravelKitEntity travelKit,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTravelKitEntity.insert(travelKit);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearTravelKit(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearTravelKit.acquire();
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
          __preparedStmtOfClearTravelKit.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<TravelKitEntity> getTravelKit() {
    final String _sql = "SELECT * FROM travel_kit WHERE id = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"travel_kit"}, new Callable<TravelKitEntity>() {
      @Override
      @Nullable
      public TravelKitEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPrimaryOperator = CursorUtil.getColumnIndexOrThrow(_cursor, "primaryOperator");
          final int _cursorIndexOfOperatorUssdHelp = CursorUtil.getColumnIndexOrThrow(_cursor, "operatorUssdHelp");
          final int _cursorIndexOfUserRechargeNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "userRechargeNotes");
          final int _cursorIndexOfMeetingPoint = CursorUtil.getColumnIndexOrThrow(_cursor, "meetingPoint");
          final int _cursorIndexOfDestinationAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "destinationAddress");
          final int _cursorIndexOfEmergencyMedicalNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyMedicalNotes");
          final int _cursorIndexOfBloodGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "bloodGroup");
          final int _cursorIndexOfEmergencyContactName1 = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyContactName1");
          final int _cursorIndexOfEmergencyContactPhone1 = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyContactPhone1");
          final int _cursorIndexOfEmergencyContactName2 = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyContactName2");
          final int _cursorIndexOfEmergencyContactPhone2 = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyContactPhone2");
          final int _cursorIndexOfOfflineSosTemplate = CursorUtil.getColumnIndexOrThrow(_cursor, "offlineSosTemplate");
          final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
          final TravelKitEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpPrimaryOperator;
            _tmpPrimaryOperator = _cursor.getString(_cursorIndexOfPrimaryOperator);
            final String _tmpOperatorUssdHelp;
            _tmpOperatorUssdHelp = _cursor.getString(_cursorIndexOfOperatorUssdHelp);
            final String _tmpUserRechargeNotes;
            _tmpUserRechargeNotes = _cursor.getString(_cursorIndexOfUserRechargeNotes);
            final String _tmpMeetingPoint;
            _tmpMeetingPoint = _cursor.getString(_cursorIndexOfMeetingPoint);
            final String _tmpDestinationAddress;
            _tmpDestinationAddress = _cursor.getString(_cursorIndexOfDestinationAddress);
            final String _tmpEmergencyMedicalNotes;
            _tmpEmergencyMedicalNotes = _cursor.getString(_cursorIndexOfEmergencyMedicalNotes);
            final String _tmpBloodGroup;
            _tmpBloodGroup = _cursor.getString(_cursorIndexOfBloodGroup);
            final String _tmpEmergencyContactName1;
            _tmpEmergencyContactName1 = _cursor.getString(_cursorIndexOfEmergencyContactName1);
            final String _tmpEmergencyContactPhone1;
            _tmpEmergencyContactPhone1 = _cursor.getString(_cursorIndexOfEmergencyContactPhone1);
            final String _tmpEmergencyContactName2;
            _tmpEmergencyContactName2 = _cursor.getString(_cursorIndexOfEmergencyContactName2);
            final String _tmpEmergencyContactPhone2;
            _tmpEmergencyContactPhone2 = _cursor.getString(_cursorIndexOfEmergencyContactPhone2);
            final String _tmpOfflineSosTemplate;
            _tmpOfflineSosTemplate = _cursor.getString(_cursorIndexOfOfflineSosTemplate);
            final long _tmpLastUpdated;
            _tmpLastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
            _result = new TravelKitEntity(_tmpId,_tmpPrimaryOperator,_tmpOperatorUssdHelp,_tmpUserRechargeNotes,_tmpMeetingPoint,_tmpDestinationAddress,_tmpEmergencyMedicalNotes,_tmpBloodGroup,_tmpEmergencyContactName1,_tmpEmergencyContactPhone1,_tmpEmergencyContactName2,_tmpEmergencyContactPhone2,_tmpOfflineSosTemplate,_tmpLastUpdated);
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
