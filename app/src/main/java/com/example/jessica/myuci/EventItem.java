package com.example.jessica.myuci;

import java.util.Date;

/**
 * Created by Jessica on 2/9/2016.
 */
public class EventItem {
    private int event_id;
    private String title;
    private String hoster;
    private Date start_time;
    private Date end_time;
    private double lat;
    private double lon;
    private String location;
    private String description;
    private String link;

    public EventItem(){


    }

    public EventItem(int event_id, String title, String hoster,
                     Date start_time, Date end_time, double lat, double lon,
                     String location, String description, String link){
        this.event_id = event_id;
        this.title = title;
        this.hoster = hoster;
        this.start_time = start_time;
        this.end_time = end_time;
        this.lat = lat;
        this.lon = lon;
        this.location = location;
        this.description = description;
        this.link = link;
    }


    public String toString(){
        return "EventItem [id=" + event_id + ", title=" + title + ", hoaster = " + hoster +
                ", start_time=" + start_time.toString() + ", end_time=" + end_time.toString() +
                ", lat=" + String.valueOf(lat) + ", lon=" + String.valueOf(lon) +
                ", location=" + location + ", description=" + description + ", link=" + link + "]";
    }


    public void setID(int id){
        this.event_id = id;
    }
    public int getID(){
        return this.event_id;
    }

    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return this.title;
    }

    public void setHoster(String hoster){
        this.hoster = hoster;
    }
    public String getHoster(){
        return this.hoster;
    }

    public void setStartTime(Date start_time){this.start_time = start_time;}
    public Date getStartTime(){ return this.start_time; }

    public void setEndTime(Date end_time){ this.end_time = end_time; }
    public Date getEndTime(){ return this.end_time; }

    public void setLatLon(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
    }
    public double getLat(){
        return this.lat;
    }
    public double getLon(){
        return this.lon;
    }

    public void setLocation(String loc){
        this.location = loc;
    }
    public String getLocation(){
        return this.location;
    }

    public void setDescription(String description){
        this.description = description;
    }
    public String getDescription(){
        return description;
    }

    public void setLink(String link){
        this.link = link;
    }
    public String getLink(){
        return this.link;
    }

}
