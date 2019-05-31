package com.example.myfirstapplication;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.podcopic.animationlib.library.AnimationType;
import com.podcopic.animationlib.library.StartSmartAnimation;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {
    private Context context;
    private List<Item> items;
    private int template_resource;

    public ItemAdapter(Context context, int resource, ArrayList<Item> items) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
        template_resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new TextView(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(template_resource, parent, false);
        }

        Item currentItem = (Item) getItem(position);
        CheckBox check = (CheckBox) convertView.findViewById(R.id.check);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView time = (TextView) convertView.findViewById(R.id.time);
        TextView category = (TextView) convertView.findViewById(R.id.category);

        //set click listener on checkbox and code animation
        final View finalConvertView = convertView;
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartSmartAnimation.startAnimation((LinearLayout) finalConvertView.findViewById(R.id.item), AnimationType.SlideOutRight, 1000,0,true);
            }
        });
        //set name and the rolling text
        name.setText(currentItem.getName());
        name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        name.setSingleLine(true);
        name.setMarqueeRepeatLimit(-1);
        name.setFocusableInTouchMode(true);
        name.setFocusable(true);
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView itemName = (TextView) v;
                itemName.setSelected(true);
            }
        });

        //set time
        int dueHour = currentItem.getDueHour();
        int dueMinute = currentItem.getDueMinute();
        String itemTime = "";
        if(dueHour % 12 == 0){
            itemTime = 12+":";
        } else{
            itemTime = (dueHour % 12) + ":";
        }
        if (dueMinute < 10) {
            itemTime += "0";
        }
        itemTime += dueMinute;
        if (dueHour >= 12) {
            itemTime += " PM";
        } else {
            itemTime += " AM";
        }
        time.setText(itemTime);

        //set category
        if (currentItem.getCategory().equals("Work")) {
            category.setText("Work");
            category.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_border_red));
        } else {
            category.setText("Life");
            category.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_border_green));
        }

        return convertView;
    }
}
