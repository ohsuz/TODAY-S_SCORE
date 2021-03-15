package com.example.ohjeom.ui.statistics;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import com.example.ohjeom.R;
import com.example.ohjeom.adapters.CalendarAdapter;
import com.example.ohjeom.adapters.WeekAdapter;
import com.example.ohjeom.models.DayInfo;
import com.example.ohjeom.models.Diary;
import com.example.ohjeom.models.SelectedDate;
import com.example.ohjeom.models.Storage;
import com.example.ohjeom.retrofit.DiaryService;
import com.example.ohjeom.retrofit.RetrofitClient;
import com.example.ohjeom.retrofit.ScoreFunctions;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class StatisticsFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    public static int SUNDAY        = 1;

    private static final String TAG = "StatisticsFragment";
    private StatisticsViewModel statisticsViewModel;
    private TextView mTvCalendarTitle;
    private GridView mGvCalendar, mGvWeek;
    private ArrayList<DayInfo> mDayList;
    private CalendarAdapter mCalendarAdapter;
    private ArrayList<String> mWeekList;
    private WeekAdapter mWeekAdapter;

    Date selectedDate = new Date();
    final DetailsFragment detailsFragment = new DetailsFragment();
    final DiaryRecordFragment diaryRecordFragment = new DiaryRecordFragment();
    Calendar mThisMonthCalendar;

    private Diary selectedDiary;
    private String userID;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticsViewModel =
                ViewModelProviders.of(this).get(StatisticsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistics, container, false);
        userID = getContext().getSharedPreferences("user", MODE_PRIVATE).getString("id", "aaa");

        /*
        Title 설정
         */
        final TextView textView = root.findViewById(R.id.text_statistics);
        statisticsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        /*
        TabLayout, Fragment 설정
         */
        final TabLayout tabLayout = (TabLayout) root.findViewById(R.id.tabLayout);

        getFragmentManager().beginTransaction().replace(R.id.statistics_frameLayout, detailsFragment).commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment selected = null;

                if (position == 0) {
                    selected = detailsFragment;
                } else {
                    selected = diaryRecordFragment;
                }

                getFragmentManager().beginTransaction().replace(R.id.statistics_frameLayout, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        /*
        Calendar 설정
         */
        ImageButton bLastMonth = (ImageButton)root.findViewById(R.id.gv_calendar_activity_b_last);
        ImageButton bNextMonth = (ImageButton)root.findViewById(R.id.gv_calendar_activity_b_next);

        mTvCalendarTitle = (TextView)root.findViewById(R.id.calendar_month);
        mGvCalendar = (GridView)root.findViewById(R.id.gv_calendar_activity_gv_calendar);
        mGvWeek = (GridView)root.findViewById(R.id.calendar_week);

        bLastMonth.setOnClickListener(this);
        bNextMonth.setOnClickListener(this);
        mGvCalendar.setOnItemClickListener(this);

        mWeekList = new ArrayList<String>();
        String[] weeks = {"일","월","화","수","목","금","토"};
        Collections.addAll(mWeekList,weeks);
        mWeekAdapter = new WeekAdapter(getActivity(), R.layout.item_week, mWeekList);
        mGvWeek.setAdapter(mWeekAdapter);

        SelectedDate.setSelectedDate(selectedDate);
        mDayList = new ArrayList<DayInfo>();

        /*
        우선 default 다이어리로 오늘의 다이어리를 받아옴
         */
        getDiary(userID, ScoreFunctions.getDate());

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 이번달 의 캘린더 인스턴스를 생성한다.
        mThisMonthCalendar = Calendar.getInstance();
        getCalendar(mThisMonthCalendar.getTime());

    }

    public void setSelectedDate(Date date) {
        selectedDate = date;
        SelectedDate.setSelectedDate(date);

        if(mCalendarAdapter!=null) {
            mCalendarAdapter.selectedDate = date;
        }
    }

    /**
     * 달력을 셋팅한다.
     *
     * @param dateForCurrentMonth 달력에 보여지는 이번달의 Calendar 객체
     */
    private void getCalendar(Date dateForCurrentMonth)
    {
        int dayOfMonth;
        int thisMonthLastDay;

        mDayList.clear();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateForCurrentMonth);
        calendar.set(Calendar.DATE,1);

        // 이번달 시작일의 요일을 구한다. 시작일이 일요일인 경우 인덱스를 1(일요일)에서 8(다음주 일요일)로 바꾼다.)
        dayOfMonth = calendar.get(Calendar.DAY_OF_WEEK);
        if(dayOfMonth == SUNDAY)
        {
            dayOfMonth += 7;
        }

        thisMonthLastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        // 캘린더 타이틀(년월 표시)을 세팅한다.
        mTvCalendarTitle.setText(mThisMonthCalendar.get(Calendar.YEAR) + "년 "
                + (mThisMonthCalendar.get(Calendar.MONTH) + 1) + "월");

        calendar.add(Calendar.DATE, -1*(dayOfMonth-1));

        DayInfo day;

        for(int i=0; i<dayOfMonth-1; i++)
        {
            day = new DayInfo();
            day.setDate(calendar.getTime());
            day.setInMonth(false);
            mDayList.add(day);

            calendar.add(Calendar.DATE,+1);
        }

        Integer[] score = {30,30,50,100,100,25,70,70,0,25};
        for(int i=1; i <= thisMonthLastDay; i++)
        {
            day = new DayInfo();
            day.setDate(calendar.getTime());
            day.setInMonth(true);
            day.setAverageScore(score[i%10]);
            mDayList.add(day);

            calendar.add(Calendar.DATE,+1);
        }
        for(int i=1; i<42-(thisMonthLastDay+dayOfMonth-1)+1; i++)
        {
            day = new DayInfo();
            day.setDate(calendar.getTime());
            day.setInMonth(false);
            mDayList.add(day);

            calendar.add(Calendar.DATE, +1);
        }

        initCalendarAdapter();
    }

    /**
     * 지난달의 Calendar 객체를 반환합니다.
     *
     * @param calendar
     * @return LastMonthCalendar
     */
    private Calendar getLastMonth(Calendar calendar)
    {
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        calendar.add(Calendar.MONTH, -1);
        mTvCalendarTitle.setText(mThisMonthCalendar.get(Calendar.YEAR) + "년 "
                + (mThisMonthCalendar.get(Calendar.MONTH) + 1) + "월");

        return calendar;
    }

    /**
     * 다음달의 Calendar 객체를 반환합니다.
     *
     * @param calendar
     * @return NextMonthCalendar
     */
    private Calendar getNextMonth(Calendar calendar)
    {
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        calendar.add(Calendar.MONTH, +1);
        mTvCalendarTitle.setText(mThisMonthCalendar.get(Calendar.YEAR) + "년 "
                + (mThisMonthCalendar.get(Calendar.MONTH) + 1) + "월");
        return calendar;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long arg3)
    {
        Date selectedDate = ((DayInfo)v.getTag()).getDate();
        setSelectedDate(selectedDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(selectedDate);
        getDiary(userID, date);
        mCalendarAdapter.notifyDataSetChanged();
        detailsFragment.changeDetailsFragment(selectedDate);
        diaryRecordFragment.changeDiaryRecordFragment(selectedDate);
        Toast.makeText(getContext(),mDayList.get(position).getDate().toString(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.gv_calendar_activity_b_last:
                mThisMonthCalendar = getLastMonth(mThisMonthCalendar);
                getCalendar(mThisMonthCalendar.getTime());
                break;
            case R.id.gv_calendar_activity_b_next:
                mThisMonthCalendar = getNextMonth(mThisMonthCalendar);
                getCalendar(mThisMonthCalendar.getTime());
                break;
        }
    }

    private void initCalendarAdapter()
    {
        mCalendarAdapter = new CalendarAdapter(getActivity(), R.layout.item_calendar, mDayList, selectedDate);
        mGvCalendar.setAdapter(mCalendarAdapter);
    }

    public void getDiary(String userID, String date) {
        Retrofit retrofit = RetrofitClient.getInstance();
        DiaryService diaryService = retrofit.create(DiaryService.class);

        diaryService.getDiary(userID, date).enqueue(new Callback<Diary>() {
            @Override
            public void onResponse(Call<Diary> call, Response<Diary> response) {
                Log.d("@@@@@@@@", String.valueOf(response.body()));
                Diary diary = response.body();
                Storage storage = new Storage();
                storage.setDiary(diary);
            }
            @Override
            public void onFailure(Call<Diary> call, Throwable t) {
                Log.d("DiaryService", "Failed API call with call: " + call
                        + ", exception: " + t);
            }
        });
    }
}