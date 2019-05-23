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

public class create_item extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    boolean isStartTime = false; //keep track of which timepicker we are on
    int startHour = 0;
    int startMinute = 0;
    int endHour = 0;
    int endMinute = 0;
    String category = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);
    }

    public void startButtonOnClick(View v) {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "start time picker");
        isStartTime = true;
    }

    public void endButtonOnClick(View v) {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "end time picker");
        isStartTime = false;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView textTime;
        if (isStartTime) {
            textTime = (TextView) findViewById(R.id.startTime);
            startHour = hourOfDay;
            startMinute = minute;
        } else {
            textTime = (TextView) findViewById(R.id.endTime);
            endHour = hourOfDay;
            endMinute = minute;
        }

        if (minute < 10) {
            textTime.setText(hourOfDay + ":0" + minute);
        } else {
            textTime.setText(hourOfDay + ":" + minute);
        }
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
        } else{
            goodCategory = true;
        }
        if ((startHour > endHour)
                || ((startHour == endHour) && (startMinute > endMinute))
                || ((startHour == endHour) && (startMinute == endMinute))) {
            Toast correctTime = Toast.makeText(getApplicationContext(), "End time must be after start time", Toast.LENGTH_LONG);
            correctTime.show();
        } else{
            goodTime = true;
        }
        if (goodName && goodCategory && goodTime) {
            Intent i = new Intent();
            i.putExtra("name", nameField.getText().toString());
            i.putExtra("category", category);
            i.putExtra("startHour", startHour);
            i.putExtra("startMinute", startMinute);
            i.putExtra("endHour", endHour);
            i.putExtra("endMinute", endMinute);
            setResult(RESULT_OK, i);
            finish();
        }
    }
}
