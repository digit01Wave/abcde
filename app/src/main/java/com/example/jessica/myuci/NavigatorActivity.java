package com.example.jessica.myuci;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.Map;

import io.krumbs.sdk.KrumbsSDK;
import io.krumbs.sdk.krumbscapture.KCaptureCompleteListener;

public class NavigatorActivity extends BaseActivity {
    private View cameraView;
    private View startCaptureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator);
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

        //KrumbsSDK.initialize(getApplicationContext(), "zmmzAIzwM65XahnKb1lmD1ij7z4J2bToqRRIuGDH", "bJI7wx5HafWH9x6icjJ9RtMQEZf4XxpoOtX6TJwm");

    }

    public void goLoginActivity(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void goPopulateDatabaseActivity(View view){
        Intent intent = new Intent(this, PopulateDatabaseActivity.class);
        startActivity(intent);
    }

    public void goEventListActivity(View view){
        Intent intent = new Intent(this, EventListActivity.class);
        startActivity(intent);
    }

    public void goTestActivity(View view){
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
    }

    public void goKrumbsActivity(View view){
        Intent intent = new Intent(this, KrumbsActivity.class);
        startActivity(intent);
    }

    public void goWatchLaterListActivity(View view){
        Intent intent = new Intent(this, WatchLaterListActivity.class);
        startActivity(intent);
    }




}
