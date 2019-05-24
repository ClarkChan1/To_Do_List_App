package com.example.myfirstapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Item> listItems = new ArrayList<Item>();

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
                                long currentTotalDate = System.currentTimeMillis();
                                SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd yyyy\nhh:mm a");
                                String dateString = sdfDate.format(currentTotalDate);
                                dateString = String.format("%-30s", dateString.substring(0, dateString.indexOf("\n"))) + dateString.substring(dateString.indexOf("\n") + 1);
                                dateText.setText(dateString);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        dateAndTimeThread.start();
        if(savedInstanceState != null){
            printItems();
        }
    }

    public void buttonOnClick(View v) {
        Button addNew = (Button) v;
        startActivityForResult(new Intent(this, create_item.class), 100);
    }

    public void insertItem(Item toAdd) {
        if (listItems.isEmpty()) {
            listItems.add(toAdd);
        } else {
            boolean added = false;
            for (int a = 0; a < listItems.size(); a++) {
                if ((listItems.get(a).getDueHour() > toAdd.getDueHour())
                        || ((listItems.get(a).getDueHour() == toAdd.getDueHour()) && (listItems.get(a).getDueMinute() > toAdd.getDueMinute()))) {
                    listItems.add(a, toAdd);
                    added = true;
                    break; //since the end condition is a<listItems.size(), this will run infinitely without this break statement because we added an item to the list, so size increased by 1 and will keep doing so as we add the same element again and again
                }
            }
            if (!added) {
                listItems.add(toAdd);
            }
        }
        printItems();
    }

    public void printItems() {
        LinearLayout canvas = findViewById(R.id.linearLayout);
        canvas.removeAllViews();
        for (int a = 0; a < listItems.size(); a++) {
            Item currentItem = listItems.get(a);

            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
            CheckBox itemCheck = new CheckBox(this);
            TextView itemName = new TextView(this);
            TextView timeText = new TextView(this);
            TextView itemCategory = new TextView(this);

            String nameString = currentItem.getName();
            String itemTime = currentItem.getDueHour() + ":";
            if (currentItem.getDueMinute() < 10) {
                itemTime += "0";
            }
            itemTime += currentItem.getDueMinute();

            itemName.setText(nameString);
            itemName.setTextSize(16);
            timeText.setText(itemTime);
            timeText.setTextSize(16);

            if (currentItem.getCategory().equals("Work")) {
                itemCategory.setText("Work");
                itemCategory.setBackgroundColor(Color.parseColor("#ff4d4d"));
            } else {
                itemCategory.setText("Life");
                itemCategory.setBackgroundColor(Color.parseColor("#80ff80"));
            }

            itemLayout.addView(itemCheck);
            itemLayout.addView(itemName);
            itemLayout.addView(timeText);
            itemLayout.addView(itemCategory);

            itemLayout.setWeightSum(9f);
            LinearLayout.LayoutParams sizeText = new LinearLayout.LayoutParams
                    (0, LinearLayout.LayoutParams.MATCH_PARENT);
            sizeText.weight = 4f;
            LinearLayout.LayoutParams sizeCheck = new LinearLayout.LayoutParams
                    (0, LinearLayout.LayoutParams.MATCH_PARENT);
            sizeCheck.weight = 1f;
            LinearLayout.LayoutParams sizeRest = new LinearLayout.LayoutParams
                    (0, LinearLayout.LayoutParams.MATCH_PARENT);
            sizeRest.weight = 2f;

            itemName.setGravity(Gravity.CENTER);
            itemName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            itemName.setSingleLine(true);
            itemName.setMarqueeRepeatLimit(-1);
            itemName.setFocusableInTouchMode(true);
            itemName.setFocusable(true);
            itemName.setSelected(true);
            itemName.setLayoutParams(sizeText);

            itemCheck.setLayoutParams(sizeCheck);

            timeText.setSingleLine(true);
            timeText.setGravity(Gravity.CENTER);
            timeText.setLayoutParams(sizeRest);

            itemCategory.setSingleLine(true);
            itemCategory.setGravity(Gravity.CENTER);
            itemCategory.setLayoutParams(sizeRest);

            canvas.addView(itemLayout);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((requestCode == 100) && (resultCode == RESULT_OK)) {
            Item toAdd = new Item(data.getStringExtra("name"), data.getStringExtra("category"),
                    data.getIntExtra("dueHour", -1), data.getIntExtra("dueMinute", -1));
            insertItem(toAdd);
            System.out.println(data.getStringExtra("name"));
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
        printItems();
    }
}
