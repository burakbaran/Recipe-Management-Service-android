package com.recipemanagement.recipemanagement;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ImageBitmapAdapter  extends ArrayAdapter {

    private int resource;
    private LayoutInflater layoutInflater;
    private List<Bitmap> imageList;

    public ImageBitmapAdapter(Context context, int resource, List<Bitmap> objects) {
        super(context, resource, objects);
        System.out.println("BURAYA GİRİYO MUééééééééééééééééééééééééééééééééééééééééééééééééééééééééééééééé");
        for (int i = 0; i < objects.size(); i++){
            System.out.println(objects.get(i) + " ONJECTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
        }
        imageList = objects;
        this.resource = resource;
        layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            System.out.println("LAN BURA MI Aqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
            convertView = layoutInflater.inflate(resource, null);
        }
        ImageView im;
        im = (ImageView) convertView.findViewById(R.id.imageViewPhotos);
        System.out.println("POSITION:          " + imageList.get(position));

        im.setImageBitmap(imageList.get(position));
        im.setScaleType(ImageView.ScaleType.FIT_XY);


        return convertView;
    }
}
