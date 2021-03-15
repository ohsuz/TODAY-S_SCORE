package com.example.ohjeom.ui.friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ohjeom.R;
import com.example.ohjeom.models.FriendScore;

import java.util.ArrayList;

public class FriendScoreAdapter extends BaseAdapter {
    private ArrayList<FriendScore> friendScore;

    public FriendScoreAdapter(ArrayList<FriendScore> friendScore) {
        this.friendScore = friendScore;
    }

    @Override
    public int getCount() {
        return friendScore.size();
    }

    @Override
    public Object getItem(int i) {
        return friendScore.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_friendscore, parent, false);
        }

        TextView testName = (TextView) convertView.findViewById(R.id.test) ;
        TextView score = (TextView) convertView.findViewById(R.id.score) ;

        if(friendScore.get(i).getComponent() == 0) {
            testName.setText("기상 검사");
        } else if(friendScore.get(i).getComponent() == 1) {
            testName.setText("수면 검사");
        } else if(friendScore.get(i).getComponent() == 2) {
            testName.setText("걸음수 검사");
        } else if(friendScore.get(i).getComponent() == 3) {
            testName.setText("핸드폰 사용량 검사");
        } else if(friendScore.get(i).getComponent() == 4) {
            testName.setText("장소 도착 검사");
        } else if(friendScore.get(i).getComponent() == 5) {
            testName.setText("소비 검사");
        }

        score.setText(Integer.toString(friendScore.get(i).getScore())+"점");

        return convertView;
    }
}
