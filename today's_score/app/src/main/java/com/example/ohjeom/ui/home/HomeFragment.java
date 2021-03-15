package com.example.ohjeom.ui.home;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.service.controls.actions.FloatAction;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import com.example.ohjeom.R;
import com.example.ohjeom.models.Score;
import com.example.ohjeom.models.Template;
import com.example.ohjeom.models.User;
import com.example.ohjeom.retrofit.RetrofitClient;
import com.example.ohjeom.retrofit.ScoreFunctions;
import com.example.ohjeom.retrofit.TemplateService;
import com.example.ohjeom.services.StartService;
import com.example.ohjeom.ui.templates.privateTemplate.PrivateAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private String[] components = new String[]{};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final ListView scores;
        HomeAdapter homeAdapter = new HomeAdapter();

        // 리스트뷰 참조 및 Adapter달기
        scores = (ListView)root.findViewById(R.id.score_list);


        if (User.getCurTemplate() != null) {
            // 설정된 템플릿이 있는 경우
            Log.d("@@@@@HomeAdapter", User.getCurTemplate().getNameResult());
            scores.setAdapter(homeAdapter);

            if (!User.isIsInitialized()) {
                components = User.getComponents();
                for (int i=0; i<6; i++) {
                    if (components[i].equals("true")) {
                        homeAdapter.addScore(Score.getComponentNames()[i], -1);
                    }
                }
            }
            else {
                Score score =  ScoreFunctions.getScore();
                components = score.getComponents();
                for(int i=0; i<6; i++){
                    if (components[i].equals("true")) {
                        Log.d("@@@@@HomeAdapter", Score.getComponentNames()[i]);
                        homeAdapter.updateScore(Score.getComponentNames()[i], score.getScores()[i]);
                    }
                }
            }
            homeAdapter.notifyDataSetChanged();
        }

        //측정 해제 버튼
        FloatingActionButton button = root.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serviceName = "com.example.ohjeom.services.StartService";
                String userID = getActivity().getSharedPreferences("user", MODE_PRIVATE).getString("id", "aaa");

                if(isServiceRunning(serviceName)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    View view = LayoutInflater.from(getActivity())
                            .inflate(R.layout.dialog_stop, null, false);
                    builder.setView(view);

                    final Button registerBtn = (Button) view.findViewById(R.id.register_button);
                    final Button cancelBtn = (Button) view.findViewById(R.id.cancel_button);

                    final AlertDialog dialog = builder.create();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    registerBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(getActivity(), StartService.class);
                            getActivity().stopService(intent);
                            stopTemplate(userID, User.getCurTemplate().getNameResult());

                            dialog.dismiss();
                        }
                    });

                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
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
            Log.d("@@@@@@",runServiceInfo.service.getClassName());
            if (serviceName.equals(runServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void stopTemplate(String userID, String templateName) {
        Retrofit retrofit = RetrofitClient.getInstance();
        TemplateService templateService = retrofit.create(TemplateService.class);

        templateService.stopTemplate(userID, templateName).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.d("@@@@@@", "Stop template: " + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("TemplateService", "Failed API call with call: " + call
                        + ", exception: " + t);
            }
        });
    }
}

