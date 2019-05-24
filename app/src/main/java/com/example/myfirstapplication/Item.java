package com.example.myfirstapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
    private String name;
    private String category;
    private int dueHour;
    private int dueMinute;
    public Item(String name, String category, int dueHour, int dueMinute) {
        this.name = name;
        this.category = category;
        this.dueHour = dueHour;
        this.dueMinute = dueMinute;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getDueHour() {
        return dueHour;
    }

    public int getDueMinute() {
        return dueMinute;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
