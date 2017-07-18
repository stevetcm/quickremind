package com.orangemuffin.quickremind.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.orangemuffin.quickremind.R;
import com.orangemuffin.quickremind.database.ReminderDatabase;
import com.orangemuffin.quickremind.models.Reminder;
import com.orangemuffin.quickremind.receivers.AlarmReceiver;
import com.orangemuffin.quickremind.utils.DateAndTimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/* Created by OrangeMuffin on 6/30/2017 */
public class ReminderEditActivity extends AppCompatActivity {
    private Toolbar toolbar;

    private Calendar calendar;

    private int reminderID;
    private ReminderDatabase rb;
    private Reminder mReminder;

    private EditText reminder_content;
    private TextView date_text;
    private TextView time_text;
    private TextView repeat_text;

    private String mContent;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String mDate;
    private int mHour;
    private int mMinute;
    private String mTime;
    private String mRepeat;
    private int mRepeatNo;
    private String mRepeatType;
    private String mPersistent;
    private String mActive;

    private DatePickerDialog datePicker;
    private TimePickerDialog timePicker;

    private CheckBox persistent_box;

    private AlarmReceiver mAlarmReceiver;

    boolean colorChange = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        //initializing views
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        reminder_content = (EditText) findViewById(R.id.reminder_content);
        date_text = (TextView) findViewById(R.id.date_text);
        time_text = (TextView) findViewById(R.id.time_text);
        repeat_text = (TextView) findViewById(R.id.repeat_text);
        persistent_box = (CheckBox) findViewById(R.id.persistent_checkbox);

        //setup toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Reminder");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get reminder id from intent extras
        reminderID = getIntent().getIntExtra("REMINDER_ID", -1);

        // Get reminder using reminder id
        rb = new ReminderDatabase(this);
        mReminder = rb.getReminder(reminderID);

        calendar = Calendar.getInstance();
        mAlarmReceiver = new AlarmReceiver();

        //recreate calendar from date and time values saved
        String dateandtime = mReminder.getDate() + " " + mReminder.getTime();
        try {
            SimpleDateFormat dt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm");
            Date converted = dt.parse(dateandtime);
            dt = new SimpleDateFormat("yyyyMMddHHmm");
            dateandtime = dt.format(converted);
            calendar.setTime(dt.parse(dateandtime));
        } catch (Exception e) { }

