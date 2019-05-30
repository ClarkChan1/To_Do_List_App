package com.example.myfirstapplication;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class ViewHolder {
    private CheckBox check;
    private TextView name;
    private TextView time;
    private TextView category;

    public ViewHolder(View view){
        check = (CheckBox) view.findViewById(R.id.check);
        name = (TextView) view.findViewById(R.id.name);
        time = (TextView)view.findViewById(R.id.time);
        category = (TextView) view.findViewById(R.id.category);
        view.setTag(this);
    }

    public CheckBox getCheck() {
        return check;
    }

    public TextView getName() {
        return name;
    }

    public TextView getTime() {
        return time;
    }

    public TextView getCategory() {
        return category;
    }
}
