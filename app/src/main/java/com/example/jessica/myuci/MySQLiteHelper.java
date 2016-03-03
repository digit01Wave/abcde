package com.example.jessica.myuci;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.jessica.myuci.FeedReaderContract.EventEntry;
import com.example.jessica.myuci.FeedReaderContract.WLEntry;
import com.example.jessica.myuci.FeedReaderContract.ServerEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
                    EventEntry.COLUMN_NAME_EVENT_ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    EventEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_HOSTER + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_START_TIME + INT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_END_TIME + INT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_LAT + REAL_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_LON + REAL_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_LINK + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_IMAGE_LINK + TEXT_TYPE +
            " )";
    private static final String SQL_CREATE_WATCH_LATER_TABLE =
            "CREATE TABLE " + WLEntry.TABLE_NAME + " (" +
                    WLEntry.COLUMN_NAME_USER_ID + TEXT_TYPE + COMMA_SEP +
                    WLEntry.COLUMN_NAME_EVENT_ID + INT_TYPE + COMMA_SEP +
                    WLEntry.COLUMN_NAME_UPDATE_STATUS + TEXT_TYPE + COMMA_SEP +
                    "PRIMARY KEY (" + WLEntry.COLUMN_NAME_USER_ID + ", " + WLEntry.COLUMN_NAME_EVENT_ID + ")" +
                    ")";

    private static final String SQLITE_SELECT_UNSYNCED_WL = "SELECT  * FROM " + WLEntry.TABLE_NAME +
            " where "+WLEntry.COLUMN_NAME_UPDATE_STATUS +" = '"+ServerEntry.UPDATE_STATUS_UNSYNCED+"'";

    private static final String SQL_CREATE_WATCH_LATER_DELETE_TABLE =
            "CREATE TABLE " + WLEntry.TO_DELETE_TABLE_NAME + " (" +
                    WLEntry.COLUMN_NAME_USER_ID + TEXT_TYPE + COMMA_SEP +
                    WLEntry.COLUMN_NAME_EVENT_ID + INT_TYPE + COMMA_SEP +
                    "PRIMARY KEY (" + WLEntry.COLUMN_NAME_USER_ID + ", " + WLEntry.COLUMN_NAME_EVENT_ID + ")" +
                    ")";


    private static final String SQL_DELETE_EVENT_TABLE =
            "DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME;
    private static final String SQL_DELETE_WATCH_LATER_TABLE =
            "DROP TABLE IF EXISTS " + WLEntry.TABLE_NAME;
    private static final String SQL_DELETE_WATCH_LATER_DELETE_TABLE =
            "DROP TABLE IF EXISTS "+ WLEntry.TO_DELETE_TABLE_NAME;


    public MySQLiteHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create event table
        db.execSQL(SQL_CREATE_EVENT_TABLE);
        db.execSQL(SQL_CREATE_WATCH_LATER_TABLE);
        db.execSQL(SQL_CREATE_WATCH_LATER_DELETE_TABLE);
        Log.d("MSG: ", "Created Event and Watch Later Tables");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL(SQL_DELETE_EVENT_TABLE);
        db.execSQL(SQL_DELETE_WATCH_LATER_TABLE);
        db.execSQL(SQL_CREATE_WATCH_LATER_DELETE_TABLE);
        Log.d("MSG: ", "Upgraded Event and Watch Later Tables");

        // create fresh tables
        this.onCreate(db);
    }

    //for testing purposes
    public void addEventItem(EventItem event){
        Log.d("MSG:", "addEventItem(event)"+event.toString());
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
        values.put(EventEntry.COLUMN_NAME_IMAGE_LINK, event.getImageLink());

        try {
            // insert
            db.insert(EventEntry.TABLE_NAME, // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values
        }catch(Exception e){
            //for logging
            Log.d("---", "FAILED to addEventItem(event)"+event.toString() + e.toString());
        }
        // close
        db.close();

    }

    public void addEventItem(String[] event_cols, boolean milisecond_format) throws java.text.ParseException{
        /*given array of strings of all event properties, will create event item
        * in the form [id, title, hoster, start_time, end_time, lat, lon, location, description, link]
        *
        * milesecond_format means that the start and end_time is in miliseconds (can be converted to
        * int. If set to false instead, then it means it is in the sql format 'YYYY-MM-DD HH:MM:SS'
        * */
        Log.d("MSG: ", "AddEventItemStr");
        // reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        //create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(EventEntry.COLUMN_NAME_EVENT_ID, event_cols[0]);
        values.put(EventEntry.COLUMN_NAME_TITLE, event_cols[1]);
        if(!(event_cols[2].equals("null"))) {
            values.put(EventEntry.COLUMN_NAME_HOSTER, event_cols[2]);
        }
        if(milisecond_format){ //is in milisecond format
            values.put(EventEntry.COLUMN_NAME_START_TIME, Integer.parseInt(event_cols[3]));
            if(!(event_cols[4].equals("null"))) {
                values.put(EventEntry.COLUMN_NAME_END_TIME, Integer.parseInt(event_cols[4]));
            }
        }
        else{
            DateFormat date_format = new SimpleDateFormat(EventEntry.DATE_FORMAT);
            Date start_time = date_format.parse(event_cols[3]);
            values.put(EventEntry.COLUMN_NAME_START_TIME, start_time.getTime());
            if(!(event_cols[4].equals("null"))) {
                Date end_time = date_format.parse(event_cols[4]);
                values.put(EventEntry.COLUMN_NAME_END_TIME, end_time.getTime());
            }

        }

        if(!(event_cols[5].equals("null"))) {
            values.put(EventEntry.COLUMN_NAME_LAT, event_cols[5]);
        }
        if(!(event_cols[6].equals("null"))) {
            values.put(EventEntry.COLUMN_NAME_LON, event_cols[6]);
        }
        values.put(EventEntry.COLUMN_NAME_LOCATION, event_cols[7]);
        if(!(event_cols[8].equals("null"))) {
            values.put(EventEntry.COLUMN_NAME_DESCRIPTION, event_cols[8]);
        }
        if(!(event_cols[9].equals("null"))) {
            values.put(EventEntry.COLUMN_NAME_LINK, event_cols[9]);
        }
        if(!(event_cols[10].equals("null"))) {
            values.put(EventEntry.COLUMN_NAME_IMAGE_LINK, event_cols[10]);
        }

        try {
            Log.d("MSG: ", "STRING START");
            // insert
            db.insert(EventEntry.TABLE_NAME, // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values
        }catch(Exception e){
            //for logging
            Log.d("---", "FAILED to addEventItemStr" + e.toString());
        }
        // close
        db.close();
    }


    public String[][] getAllEventStrings() {
        String selectQuery = "SELECT  * FROM " + EventEntry.TABLE_NAME;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int num_rows = cursor.getCount();
        String[][] event_list = new String[num_rows][];
        Log.d("MSG:", "getAllEventStringsSTART()");
        if (cursor.moveToFirst()) {
            int index = 0;
            do {
                String[] event = {
                        cursor.getString(0), //event_id
                        cursor.getString(1), //title
                        cursor.getString(2), //hoster
                        cursor.getString(3), //start_time
                        cursor.getString(4), //end_time
                        cursor.getString(5), //lat
                        cursor.getString(6), //lon
                        cursor.getString(7), //location
                        cursor.getString(8), //description
                        cursor.getString(9), //link
                        cursor.getString(10) //image_link
                };
                event_list[index] = event;

                //since start_time and end_time are still in integer format, need to convert to datetime
                SimpleDateFormat ft = new SimpleDateFormat (EventEntry.DATE_FORMAT);
                event_list[index][3] = ft.format(Long.parseLong(event_list[index][3]));
                if(event_list[index][4] != null) {
                    event_list[index][4] = ft.format(Long.parseLong(event_list[index][4]));
                }
                index++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        Log.d("MSG:", "getAllEventStrings()");
        return event_list;
    }


    public void deleteAllEvents(SQLiteDatabase db){
        /*will delete all events in database*/
        db.delete(EventEntry.TABLE_NAME, null, null);
        Log.d("MSG: ", "All events have been deleted");
    }
        /*
    ################################################################################################

    BELOW ARE METHODS FOR WATCH LATER LIST

    ################################################################################################
    */
    public void addWatchLaterItem(String user_id, String event_id){
    /* adds user_id, event_id to watch_later_list table in SQLite
    *
    * */

        // reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        //create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(WLEntry.COLUMN_NAME_USER_ID, user_id);
        values.put(WLEntry.COLUMN_NAME_EVENT_ID, event_id);
        values.put(WLEntry.COLUMN_NAME_UPDATE_STATUS, ServerEntry.UPDATE_STATUS_UNSYNCED);

        // insert
        db.insert(WLEntry.TABLE_NAME, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // close
        db.close();
        Log.d("MSG: ", "AddWatchLaterEvent(" + user_id + ", " + event_id + ")");
    }

    public void deleteWatchLaterItem(String user_id, String event_id){
        /*deletes item from watch_later_list sqlLite table*/

        SQLiteDatabase db = this.getWritableDatabase();

        //add to WLtoDelete if entry synced in order to sync with database later
        String selectQuery = "SELECT * FROM " + WLEntry.TABLE_NAME + " WHERE " + WLEntry.COLUMN_NAME_USER_ID +
                " = '" + user_id +"' AND " + WLEntry.COLUMN_NAME_EVENT_ID + " = " + event_id;
        Cursor cursor = db.rawQuery(selectQuery, null);

        //if there are synced items to be deleted
        if(cursor.moveToFirst()){
            //create ContentValues to add key "column"/value
            ContentValues values = new ContentValues();
            values.put(WLEntry.COLUMN_NAME_USER_ID, user_id);
            values.put(WLEntry.COLUMN_NAME_EVENT_ID, event_id);

            // insert
            db.insert(WLEntry.TO_DELETE_TABLE_NAME, // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values
        }
        cursor.close();

        //delete from database
        db.delete(WLEntry.TABLE_NAME, WLEntry.COLUMN_NAME_USER_ID + " = '" + user_id + "' AND " +
                WLEntry.COLUMN_NAME_EVENT_ID + " = " + event_id, null);
        db.close();
    }

    /*
    * Returns whether or not the watch later list has that item
    * */
    public boolean hasWLItem(String user_id, String event_id){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + WLEntry.TABLE_NAME + " WHERE " + WLEntry.COLUMN_NAME_USER_ID +
                " = '" + user_id +"' AND " + WLEntry.COLUMN_NAME_EVENT_ID + " = " + event_id;
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d("MSG: ", "hasWLItem " + selectQuery);
        if(cursor.moveToFirst()){
            return true;
        }
        return false;

    }

    public String[][] getAllWatchLaterEvents(String user_id) {
        String selectQuery = "SELECT  * FROM " + EventEntry.TABLE_NAME + " INNER JOIN " + WLEntry.TABLE_NAME +
                " ON " + WLEntry.TABLE_NAME + "." + WLEntry.COLUMN_NAME_USER_ID + " = '" + user_id +
                "' AND " + EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_NAME_EVENT_ID + " = " +
                WLEntry.TABLE_NAME + "." + WLEntry.COLUMN_NAME_EVENT_ID;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int num_rows = cursor.getCount();
        String[][] event_list = new String[num_rows][];
        if (cursor.moveToFirst()) {
            int index = 0;
            do {
                String[] event = {
                        cursor.getString(0), //event_id
                        cursor.getString(1), //title
                        cursor.getString(2), //hoster
                        cursor.getString(3), //start_time
                        cursor.getString(4), //end_time
                        cursor.getString(5), //lat
                        cursor.getString(6), //lon
                        cursor.getString(7), //location
                        cursor.getString(8), //description
                        cursor.getString(9), //link
                        cursor.getString(10) //image_link
                };
                event_list[index] = event;

                //since start_time and end_time are still in integer format, need to convert to datetime
                SimpleDateFormat ft = new SimpleDateFormat (EventEntry.DATE_FORMAT);
                event_list[index][3] = ft.format(Long.parseLong(event_list[index][3]));
                event_list[index][4] = ft.format(Long.parseLong(event_list[index][4]));

                index++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        Log.d("MSG:", selectQuery);
        return event_list;
    }


    /**
     * Compose JSON out of SQLite records
     */
    public String composeJSONfromWatchLaterSQLite(){
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(SQLITE_SELECT_UNSYNCED_WL, null);

        //add all entries to be added
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(WLEntry.COLUMN_NAME_USER_ID, cursor.getString(0));
                map.put(WLEntry.COLUMN_NAME_EVENT_ID, cursor.getString(1));
                map.put(ServerEntry.UPDATE_ACTION_TITLE, ServerEntry.UPDATE_ACTION_ADD);
                wordList.add(map);
            } while (cursor.moveToNext());
        }

        //add all entries to be deleted
        cursor = database.rawQuery("SELECT * FROM " + WLEntry.TO_DELETE_TABLE_NAME, null);
        if(cursor.moveToFirst()){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(WLEntry.COLUMN_NAME_USER_ID, cursor.getString(0));
            map.put(WLEntry.COLUMN_NAME_EVENT_ID, cursor.getString(1));
            map.put(ServerEntry.UPDATE_ACTION_TITLE, ServerEntry.UPDATE_ACTION_DELETE);
            wordList.add(map);
        }

        cursor.close();
        database.close();

        //Use GSON to serialize Array List to JSON
        Gson gson = new GsonBuilder().create();
        return gson.toJson(wordList);
    }


    /**
     * Get SQLite records that are yet to be Synced
     */
    public int dbWatchLaterSyncCount(){
        //count all the newly added items
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(SQLITE_SELECT_UNSYNCED_WL, null);
        int count = cursor.getCount();

        //adds newly deleted items
        cursor = db.rawQuery("SELECT * FROM " + WLEntry.TO_DELETE_TABLE_NAME, null);
        count += cursor.getCount();

        //clean and return
        cursor.close();
        db.close();
        return count;
    }

    /**
     * Update Sync status against each user and event id
     * action_completed is either delete or add
     * sync status is the status the server sponded with
     */
    public void updateWatchLaterSyncStatus(String user_id, String event_id, String action_completed, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        if(action_completed.equals(ServerEntry.UPDATE_ACTION_ADD)){ //added
            String updateQuery = "Update " + WLEntry.TABLE_NAME + " set "+WLEntry.COLUMN_NAME_UPDATE_STATUS+" = '"+ status +
                    "' where " + WLEntry.COLUMN_NAME_USER_ID + "= '"+ user_id +"' AND "
                    + WLEntry.COLUMN_NAME_EVENT_ID + " = '" + event_id +"'";
            db.execSQL(updateQuery);
            Log.d("MSG: ", "query = " + updateQuery);
        }else if(action_completed.equals(ServerEntry.UPDATE_ACTION_DELETE) &&
                status.equals(ServerEntry.UPDATE_STATUS_SYNCED)){ //deleted successfully
            //delete from toDelete database
            db.delete(WLEntry.TO_DELETE_TABLE_NAME, WLEntry.COLUMN_NAME_USER_ID + " = '" + user_id +
                    "' AND " + WLEntry.COLUMN_NAME_EVENT_ID + " = " + event_id, null);
        }

        db.close();
    }

    /*
    ################################################################################################

    BELOW ARE POSSIBLE OTHER METHODS. UNKNOWN IF WILL BE USEFUL

    ################################################################################################
    */

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
            e.setID(Integer.parseInt(cursor.getString(0)));
            e.setTitle(cursor.getString(1));
            e.setHoster(cursor.getString(2));

            SimpleDateFormat date_format = new SimpleDateFormat(EventEntry.DATE_FORMAT);
            e.setStartTime(new Date(cursor.getInt(3)));
            e.setEndTime(new Date(cursor.getInt(4)));
            e.setLatLon(Double.parseDouble(cursor.getString(5)),
                    Double.parseDouble(cursor.getString(6)));
            e.setLocation(cursor.getString(7));
            e.setDescription(cursor.getString(8));
            e.setLink(cursor.getString(9));
            e.setImageLink(cursor.getString(10));

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
        Log.d("MSG: ", "deleteEventItem id=" + Integer.toString(id));

        return false;
    }

    public ArrayList<EventItem> getAllEvents() {
        ArrayList<EventItem> events = new ArrayList<EventItem>();

        // 1. build the query
        String query = "SELECT  * FROM " + EventEntry.TABLE_NAME;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        EventItem e = null;
        if (cursor.moveToFirst()) {
            do {
                e = new EventItem();
                e.setID(Integer.parseInt(cursor.getString(0)));
                e.setTitle(cursor.getString(1));
                e.setHoster(cursor.getString(2));

                e.setStartTime(new Date(cursor.getInt(3)));
                e.setEndTime(new Date(cursor.getInt(4)));
                e.setLatLon(Double.parseDouble(cursor.getString(5)),
                        Double.parseDouble(cursor.getString(6)));
                e.setLocation(cursor.getString(7));
                e.setDescription(cursor.getString(8));
                e.setLink(cursor.getString(9));
                e.setImageLink(cursor.getString(10));

                // Add newly created e to events
                events.add(e);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Log.d("MSG: getAllEvents()", events.toString());

        // return events
        return events;
    }


}