package com.example.wxc.memo;

import android.provider.ContactsContract;

import org.litepal.crud.LitePalSupport;

public class Note extends LitePalSupport{
    private int id;
    private int start_hour;
    private int start_minute;
    private int end_hour;
    private int end_minute;
    private int week;
    private int day;
    private String theme;
    private String note;

    public void setStart_minute(int start_minute) {
        this.start_minute = start_minute;
    }

    public void setEnd_minute(int end_minute) {
        this.end_minute = end_minute;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getStart_minute() {
        return start_minute;
    }

    public int getEnd_minute() {
        return end_minute;
    }

    public int getWeek() {
        return week;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStart_hour(int start) {
        this.start_hour = start;
    }

    public void setEnd_hour(int end) {
        this.end_hour = end;
    }

    public void setDay(int dat) {
        this.day = dat;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public int getStart_hour() {
        return start_hour;
    }

    public int getEnd_hour() {
        return end_hour;
    }

    public int getDay() {
        return day;
    }

    public String getTheme() {
        return theme;
    }

    public String getNote() {
        return note;
    }
}
