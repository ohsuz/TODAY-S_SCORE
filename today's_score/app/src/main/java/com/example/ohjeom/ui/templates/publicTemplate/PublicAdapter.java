package com.example.ohjeom.ui.templates.publicTemplate;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ohjeom.R;
import com.example.ohjeom.models.Location;
import com.example.ohjeom.ui.templates.PublicTemplateActivity;

import java.io.Serializable;
import java.util.ArrayList;

public class PublicAdapter extends RecyclerView.Adapter<PublicAdapter.CustomViewHolder> {

    private String name;
    private String[] components;
    private int wakeupHour, wakeupMin;
    private int walkHour, walkMin, walkCount;
    private int sleepHour, sleepMin;
    private String[] appNames;
    private int startHour, startMin, stopHour, stopMin;
    private ArrayList<Location> locations = new ArrayList<>();
    private int money;
    private ArrayList<String> mList;

    public PublicAdapter(ArrayList<String> mlist) {
        this.mList = mlist;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView textView;

        CustomViewHolder(View itemView){
            super(itemView);

            textView = itemView.findViewById(R.id.template_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();

                    if(pos == 0) {
                        name = mList.get(pos);
                        components = new String[]{"true", "true", "true", "true", "false", "true"};
                        wakeupHour = 7;
                        wakeupMin = 0;
                        sleepHour = 0;
                        sleepMin = 0;
                        walkHour = 12;
                        walkMin = 0;
                        walkCount = 10000;
                        startHour = 13;
                        startMin = 0;
                        stopHour = 18;
                        stopMin = 0;
                        appNames = new String[]{"com.google.android.googlequicksearchbox","com.google.android.youtube"};
                        money = 50000;
                    }

                    if(pos == 1) {
                        name = mList.get(pos);
                        components = new String[]{"true", "true", "true", "true", "false", "true"};
                        wakeupHour = 18;
                        wakeupMin = 0;
                        sleepHour = 10;
                        sleepMin = 0;
                        walkHour = 0;
                        walkMin = 0;
                        walkCount = 7000;
                        startHour = 20;
                        startMin = 0;
                        stopHour = 4;
                        stopMin = 0;
                        appNames = new String[]{"com.google.android.youtube"};
                        money = 70000;
                    }

                    if(pos == 2) {
                        name = mList.get(pos);
                        components = new String[]{"false", "false", "true", "false", "false", "false"};
                        walkHour = 22;
                        walkMin = 0;
                        walkCount = 15000;
                    }

                    if(pos == 3) {
                        name = mList.get(pos);
                        components = new String[]{"true", "true", "false", "true", "false", "false"};
                        wakeupHour = 7;
                        wakeupMin = 0;
                        sleepHour = 23;
                        sleepMin = 0;
                        startHour = 8;
                        startMin = 0;
                        stopHour = 22;
                        stopMin = 0;
                        appNames = new String[]{"com.google.android.youtube"};
                    }

                    if(pos == 4) {
                        name = mList.get(pos);
                        components = new String[]{"false", "false", "false", "false", "false", "true"};
                        money = 30000;
                    }

                    if(pos == 5) {
                        name = mList.get(pos);
                        components = new String[]{"true", "true", "false", "false", "false", "false"};
                        wakeupHour = 8;
                        wakeupMin = 0;
                        sleepHour = 0;
                        sleepMin = 0;
                    }
                    Intent intent = new Intent(view.getContext(), PublicTemplateActivity.class);

                    intent.putExtra("name",name);
                    intent.putExtra("components",components);
                    intent.putExtra("wakeupHour",wakeupHour);
                    intent.putExtra("wakeupMin",wakeupMin);
                    intent.putExtra("sleepHour",sleepHour);
                    intent.putExtra("sleepMin",sleepMin);
                    intent.putExtra("walkHour",walkHour);
                    intent.putExtra("walkMin",walkMin);
                    intent.putExtra("locations",locations);
                    intent.putExtra("walkCount",walkCount);
                    intent.putExtra("startHour",startHour);
                    intent.putExtra("startMin",startMin);
                    intent.putExtra("stopHour",stopHour);
                    intent.putExtra("stopMin",stopMin);
                    intent.putExtra("appNames",appNames);
                    intent.putExtra("money",money);

                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_template_public, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        viewholder.textView.setText(mList.get(position));
        viewholder.textView.setTextColor(Color.WHITE);

        if(position == 0) {
            viewholder.textView.setTextSize(40);
            viewholder.textView.setBackgroundResource(R.drawable.morning);
        }

        if(position == 1) {
            viewholder.textView.setTextSize(35);
            viewholder.textView.setBackgroundResource(R.drawable.night);
        }

        if(position == 2) {
            viewholder.textView.setBackgroundResource(R.drawable.running);
        }

        if(position == 3) {
            viewholder.textView.setBackgroundResource(R.drawable.study);
        }

        if(position == 4) {
            viewholder.textView.setBackgroundResource(R.drawable.money);
        }

        if(position == 5) {
            viewholder.textView.setTextSize(30);
            viewholder.textView.setBackgroundResource(R.drawable.sleep);
        }

        Drawable alpha = viewholder.textView.getBackground();
        alpha.setAlpha(170);
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }
}