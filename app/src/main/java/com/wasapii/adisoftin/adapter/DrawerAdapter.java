package com.wasapii.adisoftin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.wasapii.adisoftin.R;
import com.wasapii.adisoftin.model.DrawerItem;

import java.util.ArrayList;

/**
 * Created by adisoft3 on 10/12/16.
 */

public class DrawerAdapter extends BaseAdapter {
    Context context;

    ArrayList<DrawerItem> drawerList;

    public DrawerAdapter(Context context, ArrayList<DrawerItem> drawerList) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.drawerList= drawerList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return drawerList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        TextView title = null,tv_counter=null;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.drawer_list_item, null);

        }

        title = (TextView) convertView.findViewById(R.id.list_item);


        DrawerItem item = drawerList.get(position);

        title.setText(item.getTitle());
        //tv_counter.setText(item.getCount());

        return convertView;
    }
}
