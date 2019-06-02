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
    int dueHour = -1;
    int dueMinute = -1;
    String category = "";

    //these are for remembering what data the user has already inputted when they rotate screen and we need to restore data
    boolean collectCategory = false;
    boolean collectDueTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);
        if(savedInstanceState != null){
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
        //create boolean statements to check if user input was good
        boolean goodName = false;
        boolean goodCategory = false;
        boolean goodTime = false;
        EditText nameField = (EditText) findViewById(R.id.nameField);
        if (nameField.getText().toString().equals("")) {
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
        if (goodName && goodCategory && goodTime) {
            Intent i = new Intent();
            i.putExtra("name", nameField.getText().toString());
            i.putExtra("category", category);
            i.putExtra("dueHour", dueHour);
            i.putExtra("dueMinute", dueMinute);
            setResult(RESULT_OK, i);
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
