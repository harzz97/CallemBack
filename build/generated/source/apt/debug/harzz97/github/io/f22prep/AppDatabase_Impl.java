package harzz97.github.io.f22prep;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Callback;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Configuration;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomOpenHelper;
import android.arch.persistence.room.RoomOpenHelper.Delegate;
import android.arch.persistence.room.util.TableInfo;
import android.arch.persistence.room.util.TableInfo.Column;
import android.arch.persistence.room.util.TableInfo.ForeignKey;
import android.arch.persistence.room.util.TableInfo.Index;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.HashSet;

public class AppDatabase_Impl extends AppDatabase {
  private volatile LogDetails.LogDao _logDao;

  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `LogDetails` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `Name` TEXT, `Number` TEXT, `ProfilePath` TEXT, `isEnabled` TEXT, `InitialTime` TEXT, `Duration` TEXT, `isSnoozed` TEXT)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, \"befacfe5a7f3b5c287dd5c8857412bc6\")");
      }

      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `LogDetails`");
      }

      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      protected void validateMigration(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsLogDetails = new HashMap<String, TableInfo.Column>(8);
        _columnsLogDetails.put("_id", new TableInfo.Column("_id", "INTEGER", true, 1));
        _columnsLogDetails.put("Name", new TableInfo.Column("Name", "TEXT", false, 0));
        _columnsLogDetails.put("Number", new TableInfo.Column("Number", "TEXT", false, 0));
        _columnsLogDetails.put("ProfilePath", new TableInfo.Column("ProfilePath", "TEXT", false, 0));
        _columnsLogDetails.put("isEnabled", new TableInfo.Column("isEnabled", "TEXT", false, 0));
        _columnsLogDetails.put("InitialTime", new TableInfo.Column("InitialTime", "TEXT", false, 0));
        _columnsLogDetails.put("Duration", new TableInfo.Column("Duration", "TEXT", false, 0));
        _columnsLogDetails.put("isSnoozed", new TableInfo.Column("isSnoozed", "TEXT", false, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLogDetails = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLogDetails = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLogDetails = new TableInfo("LogDetails", _columnsLogDetails, _foreignKeysLogDetails, _indicesLogDetails);
        final TableInfo _existingLogDetails = TableInfo.read(_db, "LogDetails");
        if (! _infoLogDetails.equals(_existingLogDetails)) {
          throw new IllegalStateException("Migration didn't properly handle LogDetails(harzz97.github.io.f22prep.LogDetails).\n"
                  + " Expected:\n" + _infoLogDetails + "\n"
                  + " Found:\n" + _existingLogDetails);
        }
      }
    }, "befacfe5a7f3b5c287dd5c8857412bc6");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    return new InvalidationTracker(this, "LogDetails");
  }

  @Override
  public LogDetails.LogDao LogDao() {
    if (_logDao != null) {
      return _logDao;
    } else {
      synchronized(this) {
        if(_logDao == null) {
          _logDao = new LogDao_Impl(this);
        }
        return _logDao;
      }
    }
  }
}
