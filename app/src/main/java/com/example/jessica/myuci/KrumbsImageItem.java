package com.example.jessica.myuci;

/**
 * Created by xi on 2016/3/11.
 */
public class KrumbsImageItem {
    private String imageLink;
    private double lat;
    private double lng;
    private String mood;
    public KrumbsImageItem(String link, double lat, double lng, String mood){
        this.imageLink = link;
        this.lat = lat;
        this.lng = lng;
        this.mood = mood;
    }
    public String getImageLink() {
        return imageLink;
    }
    public double getLat() {
        return lat;
    }
    public double getLng() {
        return lng;
    }
    public String getMood() {
        return mood;
    }
}
