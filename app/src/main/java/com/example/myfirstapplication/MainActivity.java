package com.example.myfirstapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Item> listItems = new ArrayList<Item>();
    ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        View view = this.getWindow().getDecorView();
//        view.setBackgroundColor(Color.parseColor("#212121"));

        Thread dateAndTimeThread = new Thread() {
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView dateText = (TextView) findViewById(R.id.currentDate);
                                TextView timeText = (TextView) findViewById(R.id.currentTime);
                                long currentTotalDate = System.currentTimeMillis();
                                SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd yyyy\nhh:mm a");
                                String dateString = sdfDate.format(currentTotalDate);
                                String timeString = dateString.substring(dateString.indexOf("\n") + 1);
                                //this is to get rid of the leading 0 when hours is < 10
                                if (dateString.charAt(dateString.indexOf("\n") + 1) == '0') {
                                    timeString = dateString.substring(dateString.indexOf("\n") + 2);
                                }
                                dateString = dateString.substring(0, dateString.indexOf("\n"));
                                dateText.setText(dateString);
                                timeText.setText(timeString);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        dateAndTimeThread.start();

        //keep track of the current day
        long currentTotalDate = System.currentTimeMillis();
        SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd yyyy");
        String dateString = sdfDate.format(currentTotalDate);
        DataManager.checkDate(this, "CurrentDate.txt", dateString);

        listItems = DataManager.readItems(this, "ListItems.json");
        ListView listView = (ListView) findViewById(R.id.listView);
        itemAdapter = new ItemAdapter(this, R.layout.item_template, listItems);
        listView.setAdapter(itemAdapter);
        //printItems();
    }

    public void buttonOnClick(View v) {
        startActivityForResult(new Intent(this, create_item.class), 100);
    }

    public void insertItem(Item toAdd) {
        if (listItems.isEmpty()) {
            listItems.add(toAdd);
        } else {
            boolean added = false;
            for (int a = 0; a < listItems.size(); a++) {
                Item currentItem = listItems.get(a);
                if ((currentItem.getDueHour() > toAdd.getDueHour())
                        || ((currentItem.getDueHour() == toAdd.getDueHour()) && (currentItem.getDueMinute() > toAdd.getDueMinute()))) {
                    listItems.add(a, toAdd);
                    added = true;
                    break; //since the end condition is a<listItems.size(), this will run infinitely without this break statement because we added an item to the list, so size increased by 1 and will keep doing so as we add the same element again and again
                }
            }
            if (!added) {
                listItems.add(toAdd);
            }
        }
        //format the hour so it will be in 12hr format since earlier we needed 24 hr format to do conversions
        if (toAdd.getDueHour() > 12) {
            toAdd.setFormattedDueHour(toAdd.getDueHour() - 12);
        } else if (toAdd.getDueHour() == 0) {
            toAdd.setFormattedDueHour(12);
        } else {
            toAdd.setFormattedDueHour(toAdd.getDueHour());
        }
        DataManager.saveItem(this, "ListItems.json", listItems);
        itemAdapter.notifyDataSetChanged();
//        printItems();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//    public void printItems() {
//        LinearLayout canvas = findViewById(R.id.linearLayout);
//        canvas.removeAllViews();
//        for (int a = 0; a < listItems.size(); a++) {
//            Item currentItem = listItems.get(a);
//
//            LinearLayout itemLayout = new LinearLayout(this);
//            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
//            CheckBox itemCheck = new CheckBox(this);
//            TextView itemName = new TextView(this);
//            TextView timeText = new TextView(this);
//            TextView itemCategory = new TextView(this);
//
//            String nameString = currentItem.getName();
//            itemName.setText(nameString);
//            itemName.setTextSize(16);
//
//            String itemTime = currentItem.getDueHour() + ":";
//            if (currentItem.getDueMinute() < 10) {
//                itemTime += "0";
//            }
//            itemTime += currentItem.getDueMinute();
//            if (currentItem.isAfternoon()) {
//                itemTime += " PM";
//            } else {
//                itemTime += " AM";
//            }
//
//            timeText.setText(itemTime);
//            timeText.setTextSize(16);
//
//            if (currentItem.getCategory().equals("Work")) {
//                itemCategory.setText("Work");
//                itemCategory.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_border_red));
//            } else {
//                itemCategory.setText("Life");
//                itemCategory.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_border_green));
//            }
//
//            itemLayout.addView(itemCheck);
//            itemLayout.addView(itemName);
//            itemLayout.addView(timeText);
//            itemLayout.addView(itemCategory);
//
//            itemLayout.setWeightSum(9f);
//            LinearLayout.LayoutParams sizeText = new LinearLayout.LayoutParams
//                    (0, LinearLayout.LayoutParams.MATCH_PARENT);
//            sizeText.weight = 4f;
//            LinearLayout.LayoutParams sizeCheck = new LinearLayout.LayoutParams
//                    (0, LinearLayout.LayoutParams.MATCH_PARENT);
//            sizeCheck.weight = 1f;
//            LinearLayout.LayoutParams sizeTime = new LinearLayout.LayoutParams
//                    (0, LinearLayout.LayoutParams.MATCH_PARENT);
//            sizeTime.weight = 2f;
//            LinearLayout.LayoutParams sizeCategory = new LinearLayout.LayoutParams
//                    (0, LinearLayout.LayoutParams.MATCH_PARENT);
//            sizeCategory.weight = 2f;
//            sizeCategory.setMargins(0, 10, 0, 10);
//
//            itemName.setGravity(Gravity.CENTER);
//            itemName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//            itemName.setSingleLine(true);
//            itemName.setMarqueeRepeatLimit(-1);
//            itemName.setFocusableInTouchMode(true);
//            itemName.setFocusable(true);
//            itemName.setLayoutParams(sizeText);
//            itemName.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    TextView itemName = (TextView) v;
//                    itemName.setSelected(true);
//                }
//            });
//
//            itemCheck.setLayoutParams(sizeCheck);
//
//            timeText.setSingleLine(true);
//            timeText.setGravity(Gravity.CENTER);
//            timeText.setLayoutParams(sizeTime);
//
//            itemCategory.setSingleLine(true);
//            itemCategory.setGravity(Gravity.CENTER);
//            itemCategory.setLayoutParams(sizeCategory);
//
//            canvas.addView(itemLayout);
//
//            //add the horizontal line separator
//            View lineDivider = new View(this);
//            lineDivider.setBackgroundColor(Color.parseColor("#000000"));
//            LinearLayout.LayoutParams lineDividerMargins = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
//            lineDividerMargins.setMargins(0, 20, 0, 20);
//            lineDivider.setLayoutParams(lineDividerMargins);
//            canvas.addView(lineDivider);
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((requestCode == 100) && (resultCode == RESULT_OK)) {
            Item toAdd = new Item(data.getStringExtra("name"), data.getStringExtra("category"),
                    data.getIntExtra("dueHour", -1), data.getIntExtra("dueMinute", -1), data.getBooleanExtra("isAfternoon", false));
            insertItem(toAdd);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("items", listItems);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        listItems = savedInstanceState.getParcelableArrayList("items");
//        printItems();
    }
}
