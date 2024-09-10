package com.omotion.contentsx.android.licon.data.remote.model;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

public class Song implements Serializable {
    public String singer;
    public String album;
    public String title;
    public int duration;
    public String image;
    public int imageResId;
    public String file;
    public Uri fileUri;
    public ArrayList<Lyric> lyrics;

    @Override
    public String toString() {
        return "Song{" +
                "singer='" + singer + '\'' +
                ", album='" + album + '\'' +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", image='" + image + '\'' +
                ", imageResId=" + imageResId +
                ", file='" + file + '\'' +
                ", fileUri=" + fileUri +
                ", lyrics=" + lyrics +
                '}';
    }

    public class Lyric implements Serializable{
        public String lyrics_;

        @Override
        public String toString() {
            return "Lyric{" +
                    "lyrics_='" + lyrics_ + '\'' +
                    '}';
        }
    }
}


