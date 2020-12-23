package com.example.ohjeom.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import com.example.ohjeom.models.ListViewItem;
import com.example.ohjeom.etc.CheckableLinearLayout;
import com.example.ohjeom.R;

public class TemplateMakerAdapter extends BaseAdapter {
    private ArrayList<ListViewItem> itemList = new ArrayList<ListViewItem>();
    public TemplateMakerAdapter(){
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_template_maker, parent, false);
        }

        TextView optionName = (TextView) convertView.findViewById(R.id.option) ;
        ListViewItem listViewItem = itemList.get(position);

        optionName.setText(listViewItem.getText());

        return convertView;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(String text) {
        ListViewItem item = new ListViewItem();
        item.setText(text);
        itemList.add(item);
    }
}
