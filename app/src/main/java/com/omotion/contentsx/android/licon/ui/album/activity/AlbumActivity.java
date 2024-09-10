package com.omotion.contentsx.android.licon.ui.album.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.RBW;
import com.omotion.contentsx.android.licon.core.contents.ContentsManager;
import com.omotion.contentsx.android.licon.core.player.MusicPlayer;
import com.omotion.contentsx.android.licon.core.util.LLog;
import com.omotion.contentsx.android.licon.data.remote.model.AlbumInfoVO;
import com.omotion.contentsx.android.licon.ui.BaseActivity;
import com.omotion.contentsx.android.licon.ui.album.adapter.AlbumAdapter;
import com.omotion.contentsx.android.licon.ui.album.fragment.AlbumMediaFragment;
import com.omotion.contentsx.android.licon.ui.album.fragment.AlbumPhotoFragment;
import com.omotion.contentsx.android.licon.ui.album.fragment.AlbumTrackFragment;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class AlbumActivity extends BaseActivity {

    private ViewPager2 viewPager2;
    private AlbumAdapter albumAdapter;
    private TabLayout tabLayout;

    private ImageButton album_play_btn;
    private ImageButton backBtn;
    private ImageView album_poster;

    private Button album_credit;
    private AppCompatImageView album_blur;
    private TextView albumTitle, albumArtist, albumTrackInfo;
    private Toolbar toolbar;
    Fragment fragment1, fragment2, fragment3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        AlbumInfoVO albumItem = ContentsManager.getInstance().getAlbumInfoVO();
        LLog.d(TAG, "onCreate", "albumItem ::: " + albumItem);
        init(albumItem);
        MusicPlayer.getInstance().addMusicPlayListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        RBW.Activity_screenshot = "AlbumActivity";
    }

    private void init(AlbumInfoVO albumItem) {
        fragment1 = new AlbumTrackFragment();
        fragment2 = new AlbumMediaFragment();
        fragment3 = new AlbumPhotoFragment();


        albumTitle = findViewById(R.id.album_title);
        albumArtist = findViewById(R.id.album_artist);
        albumTrackInfo = findViewById(R.id.album_text);
        album_play_btn = findViewById(R.id.album_play_btn);
        album_credit = findViewById(R.id.album_credit);
        album_poster = findViewById(R.id.iv_album_poster);
        album_blur = findViewById(R.id.album_blur);
        backBtn = findViewById(R.id.backBtn);


        Glide.with(this).load(ContentsManager.getInstance().getAlbumImgCover()).into(album_poster); //여기 입니다 쉔세이 Firebase에서 다운받은 앨범 커버 이미지 할당
        album_poster.setClipToOutline(true);
        // 툴바에 타이틀 넣기/////////////////////////////////////
        toolbar = findViewById(R.id.toolbar);

        albumTitle.setText(ContentsManager.getInstance().getAlbumInfoVO().getCardAlbumText()); // 여기 입니다 쉔세이 앨범 타이틀
        albumTitle.setSingleLine(true);    // 한줄로 표시하기
        albumTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE); // 흐르게 만들기
        albumTitle.setSelected(true);      // 선택하기
        albumArtist.setText(ContentsManager.getInstance().getAlbumInfoVO().getCardArtistText()); // 여기 입니다 쉔세이 앨범 아티스트

        albumTrackInfo.setText(albumItem.getTrackInfo());
        // 뷰페이저에 어댑터 연결
        viewPager2 = findViewById(R.id.viewPager2);
        albumAdapter = new AlbumAdapter(getSupportFragmentManager(), getLifecycle());
        albumAdapter.addFragment(fragment1);
        albumAdapter.addFragment(fragment2);
        albumAdapter.addFragment(fragment3);
        viewPager2.setAdapter(albumAdapter);
        int pos = getIntent().getBooleanExtra("NEED_MOVE_TO_PHOTO_CARD", false) ? 2 : 0;
        viewPager2.setCurrentItem(pos);

        // 탭과 뷰페이저 연결
        tabLayout = findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0) {
                    tab.setText(getResources().getString(R.string.album_detail_tap1));
                } else if (position == 1) {
                    tab.setText(getResources().getString(R.string.album_detail_tap2));
                } else {
                    tab.setText(getResources().getString(R.string.album_detail_tap3));
                }
            }
        });
        tabLayoutMediator.attach();
        /////////////////////////////////////

        //Blur 이미지 크기조절 및 처리
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) album_blur.getLayoutParams();
        layoutParams.height = RBW.deviceWidth; //가로 = 세로
        album_blur.setLayoutParams(layoutParams);
        // 여기 입니다 쉔세이 Firebase에서 다운받은 블러 이미지 할당
        Glide.with(this).load(ContentsManager.getInstance().getAlbumImgCover()) // 여기 입니다 쉔세이 제가한것같습니다. (백그라운드 블러)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 5)))
                .into(album_blur);

        album_play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicPlayer.getInstance().onPlayEvent();
            }
        });
        album_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AlbumCredit.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.no_animation);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitActivity();
            }
        });
    }

    private void exitActivity() {
        finish();
        overridePendingTransition(R.anim.no_animation, R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        exitActivity();
    }


    @Override
    public void onPlayStatusChanged(MusicPlayer.PlayStatus status) {
        super.onPlayStatusChanged(status);
        switch (status) {
            case PLAY:
                album_play_btn.setImageResource(R.drawable.ic_main_pause);
                break;
            case STOP:
            case PAUSE:
                album_play_btn.setImageResource(R.drawable.ic_main_play);
                break;
        }
    }
}