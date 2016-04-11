package com.example.jessica.myuci;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Jessica on 3/2/2016.
 * Creates reusable base layout with bottom toolbar
 * Schema from http://stackoverflow.com/questions/6362172/how-to-create-reusable-xml-wrappers-for-android-layout-files
 */
public class BaseActivity extends AppCompatActivity{
    RelativeLayout innerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //make sure already logged in
        if(FeedReaderContract.UserInfo.USER_ID == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);

        this.innerLayout = (RelativeLayout) this.findViewById(R.id.inner_layout);
    }

    @Override
    public void setContentView(int id) {
        LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(id, this.innerLayout);
    }

    // Options Menu (ActionBar Menu)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // When Options Menu is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        // When logout action button is clicked
        if (id == R.id.action_logout) {
            //logout and go back to login screen while clearing the activity stack
            Intent intent  = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            FeedReaderContract.UserInfo.USER_ID = null;
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setOnCreateOptions(Toolbar toolbar, int action_id){
        toolbar.inflateMenu(R.menu.menu_my);
        Menu menu = toolbar.getMenu();
        if(action_id == R.id.action_watch_later) {
            MenuItem menuItem = menu.findItem(R.id.action_watch_later);
            menuItem.setIcon(R.drawable.ic_action_watch_later_selected);
        }
        else if(action_id == R.id.action_calendar){
            MenuItem menuItem = menu.findItem(R.id.action_calendar);
            menuItem.setIcon(R.drawable.ic_action_calendar_selected);
        }
        else if(action_id == R.id.action_upcoming_events){
            MenuItem menuItem = menu.findItem(R.id.action_upcoming_events);
            menuItem.setIcon(R.drawable.ic_action_upcoming_events_selected);
        }
        else if(action_id == R.id.action_current_events){
            MenuItem menuItem = menu.findItem(R.id.action_current_events);
            menuItem.setIcon(R.drawable.ic_action_current_events_selected);
        }

    }



    public void setClickListener(Toolbar toolbar, final int action_id){
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem){
                // Handle action bar item clicks here.
                int id = menuItem.getItemId();
                if(id == action_id){
                    //do nothing
                    return true;
                }
                if(id == R.id.action_watch_later){
                    Intent intent = new Intent(getApplicationContext(), PersonalListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("table_name", FeedReaderContract.WLEntry.TABLE_NAME);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if(id == R.id.action_calendar){
                    Intent intent = new Intent(getApplicationContext(), PersonalListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("table_name", FeedReaderContract.CalendarEntry.TABLE_NAME);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if(id == R.id.action_upcoming_events){
                    Intent intent = new Intent(getApplicationContext(), EventListActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if(id == R.id.action_current_events){
                    Intent intent = new Intent(getApplicationContext(), CurrentEventsActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return true;
            }

        });

    }
}
