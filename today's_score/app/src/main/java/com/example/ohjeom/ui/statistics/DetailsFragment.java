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
import com.example.ohjeom.models.Score;
import com.example.ohjeom.models.SelectedDate;
import com.example.ohjeom.models.Storage;
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

        if (Storage.getCalendarScore() != null) {
            Score score = Storage.getCalendarScore();
            String[] components = score.getComponents();
            for (int i = 0; i < 6; i++) {
                if (components[i].equals("true")) {
                    details.add(new Details(Score.getComponentNames()[i], score.getScores()[i]));
                }
            }
        }

        DetailsAdapter detailsAdapter = new DetailsAdapter(details);
        detailsList.setAdapter(detailsAdapter);
        detailsAdapter.notifyDataSetChanged();
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
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,75 * detailsAdapter.getCount() + 20 * (detailsAdapter.getCount()-1) + 60, getResources().getDisplayMetrics());
            params.height = height;
            listView.setLayoutParams(params);
            listView.requestLayout();
            return true;
    }
}