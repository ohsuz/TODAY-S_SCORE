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
import com.example.ohjeom.models.SelectedDate;

import java.util.Calendar;
import java.util.Date;

public class DiaryRecordFragment extends Fragment {

    private DiaryRecordViewModel diaryRecordViewModel;
    private TextView txt;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        diaryRecordViewModel =
                ViewModelProviders.of(this).get(DiaryRecordViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistics_diary, container, false);

        txt = (TextView)root.findViewById(R.id.test_diary);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(SelectedDate.getSelectedDate());
        txt.setText(calendar.get(Calendar.YEAR) + "년" +
                (calendar.get(Calendar.MONTH) + 1) + "월" +
                calendar.get(Calendar.DAY_OF_MONTH) + "일");

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        diaryRecordViewModel = new ViewModelProvider(this).get(DiaryRecordViewModel.class);
        // TODO: Use the ViewModel
    }

    public void changeDiaryRecordFragment(Date date) {
        if(getActivity() != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            txt.setText(calendar.get(Calendar.YEAR) + "년" +
                    (calendar.get(Calendar.MONTH) + 1) + "월" +
                    calendar.get(Calendar.DAY_OF_MONTH) + "일");
        }
    }
}