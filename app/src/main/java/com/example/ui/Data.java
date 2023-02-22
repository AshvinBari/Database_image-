package com.example.ui;

public class Data {
private String imageURL,caption;


public Data(){

}
    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Data(String imageURL, String caption) {
        this.imageURL = imageURL;
        this.caption = caption;
    }
}
