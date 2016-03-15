package com.example.jessica.myuci;

import android.provider.BaseColumns;

/**
 * Created by Jessica on 2/9/2016.
 */
public final class FeedReaderContract {
    //contract class is container for constants that define names for URI's, tables, and columns
    //and allows you to use the sme constants across all the other classes in the same package

    // To prevent someone from accidentally instantiating the contract class,
    // we give it an empty constructor.
    public FeedReaderContract() {}

    /* Inner class that defines the event table contents */
    public static abstract class EventEntry{
        public static final String TABLE_NAME = "uci_events";
        public static final int NUM_COLUMNS = 13;
        public static final String COLUMN_NAME_EVENT_ID = "event_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_HOSTER = "hoster";
        public static final String COLUMN_NAME_START_TIME = "start_time";
        public static final String COLUMN_NAME_END_TIME = "end_time";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LON = "lon";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_LINK = "link";
        public static final String COLUMN_NAME_IMAGE_LINK = "image_link";
        public static final String COLUMN_NAME_SOURCE_TYPE = "source_type";
        public static final String COLUMN_NAME_SOURCE_SUBTYPE = "source_subtype";

        public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    }

    /*Iner class that defines the common elements of watch_later and calendar_table*/
    public static abstract class PersonalEntry{
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_EVENT_ID = "event_id";
        public static final String COLUMN_NAME_UPDATE_STATUS = "update_status";
    }
    /* Inner class that defines the watch later events table contents */
    public static abstract class WLEntry{
        public static final String TABLE_NAME = "watch_later_events";
        public static final String TO_DELETE_TABLE_NAME = "watch_later_to_delete";
    }

    /* Inner class that defines the watch later events table contents */
    public static abstract class CalendarEntry{
        public static final String TABLE_NAME = "calendar_events";
        public static final String TO_DELETE_TABLE_NAME = "calendar_to_delete";
    }

    public static abstract class KrumbsImagesEntry {
        public static final String TABLE_NAME = "cur_krumbs_images";
        public static final String COLUMN_NAME_IMAGELINK = "ImageLink";
        public static final String COLUMN_NAME_LAT = "Latitude";
        public static final String COLUMN_NAME_LNG = "Longitude";
        public static final String COLUMN_NAME_MOOD = "Mood";
        public static final String COLUMN_NAME_SCORE = "Score";
    }

    /* Inner class thad defines server related content*/
    public static abstract class ServerEntry{
        public static final String URL_GET_EVENT = "http://54.215.240.25/myuci/getevents.php";
        public static final String URL_GET_SYNC = "http://54.215.240.25/myuci/getdbrowcount.php";

        public static final String URL_INSERT_WATCH_LATER = "http://54.215.240.25/myuci/updateWatchLater.php";
        public static final String URL_GET_WL = "http://54.215.240.25/myuci/getWatchLater.php";

        public static final String URL_INSERT_CALENDAR = "http://54.215.240.25/myuci/updateCalendar.php";
        public static final String URL_GET_CALENDAR = "http://54.215.240.25/myuci/getCalendar.php";

//        public static final String URL_GET_EVENT = "http://10.0.2.2/myuci/getevents.php";
//        public static final String URL_GET_SYNC = "http://10.0.2.2/myuci/getdbrowcount.php";
//
//        public static final String URL_INSERT_WATCH_LATER = "http://10.0.2.2/myuci/updateWatchLater.php";
//        public static final String URL_GET_WL = "http://10.0.2.2/myuci/getWatchLater.php";
//
//        public static final String URL_INSERT_CALENDAR = "http://10.0.2.2/myuci/updateCalendar.php";
//        public static final String URL_GET_CALENDAR = "http://10.0.2.2/myuci/getCalendar.php";

        public static final String JSON_UPDATE_TITLE = "UpdateEventsJSON";

        public static final String UPDATE_STATUS_UNSYNCED = "no";
        public static final String UPDATE_STATUS_SYNCED = "yes";

        public static final String UPDATE_ACTION_TITLE = "update_action";
        public static final String UPDATE_ACTION_ADD = "add";
        public static final String UPDATE_ACTION_DELETE = "delete";
    }

    /*Current User Information*/
    public static abstract class UserInfo{
        public static String USER_ID;
    }


}
