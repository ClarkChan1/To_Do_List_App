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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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

    //check if user has set the date and time
    boolean dateSet = false;
    boolean timeSet = false;
    //controls if user can set a specific date and time
    boolean canSetDateTime = true;

    //create boolean statements to check if user input was good
    boolean goodName = false;
    boolean goodCategory = false;
    boolean goodDateAndTime = false;
    //keep track of whether or not I'm creating or editing an item with this String.
    String activityType = "";
    //since I'm using one xml file for both creating and editing items, this button reference is needed to change the bottom button text to "create" or "edit"
    Button actionButton;

    //linear layouts for the date and time
    LinearLayout dateLinear;
    LinearLayout timeLinear;
    //checkbox for setting sepcific date and time
    CheckBox dateTimeCheckbox;

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
        //checkbox for setting sepcific date and time
        dateTimeCheckbox = (CheckBox)findViewById(R.id.specificDateAndTime);
        //linear layouts for the date and time
        dateLinear = (LinearLayout) findViewById(R.id.dateLinearLayout);
        timeLinear = (LinearLayout) findViewById(R.id.timeLinearLayout);

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
                if (dueYear < 9000) {
                    setDateString();
                }
            }
            if (savedInstanceState.getBoolean("collectDueTime")) {
                dueHour = savedInstanceState.getInt("dueHour");
                dueMinute = savedInstanceState.getInt("dueMinute");
                if (dueYear < 9000) {
                    setTimeString();
                }
            }
            dateSet = savedInstanceState.getBoolean("dateSet");
            timeSet = savedInstanceState.getBoolean("timeSet");
            notificationID = savedInstanceState.getInt("notificationID");
            repeat = savedInstanceState.getInt("repeat");
            canSetDateTime = savedInstanceState.getBoolean("canSetDateTime");
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
                if (dueYear < 9000) {
                    dateSet = true;
                    timeSet = true;
                }
                editPosition = data.getIntExtra("position", -1);
                repeat = data.getIntExtra("repeat", -1);
                canSetDateTime = data.getBooleanExtra("canSetDateTime", false);
                spinner.setSelection(repeat);
                populateData();
            }
            notificationID = data.getIntExtra("notificationID", -1);
        }
        //see if the checkbox for setting date and time is checked
        if(canSetDateTime){
            dateTimeCheckbox.setChecked(true);
            dateLinear.setVisibility(View.VISIBLE);
            timeLinear.setVisibility(View.VISIBLE);
        } else {
            dateTimeCheckbox.setChecked(false);
            dateLinear.setVisibility(View.INVISIBLE);
            timeLinear.setVisibility(View.INVISIBLE);
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
        if (canSetDateTime) {
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "start time picker");
        }
    }

    public void setDateTimeCheckbox(View v) {
        if (dateTimeCheckbox.isChecked()) {
            canSetDateTime = true;
            dateLinear.setVisibility(View.VISIBLE);
            timeLinear.setVisibility(View.VISIBLE);
        } else {
            canSetDateTime = false;
            dateLinear.setVisibility(View.INVISIBLE);
            timeLinear.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar currentTime = Calendar.getInstance();
        dueHour = hourOfDay;
        dueMinute = minute;
        timeSet = true;
        setTimeString();
        if (!dateSet) {
            dueYear = currentTime.get(Calendar.YEAR);
            dueMonth = currentTime.get(Calendar.MONTH);
            dueDay = currentTime.get(Calendar.DAY_OF_MONTH);
            setDateString();
            dateSet = true;
        }
    }

    public void selectDateButtonClicked(View v) {
        if (canSetDateTime) {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "start date picker");
        }
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dueYear = year;
        dueMonth = month;
        dueDay = dayOfMonth;
        dateSet = true;
        setDateString();
        if (!timeSet) {
            dueHour = 23;
            dueMinute = 59;
            setTimeString();
            timeSet = true;
        }
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
            Calendar currentTime = Calendar.getInstance();
            Calendar dateAndTime = Calendar.getInstance();
            if (canSetDateTime) {
                if (!dateSet) {
                    dateAndTime.set(Calendar.YEAR, currentTime.get(Calendar.YEAR));
                    dateAndTime.set(Calendar.MONTH, currentTime.get(Calendar.MONTH));
                    dateAndTime.set(Calendar.DAY_OF_MONTH, currentTime.get(Calendar.DAY_OF_MONTH));
                    dateAndTime.set(Calendar.HOUR_OF_DAY, dueHour);
                    dateAndTime.set(Calendar.MINUTE, dueMinute);
                    dateAndTime.set(Calendar.SECOND, 0);
                } else {
                    if (!timeSet) {
                        dateAndTime.set(Calendar.YEAR, dueYear);
                        dateAndTime.set(Calendar.MONTH, dueMonth);
                        dateAndTime.set(Calendar.DAY_OF_MONTH, dueDay);
                        dateAndTime.set(Calendar.HOUR_OF_DAY, 23);
                        dateAndTime.set(Calendar.MINUTE, 59);
                        dateAndTime.set(Calendar.SECOND, 59);
                    } else {
                        dateAndTime.set(Calendar.YEAR, dueYear);
                        dateAndTime.set(Calendar.MONTH, dueMonth);
                        dateAndTime.set(Calendar.DAY_OF_MONTH, dueDay);
                        dateAndTime.set(Calendar.HOUR_OF_DAY, dueHour);
                        dateAndTime.set(Calendar.MINUTE, dueMinute);
                        dateAndTime.set(Calendar.SECOND, 0);
                    }
                }
            } else {
                dateAndTime.set(Calendar.YEAR, 9001);
                dateAndTime.set(Calendar.MONTH, 0);
                dateAndTime.set(Calendar.DAY_OF_MONTH, 1);
                dateAndTime.set(Calendar.HOUR_OF_DAY, 0);
                dateAndTime.set(Calendar.MINUTE, 0);
                dateAndTime.set(Calendar.SECOND, 0);
            }
//            if (!dateSet) {
//                if (!timeSet) {
//                    dateAndTime.set(Calendar.YEAR, 9001);
//                    dateAndTime.set(Calendar.MONTH, 0);
//                    dateAndTime.set(Calendar.DAY_OF_MONTH, 1);
//                    dateAndTime.set(Calendar.HOUR_OF_DAY, 0);
//                    dateAndTime.set(Calendar.MINUTE, 0);
//                    dateAndTime.set(Calendar.SECOND, 0);
//                } else {
//                    dateAndTime.set(Calendar.YEAR, currentTime.get(Calendar.YEAR));
//                    dateAndTime.set(Calendar.MONTH, currentTime.get(Calendar.MONTH));
//                    dateAndTime.set(Calendar.DAY_OF_MONTH, currentTime.get(Calendar.DAY_OF_MONTH));
//                    dateAndTime.set(Calendar.HOUR_OF_DAY, dueHour);
//                    dateAndTime.set(Calendar.MINUTE, dueMinute);
//                    dateAndTime.set(Calendar.SECOND, 0);
//                }
//            } else {
//                if (!timeSet) {
//                    dateAndTime.set(Calendar.YEAR, dueYear);
//                    dateAndTime.set(Calendar.MONTH, dueMonth);
//                    dateAndTime.set(Calendar.DAY_OF_MONTH, dueDay);
//                    dateAndTime.set(Calendar.HOUR_OF_DAY, 23);
//                    dateAndTime.set(Calendar.MINUTE, 59);
//                    dateAndTime.set(Calendar.SECOND, 59);
//                } else {
//                    if (canSetDateTime) {
//                        dateAndTime.set(Calendar.YEAR, dueYear);
//                        dateAndTime.set(Calendar.MONTH, dueMonth);
//                        dateAndTime.set(Calendar.DAY_OF_MONTH, dueDay);
//                        dateAndTime.set(Calendar.HOUR_OF_DAY, dueHour);
//                        dateAndTime.set(Calendar.MINUTE, dueMinute);
//                        dateAndTime.set(Calendar.SECOND, 0);
//                    } else {
//                        dateAndTime.set(Calendar.YEAR, 9001);
//                        dateAndTime.set(Calendar.MONTH, 0);
//                        dateAndTime.set(Calendar.DAY_OF_MONTH, 1);
//                        dateAndTime.set(Calendar.HOUR_OF_DAY, 0);
//                        dateAndTime.set(Calendar.MINUTE, 0);
//                        dateAndTime.set(Calendar.SECOND, 0);
//                    }
//                }
//            }
            i.putExtra("timeStamp", dateAndTime.getTimeInMillis());
            i.putExtra("notificationID", notificationID);
            i.putExtra("repeat", repeat);
            i.putExtra("canSetDateTime", canSetDateTime);
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
            Calendar currentTime = Calendar.getInstance();
            Calendar dateAndTime = Calendar.getInstance();
            if (canSetDateTime) {
                if (!dateSet) {
                    dateAndTime.set(Calendar.YEAR, currentTime.get(Calendar.YEAR));
                    dateAndTime.set(Calendar.MONTH, currentTime.get(Calendar.MONTH));
                    dateAndTime.set(Calendar.DAY_OF_MONTH, currentTime.get(Calendar.DAY_OF_MONTH));
                    dateAndTime.set(Calendar.HOUR_OF_DAY, dueHour);
                    dateAndTime.set(Calendar.MINUTE, dueMinute);
                    dateAndTime.set(Calendar.SECOND, 0);
                } else {
                    if (!timeSet) {
                        dateAndTime.set(Calendar.YEAR, dueYear);
                        dateAndTime.set(Calendar.MONTH, dueMonth);
                        dateAndTime.set(Calendar.DAY_OF_MONTH, dueDay);
                        dateAndTime.set(Calendar.HOUR_OF_DAY, 23);
                        dateAndTime.set(Calendar.MINUTE, 59);
                        dateAndTime.set(Calendar.SECOND, 59);
                    } else {
                        dateAndTime.set(Calendar.YEAR, dueYear);
                        dateAndTime.set(Calendar.MONTH, dueMonth);
                        dateAndTime.set(Calendar.DAY_OF_MONTH, dueDay);
                        dateAndTime.set(Calendar.HOUR_OF_DAY, dueHour);
                        dateAndTime.set(Calendar.MINUTE, dueMinute);
                        dateAndTime.set(Calendar.SECOND, 0);
                    }
                }
            } else {
                dateAndTime.set(Calendar.YEAR, 9001);
                dateAndTime.set(Calendar.MONTH, 0);
                dateAndTime.set(Calendar.DAY_OF_MONTH, 1);
                dateAndTime.set(Calendar.HOUR_OF_DAY, 0);
                dateAndTime.set(Calendar.MINUTE, 0);
                dateAndTime.set(Calendar.SECOND, 0);
            }
            i.putExtra("timeStamp", dateAndTime.getTimeInMillis());
            i.putExtra("position", editPosition);
            i.putExtra("notificationID", notificationID);
            i.putExtra("repeat", repeat);
            i.putExtra("canSetDateTime", canSetDateTime);
            setResult(RESULT_OK, i);
            finish();
        }
    }


    public void populateData() {
        EditText nameField = (EditText) findViewById(R.id.nameField);
        nameField.setText(name);
        if (dueYear < 9000) {
            setTimeString();
            setDateString();
        }
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
        if (canSetDateTime) {
            if (!dateSet) {
                if (!timeSet) {
//                    goodDateAndTime = false;
                    Toast correctTime = Toast.makeText(getApplicationContext(), "Task must be due sometime after this moment", Toast.LENGTH_LONG);
                    correctTime.show();
                    return;
                } else {
                    timePicked.set(Calendar.HOUR_OF_DAY, dueHour);
                    timePicked.set(Calendar.MINUTE, dueMinute);
                    timePicked.set(Calendar.SECOND, 0);
                }
            } else {
                if (!timeSet) {
                    timePicked.set(Calendar.YEAR, dueYear);
                    timePicked.set(Calendar.MONTH, dueMonth);
                    timePicked.set(Calendar.DAY_OF_MONTH, dueDay);
                    timePicked.set(Calendar.SECOND, 0);
                } else {
                    timePicked.set(Calendar.YEAR, dueYear);
                    timePicked.set(Calendar.MONTH, dueMonth);
                    timePicked.set(Calendar.DAY_OF_MONTH, dueDay);
                    timePicked.set(Calendar.HOUR_OF_DAY, dueHour);
                    timePicked.set(Calendar.MINUTE, dueMinute);
                    timePicked.set(Calendar.SECOND, 0);
                }
            }
        } else {
            goodDateAndTime = true;
            return;
        }

//        if (!dateSet) {
//            if (!timeSet) {
//                //this means the task has an indefinite due date
//                if(!canSetDateTime) {
//                    goodDateAndTime = true;
//                } else {
//                    goodDateAndTime = false;
//                    Toast correctTime = Toast.makeText(getApplicationContext(), "Task must be due sometime after this moment", Toast.LENGTH_LONG);
//                    correctTime.show();
//                }
//                return;
//            }
//            timePicked.set(Calendar.HOUR_OF_DAY, dueHour);
//            timePicked.set(Calendar.MINUTE, dueMinute);
//            timePicked.set(Calendar.SECOND, 0);
//        } else {
//            if (!timeSet) {
//                timePicked.set(Calendar.YEAR, dueYear);
//                timePicked.set(Calendar.MONTH, dueMonth);
//                timePicked.set(Calendar.DAY_OF_MONTH, dueDay);
//                timePicked.set(Calendar.SECOND, 0);
//            } else {
//                timePicked.set(Calendar.YEAR, dueYear);
//                timePicked.set(Calendar.MONTH, dueMonth);
//                timePicked.set(Calendar.DAY_OF_MONTH, dueDay);
//                timePicked.set(Calendar.HOUR_OF_DAY, dueHour);
//                timePicked.set(Calendar.MINUTE, dueMinute);
//                timePicked.set(Calendar.SECOND, 0);
//            }
//        }
        if ((timePicked.compareTo(Calendar.getInstance()) < 0)) {
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
        if (dateSet) {
            outState.putInt("dueYear", dueYear);
            outState.putInt("dueMonth", dueMonth);
            outState.putInt("dueDay", dueDay);
            outState.putBoolean("collectDate", true);
        } else {
            outState.putBoolean("collectDate", false);
        }
        if (timeSet) {
            outState.putInt("dueHour", dueHour);
            outState.putInt("dueMinute", dueMinute);
            outState.putBoolean("collectDueTime", true);
        } else {
            outState.putBoolean("collectDueTime", false);
        }
        outState.putBoolean("dateSet", dateSet);
        outState.putBoolean("timeSet", timeSet);
        outState.putInt("notificationID", notificationID);
        outState.putInt("repeat", repeat);
        outState.putBoolean("canSetDateTime", canSetDateTime);
        outState.putInt("position", editPosition);
    }
}
