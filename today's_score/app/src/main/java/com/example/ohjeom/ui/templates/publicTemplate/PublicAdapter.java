package com.example.ohjeom.ui.templates.publicTemplate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ohjeom.R;

import java.util.ArrayList;

public class PublicAdapter extends RecyclerView.Adapter<PublicAdapter.CustomViewHolder> {

    ArrayList<String> mList;

    public PublicAdapter(ArrayList<String> mlist) {
        this.mList = mlist;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView textView;

        CustomViewHolder(View itemView){
            super(itemView);

            textView = itemView.findViewById(R.id.template_name);
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

        if(position == 0) {
            viewholder.textView.setTextSize(40);
        }

        if(position == 1) {
            viewholder.textView.setTextSize(35);
        }

        if(position == 5) {
            viewholder.textView.setTextSize(30);
        }
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }
}