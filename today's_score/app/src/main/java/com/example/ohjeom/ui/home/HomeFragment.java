package com.example.ohjeom.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ohjeom.MainActivity;
import com.example.ohjeom.R;
import com.example.ohjeom.adapters.TemplateMakerAdapter;
import com.example.ohjeom.models.Template;
import com.example.ohjeom.models.Templates;
import com.example.ohjeom.models.Test;
import com.example.ohjeom.ui.templates.TemplateActivity;
import com.example.ohjeom.ui.templates.TemplatesViewModel;
import com.example.ohjeom.ui.templates.privateTemplate.PrivateAdapter;
import com.example.ohjeom.ui.templates.privateTemplate.PrivateFragment;
import com.example.ohjeom.ui.templates.privateTemplate.PrivateViewModel;
import com.example.ohjeom.ui.templates.publicTemplate.PublicFragment;
import com.example.ohjeom.ui.templates.templateMaker.MakerActivity1;
import com.example.ohjeom.ui.templates.templateMaker.MakerActivity2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private boolean[] components;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final ListView scores;
        HomeAdapter homeAdapter = new HomeAdapter();

        // 리스트뷰 참조 및 Adapter달기
        scores = (ListView)root.findViewById(R.id.score_list);

        // 설정된 템플릿이 있는 경우
        if (Test.getTemplate() != null) {
            scores.setAdapter(homeAdapter);

            components = Test.getTemplate().getComponents();

            //scores.setBackgroundColor(Color.parseColor("#CCE6F1E6"));

            for(int i=0; i<5; i++){
                if (components[i]) {
                    Test test = new Test(Template.componentNames[i]);
                    homeAdapter.addTest(test);
                }
            }

            homeAdapter.notifyDataSetChanged();
        }
        return root;
    }

}