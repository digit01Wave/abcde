package com.example.jessica.myuci;

/**
 * Created by Jessica on 2/9/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.jessica.myuci.FeedReaderContract.EventEntry;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "dbeventshop";

    // SQL statement to create eventList table
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_EVENT_TABLE =
            "CREATE TABLE " + EventEntry.TABLE_NAME + " (" +
                    //EventEntry._ID + " INTEGER PRIMARY KEY," +
                    EventEntry.COLUMN_NAME_EVENT_ID + INT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_HOSTER + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_START_TIME + INT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_END_TIME + INT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_LAT + REAL_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_LON + REAL_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_LINK + TEXT_TYPE +
            " )";

    private static final String SQL_DELETE_EVENT_TABLE =
            "DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME;

    public MySQLiteHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create event table
        db.execSQL(SQL_CREATE_EVENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL(SQL_DELETE_EVENT_TABLE);

        // create fresh books table
        this.onCreate(db);
    }

    public void addEventItem(EventItem event){
        //for logging
        Log.d("addEventItem", event.toString());

        // reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        //create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(EventEntry.COLUMN_NAME_EVENT_ID, event.getID());
        values.put(EventEntry.COLUMN_NAME_TITLE, event.getTitle());
        values.put(EventEntry.COLUMN_NAME_HOSTER, event.getHoster());
        values.put(EventEntry.COLUMN_NAME_START_TIME, event.getStartTime().getTime());
        values.put(EventEntry.COLUMN_NAME_END_TIME, event.getEndTime().getTime());
        values.put(EventEntry.COLUMN_NAME_LAT, event.getLat());
        values.put(EventEntry.COLUMN_NAME_LON, event.getLon());
        values.put(EventEntry.COLUMN_NAME_LOCATION, event.getLocation());
        values.put(EventEntry.COLUMN_NAME_DESCRIPTION, event.getDescription());
        values.put(EventEntry.COLUMN_NAME_LINK, event.getLink());

        // insert
        db.insert(EventEntry.TABLE_NAME, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // close
        db.close();

    }

    public EventItem getEventItem(int id) throws java.text.ParseException{

        // get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "Select * FROM " + EventEntry.TABLE_NAME + " WHERE "
                + EventEntry.COLUMN_NAME_EVENT_ID + " = "+ id;

        // build query
        Cursor cursor = db.rawQuery(query, null);

        //build EventItem e
        EventItem e = new EventItem();
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            e.setID(Integer.parseInt(cursor.getString(1)));
            e.setTitle(cursor.getString(2));
            e.setHoster(cursor.getString(3));

            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
            e.setStartTime(new Date(cursor.getInt(4)));
            e.setEndTime(new Date(cursor.getInt(5)));
            e.setLatLon(Double.parseDouble(cursor.getString(6)),
                    Double.parseDouble(cursor.getString(7)));
            e.setLocation(cursor.getString(8));
            e.setDescription(cursor.getString(9));
            e.setLink(cursor.getString(10));

            cursor.close();

            //log
            Log.d("MSG:getEventItem(" + id + ")", e.toString());
        } else {
            e = null;
        }

        //close database and return the event
        db.close();
        return e;
    }

    public boolean deleteEventItem(int id){
        boolean result = false;

        //create query
        String query = "Select * FROM " + EventEntry.TABLE_NAME + " WHERE "
                + EventEntry.COLUMN_NAME_EVENT_ID + " = "+ id;

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(EventEntry.TABLE_NAME,
                EventEntry.COLUMN_NAME_EVENT_ID + " = ?", //selection
                new String[] { String.valueOf(id)}
        );
        db.close();

        //log
        Log.d("MSG:deleteEventItem id=", Integer.toString(id));

        return result;
    }


}