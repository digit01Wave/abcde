package com.example.jessica.myuci;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.jessica.myuci.FeedReaderContract.UserInfo;
import com.example.jessica.myuci.FeedReaderContract.WLEntry;
import com.example.jessica.myuci.FeedReaderContract.CalendarEntry;
import com.example.jessica.myuci.FeedReaderContract.ServerEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class EventViewActivity extends BaseActivity {
    private ProgressDialog prgDialog;
    private MySQLiteHelper controller = new MySQLiteHelper(this, null);

    private String[] event_info;
    private ArrayList<String> krumbsLinkArray = new ArrayList<String>();


    private ImageView image_link;
    private TextView title;
    private TextView hoster;
    private TextView start_time;
    private TextView end_time;
    private TextView location;
    private TextView description;
    private TextView link;
    private Bitmap img;

    private TextView krumbs_image_link;
    private Button WLButton;
    private Button CalendarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //bind views
        image_link = (ImageView) findViewById(R.id.image);
        title = (TextView) findViewById(R.id.event_title);
        hoster = (TextView) findViewById(R.id.event_hoster);
        start_time = (TextView) findViewById(R.id.event_start_time);
        end_time = (TextView) findViewById(R.id.event_end_time);
        location = (TextView) findViewById(R.id.event_location);
        description = (TextView) findViewById(R.id.event_description);
        link = (TextView) findViewById(R.id.event_link);
        WLButton = (Button) findViewById(R.id.event_watch_later_button);
        CalendarButton = (Button) findViewById(R.id.event_calendar_button);


        //get event items
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Getting full event data. Please wait...");
        prgDialog.setCancelable(false);
        prgDialog.show();


        new Thread(){
            public void run() {
                //do long operations like image gathering here
                Bundle extras = getIntent().getExtras();
                event_info = extras.getStringArray("event_info");
                if(event_info != null){
                    try {
                        img = new DownloadImageTask().execute(event_info[10]).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                ///

                createEventView();
            }
        }.start();



    }

    private void createEventView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //set textViews
                title.setText(event_info[1]);
                
                if (event_info[2] == null) { //host does not need to be there
                    hoster.setVisibility(View.GONE);
                } else {
                    hoster.setText(event_info[2]);
                }
                start_time.setText(event_info[3]); //start_time
                if (event_info[4] == null) { //end time does not need to be there
                    end_time.setVisibility(View.GONE);
                } else {
                    end_time.setText(event_info[4]);
                }
                location.setText(event_info[7]); //location
                if (event_info[8] != null) { //description does not have to be there
                    description.setText(event_info[8]);
                }
                if (event_info[9] == null) { //link does not need to be there
                    link.setVisibility(View.GONE);
                } else {
                    link.setText(event_info[9]);
                }
                if(event_info[10] != null && img != null) { //if image_link is not empty
                    image_link.setImageBitmap(img);
                }

                if(event_info[5] != null && event_info[6] != null){ // has lat lng info
                    List links = controller.getKrumbsImageNearMe(Double.parseDouble(event_info[5]), Double.parseDouble(event_info[6]));
                    for(int i = 0; i < links.size(); i ++){
                        Log.d("Krumbs:", "yes added link " + links.get(i).toString());
                        krumbsLinkArray.add(links.get(i).toString());
                    }
                }
                //Watch Later button
                if (controller.hasPersonalItem(FeedReaderContract.WLEntry.TABLE_NAME,UserInfo.USER_ID, event_info[0])) {
                    WLButton.setText(getString(R.string.delete_from_watch_later));
                } else {
                    WLButton.setText(getString(R.string.add_to_watch_later));
                }


                WLButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (WLButton.getText().equals(getString(R.string.add_to_watch_later))) {
                            controller.addPersonalListItem(WLEntry.TABLE_NAME, UserInfo.USER_ID, event_info[0], ServerEntry.UPDATE_STATUS_UNSYNCED);
                            //toggle view
                            WLButton.setText(getString(R.string.delete_from_watch_later));

                        } else {
                            controller.deletePersonalListItem(WLEntry.TABLE_NAME, UserInfo.USER_ID, event_info[0]);
                            WLButton.setText(getString(R.string.add_to_watch_later));
                        }
                    }
                });

                //Calendar button
                if (controller.hasPersonalItem(CalendarEntry.TABLE_NAME,UserInfo.USER_ID, event_info[0])) {
                    CalendarButton.setText(getString(R.string.delete_from_calendar));
                } else {
                    CalendarButton.setText(getString(R.string.add_to_calendar));
                }


                CalendarButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (CalendarButton.getText().equals(getString(R.string.add_to_calendar))) {
                            controller.addPersonalListItem(CalendarEntry.TABLE_NAME, UserInfo.USER_ID, event_info[0], ServerEntry.UPDATE_STATUS_UNSYNCED);
                            //toggle view
                            CalendarButton.setText(getString(R.string.delete_from_calendar));

                        } else {
                            controller.deletePersonalListItem(CalendarEntry.TABLE_NAME, UserInfo.USER_ID, event_info[0]);
                            CalendarButton.setText(getString(R.string.add_to_calendar));
                        }
                    }
                });
                Log.d("MSG: ", "EventViewActivity Completed");
                try{
                    prgDialog.dismiss();
                }catch(Exception ex){
                    Log.i("---", "Exception in thread");
                }
            }

        });
    }

    public void goEventMapView(View view){
        Intent intent = new Intent(this, MapViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArray("event_info", event_info);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //go to Krumbs View
    public void goKrumbsView(View view){
        Intent intent = new Intent(this, KrumbsListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("dataset", krumbsLinkArray);
        intent.putExtras(bundle);
        startActivity(intent);
    }


}
