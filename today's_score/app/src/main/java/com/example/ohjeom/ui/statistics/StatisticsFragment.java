package com.example.ohjeom.ui.statistics;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import com.example.ohjeom.R;
import com.example.ohjeom.adapters.CalendarAdapter;
import com.example.ohjeom.adapters.WeekAdapter;
import com.example.ohjeom.etc.PermissionChecker;
import com.example.ohjeom.models.DayInfo;
import com.example.ohjeom.models.Diary;
import com.example.ohjeom.models.Location;
import com.example.ohjeom.models.Score;
import com.example.ohjeom.models.SelectedDate;
import com.example.ohjeom.models.Storage;
import com.example.ohjeom.retrofit.DiaryService;
import com.example.ohjeom.retrofit.RetrofitClient;
import com.example.ohjeom.retrofit.ScoreFunctions;
import com.example.ohjeom.retrofit.ScoreService;
import com.example.ohjeom.ui.templates.templateMaker.MakerActivity2;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class StatisticsFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    public static int SUNDAY = 1;

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
        Graph 설정
         */

        final ImageView graphButton = root.findViewById(R.id.graph_button);
        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                View view = LayoutInflater.from(getActivity())
                        .inflate(R.layout.dialog_statistics, null, false);
                builder.setView(view);

                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                LineChart lineChart = view.findViewById(R.id.line_chart);

                ArrayList<Entry> entry1 = new ArrayList<>();
                ArrayList<Entry> entry2 = new ArrayList<>();
                ArrayList<Entry> entry3 = new ArrayList<>();
                ArrayList<Entry> entry4 = new ArrayList<>();
                ArrayList<Entry> entry5 = new ArrayList<>();
                ArrayList<Entry> entry6 = new ArrayList<>();
                ArrayList<Entry> entry7 = new ArrayList<>();

                Integer[] ave = {76,58,51,86,45,0,0};
                Integer[] wakeUp = {40,60,80,100,20,0,0};
                Integer[] sleep = {50,42,100,100,25,0,0};
                Integer[] walk = {90,66,5,30,50,0,0};
                Integer[] phone = {100,76,70,100,80,0,0};
                Integer[] location = {100,50,0,100,50,0,0};
                Integer[] payment = {0,0,0,0,0,0,0};


                for(int i=1;i<=7;i++) {
                    entry1.add(new Entry(i, ave[i-1]));
                    entry2.add(new Entry(i, wakeUp[i-1]));
                    entry3.add(new Entry(i, sleep[i-1]));
                    entry4.add(new Entry(i, walk[i-1]));
                    entry5.add(new Entry(i, phone[i-1]));
                    entry6.add(new Entry(i, location[i-1]));
                    entry7.add(new Entry(i, payment[i-1]));
                }

                LineData chartData = new LineData();

                LineDataSet set1 = new LineDataSet(entry1, "전체");
                set1.setColor(Color.parseColor("#FFA7A7"));
                set1.setLineWidth(2); // 선 굵기
                set1.setDrawHorizontalHighlightIndicator(false);
                set1.setDrawHighlightIndicators(false);
                set1.setDrawValues(false);
                set1.setCircleColor(Color.parseColor("#FFA7A7"));
                set1.setCircleRadius(5f);
                chartData.addDataSet(set1);

                LineDataSet set2 = new LineDataSet(entry2, "기상");
                set2.setColor(Color.parseColor("#FFC19E"));
                set2.setLineWidth(2); // 선 굵기
                set2.setDrawHorizontalHighlightIndicator(false);
                set2.setDrawHighlightIndicators(false);
                set2.setDrawValues(false);
                set2.setCircleColor(Color.parseColor("#FFC19E"));
                set2.setCircleRadius(5f);
                chartData.addDataSet(set2);

                LineDataSet set3 = new LineDataSet(entry3, "수면");
                set3.setColor(Color.parseColor("#FFE08C"));
                set3.setLineWidth(2); // 선 굵기
                set3.setDrawHorizontalHighlightIndicator(false);
                set3.setDrawHighlightIndicators(false);
                set3.setDrawValues(false);
                set3.setCircleColor(Color.parseColor("#FFE08C"));
                set3.setCircleRadius(5f);
                chartData.addDataSet(set3);

                LineDataSet set4 = new LineDataSet(entry4, "걸음 수");
                set4.setColor(Color.parseColor("#CEF279"));
                set4.setLineWidth(2); // 선 굵기
                set4.setDrawHorizontalHighlightIndicator(false);
                set4.setDrawHighlightIndicators(false);
                set4.setDrawValues(false);
                set4.setCircleColor(Color.parseColor("#CEF279"));
                set4.setCircleRadius(5f);
                chartData.addDataSet(set4);

                LineDataSet set5 = new LineDataSet(entry5, "앱");
                set5.setColor(Color.parseColor("#B2EBF4"));
                set5.setLineWidth(2); // 선 굵기
                set5.setDrawHorizontalHighlightIndicator(false);
                set5.setDrawHighlightIndicators(false);
                set5.setDrawValues(false);
                set5.setCircleColor(Color.parseColor("#B2EBF4"));
                set5.setCircleRadius(5f);
                chartData.addDataSet(set5);

                LineDataSet set6 = new LineDataSet(entry6, "장소");
                set6.setColor(Color.parseColor("#B5B2FF"));
                set6.setLineWidth(2); // 선 굵기
                set6.setDrawHorizontalHighlightIndicator(false);
                set6.setDrawHighlightIndicators(false);
                set6.setDrawValues(false);
                set6.setCircleColor(Color.parseColor("#B5B2FF"));
                set6.setCircleRadius(5f);
                chartData.addDataSet(set6);

                LineDataSet set7 = new LineDataSet(entry7, "소비");
                set7.setColor(Color.parseColor("#FFB2F5"));
                set7.setLineWidth(2); // 선 굵기
                set7.setDrawHorizontalHighlightIndicator(false);
                set7.setDrawHighlightIndicators(false);
                set7.setDrawValues(false);
                set7.setCircleColor(Color.parseColor("#FFB2F5"));
                set7.setCircleRadius(5f);
                chartData.addDataSet(set7);

                lineChart.setData(chartData);

                XAxis xAxis = lineChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setTextColor(Color.BLACK);
                xAxis.setLabelCount(7,true);
                xAxis.setDrawGridLines(false);


                YAxis yLAxis = lineChart.getAxisLeft();
                yLAxis.setTextColor(Color.BLACK);
                yLAxis.setAxisMinimum(0);
                yLAxis.setAxisMaximum(100);
                yLAxis.setDrawGridLines(false);
                yLAxis.setDrawAxisLine(false);
                yLAxis.setXOffset(15f);
                yLAxis.setTextSize(15f);

                YAxis yRAxis = lineChart.getAxisRight();
                yRAxis.setDrawLabels(false);
                yRAxis.setDrawAxisLine(false);
                yRAxis.setDrawGridLines(false);

                lineChart.setDescription(null);

                Button button1 = view.findViewById(R.id.btn1);
                Button button2 = view.findViewById(R.id.btn2);
                Button button3 = view.findViewById(R.id.btn3);
                Button button4 = view.findViewById(R.id.btn4);
                Button button5 = view.findViewById(R.id.btn5);
                Button button6 = view.findViewById(R.id.btn6);
                Button button7 = view.findViewById(R.id.btn7);
                Button[] buttons = new Button[]{button1, button2, button3, button4, button5, button6, button7};
                LineDataSet[] sets = new LineDataSet[]{set1,set2,set3,set4,set5,set6,set7};

                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean check = false;

                        for(Button b:buttons) {
                            if(b.isSelected()){
                                check = true;
                                break;
                            }
                        }

                        button1.setSelected(!button1.isSelected());

                        lineChart.clear();
                        chartData.clearValues();

                        for(int i=0;i<7;i++) {
                            if(buttons[i].isSelected()){
                                chartData.addDataSet(sets[i]);
                            }
                        }
                        lineChart.setData(chartData);
                    }
                });

                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean check = false;

                        for(Button b:buttons) {
                            if(b.isSelected()){
                                check = true;
                                break;
                            }
                        }

                        button2.setSelected(!button2.isSelected());

                        lineChart.clear();
                        chartData.clearValues();

                        for(int i=0;i<7;i++) {
                            if(buttons[i].isSelected()){
                                chartData.addDataSet(sets[i]);
                            }
                        }
                        lineChart.setData(chartData);
                    }
                });

                button3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean check = false;

                        for(Button b:buttons) {
                            if(b.isSelected()){
                                check = true;
                                break;
                            }
                        }

                        button3.setSelected(!button3.isSelected());

                        lineChart.clear();
                        chartData.clearValues();

                        for(int i=0;i<7;i++) {
                            if(buttons[i].isSelected()){
                                chartData.addDataSet(sets[i]);
                            }
                        }
                        lineChart.setData(chartData);
                    }
                });

                button4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean check = false;

                        for(Button b:buttons) {
                            if(b.isSelected()){
                                check = true;
                                break;
                            }
                        }

                        button4.setSelected(!button4.isSelected());

                        lineChart.clear();
                        chartData.clearValues();

                        for(int i=0;i<7;i++) {
                            if(buttons[i].isSelected()){
                                chartData.addDataSet(sets[i]);
                            }
                        }
                        lineChart.setData(chartData);
                    }
                });

                button5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean check = false;

                        for(Button b:buttons) {
                            if(b.isSelected()){
                                check = true;
                                break;
                            }
                        }

                        button5.setSelected(!button5.isSelected());

                        lineChart.clear();
                        chartData.clearValues();

                        for(int i=0;i<7;i++) {
                            if(buttons[i].isSelected()){
                                chartData.addDataSet(sets[i]);
                            }
                        }
                        lineChart.setData(chartData);
                    }
                });

                button6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean check = false;

                        for(Button b:buttons) {
                            if(b.isSelected()){
                                check = true;
                                break;
                            }
                        }

                        button6.setSelected(!button6.isSelected());

                        lineChart.clear();
                        chartData.clearValues();

                        for(int i=0;i<7;i++) {
                            if(buttons[i].isSelected()){
                                chartData.addDataSet(sets[i]);
                            }
                        }
                        lineChart.setData(chartData);
                    }
                });

                button7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean check = false;

                        for(Button b:buttons) {
                            if(b.isSelected()){
                                check = true;
                                break;
                            }
                        }

                        button7.setSelected(!button7.isSelected());

                        lineChart.clear();
                        chartData.clearValues();

                        for(int i=0;i<7;i++) {
                            if(buttons[i].isSelected()){
                                chartData.addDataSet(sets[i]);
                            }
                        }
                        lineChart.setData(chartData);
                    }
                });

                dialog.show();
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

        Integer[] score = {76,58,51,86,45};
        int j=0;
        for(int i=1; i <= thisMonthLastDay; i++)
        {
            day = new DayInfo();
            day.setDate(calendar.getTime());
            day.setInMonth(true);
            if(calendar.get(Calendar.MONTH) == 3 && 4 <= calendar.get(Calendar.DAY_OF_MONTH) && calendar.get(Calendar.DAY_OF_MONTH) <= 8) {
                day.setAverageScore(score[j]);
                j++;
            }
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
        getScores(userID, date);
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

    public static void getScores(String userID, String date) {
        Retrofit retrofit = RetrofitClient.getInstance();
        ScoreService scoreService = retrofit.create(ScoreService.class);

        scoreService.getScores(userID, date).enqueue(new Callback<Score>() {
            @Override
            public void onResponse(Call<Score> call, Response<Score> response) {
                if (response.code() == 200) {
                    Score score = response.body();
                    Storage.setCalendarScore(score);
                    Storage.getCalendarScore().parseInfo();
                    Log.d("@@@@@@@@", String.valueOf(Storage.getCalendarScore().getTemplateName()));
                }
                if (response.code() == 404) {
                    Storage.setCalendarScore(null);
                }
            }
            @Override
            public void onFailure(Call<Score> call, Throwable t) {
                Log.d("ScoreService", "Failed API call with call: " + call
                        + ", exception: " + t);
            }
        });
    }
}