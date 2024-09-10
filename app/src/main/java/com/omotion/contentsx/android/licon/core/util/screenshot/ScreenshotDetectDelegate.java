package com.omotion.contentsx.android.licon.core.util.screenshot;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import com.omotion.contentsx.android.licon.core.util.LLog;

public class ScreenshotDetectDelegate extends ContentObserver {
    protected String TAG = getClass().getSimpleName();
    private Context context;
    private ScreenshotDetectListener listener;

    public ScreenshotDetectDelegate(Handler handler, Context context, ScreenshotDetectListener listener) {
        super(handler);
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void onChange(boolean selfChange, @Nullable Uri uri) {
        super.onChange(selfChange, uri);
        if (uri != null) {
            LLog.d(TAG, "onChange", uri.toString());
            String path = getFilePathFromUri(uri);
            // 스크린 캡쳐가 감지되었음
            if (isScreenshotPath(path) && listener != null) {
                listener.onScreenCaptured(path);
            }
        }
    }

    public void startScreenshotDetection() {
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                this
        );
    }

    public void stopScreenshotDetection() {
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.unregisterContentObserver(this);
    }

    private boolean isScreenshotPath(String path) {
        String lowercasePath = path.toLowerCase();
        if (lowercasePath.isEmpty()) {
            return false;
        }
        String screenshotDirectory = getPublicScreenshotDirectoryName();
        LLog.d(TAG, "isScreenshotPath", "screenshotDirectory: " + screenshotDirectory);
        LLog.d(TAG, "isScreenshotPath", "lowercasePath: " + lowercasePath);
        return (screenshotDirectory != null && lowercasePath.contains(screenshotDirectory.toLowerCase())) ||
                lowercasePath.contains("screenshot");
    }

    private String getPublicScreenshotDirectoryName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_SCREENSHOTS).getName();
        } else {
            return null;
        }
    }

    private String getFilePathFromUri(Uri uri) {
        String filePath = "";
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {MediaStore.Images.Media.DATA};

        try {
            Cursor cursor = contentResolver.query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
                cursor.close();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return filePath;
    }
}
