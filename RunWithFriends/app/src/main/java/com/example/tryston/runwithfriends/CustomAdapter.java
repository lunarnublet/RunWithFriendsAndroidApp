package com.example.tryston.runwithfriends;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<String> {

    Context context;
    int layoutResourceId;
    ArrayList<String> routeNames = null;

    public CustomAdapter(Context context, int layoutResourceId, ArrayList<String> routeNames) {
        super(context, layoutResourceId, routeNames);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.routeNames = routeNames;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TextView holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = (TextView)row.findViewById(R.id.text1);

            row.setTag(holder);
        }
        else
        {
            holder = (TextView) row.getTag();
        }

        String name = routeNames.get(position);
        holder.setText(name);

        return row;
    }
}
