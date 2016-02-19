package com.example.jessica.myuci;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.jessica.myuci.FeedReaderContract.EventEntry;

/**
 * Created by Jessica on 2/18/2016.
 */
public class MyCursorAdapter extends CursorRecyclerViewAdapter<MyCursorAdapter.ViewHolder> {
//    public MyCursorAdapter(Context context, Cursor cursor, int flags) {
//        super(context, cursor,0);
//    }

    public MyCursorAdapter(Context context,Cursor cursor)
    {
        super(context,cursor);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView event_id;
        public TextView title;
        public TextView hoster;
        public TextView start_time;
        public TextView end_time;
        public TextView lat;
        public TextView lon;
        public TextView location;
        public TextView description;
        public TextView link;
        public ViewHolder(View view) {
            super(view);
            event_id = (TextView) view.findViewById(R.id.event_id);
            title = (TextView) view.findViewById(R.id.title);
            hoster = (TextView) view.findViewById(R.id.hoster);
            start_time = (TextView) view.findViewById(R.id.start_time);
            end_time = (TextView) view.findViewById(R.id.end_time);
            lat = (TextView) view.findViewById(R.id.lat);
            lon = (TextView) view.findViewById(R.id.lon);
            location = (TextView) view.findViewById(R.id.location);
            description = (TextView) view.findViewById(R.id.description);
            link = (TextView) view.findViewById(R.id.link);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_event_list, parent, false);
        Log.d("MSG:", "CREATING VIEWHOLDER");
        ViewHolder vh = new ViewHolder(itemView);
        Log.d("MSG:", "ALMOST DONE VIEW HOLDER");
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        // Populate fields with extracted properties
        viewHolder.event_id.setText(cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_EVENT_ID)));
        viewHolder.title.setText(cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_TITLE)));
        viewHolder.hoster.setText(cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_HOSTER)));
        viewHolder.start_time.setText(cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_START_TIME)));
        viewHolder.end_time.setText(cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_END_TIME)));
        viewHolder.lat.setText(cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_LAT)));
        viewHolder.lon.setText(cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_LON)));
        viewHolder.location.setText(cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_LOCATION)));
        viewHolder.description.setText(cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_DESCRIPTION)));
        viewHolder.link.setText(cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_LINK)));
    }

}
