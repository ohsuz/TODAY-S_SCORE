package com.example.ohjeom.ui.statistics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ohjeom.R;
import com.example.ohjeom.models.Details;
import com.example.ohjeom.models.FriendScore;
import com.example.ohjeom.models.Score;

import java.util.ArrayList;

public class DetailsAdapter extends BaseAdapter {
    private ArrayList<Details> details;

    public DetailsAdapter(ArrayList<Details> details) {
        this.details = details;
    }

    @Override
    public int getCount() {
        return details.size();
    }

    @Override
    public Object getItem(int i) {
        return details.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_details, parent, false);
        }

        TextView testName = (TextView) convertView.findViewById(R.id.test) ;
        TextView score = (TextView) convertView.findViewById(R.id.score) ;

        testName.setText(details.get(i).getComponent());
        score.setText(details.get(i).getScore()+"점");

        return convertView;
    }
}
