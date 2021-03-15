package com.example.ohjeom.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ohjeom.R;
import com.example.ohjeom.models.DayInfo;

import java.util.ArrayList;

public class WeekAdapter extends BaseAdapter {
    private ArrayList<String> mWeekList;
    private Context mContext;
    private int mResource;
    private LayoutInflater mLiInflater;

    public WeekAdapter(Context context, int textResource, ArrayList<String> weekList)
    {
        this.mContext = context;
        this.mWeekList = weekList;
        this.mResource = textResource;
        this.mLiInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return mWeekList.size();
    }

    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return mWeekList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        String week = mWeekList.get(position);
        WeekViewHolder weekViewHolder;

        if(convertView == null)
        {
            convertView = mLiInflater.inflate(mResource, null);

            weekViewHolder = new WeekViewHolder();

            weekViewHolder.llBackground = (LinearLayout)convertView.findViewById(R.id.week_cell_ll_background);
            weekViewHolder.tvWeek = (TextView) convertView.findViewById(R.id.calendar_week);

            convertView.setTag(weekViewHolder);
        }
        else
        {
            weekViewHolder = (WeekViewHolder) convertView.getTag();
        }

        if(week != null)
        {
            weekViewHolder.tvWeek.setText(week);
        }

        return convertView;
    }

    public class WeekViewHolder
    {
        public LinearLayout llBackground;
        public TextView tvWeek;
    }

}

