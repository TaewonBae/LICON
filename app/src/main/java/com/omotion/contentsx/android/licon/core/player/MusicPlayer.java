package com.omotion.contentsx.android.licon.core.player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.contents.ContentsManager;
import com.omotion.contentsx.android.licon.core.util.LLog;
import com.omotion.contentsx.android.licon.core.util.SharedPrefManager;
import com.omotion.contentsx.android.licon.data.remote.model.Song;
import com.omotion.contentsx.android.licon.ui.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat; // 여기
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;


public class MusicPlayer implements Player.Listener {

    // 추가된 부분: MediaSessionCompat
    private MediaSessionCompat mediaSession;
    // 추가된 부분: Notification 및 NotificationManager
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "LICON"; // 여기에 적절한 값을 넣어주세요

    // 추가된 부분: 사용자 정의된 액션 상수
    private static final String PACKAGE_NAME = "com.omotion.contentsx.android.licon.core.player";
    private static final String PREVIOUS_ACTION = PACKAGE_NAME + ".PREVIOUS_ACTION";
    private static final String PAUSE_ACTION = PACKAGE_NAME + ".PAUSE_ACTION";
    private static final String NEXT_ACTION = PACKAGE_NAME + ".NEXT_ACTION";

    private final String TAG = this.getClass().getName();
    private static MusicPlayer instance = null;
    private SimpleExoPlayer exoPlayer = null;
    private ArrayList<MusicPlayListener> listeners = null;
    private Handler progressHandler = new Handler();
    private Runnable progressRunnable;
    private long currentMilli;
    public final long PLAY_DELAY = 1000;
    private final long LYRIC_OFFSET = -50;
    public static final SimpleDateFormat PLAY_TIME_FORMAT = new SimpleDateFormat("mm:ss");
    private Song curSong = null;
    private List<Song> allSongs = null;

    private PlayStatus curPlayStatus = PlayStatus.STOP;

    public enum PlayStatus {
        PLAY, PAUSE, STOP
    }

    private RepeatMode repeatMode;


    public enum RepeatMode {
        NORMAL, REPEAT_ONE, REPEAT_LIST
    }

    private boolean isShuffle;
    private boolean isShowPronunciation;
    private int lyricLanguageIndex;

