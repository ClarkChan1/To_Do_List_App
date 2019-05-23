package com.example.myfirstapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
    private String name;
    private String category;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endtMinute;
    public Item(String name, String category, int startHour, int startMinute, int endHour, int endtMinute) {
        this.name = name;
        this.category = category;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endtMinute = endtMinute;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndtMinute() {
        return endtMinute;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
