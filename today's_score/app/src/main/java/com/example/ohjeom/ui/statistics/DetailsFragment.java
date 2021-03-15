package com.example.ohjeom.ui.statistics;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ohjeom.R;
import com.example.ohjeom.etc.PublicTemplateDecoration;
import com.example.ohjeom.etc.SpannedGridLayoutManager;
import com.example.ohjeom.models.Details;
import com.example.ohjeom.models.SelectedDate;
import com.example.ohjeom.ui.templates.publicTemplate.PublicAdapter;
import com.example.ohjeom.ui.templates.publicTemplate.PublicViewModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DetailsFragment extends Fragment {

    private DetailsViewModel detailsViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        detailsViewModel =
                ViewModelProviders.of(this).get(DetailsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistics_details, container, false);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(SelectedDate.getSelectedDate());

        ListView detailsList = (ListView) root.findViewById(R.id.details_score);
        ArrayList<Details> details = new ArrayList<Details>();
        //날짜(SelectedDate.getSelectedDate(); = 자료형 : Date)를 보내서 점수 가져오는 부분 or 날짜 & 점수 한꺼번에 로그인화면에서 가져와서 거기서 찾는 방법 ..?

        String[] components = {"기상 검사","수면 검사","걸음수 검사","핸드폰 사용량 검사","장소 도착 검사","소비 검사"};
        Integer[] score = {100,100,80,70,60,30};

        for(int i=0;i<3;i++) {
            details.add(new Details(components[i],score[i]));
        }

        DetailsAdapter detailsAdapter = new DetailsAdapter(details);

        detailsList.setAdapter(detailsAdapter);

        setListViewHeightBasedOnItems(detailsAdapter, detailsList);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        detailsViewModel = new ViewModelProvider(this).get(DetailsViewModel.class);
        // TODO: Use the ViewModel
    }

    public void changeDetailsFragment(Date date) {
        if(getActivity() != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
        }
    }

    public boolean setListViewHeightBasedOnItems(DetailsAdapter detailsAdapter, ListView listView) {

        // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,75 * detailsAdapter.getCount() + 20 * (detailsAdapter.getCount()-1) + 60, getResources().getDisplayMetrics());
            params.height = height;
            listView.setLayoutParams(params);
            listView.requestLayout();

            //setDynamicHeight(listView);
            return true;
    }
}