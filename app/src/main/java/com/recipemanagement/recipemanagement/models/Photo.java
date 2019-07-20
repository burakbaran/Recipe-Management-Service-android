package com.recipemanagement.recipemanagement.models;

public class Photo {

    private String id;
    private String photoLink;
    private String cloudinaryId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public String getCloudinaryId() {
        return cloudinaryId;
    }

    public void setCloudinaryId(String cloudinaryId) {
        this.cloudinaryId = cloudinaryId;
    }
}
