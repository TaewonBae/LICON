package com.omotion.contentsx.android.licon.ui.album.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.contents.ContentsManager;
import com.omotion.contentsx.android.licon.ui.album.adapter.AlbumPhotoGridViewAdapter;
import com.omotion.contentsx.android.licon.ui.album.vo.AlbumPhotoItem;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlbumPhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumPhotoFragment extends Fragment {

    private GridView gridView;

    public static AlbumPhotoFragment newInstance() {
        AlbumPhotoFragment fragment = new AlbumPhotoFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo, container, false);
        gridView = v.findViewById(R.id.photo_gridView);

        bindList();
        return v;
    }

    private void bindList() {
        AlbumPhotoGridViewAdapter adapter = new AlbumPhotoGridViewAdapter();
        List<Uri> photoUriList = ContentsManager.getInstance().getUriListFromDirectory(ContentsManager.CARD_PHOTO_IMG);
        if (photoUriList != null && photoUriList.size() > 0) {
            for (int i = 0; i < photoUriList.size(); i++) {
                adapter.addItem(new AlbumPhotoItem(photoUriList.get(i), i));
            }
        }
        gridView.setAdapter(adapter);

    }
}