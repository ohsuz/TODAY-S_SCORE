package com.example.ohjeom.ui.home;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.example.ohjeom.R;
import com.example.ohjeom.models.Template;
import com.example.ohjeom.models.Test;
import com.example.ohjeom.services.StartService;

import static android.content.Context.ACTIVITY_SERVICE;

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

        //측정 해제 버튼
        Button button = root.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String serviceName = "com.example.ohjeom.services.StartService";

                if(isServiceRunning(serviceName)) {
                    Intent intent = new Intent(getActivity(), StartService.class);
                    getActivity().stopService(intent);
                }
                else
                    Toast.makeText(getActivity(),"현재 측정중이 아닙니다.",Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    public boolean isServiceRunning(String serviceName) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runServiceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.d("ㅇㅇㅇ",runServiceInfo.service.getClassName());
            if (serviceName.equals(runServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

