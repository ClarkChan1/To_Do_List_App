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
import java.util.Calendar;

public class OverdueItemsAdapter extends ArrayAdapter<Item> {
    private MainActivity context;
    private ArrayList<Item> overdueItems;
    private ArrayList<Item> toDelete;
    private ArrayList<Item> toComplete;
    private int template_resource;
    static int completeInstances = 0;
    static int deleteInstances = 0;

    public OverdueItemsAdapter(MainActivity context, int resource, ArrayList<Item> overdueItems) {
        super(context, resource, overdueItems);
        this.context = context;
        this.overdueItems = overdueItems;
        toDelete = new ArrayList<>();
        toComplete = new ArrayList<>();
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
        CheckBox check = (CheckBox) convertView.findViewById(R.id.check);
        ImageView deleteButton = (ImageView) convertView.findViewById(R.id.deleteButton);

        //set click listener on deleteButton and code animation
        final View finalConvertView = convertView;
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteInstances == 0) {
                    completeInstances++;
                    StartSmartAnimation.startAnimation((LinearLayout) finalConvertView.findViewById(R.id.item), AnimationType.FadeOut, 1000, 0, true);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toDelete.add(overdueItems.get(position));
                            completeInstances--;
                            if (completeInstances == 0) {
                                overdueItems.removeAll(toDelete);
                                context.switchAdapter(context.overdueItemsAdapter);
                                //save everything
                                context.saveItems("OverdueItems.json", overdueItems);
                                //reset toDelete so it doesn't just infinitely grow
                                toDelete = new ArrayList<>();
                            }
                        }
                    }, 1000);
                }
            }
        });

        //set click listener on checkbox and code animation
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (completeInstances == 0) {
                    deleteInstances++;
                    StartSmartAnimation.startAnimation((LinearLayout) finalConvertView.findViewById(R.id.item), AnimationType.SlideOutRight, 1000, 0, true);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toComplete.add(overdueItems.get(position));
                            deleteInstances--;
                            if (deleteInstances == 0) {
                                overdueItems.removeAll(toComplete);
                                for (int a = 0; a < toComplete.size(); a++) {
                                    context.insertItem(context.completedItems, toComplete.get(a), "completed");
                                }
                                context.switchAdapter(context.overdueItemsAdapter);
                                //save everything
                                context.saveItems("OverdueItems.json", overdueItems);
                                context.saveItems("CompletedItems.json", context.completedItems);
                                //reset toComplete so it doesn't just infinitely grow
                                toComplete = new ArrayList<>();
                            }
                        }
                    }, 1000);

                }
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
}
