package com.example.jessica.myuci;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.jessica.myuci.FeedReaderContract.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static long last_updated = 0; //keeps track of when we had last updated our database

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "dbeventshop";

    // SQL statement to create eventList table
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";
    private static final Double LatLngRange = 0.001;
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
                    EventEntry.COLUMN_NAME_IMAGE_LINK + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_SOURCE_TYPE + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_SOURCE_SUBTYPE + TEXT_TYPE +
                    " )";
    private static final String SQL_CREATE_WATCH_LATER_TABLE =
            "CREATE TABLE " + WLEntry.TABLE_NAME + " (" +
                    PersonalEntry.COLUMN_NAME_USER_ID + TEXT_TYPE + COMMA_SEP +
                    PersonalEntry.COLUMN_NAME_EVENT_ID + INT_TYPE + COMMA_SEP +
                    PersonalEntry.COLUMN_NAME_UPDATE_STATUS + TEXT_TYPE + COMMA_SEP +
                    "PRIMARY KEY (" + PersonalEntry.COLUMN_NAME_USER_ID + ", " + PersonalEntry.COLUMN_NAME_EVENT_ID + ")" +
                    ")";

    private static final String SQL_CREATE_CALENDAR_TABLE =
            "CREATE TABLE " + CalendarEntry.TABLE_NAME + " (" +
                    PersonalEntry.COLUMN_NAME_USER_ID + TEXT_TYPE + COMMA_SEP +
                    PersonalEntry.COLUMN_NAME_EVENT_ID + INT_TYPE + COMMA_SEP +
                    PersonalEntry.COLUMN_NAME_UPDATE_STATUS + TEXT_TYPE + COMMA_SEP +
                    "PRIMARY KEY (" + PersonalEntry.COLUMN_NAME_USER_ID + ", " + PersonalEntry.COLUMN_NAME_EVENT_ID + ")" +
                    ")";

    private static final String SQL_CREATE_WATCH_LATER_DELETE_TABLE =
            "CREATE TABLE " + WLEntry.TO_DELETE_TABLE_NAME + " (" +
                    PersonalEntry.COLUMN_NAME_USER_ID + TEXT_TYPE + COMMA_SEP +
                    PersonalEntry.COLUMN_NAME_EVENT_ID + INT_TYPE + COMMA_SEP +
                    "PRIMARY KEY (" + PersonalEntry.COLUMN_NAME_USER_ID + ", " + PersonalEntry.COLUMN_NAME_EVENT_ID + ")" +
                    ")";


    private static final String SQL_CREATE_CALENDAR_DELETE_TABLE =
            "CREATE TABLE " + CalendarEntry.TO_DELETE_TABLE_NAME + " (" +
                    PersonalEntry.COLUMN_NAME_USER_ID + TEXT_TYPE + COMMA_SEP +
                    PersonalEntry.COLUMN_NAME_EVENT_ID + INT_TYPE + COMMA_SEP +
                    "PRIMARY KEY (" + PersonalEntry.COLUMN_NAME_USER_ID + ", " + PersonalEntry.COLUMN_NAME_EVENT_ID + ")" +
                    ")";

    private static final String SQL_DELETE_EVENT_TABLE =
            "DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME;
    private static final String SQL_DELETE_WATCH_LATER_TABLE =
            "DROP TABLE IF EXISTS " + WLEntry.TABLE_NAME;
    private static final String SQL_DELETE_CALENDAR_TABLE =
            "DROP TABLE IF EXISTS " + CalendarEntry.TABLE_NAME;
    private static final String SQL_DELETE_WATCH_LATER_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + WLEntry.TO_DELETE_TABLE_NAME;
    private static final String SQL_DELETE_CALENDAR_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + CalendarEntry.TO_DELETE_TABLE_NAME;
    private static final String SQL_DELETE_KRUMBS_IMAGES_TABLE =
            "DROP TABLE IF EXISTS " + KrumbsImagesEntry.TABLE_NAME;

    private static final String SQL_CREATE_KRUMBS_IMAGES_TABLE =
                    "CREATE TABLE " + KrumbsImagesEntry.TABLE_NAME + "( " +
                    KrumbsImagesEntry.COLUMN_NAME_IMAGELINK + TEXT_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    KrumbsImagesEntry.COLUMN_NAME_LAT + REAL_TYPE + COMMA_SEP +
                    KrumbsImagesEntry.COLUMN_NAME_LNG + REAL_TYPE + COMMA_SEP +
                    KrumbsImagesEntry.COLUMN_NAME_MOOD + TEXT_TYPE + COMMA_SEP +
                            KrumbsImagesEntry.COLUMN_NAME_SCORE + INT_TYPE +
                    ")";
    public MySQLiteHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create event table
        db.execSQL(SQL_CREATE_EVENT_TABLE);
        db.execSQL(SQL_CREATE_WATCH_LATER_TABLE);
        db.execSQL(SQL_CREATE_CALENDAR_TABLE);
        db.execSQL(SQL_CREATE_WATCH_LATER_DELETE_TABLE);
        db.execSQL(SQL_CREATE_CALENDAR_DELETE_TABLE);
        db.execSQL(SQL_CREATE_KRUMBS_IMAGES_TABLE);
        Log.d("MSG: ", "Created Event and Watch Later Tables");
        Log.d("Krumbs", "Krumbs_image_table created!!!!!!!!!!!!!!!!!!!!!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL(SQL_DELETE_EVENT_TABLE);
        db.execSQL(SQL_DELETE_WATCH_LATER_TABLE);
        db.execSQL(SQL_DELETE_CALENDAR_TABLE);
        db.execSQL(SQL_DELETE_WATCH_LATER_DELETE_TABLE);
        db.execSQL(SQL_DELETE_CALENDAR_DELETE_TABLE);
        db.execSQL(SQL_DELETE_KRUMBS_IMAGES_TABLE);
        Log.d("MSG: ", "Upgraded Event and Watch Later Tables");

        // create fresh tables
        this.onCreate(db);
    }


    public void addEventItem(String[] event_cols, boolean milisecond_format) throws java.text.ParseException {
        /*given array of strings of all event properties, will create event item
        * in the form [id, title, hoster, start_time, end_time, lat, lon, location, description, link]
        * event col format: index => info
        * 0 => event_id
        * 1 => title
        * 2 => hoster
        * 3 => start_time
        * 4 => end_time
        * 5 => lat
        * 6 => lon
        * 7 => location
        * 8 => description
        * 9 => link
        * 10 => image_link
        * 11 => source_type
        * 12 => source_subtype
        * milesecond_format means that the start and end_time is in miliseconds (can be converted to
        * int. If set to false instead, then it means it is in the sql format 'YYYY-MM-DD HH:MM:SS'
        * */
        Log.d("MSG: ", "AddEventItemStr");
        // reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        //create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(EventEntry.COLUMN_NAME_EVENT_ID, event_cols[0]); //event_id
        values.put(EventEntry.COLUMN_NAME_TITLE, event_cols[1]); //evnet_title
        if (!(event_cols[2].equals("null"))) { //hoster (can be null)
            values.put(EventEntry.COLUMN_NAME_HOSTER, event_cols[2]);
        }

        //start_time and end_time converted to proper format (millisecond)
        if (milisecond_format) { //is in milisecond format
            values.put(EventEntry.COLUMN_NAME_START_TIME, Integer.parseInt(event_cols[3]));
            if (!(event_cols[4].equals("null"))) {
                values.put(EventEntry.COLUMN_NAME_END_TIME, Integer.parseInt(event_cols[4]));
            }
        } else {
            DateFormat date_format = new SimpleDateFormat(EventEntry.DATE_FORMAT);
            Date start_time = date_format.parse(event_cols[3]);
            values.put(EventEntry.COLUMN_NAME_START_TIME, start_time.getTime());
            if (!(event_cols[4].equals("null"))) {
                Date end_time = date_format.parse(event_cols[4]);
                values.put(EventEntry.COLUMN_NAME_END_TIME, end_time.getTime());
            }

        }

        //add lat lon only if both are provided
        if (!(event_cols[5].equals("null") || event_cols[6].equals("null"))) {
            values.put(EventEntry.COLUMN_NAME_LAT, event_cols[5]);
            values.put(EventEntry.COLUMN_NAME_LON, event_cols[6]);
        }
        values.put(EventEntry.COLUMN_NAME_LOCATION, event_cols[7]); //add location
        if (!(event_cols[8].equals("null"))) {
            values.put(EventEntry.COLUMN_NAME_DESCRIPTION, event_cols[8]); //add description (can be null)
        }
        if (!(event_cols[9].equals("null"))) {
            values.put(EventEntry.COLUMN_NAME_LINK, event_cols[9]); //add link (can be null)
        }
        if (!(event_cols[10].equals("null"))) {
            values.put(EventEntry.COLUMN_NAME_IMAGE_LINK, event_cols[10]); //add image_link if there
        }
        if (!(event_cols[11].equals("null"))) {
            values.put(EventEntry.COLUMN_NAME_SOURCE_TYPE, event_cols[11]); //add source_type if there
        }
        if (!(event_cols[12].equals("null"))) {
            values.put(EventEntry.COLUMN_NAME_SOURCE_SUBTYPE, event_cols[12]); //add image_link if there
        }

        try {
            Log.d("MSG: ", "STRING START");
            // insert
            db.insert(EventEntry.TABLE_NAME, // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values
        } catch (Exception e) {
            //for logging
            Log.d("---", "FAILED to addEventItemStr" + e.toString());
        }
        // close
        db.close();
    }


    /*Returns all events in SQLite Db - same format as in addEventItem ordered by some column*/
    public String[][] getAllEventStrings(String col_order) {
        String selectQuery;
        Long curTimeStamp = System.currentTimeMillis();
        if(col_order == null){
            selectQuery = "SELECT  * FROM " + EventEntry.TABLE_NAME + " WHERE start_time >=" +curTimeStamp;
        }else {
            selectQuery = "SELECT  * FROM " + EventEntry.TABLE_NAME + " WHERE start_time >=" +curTimeStamp + " ORDER BY " + col_order;
        }
        Log.d("MSG:", "getAllEventStrings()" + selectQuery);
        return getEventStringsHelper(selectQuery);
    }

    /*Returns all events in SQLite Db with specific contraints (WHERE)*/
    public String[][] getAllEventStringsWhere(String where_clause) {
        if (where_clause==null || where_clause.equals("null")) {
            return getAllEventStrings(null);
        }
        String selectQuery = "SELECT  * FROM " + EventEntry.TABLE_NAME + " WHERE " + where_clause;
        Log.d("MSG:", "getAllEventStringsWhere()" + selectQuery);
        return getEventStringsHelper(selectQuery);
    }

    /*Returns all events in SQLite Db with specific contraints (WHERE) and roder (order*/
    public String[][] getAllEventStringsWhereOrder(String where_clause, String ordered_by) {
        if (where_clause==null || where_clause.equals("null")) {
            return getAllEventStrings(ordered_by);
        }
        String selectQuery = "SELECT  * FROM " + EventEntry.TABLE_NAME + " WHERE " + where_clause + " ORDER BY " + ordered_by;
        Log.d("MSG:", "getAllEventStringsWhereOrder()" + selectQuery);
        return getEventStringsHelper(selectQuery);
    }

    /*Deletes all the Events - For Debugging Purposes*/
    public void deleteAllEvents(SQLiteDatabase db) {
        /*will delete all events in database*/
        db.delete(EventEntry.TABLE_NAME, null, null);
        Log.d("MSG: ", "All events have been deleted");
    }
        /*
    ################################################################################################

    BELOW ARE METHODS FOR Personal Lists (Watch Later and Calendar)

    ################################################################################################
    */

    public void addPersonalListItem(String table_name, String user_id, String event_id, String update_status) {
        // reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        //create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(PersonalEntry.COLUMN_NAME_USER_ID, user_id);
        values.put(PersonalEntry.COLUMN_NAME_EVENT_ID, event_id);
        values.put(PersonalEntry.COLUMN_NAME_UPDATE_STATUS, update_status);

        // insert
        db.insert(table_name, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // close
        db.close();

        Log.d("MSG: ", "Added to List " + table_name + " (" + user_id + ", " + event_id + ", " + update_status + ")");
    }

    public void deletePersonalListItem(String table_name, String user_id, String event_id) {
        /*deletes item from watch_later_list sqlLite table*/

        SQLiteDatabase db = this.getWritableDatabase();

        //add to WLtoDelete if entry synced in order to sync with database later
        String selectQuery = "SELECT * FROM " + table_name + " WHERE " + PersonalEntry.COLUMN_NAME_USER_ID +
                " = '" + user_id + "' AND " + PersonalEntry.COLUMN_NAME_EVENT_ID + " = " + event_id;
        Cursor cursor = db.rawQuery(selectQuery, null);

        //if there are synced items to be deleted
        if (cursor.moveToFirst()) {
            //create ContentValues to add key "column"/value
            ContentValues values = new ContentValues();
            values.put(PersonalEntry.COLUMN_NAME_USER_ID, user_id);
            values.put(PersonalEntry.COLUMN_NAME_EVENT_ID, event_id);

            // insert into appropriate delete list
            if (table_name.equals(WLEntry.TABLE_NAME)) {
                db.insert(WLEntry.TO_DELETE_TABLE_NAME, // table
                        null, //nullColumnHack
                        values); // key/value -> keys = column names/ values = column values
            } else if (table_name.equals(CalendarEntry.TABLE_NAME)) {
                db.insert(CalendarEntry.TO_DELETE_TABLE_NAME, // table
                        null, //nullColumnHack
                        values); // key/value -> keys = column names/ v
                // alues = column values
            }
        }
        cursor.close();

        //delete from database
        db.delete(table_name, PersonalEntry.COLUMN_NAME_USER_ID + " = '" + user_id + "' AND " +
                PersonalEntry.COLUMN_NAME_EVENT_ID + " = " + event_id, null);
        db.close();
    }

    /*
    * Returns whether or not the personal list has that item
    * */
    public boolean hasPersonalItem(String table_name, String user_id, String event_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + table_name + " WHERE " + PersonalEntry.COLUMN_NAME_USER_ID +
                " = '" + user_id + "' AND " + PersonalEntry.COLUMN_NAME_EVENT_ID + " = " + event_id;
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d("MSG: ", "hasItem " + selectQuery);
        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;

    }

    public String[][] getAllPersonalListEvents(String table_name, String user_id) {
        String selectQuery = "SELECT  * FROM " + EventEntry.TABLE_NAME + " INNER JOIN " + table_name +
                " ON " + table_name + "." + PersonalEntry.COLUMN_NAME_USER_ID + " = '" + user_id +
                "' AND " + EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_NAME_EVENT_ID + " = " +
                table_name + "." + PersonalEntry.COLUMN_NAME_EVENT_ID;
        Log.d("MSG:", "get all personal list events" + selectQuery);
        return getEventStringsHelper(selectQuery);
    }

    /**
     * Compose JSON out of SQLite records
     */
    public String composeJSONfromPersonalSQLite(String table_name) {
        //initialize
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase database = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + table_name + " where " +
                PersonalEntry.COLUMN_NAME_UPDATE_STATUS + " = '" + ServerEntry.UPDATE_STATUS_UNSYNCED + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);

        //add all entries to be added
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(PersonalEntry.COLUMN_NAME_USER_ID, cursor.getString(0));
                map.put(PersonalEntry.COLUMN_NAME_EVENT_ID, cursor.getString(1));
                map.put(ServerEntry.UPDATE_ACTION_TITLE, ServerEntry.UPDATE_ACTION_ADD);
                wordList.add(map);
            } while (cursor.moveToNext());
        }

        //add all entries to be deleted to appropriate table
        if (table_name.equals(WLEntry.TABLE_NAME)) {
            cursor = database.rawQuery("SELECT * FROM " + WLEntry.TO_DELETE_TABLE_NAME, null);
        } else { //is calendar event
            cursor = database.rawQuery("SELECT * FROM " + CalendarEntry.TO_DELETE_TABLE_NAME, null);
        }
        if (cursor.moveToFirst()) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(PersonalEntry.COLUMN_NAME_USER_ID, cursor.getString(0));
            map.put(PersonalEntry.COLUMN_NAME_EVENT_ID, cursor.getString(1));
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
    public int dbPersonalListSyncCount(String table_name) {
        //count all the newly added items
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + table_name + " where " +
                PersonalEntry.COLUMN_NAME_UPDATE_STATUS + " = '" + ServerEntry.UPDATE_STATUS_UNSYNCED + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();

        //adds newly deleted items
        if (table_name.equals(WLEntry.TABLE_NAME)) {
            cursor = db.rawQuery("SELECT * FROM " + WLEntry.TO_DELETE_TABLE_NAME, null);
        } else if (table_name.equals(CalendarEntry.TABLE_NAME)) {
            cursor = db.rawQuery("SELECT * FROM " + CalendarEntry.TO_DELETE_TABLE_NAME, null);
        }
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
    public void updatePersonalListSyncStatus(String table_name, String user_id, String event_id, String action_completed, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (action_completed.equals(ServerEntry.UPDATE_ACTION_ADD)) { //added
            String updateQuery = "Update " + table_name + " set " + PersonalEntry.COLUMN_NAME_UPDATE_STATUS + " = '" + status +
                    "' where " + PersonalEntry.COLUMN_NAME_USER_ID + "= '" + user_id + "' AND "
                    + PersonalEntry.COLUMN_NAME_EVENT_ID + " = '" + event_id + "'";
            db.execSQL(updateQuery);
            Log.d("MSG: ", "query = " + updateQuery);
        } else if (action_completed.equals(ServerEntry.UPDATE_ACTION_DELETE) &&
                status.equals(ServerEntry.UPDATE_STATUS_SYNCED)) { //deleted successfully
            //delete from toDelete database if had been deleted successfully
            if (table_name.equals(WLEntry.TABLE_NAME)) {
                db.delete(WLEntry.TO_DELETE_TABLE_NAME, PersonalEntry.COLUMN_NAME_USER_ID + " = '" + user_id +
                        "' AND " + PersonalEntry.COLUMN_NAME_EVENT_ID + " = " + event_id, null);
            } else {
                db.delete(CalendarEntry.TO_DELETE_TABLE_NAME, PersonalEntry.COLUMN_NAME_USER_ID + " = '" + user_id +
                        "' AND " + PersonalEntry.COLUMN_NAME_EVENT_ID + " = " + event_id, null);
            }
        }

        db.close();
    }

    /*
    ###############################################################################################

    BELOW ARE GENERAL FUNCTIONS AND HELPER METHODS

    ################################################################################################
    * */

    /*Returns a particular table's table length*/
    public int getTableLength(String table_name) {
        String selectQuery = "SELECT  * FROM " + table_name;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int toReturn = cursor.getCount();
        cursor.close();
        database.close();
        return toReturn;
    }

    private String[][] getEventStringsHelper(String selectQuery) {
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
                        cursor.getString(10), //image_link
                        cursor.getString(11), //source_type
                        cursor.getString(12) //source_subtype
                };
                event_list[index] = event;

                //since start_time and end_time are still in integer format, need to convert to datetime
                SimpleDateFormat ft = new SimpleDateFormat(EventEntry.DATE_FORMAT);
                event_list[index][3] = ft.format(Long.parseLong(event_list[index][3]));
                if (event_list[index][4] != null) { //since sometimes end_time is null
                    event_list[index][4] = ft.format(Long.parseLong(event_list[index][4]));
                }
                index++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return event_list;
    }



    /*
    ################################################################################################

    BELOW ARE POSSIBLE OTHER METHODS. UNKNOWN IF WILL BE USEFUL

    ################################################################################################
    */

    //for testing purposes
    public void addEventItem(EventItem event) {
        Log.d("MSG:", "addEventItem(event)" + event.toString());
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
        } catch (Exception e) {
            //for logging
            Log.d("---", "FAILED to addEventItem(event)" + event.toString() + e.toString());
        }
        // close
        db.close();

    }

    public List getKrumbsImageNearMe(Double lat, Double lng) {
        Log.d("Krumbs", "get image near " + lat + " " + lng);
        Double lat1 = lat - LatLngRange;
        Double lat2 = lat + LatLngRange;
        Double lng1 = lng - LatLngRange;
        Double lng2 = lng + LatLngRange;
        String selectQuery = "SELECT " + KrumbsImagesEntry.COLUMN_NAME_IMAGELINK + " FROM " + KrumbsImagesEntry.TABLE_NAME  +
                " WHERE " + KrumbsImagesEntry.COLUMN_NAME_LAT + " > " + lat1 + " AND " +
                KrumbsImagesEntry.COLUMN_NAME_LAT + " < " + lat2 + " AND " +
                KrumbsImagesEntry.COLUMN_NAME_LNG + " > " + lng1 + " AND " +
                KrumbsImagesEntry.COLUMN_NAME_LNG + " < " + lng2;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        List imageLinks = new ArrayList();
        if (cursor.moveToFirst()) {
            do {
                imageLinks.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return imageLinks;
    }
    public double getScoreOfEvent(Double lat, Double lng) {
        Log.d("Krumbs", "get image near " + lat + " " + lng);
        Double lat1 = lat - LatLngRange;
        Double lat2 = lat + LatLngRange;
        Double lng1 = lng - LatLngRange;
        Double lng2 = lng + LatLngRange;
        String selectQuery = "SELECT SUM( " + KrumbsImagesEntry.COLUMN_NAME_SCORE + " ), " +
                "COUNT(*) FROM " + KrumbsImagesEntry.TABLE_NAME  +
                " WHERE " + KrumbsImagesEntry.COLUMN_NAME_LAT + " > " + lat1 + " AND " +
                KrumbsImagesEntry.COLUMN_NAME_LAT + " < " + lat2 + " AND " +
                KrumbsImagesEntry.COLUMN_NAME_LNG + " > " + lng1 + " AND " +
                KrumbsImagesEntry.COLUMN_NAME_LNG + " < " + lng2;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        double avgScore = 0;
        if (cursor.moveToFirst()) {
            if(cursor.getString(0) != null) {
                double totalScore = Double.parseDouble(cursor.getString(0));
                double totalCount = Double.parseDouble(cursor.getString(1));
                avgScore = totalScore / totalCount;
                Log.d("Krumbs", "total score: " + totalScore + " total count: " + totalCount);
            }
        }
        Log.d("Krumbs", "avg score is " + avgScore);
        cursor.close();
        database.close();
        return avgScore;

    }
}