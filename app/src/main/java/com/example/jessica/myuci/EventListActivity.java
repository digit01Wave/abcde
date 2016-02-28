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
import android.view.View;
import com.example.jessica.myuci.FeedReaderContract.EventEntry;

import java.util.ArrayList;
import java.util.Date;


public class EventListActivity extends AppCompatActivity {
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


        String[][] myDataset = controller.getAllEventStrings();

        //if not empty
        if(myDataset[0].length != 0){
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
