package com.omotion.contentsx.android.licon.ui.album.vo;

import android.net.Uri;

public class AlbumPhotoItem {
    private Uri imageUri;
    private int position;

    public AlbumPhotoItem(Uri imageUri_, int position_) {
        imageUri = imageUri_;
        position = position_;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public int getPosition() {
        return position;
    }
}