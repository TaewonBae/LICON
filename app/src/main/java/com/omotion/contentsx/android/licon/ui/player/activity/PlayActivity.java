package com.omotion.contentsx.android.licon.ui.player.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.RBW;
import com.omotion.contentsx.android.licon.core.contents.ContentsManager;
import com.omotion.contentsx.android.licon.core.player.MusicPlayer;
import com.omotion.contentsx.android.licon.core.util.LLog;
import com.omotion.contentsx.android.licon.core.util.Utils;
import com.omotion.contentsx.android.licon.data.remote.model.Song;
import com.omotion.contentsx.android.licon.ui.BaseActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class PlayActivity extends BaseActivity {
    private final String TAG = this.getClass().getName();
    private ImageView blur_img; //블러 이미지 (백그라운드)
    private ImageButton backBtn; // 뒤로가기 버튼
    /*private ImageButton btn_cc; // 한국어 버튼
    private ImageButton btn_pronun; // 발음 버튼*/
    private ImageButton btn_shuffle; // 셔플 버튼
    private ImageButton btn_repeat; // repeat 버튼
    private ImageButton btn_play; // 재생 버튼
    private ImageButton btn_prev; // 이전곡 버튼
    private ImageButton btn_next; // 다음곡 버튼
    private BottomSheetDialog dialog; // 언어선택 다이얼로그
    private TextView lyrics/*, lyricsAnnounce*/;
    private SeekBar sb_progress;
    private ArrayList<Song.Lyric> lyricses;
    private List<String> mSelectedLyricLines/*, mLyricPronunLines*/;
    private ArrayList<CheckBox> mCbLanguages = null;
    private ArrayList<Integer> mCCResIds = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        RBW.Activity_screenshot = "";

        init();
        MusicPlayer.getInstance().addMusicPlayListener(this);
        if (MusicPlayer.getInstance().getCurPlayStatus() != MusicPlayer.PlayStatus.PLAY) {
            MusicPlayer.getInstance().play();

        }
    }

    private void init() {
        blur_img = findViewById(R.id.blur_img);
        backBtn = findViewById(R.id.backBtn);
        /*btn_cc = findViewById(R.id.btn_cc);
        btn_pronun = findViewById(R.id.btn_pronun);*/
        btn_shuffle = findViewById(R.id.btn_shuffle);
        btn_play = findViewById(R.id.btn_play);
        btn_repeat = findViewById(R.id.btn_repeat);
        btn_prev = findViewById(R.id.btn_prev);
        btn_next = findViewById(R.id.btn_next);
        lyrics = findViewById(R.id.play_lyrics);
        /*lyricsAnnounce = findViewById(R.id.play_lyrics_announce);
        lyricsAnnounce.setVisibility(View.VISIBLE);*/
        sb_progress = findViewById(R.id.seek_bar);

        //bottom sheet dialog 생성
        dialog = new BottomSheetDialog(this);
//        createDialog();
/*
        // 한국어 버튼
        btn_cc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
        // 발음 버튼
        btn_pronun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicPlayer.getInstance().setShowPronunciation(!MusicPlayer.getInstance().isShowPronunciation());
                setAnnounceLyricView();
            }
        });*/
        // 셔플 버튼
        btn_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MusicPlayer.getInstance().isShuffle()) {
                    MusicPlayer.getInstance().setShuffle(true);
                    btn_shuffle.setImageResource(R.drawable.ic_shuffle_black);
                    Toast message = Toast.makeText(getApplication(), getResources().getString(R.string.shuffle_on), Toast.LENGTH_SHORT); // 셔플 선택
                    message.show();
                } else {
                    MusicPlayer.getInstance().setShuffle(false);
                    btn_shuffle.setImageResource(R.drawable.ic_shuffle);
                    Toast message = Toast.makeText(getApplication(), getResources().getString(R.string.shuffle_off), Toast.LENGTH_SHORT); // 셔플 해제
                    message.show();
                }
            }
        });
        // 반복 버튼
        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast message;
                switch (MusicPlayer.getInstance().getRepeatMode()) {
                    case NORMAL:
                        MusicPlayer.getInstance().setRepeatMode(MusicPlayer.RepeatMode.REPEAT_LIST);
                        btn_repeat.setImageResource(R.drawable.ic_repeat_black);
                        message = Toast.makeText(getApplication(), getResources().getString(R.string.play_all), Toast.LENGTH_SHORT); // 전체 반복
                        message.show();
                        break;
                    case REPEAT_LIST:
                        btn_repeat.setImageResource(R.drawable.ic_repeat_black2);
                        MusicPlayer.getInstance().setRepeatMode(MusicPlayer.RepeatMode.REPEAT_ONE);
                        message = Toast.makeText(getApplication(), getResources().getString(R.string.play_one), Toast.LENGTH_SHORT); // 한곡 반복 재생
                        message.show();
                        break;
                    case REPEAT_ONE:
                        btn_repeat.setImageResource(R.drawable.ic_repeat);
                        MusicPlayer.getInstance().setRepeatMode(MusicPlayer.RepeatMode.NORMAL);
                        message = Toast.makeText(getApplication(), getResources().getString(R.string.play_no_repeat), Toast.LENGTH_SHORT); // 반복 없음
                        message.show();
                        break;
                }
            }
        });
        // 이전곡 버튼
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicPlayer.getInstance().prev();
            }
        });
        // 다음곡 버튼
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicPlayer.getInstance().next();
            }
        });
        // 재생 버튼
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicPlayer.getInstance().onPlayEvent();
            }
        });
        // 뒤로가기 버튼
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.no_animation, R.anim.slide_down); // slide down 애니메이션
            }
        });
        sb_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                LLog.d(TAG, "onProgressChanged", "i: " + i + ",b: " + b);
                if (b) {
                    MusicPlayer.getInstance().moveToPosition(i, true);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                LLog.d(TAG, "onStartTrackingTouch", "");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicPlayer.getInstance().moveToPosition(seekBar.getProgress(), false);
            }
        });
        // 가사 클릭 이벤트
        findViewById(R.id.ll_lyrics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), LyricsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.no_animation);
            }
        });
    }

    private void setSongInfo(Song song) {
        //앨범 타이틀
        ((TextView) findViewById(R.id.tv_album)).setText(song.album);
        ((TextView) findViewById(R.id.tv_album)).setSelected(true);
        //노래 제목
        ((TextView) findViewById(R.id.tv_song)).setText(song.title);
        ((TextView) findViewById(R.id.tv_song)).setSelected(true);
        //가수
        ((TextView) findViewById(R.id.tv_artist)).setText(song.singer);
        //앨범 이미지
        ImageView ivAlbum = (ImageView) findViewById(R.id.play_album_img);
        Glide.with(this).load(ContentsManager.getInstance().getAlbumImgCover()) // Firebase에서 다운 받은 앨범 커버 이미지 할당
                .into(ivAlbum);

        //배경 Blur 이미지 처리
        Glide.with(this).load(ContentsManager.getInstance().getAlbumImgCover()) // Firebase에서 다운 받은 앨범 커버 이미지 할당
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 5)))
                .into(blur_img);
        //duration millis
        String totalDuration = MusicPlayer.PLAY_TIME_FORMAT.format(song.duration * 1000);
        ((TextView) findViewById(R.id.tv_total_duration)).setText(totalDuration);
        lyricses = song.lyrics;
        setLyrics(MusicPlayer.getInstance().getLyricLanguageIndex());
        //발음
        /*String lyricPronun = lyricses.get(lyricses.size() - 1).lyrics_;
        mLyricPronunLines = Arrays.asList(lyricPronun.split("\n"));*/
    }

    /*private void createDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_language, null, false);
        mCbLanguages = new ArrayList<>();
        mCCResIds = new ArrayList<>();
        mCbLanguages.add(view.findViewById(R.id.cb_korea));
        mCCResIds.add(R.drawable.btn_cc_kor_on);
        mCbLanguages.add(view.findViewById(R.id.cb_english));
        mCCResIds.add(R.drawable.btn_cc_eng_on);
        mCbLanguages.add(view.findViewById(R.id.cb_spain));
        mCCResIds.add(R.drawable.btn_cc_esp_on);
        mCbLanguages.add(view.findViewById(R.id.cb_france));
        mCCResIds.add(R.drawable.btn_cc_fran_on);
        mCbLanguages.add(view.findViewById(R.id.cb_malaysia));
        mCCResIds.add(R.drawable.btn_cc_mel_on);
        mCbLanguages.add(view.findViewById(R.id.cb_portu));
        mCCResIds.add(R.drawable.btn_cc_port_on);
        mCbLanguages.add(view.findViewById(R.id.cb_indonesia));
        mCCResIds.add(R.drawable.btn_cc_bah_on);
        mCbLanguages.add(view.findViewById(R.id.cb_arab));
        mCCResIds.add(R.drawable.btn_cc_arab_on);
        mCbLanguages.add(view.findViewById(R.id.cb_thai));
        mCCResIds.add(R.drawable.btn_cc_tai_on);
        mCbLanguages.add(view.findViewById(R.id.cb_japan));
        mCCResIds.add(R.drawable.btn_cc_jap_on);
        mCbLanguages.add(view.findViewById(R.id.cb_china_ganche));
        mCCResIds.add(R.drawable.btn_cc_chini_s_on);
        mCbLanguages.add(view.findViewById(R.id.cb_china_bunche));
        mCCResIds.add(R.drawable.btn_cc_chini_t_on);

        for (CheckBox cb : mCbLanguages) {
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        for (CheckBox cb : mCbLanguages) {
                            if (!cb.equals(compoundButton)) {
                                cb.setChecked(false);
                            }
                        }
                        MusicPlayer.getInstance().setLyricLanguageIndex(mCbLanguages.indexOf(compoundButton));
                        setCCView();
                    }
                }
            });
        }
        int index = MusicPlayer.getInstance().getLyricLanguageIndex();
        mCbLanguages.get(index).setChecked(true);
        btn_cc.setImageResource(mCCResIds.get(index));
        view.findViewById(R.id.language_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        //한국어 버튼
        dialog.setContentView(view);
    }

    private void setCCView() {
        int index = MusicPlayer.getInstance().getLyricLanguageIndex();
        btn_cc.setImageResource(mCCResIds.get(index));
        setLyrics(index);
        MusicPlayer.getInstance().callOnPlayTimeMillis();
    }

    private void setAnnounceLyricView() {
        boolean isShowPronun = MusicPlayer.getInstance().isShowPronunciation();
        btn_pronun.setImageResource(isShowPronun ? R.drawable.btn_pronunciation_kor_on : R.drawable.ic_pronun_btn);
        lyricsAnnounce.setVisibility(isShowPronun ? View.VISIBLE : View.INVISIBLE);
    }*/

    private void setLyrics(int languageIndex) {
        if (lyricses != null && !lyricses.isEmpty()) {
            String lyric = lyricses.get(languageIndex).lyrics_;
            mSelectedLyricLines = Arrays.asList(lyric.split("\n"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //play mode view 갱신
        btn_shuffle.setImageResource(MusicPlayer.getInstance().isShuffle() ? R.drawable.ic_shuffle_black : R.drawable.ic_shuffle);
        int resId = R.drawable.ic_repeat;
        switch (MusicPlayer.getInstance().getRepeatMode()) {
            case NORMAL:
                resId = R.drawable.ic_repeat;
                break;
            case REPEAT_LIST:
                resId = R.drawable.ic_repeat_black;
                break;
            case REPEAT_ONE:
                resId = R.drawable.ic_repeat_black2;
                break;
        }
        btn_repeat.setImageResource(resId);
        /*setCCView();
        setAnnounceLyricView();*/
    }

    @Override
    public void onPlayTimeMillis(long timeMillis) {
        if (timeMillis == 0) {
            lyrics.setText("간주중");
        }
        if (mSelectedLyricLines != null && mSelectedLyricLines.size() > 0
            /*&& mLyricPronunLines != null && mLyricPronunLines.size() > 0*/) {
            for (String lines : mSelectedLyricLines) {
                String lyricTimeLine = lines.substring(0, lines.lastIndexOf("]") + 1);
                long lyricMillis = Utils.timeFormatToLong(lyricTimeLine);
                long gap = lyricMillis - timeMillis;
                if (gap >= 0 && gap <= MusicPlayer.getInstance().PLAY_DELAY) {
                    String lyricStr = lines.substring(lines.lastIndexOf("]") + 1);
                    lyrics.setText(lyricStr);
                    //            lyricsAnnounce.setText(mLyricPronunLines.get(mFocusedIdx).substring(millisTime.length()));
                    break;
//                }
                }
            }
        }
    }

    @Override
    public void onPlayTimeChanged(String playTime, int percent) {
        LLog.d(TAG, "onPlayTimeChanged", "playTime: " + playTime + ", percent: " + percent);
        //current millis
        ((TextView) findViewById(R.id.tv_current_duration)).setText(playTime);
        //seek bar
        sb_progress.setProgress(percent);
    }

    @Override
    public void onPlayStatusChanged(MusicPlayer.PlayStatus status) {
        switch (status) {
            case STOP:
            case PAUSE:
                btn_play.setImageResource(R.drawable.ic_btn_play);
                break;
            case PLAY:
                btn_play.setImageResource(R.drawable.ic_btn_pause);
                break;
        }
    }

    @Override
    public void onSongInfoChanged(Song song) {
        if (song == null) {
            LLog.d(TAG, "onSongInfoChanged", "song is null");
            return;
        }
        setSongInfo(song);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicPlayer.getInstance().removeMusicPlayListener(this);
    }
}
