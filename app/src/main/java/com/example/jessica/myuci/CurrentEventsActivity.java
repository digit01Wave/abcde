package com.example.jessica.myuci;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class CurrentEventsActivity extends EventSyncActivity {

    String[][] myDataset;
    MyAdapter mAdapter;

    //Spinner, learned how to use spinner from https://www.youtube.com/watch?v=28jA5-mO8K8
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    String whereClause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.content_event_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //create specialized bottom toolbar
        SplitToolbar myToolbar = (SplitToolbar) findViewById(R.id.bottom_toolbar);
        super.setOnCreateOptions(myToolbar, R.id.action_current_events);
        super.setClickListener(myToolbar, R.id.action_current_events);

        syncSQLiteMySQLDB();
        Long curTimeStamp = System.currentTimeMillis();
        //select ongoing events
        whereClause = FeedReaderContract.EventEntry.COLUMN_NAME_START_TIME + " < " + curTimeStamp +
                " AND " + FeedReaderContract.EventEntry.COLUMN_NAME_END_TIME + " > " + curTimeStamp;
        myDataset = controller.getAllEventStringsWhere(whereClause);
        mAdapter = new MyAdapter(myDataset);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        // Spinner
        spinner=(Spinner) findViewById(R.id.spinner);
        adapter=ArrayAdapter.createFromResource(this, R.array.sort, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    myDataset = controller.getAllEventStringsWhere(whereClause);
                }
                if (position == 1) {
                    myDataset = controller.getAllEventStringsWhereOrder(whereClause, FeedReaderContract.EventEntry.COLUMN_NAME_START_TIME);
                }
                if (position == 2) {
                    myDataset = controller.getAllEventStringsWhereOrder(whereClause, FeedReaderContract.EventEntry.COLUMN_NAME_END_TIME);
                }
                if (position == 3) {
                    myDataset = controller.getAllEventStringsWhereOrder(whereClause, FeedReaderContract.EventEntry.COLUMN_NAME_LOCATION);
                }
                mAdapter.setMyDataset(myDataset);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //below is a solution created by http://www.littlerobots.nl/blog/Handle-Android-RecyclerView-Clicks/
        //to solve recycler view's onclick problem
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent = new Intent(v.getContext(), EventViewActivity.class);
                Bundle bundle = new Bundle();
                MyAdapter a = (MyAdapter) recyclerView.getAdapter();
                bundle.putStringArray("event_info", a.getDatasetItem(position));
                bundle.putString("list_title", FeedReaderContract.EventEntry.TABLE_NAME);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

//        // BroadCase Receiver Intent Object
//        Intent alarmIntent = new Intent(getApplicationContext(), MyBroadcastReceiver.class);
//        // Pending Intent Object
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        // Alarm Manager Object
//        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        // Alarm Manager calls BroadCast for every 20 seconds (10 * 1000), BroadCase further calls service to check if new records are inserted in
//        // Remote MySQL DB
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 5000, 20 * 1000, pendingIntent);

    }


    @Override
    protected void onResume() {
        Log.d("MSG:", "Resuming Event List Activities");
        super.onResume();
    }

    //Go to Map View
    public void goMapViewActivity(View view){
        Intent intent = new Intent(this, MapViewActivity.class);
        Bundle bundle = new Bundle();
        Long curTimeStamp = System.currentTimeMillis();
        //select ongoing events
        String whereClause = FeedReaderContract.EventEntry.COLUMN_NAME_START_TIME + " < " + curTimeStamp +
                " AND " + FeedReaderContract.EventEntry.COLUMN_NAME_END_TIME + " > " + curTimeStamp;
        bundle.putString("where_clause", whereClause);
        intent.putExtras(bundle);
        startActivity(intent);
    }


}
