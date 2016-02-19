package com.example.jessica.myuci;

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
import android.view.View;
import com.example.jessica.myuci.FeedReaderContract.EventEntry;

import java.util.ArrayList;
import java.util.Date;


public class EventListActivity extends AppCompatActivity {

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

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //get database
        MySQLiteHelper controller = new MySQLiteHelper(this, null);

        controller.addEventItem(new EventItem(0, "FirstTitle", "Some Host", new Date(), new Date(), 12.3456, 14.5678,
                "DBH 100", "SOME REALLY LONG DESCRIPTION", "Some Link"));
        controller.addEventItem(new EventItem(1, "Second Title", "Some Host", new Date(), new Date(), 12.3456, 14.5678,
                "DBH 100", "SOME REALLY LONG DESCRIPTION", "Some Link"));

        //get writable database
        //QLiteDatabase db = controller.getReadableDatabase();

        String[][] myDataset = controller.getAllEventStrings();

        //get cursor for all events
        //Cursor event_cursor = db.rawQuery("SELECT  * FROM " + EventEntry.TABLE_NAME, null);

        //MyAdapter mAdapter = new MyAdapter(myDataset.get(0));
        //MyCursorAdapter mAdapter = new MyCursorAdapter(this, event_cursor);
        MyAdapter mAdapter = new MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
        //db.close();

    }

    //for debugging purposes
    public void printDataSet(String[][] dataset){
        Log.d("MSG: ", "DATASET PRINT START_____________________________________");
        StringBuilder sb = new StringBuilder();
        for(String[] arr: dataset){
            for(String col_item: arr){
                sb.append(col_item);
                sb.append(", ");
            }
            sb.append("\n");
        }
        Log.d("MSG: ", sb.toString());
        Log.d("MSG: ", "DATASET PRINT END_____________________________________");
    }



}
