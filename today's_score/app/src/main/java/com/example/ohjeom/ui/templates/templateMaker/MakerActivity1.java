package com.example.ohjeom.ui.templates.templateMaker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.ohjeom.R;
import com.example.ohjeom.adapters.TemplateMakerAdapter;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class MakerActivity1 extends AppCompatActivity {

    final String[] options = {"기상 검사","장소 도착 검사","걸음수 검사","핸드폰 사용량 검사","수면 검사","소비 검사"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_templates_maker_1);

        final ListView optionlist;
        final EditText templateName = (EditText)findViewById(R.id.temp_name);
        TemplateMakerAdapter makerAdapter;

        //어댑터 생성
        makerAdapter = new TemplateMakerAdapter();

        // 리스트뷰 참조 및 Adapter달기
        optionlist = (ListView) findViewById(R.id.option_list);
        optionlist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        optionlist.setAdapter(makerAdapter);

        for(int i=0;i<6;i++){
            makerAdapter.addItem(options[i]);
        }

        ImageView next = (ImageView) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SparseBooleanArray booleans = optionlist.getCheckedItemPositions();
                ArrayList<String> selectedOptions = new ArrayList<String>();
                for(int i=0; i<6; i++) {
                    if(booleans.get(i)){
                        selectedOptions.add(options[i]);
                    }
                }

                for(String s:selectedOptions){
                    Log.d("체크 목록:",s);
                }

                Intent intent = new Intent(getApplicationContext(), MakerActivity2.class);
                intent.putExtra("selectedOptions", selectedOptions);
                intent.putExtra("templateName", templateName.getText().toString());
                startActivity(intent);
            }
        });
    }
}