package com.omotion.contentsx.android.licon.ui.album.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.contents.ContentsManager;
import com.omotion.contentsx.android.licon.ui.album.adapter.AlbumMediaGridViewAdapter;
import com.omotion.contentsx.android.licon.ui.album.vo.AlbumMediaItem;
import com.omotion.contentsx.android.licon.ui.widget.ScrollableGridView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlbumMediaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumMediaFragment extends Fragment {

    private ScrollableGridView gridView;

    public static AlbumMediaFragment newInstance() {
        AlbumMediaFragment fragment = new AlbumMediaFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_media, container, false);

        gridView = v.findViewById(R.id.media_gridView);

        bindList();
        return v;
    }

    private void bindList() {
        AlbumMediaGridViewAdapter adapter = new AlbumMediaGridViewAdapter();
        List<Uri> mediaUriList = ContentsManager.getInstance().getUriListFromDirectory(ContentsManager.CARD_MEDIA_IMG);
        List<String> cardMediaCheckList = ContentsManager.getInstance().getAlbumInfoVO().getCardMediaImgCheck();
        List<String> cardYotubeTitleList = ContentsManager.getInstance().getAlbumInfoVO().getCardMediaYoutube();

        if (mediaUriList != null && mediaUriList.size() > 0) {
            for (int i = 0; i < mediaUriList.size(); i++) {
                String mediaUrlStr = "";
                String youtubeTitleStr = "";
                if (cardMediaCheckList.size() > i) {
                    mediaUrlStr = cardMediaCheckList.get(i);
                    youtubeTitleStr = cardYotubeTitleList.get(i);
                }
                adapter.addItem(new AlbumMediaItem(mediaUriList.get(i), mediaUrlStr, youtubeTitleStr, -1));
            }
        }
        gridView.setAdapter(adapter);

    }
}