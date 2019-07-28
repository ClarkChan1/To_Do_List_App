package com.to_do.to_do_list;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private ListView listView;

    //variable so we can change color of header background when switching sections
    LinearLayout header;
    ImageButton actionButton;

    ArrayList<Item> listItems = new ArrayList<>();
    ItemAdapter itemAdapter;

    ArrayList<Item> completedItems = new ArrayList<>();
    CompletedItemsAdapter completedItemsAdapter;

    ArrayList<Item> overdueItems = new ArrayList<>();
    OverdueItemsAdapter overdueItemsAdapter;

    private int currentSection = 0;

    int notificationID;

    ListOrderTracker listOrderTracker;

    Handler displayDateAndTime;
    Runnable dateAndTimeRun;

    //dialogs we need to keep track of in case the user rotates the screen while the dialog is open.
    Dialog deleteCompletedDialog;
    Dialog deleteOverdueDialog;


    //global fonts to be used by all classes
//    Typeface headerFont;
//    Typeface professionalFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                SimpleDateFormat sdfDate = new SimpleDateFormat("E MMM dd\nhh:mm a");
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
        getData();

        listView = (ListView) findViewById(R.id.listView);
        header = (LinearLayout) findViewById(R.id.header);
        actionButton = (ImageButton) findViewById(R.id.createItemButton);


        //get lists
        listItems = DataManager.readItems(this, "ListItems.json");
        completedItems = DataManager.readItems(this, "CompletedItems.json");
        overdueItems = DataManager.readItems(this, "OverdueItems.json");

        //get adapters
        itemAdapter = new ItemAdapter(this, R.layout.item_template, listItems);
        completedItemsAdapter = new CompletedItemsAdapter(this, R.layout.completed_item_template, completedItems);
        overdueItemsAdapter = new OverdueItemsAdapter(this, R.layout.item_template, overdueItems);


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
        switch (currentSection) {
            case 0:
                if (ItemAdapter.itemPopup != null) {
                    if (ItemAdapter.itemPopup.isShowing()) {
                        ItemAdapter.itemPopup.dismiss();
                    }
                }
                break;
            case 1:
                if (CompletedItemsAdapter.itemPopup != null) {
                    if (CompletedItemsAdapter.itemPopup.isShowing()) {
                        CompletedItemsAdapter.itemPopup.dismiss();
                    }
                }
                if(deleteCompletedDialog != null){
                    if(deleteCompletedDialog.isShowing()) {
                        deleteCompletedDialog.dismiss();
                    }
                }
                break;
            case 2:
                if (OverdueItemsAdapter.itemPopup != null) {
                    if (OverdueItemsAdapter.itemPopup.isShowing()) {
                        OverdueItemsAdapter.itemPopup.dismiss();
                    }
                }
                if(deleteOverdueDialog != null){
                    if(deleteOverdueDialog.isShowing()) {
                        deleteOverdueDialog.dismiss();
                    }
                }
                break;
        }
//        isPaused = true;
        displayDateAndTime.removeCallbacks(dateAndTimeRun);
        DataManager.saveNotificationID(this, notificationID);
        DataManager.saveListOrders(this, listOrderTracker);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        isPaused = false;
        displayDateAndTime.post(dateAndTimeRun);
        getData();
        checkOverdue();
        switch (currentSection) {
            case 0:
                growSection(findViewById(R.id.toDoSection));
//                header.setBackgroundResource(R.drawable.gradient_blue);
                header.setBackgroundColor(Color.parseColor("#3385ff"));
                actionButton.setImageResource(R.drawable.add_icon);
                actionButton.setScaleType(ImageView.ScaleType.CENTER);
                actionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.add_button_border));
                itemAdapter = new ItemAdapter(this, R.layout.item_template, listItems);
                switchAdapter(itemAdapter);
                break;
            case 1:
                growSection(findViewById(R.id.completedSection));
//                header.setBackgroundResource(R.drawable.gradient_green);
                header.setBackgroundColor(Color.parseColor("#00cc66"));
                actionButton.setImageResource(R.drawable.delete_icon);
                actionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.delete_button_border));
                completedItemsAdapter = new CompletedItemsAdapter(this, R.layout.completed_item_template, completedItems);
                switchAdapter(completedItemsAdapter);
                break;
            case 2:
                growSection(findViewById(R.id.overdueSection));
