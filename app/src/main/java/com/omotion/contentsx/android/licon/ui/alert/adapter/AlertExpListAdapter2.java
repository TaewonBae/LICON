package com.omotion.contentsx.android.licon.ui.alert.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.omotion.contentsx.android.licon.R;
import com.omotion.contentsx.android.licon.ui.alert.vo.AlertChildItem2;
import com.omotion.contentsx.android.licon.ui.alert.vo.AlertParentItem2;

import java.util.ArrayList;

public class AlertExpListAdapter2 extends BaseExpandableListAdapter {
    public ArrayList<AlertParentItem2> parentItems = new ArrayList<AlertParentItem2>(); //부모 리스트를 담을 배열

    public void addItem(AlertParentItem2 item) {
        parentItems.add(item);
    }

    public ArrayList<ArrayList<AlertChildItem2>> childItems; //자식 리스트를 담을 배열

    @Override
    public int getGroupCount() {
        return parentItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childItems.get(groupPosition).size();
    }

    //리스트 아이템 반환
    @Override
    public AlertParentItem2 getGroup(int groupPosition) {
        return parentItems.get(groupPosition);
    }

    @Override
    public AlertChildItem2 getChild(int groupPosition, int childPosition) {
        return childItems.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //동일한 id가 항상 동일한 개체를 참조하는지 여부를 반환
    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = convertView;
        Context context = parent.getContext();

        //convertView가 비어있을 경우 xml파일을 inflate 해줌
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.exp_parent_item2, parent, false);
        }

        //View들은 반드시 아이템 레이아웃을 inflate한 뒤에 작성할 것
        ImageView arrowIcon = (ImageView) v.findViewById(R.id.arrow);
        TextView title1 = (TextView) v.findViewById(R.id.title1);
        TextView title2 = (TextView) v.findViewById(R.id.title2);

        //그룹 펼쳐짐 여부에 따라 아이콘 변경
        if (isExpanded)
            arrowIcon.setImageResource(R.drawable.ic_notice_up);
        else
            arrowIcon.setImageResource(R.drawable.ic_notice_down);

        //리스트 아이템의 내용 설정
        title1.setText(getGroup(groupPosition).getTitle1());
        title2.setText(getGroup(groupPosition).getTitle2());


        return v;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v = convertView;
        Context context = parent.getContext();

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.exp_child_item2, parent, false);
        }

        //리스트 아이템의 내용 설정
        TextView detail_text = (TextView) v.findViewById(R.id.detail_text);
        detail_text.setText(getChild(groupPosition, childPosition).getDetailText());
        return v;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    //리스트에 새로운 아이템을 추가
    public void addItem(int groupPosition, AlertChildItem2 item) {
        childItems.get(groupPosition).add(item);
    }
}
