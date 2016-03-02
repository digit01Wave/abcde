package com.example.jessica.myuci;

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
import com.example.jessica.myuci.FeedReaderContract.EventEntry;

import java.util.ArrayList;
import java.util.Date;


public class EventListActivity extends BaseActivity {
    RecyclerView mRecyclerView;
    MySQLiteHelper controller = new MySQLiteHelper(this, null);

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
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //get access to writable database
        SQLiteDatabase db = controller.getWritableDatabase();
        //Query for items
        Cursor event_cursor = db.rawQuery("SELECT  * FROM "+EventEntry.TABLE_NAME, null);

        // If events exist in SQLite DB
        if (event_cursor.getColumnCount() != 0) {
            String[][] myDataset = controller.getAllEventStrings();
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
                    bundle.putString("list_title", EventEntry.TABLE_NAME);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

    }


}
