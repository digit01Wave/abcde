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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class PersonalListActivity extends BaseActivity {

    RecyclerView mRecyclerView;
    MySQLiteHelper controller = new MySQLiteHelper(this, null);
    ProgressDialog prgDialog;
    String table_name;
    String get_url;
    String insert_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_list);
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


        //Initialize Progress Dialog properties
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Syncing your new data with remote database. Please wait...");
        prgDialog.setCancelable(false);

        //find which personalized list we are viewing
        Bundle extras = getIntent().getExtras();
        table_name = extras.getString("table_name");

        //get the appropriate urls
        if(table_name.equals(FeedReaderContract.WLEntry.TABLE_NAME)){
            insert_url = FeedReaderContract.ServerEntry.URL_INSERT_WATCH_LATER;
            get_url = FeedReaderContract.ServerEntry.URL_GET_WL;
            setTitle(getString(R.string.watch_later_list_title));
        } else { //is calendar item
            insert_url = FeedReaderContract.ServerEntry.URL_INSERT_CALENDAR;
            get_url = FeedReaderContract.ServerEntry.URL_GET_CALENDAR;
            setTitle(getString(R.string.calendar_list_title));
        }
        Log.d("MSG:", "Created Personal List " + table_name);
    }

    @Override
    protected void onResume(){
        super.onResume();


        syncSQLiteMySQLDB();

        mRecyclerView = (RecyclerView) findViewById(R.id.personal_list_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //should find a way to get a proper user id
        String[][] myDataset = controller.getAllPersonalListEvents(table_name, FeedReaderContract.UserInfo.USER_ID);

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
                bundle.putString("table_name", table_name);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        Log.d("MSG: ", "Resumed " + table_name);


    }



    public void syncSQLiteMySQLDB(){
        //Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        if(controller.dbPersonalListSyncCount(table_name) != 0){
            prgDialog.show();
            params.put(FeedReaderContract.ServerEntry.JSON_UPDATE_TITLE, controller.composeJSONfromPersonalSQLite(table_name));
            client.post(insert_url, params, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String response) {
                    System.out.println(response);
                    prgDialog.hide();
                    try {
                        JSONArray arr = new JSONArray(response);
                        System.out.println(arr.length());
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            Log.d("Print: ", obj.getString(FeedReaderContract.PersonalEntry.COLUMN_NAME_USER_ID));
                            Log.d("Print: ", obj.getString(FeedReaderContract.PersonalEntry.COLUMN_NAME_EVENT_ID));
                            Log.d("Print: ", obj.getString(FeedReaderContract.ServerEntry.UPDATE_ACTION_TITLE));
                            Log.d("Print: ", obj.getString(FeedReaderContract.PersonalEntry.COLUMN_NAME_UPDATE_STATUS));
                            controller.updatePersonalListSyncStatus(table_name, obj.getString(FeedReaderContract.PersonalEntry.COLUMN_NAME_USER_ID),
                                    obj.getString(FeedReaderContract.PersonalEntry.COLUMN_NAME_EVENT_ID),
                                    obj.getString(FeedReaderContract.ServerEntry.UPDATE_ACTION_TITLE),
                                    obj.getString(FeedReaderContract.PersonalEntry.COLUMN_NAME_UPDATE_STATUS));
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
            client.post(get_url, params, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String response) {

                    try {
                        JSONArray arr = new JSONArray(response);

                        //if have unsynced changes
                        if(arr.length() != controller.getTableLength(table_name)) {
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = (JSONObject) arr.get(i);
                                Log.d("Print: ", obj.getString(FeedReaderContract.PersonalEntry.COLUMN_NAME_USER_ID));
                                Log.d("Print: ", obj.getString(FeedReaderContract.PersonalEntry.COLUMN_NAME_EVENT_ID));
                                controller.addPersonalListItem(table_name, obj.getString(FeedReaderContract.PersonalEntry.COLUMN_NAME_USER_ID),
                                        obj.getString(FeedReaderContract.PersonalEntry.COLUMN_NAME_EVENT_ID),
                                        FeedReaderContract.ServerEntry.UPDATE_STATUS_SYNCED);
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
        Intent objIntent = new Intent(getApplicationContext(), PersonalListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("table_name", table_name);
        objIntent.putExtras(bundle);
        startActivity(objIntent);
        finish(); //so we don't have the old one on the activity stack
    }



}
