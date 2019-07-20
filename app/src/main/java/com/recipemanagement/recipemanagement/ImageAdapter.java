package com.recipemanagement.recipemanagement;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ImageAdapter extends ArrayAdapter {

    private int resource;
    private LayoutInflater layoutInflater;
    private List<Uri> imageList;

    public ImageAdapter(Context context, int resource, List<Uri> objects) {
        super(context, resource, objects);
        imageList = objects;
        this.resource = resource;
        layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(resource, null);
        }
        ImageView im;

        im = (ImageView)convertView.findViewById(R.id.imageViewPhotos);
        im.setImageURI(imageList.get(position));
        im.setScaleType(ImageView.ScaleType.FIT_XY);


        return convertView;
    }
}
