package com.example.ohjeom.ui.statistics;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ohjeom.R;
import com.example.ohjeom.models.Diary;
import com.example.ohjeom.models.SelectedDate;
import com.example.ohjeom.models.Storage;
import com.example.ohjeom.ui.diary.DiaryFragment;

import java.util.Calendar;
import java.util.Date;

public class DiaryRecordFragment extends Fragment {
    private DiaryRecordViewModel diaryRecordViewModel;
    private Diary diary;
    private TextView date, week, title, content, good, bad;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        diaryRecordViewModel =
                ViewModelProviders.of(this).get(DiaryRecordViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistics_diary, container, false);
        date = (TextView)root.findViewById(R.id.diary_date);
        week = (TextView)root.findViewById(R.id.diary_week);
        title = (TextView)root.findViewById(R.id.diary_title);
        content = (TextView)root.findViewById(R.id.diary_text);
        good = (TextView)root.findViewById(R.id.diary_best);
        bad = (TextView)root.findViewById(R.id.diary_worst);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(SelectedDate.getSelectedDate());

        if (Storage.getDiary() != null) {
            diary = Storage.getDiary();
            date.setText(calendar.get(Calendar.YEAR) + "년" +
                    (calendar.get(Calendar.MONTH) + 1) + "월" +
                    calendar.get(Calendar.DAY_OF_MONTH) + "일");
            week.setText(DiaryFragment.getWeek(calendar.get(Calendar.DAY_OF_WEEK)));
            title.setText(diary.getTitle());
            content.setText(diary.getContent());
            good.setText(diary.getGood());
            bad.setText(diary.getBad());
        }

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        diaryRecordViewModel = new ViewModelProvider(this).get(DiaryRecordViewModel.class);
        // TODO: Use the ViewModel
    }


    public void changeDiaryRecordFragment(Date selectedDate) {
                if(getActivity() != null){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(selectedDate);

                    if (Storage.getDiary() != null) {
                        diary = Storage.getDiary();
                        date.setText(calendar.get(Calendar.YEAR) + "년" +
                                (calendar.get(Calendar.MONTH) + 1) + "월" +
                                calendar.get(Calendar.DAY_OF_MONTH) + "일");
                        week.setText(DiaryFragment.getWeek(calendar.get(Calendar.DAY_OF_WEEK)));
                        title.setText(diary.getTitle());
                        content.setText(diary.getContent());
                        good.setText(diary.getGood());
                        bad.setText(diary.getBad());
                    }
        }
    }

}