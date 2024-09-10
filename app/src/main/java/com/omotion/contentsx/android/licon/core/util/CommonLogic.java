package com.omotion.contentsx.android.licon.core.util;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.contents.ContentsManager;
import com.omotion.contentsx.android.licon.core.player.MusicPlayer;
import com.omotion.contentsx.android.licon.ui.album.activity.AlbumActivity;

import java.io.File;

public class CommonLogic {
    public static void goToAlbumActivity(Activity context, boolean needMoveToPhotoCard) {
        if (MusicPlayer.getInstance().isSongsPrepared()) {
            Intent intent = new Intent(context, AlbumActivity.class);
            intent.putExtra("NEED_MOVE_TO_PHOTO_CARD", needMoveToPhotoCard);
            context.startActivity(intent);
            context.overridePendingTransition(R.anim.fade_in, R.anim.no_animation);
        } else {
            Toast.makeText(context, context.getApplication().getResources().getString(R.string.download_need), Toast.LENGTH_SHORT).show(); // 다운로드 필요
            File file = ContentsManager.getInstance().getParentFile();
            if (file.exists()) {
                Utils.deleteRecursive(file);
                ContentsManager.getInstance().setContentsPassResult(false);
            }
        }
    }
}
