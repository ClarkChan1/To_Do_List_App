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

public class ItemAdapter extends ArrayAdapter<Item> {
    private MainActivity context;
    private ArrayList<Item> items;
    private ArrayList<Item> toRemove;
    private ArrayList<Item> repeatingItems;
    private int template_resource;
    static int instances = 0;

    public ItemAdapter(MainActivity context, int resource, ArrayList<Item> items) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
        toRemove = new ArrayList<>();
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
        CheckBox check = (CheckBox) convertView.findViewById(R.id.check);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView time = (TextView) convertView.findViewById(R.id.time);
        TextView category = (TextView) convertView.findViewById(R.id.category);

        if (items.get(position).getRepeat() != 0) {
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
                        toRemove.add(items.get(position));
                        instances--;
                        if (instances == 0) {
                            items.removeAll(toRemove);

                            //cancel notifications for all the items to remove and in the same loop add them to completedItems list
                            for (int a = 0; a < toRemove.size(); a++) {
                                Item currentItem = toRemove.get(a);
                                //put item in repeatingItems BEFORE changing the timeStamp to completion time because we want the initial due date when calculating the next due date for repeating items
                                if (currentItem.getRepeat() != 0) {
                                    repeatingItems.add(currentItem);
                                }
                                //only put the item into the completed section if it is not repeating
                                if (currentItem.getRepeat() == 0) {
                                    //get time of completion and set currentItem's timeStamp to it
                                    Calendar currentTime = Calendar.getInstance();
                                    currentItem.setTimeStamp(currentTime.getTimeInMillis());
                                    context.insertItem(context.completedItems, currentItem, "completed");
                                }
                                context.cancelNotification(currentItem.getNotificationID());

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
                                context.insertItem(context.listItems, currentItem, "todo");
                            }

                            context.resetAdapter();
                            //save everything
                            context.saveItems("ListItems.json", items);
                            context.saveItems("CompletedItems.json", context.completedItems);
                            //reset toRemove so it doesn't just infinitely grow
                            toRemove = new ArrayList<>();
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
        time.setText(getDueString(items.get(position)));

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
        itemPopup.setContentView(R.layout.item_popup);
        ImageView close = (ImageView) itemPopup.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemPopup.dismiss();
                context.checkOverdue();
                context.itemAdapter.notifyDataSetChanged();
            }
        });
        TextView name = (TextView) itemPopup.findViewById(R.id.name);
        TextView category = (TextView) itemPopup.findViewById(R.id.category);
        TextView repeating = (TextView) itemPopup.findViewById(R.id.repeating);
        TextView dueDate = (TextView) itemPopup.findViewById(R.id.dueDate);
        TextView dueTime = (TextView) itemPopup.findViewById(R.id.dueTime);
        TextView editButton = (TextView) itemPopup.findViewById(R.id.editButton);
        TextView deleteButton = (TextView) itemPopup.findViewById(R.id.deleteButton);


        name.setText(items.get(position).getName());
        category.setText(items.get(position).getCategory());
        if (items.get(position).getRepeat() == 0) {
            repeating.setVisibility(View.INVISIBLE);
        }
        Item currentItem = items.get(position);
        Calendar itemdueDate = Calendar.getInstance();
        itemdueDate.setTimeInMillis(currentItem.getTimeStamp());
        String dueDateText = DateFormat.getDateInstance().format(itemdueDate.getTime());
        dueDate.setText(dueDateText);
        dueTime.setText(getTimeString(currentItem));
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemPopup.dismiss();
                context.editItem(position);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.deleteItem(position);
                itemPopup.dismiss();
            }
        });
        itemPopup.show();
    }

}
