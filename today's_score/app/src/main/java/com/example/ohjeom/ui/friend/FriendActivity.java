package com.example.ohjeom.ui.friend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ohjeom.R;
import com.example.ohjeom.models.FriendScore;

import java.util.ArrayList;

public class FriendActivity extends AppCompatActivity {

    private int position;
    private String name;
    private ArrayList<FriendScore> fs1 = new ArrayList<>();
    private TextView friendName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        Intent intent = getIntent();
        position = intent.getIntExtra("position",0);
        name = intent.getStringExtra("name");

        if(position == 0) {

                fs1.add(new FriendScore(0,100));
                fs1.add(new FriendScore(1,80));
                fs1.add(new FriendScore(2,80));

        } else if (position == 2) {

            fs1.add(new FriendScore(0,60));
            fs1.add(new FriendScore(1,40));
            fs1.add(new FriendScore(4,100));
            fs1.add(new FriendScore(5,100));

        } else if (position == 3) {

            fs1.add(new FriendScore(2,80));
            fs1.add(new FriendScore(3,40));

        } else if (position == 7) {

            fs1.add(new FriendScore(0,100));
            fs1.add(new FriendScore(1,20));
            fs1.add(new FriendScore(2,40));
            fs1.add(new FriendScore(3,40));
            fs1.add(new FriendScore(4,20));
        }

        FriendScoreAdapter friendScoreAdapter = new FriendScoreAdapter(fs1);
        ListView listview = (ListView) findViewById(R.id.friends_score);
        listview.setAdapter(friendScoreAdapter);

        friendName = findViewById(R.id.friend_name);
        friendName.setText(name);

    }
}
