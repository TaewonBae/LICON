package com.omotion.contentsx.android.licon.ui.album.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.contents.ContentsManager;
import com.omotion.contentsx.android.licon.data.remote.model.AlbumInfoVO;
import com.omotion.contentsx.android.licon.ui.album.adapter.AlbumTrackRecyclerViewAdapter;
import com.omotion.contentsx.android.licon.ui.album.vo.AlbumTrackItem;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlbumTrackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumTrackFragment extends Fragment {

    private RecyclerView recyclerView;

    private AlbumInfoVO albumItem;

    public static AlbumTrackFragment newInstance() {
        AlbumTrackFragment fragment = new AlbumTrackFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_track, container, false);
        // Recycler view
        recyclerView = v.findViewById(R.id.track_recyclerview);
        //RecyclerView 데이터 적용 함수

        System.out.println("albumItem 호출 직전");
        albumItem = ContentsManager.getInstance().getAlbumInfoVO();
        System.out.println("albumItem2 ::: " + albumItem);
        bindList();

        return v;
    }

    private void bindList() {
        // List item 생성
        List<AlbumTrackItem> itemList = new ArrayList<>();

        for (int i = 0; i < albumItem.getAlbumURL().size(); i++) {
            System.out.println("albumItem.getAlbumURL().get" + i + albumItem.getAlbumURL().get(i));
            itemList.add(new AlbumTrackItem(i + 1, albumItem.getTrackTitle().get(i), albumItem.getAlbumMusicInfo().get(i), albumItem.getTrackSinger().get(i), i == 0 ? true : false));
        }
        // Adapter 추가
        AlbumTrackRecyclerViewAdapter adapter = new AlbumTrackRecyclerViewAdapter(itemList);
        recyclerView.setAdapter(adapter);

        // Layout manager 추가
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    }
}