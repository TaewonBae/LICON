package com.omotion.contentsx.android.licon.ui.player.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.core.player.MusicPlayer;
import com.omotion.contentsx.android.licon.core.util.LLog;
import com.omotion.contentsx.android.licon.core.util.Utils;
import com.omotion.contentsx.android.licon.ui.player.vo.LyricsItem;

import java.util.List;

public class LyricsRecyclerViewAdapter extends RecyclerView.Adapter<LyricsRecyclerViewHolder> {
    private List<LyricsItem> itemList;
    Context context;

    public LyricsRecyclerViewAdapter() {

    }

    public void setList(List<LyricsItem> itemList_) {
        itemList = itemList_;
    }

    public List<LyricsItem> getList() {
        return itemList;
    }

    @Override
    public LyricsRecyclerViewHolder onCreateViewHolder(ViewGroup a_viewGroup, int a_viewType) {
        View view = LayoutInflater.from(a_viewGroup.getContext()).inflate(R.layout.lyrics_item, a_viewGroup, false);
        context = view.getContext();
        return new LyricsRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LyricsRecyclerViewHolder viewHolder, int position) {
        final LyricsItem item = itemList.get(position);
        Typeface tfRegular = ResourcesCompat.getFont(context, R.font.suitregular);
        Typeface tfBold = ResourcesCompat.getFont(context, R.font.suitbold);
        viewHolder.tvLyric.setTypeface(item.isFocused() ? tfBold : tfRegular);
        viewHolder.tvLyric.setText(item.getLyric());

        // 글꼴 색상을 설정 이거 제가 추가한 코드인데 (에러는 안나는데 혹시나해서 추가해놓습니다.) 쉔쉐이
        int textColor = ContextCompat.getColor(context, item.isFocused() ? R.color.main_color : R.color.lyrics_color);
        viewHolder.tvLyric.setTextColor(textColor);

        viewHolder.tvLyric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LLog.d("dd", "onClick", "timeline: " + item.getTimeLine());
                long selectedTimeLine = Utils.timeFormatToLong(item.getTimeLine());
                MusicPlayer.getInstance().moveToPosition(selectedTimeLine);
            }
        });
        /*viewHolder.tvPronun.setTypeface(item.isFocused() ? tfBold : tfRegular);
        if (item.getPronun() != null && !"".equals(item.getPronun())) {
            viewHolder.tvPronun.setVisibility(View.VISIBLE);
            viewHolder.tvPronun.setText(item.getPronun());
        } else {
            viewHolder.tvPronun.setVisibility(View.GONE);
        }*/
    }

    @Override
    public int getItemCount() {
        if (itemList != null) {
            return itemList.size();
        }
        return 0;
    }
}