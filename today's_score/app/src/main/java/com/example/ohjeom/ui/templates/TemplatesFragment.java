package com.example.ohjeom.ui.templates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ohjeom.R;
import com.example.ohjeom.ui.templates.privateTemplate.PrivateFragment;
import com.example.ohjeom.ui.templates.publicTemplate.PublicFragment;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class TemplatesFragment extends Fragment {

    private TemplatesViewModel templatesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        templatesViewModel =
                ViewModelProviders.of(this).get(TemplatesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_templates, container, false);

        /*
        Title 설정
         */
        final TextView textView = root.findViewById(R.id.text_templates);
        templatesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        /*
        TabLayout, Fragment 설정
         */
        final Fragment fragment_public = new PublicFragment();
        final Fragment fragment_private = new PrivateFragment();
        final TabLayout tabLayout = (TabLayout)root.findViewById(R.id.tabLayout);

        getFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment_public).commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment selected = null;

                if (position == 0) {
                    selected = fragment_public;
                } else {
                    selected = fragment_private;
                }

                getFragmentManager().beginTransaction().replace(R.id.frameLayout, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        return root;
    }
}