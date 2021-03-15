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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import com.example.ohjeom.MainActivity;
import com.example.ohjeom.R;
import com.example.ohjeom.retrofit.DiaryService;
import com.example.ohjeom.retrofit.RetrofitClient;
import com.example.ohjeom.retrofit.ScoreFunctions;
import com.example.ohjeom.retrofit.ScoreService;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class DiaryFragment extends Fragment {
    private static final String TAG = "DiaryFragment";
    private DiaryViewModel diaryViewModel;
    private TextView diaryDate, diaryWeek;
    private EditText diaryTitle, diaryText;
    private EditText diaryGood, diaryBad;
    private String userID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        diaryViewModel =
                ViewModelProviders.of(this).get(DiaryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_diary, container, false);
        userID = getContext().getSharedPreferences("user", MODE_PRIVATE).getString("id", "aaa");

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
        diaryGood = root.findViewById(R.id.diary_best);
        diaryBad = root.findViewById(R.id.diary_worst);

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
                        String date = ScoreFunctions.getDate();
                        String title = diaryTitle.getText().toString();
                        String content = diaryText.getText().toString();
                        String good = diaryGood.getText().toString();
                        String bad = diaryBad.getText().toString();

                        addDiary(userID, date, title, content, good, bad);

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

    public void addDiary(String userID, String date, String title, String content, String good, String bad) {
        Retrofit retrofit = RetrofitClient.getInstance();
        DiaryService diaryService = retrofit.create(DiaryService.class);
        Log.d("@@@@@@", "DiaryFragment - addDiary");

        diaryService.addDiary(userID, date, title, content, good, bad).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 404) {
                    Toast.makeText(getContext(), "이미 오늘의 일기가 존재합니다.", Toast.LENGTH_LONG).show();
                }
                if (response.code() == 200) {
                    Toast.makeText(getContext(), "오늘의 일기가 저장되었습니다.", Toast.LENGTH_LONG).show();
                    Log.d("@@@@@@", "DiaryFragment - addDiary: " + response.body().toString());
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("DiaryFragment", "Failed API call with call: " + call
                        + ", exception: " + t);
            }
        });
    }

    public static String getWeek(int day) {
        String week;

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