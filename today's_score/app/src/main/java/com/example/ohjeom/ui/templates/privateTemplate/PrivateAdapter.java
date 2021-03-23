package com.example.ohjeom.ui.templates.privateTemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import com.example.ohjeom.R;
import com.example.ohjeom.models.Storage;
import com.example.ohjeom.models.Template;
import com.example.ohjeom.models.Templates;
import com.example.ohjeom.retrofit.RetrofitClient;
import com.example.ohjeom.retrofit.TemplateService;
import com.example.ohjeom.ui.templates.PrivateTemplateActivity;

public class PrivateAdapter extends RecyclerView.Adapter<PrivateAdapter.ViewHolder> {
    private String[] templateNames;
    private String[] isSelectedArr;

    public PrivateAdapter(){
        templateNames = Templates.templateNames;
        isSelectedArr = Templates.isSelectedArr;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView templateName;
        LinearLayout templateLayout;

        ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조. (hold strong reference)
            templateName = itemView.findViewById(R.id.template_name);
            templateLayout = itemView.findViewById(R.id.template_layout);

            //클릭 이벤트
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition() ;
                    String templateName = Templates.templateNames[pos];
                    String userID = view.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).getString("id","aaa");
                    getPrivateDetails(templateName, userID);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (pos != RecyclerView.NO_POSITION) {
                                Intent intent = new Intent(view.getContext(), PrivateTemplateActivity.class);
                                view.getContext().startActivity(intent);
                            }
                        }
                    }, 1000); // 1초 딜레이
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition() ;
                    String templateName = Templates.templateNames[pos];
                    String userID = view.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).getString("id","aaa");

                    PrivateFragment.deleteAndUpdateTemplate(view.getContext(), userID, templateName);
                    return true;
                }
            });
        }
    }

    public void getPrivateDetails(String templateName, String userID) {
        Retrofit retrofit = RetrofitClient.getInstance();
        TemplateService templateService = retrofit.create(TemplateService.class);

        templateService.getPrivateDetails(userID, templateName).enqueue(new Callback<Template>() {
            @Override
            public void onResponse(Call<Template> call, Response<Template> response) {
                Template template = response.body();
                Storage.setTemplate(template);
                Log.d("@@@@@@", "Private Adapter Function: " + template.getNameResult());
                Storage.getTemplate().parseInfo();
            }
            @Override
            public void onFailure(Call<Template> call, Throwable t) {
                Log.d("TemplateService", "Failed API call with call: " + call
                        + ", exception: " + t);
            }
        });
    }

    @Override
    public PrivateAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = inflater.inflate(R.layout.item_template_private, parent, false);
        PrivateAdapter.ViewHolder vh = new PrivateAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PrivateAdapter.ViewHolder holder, int position) {
        String name = templateNames[position];

        holder.templateName.setText(name);

        if (isSelectedArr[position].equals("true")) {
            holder.templateLayout.setBackgroundColor(Color.parseColor("#FFF2CC"));
        } else {
            if (position%2 == 0){
                holder.templateLayout.setBackgroundColor(Color.parseColor("#80FFFFFF"));
            } else {
                holder.templateLayout.setBackgroundColor(Color.parseColor("#10FFFFFF"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return templateNames.length;
    }
}