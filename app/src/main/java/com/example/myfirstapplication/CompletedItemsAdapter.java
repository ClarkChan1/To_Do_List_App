package com.example.myfirstapplication;

import android.app.Dialog;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CompletedItemsAdapter extends ArrayAdapter<Item> {
    private MainActivity context;
    private int template_resource;
    private ArrayList<Item> completedItems;

    public CompletedItemsAdapter(MainActivity context, int resource, ArrayList<Item> completedItems) {
        super(context, resource, completedItems);
        this.context = context;
        this.completedItems = completedItems;
        template_resource = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(template_resource, parent, false);
        }

        Item currentItem = (Item) getItem(position);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView time = (TextView) convertView.findViewById(R.id.time);
        TextView category = (TextView) convertView.findViewById(R.id.category);

        //set name and the rolling text
        name.setText(currentItem.getName());
        name.setEllipsize(TextUtils.TruncateAt.END);
        name.setSingleLine(true);
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(position);

//                TextView itemName = (TextView) v;
//                itemName.setSelected(true);
            }
        });

        //set time
        Calendar currentItemTime = Calendar.getInstance();
        currentItemTime.setTimeInMillis(currentItem.getTimeStamp());
        int dueHour = currentItemTime.get(Calendar.HOUR_OF_DAY);
        int dueMinute = currentItemTime.get(Calendar.MINUTE);
        String itemTime = "";
        if (dueHour % 12 == 0) {
            itemTime = 12 + ":";
        } else {
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
            category.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_border_work));
        } else {
            category.setText("Life");
            category.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_border_life));
        }
        return convertView;
    }

    public String getTimeString(Item currentItem){
        Calendar currentItemTime = Calendar.getInstance();
        currentItemTime.setTimeInMillis(currentItem.getTimeStamp());
        int dueHour = currentItemTime.get(Calendar.HOUR_OF_DAY);
        int dueMinute = currentItemTime.get(Calendar.MINUTE);
        String itemTime = "";
        if (dueHour % 12 == 0) {
            itemTime = 12 + ":";
        } else {
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
        return itemTime;
    }

    public void showPopup(int position){
        final Dialog itemPopup = new Dialog(context);
        itemPopup.setContentView(R.layout.item_popup);
        ImageView close = (ImageView) itemPopup.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemPopup.dismiss();
            }
        });
        TextView name = (TextView)itemPopup.findViewById(R.id.name);
        TextView category= (TextView)itemPopup.findViewById(R.id.category);
        TextView dueDate= (TextView)itemPopup.findViewById(R.id.dueDate);
        TextView dueTime= (TextView)itemPopup.findViewById(R.id.dueTime);
        name.setText(completedItems.get(position).getName());
        category.setText(completedItems.get(position).getCategory());

        Item currentItem = completedItems.get(position);
        Calendar itemdueDate = Calendar.getInstance();
        itemdueDate.setTimeInMillis(currentItem.getTimeStamp());
        String dueDateText = DateFormat.getDateInstance().format(itemdueDate.getTime());
        dueDate.setText(dueDateText);
        dueTime.setText(getTimeString(currentItem));
        itemPopup.show();
    }

}
