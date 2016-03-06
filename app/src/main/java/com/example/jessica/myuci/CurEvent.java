package com.example.jessica.myuci;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by xi on 2016/3/6.
 */
public class CurEvent {
    public static EventItem[] events;
    public static void loadEvents(){
        //!! fake events for testing
        //to do: load current events from server
        LatLng[] latLngs = new LatLng[3];
        String[] locations = new String[3];
        latLngs[0] = new LatLng(33.648992, -117.842166); //student center
        locations[0] = "Student Center";
        latLngs[1] = new LatLng(33.647253, -117.840900); //langson lib
        locations[1] = "Langson Library";
        latLngs[2] = new LatLng(33.643399, -117.842070);
        locations[2] = "ICS Building";
        events = new EventItem[3];
        events[0] = new EventItem();
        events[1] = new EventItem();
        events[2] = new EventItem();
        events[0].setLink("www.ics.uci.edu");
        events[0].setLatLon(latLngs[0].latitude, latLngs[0].longitude);
        events[0].setLocation(locations[0]);
        events[1].setLatLon(latLngs[1].latitude, latLngs[1].longitude);
        events[1].setLocation(locations[1]);
        events[2].setLatLon(latLngs[2].latitude, latLngs[2].longitude);
        events[2].setLocation(locations[2]);
    }
}
