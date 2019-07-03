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
import java.text.SimpleDateFormat;
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
        time.setText(getDueString(completedItems.get(position)));

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

    public String getDueString(Item currentItem) {
        Calendar currentItemTime = Calendar.getInstance();
        currentItemTime.setTimeInMillis(currentItem.getTimeStamp());
        Calendar currentTime = Calendar.getInstance();
        String itemTime;
        if (currentTime.get(Calendar.YEAR) == currentItemTime.get(Calendar.YEAR)) {
            if (currentTime.get(Calendar.MONTH) == currentItemTime.get(Calendar.MONTH)) {
                if (currentTime.get(Calendar.WEEK_OF_MONTH) == currentItemTime.get(Calendar.WEEK_OF_MONTH)) {
                    if (currentTime.get(Calendar.DAY_OF_MONTH) == currentItemTime.get(Calendar.DAY_OF_MONTH)) {
                        //show due time
                        itemTime = getTimeString(currentItem);
                    } else {
                        //show day of week like mon, tue, wed
                        SimpleDateFormat dayOfWeekFormatter = new SimpleDateFormat("E");
                        dayOfWeekFormatter.setCalendar(currentItemTime);
                        itemTime = dayOfWeekFormatter.format(currentItemTime.getTime());
                    }
                } else {
                    //show current month
                    SimpleDateFormat monthFormatter = new SimpleDateFormat("MMMM");
                    monthFormatter.setCalendar(currentItemTime);
                    itemTime = monthFormatter.format(currentItemTime.getTime());
                }
            } else {
                //show month
                SimpleDateFormat monthFormatter = new SimpleDateFormat("MMMM");
                monthFormatter.setCalendar(currentItemTime);
                itemTime = monthFormatter.format(currentItemTime.getTime());
            }
        } else {
            SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy");
            yearFormatter.setCalendar(currentItemTime);
            itemTime = yearFormatter.format(currentItemTime.getTime());
            return itemTime;
        }
        return itemTime;
    }

    public String getTimeString(Item currentItem) {
        Calendar currentItemTime = Calendar.getInstance();
        currentItemTime.setTimeInMillis(currentItem.getTimeStamp());
        String itemTime;
        int dueHour = currentItemTime.get(Calendar.HOUR_OF_DAY);
        int dueMinute = currentItemTime.get(Calendar.MINUTE);
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

    public void showPopup(final int position) {
        final Dialog itemPopup = new Dialog(context);
        itemPopup.setContentView(R.layout.completed_item_popup);
        ImageView close = (ImageView) itemPopup.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemPopup.dismiss();
            }
        });
        TextView name = (TextView) itemPopup.findViewById(R.id.name);
        TextView category = (TextView) itemPopup.findViewById(R.id.category);
        TextView dueDate = (TextView) itemPopup.findViewById(R.id.dueDate);
        TextView dueTime = (TextView) itemPopup.findViewById(R.id.dueTime);
        TextView deleteItem = (TextView) itemPopup.findViewById(R.id.deleteButton);

        name.setText(completedItems.get(position).getName());
        category.setText(completedItems.get(position).getCategory());

        Item currentItem = completedItems.get(position);
        Calendar itemCompleteDate = Calendar.getInstance();
        itemCompleteDate.setTimeInMillis(currentItem.getTimeStamp());
        String completedDateText = DateFormat.getDateInstance().format(itemCompleteDate.getTime());
        dueDate.setText(completedDateText);
        dueTime.setText(getTimeString(currentItem));

        Calendar itemdueDate = Calendar.getInstance();
        itemdueDate.setTimeInMillis(currentItem.getTimeStamp());
        String dueDateText = DateFormat.getDateInstance().format(itemdueDate.getTime());
        dueDate.setText(dueDateText);
        dueTime.setText(getTimeString(currentItem));

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completedItems.remove(position);
                context.switchAdapter(context.completedItemsAdapter);
                //save everything
                context.saveItems("CompletedItems.json", completedItems);
                itemPopup.dismiss();
            }
        });

        itemPopup.show();
    }
}
