package com.example.ohjeom.ui.rankings;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.ohjeom.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DiaryFragment extends Fragment {

    private static final String TAG = "DiaryFragment";
    private DiaryViewModel diaryViewModel;
    private TextView diaryDate, diaryWeek;
    private EditText diaryTitle, diaryText;
    private EditText diaryBest, diaryWorst;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        diaryViewModel =
                ViewModelProviders.of(this).get(DiaryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_diary, container, false);

        final TextView textView = root.findViewById(R.id.text_diary);
        diaryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        Calendar calendar = Calendar.getInstance();

        diaryDate = root.findViewById(R.id.diary_date);
        diaryWeek = root.findViewById(R.id.diary_week);
        diaryDate.setText(calendar.get(Calendar.YEAR)+"년 "+(calendar.get(Calendar.MONTH)+1)+"월 "+calendar.get(Calendar.DAY_OF_MONTH)+"일");
        diaryWeek.setText(getWeek(calendar.get(Calendar.DAY_OF_WEEK)));

        diaryTitle = root.findViewById(R.id.diary_title);
        diaryText = root.findViewById(R.id.diary_text);
        diaryBest = root.findViewById(R.id.diary_best);
        diaryWorst = root.findViewById(R.id.diary_worst);

        ImageView saveButton = root.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                View view = LayoutInflater.from(getActivity())
                        .inflate(R.layout.dialog_diary, null, false);
                builder.setView(view);

                final Button registerBtn = (Button) view.findViewById(R.id.register_button);
                final Button cancelBtn = (Button) view.findViewById(R.id.cancel_button);

                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                registerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //밑에 String 변수 5개 보내야함.
                        String date;
                        String title = diaryTitle.getText().toString();
                        String text = diaryText.getText().toString();
                        String best = diaryBest.getText().toString();
                        String worst = diaryWorst.getText().toString();

                        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
                        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date oldDate = oldFormat.parse(diaryDate.getText().toString());
                            date = newFormat.format(oldDate);

                            Log.d("일기에 출력될 날짜 형식" , String.valueOf(oldDate));
                            Log.d("서버에 저장될 날짜 형식", date);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Log.d("TAG",title);
                        Log.d("TAG",text);
                        Log.d("TAG",best);
                        Log.d("TAG",worst);

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

    private String getWeek(int day) {
        String week = "일요일";

        if(day == 1) {
            week = "일요일";
        } else if (day == 2) {
            week = "월요일";
        } else if (day == 3) {
            week = "화요일";
        } else if (day == 4) {
            week = "수요일";
        } else if (day == 5) {
            week = "목요일";
        } else if (day == 6) {
            week = "금요일";
        } else {
            week = "토요일";
        }

        return week;
    }
}