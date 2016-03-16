package com.example.jessica.myuci;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by xi on 2016/3/11.
 */
public class RetrieveImageLinkTask extends AsyncTask<Void, Void, Void> {

    private static long timeRange = 36000000;  //10 hour
    Context context;

    private static final int happyScore = 3;
    private static final int upsetScore = -5;
    private static final int loveScore = 5;
    private static final int takingNotesScore = 2;
    private static final int neutralScore = 0;
    public RetrieveImageLinkTask(Context context){
        this.context = context;
    }
    @Override
    protected Void doInBackground(Void ... params){
        Long curTimeStamp = System.currentTimeMillis();
        String start_time = Long.toString(curTimeStamp - timeRange);
        String end_time = Long.toString(curTimeStamp + timeRange);
        List images = null;
        try{
            URL url = new URL("http://sln.ics.uci.edu:8085/eventshoplinux/rest/sttwebservice/search/70/box/null/null/" + start_time + "/" + end_time);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                Log.d("Krumbs", "open connection success");
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                images = readJsonStream(in);
            } catch(IOException e){}
            finally{
                urlConnection.disconnect();
            }
        } catch (IOException e){}

        MySQLiteHelper controller = new MySQLiteHelper(context, null);
        SQLiteDatabase db = controller.getWritableDatabase();
        db.delete(FeedReaderContract.KrumbsImagesEntry.TABLE_NAME, null, null);
        //create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        if(images != null) {
            for(int i = 0;  i < images.size(); i ++) {
                KrumbsImageItem image = (KrumbsImageItem)images.get(i);
                values.put(FeedReaderContract.KrumbsImagesEntry.COLUMN_NAME_IMAGELINK, image.getImageLink());
                values.put(FeedReaderContract.KrumbsImagesEntry.COLUMN_NAME_LAT, image.getLat());
                values.put(FeedReaderContract.KrumbsImagesEntry.COLUMN_NAME_LNG, image.getLng());
                values.put(FeedReaderContract.KrumbsImagesEntry.COLUMN_NAME_MOOD, image.getMood());
                values.put(FeedReaderContract.KrumbsImagesEntry.COLUMN_NAME_SCORE, image.getScore());
                try {
                    db.insert(FeedReaderContract.KrumbsImagesEntry.TABLE_NAME,
                            null, //nullColumnHack
                            values );
                    Log.d("Krumbs", "add image to db, link: " + image.getImageLink());
                } catch (Exception e) {
                    Log.d("Krumbs", "failed to add image");
                }
            }
        }
        db.close();
        return null;
    }

    private List readJsonStream(InputStream in) throws IOException {
        Log.d("Krumbs", "readJsonStream");
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessagesArray(reader);
        } finally {
            reader.close();
        }
    }
    private List readMessagesArray(JsonReader reader) throws IOException {
        Log.d("Krumbs", "readMessageArray");
        List messages = new ArrayList();
        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readKrumbsImageItem(reader));
        }
        reader.endArray();
        return messages;
    }
    private KrumbsImageItem readKrumbsImageItem(JsonReader reader) throws IOException {
        Log.d("Krumbs", "read krumbsImageItem");
        String link = null;
        Double lat = null;
        Double lng = null;
        String mood = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("stt_where")) {
                List<Double> loc = readPoint(reader);
                lat = loc.get(0);
                lng = loc.get(1);
            } else if (name.equals("stt_what")) {
                List<String> tmp = readImageLink(reader);
                link = tmp.get(0);
                mood = tmp.get(1);
                Log.d("Krumbs-mood", "mood: " + mood);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        double score = 0;
        if (mood.equals("Happy")) {
            score = happyScore;
        } else if (mood.equals("Upset")) {
            score = upsetScore;
        } else if (mood.equals("Love")) {
            score = loveScore;
        } else if (mood.equals("Taking notes")) {
            score = takingNotesScore;
        } else if (mood.equals("Neutral")) {
            score = neutralScore;
        } else {
            Log.d("Krumbs", "found no matching mood");
        }
        if(link != null)
            return new KrumbsImageItem(link, lat, lng, mood, score);
        return null;
    }
    private List readPoint(JsonReader reader) throws IOException {
        Log.d("Krumbs", "readPoint");
        List latlng = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if(name.equals("point")) {
                latlng = readLatLng(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return latlng;
    }
    private List readLatLng(JsonReader reader) throws  IOException{
        Log.d("Krumbs", "readLatLng");
        List doubles = new ArrayList();
        reader.beginArray();
        while(reader.hasNext()) {
            doubles.add(reader.nextDouble());
        }
        reader.endArray();
        return doubles;
    }
    private List readImageLink(JsonReader reader) throws  IOException {
        Log.d("Krumbs", "readImageLink and mood");
        List linkNmood = new ArrayList();
        String link = null;
        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if(name.equals("media_source_photo")) {
                reader.beginObject();
                reader.nextName();
                link = reader.nextString();
                linkNmood.add(link);
                reader.endObject();
            } else if(name.equals("intent_name")) {
                reader.beginObject();
                reader.nextName();
                String mood = reader.nextString();
                linkNmood.add(mood);
                Log.d("Krumbs", "mood!!!!!!!!!!!!! " + mood);
                reader.endObject();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return linkNmood;
    }
}