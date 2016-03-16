package com.example.jessica.myuci;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Jessica on 3/15/2016.
 */
public class MyImageRecyclerAdapter extends RecyclerView.Adapter<MyImageRecyclerAdapter.ViewHolder> {
    private ArrayList<String> itemsData;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView krumbsImage;

        public ViewHolder(View view) {
            super(view);
            krumbsImage = (ImageView) view.findViewById(R.id.krumbs_image);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyImageRecyclerAdapter(ArrayList<String> myDataset) {
        itemsData = myDataset;
    }

    @Override
    public MyImageRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_image_list, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        try {
            Bitmap pic = new DownloadImageTask().execute(itemsData.get(position)).get();
            viewHolder.krumbsImage.setImageBitmap(pic);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return itemsData.size();
    }
}
