package com.example.jessica.myuci;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class EventViewActivity extends AppCompatActivity {
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
        String[] event_info = extras.getStringArray("event_info");
        //set textViews
        TextView title = (TextView) findViewById(R.id.event_title);
        TextView hoster = (TextView) findViewById(R.id.event_hoster);
        TextView time_span = (TextView) findViewById(R.id.event_time_span);
        TextView location = (TextView) findViewById(R.id.event_location);
        TextView description = (TextView) findViewById(R.id.event_description);
        TextView link = (TextView) findViewById(R.id.event_link);
        title.setText(event_info[1]);
        if(event_info[2] == "None") { //host does not need to be there
            hoster.setVisibility(View.GONE);
        } else{
            hoster.setText(event_info[2]);
        }
        time_span.setText(event_info[3] + " - " + event_info[4]);
        location.setText(event_info[7]);
        description.setText(event_info[8]);
        if(event_info[9] == "None") { //host does not need to be there
            link.setVisibility(View.GONE);
        } else{
            link.setText(event_info[9]);
        }

        Log.d("MSG: ", "EventViewActivity Completed");
    }

}
