package com.example.jessica.myuci;

/**
 * Created by xi on 2016/3/11.
 */
public class KrumbsImageItem {
    private String imageLink;
    private double lat;
    private double lng;
    private String mood;
    private double score;
    public KrumbsImageItem(String link, double lat, double lng, String mood, double score){
        this.imageLink = link;
        this.lat = lat;
        this.lng = lng;
        this.mood = mood;
        this.score = score;
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
    public double getScore() {
        return score;
    }
}
