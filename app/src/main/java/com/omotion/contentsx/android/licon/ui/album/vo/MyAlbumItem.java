package com.omotion.contentsx.android.licon.ui.album.vo;

public class MyAlbumItem {
    private int imageId;
    private String title;
    private String artist;
    private boolean hasRegisted;

    public MyAlbumItem(int imageId_, String title_, String artist_, boolean hasRegisted_) {
        imageId = imageId_;
        title = title_;
        artist = artist_;
        hasRegisted = hasRegisted_;
    }

    public int getImage() {
        return imageId;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public boolean hasRegisted() {
        return hasRegisted;
    }

    public void setHasRegisted(boolean hasRegisted) {
        this.hasRegisted = hasRegisted;
    }
}