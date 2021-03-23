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
import java.util.Calendar;
import java.util.Date;

public class CalendarAdapter extends BaseAdapter {
    public Date selectedDate;
    private ArrayList<DayInfo> mDayList;
    private Context mContext;
    private int mResource;
    private LayoutInflater mLiInflater;

    public CalendarAdapter(Context context, int textResource, ArrayList<DayInfo> dayList, Date date)
    {
        this.mContext = context;
        this.mDayList = dayList;
        this.mResource = textResource;
        this.mLiInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.selectedDate = date;
    }

    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return mDayList.size();
    }

    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return mDayList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        DayInfo day = mDayList.get(position);

        if(convertView == null)
        {
            convertView = mLiInflater.inflate(mResource, null);
        }

        LinearLayout llBackground = (LinearLayout)convertView.findViewById(R.id.day_cell_ll_background);
        TextView tvDay = (TextView) convertView.findViewById(R.id.calendar_day);

        if(day != null)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(day.getDate());
            tvDay.setText(Integer.toString(calendar.get(Calendar.DATE)));

            if(day.isSameDay(selectedDate)) {
                tvDay.setTextColor(0xFFFF4F19);
            }
            else {
                if (day.isInMonth()) {
                    tvDay.setTextColor(0xFF5A5A5A);
                } else {
                    tvDay.setTextColor(Color.GRAY);
                }
            }

            if(0<= day.getAverageScore() && day.getAverageScore()<=25) {
                tvDay.setBackgroundResource(R.drawable.rounded);
            }
            else if (25< day.getAverageScore() && day.getAverageScore()<=50) {
                tvDay.setBackgroundResource(R.drawable.rounded2);
            }
            else if (50<= day.getAverageScore() && day.getAverageScore()<=75) {
                tvDay.setBackgroundResource(R.drawable.rounded3);
            }
            else {
                tvDay.setBackgroundResource(R.drawable.rounded4);
            }
        }
        convertView.setTag(day);

        return convertView;
    }

}