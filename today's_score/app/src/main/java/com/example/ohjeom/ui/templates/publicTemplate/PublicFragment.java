package com.example.ohjeom.ui.templates.publicTemplate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ohjeom.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class PublicFragment extends Fragment {
    private PublicViewModel publicViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        publicViewModel =
                ViewModelProviders.of(this).get(PublicViewModel.class);
        View root = inflater.inflate(R.layout.fragment_templates_public, container, false);
        return root;
    }

}
