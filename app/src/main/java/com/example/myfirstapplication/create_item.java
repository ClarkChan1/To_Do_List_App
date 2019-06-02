package com.example.myfirstapplication;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class create_item extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    String name="";
    int dueHour = -1;
    int dueMinute = -1;
    String category = "";
    //This is to track which item in the Listview is to be edited
    int editPosition = -1;

    //create boolean statements to check if user input was good
    boolean goodName = false;
    boolean goodCategory = false;
    boolean goodTime = false;

    //these are for remembering what data the user has already inputted when they rotate screen and we need to restore data
    boolean collectCategory = false;
    boolean collectDueTime = false;

    String activityType = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            if(savedInstanceState.getString("type").equals("create")){
                setContentView(R.layout.activity_create_item);
            } else {
                setContentView(R.layout.activity_edit_item);
            }
            if(savedInstanceState.getBoolean("collectCategory")){
                category = savedInstanceState.getString("category");
                collectCategory = false;
            }
            if(savedInstanceState.getBoolean("collectDueTime")){
                dueHour = savedInstanceState.getInt("dueHour");
                dueMinute = savedInstanceState.getInt("dueMinute");
                setTimeString();
                collectDueTime = false;
            }
        }
        //This is for the start of activity when we are either editing or creating an item
        Intent data = getIntent();
        if(data.getStringExtra("type").equals("create")){
            setContentView(R.layout.activity_create_item);
            activityType = "create";
        } else {
            setContentView(R.layout.activity_edit_item);
            activityType = "edit";
            editPosition = data.getIntExtra("position", -1);
        }

    }

    public void startButtonOnClick(View v) {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "start time picker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        dueHour = hourOfDay;
        dueMinute = minute;
        setTimeString();
    }

    public void setTimeString(){
        TextView textTime;
        textTime = findViewById(R.id.dueTime);
        String timeString = "";
        if (dueHour % 12 == 0) {
            timeString = 12 + ":";
        } else {
            timeString = (dueHour % 12) + ":";
        }
        if (dueMinute < 10) {
            timeString += "0";
        }
        timeString += dueMinute;
        if (dueHour >= 12) {
            timeString += " PM";
        } else {
            timeString += " AM";
        }
        textTime.setText(timeString);
    }
    public void onRadioButtonClicked(View v) {
        if (v.getId() == R.id.radio1) {
            category = "Work";
        } else {
            category = "Life";
        }
    }

    public void onCreateButtonClicked(View v) {
        checkData(v);
        if (goodName && goodCategory && goodTime) {
            Intent i = new Intent();
            i.putExtra("name", name);
            i.putExtra("category", category);
            i.putExtra("dueHour", dueHour);
            i.putExtra("dueMinute", dueMinute);
            setResult(RESULT_OK, i);
            finish();
        }
    }

    public void onEditButtonClicked(View v){
        checkData(v);
        if (goodName && goodCategory && goodTime) {
            Intent i = new Intent();
            i.putExtra("name", name);
            i.putExtra("category", category);
            i.putExtra("dueHour", dueHour);
            i.putExtra("dueMinute", dueMinute);
            i.putExtra("action", "edit");
            i.putExtra("position", editPosition);
            setResult(RESULT_OK, i);
            finish();
        }
    }

    public void onDeleteButtonClicked(View v){
        Intent i = new Intent();
        i.putExtra("action", "delete");
        i.putExtra("position", editPosition);
        setResult(RESULT_OK, i);
        finish();
    }

    public void checkData(View v){
        EditText nameField = (EditText) findViewById(R.id.nameField);
        name = nameField.getText().toString();
        if (name.equals("")) {
            Toast correctName = Toast.makeText(getApplicationContext(), "Fill in the name field", Toast.LENGTH_LONG);
            correctName.show();
        } else {
            goodName = true;
        }
        if (category.equals("")) {
            Toast correctCategory = Toast.makeText(getApplicationContext(), "Choose a category", Toast.LENGTH_LONG);
            correctCategory.show();
        } else {
            goodCategory = true;
        }
        Calendar c = Calendar.getInstance();
        int currentHour = c.get(Calendar.HOUR_OF_DAY);
        int currentMinute = c.get(Calendar.MINUTE);
        if ((currentHour > dueHour) || ((currentHour == dueHour) && (currentMinute >= dueMinute))) {
            Toast correctTime = Toast.makeText(getApplicationContext(), "Task must be due sometime after this moment", Toast.LENGTH_LONG);
            correctTime.show();
        } else {
            goodTime = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("type", activityType);
        if(!category.equals("")){
            outState.putString("category", category);
            collectCategory = true;
            outState.putBoolean("collectCategory", true);
        } else{
            outState.putBoolean("collectCategory", false);
        }
        if(dueHour != -1){
            outState.putInt("dueHour", dueHour);
            outState.putInt("dueMinute", dueMinute);
            collectDueTime = true;
            outState.putBoolean("collectDueTime", true);
        } else{
            outState.putBoolean("collectDueTime", false);
        }
    }
}
