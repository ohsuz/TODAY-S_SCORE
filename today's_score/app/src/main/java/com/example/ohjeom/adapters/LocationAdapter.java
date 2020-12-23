package com.example.ohjeom.adapters;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ohjeom.models.Location;
import com.example.ohjeom.R;

import java.util.ArrayList;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.CustomViewHolder> {

    private ArrayList<Location> locationList;

    public LocationAdapter(ArrayList<Location> list){
        locationList = list;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView locationName;
        protected TextView locationTime;

        public CustomViewHolder(View view) {
            super(view);
            locationName = (TextView) view.findViewById(R.id.locationName);
            locationTime = (TextView) view.findViewById(R.id.locationTime);
        }
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_location, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        viewholder.locationName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        viewholder.locationTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        viewholder.locationName.setGravity(Gravity.CENTER);
        viewholder.locationTime.setGravity(Gravity.CENTER);

        viewholder.locationName.setText(locationList.get(position).getName());
        viewholder.locationTime.setText(String.valueOf(locationList.get(position).getLocationHour())+"시"+String.valueOf(locationList.get(position).getLocationMin())+"분");

    }

    @Override
    public int getItemCount() {
        return (null != locationList ? locationList.size() : 0);
    }
}
