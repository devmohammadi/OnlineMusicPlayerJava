package com.fmohammadi.onlinemusicplayer;

public class SlidersItems {
    // change to string because fetch image url
    public String imageurl;

    public SlidersItems(String image) {
        this.imageurl = image;
    }

    public String getImageurl() {
        return imageurl;
    }
}
