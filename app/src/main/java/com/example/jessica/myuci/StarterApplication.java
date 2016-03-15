/*
 * Copyright (c) 2016 Krumbs Inc.
 * All rights reserved.
 *
 */
package com.example.jessica.myuci;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.util.Log;

import com.firebase.client.Firebase;

import java.net.URL;

import io.krumbs.sdk.KIntentPanelConfiguration;
import io.krumbs.sdk.KrumbsSDK;
import io.krumbs.sdk.KrumbsUser;
import io.krumbs.sdk.data.model.Media;
import io.krumbs.sdk.krumbscapture.KMediaUploadListener;

public class StarterApplication extends Application {
    public static final String KRUMBS_SDK_APPLICATION_ID = "io.krumbs.sdk.APPLICATION_ID";
    public static final String KRUMBS_SDK_CLIENT_KEY = "io.krumbs.sdk.CLIENT_KEY";
    public static final String SDK_STARTER_PROJECT_USER_FN = "Jessica";
    public static final String SDK_STARTER_PROJECT_USER_SN = "Public";

    @Override
    public void onCreate() {
        Log.d("Krumbs: ", "Initializing Starter Application");
        super.onCreate();
        Log.d("login: ", "set firebase context");
        Firebase.setAndroidContext(this);
        String appID = getMetadata(getApplicationContext(), KRUMBS_SDK_APPLICATION_ID);
        String clientKey = getMetadata(getApplicationContext(), KRUMBS_SDK_CLIENT_KEY);
        Log.d("Krumbs: ", "retrieving appID = " + appID);
        Log.d("Krumbs: ", "retrieving clientID = " + clientKey);
        if (appID != null && clientKey != null) {
// SDK usage step 1 - initialize the SDK with your application id and client key
            KrumbsSDK.initialize(getApplicationContext(), appID, clientKey);

// Implement the interface KMediaUploadListener.
// After a Capture completes, the media (photo and audio) is uploaded to the cloud
// KMediaUploadListener will be used to listen for various state of media upload from the SDK.
            Log.d("Krumbs: ", "Finished initialize and about to configure upload listener");
            KMediaUploadListener kMediaUploadListener = new KMediaUploadListener() {
                // onMediaUpload listens to various status of media upload to the cloud.
                @Override
                public void onMediaUpload(String id, KMediaUploadListener.MediaUploadStatus mediaUploadStatus,
                                          Media.MediaType mediaType, URL mediaUrl) {
                    if (mediaUploadStatus != null) {
                        Log.i("KRUMBS-BROADCAST-RECV", mediaUploadStatus.toString());
                        if (mediaUploadStatus == KMediaUploadListener.MediaUploadStatus.UPLOAD_SUCCESS) {
                            Log.d("Krumbs:", "Krumbs media Upload Status is a success");
                            if (mediaType != null && mediaUrl != null) {
                                Log.i("KRUMBS-BROADCAST-RECV", mediaType + ": " + id + ", " + mediaUrl);
                                Log.d("Krumbs", "KRUMBS-BROADCAST-RECV" + mediaType + ": " + id + ", " + mediaUrl);
                            }
                        }
                    }
                }
            };
            // pass the KMediaUploadListener object to the sdk
            KrumbsSDK.setKMediaUploadListener(this, kMediaUploadListener);

            try {

// SDK usage step 2 - register your customized Intent Panel with the SDK

                // Register the Intent Panel model
                // the emoji image assets will be looked up by name when the KCapture camera is started
                // Make sure to include the images in your resource directory before starting the KCapture
                // Use the 'asset-generator' tool to build your image resources from intent-categories.json
                Log.d("Krumbs", "About to register intent categories");
                String assetPath = "IntentResourcesExample";
                KrumbsSDK.registerIntentCategories(assetPath);
                Log.d("Krumbs", "got to end of create for starter application");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getMetadata(Context context, String name) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                return appInfo.metaData.getString(name);
            }
        } catch (PackageManager.NameNotFoundException e) {
// if we can’t find it in the manifest, just return null
        }
        return null;
    }
}
