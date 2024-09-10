package com.omotion.contentsx.android.licon.core.contents;

import java.io.File;

public class DownloadItem {
    String downloadUrl;
    File file;

    public DownloadItem(String downloadUrl, File file) {
        this.downloadUrl = downloadUrl;
        this.file = file;
    }
}


