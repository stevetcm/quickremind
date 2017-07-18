package com.orangemuffin.quickremind.models;

/* Created by OrangeMuffin on 6/21/2017 */
public class Reminder {
    private int id;
    private String content;
    private String date;
    private String time;
    private String repeat;
    private String repeatNo;
    private String repeatType;
    private String active;
    private String persistent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getRepeatNo() {
        return repeatNo;
    }

    public void setRepeatNo(String repeatNo) {
        this.repeatNo = repeatNo;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getPersistent() {
        return persistent;
    }

    public void setPersistent(String persistent) {
        this.persistent = persistent;
    }
}
