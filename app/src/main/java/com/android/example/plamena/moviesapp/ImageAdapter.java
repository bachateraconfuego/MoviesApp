package com.android.example.plamena.moviesapp;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by plamenapetrova on 12/28/15.
 *
 * NOTE: Modeled after http://developer.android.com/guide/topics/ui/layout/gridview.html
 */
public class ImageAdapter extends ArrayAdapter<MovieData> {

    public static final String LOG_TAG = ImageAdapter.class.getSimpleName();

    private Context mContext;
    private final String BASE_URL = "http://image.tmdb.org/t/p/";
    private final String SIZE = "w185";

    public ImageAdapter(Activity context, List<MovieData> movies) {
        super(context, 0, movies);
        mContext = context;
    }

    // Create a new View for each image
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if(convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }

        String posterPath = BASE_URL + SIZE + getItem(position).getPosterPath();

        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();

        Picasso.with(mContext)
                .load(posterPath)
                .resize(dm.widthPixels / 2, parent.getHeight() / 2)
                .centerCrop()
                .into(imageView);

        return imageView;
    }
}
