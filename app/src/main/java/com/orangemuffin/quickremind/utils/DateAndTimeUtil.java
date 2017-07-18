package com.orangemuffin.quickremind.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/* Created by OrangeMuffin on 7/3/2017 */
public class DateAndTimeUtil {

    public static int getDueDate(String reminder_date) {
        String pattern = "EEE, dd MMM yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();
        String today = dateFormat.format(calendar.getTime());
        try {
            Date one = dateFormat.parse(today);
            Date two = dateFormat.parse(reminder_date);
            long diff = two.getTime() - one.getTime();

            return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String convertDate(String pattern, String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy");
            Date temp = dateFormat.parse(date);
            dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.format(temp);
        } catch (Exception e) { }
        return null;
    }

    public static String convertTime(String pattern, String time) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Date temp = dateFormat.parse(time);
            dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.format(temp);
        } catch (Exception e) { }
        return null;
    }

    public static String dateToDay(String date) {
        String day = "";
        String pattern = "EEE, dd MMM yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(date));
            int result = calendar.get(Calendar.DAY_OF_WEEK);
            switch (result) {
                case Calendar.MONDAY:
                    day = "Monday";
                    break;
                case Calendar.TUESDAY:
                    day = "Tuesday";
                    break;
                case Calendar.WEDNESDAY:
                    day = "Wednesday";
                    break;
                case Calendar.THURSDAY:
                    day = "Thursday";
                    break;
                case Calendar.FRIDAY:
                    day = "Friday";
                    break;
                case Calendar.SATURDAY:
                    day = "Saturday";
                    break;
                case Calendar.SUNDAY:
                    day = "Sunday";
                    break;
            }
        } catch (Exception e) { }
        return day;
    }
}
