package com.example.ohjeom.ui.groups;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ohjeom.R;
import com.example.ohjeom.models.Friend;
import com.example.ohjeom.services.StartService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FriendFragment extends Fragment {

    private FriendViewModel friendViewModel;

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

        ArrayList<Friend> list = new ArrayList<>();

        Integer[] image = {R.drawable.icon_user1,R.drawable.icon_user2,R.drawable.icon_user3,R.drawable.icon_user4,R.drawable.icon_user5
        ,R.drawable.icon_user6,R.drawable.icon_user7,R.drawable.icon_user8,R.drawable.icon_user9};
        String[] name = {"토깽이","몰랑이","프로도","예지","하늘","에비츄","대학이좋아","잔망루피","호빵맨"};
        String[] intro = {"토깽이의 시간표","잠시 쉬는중","1","건강","잠시 쉬는중","잠시 쉬는중",
                "잠시 쉬는중","Full 시간표","잠시 쉬는중"};

        for(int i=0;i<8;i++) {
            list.add(new Friend(image[i],name[i],intro[i],true));
        }

        list.get(1).setUse(false);
        list.get(4).setUse(false);
        list.get(5).setUse(false);
        list.get(6).setUse(false);

        RecyclerView recyclerView = root.findViewById(R.id.friend_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FriendAdapter adapter = new FriendAdapter(list);
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
                        list.add(new Friend(image[8],name[8],intro[8],false));
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

        return root;
    }
}