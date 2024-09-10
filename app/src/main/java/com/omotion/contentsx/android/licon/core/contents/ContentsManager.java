package com.omotion.contentsx.android.licon.core.contents;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.nfc.NfcManager;
import com.omotion.contentsx.android.licon.core.player.MusicPlayer;
import com.omotion.contentsx.android.licon.core.util.LLog;
import com.omotion.contentsx.android.licon.data.remote.model.AlbumInfoVO;
import com.omotion.contentsx.android.licon.data.remote.model.Song;
import com.omotion.contentsx.android.licon.ui.MainActivity;
import com.omotion.contentsx.android.licon.ui.album.activity.MyAlbumActivity;
import com.omotion.contentsx.android.licon.ui.widget.ProgressDownloadDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ContentsManager {
    private final String TAG = this.getClass().getName();
    private final String ALBUMS = "albums";
    private final String ALBUM_1 = "album_1";
    private final String ALBUM_1_JSON = "album_1.json";
    //    private final String ALBUM_TITLE_IMG = "albumTitleImg";
    private final String PHOTO_IMG_BACK = "photoImgback";

    private final String ALBUM_IMG_TITLE = "albumImgtitle";

    private final String ALBUM_IMG_CORVER = "albumImgcorver";

    public final String ALBUM_URL = "albumURL";
    public final String ALBUM_URL_MP3 = "albumURLMp3";
    public static final String CARD_PHOTO_IMG = "cardPhotoImg";
    public static final String CARD_MEDIA_IMG = "cardMediaImg";

    private static ContentsManager instance = null;
    private ContentsManagerListener listener = null;
    private AlbumInfoVO albumInfoVO = null;
    private FirebaseFirestore fireStore = null;
    private StorageReference storageReference = null;
    private File mParentFile = null;
    private ProgressDownloadDialog progressDownloadDialog;

    public static ContentsManager getInstance() {
        if (instance == null) {
            instance = new ContentsManager();
        }
        return instance;
    }

    public void init(Context context) {
        fireStore = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mParentFile = context.getExternalFilesDir(ALBUMS);
        setProgressContext(context);
        setContents();
    }

    public void setProgressContext(Context context) {
        progressDownloadDialog = new ProgressDownloadDialog(context);
        progressDownloadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }

    public void addListener(ContentsManagerListener listener) {
        this.listener = listener;
    }

    private void setContents() {
        // check ablum_1.json
        String path = new File(mParentFile, ALBUM_1 + "/" + ALBUM_1_JSON).getPath();
        LLog.d(TAG, "setContents", "path: " + path);

        albumInfoVO = readFromFile(path, AlbumInfoVO.class);
        if (albumInfoVO == null) {
            return;
        }
        HashMap<String, ArrayList<DownloadItem>> downList = toDownloadList(albumInfoVO);
        if (downList.size() > 0) {
            return;
        }
        onPassedCheck();
    }

    public void processCheckDownload() {
        if (albumInfoVO != null) {
            HashMap<String, ArrayList<DownloadItem>> downList = toDownloadList(albumInfoVO);
            LLog.d(TAG, "processCheckDownload", downList.toString());
            if (downList.size() > 0) {
                download(downList);
            } else {
                onPassedCheck();
            }
        } else {
            setAlbum();
        }
    }

    private void setAlbum() {
        String path = new File(mParentFile, ALBUM_1 + "/" + ALBUM_1_JSON).getPath();
        fireStore.collection("database/v1/Album")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                LLog.d(TAG, "onComplete", "toObject ::: => " + document.toObject(AlbumInfoVO.class));
                                createFileInExternalStorage(ALBUM_1, ALBUM_1_JSON);
                                writeToFile(document.toObject(AlbumInfoVO.class), path);
                                albumInfoVO = readFromFile(path, AlbumInfoVO.class);
                                processCheckDownload();
                            }
                        } else {
                            LLog.w(TAG, "onComplete", "Error => " + task.getException());
                        }
                    }
                });
    }

    public HashMap<String, ArrayList<DownloadItem>> toDownloadList(AlbumInfoVO vo) {
        HashMap<String, ArrayList<DownloadItem>> map = new LinkedHashMap<>();
        //Media 파일
        ArrayList<DownloadItem> mediaList = new ArrayList<>();
        //check cardMediaImg
        if (getUriListFromDirectory(CARD_MEDIA_IMG) == null && vo.getCardMediaImg() != null) {
            for (String downPath : vo.getCardMediaImg()) {
                mediaList.add(new DownloadItem(downPath, createFileInExternalStorage(CARD_MEDIA_IMG, getFileName(downPath))));
            }
        }
        //check cardPhotoImgBack
        if (getPhotoImg(PHOTO_IMG_BACK) == null && vo.getCardPhotoImgBack() != null) {
            String downPath = vo.getCardPhotoImgBack();
            mediaList.add(new DownloadItem(downPath, createFileInExternalStorage(PHOTO_IMG_BACK, getFileName(downPath))));
        }


        //check albumImgTitle
        if (getPhotoImg(ALBUM_IMG_TITLE) == null && vo.getAlbumImgTitle() != null) {
            String downPath = vo.getAlbumImgTitle();
            mediaList.add(new DownloadItem(downPath, createFileInExternalStorage(ALBUM_IMG_TITLE, getFileName(downPath))));
        }


        //check albumImgCover
        if (getPhotoImg(ALBUM_IMG_CORVER) == null && vo.getAlbumImgCover() != null) {
            String downPath = vo.getAlbumImgCover();
            mediaList.add(new DownloadItem(downPath, createFileInExternalStorage(ALBUM_IMG_CORVER, getFileName(downPath))));
        }


        if (mediaList.size() > 0) {
            map.put("Media", mediaList);
        }

        //Photo 파일
        ArrayList<DownloadItem> photoList = new ArrayList<>();
        //check cardPhotoImg
        if (getUriListFromDirectory(CARD_PHOTO_IMG) == null && vo.getCardPhotoImg() != null) {
            for (String downPath : vo.getCardPhotoImg()) {
                photoList.add(new DownloadItem(downPath, createFileInExternalStorage(CARD_PHOTO_IMG, getFileName(downPath))));
            }
        }
        if (photoList.size() > 0) {
            map.put("Photo", photoList);
        }
        //mp3 파일
        ArrayList<DownloadItem> mp3List = new ArrayList<>();
        //check albumTitleImg
        /*if (getPhotoImg(ALBUM_TITLE_IMG) == null) {
            String downPath = vo.getAlbumTitleImg();
            mp3List.add(new DownloadItem(downPath, createFileInExternalStorage(ALBUM_TITLE_IMG, getFileName(downPath))));

        }*/
        //check albumURL
        if (getUriListFromDirectory(ALBUM_URL) == null && vo.getAlbumURL() != null) {
            for (String downPath : vo.getAlbumURL()) {
                mp3List.add(new DownloadItem(downPath, createFileInExternalStorage(ALBUM_URL, getFileName(downPath))));
            }
        }
        //check albumURLMp3
        if (getUriListFromDirectory(ALBUM_URL_MP3) == null && vo.getAlbumURLMp3() != null) {
            for (String downPath : vo.getAlbumURLMp3()) {
                mp3List.add(new DownloadItem(downPath, createFileInExternalStorage(ALBUM_URL_MP3, getFileName(downPath))));
            }
        }
        if (mp3List.size() > 0) {
            map.put("mp3", mp3List);
        }

        return map;
    }

    public AlbumInfoVO getAlbumInfoVO() {
        return albumInfoVO;
    }

    public Uri getPhotoImg(String path) {
        File dir = new File(mParentFile, path);
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            LLog.d(TAG, "getPhotoImg", "Files Size: " + files.length);
            for (File file : files) {
                LLog.d(TAG, "getPhotoImg", "FileName:" + file.getName());
                return Uri.fromFile(file);
            }
        }
        return null;
    }

    /*public Uri getAlbumTitleImgUri() {
        return getPhotoImg(ALBUM_TITLE_IMG);
    }*/

    public Uri getPhotoImgBack() {
        return getPhotoImg(PHOTO_IMG_BACK);
    } // 포토카드 뒷면

    public Uri getAlbumImgTitle() {
        return getPhotoImg(ALBUM_IMG_TITLE);
    } // 앨범 이미지

    public Uri getAlbumImgCover() { // 앨범 커버 이미지
        return getPhotoImg(ALBUM_IMG_CORVER);
    } // 앨범 커버 이미지

    // Uri 경로에 있는 이미지를 Bitmap으로 변경 (미디어 세션에 이미지 할당을 위한 코드)
    public Bitmap getBitmapFromLocalFile(String filePath) {
        try {
            File file = new File(new URI(filePath));
            if (file.exists()) {
                // 파일이 존재하는 경우 파일로부터 Bitmap 생성
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    return ImageDecoder.decodeBitmap(ImageDecoder.createSource(file));
                } else {
                    return BitmapFactory.decodeFile(file.getAbsolutePath());
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public File getParentFile() {
        return mParentFile;
    }
    Activity context;
    public void download(HashMap<String, ArrayList<DownloadItem>> map) {
        progressDownloadDialog.show();
        List<FileDownloadTask> tasks = new ArrayList<>();
        StorageReference islandRef;

        for (String key : map.keySet()) {
            ArrayList<DownloadItem> items = map.get(key);
            for (DownloadItem item : items) {
                islandRef = storageReference.child(item.downloadUrl);
                FileDownloadTask task = islandRef.getFile(item.file);
                task.addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
//                        Resources resources = context.getResources(); // context는 이전에 얻은 Context 객체
//                        String message = resources.getString(R.string.download_file); // 파일 다운로드 중 //안먹힘
                        progressDownloadDialog.setDownloadText(key + " File Download..."); //"mp3" + "파일 다운로드 중"
                        progressDownloadDialog.setProgressText((items.indexOf(item) + 1) + " / " + items.size());
                    }
                });
                tasks.add(task);
            }
        }
        Tasks.whenAll(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        LLog.d(TAG, "onComplete", "task: " + task.toString());
                        NfcManager.getInstance().RegistrationTrue();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        LLog.d(TAG, "onSuccess", "called");
                        progressDownloadDialog.dismiss();
                        onPassedCheck();
                        setContentsPassResult(true);
                        MainActivity.setAlbumItemViewRegist(true);
                        MyAlbumActivity.setAlbumItemViewRegist(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        LLog.d(TAG, "onFailure", e.toString());
                        progressDownloadDialog.dismiss();
                        if (listener != null) {
                            listener.onContentsPassResult(false);
                        }
                    }
                });
    }


    private void onPassedCheck() {
        LLog.d(TAG, "onPassedCheck", "called!");
        if (!MusicPlayer.getInstance().isSongsPrepared()) {
            // set music player songs
            MusicPlayer.getInstance().setSongs(setSongList());
        }
    }

    public void setContentsPassResult(boolean isPass) {
        if (listener != null) {
            listener.onContentsPassResult(isPass);
        }
    }

    private List<Song> setSongList() {
        List<Song> songList = new ArrayList<>();
        List<String> trackTitles = albumInfoVO.getTrackTitle();
        File[] albumUrlFiles = getFileListFromDirectory(ALBUM_URL);
        File[] albumMp3Files = getFileListFromDirectory(ALBUM_URL_MP3);

        if (trackTitles == null || trackTitles.size() == 0
                || albumUrlFiles == null || albumUrlFiles.length == 0
                || albumMp3Files == null || albumMp3Files.length == 0) {
            return null;
        }
        for (String trackTitle : trackTitles) {
            for (File urlFile : albumUrlFiles) {
                Song song = readFromFile(urlFile.getPath(), Song.class);
                String song_ = urlFile.getName().replaceFirst("[.][^.]+$", "");
                if (song == null) {
                    break;
                }
                if (trackTitle.equalsIgnoreCase(song.title)) {
                    //uri 추가
                    for (File mp3File : albumMp3Files) {
                        String fileName = mp3File.getName().replaceFirst("[.][^.]+$", "");
                        fileName = Normalizer.normalize(fileName, Normalizer.Form.NFC);
                        song.title = Normalizer.normalize(song.title, Normalizer.Form.NFC);
                        if (fileName.equalsIgnoreCase(song_)) {
                            LLog.d(TAG, "setSongList", "fileName: " + fileName + ", file: " + mp3File.getPath());
                            song.fileUri = Uri.fromFile(mp3File);
                            song.imageResId = R.drawable.album_img;
                        }
                    }
                    songList.add(song);
                }
            }
        }
        for (Song s : songList) {
            LLog.d(TAG, "setSongList", "title: " + s.title + ", uri: " + s.fileUri);
        }
        return songList;
    }


    public List<Uri> getUriListFromDirectory(String dirPath) {
        List<Uri> uriList = new ArrayList<>();
        File[] files = getFileListFromDirectory(dirPath);
        if (files != null && files.length > 0) {
            LLog.d(TAG, "getUriListFromDirectory", "Files Size: " + files.length);
            for (File file : files) {
                LLog.d(TAG, "getUriListFromDirectory", "Files FileName:" + file.getName());
                uriList.add(Uri.fromFile(file));
            }
            return uriList;
        } else {
            return null;
        }
    }

    public File[] getFileListFromDirectory(String path) {
        File dir = new File(mParentFile, path);
        return dir.listFiles();
    }

    private File createFileInExternalStorage(String folderName, String fileName) {
        File file;
        File storageDir = new File(mParentFile, folderName);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        file = new File(storageDir, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return file;
    }

    private String getFileName(String path) {
        int index = path.lastIndexOf("/");
        return path.substring(index + 1);
    }

    private <T> T readFromFile(String filename, Class<T> clz) throws JsonSyntaxException {
        Gson gson = new Gson();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            return gson.fromJson(reader, clz);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    private void writeToFile(Object clz, String path) {
        try (Writer writer = new FileWriter(path)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(clz, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
