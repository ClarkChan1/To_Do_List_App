package com.example.myfirstapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    RelativeLayout bottomPortion;
    //variable so we can change color of header background when switching sections
    LinearLayout header;
    Button createItemButton;

    private ArrayList<Item> listItems = new ArrayList<>();
    ItemAdapter itemAdapter;

    ArrayList<Item> completedItems = new ArrayList<>();
    private CompletedItemsAdapter completedItemsAdapter;

    private ArrayList<Item> overdueItems = new ArrayList<>();
    OverdueItemsAdapter overdueItemsAdapter;

    private int currentSection = 0;

    int notificationID;

    Handler displayDateAndTime;
    Runnable dateAndTimeRun;
    //global fonts to be used by all classes
//    Typeface headerFont;
//    Typeface professionalFont;
    private volatile boolean isPaused;

    final int millisInMinute = 60000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomPortion = (RelativeLayout) findViewById(R.id.bottomPortion);
        //set up the fonts to be used in this activity
//        headerFont = Typeface.createFromAsset(getAssets(), "fonts/nevis.ttf");
//        professionalFont = Typeface.createFromAsset(getAssets(), "fonts/Euphemia UCAS Regular 2.6.6.ttf");

        displayDateAndTime = new Handler();
        dateAndTimeRun = new Runnable() {
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
                displayDateAndTime.postDelayed(this, 1000);
            }
        };
        checkDate();

        listView = (ListView) findViewById(R.id.listView);
        header = (LinearLayout) findViewById(R.id.header);
        createItemButton = (Button) findViewById(R.id.createItemButton);


        if (savedInstanceState != null) {
            currentSection = savedInstanceState.getInt("section");
        } else {
            //make the toDoSection large initially
            TextView toDoTextView = findViewById(R.id.toDoSection);
            growSection(toDoTextView);
            //need to instantiate listItems first bc checkOverdue uses it
            listItems = DataManager.readItems(this, "ListItems.json");
            overdueItems = DataManager.readItems(this, "OverdueItems.json");
            checkOverdue();
            listItems = DataManager.readItems(this, "ListItems.json");
            itemAdapter = new ItemAdapter(this, R.layout.item_template, listItems);
            resetAdapter();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        isPaused = true;
        displayDateAndTime.removeCallbacks(dateAndTimeRun);
        DataManager.saveNotificationID(this, notificationID);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        isPaused = false;
        displayDateAndTime.post(dateAndTimeRun);
        checkDate();
        checkOverdue();
        switch (currentSection) {
            case 0:
                growSection(findViewById(R.id.toDoSection));
                header.setBackgroundColor(Color.parseColor("#3385ff"));
                createItemButton.setEnabled(true);
                createItemButton.setBackground(ContextCompat.getDrawable(this, R.drawable.add_button_border));
                bottomPortion.setBackgroundColor(Color.TRANSPARENT);
                itemAdapter = new ItemAdapter(this, R.layout.item_template, listItems);
                switchAdapter(itemAdapter);
                break;
            case 1:
                growSection(findViewById(R.id.completedSection));
                header.setBackgroundColor(Color.parseColor("#00cc66"));
                createItemButton.setEnabled(false);
                createItemButton.setVisibility(View.INVISIBLE);
                bottomPortion.setBackgroundColor(Color.parseColor("#00cc66"));
                completedItemsAdapter = new CompletedItemsAdapter(this, R.layout.completed_item_template, completedItems);
                switchAdapter(completedItemsAdapter);
                break;
            case 2:
                growSection(findViewById(R.id.overdueSection));
                header.setBackgroundColor(Color.parseColor("#ff0066"));
                createItemButton.setEnabled(false);
                createItemButton.setVisibility(View.INVISIBLE);
                bottomPortion.setBackgroundColor(Color.parseColor("#ff0066"));
                overdueItemsAdapter = new OverdueItemsAdapter(this, R.layout.overdue_item_template, overdueItems);
                switchAdapter(overdueItemsAdapter);
                break;
        }
    }

    public void checkDate() {
        //keep track of the current day
        long currentTotalDate = System.currentTimeMillis();
        SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd yyyy");
        String dateString = sdfDate.format(currentTotalDate);
        DataManager.checkDate(this, (new String[]{"ListItems.json", "CompletedItems.json", "OverdueItems.json"}), dateString);
        notificationID = DataManager.readNotificationID(this, "NotificationID.json");
        //reset all lists
        listItems = DataManager.readItems(this, "ListItems.json");
        overdueItems = DataManager.readItems(this, "OverdueItems.json");
        completedItems = DataManager.readItems(this, "CompletedItems.json");
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
        createIntent.putExtra("notificationID", notificationID);
        startActivityForResult(createIntent, 100);
    }

    public void editItem(int position) {
        Intent editIntent = new Intent(this, create_item.class);
        editIntent.putExtra("type", "edit");
        editIntent.putExtra("name", listItems.get(position).getName());
        editIntent.putExtra("category", listItems.get(position).getCategory());
        editIntent.putExtra("timeStamp", listItems.get(position).getTimeStamp());
        editIntent.putExtra("notificationID", notificationID);
        editIntent.putExtra("position", position);
        startActivityForResult(editIntent, 200);
    }

    public void deleteItem(int position) {
        cancelNotification(listItems.get(position).getNotificationID());
        listItems.remove(position); //MAY CAUSE ERROR IF DEFAULT VALUE IS USED
        DataManager.saveItems(this, "ListItems.json", listItems);
        itemAdapter.notifyDataSetChanged();
    }

    public void insertItem(ArrayList<Item> addTo, Item toAdd, String section) {
        if (addTo.isEmpty()) {
            addTo.add(toAdd);
        } else {
            boolean added = false;
            Calendar toAddTime = Calendar.getInstance();
            toAddTime.setTimeInMillis(toAdd.getTimeStamp());

            for (int a = 0; a < addTo.size(); a++) {
                Item currentItem = addTo.get(a);
                Calendar currentItemTime = Calendar.getInstance();
                currentItemTime.setTimeInMillis(currentItem.getTimeStamp());
                if (toAddTime.compareTo(currentItemTime) < 0) {
                    addTo.add(a, toAdd);
                    added = true;
                    break; //since the end condition is a<addTo.size(), this will run infinitely without this break statement because we added an item to the list, so size increased by 1 and will keep doing so as we add the same element again and again
                }
            }
            if (!added) {
                addTo.add(toAdd);
            }
        }
        if (section.equals("todo")) {
            DataManager.saveItems(this, "ListItems.json", listItems);
            itemAdapter.notifyDataSetChanged();
        }
        //if we are inserting item into the to do section, make a notification
        //todo (I did a bit already but)change this to make notifications set at a certain date instead of just a certain time in the current day
        if (section.equals("todo")) {
            Calendar taskDueTime = Calendar.getInstance();
            taskDueTime.setTimeInMillis(toAdd.getTimeStamp());
            setNotification(taskDueTime, toAdd.getName());
            notificationID++;
            DataManager.saveNotificationID(this, notificationID);
        }
    }

    public void setNotification(Calendar taskDueTime, String taskName) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("notificationID", notificationID);
        intent.putExtra("name", taskName);
        PendingIntent pi = PendingIntent.getBroadcast(this, notificationID, intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, taskDueTime.getTimeInMillis(), pi);
        }
    }

    public void cancelNotification(int toRemoveID) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, toRemoveID, intent, 0);
        am.cancel(pi);
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
        if ((currentSection != 0) && (OverdueItemsAdapter.completeInstances == 0) && (OverdueItemsAdapter.deleteInstances == 0)) {
            shrinkCurrent(currentSection);
            header.setBackgroundColor(Color.parseColor("#3385ff"));
            createItemButton.setEnabled(true);
            createItemButton.setVisibility(View.VISIBLE);
            createItemButton.setBackground(ContextCompat.getDrawable(this, R.drawable.add_button_border));
            bottomPortion.setBackgroundColor(Color.TRANSPARENT);
            currentSection = 0;
            growSection(v);
            listItems = DataManager.readItems(this, "ListItems.json");
            checkOverdue();
            itemAdapter = new ItemAdapter(this, R.layout.item_template, listItems);
            switchAdapter(itemAdapter);
        }
    }

    public void switchCompleted(View v) {
        if ((currentSection != 1) && (ItemAdapter.instances == 0) && (OverdueItemsAdapter.completeInstances == 0) && (OverdueItemsAdapter.deleteInstances == 0)) {
            shrinkCurrent(currentSection);
            header.setBackgroundColor(Color.parseColor("#00cc66"));
            createItemButton.setEnabled(false);
            createItemButton.setVisibility(View.INVISIBLE);
            bottomPortion.setBackgroundColor(Color.parseColor("#00cc66"));
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

            header.setBackgroundColor(Color.parseColor("#ff0066"));
            createItemButton.setEnabled(false);
            createItemButton.setVisibility(View.INVISIBLE);
            bottomPortion.setBackgroundColor(Color.parseColor("#ff0066"));
            currentSection = 2;
            growSection(v);
            overdueItems = DataManager.readItems(this, "OverdueItems.json");
            checkOverdue();
            overdueItemsAdapter = new OverdueItemsAdapter(this, R.layout.overdue_item_template, overdueItems);
            switchAdapter(overdueItemsAdapter);
        }
    }

    public void checkOverdue() {
        //get current time
        ArrayList<Item> toPutInOverdue = new ArrayList<>();
        for (int a = 0; a < listItems.size(); a++) {
            Item currentItem = listItems.get(a);
            Calendar currentItemDueTime = Calendar.getInstance();
            currentItemDueTime.setTimeInMillis(currentItem.getTimeStamp() + millisInMinute); //add a minute bc it's only overdue a minute after that time
            if (currentItemDueTime.compareTo(Calendar.getInstance()) < 0) {
                toPutInOverdue.add(currentItem);
            }
        }
        overdueItems.addAll(toPutInOverdue);
        listItems.removeAll(toPutInOverdue);
        DataManager.saveItems(this, "OverdueItems.json", overdueItems);
        DataManager.saveItems(this, "ListItems.json", listItems);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        itemAdapter = new ItemAdapter(this, R.layout.item_template, listItems);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                Item toAdd = new Item(data.getStringExtra("name"), data.getStringExtra("category"), data.getLongExtra("timeStamp", -1), data.getIntExtra("notificationID", -1));
                insertItem(listItems, toAdd, "todo");
            }
            if (requestCode == 200) {
                deleteItem(data.getIntExtra("position", -1));
                Item toAdd = new Item(data.getStringExtra("name"), data.getStringExtra("category"), data.getLongExtra("timeStamp", -1), data.getIntExtra("notificationID", -1));
                insertItem(listItems, toAdd, "todo");
            }
            checkOverdue();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("section", currentSection);
        DataManager.saveNotificationID(this, notificationID);
    }
}
