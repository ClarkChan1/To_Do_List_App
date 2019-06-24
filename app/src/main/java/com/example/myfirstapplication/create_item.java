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

import java.text.DateFormat;
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
    boolean goodDateAndTime = false;


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
            }
            if (savedInstanceState.getBoolean("collectDate")) {
                dueYear = savedInstanceState.getInt("dueYear");
                dueMonth = savedInstanceState.getInt("dueMonth");
                dueDay = savedInstanceState.getInt("dueDay");
                setDateString();
            }
            if (savedInstanceState.getBoolean("collectDueTime")) {
                dueHour = savedInstanceState.getInt("dueHour");
                dueMinute = savedInstanceState.getInt("dueMinute");
                setTimeString();
            }
            editPosition = savedInstanceState.getInt("position");
            notificationID = savedInstanceState.getInt("notificationID");
        } else {
            //This is for the start of activity when we are either editing or creating an item
            Intent data = getIntent();
            if (data.getStringExtra("type").equals("create")) {
                setContentView(R.layout.activity_create_item);
                EditText nameField = (EditText) findViewById(R.id.nameField);
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
                Calendar setDateAndTime = Calendar.getInstance();
                setDateAndTime.setTimeInMillis(data.getLongExtra("timeStamp", -1));
                dueYear = setDateAndTime.get(Calendar.YEAR);
                dueMonth =setDateAndTime.get(Calendar.MONTH);
                dueDay = setDateAndTime.get(Calendar.DAY_OF_MONTH);
                dueHour = setDateAndTime.get(Calendar.HOUR_OF_DAY);
                dueMinute = setDateAndTime.get(Calendar.MINUTE);
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
        datePicker.show(getSupportFragmentManager(), "start date picker");
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dueYear = year;
        dueMonth = month;
        dueDay = dayOfMonth;
        setDateString();
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

    public void setDateString(){
        TextView textDate;
        textDate = findViewById(R.id.selectDueDate);
        Calendar datePicked = Calendar.getInstance();
        datePicked.set(Calendar.YEAR, dueYear);
        datePicked.set(Calendar.MONTH, dueMonth);
        datePicked.set(Calendar.DAY_OF_MONTH, dueDay);
        String dateString = DateFormat.getDateInstance().format(datePicked.getTime());
        textDate.setText(dateString);
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
        if (goodName && goodCategory && goodDateAndTime) {
            Intent i = new Intent();
            i.putExtra("name", name);
            i.putExtra("category", category);
            Calendar dateAndTime = Calendar.getInstance();
            dateAndTime.set(Calendar.YEAR, dueYear);
            dateAndTime.set(Calendar.MONTH, dueMonth);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dueDay);
            dateAndTime.set(Calendar.HOUR_OF_DAY, dueHour);
            dateAndTime.set(Calendar.MINUTE, dueMinute);
            dateAndTime.set(Calendar.SECOND, 0);
            i.putExtra("timeStamp", dateAndTime.getTimeInMillis());
            i.putExtra("notificationID", notificationID);
            setResult(RESULT_OK, i);
            finish();
        }
    }

    public void onEditButtonClicked(View v) {
        checkData(v);
        if (goodName && goodCategory && goodDateAndTime) {
            Intent i = new Intent();
            i.putExtra("name", name);
            i.putExtra("category", category);
            Calendar dateAndTime = Calendar.getInstance();
            dateAndTime.set(Calendar.YEAR, dueYear);
            dateAndTime.set(Calendar.MONTH, dueMonth);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dueDay);
            dateAndTime.set(Calendar.HOUR_OF_DAY, dueHour);
            dateAndTime.set(Calendar.MINUTE, dueMinute);
            dateAndTime.set(Calendar.SECOND, 0);
            i.putExtra("timeStamp", dateAndTime.getTimeInMillis());
            i.putExtra("position", editPosition);
            i.putExtra("notificationID", notificationID);
            setResult(RESULT_OK, i);
            finish();
        }
    }


    public void populateData() {
        EditText nameField = (EditText) findViewById(R.id.nameField);
        nameField.setText(name);
        setTimeString();
        setDateString();
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
        Calendar timePicked = Calendar.getInstance();
        timePicked.set(Calendar.YEAR, dueYear);
        timePicked.set(Calendar.MONTH, dueMonth);
        timePicked.set(Calendar.DAY_OF_MONTH, dueDay);
        timePicked.set(Calendar.HOUR_OF_DAY, dueHour);
        timePicked.set(Calendar.MINUTE, dueMinute);
        if (timePicked.compareTo(Calendar.getInstance()) < 0) {
            Toast correctTime = Toast.makeText(getApplicationContext(), "Task must be due sometime after this moment", Toast.LENGTH_LONG);
            correctTime.show();
        } else {
            goodDateAndTime = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("type", activityType);
        if (!category.equals("")) {
            outState.putString("category", category);
            outState.putBoolean("collectCategory", true);
        } else {
            outState.putBoolean("collectCategory", false);
        }
        if (dueYear != -1) {
            outState.putInt("dueYear", dueYear);
            outState.putInt("dueMonth", dueMonth);
            outState.putInt("dueDay", dueDay);
            outState.putBoolean("collectDate", true);
        } else {
            outState.putBoolean("collectDate", false);
        }
        if (dueHour != -1) {
            outState.putInt("dueHour", dueHour);
            outState.putInt("dueMinute", dueMinute);
            outState.putBoolean("collectDueTime", true);
        } else {
            outState.putBoolean("collectDueTime", false);
        }
        outState.putInt("position", editPosition);
        outState.putInt("notificationID", notificationID);
    }
}
