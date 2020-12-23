package com.example.ohjeom.adapters;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.ohjeom.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder>{
    private Map<ResolveInfo,Boolean> mCheckedMap = new HashMap<>();
    private PackageManager pm;
    private List<ResolveInfo> items;
    private List<ResolveInfo> mCheckedlist =  new ArrayList<>();

    public AppAdapter(PackageManager pm, List<ResolveInfo> items){
        this.items = items;
        this.pm =pm;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_app,viewGroup,false);
        final AppViewHolder viewHolder = new AppViewHolder(view);

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                ResolveInfo resolveInfo = items.get(viewHolder.getAdapterPosition());
                mCheckedMap.put(resolveInfo,isChecked);

                if(isChecked){
                    mCheckedlist.add(resolveInfo);
                } else {
                    mCheckedlist.remove(resolveInfo);
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        ResolveInfo resolveinfo = items.get(position);

        String appName = items.get(position).activityInfo.loadLabel(pm).toString();
        Drawable appIcon = items.get(position).activityInfo.loadIcon(pm);
        holder.appName.setText(appName);
        holder.appIcon.setImageDrawable(appIcon);

        boolean isChecked = mCheckedMap.get(resolveinfo) == null
                ? false
                : mCheckedMap.get(resolveinfo);
        holder.checkBox.setChecked(isChecked);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //ViewHolder
    public static class AppViewHolder extends RecyclerView.ViewHolder{
        ImageView appIcon;
        TextView appName;
        CheckBox checkBox;
        public AppViewHolder(@NonNull View itemView){
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }

    //list return
    public List<ResolveInfo> getChecklist(){
        return mCheckedlist;
    }
}
