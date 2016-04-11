package com.example.jessica.myuci;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.example.jessica.myuci.GetLatLng.getlatlngFromAddress;

public class CurrentEventsActivity extends BaseActivity {

    //DB Class to perform DB related operations
    MySQLiteHelper controller = new MySQLiteHelper(this, null);

    //Progress Dialog Object
    ProgressDialog prgDialog;
    String[] queryValues;
    String[][] myDataset;

    //Spinner, learned how to use spinner from https://www.youtube.com/watch?v=28jA5-mO8K8
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    String whereClause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize Progress Dialog properties
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Transferring Data from Remote MySQL DB and Syncing SQLite. Please wait...");
        prgDialog.setCancelable(false);

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
        MyAdapter mAdapter = new MyAdapter(myDataset);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
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
                    MyAdapter mAdapter = new MyAdapter(myDataset);
                    RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                    mRecyclerView.setHasFixedSize(true);
                    // use a linear layout manager
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);
                }
                if (position == 1) {
                    myDataset = controller.getAllEventStringsWhereOrder(whereClause, FeedReaderContract.EventEntry.COLUMN_NAME_START_TIME);
                    MyAdapter mAdapter = new MyAdapter(myDataset);
                    RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                    mRecyclerView.setHasFixedSize(true);
                    // use a linear layout manager
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);
                }
                if (position == 2) {
                    myDataset = controller.getAllEventStringsWhereOrder(whereClause, FeedReaderContract.EventEntry.COLUMN_NAME_END_TIME);
                    MyAdapter mAdapter = new MyAdapter(myDataset);
                    RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                    mRecyclerView.setHasFixedSize(true);
                    // use a linear layout manager
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);
                }
                if (position == 3) {
                    myDataset = controller.getAllEventStringsWhereOrder(whereClause, FeedReaderContract.EventEntry.COLUMN_NAME_LOCATION);
                    MyAdapter mAdapter = new MyAdapter(myDataset);
                    RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                    mRecyclerView.setHasFixedSize(true);
                    // use a linear layout manager
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);
                }
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

    // Method to Sync MySQL to SQLite DB
    public void syncSQLiteMySQLDB() {
        Log.d("MSG: ", "Starting Event Sync");
        // Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();
        // Show ProgressBar
        prgDialog.show();
        // Make Http call to getusers.php
        client.get(FeedReaderContract.ServerEntry.URL_GET_EVENT, params, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) { //byte [] response
                // called when response HTTP status is "200 OK"
                // Hide ProgressBar
                prgDialog.dismiss();
                // Update SQLite DB with response sent by getusers.php
                Log.d("MSG: ", "Grabbed Event Successfully = " + response);
                updateSQLite(response);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable e) { //byte[] response
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                // Hide ProgressBar
                prgDialog.dismiss();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                            Toast.LENGTH_LONG).show();
                }

            }


        });
    }

    public void updateSQLite(String response){
        Log.d("MSG: ", "starting sqlLite event update ");
        // Create GSON object
        Gson gson = new GsonBuilder().create();
        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(response);
            Log.d("MSG: ", "JSON array length = " + Integer.toString(arr.length()));
            // If array elements is not zero and is not equal to what we already have
            if(arr.length() != 0 && arr.length() != controller.getTableLength(FeedReaderContract.EventEntry.TABLE_NAME)){
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);
                    queryValues = new String[FeedReaderContract.EventEntry.NUM_COLUMNS];

                    // DB QueryValues Object to insert into SQLite
                    queryValues[0] = obj.get(FeedReaderContract.EventEntry.COLUMN_NAME_EVENT_ID).toString();
                    queryValues[1] = obj.get(FeedReaderContract.EventEntry.COLUMN_NAME_TITLE).toString();
                    queryValues[2] = obj.get(FeedReaderContract.EventEntry.COLUMN_NAME_HOSTER).toString();
                    queryValues[3] = obj.get(FeedReaderContract.EventEntry.COLUMN_NAME_START_TIME).toString();
                    queryValues[4] = obj.get(FeedReaderContract.EventEntry.COLUMN_NAME_END_TIME).toString();
                    queryValues[5] = obj.get(FeedReaderContract.EventEntry.COLUMN_NAME_LAT).toString();
                    queryValues[6] = obj.get(FeedReaderContract.EventEntry.COLUMN_NAME_LON).toString();
                    queryValues[7] = obj.get(FeedReaderContract.EventEntry.COLUMN_NAME_LOCATION).toString();
                    queryValues[8] = obj.get(FeedReaderContract.EventEntry.COLUMN_NAME_DESCRIPTION).toString();
                    queryValues[9] = obj.get(FeedReaderContract.EventEntry.COLUMN_NAME_LINK).toString();
                    queryValues[10] = obj.get(FeedReaderContract.EventEntry.COLUMN_NAME_IMAGE_LINK).toString();
                    queryValues[11] = obj.get(FeedReaderContract.EventEntry.COLUMN_NAME_SOURCE_TYPE).toString();
                    queryValues[12] = obj.get(FeedReaderContract.EventEntry.COLUMN_NAME_SOURCE_SUBTYPE).toString();
                    //if no lat or lon, then try and generate them from location
                    if(queryValues[5].equals("null") || queryValues[6].equals("null")){
                        LatLng loc = getlatlngFromAddress(CurrentEventsActivity.this, queryValues[7] + " Irvine, CA");
                        if(loc != null){
                            queryValues[5] = Double.toString(loc.latitude);
                            queryValues[6] = Double.toString(loc.longitude);
                            Log.d("MSG: ", "Retrieved Lat Long for event_id {" + queryValues[0]+ "} is (" + queryValues[5] + ", " + queryValues[6] + ")");
                        }
                    }

                    // Insert Event into SQLite DB
                    try {
                        controller.addEventItem(queryValues, false);
                    } catch(java.text.ParseException e){
                        Log.d("FAILED: ", "Parse excpetion error. Could not add to Database" + e);
                    }
                }
                // Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Users
                //updateMySQLSyncSts(gson.toJson(event_synclist));
                // Reload the Main Activity
                reloadActivity();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d("---", "error in updateMySqlLite");
            e.printStackTrace();
        }
    }

    // Reload MainActivity
    public void reloadActivity() {
        Intent objIntent = new Intent(getApplicationContext(), CurrentEventsActivity.class);
        startActivity(objIntent);
        finish(); //so we don't have the old one on the activity stack
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
