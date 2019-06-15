package com.recipemanagement.recipemanagement.models;

import org.json.JSONObject;

import java.util.ArrayList;

public class RecipeModel {
    private String name;
    private String id;
    private String details;
    private ArrayList<String> tags;

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


}
