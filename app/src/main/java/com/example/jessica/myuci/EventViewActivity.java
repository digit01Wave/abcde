package com.example.jessica.myuci;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.jessica.myuci.FeedReaderContract.WLEntry;

public class EventViewActivity extends AppCompatActivity {
    private ProgressDialog prgDialog;
    private MySQLiteHelper controller = new MySQLiteHelper(this, null);
    private String[] event_info;

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
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Getting full event data. Please wait...");
        prgDialog.setCancelable(false);
        if (!prgDialog.isShowing()) {
            prgDialog.show();
        }


        new Thread(){
            public void run(){
                try{
                    runOnUiThread(new Runnable(){
                       @Override
                       public void run(){
                           createEventView();
                           prgDialog.dismiss();
                       }
                    });

                }catch(Exception e){
                    Log.e("---", e.getMessage());
                }
            }
        }.start();


    }

    private void createEventView() {
        Bundle extras = getIntent().getExtras();
        event_info = extras.getStringArray("event_info");
        TextView image_link = (TextView) findViewById(R.id.image);
        TextView title = (TextView) findViewById(R.id.event_title);
        TextView hoster = (TextView) findViewById(R.id.event_hoster);
        TextView start_time = (TextView) findViewById(R.id.event_start_time);
        TextView end_time = (TextView) findViewById(R.id.event_end_time);
        TextView location = (TextView) findViewById(R.id.event_location);
        TextView description = (TextView) findViewById(R.id.event_description);
        TextView link = (TextView) findViewById(R.id.event_link);
        Button watch_later = (Button) findViewById(R.id.event_watch_later_button);

        Log.d("MSG: ", "Creating Event View");
        if(extras.getString("list_title").equals(FeedReaderContract.WLEntry.TABLE_NAME)){
            watch_later.setVisibility(View.GONE);
            Log.d("MSG: ", "Set Watch Later Visibility to Gone");
        }

        //set textViews
        title.setText(event_info[1]);
        if (event_info[2].equals("None")) { //host does not need to be there
            hoster.setVisibility(View.GONE);
        } else {
            hoster.setText(event_info[2]);
        }
        start_time.setText(event_info[3]);
        end_time.setText(event_info[4]);
        location.setText(event_info[7]);
        description.setText(event_info[8]);
        if (event_info[9].equals("None")) { //link does not need to be there
            link.setVisibility(View.GONE);
        } else {
            link.setText(event_info[9]);
        }
        if (event_info[10].equals("None")) { //image_link empty
            image_link.setText(event_info[10]);
        } else {
            image_link.setText(event_info[10]);
        }


        Log.d("MSG: ", "EventViewActivity Completed");


    }

    public void addToWatchLater(View v){
        //int event_id = Integer.parseInt(event_info[0]);
        controller.addWatchLaterItem(WLEntry.GET_ID, event_info[0]);
    }

}
