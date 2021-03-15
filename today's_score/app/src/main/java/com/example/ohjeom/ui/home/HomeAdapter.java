package com.example.ohjeom.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ohjeom.R;
import com.example.ohjeom.models.ListViewItem;
import com.example.ohjeom.models.Score;
import com.example.ohjeom.retrofit.ScoreFunctions;

import java.util.ArrayList;

public class HomeAdapter extends BaseAdapter {
    private ArrayList<Integer> scores = new ArrayList<Integer>();
    private ArrayList<String> tests = new ArrayList<String>();

    public HomeAdapter(){
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_test, parent, false);
        }

        TextView testName = (TextView) convertView.findViewById(R.id.test) ;
        TextView scoreText = (TextView) convertView.findViewById(R.id.score) ;

        testName.setText(tests.get(position));
        if (scores.get(position) == -1) {
            scoreText.setText("미채점");
        } else {
            scoreText.setText(scores.get(position)+" 점");
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return scores.size();
    }

    @Override
    public Object getItem(int position) {
        return scores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addScore(String test, int score) {
        tests.add(test);
        scores.add(score);
    }

    public void updateScore(String test, int score) {
        int index = tests.indexOf(test);
        scores.set(index, score);
    }
}
