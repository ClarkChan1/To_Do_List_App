package com.example.myfirstapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class create_item extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    String name = "";
    int dueYear = -1;
    int dueMonth = -1;
    int dueDay = -1;
    int dueHour = -1;
    int dueMinute = -1;
    String category = "";
    int notificationID = 0;
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
        if (savedInstanceState != null) {
            activityType = savedInstanceState.getString("type");
            if (activityType.equals("create")) {
                setContentView(R.layout.activity_create_item);
            } else {
                setContentView(R.layout.activity_edit_item);
            }
            if (savedInstanceState.getBoolean("collectCategory")) {
                category = savedInstanceState.getString("category");
                collectCategory = false;
            }
            if (savedInstanceState.getBoolean("collectDueTime")) {
                dueHour = savedInstanceState.getInt("dueHour");
                dueMinute = savedInstanceState.getInt("dueMinute");
                setTimeString();
                collectDueTime = false;
            }
            editPosition = savedInstanceState.getInt("position");
            notificationID = savedInstanceState.getInt("notificationID");
        } else {
            //This is for the start of activity when we are either editing or creating an item
            Intent data = getIntent();
            if (data.getStringExtra("type").equals("create")) {
                setContentView(R.layout.activity_create_item);
                EditText nameField = (EditText)findViewById(R.id.nameField);
                nameField.requestFocus();
                //get keyboard to appear upon entering create item
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                activityType = "create";
            } else if (data.getStringExtra("type").equals("edit")) {
                setContentView(R.layout.activity_edit_item);
                activityType = "edit";
                name = data.getStringExtra("name");
                category = data.getStringExtra("category");
                dueHour = data.getIntExtra("dueHour", -1);
                dueMinute = data.getIntExtra("dueMinute", -1);
                editPosition = data.getIntExtra("position", -1);
                populateData();
            }
            notificationID = data.getIntExtra("notificationID", -1);
        }

    }

    public void selectTimeButtonClicked(View v) {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "start time picker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        dueHour = hourOfDay;
        dueMinute = minute;
        setTimeString();
    }

    public void selectDateButtonClicked(View v) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "start time picker");
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    public void setTimeString() {
        TextView textTime;
        textTime = findViewById(R.id.selectDueTime);
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
            i.putExtra("notificationID", notificationID);
            setResult(RESULT_OK, i);
            finish();
        }
    }

    public void onEditButtonClicked(View v) {
        checkData(v);
        if (goodName && goodCategory && goodTime) {
            Intent i = new Intent();
            i.putExtra("name", name);
            i.putExtra("category", category);
            i.putExtra("dueHour", dueHour);
            i.putExtra("dueMinute", dueMinute);
            i.putExtra("action", "edit");
            i.putExtra("position", editPosition);
            i.putExtra("notificationID", notificationID);
            setResult(RESULT_OK, i);
            finish();
        }
    }

    public void onDeleteButtonClicked(View v) {
        Intent i = new Intent();
        i.putExtra("action", "delete");
        i.putExtra("position", editPosition);
        setResult(RESULT_OK, i);
        finish();
    }

    public void populateData() {
        EditText nameField = (EditText) findViewById(R.id.nameField);
        nameField.setText(name);
        setTimeString();
        if (category.equals("Work")) {
            RadioButton workButton = findViewById(R.id.radio1);
            workButton.setChecked(true);
        } else {
            RadioButton lifeButton = findViewById(R.id.radio2);
            lifeButton.setChecked(true);
        }
    }

    public void checkData(View v) {
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
        if (!category.equals("")) {
            outState.putString("category", category);
            collectCategory = true;
            outState.putBoolean("collectCategory", true);
        } else {
            outState.putBoolean("collectCategory", false);
        }
        if (dueHour != -1) {
            outState.putInt("dueHour", dueHour);
            outState.putInt("dueMinute", dueMinute);
            collectDueTime = true;
            outState.putBoolean("collectDueTime", true);
        } else {
            outState.putBoolean("collectDueTime", false);
        }
        outState.putInt("position", editPosition);
        outState.putInt("notificationID", notificationID);
    }
}