//                header.setBackgroundResource(R.drawable.gradient_red);
                header.setBackgroundColor(Color.parseColor("#fc0054"));
                actionButton.setImageResource(R.drawable.delete_icon);
                actionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.delete_button_border));
                overdueItemsAdapter = new OverdueItemsAdapter(this, R.layout.item_template, overdueItems);
                switchAdapter(overdueItemsAdapter);
                break;
        }
    }

    public void getData() {
        //keep track of the current day
//        long currentTotalDate = System.currentTimeMillis();
//        SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd yyyy");
//        String dateString = sdfDate.format(currentTotalDate);
//        DataManager.checkDate(this, (new String[]{"ListItems.json", "CompletedItems.json", "OverdueItems.json"}), dateString);
        notificationID = DataManager.readNotificationID(this, "NotificationID.json");
        //reset all lists
        listItems = DataManager.readItems(this, "ListItems.json");
        overdueItems = DataManager.readItems(this, "OverdueItems.json");
        completedItems = DataManager.readItems(this, "CompletedItems.json");
        //get the orders of the lists
        listOrderTracker = DataManager.readListOrders(this, "ListOrders.json");
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

    public void actionButtonPressed(View v) {
        switch (currentSection) {
            case 0:
                createItem();
                break;
            case 1:
                //todo make a popup saying are you sure you want to delete all items?
                if (!completedItems.isEmpty()) {
                    deleteCompletedPopup();
                }
                break;
            case 2:
                //todo make a popup saying are you sure you want to delete all items?
                if (!overdueItems.isEmpty()) {
                    deleteOverduePopup();
                }
                break;
        }
    }

    public void deleteCompletedPopup() {
        deleteCompletedDialog = new Dialog(this);
        deleteCompletedDialog.setContentView(R.layout.delete_items_popup);
        TextView message = deleteCompletedDialog.findViewById(R.id.message);
        message.setText("Are you sure you want to delete ALL completed items?");
        TextView deleteButton = deleteCompletedDialog.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completedItems.clear();
                switchAdapter(completedItemsAdapter);
                saveItems("CompletedItems.json", completedItems);
                deleteCompletedDialog.dismiss();
            }
        });
        TextView cancelButton = deleteCompletedDialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCompletedDialog.dismiss();
            }
        });
        deleteCompletedDialog.show();
    }

    public void deleteOverduePopup() {
        deleteOverdueDialog = new Dialog(this);
        deleteOverdueDialog.setContentView(R.layout.delete_items_popup);
        TextView message = deleteOverdueDialog.findViewById(R.id.message);
        message.setText("Are you sure you want to delete ALL overdue items?");
        TextView deleteButton = deleteOverdueDialog.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overdueItems.clear();
                switchAdapter(overdueItemsAdapter);
                saveItems("OverdueItems.json", overdueItems);
                deleteOverdueDialog.dismiss();
            }
        });
        TextView cancelButton = deleteOverdueDialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOverdueDialog.dismiss();
            }
        });
        deleteOverdueDialog.show();
    }

    public void createItem() {
        Intent createIntent = new Intent(this, create_item.class);
        createIntent.putExtra("type", "create");
        createIntent.putExtra("notificationID", notificationID);
        startActivityForResult(createIntent, 100);
    }

    public void editItem(int position) {
        Item currentItem = listItems.get(position);
        Intent editIntent = new Intent(this, create_item.class);
        editIntent.putExtra("type", "edit");
        editIntent.putExtra("name", currentItem.getName());
        editIntent.putExtra("category", currentItem.getCategory());
        editIntent.putExtra("timeStamp", currentItem.getTimeStamp());
        editIntent.putExtra("notificationID", notificationID);
        editIntent.putExtra("repeat", currentItem.getRepeat());
        editIntent.putExtra("position", position);
        editIntent.putExtra("canSetDateTime", currentItem.isCanSetDateTime());
        startActivityForResult(editIntent, 200);
    }

    public void deleteItem(int position) {
        cancelNotification(listItems.get(position).getNotificationID());
        listItems.remove(position); //MAY CAUSE ERROR IF DEFAULT VALUE IS USED
        DataManager.saveItems(this, "ListItems.json", listItems);
        resetAdapter();
    }

    public void insertItem(ArrayList<Item> addTo, Item toAdd, String section) {
        if (addTo.isEmpty()) {
            addTo.add(toAdd);
        } else {
            boolean added = false;
            boolean ascending;
            Calendar toAddTime = Calendar.getInstance();
            toAddTime.setTimeInMillis(toAdd.getTimeStamp());

            for (int a = 0; a < addTo.size(); a++) {
                Item currentItem = addTo.get(a);
                Calendar currentItemTime = Calendar.getInstance();
                currentItemTime.setTimeInMillis(currentItem.getTimeStamp());
                ascending = false;
                switch (section) {
                    case "todo":
                        if (listOrderTracker.todoAscending) {
                            ascending = true;
                        }
                        break;
                    case "completed":
                        if (listOrderTracker.completedAscending) {
                            ascending = true;
                        }
                        break;
                    case "overdue":
                        if (listOrderTracker.overdueAscending) {
                            ascending = true;
                        }
                        break;
                }
                if (ascending) {
                    if (toAddTime.compareTo(currentItemTime) < 0) {
                        addTo.add(a, toAdd);
                        added = true;
                        break; //since the end condition is a<addTo.size(), this will run infinitely without this break statement because we added an item to the list, so size increased by 1 and will keep doing so as we add the same element again and again
                    }
                } else {
                    if (toAddTime.compareTo(currentItemTime) > 0) {
                        addTo.add(a, toAdd);
                        added = true;
                        break; //since the end condition is a<addTo.size(), this will run infinitely without this break statement because we added an item to the list, so size increased by 1 and will keep doing so as we add the same element again and again
                    }
                }
//                //descending order in terms of current millis
//                if (section.equals("completed") || section.equals("overdue")) {
//                    addTo.add(a, toAdd);
//                    added = true;
//                    break; //since the end condition is a<addTo.size(), this will run infinitely without this break statement because we added an item to the list, so size increased by 1 and will keep doing so as we add the same element again and again
//                } else { //ascending order in terms of current millis
//                    if (toAddTime.compareTo(currentItemTime) < 0) {
//                        addTo.add(a, toAdd);
//                        added = true;
//                        break; //since the end condition is a<addTo.size(), this will run infinitely without this break statement because we added an item to the list, so size increased by 1 and will keep doing so as we add the same element again and again
//                    }
//                }
            }
            if (!added) {
                addTo.add(toAdd);
//                if (ascending) {
//                    addTo.add(toAdd);
//                } else {
//                    addTo.add(0, toAdd);
//                }
            }
        }
        if (section.equals("todo")) {
            DataManager.saveItems(this, "ListItems.json", listItems);
            itemAdapter.notifyDataSetChanged();

            //if we are inserting item into the to do section, make a notification
            //todo (I did a bit already but)change this to make notifications set at a certain date instead of just a certain time in the current day
            Calendar taskDueTime = Calendar.getInstance();
            taskDueTime.setTimeInMillis(toAdd.getTimeStamp());
            setNotification(taskDueTime, toAdd.getName());
            notificationID++;
            DataManager.saveNotificationID(this, notificationID);
        }
    }

    public void reverseList(int section) {
        switch (section) {
            case 0:
                Collections.reverse(listItems);
                resetAdapter();
                break;
            case 1:
                Collections.reverse(completedItems);
                switchAdapter(completedItemsAdapter);
                break;
            case 2:
                Collections.reverse(overdueItems);
                switchAdapter(overdueItemsAdapter);
                break;
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
        if ((currentSection != 0) && (OverdueItemsAdapter.instances == 0)) {
            shrinkCurrent(currentSection);
//            header.setBackgroundResource(R.drawable.gradient_blue);
            header.setBackgroundColor(Color.parseColor("#3385ff"));
            actionButton.setImageResource(R.drawable.add_icon);
            actionButton.setScaleType(ImageView.ScaleType.CENTER);
            actionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.add_button_border));
            currentSection = 0;
            growSection(v);
            listItems = DataManager.readItems(this, "ListItems.json");
            checkOverdue();
            itemAdapter = new ItemAdapter(this, R.layout.item_template, listItems);
            switchAdapter(itemAdapter);
        } else if (currentSection == 0) {
            reverseList(currentSection);
            switchAdapter(itemAdapter);
            listOrderTracker.todoAscending = !(listOrderTracker.todoAscending);
            saveItems("ListItems.json", listItems);
            DataManager.saveListOrders(this, listOrderTracker);
        }
    }

    public void switchCompleted(View v) {
        if ((currentSection != 1) && (ItemAdapter.instances == 0) && (OverdueItemsAdapter.instances == 0)) {
            shrinkCurrent(currentSection);
//            header.setBackgroundResource(R.drawable.gradient_green);
            header.setBackgroundColor(Color.parseColor("#00cc66"));
            actionButton.setImageResource(R.drawable.delete_icon);
//            actionButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            actionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.delete_button_border));
            currentSection = 1;
            growSection(v);
            completedItems = DataManager.readItems(this, "CompletedItems.json");
            completedItemsAdapter = new CompletedItemsAdapter(this, R.layout.completed_item_template, completedItems);
            switchAdapter(completedItemsAdapter);
        } else if (currentSection == 1) {
            reverseList(currentSection);
            switchAdapter(completedItemsAdapter);
            listOrderTracker.completedAscending = !(listOrderTracker.completedAscending);
            saveItems("CompletedItems.json", completedItems);
            DataManager.saveListOrders(this, listOrderTracker);
        }
    }

    public void switchOverdue(View v) {
        if ((currentSection != 2) && (ItemAdapter.instances == 0)) {
            shrinkCurrent(currentSection);
//            header.setBackgroundResource(R.drawable.gradient_red);
            header.setBackgroundColor(Color.parseColor("#fc0054"));
            actionButton.setImageResource(R.drawable.delete_icon);
//            actionButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            actionButton.setBackground(ContextCompat.getDrawable(this, R.drawable.delete_button_border));
            currentSection = 2;
            growSection(v);
            overdueItems = DataManager.readItems(this, "OverdueItems.json");
            checkOverdue();
            overdueItemsAdapter = new OverdueItemsAdapter(this, R.layout.item_template, overdueItems);
            switchAdapter(overdueItemsAdapter);
        } else if (currentSection == 2) {
            reverseList(currentSection);
            switchAdapter(overdueItemsAdapter);
            listOrderTracker.overdueAscending = !(listOrderTracker.overdueAscending);
            saveItems("OverdueItems.json", overdueItems);
            DataManager.saveListOrders(this, listOrderTracker);
        }
    }

    public void checkOverdue() {
        //get current time
        ArrayList<Item> toPutInOverdue = new ArrayList<>();
        for (int a = 0; a < listItems.size(); a++) {
            Item currentItem = listItems.get(a);
            Calendar currentItemDueTime = Calendar.getInstance();
            currentItemDueTime.setTimeInMillis(currentItem.getTimeStamp()); //add a minute bc it's only overdue a minute after that time
            currentItemDueTime.add(Calendar.MINUTE, 1);
            if (currentItemDueTime.compareTo(Calendar.getInstance()) < 0) {
                insertItem(overdueItems, currentItem, "overdue");
                toPutInOverdue.add(currentItem);
            }
        }
        listItems.removeAll(toPutInOverdue);
        DataManager.saveItems(this, "OverdueItems.json", overdueItems);
        DataManager.saveItems(this, "ListItems.json", listItems);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        itemAdapter = new ItemAdapter(this, R.layout.item_template, listItems);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                Item toAdd = new Item(data.getStringExtra("name"), data.getStringExtra("category"), data.getLongExtra("timeStamp", -1), data.getIntExtra("notificationID", -1), data.getIntExtra("repeat", -1), data.getBooleanExtra("canSetDateTime", false));
                insertItem(listItems, toAdd, "todo");
            }
            if (requestCode == 200) {
                deleteItem(data.getIntExtra("position", -1));
                Item toAdd = new Item(data.getStringExtra("name"), data.getStringExtra("category"), data.getLongExtra("timeStamp", -1), data.getIntExtra("notificationID", -1), data.getIntExtra("repeat", -1), data.getBooleanExtra("canSetDateTime", false));
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
        DataManager.saveListOrders(this, listOrderTracker);
    }
}
