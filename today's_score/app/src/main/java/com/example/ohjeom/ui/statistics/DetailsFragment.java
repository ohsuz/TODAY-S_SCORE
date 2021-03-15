package com.example.ohjeom.ui.statistics;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ohjeom.R;
import com.example.ohjeom.etc.PublicTemplateDecoration;
import com.example.ohjeom.etc.SpannedGridLayoutManager;
import com.example.ohjeom.models.SelectedDate;
import com.example.ohjeom.ui.templates.publicTemplate.PublicAdapter;
import com.example.ohjeom.ui.templates.publicTemplate.PublicViewModel;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

public class DetailsFragment extends Fragment {

    private DetailsViewModel detailsViewModel;
    private TextView txt;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        detailsViewModel =
                ViewModelProviders.of(this).get(DetailsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistics_details, container, false);

        txt = (TextView)root.findViewById(R.id.test_details);
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
        detailsViewModel = new ViewModelProvider(this).get(DetailsViewModel.class);
        // TODO: Use the ViewModel
    }

    public void changeDetailsFragment(Date date) {
        if(getActivity() != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            txt.setText(calendar.get(Calendar.YEAR) + "년" +
                    (calendar.get(Calendar.MONTH) + 1) + "월" +
                    calendar.get(Calendar.DAY_OF_MONTH) + "일");
        }
    }
}