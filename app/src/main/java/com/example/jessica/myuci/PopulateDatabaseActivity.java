package com.example.jessica.myuci;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.Date;

public class PopulateDatabaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_populate_database);
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
        Button button = (Button) findViewById(R.id.button5);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                addEvents(v.getContext());
            }
        });

        Button button_delete = (Button) findViewById(R.id.button6);
        button_delete.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                deleteEvents(v.getContext());
            }
        });



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

    public void addEvents(Context context){
        MySQLiteHelper controller = new MySQLiteHelper(context, null);

        // add EventItems
        controller.addEventItem(new EventItem(0, "Some Title", "Some Host", new Date(), new Date(), 12.3456, 14.5678,
                "DBH 100", "SOME REALLY LONG DESCRIPTION", "None", "None"));
        controller.addEventItem(new EventItem(1, "Second Title", "Some Host", new Date(), new Date(), 12.3456, 14.5678,
                "DBH 100", "SOME REALLY LONG DESCRIPTION", "http://www.arts.uci.edu/event/new-expressions-women-music-technology", "http://www.accounting.uci.edu/img/anteater-right-facing.jpg"));
        controller.addEventItem(new EventItem(2, "Thrid Title", "None", new Date(), new Date(), 12.3456, 14.5678,
                "DBH 100", "SOME REALLY LONG DESCRIPTION", "http://www.arts.uci.edu/event/uci-small-jazz-group-concert-3", "None"));
        controller.addEventItem(new EventItem(3, "Fourth Title", "None", new Date(), new Date(), 12.3456, 14.5678,
                "DBH 100", "SOME REALLY LONG DESCRIPTION", "None", "db.addEventItem(new EventItem(1, \"Thrid Title\", \"None\", new Date(), new Date(), 12.3456, 14.5678,\n" +
                "                \"DBH 100\", \"SOME REALLY LONG DESCRIPTION\", \"http://www.arts.uci.edu/event/uci-small-jazz-group-concert-3\", \"None\"));"));
        controller.close();
    }

    public void deleteEvents(Context context){
        MySQLiteHelper controller = new MySQLiteHelper(context, null);
        SQLiteDatabase db = controller.getWritableDatabase();
        controller.deleteAllEvents(db);
        controller.close();
    }
}
