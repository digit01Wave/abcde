package com.example.jessica.myuci;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.jessica.myuci.FeedReaderContract.EventEntry;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

import static com.example.jessica.myuci.GetLatLng.getlatlngFromAddress;


public class EventListActivity extends BaseActivity {

    //DB Class to perform DB related operations
    MySQLiteHelper controller = new MySQLiteHelper(this, null);

    //Progress Dialog Object
    ProgressDialog prgDialog;
    String[] queryValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Initialize Progress Dialog properties
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Transferring Data from Remote MySQL DB and Syncing SQLite. Please wait...");
        prgDialog.setCancelable(false);

        syncSQLiteMySQLDB();

        String[][] myDataset = controller.getAllEventStrings();
        MyAdapter mAdapter = new MyAdapter(myDataset);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


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


    // Options Menu (ActionBar Menu)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    // When Options Menu is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        // When Sync action button is clicked
        if (id == R.id.refresh) {
            // Transfer data from remote MySQL DB to SQLite on Android and perform Sync
            syncSQLiteMySQLDB();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                prgDialog.hide();
                // Update SQLite DB with response sent by getusers.php
                Log.d("MSG: ", "Grabbed Event Successfully = "+response);
                updateSQLite(response);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable e) { //byte[] response
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                // Hide ProgressBar
                prgDialog.hide();
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
            if(arr.length() != 0 && arr.length() != controller.getTableLength(EventEntry.TABLE_NAME)){
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);
                    queryValues = new String[EventEntry.NUM_COLUMNS];

                    // DB QueryValues Object to insert into SQLite
                    queryValues[0] = obj.get(EventEntry.COLUMN_NAME_EVENT_ID).toString();
                    queryValues[1] = obj.get(EventEntry.COLUMN_NAME_TITLE).toString();
                    queryValues[2] = obj.get(EventEntry.COLUMN_NAME_HOSTER).toString();
                    queryValues[3] = obj.get(EventEntry.COLUMN_NAME_START_TIME).toString();
                    queryValues[4] = obj.get(EventEntry.COLUMN_NAME_END_TIME).toString();
                    queryValues[5] = obj.get(EventEntry.COLUMN_NAME_LAT).toString();
                    queryValues[6] = obj.get(EventEntry.COLUMN_NAME_LON).toString();
                    queryValues[7] = obj.get(EventEntry.COLUMN_NAME_LOCATION).toString();
                    queryValues[8] = obj.get(EventEntry.COLUMN_NAME_DESCRIPTION).toString();
                    queryValues[9] = obj.get(EventEntry.COLUMN_NAME_LINK).toString();
                    queryValues[10] = obj.get(EventEntry.COLUMN_NAME_IMAGE_LINK).toString();

                    //if no lat or lon, then try and generate them from location
                    if(queryValues[5].equals("null") || queryValues[6].equals("null")){
                        LatLng loc = getlatlngFromAddress(EventListActivity.this, queryValues[7]);
                        if(loc != null){
                            queryValues[5] = Double.toString(loc.latitude);
                            queryValues[6] = Double.toString(loc.longitude);
                            Log.d("MSG: ", "YES. GOT THE LAT LONG TO BE (" + queryValues[5] + ", " + queryValues[6] + ")");
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
        Intent objIntent = new Intent(getApplicationContext(), EventListActivity.class);
        startActivity(objIntent);
        finish(); //so we don't have the old one on the activity stack
    }


}
