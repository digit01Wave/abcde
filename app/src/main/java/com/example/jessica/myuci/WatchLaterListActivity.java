/*
* most code from site http://programmerguru.com/android-tutorial/how-to-sync-sqlite-on-android-to-mysql-db/
* */
package com.example.jessica.myuci;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.jessica.myuci.FeedReaderContract.WLEntry;
import com.example.jessica.myuci.FeedReaderContract.PersonalEntry;
import com.example.jessica.myuci.FeedReaderContract.ServerEntry;
import com.example.jessica.myuci.FeedReaderContract.UserInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import cz.msebera.android.httpclient.Header;

public class WatchLaterListActivity extends BaseActivity {

    RecyclerView mRecyclerView;
    MySQLiteHelper controller = new MySQLiteHelper(this, null);
    ProgressDialog prgDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_later_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    @Override
    protected void onResume(){
        super.onResume();

        //Initialize Progress Dialog properties
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Synching your new data with remote database. Please wait...");
        prgDialog.setCancelable(false);

        syncWatchLaterSQLiteMySQLDB();

        mRecyclerView = (RecyclerView) findViewById(R.id.watch_later_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //get database
        MySQLiteHelper controller = new MySQLiteHelper(this, null);

        //should find a way to get a proper user id
        String[][] myDataset = controller.getAllPersonalListEvents(WLEntry.TABLE_NAME, UserInfo.USER_ID);

        MyAdapter mAdapter = new MyAdapter(myDataset);
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
                bundle.putString("list_title", WLEntry.TABLE_NAME);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


    }



    public void syncWatchLaterSQLiteMySQLDB(){
        //Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        if(controller.dbPersonalListSyncCount(WLEntry.TABLE_NAME) != 0){
            prgDialog.show();
            params.put(ServerEntry.JSON_UPDATE_TITLE, controller.composeJSONfromPersonalSQLite(WLEntry.TABLE_NAME));
            client.post(ServerEntry.URL_INSERT_WATCH_LATER, params, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String response) {
                    System.out.println(response);
                    prgDialog.hide();
                    try {
                        JSONArray arr = new JSONArray(response);
                        System.out.println(arr.length());
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            Log.d("Print: ", obj.getString(PersonalEntry.COLUMN_NAME_USER_ID));
                            Log.d("Print: ", obj.getString(PersonalEntry.COLUMN_NAME_EVENT_ID));
                            Log.d("Print: ", obj.getString(ServerEntry.UPDATE_ACTION_TITLE));
                            Log.d("Print: ", obj.getString(PersonalEntry.COLUMN_NAME_UPDATE_STATUS));
                            controller.updatePersonalListSyncStatus(WLEntry.TABLE_NAME, obj.getString(PersonalEntry.COLUMN_NAME_USER_ID),
                                    obj.getString(PersonalEntry.COLUMN_NAME_EVENT_ID),
                                    obj.getString(ServerEntry.UPDATE_ACTION_TITLE),
                                    obj.getString(PersonalEntry.COLUMN_NAME_UPDATE_STATUS));
                        }
                        Toast.makeText(getApplicationContext(), "DB Sync completed!", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
                    // TODO Auto-generated method stub
                    prgDialog.hide();
                    if (statusCode == 404) {
                        Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                    } else if (statusCode == 500) {
                        Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{ //check if changes had been made on another machine
            client.post(ServerEntry.URL_GET_WL, params, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String response) {

                    try {
                        JSONArray arr = new JSONArray(response);

                        //if have unsynced changes
                        if(arr.length() != controller.getTableLength(WLEntry.TABLE_NAME)) {
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = (JSONObject) arr.get(i);
                                Log.d("Print: ", obj.getString(PersonalEntry.COLUMN_NAME_USER_ID));
                                Log.d("Print: ", obj.getString(PersonalEntry.COLUMN_NAME_EVENT_ID));
                                controller.addPersonalListItem(WLEntry.TABLE_NAME, obj.getString(PersonalEntry.COLUMN_NAME_USER_ID),
                                        obj.getString(PersonalEntry.COLUMN_NAME_EVENT_ID),
                                        ServerEntry.UPDATE_STATUS_SYNCED);
                                reloadActivity();
                            }
                        }
                        Toast.makeText(getApplicationContext(), "Remote Sync completed!", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(getApplicationContext(), "Error Occured In Watch Later Sync [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String response, Throwable e) {
                    // TODO Auto-generated method stub
                    prgDialog.hide();
                    if (statusCode == 404) {
                        Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                    } else if (statusCode == 500) {
                        Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }

    // Reload MainActivity
    public void reloadActivity() {
        Intent objIntent = new Intent(getApplicationContext(), WatchLaterListActivity.class);
        startActivity(objIntent);
        finish(); //so we don't have the old one on the activity stack
    }


}

