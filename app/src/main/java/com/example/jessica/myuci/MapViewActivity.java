package com.example.jessica.myuci;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;

public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnInfoWindowCloseListener,
        GoogleMap.OnInfoWindowLongClickListener{

    private GoogleMap mMap;
    private Marker[] markers;
    private Integer clickedMarkerId;
    String[][] allEvents;

    MySQLiteHelper controller = new MySQLiteHelper(this, null);

    public static final CameraPosition uci =
            new CameraPosition.Builder().target(new LatLng(33.645898, -117.842703))
                    .zoom(16.0f)
                    .bearing(0)
                    .tilt(0)
                    .build();

    private TextView image_link;
    private TextView title;
    private TextView hoster;
    private TextView start_time;
    private TextView end_time;
    private TextView location;
    private TextView description;
    private TextView link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("map", "onCreate map");
        setContentView(R.layout.activity_map_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Bundle b = this.getIntent().getExtras();
        if(b.containsKey("event_info")){ //if just a single event
            allEvents = new String[1][];
            allEvents[0] = b.getStringArray("event_info");
        }else {
            allEvents = controller.getAllEventStringsWhere(b.getString("where_clause"));
        }
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("map", "onMapReady");
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(uci));
        if(allEvents.length == 1){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(allEvents[0][5]), Double.parseDouble(allEvents[0][6]))));
        }

        Log.d("map", "loadedEvents");

        //UI settings controls
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        markers = new Marker[allEvents.length];
        for(int i = 0; i < allEvents.length; i ++) {
            //make sure there is a lat long to see
            if(!(allEvents[i][5] == null)){
                markers[i] = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(allEvents[i][5]), Double.parseDouble(allEvents[i][6]))).
                        title(allEvents[i][1]));

            }
        }

        Log.d("map", "add markers");
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getLayoutInflater()));
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowCloseListener(this);
        googleMap.setContentDescription("Map with lots of markers");
        Log.d("map", "on map ready");
    }

    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        LayoutInflater inflater = null;
        public CustomInfoWindowAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
            Log.d("map", "customInfoWindowAdapter");
        }

        @Override
        public View getInfoWindow(Marker marker) {
            Log.d("map", "getInfoWindow");
            View v = inflater.inflate(R.layout.infowindow_event_view, null);

            image_link = (TextView) v.findViewById(R.id.image);
            title = (TextView) v.findViewById(R.id.event_title);
            hoster = (TextView) v.findViewById(R.id.event_hoster);
            start_time = (TextView) v.findViewById(R.id.event_start_time);
            end_time = (TextView) v.findViewById(R.id.event_end_time);
            location = (TextView) v.findViewById(R.id.event_location);
            description = (TextView) v.findViewById(R.id.event_description);
            link = (TextView) v.findViewById(R.id.event_link);

            if(allEvents[clickedMarkerId][1] != null) {
                title.setText(new SpannableString(allEvents[clickedMarkerId][1]));
            }
            if(allEvents[clickedMarkerId][2] != null) {
                hoster.setText(new SpannableString(allEvents[clickedMarkerId][2]));
            }
            if(allEvents[clickedMarkerId][7] != null) {
                location.setText(new SpannableString(allEvents[clickedMarkerId][7]));
            }
            if(allEvents[clickedMarkerId][9] != null) {
                link.setText(new SpannableString(allEvents[clickedMarkerId][9]));
            }
            if(allEvents[clickedMarkerId][8] != null) {
                description.setText(new SpannableString(allEvents[clickedMarkerId][8]));
            }
            if(allEvents[clickedMarkerId][3] != null) {
                start_time.setText(new SpannableString(allEvents[clickedMarkerId][3]));
            }
            if(allEvents[clickedMarkerId][4] != null) {
                end_time.setText(new SpannableString(allEvents[clickedMarkerId][4]));
            }
            Log.d("map", "render marker info window ");
            return v;
        }

        @Override
        public View getInfoContents(Marker marker) {
            Log.d("map", "getInfoContents");
            return null;
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        for(int i = 0; i < markers.length; i ++) {
            if (marker.equals(markers[i])) {
                Log.d("map", "detected clicked marker");
                final Handler handler = new Handler();
                final long start = SystemClock.uptimeMillis();
                final long duration = 1500;

                final Interpolator interpolator = new BounceInterpolator();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        long elapsed = SystemClock.uptimeMillis() - start;
                        float t = Math.max(1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                        marker.setAnchor(0.5f, 1.0f + 2 * t);

                        if (t > 0.0) {
                            // Post again 16ms later.
                            handler.postDelayed(this, 16);
                        }
                    }
                });
                clickedMarkerId = i;
                break;
            }
        }
        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        marker.hideInfoWindow();
        //Toast.makeText(this, "Click Info Window", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onInfoWindowClose(Marker marker) {
        //Toast.makeText(this, "Close Info Window", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onInfoWindowLongClick(Marker marker) {
        //Toast.makeText(this, "Long Click Info Window", Toast.LENGTH_SHORT).show();
    }
}
