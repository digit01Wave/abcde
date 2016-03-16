package com.example.jessica.myuci;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Jessica on 2/9/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private String[][] mDataset;
    /*Dataset Item Format based on Index
    * 0: id
    * 1: title
    * 2: hoster
    * 3: start_time
    * 4: end_time
    * 5: lat
    * 6: lon
    * 7: location
    * 8: description
    * 9: link
    * 10: image_link
    * 11: source_type
    * 12: source_subtype
    * */

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView hoster;
        public TextView start_time;
        public TextView end_time;
        public TextView location;
        public TextView description;
        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            hoster = (TextView) view.findViewById(R.id.hoster);
            start_time = (TextView) view.findViewById(R.id.start_time);
            end_time = (TextView) view.findViewById(R.id.end_time);
            location = (TextView) view.findViewById(R.id.location);
            description = (TextView) view.findViewById(R.id.description);
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
        viewHolder.title.setText(mDataset[position][1]);
        viewHolder.hoster.setText(mDataset[position][2]);
        viewHolder.start_time.setText(mDataset[position][3]);
        viewHolder.end_time.setText(mDataset[position][4]);
        viewHolder.location.setText(mDataset[position][7]);
        viewHolder.description.setText(mDataset[position][8]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    public String[] getDatasetItem(int position){
        return mDataset[position];
    }


}
