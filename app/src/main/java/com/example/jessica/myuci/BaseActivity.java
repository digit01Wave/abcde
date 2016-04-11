package com.example.jessica.myuci;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Jessica on 3/2/2016.
 */
public class BaseActivity extends AppCompatActivity {
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
        if(id == R.id.action_watch_later){
            Intent intent = new Intent(this, PersonalListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("table_name", FeedReaderContract.WLEntry.TABLE_NAME);
            intent.putExtras(bundle);
            startActivity(intent);
            return true;
        }
        if(id == R.id.action_calendar){
            Intent intent = new Intent(this, PersonalListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("table_name", FeedReaderContract.CalendarEntry.TABLE_NAME);
            intent.putExtras(bundle);
            startActivity(intent);
            return true;
        }
        if(id == R.id.action_upcoming_events){
            Intent intent = new Intent(this, EventListActivity.class);
            startActivity(intent);
            return true;
        }
        if(id == R.id.action_current_events){
            Intent intent = new Intent(this, CurrentEventsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setOnCreateOptions(Toolbar toolbar, int action_id){
        toolbar.inflateMenu(R.menu.menu_main);
        Menu menu = toolbar.getMenu();
        MenuItem menuItem = menu.findItem(R.id.action_upcoming_events);
        if(action_id == R.id.action_watch_later) {
            menuItem.setIcon(R.drawable.ic_action_watch_later_selected);
        }
        else if(action_id == R.id.action_calendar){
            menuItem.setIcon(R.drawable.ic_action_calendar_selected);
        }
        else if(action_id == R.id.action_upcoming_events){
            menuItem.setIcon(R.drawable.ic_action_upcoming_events_selected);
        }
        else if(action_id == R.id.action_current_events){
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
                    return true;
                }
                if(id == R.id.action_calendar){
                    Intent intent = new Intent(getApplicationContext(), PersonalListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("table_name", FeedReaderContract.CalendarEntry.TABLE_NAME);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    return true;
                }
                if(id == R.id.action_upcoming_events){
                    Intent intent = new Intent(getApplicationContext(), EventListActivity.class);
                    startActivity(intent);
                    return true;
                }
                if(id == R.id.action_current_events){
                    Intent intent = new Intent(getApplicationContext(), CurrentEventsActivity.class);
                    startActivity(intent);
                    return true;
                }
                return true;
            }

        });

    }
}
