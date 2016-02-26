package com.example.jessica.myuci;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class EventViewActivity extends AppCompatActivity {
    ProgressDialog prgDialog;
    String[] event_info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get event items
        Bundle extras = getIntent().getExtras();
        event_info = extras.getStringArray("event_info");

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Getting full event data. Please wait...");
        prgDialog.setCancelable(false);

        createEventView();

    }

    private void createEventView() {
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                prgDialog.show();
                TextView image_link = (TextView) findViewById(R.id.image);
                TextView title = (TextView) findViewById(R.id.event_title);
                TextView hoster = (TextView) findViewById(R.id.event_hoster);
                TextView time_span = (TextView) findViewById(R.id.event_time_span);
                TextView location = (TextView) findViewById(R.id.event_location);
                TextView description = (TextView) findViewById(R.id.event_description);
                TextView link = (TextView) findViewById(R.id.event_link);
                //set textViews
                title.setText(event_info[1]);
                if(event_info[2] == "None") { //host does not need to be there
                    hoster.setVisibility(View.GONE);
                } else{
                    hoster.setText(event_info[2]);
                }
                time_span.setText(event_info[3] + " - " + event_info[4]);
                location.setText(event_info[7]);
                description.setText(event_info[8]);
                if(event_info[9] == "None") { //link does not need to be there
                    link.setVisibility(View.GONE);
                } else{
                    link.setText(event_info[9]);
                }
                if(event_info[10] == "None") { //image_link empty
                    image_link.setText(event_info[10]);
                } else{
                    image_link.setText(event_info[10]);
                }


                Log.d("MSG: ", "EventViewActivity Completed");
                try {
                    prgDialog.dismiss();
                } catch (final Exception ex) {
                    Log.i("---","Exception in thread");
                }
            }
        });
    }

}
