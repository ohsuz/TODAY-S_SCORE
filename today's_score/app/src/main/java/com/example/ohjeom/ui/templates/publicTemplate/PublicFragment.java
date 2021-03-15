package com.example.ohjeom.ui.templates.publicTemplate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ohjeom.R;
import com.example.ohjeom.etc.PublicTemplateDecoration;
import com.example.ohjeom.etc.SpannedGridLayoutManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PublicFragment extends Fragment {
    private PublicViewModel publicViewModel;
    RecyclerView publicRecyclerView;
    PublicAdapter publicAdapter;

    ArrayList<String> publicTemplates = new ArrayList<String>() {{
        add("아침형 시간표");
        add("저녁형 시간표");
        add("운동중심\n시간표");
        add("공부중심\n시간표");
        add("예헤이\n모르겠다");
        add("Festival 시간표");
    }};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        publicViewModel =
                ViewModelProviders.of(this).get(PublicViewModel.class);
        View root = inflater.inflate(R.layout.fragment_templates_public, container, false);

        publicRecyclerView = (RecyclerView)root.findViewById(R.id.grid_recyclerview);
        publicAdapter = new PublicAdapter(publicTemplates);

        publicRecyclerView.setLayoutManager(new SpannedGridLayoutManager(
                new SpannedGridLayoutManager.GridSpanLookup() {
                    @Override
                    public SpannedGridLayoutManager.SpanInfo getSpanInfo(int position) {
                        position %= 6;
                        if (position == 0) {
                            return new SpannedGridLayoutManager.SpanInfo(3, 1);
                        } else if (position == 1) {
                            return new SpannedGridLayoutManager.SpanInfo(2, 2);
                        } else if (position == 2 || position == 3 || position == 4) {
                            return new SpannedGridLayoutManager.SpanInfo(1, 1);
                        } else if (position == 5) {
                            return new SpannedGridLayoutManager.SpanInfo(2, 1);
                        }
                        return new SpannedGridLayoutManager.SpanInfo(1,1);
                    }
                },
                3 /* Three columns */,
                1f /* We want our items to be 1:1 ratio */));

        publicRecyclerView.addItemDecoration(new PublicTemplateDecoration(15,15));
        publicRecyclerView.setAdapter(publicAdapter);

        return root;
    }

}
