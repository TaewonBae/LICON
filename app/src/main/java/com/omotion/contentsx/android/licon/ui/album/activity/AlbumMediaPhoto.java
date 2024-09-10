package com.omotion.contentsx.android.licon.ui.album.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.OnScaleChangedListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.RBW;
import com.omotion.contentsx.android.licon.core.contents.ContentsManager;
import com.omotion.contentsx.android.licon.core.util.LLog;
import com.omotion.contentsx.android.licon.ui.BaseActivity;
import com.omotion.contentsx.android.licon.ui.album.vo.AlbumMediaItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class AlbumMediaPhoto extends BaseActivity {

    private ImageButton prevBtn;
    private ImageButton nextBtn;
    private ImageButton backBtn;
    private ImageButton downloadBtn;
    private ImageButton youtubePlayImage;
    private PhotoView mediaPhotoView;
    private ConstraintLayout topBarLayout;
    private ConstraintLayout mediaLayoutCount;
    private TextView mediaCountText;

    private TextView youtubeTitle;

    private int mediaCount;
    private int TitleCount;
    private final String namePreferences = "checkStatus";
    private Boolean checkPreferences = false;
    private AlbumMediaItem listItem = null;

    ArrayList<AlbumMediaItem> mediaImageList = new ArrayList<>();

    ArrayList<AlbumMediaItem> mediaYoutubeList = new ArrayList<>();

    Dialog youtubeDialog;
    Context context;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_photo);

        context = this;
        //이미지 height 설정 = device width
        topBarLayout = findViewById(R.id.mediaTopLayout);
        mediaLayoutCount = findViewById(R.id.mediaLayoutCount);
        mediaPhotoView = findViewById(R.id.mediaPhotoView);
        backBtn = findViewById(R.id.backBtn);
        downloadBtn = findViewById(R.id.downloadBtn);
        prevBtn = findViewById(R.id.prevBtn);
        nextBtn = findViewById(R.id.nextBtn);
        mediaCountText = findViewById(R.id.mediaCountText);
        youtubeTitle = findViewById(R.id.youtubeTitle);
        youtubePlayImage = findViewById(R.id.youtubePlayImage);

        listItem = getIntent().getExtras().getParcelable("listItem");

        if (listItem.getMediaUrl().isEmpty()) {
            mediaPhotoView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            youtubePlayImage.setVisibility(View.INVISIBLE);
        }

        mediaImageList = getIntent().getParcelableArrayListExtra("mediaImageList");

        for (int i = 0; i < mediaImageList.size(); i++) {
            if (listItem.getImageUri().equals(mediaImageList.get(i).getImageUri())) {
                mediaCount = i;
            }
        }

        mediaYoutubeList = getIntent().getParcelableArrayListExtra("mediaYoutubeList");

        for (int i = 0; i < mediaYoutubeList.size(); i++) {
            if (listItem.getYotubeTitle().equals(mediaYoutubeList.get(i).getYotubeTitle())) {
                TitleCount = i;
            }
        }

        LLog.d(TAG, "onCreate", "mediaImageList :" + mediaImageList);

        setMediaPhotoView();

        //뒤로가기 버튼 클릭이벤트
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.no_animation, R.anim.slide_down); // slide down 애니메이션
            }
        });

        //다운로드 버튼 클릭이벤트
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imageUri = mediaImageList.get(mediaCount).getImageUri().toString();
                copyFileToGallery(imageUri);
            }
        });
        //이전 버튼 클릭이벤트
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaCount--;
                if (mediaCount < 0) {
                    mediaCount = mediaImageList.size() - 1;
                }
                setMediaPhotoView();
            }
        });

        //다음 버튼 클릭이벤트
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaCount++;
                if (mediaCount >= mediaImageList.size()) {
                    mediaCount = 0;
                }
                setMediaPhotoView();
            }
        });
        // 이미지 클릭 이벤트
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        mediaPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (topBarLayout.getVisibility() == View.VISIBLE || mediaLayoutCount.getVisibility() == View.VISIBLE) {
                    topBarLayout.startAnimation(fadeOut);
                    topBarLayout.setVisibility(View.INVISIBLE);
                    mediaLayoutCount.startAnimation(fadeOut);
                    mediaLayoutCount.setVisibility(View.INVISIBLE);
                } else {
                    topBarLayout.startAnimation(fadeIn);
                    topBarLayout.setVisibility(View.VISIBLE);
                    mediaLayoutCount.startAnimation(fadeIn);
                    mediaLayoutCount.setVisibility(View.VISIBLE);
                }
            }
        });

        // 이미지 Scale 변경 이벤트
        mediaPhotoView.setOnScaleChangeListener(new OnScaleChangedListener() {
            @Override
            public void onScaleChange(float scaleFactor, float focusX, float focusY) {
                // 확대시 Ui 감추게 처리
                if (scaleFactor > 1) {
                    topBarLayout.setVisibility(View.INVISIBLE);
                    mediaLayoutCount.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void setMediaPhotoView() {
        youtubeTitle.setText(mediaYoutubeList.get(mediaCount).getYotubeTitle());
        Glide.with(context).load(mediaImageList.get(mediaCount).getImageUri()).into(mediaPhotoView);
        mediaCountText.setText((mediaCount + 1) + " / " + mediaImageList.size());
    }

    @Override
    protected void onResume() {
        super.onResume();
        RBW.Activity_screenshot = "AlbumMediaPhoto";

        // SharedPreferences 획득
        preferences = getSharedPreferences("NamePreferences", MODE_PRIVATE);

        Boolean boolPreferences = preferences.getBoolean(namePreferences, false);
        // 유튜브 재생 버튼 클릭이벤트
        youtubePlayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!boolPreferences) {
                    showYoutubeDialog();
                } else {
                    goToYouTube();
                }
            }
        });
    }

    private void showYoutubeDialog() {
        youtubeDialog = new Dialog(context);
        youtubeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        youtubeDialog.setContentView(R.layout.dialog_youtube);
        youtubeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        youtubeDialog.show();

        Button closeYoutubeBtn = (Button) youtubeDialog.findViewById(R.id.closeYoutubeBtn); // 닫기 버튼
        //확인
        closeYoutubeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                youtubeDialog.cancel();
            }
        });

        Button openYoutubeBtn = (Button) youtubeDialog.findViewById(R.id.openYoutubeBtn); // 유튜브 열기 버튼
        //확인
        openYoutubeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor SPreferencesEditor = preferences.edit();
                SPreferencesEditor.putBoolean(namePreferences, checkPreferences);
                SPreferencesEditor.commit();

                goToYouTube();
                youtubeDialog.dismiss();

            }

        });

        CheckBox youtubeCheckbox = (CheckBox) youtubeDialog.findViewById(R.id.youtubeCheckbox); // 닫기 버튼
        TextView dontAskText = (TextView) youtubeDialog.findViewById(R.id.dontAskText); // 유튜브 열기 버튼
        //확인
        youtubeCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (youtubeCheckbox.isChecked()) {
                    youtubeCheckbox.setChecked(true);
                    checkPreferences = true;
                    dontAskText.setTextColor(Color.parseColor("#000000"));

                } else {
                    youtubeCheckbox.setChecked(false);
                    checkPreferences = false;
                    dontAskText.setTextColor(Color.parseColor("#33000000"));
                }
            }
        });
    }

    private void goToYouTube() {
        LLog.d(TAG, "goToYouTube", "mediaImageList.getMediaUrl() ::: " + mediaImageList.get(mediaCount).getMediaUrl());
        startActivity(new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse("https://www.youtube.com/watch?v=" + mediaImageList.get(mediaCount).getMediaUrl()))
                .setPackage("com.google.android.youtube"));
    }

    public void copyFileToGallery(String sourcePath) {
        if (sourcePath.startsWith("file://")) {
            sourcePath = sourcePath.substring(7);
        }

        File sourceFile = new File(sourcePath);
        File destinationDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File destinationFile = new File(destinationDir, sourceFile.getName());

        try {
            FileInputStream inputStream = new FileInputStream(sourceFile);
            FileOutputStream outputStream = new FileOutputStream(destinationFile);

            FileChannel inChannel = inputStream.getChannel();
            FileChannel outChannel = outputStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inputStream.close();
            outputStream.close();

            MediaScannerConnection.scanFile(context, new String[]{destinationFile.getAbsolutePath()}, null, (path, uri) -> {
                Toast.makeText(this, "이미지 다운로드 완료.", Toast.LENGTH_SHORT).show();
            });
        } catch (IOException e) {
            Toast.makeText(this, "이미지 다운로드 실패.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}