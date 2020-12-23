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
import com.example.ohjeom.models.Test;

import java.util.ArrayList;

public class HomeAdapter extends BaseAdapter {
    private ArrayList<Test> tests = new ArrayList<Test>();

    public HomeAdapter(){
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_test, parent, false);
        }

        TextView testName = (TextView) convertView.findViewById(R.id.test) ;
        TextView score = (TextView) convertView.findViewById(R.id.score) ;
        Test test = tests.get(position);


        testName.setText(test.getTestName());
        score.setText(test.getScore());

        return convertView;
    }

    @Override
    public int getCount() {
        return tests.size();
    }

    @Override
    public Object getItem(int position) {
        return tests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addTest(Test test) {
        tests.add(test);
    }
}
