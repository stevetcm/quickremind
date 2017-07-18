package com.orangemuffin.quickremind.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.orangemuffin.quickremind.models.Reminder;

import java.util.ArrayList;
import java.util.List;

/* Created by OrangeMuffin on 6/21/2017 */
public class ReminderDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    //Database Names
    private static final String DATABASE_NAME = "ReminderDatabase";
    private static final String TABLE_REMINDERS = "ReminderTable";

    //Table Columns Names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_REPEAT = "repeat";
    private static final String KEY_REPEAT_NO = "repeat_no";
    private static final String KEY_REPEAT_TYPE = "repeat_type";
    private static final String KEY_ACTIVE = "active";
    private static final String KEY_PERSISTENT = "persistent";

    public ReminderDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_REMINDERS_TABLE = "CREATE TABLE " + TABLE_REMINDERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_TIME + " INTEGER,"
                + KEY_REPEAT + " BOOLEAN,"
                + KEY_REPEAT_NO + " INTEGER,"
                + KEY_REPEAT_TYPE + " TEXT,"
                + KEY_ACTIVE + " BOOLEAN,"
                + KEY_PERSISTENT + " BOOLEAN" + ")";
        //create table specified
        db.execSQL(CREATE_REMINDERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop older table if existed
        if (oldVersion >= newVersion) { return; }
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);
        this.onCreate(db); //recreate table again
    }

    public int addReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TITLE , reminder.getContent());
        values.put(KEY_DATE , reminder.getDate());
        values.put(KEY_TIME , reminder.getTime());
        values.put(KEY_REPEAT , reminder.getRepeat());
        values.put(KEY_REPEAT_NO , reminder.getRepeatNo());
        values.put(KEY_REPEAT_TYPE, reminder.getRepeatType());
        values.put(KEY_ACTIVE, reminder.getActive());
        values.put(KEY_PERSISTENT, reminder.getPersistent());

        //insert row and retrieve generated unique id
        long ID = db.insert(TABLE_REMINDERS, null, values);
        db.close();
        return (int) ID;
    }

    public Reminder getReminder(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String GET_REMINDER_BY_ID = "SELECT * FROM " + TABLE_REMINDERS
                + " WHERE " + KEY_ID + " = ? LIMIT 1";
        Cursor cursor = db.rawQuery(GET_REMINDER_BY_ID,  new String[]{String.valueOf(id)});

        if (cursor != null && cursor.getCount() != 0) { cursor.moveToFirst(); }

        Reminder reminder = new Reminder();
        reminder.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
        reminder.setContent(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
        reminder.setDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)));
        reminder.setTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIME)));
        reminder.setRepeat(cursor.getString(cursor.getColumnIndexOrThrow(KEY_REPEAT)));
        reminder.setRepeatNo(cursor.getString(cursor.getColumnIndexOrThrow(KEY_REPEAT_NO)));
        reminder.setRepeatType(cursor.getString(cursor.getColumnIndexOrThrow(KEY_REPEAT_TYPE)));
        reminder.setActive(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ACTIVE)));
        reminder.setPersistent(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PERSISTENT)));

        return reminder;
    }

    public List<Reminder> getAllReminders() {
        List<Reminder> reminderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String GET_ALL_REMINDERS = "SELECT * FROM " + TABLE_REMINDERS;
        Cursor cursor = db.rawQuery(GET_ALL_REMINDERS, null);

        if (cursor.moveToFirst()) {
            do {
                Reminder reminder = new Reminder();
                reminder.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                reminder.setContent(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
                reminder.setDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)));
                reminder.setTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIME)));
                reminder.setRepeat(cursor.getString(cursor.getColumnIndexOrThrow(KEY_REPEAT)));
                reminder.setRepeatNo(cursor.getString(cursor.getColumnIndexOrThrow(KEY_REPEAT_NO)));
                reminder.setRepeatType(cursor.getString(cursor.getColumnIndexOrThrow(KEY_REPEAT_TYPE)));
                reminder.setActive(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ACTIVE)));
                reminder.setPersistent(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PERSISTENT)));

                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }

        return reminderList;
    }

    public int getReminderCount() {
        SQLiteDatabase db = this.getReadableDatabase();

        String GET_ALL_REMINDERS = "SELECT * FROM " + TABLE_REMINDERS;
        Cursor cursor = db.rawQuery(GET_ALL_REMINDERS, null);

        return cursor.getCount();
    }

    public int updateReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TITLE, reminder.getContent());
        values.put(KEY_DATE, reminder.getDate());
        values.put(KEY_TIME, reminder.getTime());
        values.put(KEY_REPEAT, reminder.getRepeat());
        values.put(KEY_REPEAT_NO, reminder.getRepeatNo());
        values.put(KEY_REPEAT_TYPE, reminder.getRepeatType());
        values.put(KEY_ACTIVE, reminder.getActive());
        values.put(KEY_PERSISTENT, reminder.getPersistent());

        long ID = db.update(TABLE_REMINDERS, values, KEY_ID + "=?",
                new String[]{String.valueOf(reminder.getId())});
        return (int) ID;
    }

    public void deleteReminder(Reminder reminder){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REMINDERS, KEY_ID + "=?",
                new String[]{String.valueOf(reminder.getId())});
        db.close();
    }
}