    public static MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }

    public void init(Context context) {
        listeners = new ArrayList<>();
        exoPlayer = new SimpleExoPlayer.Builder(context).build();
        exoPlayer.addListener(this);

        lyricLanguageIndex = SharedPrefManager.getData(SharedPrefManager.LYRIC_LANGUAGE_INDEX, 0);
        isShowPronunciation = SharedPrefManager.getData(SharedPrefManager.IS_SHOW_PRONUNCIATION, false);
        isShuffle = SharedPrefManager.getData(SharedPrefManager.IS_SHUFFLE, false);
        exoPlayer.setShuffleModeEnabled(isShuffle);
        repeatMode = RepeatMode.values()[SharedPrefManager.getData(SharedPrefManager.REPEAT_MODE,
                RepeatMode.NORMAL.ordinal())];
        exoPlayer.setRepeatMode(repeatMode.ordinal());

        // 추가된 부분: MediaSessionCompat 관련
        mediaSession = new MediaSessionCompat(context, TAG); // 추가된 부분: MediaSessionCompat 초기화
        mediaSession.setCallback(new MediaSessionCallback()); // 추가된 부분: MediaSessionCompat 콜백 설정
        mediaSession.setActive(true); // 추가된 부분: MediaSessionCompat을 활성화

        // 추가된 부분: NotificationManager 초기화
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 추가된 부분: Notification 채널 생성
        createNotificationChannel();

        // 추가된 부분: Notification Builder 초기화
        notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("LICON")
                .setDefaults(Notification.DEFAULT_VIBRATE) // 알림오면 소리, 진동, 불빛 설정
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.logo_licon2)
                //.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(null) // 버튼 외 영역 null로 처리 안하면 오류발생
                .setAutoCancel(true) // 음원 정지하고 슬라이드시 앱 종료 (재생중 종료불가)
                .setDeleteIntent(createPendingIntent(context)); // 삭제 인텐트 설정

        // 추가된 부분: Notification에 미디어 세션 연동
        mediaSession.setMediaButtonReceiver(createPendingIntent(context));
        notificationBuilder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession.getSessionToken())
                .setShowActionsInCompactView(0, 1, 2)
        );

        // 추가된 부분: Notification에 액션 추가
        notificationBuilder.addAction(new NotificationCompat.Action(
                R.drawable.ic_btn_prev,
                "Previous",
                createPendingIntent(context, PREVIOUS_ACTION)
        ));
        notificationBuilder.addAction(new NotificationCompat.Action(
                R.drawable.ic_btn_pause,
                "Pause",
                createPendingIntent(context, PAUSE_ACTION)
        ));
        notificationBuilder.addAction(new NotificationCompat.Action(
                R.drawable.ic_btn_next,
                "Next",
                createPendingIntent(context, NEXT_ACTION)
        ));

        // 추가된 부분: Notification 갱신
        updateNotification();


        // 추가된 부분: NotificationManager에 Notification 등록
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void addMusicPlayListener(MusicPlayListener listener) {
        listeners.add(listener);
        callListeners();
    }

    public void removeMusicPlayListener(MusicPlayListener listener) {
        listeners.remove(listener);
    }

    public void setSongs(List<Song> songs) {
        allSongs = songs;
        if (allSongs == null || allSongs.isEmpty()) {
            LLog.d(TAG, "setSongs", "is null");
            return;
        }
        List<MediaItem> items = new ArrayList<>();
        for (Song s : allSongs) {
            LLog.d(TAG, "setSongs", "s.fileUri: " + s.fileUri);
            if (s.fileUri != null) {
                items.add(new MediaItem.Builder().setUri(s.fileUri).build());
            }
        }
        if (exoPlayer != null && !items.isEmpty()) {
            exoPlayer.setMediaItems(items);
            exoPlayer.prepare();
            moveToPosition(0, true);
            exoPlayer.pause();
        }
    }

    public boolean isSongsPrepared() {
        return allSongs != null && allSongs.size() > 0;
    }

    public void checkCurrentMediaItem(String title) {
        if (curSong == null || curSong.title.equals(title)) {
            return;
        }
        // curSong 변경
        for (Song song : allSongs) {
            if (song.title.equals(title)) {
                for (int i = 0; i < exoPlayer.getMediaItemCount(); i++) {
                    MediaItem item = exoPlayer.getMediaItemAt(i);
                    if (song.fileUri.toString().equals(item.playbackProperties.uri.toString())) {
                        LLog.d(TAG, "checkCurrentMediaItem", song.fileUri.toString());
                        exoPlayer.seekTo(i, 0);
                        curSong = song;
                        break;
                    }
                }
                break;
            }
        }
    }

    private void setCurrentSong() {
        if (exoPlayer.getCurrentMediaItem() == null) {
            return;
        }
        String url = exoPlayer.getCurrentMediaItem().playbackProperties.uri.toString();
        for (Song s : allSongs) {
            if (s.fileUri.toString().equals(url)) {
                LLog.d(TAG, "setCurrentSong", "s.fileUri: " + s.fileUri);
                curSong = s;
                callListeners();
                break;
            }
        }
    }

    public void onPlayEvent() {
        if (curPlayStatus == MusicPlayer.PlayStatus.PLAY) {
            MusicPlayer.getInstance().pause();
        } else {
            MusicPlayer.getInstance().play();
        }
    }

    public void play() {
        LLog.d(TAG, "play", "called!");
        if (exoPlayer != null) {
            if (curPlayStatus == PlayStatus.STOP) {
                exoPlayer.seekTo(currentMilli);
            }
            exoPlayer.play();
            startProgressTracking();
            setPlayStatus(PlayStatus.PLAY);
        }
    }

    public void pause() {
        LLog.d(TAG, "pause", "called!");
        if (exoPlayer != null && exoPlayer.isPlaying()) {
            exoPlayer.pause();
            progressHandler.removeCallbacks(progressRunnable);
            setPlayStatus(PlayStatus.PAUSE);
        }
    }

    public void stop() {
        LLog.d(TAG, "stop", "called!");
        if (exoPlayer != null) {
            exoPlayer.seekTo(0);
            exoPlayer.pause();
            setPlayStatus(PlayStatus.STOP);
        }
    }

    public void prev() {
        LLog.d(TAG, "prev", "called!");
        if (exoPlayer != null) {
            if (exoPlayer.hasPreviousWindow()) {
                exoPlayer.seekToPrevious();
            } else {
                Timeline currentTimeline = exoPlayer.getCurrentTimeline();
                int lastWindowIndex = currentTimeline.getLastWindowIndex(isShuffle);
                LLog.d(TAG, "prev", "lastWindowIndex: " + lastWindowIndex);
                exoPlayer.seekTo(lastWindowIndex, 0);
            }
        }
    }

    public void next() {
        LLog.d(TAG, "next", "called!");
        if (exoPlayer != null) {
            if (exoPlayer.hasNextWindow()) {
                exoPlayer.seekToNext();
            } else {
                Timeline currentTimeline = exoPlayer.getCurrentTimeline();
                int firstWindowIndex = currentTimeline.getFirstWindowIndex(isShuffle);
                LLog.d(TAG, "next", "firstWindowIndex: " + firstWindowIndex);
                exoPlayer.seekTo(firstWindowIndex, 0);
            }
        }
    }

    private void setPlayStatus(PlayStatus status) {
        curPlayStatus = status;
        for (MusicPlayListener listener : listeners) {
            listener.onPlayStatusChanged(curPlayStatus);

            // 추가된 부분: 미디어 세션의 상태 업데이트
            updateMediaSessionState();
        }
    }

    public PlayStatus getCurPlayStatus() {
        return curPlayStatus;
    }

    public RepeatMode getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(RepeatMode repeatMode) {
        this.repeatMode = repeatMode;
        exoPlayer.setRepeatMode(repeatMode.ordinal());
        SharedPrefManager.setData(SharedPrefManager.REPEAT_MODE, repeatMode.ordinal());
    }

    public boolean isShuffle() {
        return isShuffle;
    }

    public void setShuffle(boolean shuffle) {
        isShuffle = shuffle;
        exoPlayer.setShuffleModeEnabled(isShuffle);
        SharedPrefManager.setData(SharedPrefManager.IS_SHUFFLE, isShuffle);
    }

    public boolean isShowPronunciation() {
        return isShowPronunciation;
    }

    public void setShowPronunciation(boolean showPronunciation) {
        isShowPronunciation = showPronunciation;
        SharedPrefManager.setData(SharedPrefManager.IS_SHOW_PRONUNCIATION, showPronunciation);
    }

    public int getLyricLanguageIndex() {
        return lyricLanguageIndex;
    }

    public void setLyricLanguageIndex(int lyricLanguageIndex) {
        this.lyricLanguageIndex = lyricLanguageIndex;
        SharedPrefManager.setData(SharedPrefManager.LYRIC_LANGUAGE_INDEX, lyricLanguageIndex);
    }

    private void onPlayTimeChange(MusicPlayListener listener) {
        String playTime = PLAY_TIME_FORMAT.format(currentMilli);
        int progress = (int) (((float) currentMilli / (float) exoPlayer.getDuration()) * 100);
        listener.onPlayTimeChanged(playTime, progress);

        // 추가된 부분: 미디어 세션의 재생 위치 업데이트
        updateMediaSessionPlaybackPosition();
    }

    public void moveToPosition(int progress, boolean isMoving) {
        LLog.d(TAG, "moveToPosition", "progress: " + progress);
        if (exoPlayer != null && progress >= 0) {
            long duration = exoPlayer.getDuration();
            currentMilli = (long) ((float) duration / 100 * progress);
            //100단위 반올림
            currentMilli = (long) (Math.round(currentMilli / 100.0) * 100);
            LLog.d(TAG, "moveToPosition", "currentMilli: " + currentMilli);
            if (isMoving) {
                for (MusicPlayListener listener : listeners) {
                    onPlayTimeChange(listener);
                    listener.onPlayTimeMillis(currentMilli);
                    // 추가된 부분: 미디어 세션의 재생 위치 업데이트
                    updateMediaSessionPlaybackPosition();
                }
                return;
            }
            //if (curPlayStatus == PlayStatus.PLAY) {
                exoPlayer.seekTo(currentMilli);
            //}
        }
    }

    public void moveToPosition(long millis) {
        if (exoPlayer != null) {
            currentMilli = (millis / 1000) * 1000;
            for (MusicPlayListener listener : listeners) {
                onPlayTimeChange(listener);
                listener.onPlayTimeMillis(currentMilli);
            }
            //if (curPlayStatus == PlayStatus.PLAY) {
                exoPlayer.seekTo(currentMilli);
            //}
        }
    }

    public void callOnPlayTimeMillis() {
        for (MusicPlayListener listener : listeners) {
            listener.onPlayTimeMillis(currentMilli);
        }
    }

    private void callListeners() {
        for (MusicPlayListener listener : listeners) {
            listener.onSongInfoChanged(curSong);
            listener.onPlayStatusChanged(curPlayStatus);
            onPlayTimeChange(listener);
            listener.onPlayTimeMillis(currentMilli);
        }
    }

    public void release() {
        LLog.d(TAG, "release", "called!");
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer.removeListener(this);
            listeners = null;
            exoPlayer = null;
        }

        // 추가된 부분: MediaSessionCompat 해제
        if (mediaSession != null) {
            mediaSession.setActive(false);
            mediaSession.release();
            mediaSession = null;
        }
    }

    // 진행 상황 추적 시작
    private void startProgressTracking() {
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (exoPlayer.getDuration() >= currentMilli && listeners != null) {
                    for (MusicPlayListener listener : listeners) {
                        listener.onPlayTimeMillis(currentMilli + LYRIC_OFFSET);
                        if (currentMilli % 1000 == 0) {
                            onPlayTimeChange(listener);
                        }
                    }
                }
                progressHandler.postDelayed(this, PLAY_DELAY);
                currentMilli += PLAY_DELAY;
            }
        };
        progressHandler.post(progressRunnable);
    }

    private void stopProgressTracking() {
        LLog.d(TAG, "stopProgressTracking", "called!");
        progressHandler.removeCallbacks(progressRunnable);
        currentMilli = 0;
        stop();
        for (MusicPlayListener listener : listeners) {
            listener.onPlayTimeMillis(currentMilli);
            onPlayTimeChange(listener);
            listener.onPlayStatusChanged(curPlayStatus);
        }
    }

    @Override
    public void onPlaybackStateChanged(int playbackState) {
        LLog.d(TAG, "onPlaybackStateChanged", "playbackState :" + playbackState);
        switch (playbackState) {
            case Player.STATE_READY:
                //현재 곡 재생 중 prev() 시 현재곡 view 초기화
                LLog.d(TAG, "onPlaybackStateChanged", "exoPlayer.getCurrentPosition() : " + exoPlayer.getCurrentPosition());
                if (exoPlayer.getCurrentPosition() == 0) {
                    progressHandler.removeCallbacks(progressRunnable);
                    currentMilli = 0;
                    for (MusicPlayListener listener : listeners) {
                        listener.onPlayTimeMillis(currentMilli);
                    }
                    if (curPlayStatus == PlayStatus.PLAY) {
                        startProgressTracking();
                    }
                }
                // 추가된 부분: 미디어 세션의 상태 업데이트
                updateMediaSessionState();
                break;
            case Player.STATE_IDLE:
                stopProgressTracking();
                break;
            case Player.STATE_ENDED:
                stopProgressTracking();
                // 첫 곡으로
                next();
                break;
        }
    }

    @Override
    public void onPlayerError(PlaybackException error) {
        stopProgressTracking();
    }

    @Override
    public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
        Player.Listener.super.onMediaItemTransition(mediaItem, reason);
        if (mediaItem == null) {
            return;
        }
        LLog.d(TAG, "onMediaItemTransition", "mediaItem uri: " + mediaItem.playbackProperties.uri);
        progressHandler.removeCallbacks(progressRunnable);
        currentMilli = 0;
        setCurrentSong();
        if (curPlayStatus == PlayStatus.PLAY) {
            startProgressTracking();
        }

        // 추가된 부분: 미디어 세션의 상태 업데이트
        updateMediaSessionState();
    }

    // 추가된 부분: MediaSessionCompat 콜백 클래스
    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            play();
        }

        @Override
        public void onPause() {
            pause();
        }

        @Override
        public void onSkipToNext() {
            next();
        }

        @Override
        public void onSkipToPrevious() {
            prev();
        }

        // 여기 Seek 쪽인데 문제있어보임
        @Override
        public void onSeekTo(long pos) {

            if (exoPlayer != null) {
                exoPlayer.seekTo(pos);
                currentMilli = pos;
                for (MusicPlayListener listener : listeners) {
                    onPlayTimeChange(listener);
                    listener.onPlayTimeMillis(currentMilli);
                }
            }
        }
    }

    // 추가된 부분: 미디어 세션의 상태 업데이트
    private void updateMediaSessionState() {
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();

        // 재생 중인지 여부 설정
        int playbackState = curPlayStatus == PlayStatus.PLAY ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
        stateBuilder.setState(playbackState, exoPlayer.getCurrentPosition(), 1.0f);

        // 추가된 부분: 지원하는 액션 설정
        long actions = PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE |
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS | PlaybackStateCompat.ACTION_SEEK_TO;
        stateBuilder.setActions(actions);

        // 추가된 부분: 재생 가능한 위치 설정
        stateBuilder.setActiveQueueItemId(exoPlayer.getCurrentWindowIndex());

        // 추가된 부분: 미디어 세션에 상태 설정
        mediaSession.setPlaybackState(stateBuilder.build());
    }

    // 추가된 부분: 메서드 추가
    private void updateMediaSessionPlaybackPosition() {
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();

        // 재생 중인지 여부 설정
        int playbackState = curPlayStatus == PlayStatus.PLAY ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
        stateBuilder.setState(playbackState, exoPlayer.getCurrentPosition(), 1.0f);

        // 추가된 부분: 지원하는 액션 설정
        long actions = PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE |
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS | PlaybackStateCompat.ACTION_SEEK_TO;
        stateBuilder.setActions(actions);

        // 추가된 부분: 재생 가능한 위치 설정
        stateBuilder.setActiveQueueItemId(exoPlayer.getCurrentWindowIndex());
        // 추가된 부분: 이미지를 비트맵으로 변환 및 null 체크
        Uri albumImgUri = ContentsManager.getInstance().getAlbumImgCover();
        Bitmap coverImage = null;
        if (albumImgUri != null) {
            new LoadImageTask(albumImgUri, new LoadImageTask.Callback() {
                @Override
                public void onBitmapLoaded(Bitmap coverImage) {
                    // 로그
                    Log.d("NetworkStatus", "curSong.title : " + curSong.title);
                    Log.d("NetworkStatus", "curSong.singer : " + curSong.singer);
                    Log.d("NetworkStatus", "albumImgUri : " + albumImgUri);
                    Log.d("NetworkStatus", "albumImgUri.toString() : " + albumImgUri.toString());
                    // 추가된 부분: 현재 곡 정보 설정
                    MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, curSong.title)
                            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, curSong.singer)
                            .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, coverImage)
                            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, exoPlayer.getDuration()) // 총 재생시간
                            .build();

                    // 추가된 부분: 미디어 세션에 현재 곡 정보 설정
                    mediaSession.setMetadata(metadata);

                    // 추가된 부분: 미디어 세션에 상태 설정
                    mediaSession.setPlaybackState(stateBuilder.build());

                    // 추가된 부분: Notification에 앨범 아트워크 설정
                    notificationBuilder.setLargeIcon(coverImage);

                    // 추가된 부분: Notification 갱신
                    updateNotification();
                }
            }).execute();
        }
    }

    // 추가된 부분: Notification 채널 생성
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Media Playback",
                    NotificationManager.IMPORTANCE_LOW
            );
            notificationManager.createNotificationChannel(channel);
        }
    }

    // 추가된 부분: Notification에 액션에 따른 PendingIntent 생성
    private PendingIntent createPendingIntent(Context context, String action) {
        Intent intent = new Intent(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    // 추가된 부분: Notification에 기본 PendingIntent 생성
    private PendingIntent createPendingIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class); // 대체할 액티비티 클래스를 지정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    // 추가된 부분: Notification 갱신
    private void updateNotification() {

        // 추가된 부분: Notification을 NotificationManager에 다시 등록
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}