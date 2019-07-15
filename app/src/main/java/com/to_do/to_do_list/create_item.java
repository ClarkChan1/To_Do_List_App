package com.to_do.to_do_list;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.util.Calendar;

public class create_item extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {
    String name = "";
    int dueYear = -1;
    int dueMonth = -1;
    int dueDay = -1;
    int dueHour = -1;
    int dueMinute = -1;
    String category = "";
    int notificationID = 0;
    int repeat = 0;
    //This is to track which item in the Listview is to be edited
    int editPosition = -1;

    //create boolean statements to check if user input was good
    boolean goodName = false;
    boolean goodCategory = false;
    boolean goodDateAndTime = false;
    //keep track of whether or not I'm creating or editing an item with this String.
    String activityType = "";
    //since I'm using one xml file for both creating and editing items, this button reference is needed to change the bottom button text to "create" or "edit"
    Button actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_or_edit_item);
        actionButton = findViewById(R.id.actionButton);
        //create the dropdown menu
        Spinner spinner = findViewById(R.id.repeating);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.repeating, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        if (savedInstanceState != null) {
            activityType = savedInstanceState.getString("type");
            if (activityType.equals("create")) {
                actionButton.setText("create");
            } else {
                actionButton.setText("edit");
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
            notificationID = savedInstanceState.getInt("notificationID");
            repeat = savedInstanceState.getInt("repeat");
            spinner.setSelection(repeat);
            editPosition = savedInstanceState.getInt("position");
        } else {
            //This is for the start of activity when we are either editing or creating an item
            Intent data = getIntent();
            if (data.getStringExtra("type").equals("create")) {
                actionButton.setText("create");
                EditText nameField = (EditText) findViewById(R.id.nameField);
                nameField.requestFocus();
                //get keyboard to appear upon entering create item
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                activityType = "create";
            } else if (data.getStringExtra("type").equals("edit")) {
                actionButton.setText("edit");
                activityType = "edit";
                name = data.getStringExtra("name");
                category = data.getStringExtra("category");
                Calendar setDateAndTime = Calendar.getInstance();
                setDateAndTime.setTimeInMillis(data.getLongExtra("timeStamp", -1));
                dueYear = setDateAndTime.get(Calendar.YEAR);
                dueMonth = setDateAndTime.get(Calendar.MONTH);
                dueDay = setDateAndTime.get(Calendar.DAY_OF_MONTH);
                dueHour = setDateAndTime.get(Calendar.HOUR_OF_DAY);
                dueMinute = setDateAndTime.get(Calendar.MINUTE);
                editPosition = data.getIntExtra("position", -1);
                repeat = data.getIntExtra("repeat", -1);
                spinner.setSelection(repeat);
                populateData();
            }
            notificationID = data.getIntExtra("notificationID", -1);
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        repeat = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        repeat = 0;
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

    public void setDateString() {
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

    public void actionButtonClicked(View v) {
        if (activityType.equals("create")) {
            onCreateButtonClicked();
        } else {
            onEditButtonClicked();
        }
    }

    public void onCreateButtonClicked() {
        checkData();
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
            i.putExtra("repeat", repeat);
            setResult(RESULT_OK, i);
            finish();
        }
    }

    public void onEditButtonClicked() {
        checkData();
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
            i.putExtra("repeat", repeat);
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

    public void checkData() {
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
        if ((timePicked.compareTo(Calendar.getInstance()) < 0) || (dueYear == -1) || (dueHour == -1)) {
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
        outState.putInt("notificationID", notificationID);
        outState.putInt("repeat", repeat);
        outState.putInt("position", editPosition);
    }
}