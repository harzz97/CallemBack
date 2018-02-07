package harzz97.github.io.f22prep;

import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityDeletionOrUpdateAdapter;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.database.Cursor;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public class LogDao_Impl implements LogDetails.LogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfLogDetails;

  private final EntityDeletionOrUpdateAdapter __deletionAdapterOfLogDetails;

  public LogDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLogDetails = new EntityInsertionAdapter<LogDetails>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `LogDetails`(`_id`,`Name`,`Number`,`ProfilePath`,`isEnabled`,`InitialTime`,`Duration`,`isSnoozed`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, LogDetails value) {
        stmt.bindLong(1, value.get_id());
        if (value.getContactName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getContactName());
        }
        if (value.getContactNumber() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getContactNumber());
        }
        if (value.getProfilePath() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getProfilePath());
        }
        if (value.getEnabled() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getEnabled());
        }
        if (value.getTimeSet() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getTimeSet());
        }
        if (value.getDuration() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getDuration());
        }
        if (value.getSnoozed() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getSnoozed());
        }
      }
    };
    this.__deletionAdapterOfLogDetails = new EntityDeletionOrUpdateAdapter<LogDetails>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `LogDetails` WHERE `_id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, LogDetails value) {
        stmt.bindLong(1, value.get_id());
      }
    };
  }

  @Override
  public void insert(LogDetails... log) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfLogDetails.insert(log);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(LogDetails log) {
    __db.beginTransaction();
    try {
      __deletionAdapterOfLogDetails.handle(log);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<LogDetails> getAll() {
    final String _sql = "SELECT * FROM LogDetails";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("_id");
      final int _cursorIndexOfContactName = _cursor.getColumnIndexOrThrow("Name");
      final int _cursorIndexOfContactNumber = _cursor.getColumnIndexOrThrow("Number");
      final int _cursorIndexOfProfilePath = _cursor.getColumnIndexOrThrow("ProfilePath");
      final int _cursorIndexOfEnabled = _cursor.getColumnIndexOrThrow("isEnabled");
      final int _cursorIndexOfTimeSet = _cursor.getColumnIndexOrThrow("InitialTime");
      final int _cursorIndexOfDuration = _cursor.getColumnIndexOrThrow("Duration");
      final int _cursorIndexOfSnoozed = _cursor.getColumnIndexOrThrow("isSnoozed");
      final List<LogDetails> _result = new ArrayList<LogDetails>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final LogDetails _item;
        _item = new LogDetails();
        final int _tmp_id;
        _tmp_id = _cursor.getInt(_cursorIndexOfId);
        _item.set_id(_tmp_id);
        final String _tmpContactName;
        _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
        _item.setContactName(_tmpContactName);
        final String _tmpContactNumber;
        _tmpContactNumber = _cursor.getString(_cursorIndexOfContactNumber);
        _item.setContactNumber(_tmpContactNumber);
        final String _tmpProfilePath;
        _tmpProfilePath = _cursor.getString(_cursorIndexOfProfilePath);
        _item.setProfilePath(_tmpProfilePath);
        final String _tmpEnabled;
        _tmpEnabled = _cursor.getString(_cursorIndexOfEnabled);
        _item.setEnabled(_tmpEnabled);
        final String _tmpTimeSet;
        _tmpTimeSet = _cursor.getString(_cursorIndexOfTimeSet);
        _item.setTimeSet(_tmpTimeSet);
        final String _tmpDuration;
        _tmpDuration = _cursor.getString(_cursorIndexOfDuration);
        _item.setDuration(_tmpDuration);
        final String _tmpSnoozed;
        _tmpSnoozed = _cursor.getString(_cursorIndexOfSnoozed);
        _item.setSnoozed(_tmpSnoozed);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
