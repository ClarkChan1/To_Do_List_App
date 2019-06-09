package com.example.myfirstapplication;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView listView;

    private ArrayList<Item> listItems = new ArrayList<>();
    private ItemAdapter itemAdapter;

    ArrayList<Item> completedItems = new ArrayList<>();
    private CompletedItemsAdapter completedItemsAdapter;

    private ArrayList<Item> overdueItems = new ArrayList<>();
    private OverdueItemsAdapter overdueItemsAdapter;

    private int currentSection = 0;
    //global fonts to be used by all classes
    Typeface headerFont;
    Typeface professionalFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //set up the fonts to be used in this activity
        headerFont = Typeface.createFromAsset(getAssets(), "fonts/nevis.ttf");
        professionalFont = Typeface.createFromAsset(getAssets(), "fonts/Euphemia UCAS Regular 2.6.6.ttf");
//        View view = this.getWindow().getDecorView();
//        view.setBackgroundColor(Color.parseColor("#212121"));

        listView = (ListView) findViewById(R.id.listView);
        if (savedInstanceState != null) {
            currentSection = savedInstanceState.getInt("section");
            switch (currentSection) {
                case 0:
                    growSection(findViewById(R.id.toDoSection));
                    listItems = DataManager.readItems(this, "ListItems.json");
                    itemAdapter = new ItemAdapter(this, R.layout.item_template, listItems);
                    switchAdapter(itemAdapter);
                    break;
                case 1:
                    growSection(findViewById(R.id.completedSection));
                    completedItems = DataManager.readItems(this, "CompletedItems.json");
                    completedItemsAdapter = new CompletedItemsAdapter(this, R.layout.completed_item_template, completedItems);
                    switchAdapter(completedItemsAdapter);
                    break;
                case 2:
                    growSection(findViewById(R.id.overdueSection));
                    break;
            }
        } else {
            //make the toDoSection large initially
            TextView toDoTextView = findViewById(R.id.toDoSection);
            growSection(toDoTextView);

            listItems = DataManager.readItems(this, "ListItems.json");
            itemAdapter = new ItemAdapter(this, R.layout.item_template, listItems);
            resetAdapter();
        }

//        saveItems("ListItems.json", listItems);

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
                                //dateText.setTypeface(headerFont);
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
        DataManager.checkDate(this, (new String[]{"ListItems.json", "CompletedItems.json"}), dateString);
    }

    public void resetAdapter() {
        listView.setAdapter(itemAdapter);
//        DataManager.saveItems(this,"ListItems.json", listItems);
    }

    public void saveItems(String fileName, ArrayList<Item> toSave) {
        DataManager.saveItems(this, fileName, toSave);
    }

    public void switchAdapter(ArrayAdapter adapter) {
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void createItem(View v) {
        Intent createIntent = new Intent(this, create_item.class);
        createIntent.putExtra("type", "create");
        startActivityForResult(createIntent, 100);
    }

    public void editItem(View v, int position) {
        Intent editIntent = new Intent(this, create_item.class);
        editIntent.putExtra("type", "edit");
        editIntent.putExtra("name", listItems.get(position).getName());
        editIntent.putExtra("category", listItems.get(position).getCategory());
        editIntent.putExtra("dueHour", listItems.get(position).getDueHour());
        editIntent.putExtra("dueMinute", listItems.get(position).getDueMinute());
        editIntent.putExtra("position", position);
        startActivityForResult(editIntent, 200);
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
        DataManager.saveItems(this, "ListItems.json", listItems);
        itemAdapter.notifyDataSetChanged();
//        printItems();
    }

    public void shrinkCurrent(int currentSection) {
        Animation shrinkAnimation = AnimationUtils.loadAnimation(this, R.anim.shrink);
        TextView toShrink = new TextView(this);
        switch (currentSection) {
            case 0:
                toShrink = (TextView) findViewById(R.id.toDoSection);
                break;
            case 1:
                toShrink = (TextView) findViewById(R.id.completedSection);
                break;
            case 2:
                toShrink = (TextView) findViewById(R.id.overdueSection);
                break;
            default:
                break;
        }
        toShrink.startAnimation(shrinkAnimation);
    }

    public void growSection(View v) {
        Animation growAnimation = AnimationUtils.loadAnimation(this, R.anim.grow);
        TextView toGrow = (TextView) v;
        toGrow.startAnimation(growAnimation);
    }

    public void switchToDo(View v) {
        if ((currentSection != 0) && (ItemAdapter.instances == 0)) {
            shrinkCurrent(currentSection);
            currentSection = 0;
            growSection(v);
            listItems = DataManager.readItems(this, "ListItems.json");
            itemAdapter = new ItemAdapter(this, R.layout.item_template, listItems);
            switchAdapter(itemAdapter);
        }
    }

    public void switchCompleted(View v) {
        if ((currentSection != 1) && (ItemAdapter.instances == 0)) {
            shrinkCurrent(currentSection);
            currentSection = 1;
            growSection(v);
            completedItems = DataManager.readItems(this, "CompletedItems.json");
            completedItemsAdapter = new CompletedItemsAdapter(this, R.layout.completed_item_template, completedItems);
            switchAdapter(completedItemsAdapter);
        }
    }

    public void switchOverdue(View v) {
        if ((currentSection != 2) && (ItemAdapter.instances == 0)) {
            shrinkCurrent(currentSection);
            currentSection = 2;
            growSection(v);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((requestCode == 100) && (resultCode == RESULT_OK)) {
            Item toAdd = new Item(data.getStringExtra("name"), data.getStringExtra("category"),
                    data.getIntExtra("dueHour", -1), data.getIntExtra("dueMinute", -1));
            insertItem(toAdd);
        }
        if ((requestCode == 200) && (resultCode == RESULT_OK)) {
            listItems.remove(data.getIntExtra("position", -1)); //MAY CAUSE ERROR IF DEFAULT VALUE IS USED
            if (data.getStringExtra("action").equals("edit")) {
                Item toAdd = new Item(data.getStringExtra("name"), data.getStringExtra("category"),
                        data.getIntExtra("dueHour", -1), data.getIntExtra("dueMinute", -1));
                insertItem(toAdd);
            } else {
                DataManager.saveItems(this, "ListItems.json", listItems);
                itemAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("section", currentSection);
    }
}
