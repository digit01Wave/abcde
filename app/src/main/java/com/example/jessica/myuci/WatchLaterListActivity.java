/*
* most code from site http://programmerguru.com/android-tutorial/how-to-sync-sqlite-on-android-to-mysql-db/
* */
package com.example.jessica.myuci;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.example.jessica.myuci.FeedReaderContract.WLEntry;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class WatchLaterListActivity extends AppCompatActivity {
    public static final String URL_INSERT_WATCH_LATER = "http://10.0.2.2/mysqlitesync/insertWatchLater.php";

    RecyclerView mRecyclerView;
    MySQLiteHelper controller = new MySQLiteHelper(this, null);
    ProgressDialog prgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_later_list);
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
        String[][] myDataset = controller.getAllWatchLaterEvents(WLEntry.GET_ID);

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

        //Initialize Progress Dialog properties
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Synching your new data with remote database. Please wait...");
        prgDialog.setCancelable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //When Sync action button is clicked
        if (id == R.id.refresh) {
            //Sync SQLite DB data to remote MySQL DB
            syncWatchLaterSQLiteMySQLDB();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void syncWatchLaterSQLiteMySQLDB(){
        //Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String[][] watch_later =  controller.getSQLiteWatchLaterEvents(WLEntry.GET_ID);
        if(watch_later.length!=0){
            if(controller.dbWatchLaterSyncCount() != 0){
                prgDialog.show();
                params.put("watchLaterEventsJSON", controller.composeJSONfromWatchLaterSQLite());
                client.post(URL_INSERT_WATCH_LATER,params ,new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String response){
                        System.out.println(response);
                        prgDialog.hide();
                        try {
                            JSONArray arr = new JSONArray(response);
                            System.out.println(arr.length());
                            for(int i=0; i<arr.length();i++){
                                JSONObject obj = (JSONObject)arr.get(i);
                                Log.d("Print: ", obj.getString(WLEntry.COLUMN_NAME_USER_ID));
                                Log.d("Print: ", obj.getString(WLEntry.COLUMN_NAME_EVENT_ID));
                                Log.d("Print: ", obj.getString(WLEntry.COLUMN_NAME_UPDATE_STATUS));
                                controller.updateWatchLaterSyncStatus(obj.getString(WLEntry.COLUMN_NAME_USER_ID),
                                        obj.getString(WLEntry.COLUMN_NAME_EVENT_ID), obj.getString(WLEntry.COLUMN_NAME_UPDATE_STATUS));
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
                        if(statusCode == 404){
                            Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                        }else if(statusCode == 500){
                            Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else{
                Toast.makeText(getApplicationContext(), "SQLite and Remote MySQL DBs are in Sync!", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "No data in SQLite DB, please do enter User name to perform Sync action", Toast.LENGTH_LONG).show();
        }
    }


}

