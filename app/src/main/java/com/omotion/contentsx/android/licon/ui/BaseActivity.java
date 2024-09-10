package com.omotion.contentsx.android.licon.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.RBW;
import com.omotion.contentsx.android.licon.core.contents.ContentsManager;
import com.omotion.contentsx.android.licon.core.player.MusicPlayListener;
import com.omotion.contentsx.android.licon.core.player.MusicPlayer;
import com.omotion.contentsx.android.licon.core.util.LLog;
import com.omotion.contentsx.android.licon.core.util.screenshot.ScreenshotDetectDelegate;
import com.omotion.contentsx.android.licon.core.util.screenshot.ScreenshotDetectListener;
import com.omotion.contentsx.android.licon.data.remote.model.Song;
import com.omotion.contentsx.android.licon.ui.player.activity.PlayActivity;

public class BaseActivity extends AppCompatActivity implements ScreenshotDetectListener, MusicPlayListener {
    protected Activity mCurrentActivity;
    protected String TAG = getClass().getSimpleName();
    // bottom_play_controller.xml
    public LinearLayout llParentBottomPlayer = null;
    public ProgressBar pbBottomPlayer = null;
    public ImageView ivAlbumBottomPlayer = null;
    public TextView tvTitleBottomPlayer = null, tvArtistBottomPlayer = null;
    public ImageButton ibPlayBottomPlayer = null, ibCloseBottomPlayer = null;
    private ScreenshotDetectDelegate screenshotDetectionDelegate = new ScreenshotDetectDelegate(new Handler(), this, this);
    private Dialog screenShotDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentActivity = this;
    }

    @Override
    public void setContentView(int layoutResID) {
        ConstraintLayout clParent = (ConstraintLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        ConstraintLayout clContainer = clParent.findViewById(R.id.cl_container);
        initBottomPlayController(clParent);
        getLayoutInflater().inflate(layoutResID, clContainer, true);
        super.setContentView(clParent);
    }

    @Override
    public void onScreenCaptured(@NonNull String s) {
        LLog.d(TAG, "onScreenCaptured", "path: " + s);
        if (screenShotDialog == null && mCurrentActivity.getClass().getSimpleName().equals(RBW.Activity_screenshot)) {
            screenShotDialog = new Dialog(mCurrentActivity);
            screenShotDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            screenShotDialog.setContentView(R.layout.dialog_capture_alert);
            screenShotDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            screenShotDialog.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    screenShotDialog.cancel();
                    screenShotDialog = null;
                }
            });

            if (!screenShotDialog.isShowing()) {
                screenShotDialog.show();
            }
        } //여기입니다 쉔쉐이

    }

    @Override
    protected void onStart() {
        super.onStart();
        screenshotDetectionDelegate.startScreenshotDetection();
    }

    @Override
    protected void onStop() {
        super.onStop();
        screenshotDetectionDelegate.stopScreenshotDetection();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCurrentActivity = null;
    }

    private void initBottomPlayController(View v) {
        llParentBottomPlayer = v.findViewById(R.id.ll_parent_bottom_player);
        pbBottomPlayer = v.findViewById(R.id.pb_bottom_player);
        ivAlbumBottomPlayer = v.findViewById(R.id.iv_album_bottom_player);
        tvTitleBottomPlayer = v.findViewById(R.id.tv_title_bottom_player);
        tvArtistBottomPlayer = v.findViewById(R.id.tv_artist_bottom_player);
        ibPlayBottomPlayer = v.findViewById(R.id.ib_play_bottom_player);
        ibCloseBottomPlayer = v.findViewById(R.id.ib_close_bottom_player);

        ivAlbumBottomPlayer.setClipToOutline(true); // xml에서 background 넣어준 뒤 해당 코드 추가해야 적
        llParentBottomPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mCurrentActivity, PlayActivity.class));
                overridePendingTransition(R.anim.slide_up, R.anim.no_animation);
            }
        });

        ibPlayBottomPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicPlayer.getInstance().onPlayEvent();
            }
        });
        ibCloseBottomPlayer.setOnClickListener(new View.OnClickListener() { //play_view의 close 버튼 클릭이벤트
            @Override
            public void onClick(View view) {
                MusicPlayer.getInstance().stop();
            }
        });
    }

    @Override
    public void onPlayTimeMillis(long timeMillis) {

    }

    @Override
    public void onPlayTimeChanged(String playTime, int percent) {
        pbBottomPlayer.setProgress(percent);

    }

    @Override
    public void onPlayStatusChanged(MusicPlayer.PlayStatus status) {
        switch (status) {
            case PLAY:
                llParentBottomPlayer.setVisibility(View.VISIBLE);
                ibPlayBottomPlayer.setImageResource(R.drawable.ic_main_play_pause);
                break;
            case STOP:
                ibPlayBottomPlayer.setImageResource(R.drawable.ic_main_play_play);
                llParentBottomPlayer.setVisibility(View.GONE);
                break;
            case PAUSE:
                llParentBottomPlayer.setVisibility(View.VISIBLE);
                ibPlayBottomPlayer.setImageResource(R.drawable.ic_main_play_play);
                break;
        }
    }

    @Override
    public void onSongInfoChanged(Song song) {
        if (song == null) {
            LLog.d(TAG, "onSongInfoChanged", "is null!");
            return;
        }

        if (mCurrentActivity != null) {
            //Glide.with(mCurrentActivity).load(R.drawable.album_cover).into(ivAlbumBottomPlayer); //여기 입니다 쉔세이
            Glide.with(mCurrentActivity).load(ContentsManager.getInstance().getAlbumImgCover()).into(ivAlbumBottomPlayer); //여기 입니다 쉔세이
        }
        tvTitleBottomPlayer.setText(song.title);
        tvTitleBottomPlayer.setSelected(true);
        tvArtistBottomPlayer.setText(song.singer);
    }
}
