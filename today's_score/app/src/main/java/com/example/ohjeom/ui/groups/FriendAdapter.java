package com.example.ohjeom.ui.groups;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ohjeom.R;
import com.example.ohjeom.models.Friend;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private ArrayList<Friend> friends;

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout friendLayout;
        CircleImageView photoImage;
        TextView tvName;
        TextView tvIntro;
        ImageView useImage;

        ViewHolder(View itemView) {
            super(itemView);

            friendLayout = itemView.findViewById(R.id.friend_layout);
            photoImage = itemView.findViewById(R.id.friend_photo);
            tvName = itemView.findViewById(R.id.friend_name);
            tvIntro = itemView.findViewById(R.id.friend_intro);
            useImage = itemView.findViewById(R.id.use_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if(friends.get(pos).isUse()) {
                            Intent intent = new Intent(view.getContext(),FriendActivity.class);
                            intent.putExtra("position",pos);
                            intent.putExtra("name",tvName.getText().toString());
                            view.getContext().startActivity(intent);
                        }
                    }
                }
            });
        }
    }

    FriendAdapter(ArrayList<Friend> list) {
        friends = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.item_friend, parent, false);
        FriendAdapter.ViewHolder vh = new FriendAdapter.ViewHolder(view) ;

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int photo = friends.get(position).getPhoto();
        String name = friends.get(position).getName();
        String intro = friends.get(position).getIntro();

        if(!friends.get(position).isUse()) {
            holder.useImage.setImageResource(R.drawable.ic_baseline_star_1);
            holder.friendLayout.setBackgroundColor(Color.parseColor("#10FFFFFF"));
        } else {
            holder.friendLayout.setBackgroundColor(Color.parseColor("#80FFFFFF"));
        }

        holder.photoImage.setImageResource(photo);
        holder.tvName.setText(name);
        holder.tvIntro.setText(intro);

    }

    @Override
    public int getItemCount() {
        return friends.size();
    }
}
