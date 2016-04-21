package com.example.jessica.myuci;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Jessica on 4/18/2016.
 * EventSync holds common functions used to sync events
 */
public class EventSyncActivity extends BaseActivity{
    //DB Class to perform DB related operations
    MySQLiteHelper controller = new MySQLiteHelper(this, null);

    //Progress Dialog Object
    ProgressDialog prgDialog;
    String[] queryValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Progress Dialog for event list sync
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Transferring Data from Remote MySQL DB and Syncing SQLite. Please wait...");
        prgDialog.setCancelable(false);
    }


    //method to get current GPS location
    public Location getMyLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        List<String> providers = locationManager.getProviders(true);
        Location l = null;
        for (int i = 0; i < providers.size(); i++) {
            l = locationManager.getLastKnownLocation(providers.get(i));
            if (l != null)
                break;
        }
        return l;

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
        //build JSON and get response
        Gson gson = new GsonBuilder().create();
        HashMap<String, Long> previousUpdate = new HashMap<>();
        previousUpdate.put(FeedReaderContract.EventEntry.COLUMN_NAME_LAST_UPDATED, MySQLiteHelper.last_updated);
        params.put(FeedReaderContract.ServerEntry.JSON_GET_TITLE, gson.toJson(previousUpdate));
        // Make Http call to getusers.php
        client.post(FeedReaderContract.ServerEntry.URL_GET_EVENT, params, new TextHttpResponseHandler() {

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
                Log.d("MSG: ", "Grabbed Event Successfully from last updated (" + MySQLiteHelper.last_updated+ ")= " + response);
                MySQLiteHelper.last_updated = java.lang.System.currentTimeMillis()/1000L;
                Log.d("MSG: ", "Update complete at "+ MySQLiteHelper.last_updated);
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

    //helper method to update SQLite based on resonse provided by the server, after which the page is reloaded
    public void updateSQLite(String response){
        Log.d("MSG: ", "starting sqlLite event update ");
        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(response);
            Log.d("MSG: ", "JSON array length = " + Integer.toString(arr.length()));
            // If array elements is not zero and is not equal to what we already have
            if(arr.length() != 0){
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
                //reloadActivity();
            }
        } catch (JSONException e) {
            Log.d("---", "error in updateMySqlLite");
            e.printStackTrace();
        }
    }
}
