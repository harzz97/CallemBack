package harzz97.github.io.f22prep;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {



    private static final int DB_VERSION = 1;
    private static  final  String DB_NAME ="LogDetails";
    private static final  String TABLE_NAME = "Logs";
    private static final String _id = "id";
    private static final String ContactName = "ContactName";
    private static final String ContactNumber = "ContactNumber";
    private static final String profilePath = "ProfilePath";
    private static final String callTime = "CallTime";
    private static final String duration = "Duration";
    private static final String snoozed = "Snoozed";
    private static final String enabled = "Enabled";

    public DatabaseHandler(Context c){
        super(c, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE "+ TABLE_NAME + " ( "+
                _id +" INTEGER PRIMARY KEY, "
                + ContactName + " TEXT, "
                + ContactNumber+" TEXT, "
                + profilePath +" TEXT, "
                + callTime+" TEXT, "
                + duration+" INTEGER, "
                + snoozed+" TEXT, "
                + enabled+" TEXT )";

        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);

        onCreate(db);

    }

    //add new Entry
    void addEntry(UserDetails details){
        SQLiteDatabase db = this.getWritableDatabase();

        //add contactName,contactNumber,profilePicture path,Current Time,reminder time
        ContentValues values = new ContentValues();

        values.put(ContactName,details.ContactName);
        values.put(ContactNumber,details.ContactNumber);
        values.put(callTime,details.callTime);
        values.put(profilePath,details.profilePath);
        values.put(duration,details.duration);
        values.put(enabled,"true");
        values.put(snoozed,"false");

        db.insert(TABLE_NAME,null,values);
        db.close();
    }

    //Get All the Entries
    List<UserDetails> getEntries(){

        List<UserDetails> userDetailsList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT id," +
                "ContactName," +
                "ContactNumber," +
                "ProfilePath," +
                "CallTime," +
                "Duration," +
                "Snoozed," +
                "Enabled FROM "+ TABLE_NAME+" ORDER BY id DESC";

        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();

        do{
            if(c.getCount()>0){
                UserDetails details = new UserDetails();
                details.set_id(c.getInt(0));
                details.setContactName(c.getString(1));
                details.setContactNumber(c.getString(2));
                details.setProfilePath(c.getString(3));
                details.setCallTime(c.getString(4));
                details.setDuration(c.getString(5));
                details.setSnoozed(c.getString(6));
                details.setEnabled(c.getColumnName(7));
                userDetailsList.add(details);
            }

        }while(c.moveToNext());

        c.close();
        db.close();
        return  userDetailsList;

    }

    //Delete a single Entry
    void deleteEntry(UserDetails d){

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME,_id+"= ?",new String[]{String.valueOf(d.get_id())});
        db.close();
    }

    //get the count
    int getCount(){
        int count;
        String query = "SELECT * FROM "+TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query,null);

        count = c.getCount();
        c.close();
        return count;
    }

    //update individual logs
    void updateEntry(UserDetails details){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactName,details.ContactName);
        values.put(ContactNumber,details.ContactNumber);
        values.put(callTime,details.callTime);
        values.put(duration,details.duration);
        values.put(enabled,"true");
        db.update(TABLE_NAME,values,_id+" =?",new String[]{String.valueOf(details._id)});
        db.close();
    }


    String[] getNotificationDetail(long schedule){

        String[] result=new String[]{};
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.query(TABLE_NAME,new String[]{_id,ContactName,ContactNumber,profilePath,callTime},
                duration +" =? ",new String[]{String.valueOf(schedule)},null,null,null,null);

        c.moveToFirst();

        do{
            if(c.getCount()>0){
             result = new String[]{c.getString(0),c.getString(1),c.getString(2),c.getString(3), c.getString(4)};
            }
        }while(c.moveToNext());
        c.close();

        //disableNotification(schedule);

        return  result;
    }

    void disableNotification(long schedule){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(enabled,"false");

        db.update(TABLE_NAME,values,duration+" =?",new String[]{String.valueOf(schedule)});
        db.close();
    }

    void snoozeNotification(long schedule, long updateTime){

        SQLiteDatabase helper = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(duration,updateTime);

        helper.update(TABLE_NAME,values,duration+ " =? ",new String[]{String.valueOf(schedule)});
        helper.close();
    }

    void deleteNotification(long schedule){

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME,duration+" =? ",new String[]{String.valueOf(schedule)});
        db.close();
    }
}
