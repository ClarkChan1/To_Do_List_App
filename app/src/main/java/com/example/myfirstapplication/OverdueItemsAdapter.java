package com.example.myfirstapplication;

import android.app.Dialog;
import android.graphics.Color;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class OverdueItemsAdapter extends ArrayAdapter<Item> {
    private MainActivity context;
    private ArrayList<Item> overdueItems;
    private ArrayList<Item> toComplete;
    private ArrayList<Item> repeatingItems;
    private int template_resource;
    static int instances = 0;
    static Dialog itemPopup;

    public OverdueItemsAdapter(MainActivity context, int resource, ArrayList<Item> overdueItems) {
        super(context, resource, overdueItems);
        this.context = context;
        this.overdueItems = overdueItems;
        toComplete = new ArrayList<>();
        repeatingItems = new ArrayList<>();
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

        if (overdueItems.get(position).getRepeat() != 0) {
            name.setBackgroundColor(Color.parseColor("#21aaff"));
        }

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
                        toComplete.add(overdueItems.get(position));
                        instances--;
                        if (instances == 0) {
                            overdueItems.removeAll(toComplete);
                            for (int a = 0; a < toComplete.size(); a++) {
                                Item currentItem = toComplete.get(a);
                                //put item in repeatingItems BEFORE changing the timeStamp to completion time because we want the initial due date when calculating the next due date for repeating items
                                if (currentItem.getRepeat() != 0) {
                                    repeatingItems.add(currentItem);
                                }
                                if (currentItem.getRepeat() == 0) {
                                    //get time of completion
                                    Calendar currentTime = Calendar.getInstance();
                                    currentItem.setTimeStamp(currentTime.getTimeInMillis());
                                    context.insertItem(context.completedItems, currentItem, "completed");
                                }
                            }

                            //add back any items that are set to repeating and set their time to be the next interval
                            for (int b = 0; b < repeatingItems.size(); b++) {
                                Item currentItem = repeatingItems.get(b);
                                Calendar currentItemTime = Calendar.getInstance();
                                currentItemTime.setTimeInMillis(currentItem.getTimeStamp());
                                switch (currentItem.getRepeat()) {
                                    case 1:
                                        currentItemTime.add(Calendar.DAY_OF_MONTH, 1);
                                        break;
                                    case 2:
                                        currentItemTime.add(Calendar.WEEK_OF_MONTH, 1);
                                        break;
                                    case 3:
                                        currentItemTime.add(Calendar.MONTH, 1);
                                        break;
                                    case 4:
                                        currentItemTime.add(Calendar.YEAR, 1);
                                        break;
                                }
                                currentItem.setTimeStamp(currentItemTime.getTimeInMillis());
                                //compare this new due time to current time to decide whether to keep it in overdue or send it back to the to do section
                                Calendar currentTime = Calendar.getInstance();
                                if (currentItemTime.compareTo(currentTime) > 0) {
                                    context.insertItem(context.listItems, currentItem, "todo");
                                } else {
                                    context.insertItem(context.overdueItems, currentItem, "overdue");
                                }
                            }

                            context.switchAdapter(context.overdueItemsAdapter);
                            //save everything
                            context.saveItems("OverdueItems.json", overdueItems);
                            context.saveItems("CompletedItems.json", context.completedItems);
                            //reset toComplete so it doesn't just infinitely grow
                            toComplete = new ArrayList<>();
                            //reset repeatingItems so it doesn't just infinitely grow
                            repeatingItems = new ArrayList<>();
                        }
                    }
                }, 1000);
            }
        });

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
        time.setText(getDueString(overdueItems.get(position)));

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
        itemPopup = new Dialog(context);
        itemPopup.setContentView(R.layout.overdue_item_popup);
        ImageView close = (ImageView) itemPopup.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemPopup.dismiss();
            }
        });
        TextView name = (TextView) itemPopup.findViewById(R.id.name);
        TextView category = (TextView) itemPopup.findViewById(R.id.category);
        TextView repeating = (TextView) itemPopup.findViewById(R.id.repeating);
        TextView dueDate = (TextView) itemPopup.findViewById(R.id.dueDate);
        TextView dueTime = (TextView) itemPopup.findViewById(R.id.dueTime);
        TextView deleteItem = (TextView) itemPopup.findViewById(R.id.deleteButton);

        name.setText(overdueItems.get(position).getName());
        category.setText(overdueItems.get(position).getCategory());
        if (overdueItems.get(position).getRepeat() == 0) {
            repeating.setVisibility(View.INVISIBLE);
        }
        Item currentItem = overdueItems.get(position);
        Calendar itemdueDate = Calendar.getInstance();
        itemdueDate.setTimeInMillis(currentItem.getTimeStamp());
        String dueDateText = DateFormat.getDateInstance().format(itemdueDate.getTime());
        dueDate.setText(dueDateText);
        dueTime.setText(getTimeString(currentItem));

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overdueItems.remove(position);
                context.switchAdapter(context.overdueItemsAdapter);
                //save everything
                context.saveItems("OverdueItems.json", overdueItems);
                itemPopup.dismiss();
            }
        });

        itemPopup.show();
    }
}
