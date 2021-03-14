package com.example.ohjeom.ui.templates.privateTemplate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ohjeom.MainActivity;
import com.example.ohjeom.R;
import com.example.ohjeom.models.Template;
import com.example.ohjeom.models.Templates;
import com.example.ohjeom.retrofit.RetrofitClient;
import com.example.ohjeom.retrofit.TemplateService;
import com.example.ohjeom.ui.templates.templateMaker.MakerActivity1;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class PrivateFragment extends Fragment {
    private PrivateViewModel privateViewModel;
    private RecyclerView privateListView;
    public static PrivateAdapter pAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        privateViewModel =
                ViewModelProviders.of(this).get(PrivateViewModel.class);
        View root = inflater.inflate(R.layout.fragment_templates_private, container, false);
        privateListView = (RecyclerView) root.findViewById(R.id.private_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        privateListView.setLayoutManager(linearLayoutManager);

        pAdapter = new PrivateAdapter();
        privateListView.setAdapter(pAdapter);

        // 스와이프로 아이템 삭제를 구현하기 위한 설정
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(privateListView);

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
        ((MainActivity) getActivity()).refresh();
        super.onResume();
    }

    // 삭제할 수 있는 방향을 더 추가할 수도 있음 ex) ItemTouchHelper.RIGHT | ItemTouchHelper.RIGHT
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            String templateName = Templates.templateNames[position];
            String userID = getContext().getSharedPreferences("user", MODE_PRIVATE).getString("id", "aaa");

            deleteAndUpdateTemplate(getContext(), userID, templateName);
        }
    };

    public static void deleteAndUpdateTemplate(Context context, String userID, String templateName) {
        Retrofit retrofit = RetrofitClient.getInstance();
        TemplateService templateService = retrofit.create(TemplateService.class);

        templateService.deleteTemplate(userID, templateName).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(context, "템플릿이 삭제됐습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("TemplateService", "Failed API call with call: " + call
                        + ", exception: " + t);
            }
        });

        templateService.getPrivateNames(userID).enqueue(new Callback<Templates>() {
            @Override
            public void onResponse(Call<Templates> call, Response<Templates> response) {
                if (response.code() == 404) {
                    //Toast.makeText(MainActivity.this, "개인 템플릿 이름 호출에 실패했습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(MainActivity.this, "개인 템플릿 이름 호출에 성공했습니다.", Toast.LENGTH_SHORT).show();
                    Templates templates = response.body();
                    Templates.templateNames = templates.getTemplateNamesResult();
                    Templates.isSelectedArr = templates.getIsSelectedArrResult();
                }
            }

            @Override
            public void onFailure(Call<Templates> call, Throwable t) {
                Log.d("TemplateService", "Failed API call with call: " + call
                        + ", exception: " + t);
            }
        });
    }
}
