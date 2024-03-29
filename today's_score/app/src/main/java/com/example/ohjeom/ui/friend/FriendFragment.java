package com.example.ohjeom.ui.friend;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ohjeom.R;
import com.example.ohjeom.models.Friend;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FriendFragment extends Fragment {

    private FriendViewModel friendViewModel;
    private Integer[] image = {R.drawable.icon_user1,R.drawable.icon_user2,R.drawable.icon_user3,R.drawable.icon_user4,R.drawable.icon_user5
            ,R.drawable.icon_user6,R.drawable.icon_user7,R.drawable.icon_user8,R.drawable.icon_user9,R.drawable.icon_user10};
    private String[] name = {"토깽이","몰랑이","프로도","예지","하늘","에비츄","대학이좋아","잔망루피","호빵맨","눈송이"};
    private String[] intro = {"토깽이의 시간표","잠시 쉬는중","1","건강","잠시 쉬는중","잠시 쉬는중",
            "잠시 쉬는중","Full 시간표","잠시 쉬는중","잠시 쉬는 중","잠시 쉬는 중"};
    public static boolean[] check = {false,false,false,false,false,false,false,false,false,false};
    private ArrayList<Friend> list = new ArrayList<>();
    private FriendAdapter adapter = new FriendAdapter(list);
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        friendViewModel =
                ViewModelProviders.of(this).get(FriendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_friend, container, false);
        final TextView textView = root.findViewById(R.id.text_friend);
        friendViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        for(int i=0;i<8;i++) {
            list.add(new Friend(image[i],name[i],intro[i],true));
        }

        list.get(1).setUse(false);
        list.get(4).setUse(false);
        list.get(5).setUse(false);
        list.get(6).setUse(false);

        recyclerView = root.findViewById(R.id.friend_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(adapter);

        FloatingActionButton button = root.findViewById(R.id.friend_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                View view = LayoutInflater.from(getActivity())
                        .inflate(R.layout.dialog_friend, null, false);
                builder.setView(view);

                final Button registerBtn = (Button) view.findViewById(R.id.register_button);
                final Button cancelBtn = (Button) view.findViewById(R.id.cancel_button);

                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                registerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        list.add(new Friend(image[list.size()],name[list.size()],intro[list.size()],false));
                        if(list.size() == 2 || list.size() == 5 || list.size() == 6 || list.size() == 7) {
                            list.get(list.size()-1).setUse(true);
                        }
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        ImageView heartBtn = root.findViewById(R.id.heart_button);
        heartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.clear();
                list = new ArrayList<>();
                for(int i=0;i<8;i++) {
                    if(check[i]) {
                        list.add(new Friend(image[i], name[i], intro[i], true));
                    }
                }
                adapter = new FriendAdapter(list);
                recyclerView.setAdapter(adapter);
            }
        });

        return root;
    }
}