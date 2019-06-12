package com.example.myfirstapplication;

import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.podcopic.animationlib.library.AnimationType;
import com.podcopic.animationlib.library.StartSmartAnimation;

import java.util.ArrayList;

public class ItemAdapter extends ArrayAdapter<Item> {
    private MainActivity context;
    private ArrayList<Item> items;
    private ArrayList<Item> toRemove;
    private int template_resource;
    static int instances = 0;

    public ItemAdapter(MainActivity context, int resource, ArrayList<Item> items) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
        toRemove = new ArrayList<>();
        template_resource = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(template_resource, parent, false);
        }

        Item currentItem = (Item) getItem(position);
        CheckBox check = (CheckBox) convertView.findViewById(R.id.check);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView time = (TextView) convertView.findViewById(R.id.time);
        TextView category = (TextView) convertView.findViewById(R.id.category);
        ImageView edit = (ImageView) convertView.findViewById(R.id.edit);

        //set click listener on checkbox and code animation
        final View finalConvertView = convertView;
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instances++;
                StartSmartAnimation.startAnimation((LinearLayout) finalConvertView.findViewById(R.id.item), AnimationType.SlideOutRight, 1000, 0, true);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toRemove.add(items.get(position));
                        instances--;
                        if (instances == 0) {
                            items.removeAll(toRemove);

                            //cancel notifications for all the items to remove and in the same loop add them to completedItems list
                            for (int a = 0; a < toRemove.size(); a++) {
                                context.insertItem(context.completedItems, toRemove.get(a), "completed");
                                context.cancelNotification(toRemove.get(a).getNotificationID());
                            }
                            context.resetAdapter();
                            //save everything
                            context.saveItems("ListItems.json", items);
                            context.saveItems("CompletedItems.json", context.completedItems);
                            //reset toRemove so it doesn't just infinitely grow
                            toRemove = new ArrayList<>();
                        }
                    }
                }, 1000);

            }
        });
        //set name and the rolling text
        name.setText(currentItem.getName());
        //name.setTypeface(context.professionalFont);
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
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.editItem(v, position);
            }
        });
        return convertView;
    }
}
