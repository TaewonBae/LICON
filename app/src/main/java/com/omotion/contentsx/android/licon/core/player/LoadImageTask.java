package com.omotion.contentsx.android.licon.core.player;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import com.omotion.contentsx.android.licon.core.contents.ContentsManager;

public class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {
   private final Uri albumImgUri;
   private final Callback callback;

   public interface Callback {
      void onBitmapLoaded(Bitmap bitmap);
   }

   public LoadImageTask(Uri albumImgUri, Callback callback) {
      this.albumImgUri = albumImgUri;
      this.callback = callback;
   }

   @Override
   protected Bitmap doInBackground(Void... params) {
      // 비트맵 변환 작업 수행
      return ContentsManager.getInstance().getBitmapFromLocalFile(albumImgUri.toString());
   }

   @Override
   protected void onPostExecute(Bitmap coverImage) {
      // 비트맵을 사용하여 콜백 호출
      if (coverImage != null) {
         callback.onBitmapLoaded(coverImage);
      }
   }
}