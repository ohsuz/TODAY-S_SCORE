package com.example.ohjeom.adapters;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ohjeom.R;

import java.util.ArrayList;
import java.util.List;

public class AppCheckAdapter extends RecyclerView.Adapter<AppCheckAdapter.ViewHolder>{
    private List<ResolveInfo> items;
    private PackageManager pm;

    public AppCheckAdapter(PackageManager pm,List<ResolveInfo> items){
        this.items = items;
        this.pm =pm;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;

        ViewHolder(View itemView){
            super(itemView);

            textView = itemView.findViewById(R.id.appcheck_name);
            imageView = itemView.findViewById(R.id.appcheck_icon);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_appcheck, parent, false) ;
        AppCheckAdapter.ViewHolder vh = new AppCheckAdapter.ViewHolder(view) ;

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResolveInfo resolveinfo = items.get(position);

        String appName = items.get(position).activityInfo.loadLabel(pm).toString();
        Drawable appIcon = items.get(position).activityInfo.loadIcon(pm);
        holder.textView.setText(appName);
        holder.imageView.setImageDrawable(appIcon);
    }

    @Override
    public int getItemCount() {
        return (null != items ? items.size() : 0);
    }
}