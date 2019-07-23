package com.recipemanagement.recipemanagement.models;


import android.graphics.Bitmap;
import android.media.Image;

import java.io.File;
import java.util.ArrayList;

public class RecipeModel {

    private String name;
    private String id;
    private String details;
    private ArrayList<String> tags;
    private ArrayList<Photo> photos;


    public Bitmap imageBitmap;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    public Bitmap getImage() {
        return imageBitmap;
    }

    public void setImage(Bitmap image) {
        this.imageBitmap = image;
    }




}
