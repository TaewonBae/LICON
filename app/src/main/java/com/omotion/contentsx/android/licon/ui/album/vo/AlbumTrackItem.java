package com.omotion.contentsx.android.licon.ui.album.vo;

public class AlbumTrackItem {
    private int number;
    private String str_title;
    private String str_content;
    private String str_artist;
    private Boolean bool_title;

    public AlbumTrackItem(int number_, String str_title_, String str_content_, String str_artist_, Boolean bool_title_) {
        number = number_;
        str_title = str_title_;
        str_content = str_content_;
        str_artist = str_artist_;
        bool_title = bool_title_;
    }

    public int getNumber() {
        return number;
    }

    public String getTitle() {
        return str_title;
    }

    public String getArtist() {
        return str_artist;
    }

    public Boolean getTitleCheck() {
        return bool_title;
    }

    public String getContent() {
        return str_content;
    }
}