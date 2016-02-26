package com.example.jessica.myuci;

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

public class KrumbsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_krumbs);
        Log.d("MSG: ", "Inside Krumbs Create");
        StarterApplication initial = new StarterApplication();
        Log.d("MSG: ", "Outside Start Application and beginning capture");
        startCapture();
    }

    private void startCapture() {
        int containerId = R.id.camera_container;
// SDK usage step 4 - Start the K-Capture component and add a listener to handle returned images and URLs
        KrumbsSDK.startCapture(containerId, this, new KCaptureCompleteListener() {
            @Override
            public void captureCompleted(CompletionState completionState, boolean audioCaptured, Map<String, Object> map) {
                // DEBUG LOG
                if (completionState != null) {
                    Log.d("KRUMBS-CALLBACK", completionState.toString());
                }
                if (completionState == CompletionState.CAPTURE_SUCCESS) {
// The local image url for your capture
                    String imagePath = (String) map.get(KCaptureCompleteListener.CAPTURE_MEDIA_IMAGE_PATH);
                    if (audioCaptured) {
// The local audio url for your capture (if user decided to record audio)
                        String audioPath = (String) map.get(KCaptureCompleteListener.CAPTURE_MEDIA_AUDIO_PATH);
                    }
// The mediaJSON url for your capture
                    String mediaJSONUrl = (String) map.get(KCaptureCompleteListener.CAPTURE_MEDIA_JSON_URL);
                    Log.i("KRUMBS-CALLBACK", mediaJSONUrl + ", " + imagePath);
                    finish();
                } else if (completionState == CompletionState.CAPTURE_CANCELLED ||
                        completionState == CompletionState.SDK_NOT_INITIALIZED) {
                    finish();
                }
            }
        });
    }

}
