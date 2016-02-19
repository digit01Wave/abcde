package com.example.jessica.myuci;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.text.ParseException;
import java.util.Date;

public class MyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
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

        MySQLiteHelper db = new MySQLiteHelper(this, null);

        // add EventItem
        try {
            db.addEventItem(new EventItem(0, "Some Title", "Some Host", new Date(), new Date(), 12.3456, 14.5678,
                    "DBH 100", "SOME REALLY LONG DESCRIPTION", "Some Link"));
            db.addEventItem(new EventItem(1, "Second Title", "Some Host", new Date(), new Date(), 12.3456, 14.5678,
                    "DBH 100", "SOME REALLY LONG DESCRIPTION", "Some Link"));
        }catch(Exception e){
            Log.d("FAILED OnCreate:", "Add failed. " + e.toString());
        }

        try{
            EventItem e = db.getEventItem(0);
        }
        catch (ParseException e){
            Log.d("FAILED onCreate:", "RETRIEVED FAILED" + e.toString());
        }

        try {
            // delete one EventItem
            db.deleteEventItem(0);
        }catch(Exception e){
            Log.d("FAILED OnCrate:", "Delete Failed" + e.toString());
        }
        db.close();


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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
