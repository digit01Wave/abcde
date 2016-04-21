package com.example.jessica.myuci;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.jessica.myuci.FeedReaderContract.EventEntry;


public class EventListActivity extends EventSyncActivity {


    String[][] myDataset;

    //Spinner, learned how to use spinner from https://www.youtube.com/watch?v=28jA5-mO8K8
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;

    MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.content_event_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //create specialized bottom toolbar
        SplitToolbar myToolbar = (SplitToolbar) findViewById(R.id.bottom_toolbar);
        super.setOnCreateOptions(myToolbar, R.id.action_upcoming_events);
        super.setClickListener(myToolbar, R.id.action_upcoming_events);

        syncSQLiteMySQLDB();

        Log.d("MSG: ", "About to get events and set adapter");
        myDataset = controller.getAllEventStrings(null);
        mAdapter = new MyAdapter(myDataset);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

        //settings for recycler view
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        // Spinner
        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.sort, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("MSG: ", "inside spinner");
                if (position == 0) {
                    myDataset = controller.getAllEventStrings(null);
                }
                if (position == 1) {
                    myDataset = controller.getAllEventStrings("start_time");
                }
                if (position == 2) {
                    myDataset = controller.getAllEventStrings("end_time");
                }
                if (position == 3) { //distance
                    Location location = getMyLocation();
                    if(location == null){
                        Toast.makeText(getApplicationContext(), "No GPS coordinates found. List not updated.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    myDataset = controller.getAllEventStrings("( 3959 * acos( cos( radians("+location.getLatitude()+")" +
                            " * cos( radians( lat ) ) * cos( radians( lon ) - radians("+location.getLongitude()+ ") ) + " +
                            "sin( radians("+location.getLatitude()+") ) * sin(radians(lat)) ) )");
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
                bundle.putString("list_title", EventEntry.TABLE_NAME);
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



    // Reload MainActivity
    public void reloadActivity() {
        Intent objIntent = new Intent(getApplicationContext(), EventListActivity.class);
        startActivity(objIntent);
        finish(); //so we don't have the old one on the activity stack
    }

    //Go to Map View
    public void goMapViewActivity(View view){
        Intent intent = new Intent(this, MapViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("where_clause", "null");
        intent.putExtras(bundle);
        startActivity(intent);
    }




}
