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
import com.example.jessica.myuci.FeedReaderContract.UserInfo;

public class EventViewActivity extends BaseActivity {
    private ProgressDialog prgDialog;
    private MySQLiteHelper controller = new MySQLiteHelper(this, null);
    private String[] event_info;

    private Button WLButton;

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

        //user_id = getSharedPreferences("loginPrefs", MODE_PRIVATE).getString("curUser", "");

        //get event items
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Getting full event data. Please wait...");
        prgDialog.setCancelable(false);
        prgDialog.show();


        new Thread(){
            public void run() {
                //do long operations like image gathering here

                ///

                createEventView();
            }
        }.start();



    }

    private void createEventView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
                WLButton = (Button) findViewById(R.id.event_watch_later_button);

                //set textViews
                title.setText(event_info[1]);
                
                if (event_info[2] == null) { //host does not need to be there
                    hoster.setVisibility(View.GONE);
                } else {
                    hoster.setText(event_info[2]);
                }
                start_time.setText(event_info[3]);
                if (event_info[4] == null) { //end time does not need to be there
                    end_time.setVisibility(View.GONE);
                } else {
                    end_time.setText(event_info[4]);
                }
                location.setText(event_info[7]);
                if (event_info[8] != null) {
                    description.setText(event_info[8]);
                }
                if (event_info[9] == null) { //link does not need to be there
                    link.setVisibility(View.GONE);
                } else {
                    link.setText(event_info[9]);
                }
                if (event_info[10] == null) { //image_link empty
                    //make sure you do something here
                } else {
                    image_link.setText(event_info[10]);
                }

                //button
                if (controller.hasWLItem(UserInfo.USER_ID, event_info[0])) {
                    WLButton.setText(getString(R.string.delete_from_watch_later));
                } else {
                    WLButton.setText(getString(R.string.add_to_watch_later));
                }


                WLButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (WLButton.getText().equals("Add to Watch Later")) {
                            controller.addWatchLaterItem(UserInfo.USER_ID, event_info[0]);
                            //toggle view
                            WLButton.setText(getString(R.string.delete_from_watch_later));

                        } else {
                            controller.deleteWatchLaterItem(UserInfo.USER_ID, event_info[0]);
                            WLButton.setText(getString(R.string.add_to_watch_later));
                        }
                    }
                });

                Log.d("MSG: ", "EventViewActivity Completed");
                try{
                    prgDialog.hide();
                }catch(Exception ex){
                    Log.i("---", "Exception in thread");
                }
            }

        });
    }


}
