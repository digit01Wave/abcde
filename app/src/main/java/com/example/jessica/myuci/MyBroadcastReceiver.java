package com.example.jessica.myuci;

/**
 * Created by Jessica on 2/22/2016.
 * Pulled from site http://programmerguru.com/android-tutorial/how-to-sync-remote-mysql-db-to-sqlite-on-android/
 */
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.jessica.myuci.FeedReaderContract.ServerEntry;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class MyBroadcastReceiver extends BroadcastReceiver {
    static int noOfTimes = 0;


    // Method gets called when Broad Case is issued from MainActivity for every 10 seconds
    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO Auto-generated method stub
        noOfTimes++;
        Toast.makeText(context, "BC Service Running for " + noOfTimes + " times", Toast.LENGTH_SHORT).show();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        // Checks if new records are inserted in Remote MySQL DB to proceed with Sync operation
        client.post(ServerEntry.URL_GET_SYNC,params ,new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                Log.d("PRINT: ", response);
                try {
                    // Create JSON object out of the response sent by getdbrowcount.php
                    JSONObject obj = new JSONObject(response);
                    System.out.println(obj.get("count"));
                    // If the count value is not zero, call MyService to display notification
                    if(obj.getInt("count") != 0){
                        final Intent intnt = new Intent(context, MyService.class);
                        // Set unsynced count in intent data
                        intnt.putExtra("intntdata", "Unsynced Rows Count "+obj.getInt("count"));
                        // Call MyService
                        context.startService(intnt);
                    }else{
                        Toast.makeText(context, "Sync not needed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
                // TODO Auto-generated method stub
                if(statusCode == 404){
                    Toast.makeText(context, "404", Toast.LENGTH_SHORT).show();
                }else if(statusCode == 500){
                    Toast.makeText(context, "500", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Error occured!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
