package com.example.ohjeom.ui.templates.privateTemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ohjeom.R;
import com.example.ohjeom.models.Template;
import com.example.ohjeom.ui.templates.TemplateActivity;

import java.util.ArrayList;

public class PrivateAdapter extends RecyclerView.Adapter<PrivateAdapter.ViewHolder> {

    private ArrayList<Template> privateList;

    public PrivateAdapter(ArrayList<Template> list){
        privateList = list;
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
                    if (pos != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(view.getContext(), TemplateActivity.class);
                        intent.putExtra("position",pos);
                        view.getContext().startActivity(intent);
                    }
                }
            });
        }
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
        Template item = privateList.get(position);

        holder.templateName.setText(item.getTemplateName());

        if (item.isSelected()) {
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
        return privateList.size();
    }
}
