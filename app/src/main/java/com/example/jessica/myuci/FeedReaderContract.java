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
        public static final int NUM_COLUMNS = 11;
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
    }

    /* Inner class that defines the watch later events table contents */
    public static abstract class WLEntry{
        public static final String GET_ID = "2";
        public static final String TABLE_NAME = "watch_later_events";
        public static final int NUM_COLUMNS = 3;
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_EVENT_ID = "event_id";
        public static final String COLUMN_NAME_UPDATE_STATUS = "update_status";
    }


}
