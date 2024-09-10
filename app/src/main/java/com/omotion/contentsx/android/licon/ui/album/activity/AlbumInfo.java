package com.omotion.contentsx.android.licon.ui.album.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
// 음원 정보 Activity
public class AlbumInfo extends BaseActivity {

    private ImageView info_blur;
    private ImageButton backBtn;
    private TextView info_title;
    private TextView info_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        RBW.Activity_screenshot = "";

        info_blur = this.findViewById(R.id.info_blur);
        backBtn = this.findViewById(R.id.backBtn);
        info_title = this.findViewById(R.id.info_title);
        info_text = this.findViewById(R.id.info_text);

        Intent intent = getIntent();
        String title = intent.getStringExtra("TITLE");
        String content = intent.getStringExtra("CONTENT");

        info_title.setText(title);
        info_title.setEllipsize(TextUtils.TruncateAt.MARQUEE); // 흐르게 만들기
        info_title.setSelected(true);      // 선택하기
        info_text.setText(content.replace("\\n", "\n"));

        //Blur 이미지 처리
        Glide.with(this).load(ContentsManager.getInstance().getAlbumImgCover()) // 음원 정보 : 백그라운드 블러 이미지 Firebase에서 다운로드 받은 데이터 할당
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 5)))
                .into(info_blur);
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