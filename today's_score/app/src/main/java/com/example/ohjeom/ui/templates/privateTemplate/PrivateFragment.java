package com.example.ohjeom.ui.templates.privateTemplate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ohjeom.MainActivity;
import com.example.ohjeom.R;
import com.example.ohjeom.models.Template;
import com.example.ohjeom.models.Templates;
import com.example.ohjeom.ui.templates.templateMaker.MakerActivity1;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PrivateFragment extends Fragment {

    private PrivateViewModel privateViewModel;
    //public static ArrayList<PrivateTemplate> privateList;
    public static ArrayList<Template> templates;
    public static PrivateAdapter pAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        privateViewModel =
                ViewModelProviders.of(this).get(PrivateViewModel.class);

        View root = inflater.inflate(R.layout.fragment_templates_private, container, false);

        RecyclerView privateListView = (RecyclerView) root.findViewById(R.id.private_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        privateListView.setLayoutManager(linearLayoutManager);

        templates = Templates.getTemplates();
        pAdapter = new PrivateAdapter(templates);
        //privateList = new ArrayList<>();
        //pAdapter = new PrivateAdapter(privateList);

        privateListView.setAdapter(pAdapter);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();

        FloatingActionButton addBtn = view.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MakerActivity1.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        ((MainActivity)getActivity()).refresh();
        super.onResume();
    }

}