        //retrieve variables from saved calendar
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH)+1;
        mDay = calendar.get(Calendar.DATE);
        mDate = mReminder.getDate();

        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
        mTime = mReminder.getTime();

        mRepeat = mReminder.getRepeat();
        mRepeatNo = Integer.parseInt(mReminder.getRepeatNo());
        mRepeatType = mReminder.getRepeatType();

        mPersistent = mReminder.getPersistent();

        mActive = "true"; //reset to active for it to be processed

        mContent = mReminder.getContent();

        //setup reminder content configuration
        reminder_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                mContent = text.toString().trim();
                reminder_content.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        //setup onClick detection for persistent checkbox
        persistent_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    mPersistent = "true";
                } else {
                    mPersistent = "false";
                }
            }
        });

        //setup edit reminder views with saved values
        reminder_content.setText(mContent);
        date_text.setText(mDate);
        time_text.setText(DateAndTimeUtil.convertTime("hh:mm a", mTime));
        if (mRepeat.equals("true")) {
            repeat_text.setText("Every " + mRepeatNo + " " + mRepeatType);
        } else {
            repeat_text.setText("Do not Repeat");
        }
        if (mPersistent.equals("true")) {
            persistent_box.setChecked(true);
        }
    }

    //show date picker dialog on layout click
    public void setDate(View view) {
        //hide soft keyboard if visible
        View view_layout = this.getCurrentFocus();
        if (view_layout != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        //configure date picker dialog
        datePicker = new DatePickerDialog(ReminderEditActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mYear = year; mMonth = month+1; mDay = day;

                mDate = mYear + "-" + mMonth + "-" + mDay;
                Date date;
                SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat dt1 = new SimpleDateFormat("EEE, dd MMM yyyy");
                try {
                    date = dt.parse(mDate);
                    mDate = dt1.format(date);
                } catch (ParseException e) { }

                date_text.setText(mDate);
            }
        }, mYear, mMonth-1, mDay);
        datePicker.show();
    }

    //show time picker dialog on layout click
    public void setTime(View view) {
        //hide soft keyboard if visible
        View view_layout = this.getCurrentFocus();
        if (view_layout != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        //configure time picker dialog
        timePicker = new TimePickerDialog(ReminderEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                mHour = hour; mMinute = minute;

                if (mHour < 10 & mMinute < 10) {
                    mTime = "0" + mHour + ":" + "0" + mMinute;
                } else if (mHour < 10 & mMinute >= 10) {
                    mTime = "0" + mHour + ":" + mMinute;
                } else if (mHour >= 10 & mMinute < 10) {
                    mTime = mHour + ":" + "0" + mMinute;
                } else {
                    mTime = mHour + ":" + mMinute;
                }

                time_text.setText(DateAndTimeUtil.convertTime("hh:mm a", mTime));
            }
        }, mHour, mMinute, false);
        timePicker.setTitle("");
        timePicker.show();
    }

    //show repeat selection dialog on layout click
    public void setRepeat(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ReminderEditActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.repeat_picker_dialog, null);
        dialog.setView(dialogView);

        final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);
        numberPicker.setMaxValue(60);
        numberPicker.setMinValue(1);
        numberPicker.setValue(mRepeatNo);

        final NumberPicker categoryPicker = (NumberPicker) dialogView.findViewById(R.id.dialog_category_picker);
        categoryPicker.setMaxValue(4);
        categoryPicker.setMinValue(0);
        final String[] category = new String[] {"Minute(s)", "Hour(s)", "Day(s)", "Week(s)", "Month(s)"};
        if (mRepeatType.equals("Minute(s))")) {
            categoryPicker.setValue(0);
        } else if (mRepeatType.equals("Hour(s)")) {
            categoryPicker.setValue(1);
        } else if (mRepeatType.equals("Day(s)")) {
            categoryPicker.setValue(2);
        } else if (mRepeatType.equals("Week(s)")) {
            categoryPicker.setValue(3);
        } else if (mRepeatType.equals("Month(s)")) {
            categoryPicker.setValue(4);
        }
        categoryPicker.setDisplayedValues(category);

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mRepeat = "true";
                mRepeatNo = numberPicker.getValue();
                mRepeatType = category[categoryPicker.getValue()];
                repeat_text.setText("Every " + mRepeatNo + " " + mRepeatType);
            }
        });
        dialog.setNeutralButton("NO REPEAT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mRepeat = "false";
                mRepeatNo = 0;
                mRepeatType = "No";
                repeat_text.setText("Do Not Repeat");
            }
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    public void updateReminder() {
        mReminder.setContent(mContent);
        mReminder.setDate(mDate);
        mReminder.setTime(mTime);
        mReminder.setRepeat(mRepeat);
        mReminder.setRepeatNo(String.valueOf(mRepeatNo));
        mReminder.setRepeatType(mRepeatType);
        mReminder.setActive(mActive);
        mReminder.setPersistent(mPersistent);

        //update reminder in database
        rb.updateReminder(mReminder);

        //cancel existing reminder alarm by using its ID
        mAlarmReceiver.cancelAlarm(getApplicationContext(), reminderID);

        //setup calendar for alarm
        String dateandtime = mDate + " " + mTime;
        try {
            SimpleDateFormat dt = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm");
            Date converted = dt.parse(dateandtime);
            dt = new SimpleDateFormat("yyyyMMddHHmm");
            dateandtime = dt.format(converted);
            calendar.setTime(dt.parse(dateandtime));
        } catch (Exception e) { }

        //reset reminder alarm marked by its ID
        mAlarmReceiver.setAlarm(getApplicationContext(), calendar, reminderID);

        Toast.makeText(getApplicationContext(), "Reminder edited", Toast.LENGTH_SHORT).show();

        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reminder_edit, menu);

        //reset to default color on create
        MenuItem colorIcon = menu.getItem(0); //first item is color icon
        GradientDrawable colorDrawable = (GradientDrawable) colorIcon.getIcon();
        colorDrawable.setColor(Color.parseColor("#8c8c8c"));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_color:
                GradientDrawable colorDrawable = (GradientDrawable) item.getIcon();
                if (colorChange) {
                    colorDrawable.setColor(Color.RED);
                    colorChange = false;
                } else {
                    colorDrawable.setColor(Color.parseColor("#00bce7"));
                    colorChange = true;
                }
                Toast.makeText(getApplicationContext(), "Coming Soon!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_edit:
                reminder_content.setText(mContent);
                if (reminder_content.getText().toString().length() == 0) {
                    reminder_content.setError("Text content cannot be empty.");
                } else {
                    updateReminder();
                }
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_down);
    }
}
