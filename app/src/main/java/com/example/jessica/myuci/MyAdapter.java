package com.example.jessica.myuci;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.jessica.myuci.FeedReaderContract.EventEntry;

/**
 * Created by Jessica on 2/9/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private String[][] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
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
            Log.d("MSG:", "Success: Created ViewHolder");
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(String[][] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_event_list, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        viewHolder.event_id.setText(mDataset[position][0]);
        viewHolder.title.setText(mDataset[position][1]);
        viewHolder.hoster.setText(mDataset[position][2]);
        viewHolder.start_time.setText(mDataset[position][3]);
        viewHolder.end_time.setText(mDataset[position][4]);
        viewHolder.lat.setText(mDataset[position][5]);
        viewHolder.lon.setText(mDataset[position][6]);
        viewHolder.location.setText(mDataset[position][7]);
        viewHolder.description.setText(mDataset[position][8]);
        viewHolder.link.setText(mDataset[position][9]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

}
