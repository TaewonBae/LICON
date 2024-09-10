package com.omotion.contentsx.android.licon.ui.album.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.RBW;
import com.omotion.contentsx.android.licon.core.contents.ContentsManager;
import com.omotion.contentsx.android.licon.ui.BaseActivity;

import jp.wasabeef.glide.transformations.BlurTransformation;

// 앨범 정보
public class AlbumCredit extends BaseActivity {

    private ImageView credit_blur;
    private ImageButton backBtn;
    private TextView album_text1;
    private TextView album_text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        RBW.Activity_screenshot = "";

        credit_blur = findViewById(R.id.credit_blur);
        backBtn = findViewById(R.id.backBtn);
        album_text1 = findViewById(R.id.album_text1);
        album_text2 = findViewById(R.id.album_text2);

        String content = ContentsManager.getInstance().getAlbumInfoVO().getCardAlbumText(); // 앨범 타이틀 텍스트
        album_text1.setText(content.replace("\\n", "\n"));

        String content2 = ContentsManager.getInstance().getAlbumInfoVO().getAlbumInfo();   // 앨범 정보 텍스트
        album_text2.setText(content2.replace("\\n", "\n"));

        //Blur 이미지 처리
        Glide.with(this).load(ContentsManager.getInstance().getAlbumImgCover()) // Firebase에서 다운 받은 앨범 커버 이미지 할당
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 5)))
                .into(credit_blur);
        // 뒤로가기 버튼
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.no_animation, R.anim.slide_down); // slide down 애니메이션
            }
        });

    }
}