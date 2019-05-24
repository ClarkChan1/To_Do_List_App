package com.example.myfirstapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
    private String name;
    private String category;
    private int dueHour;
    private int dueMinute;
    private boolean isAfternoon = false;
    public Item(String name, String category, int dueHour, int dueMinute, boolean isAfternoon) {
        this.name = name;
        this.category = category;
        this.dueHour = dueHour;
        this.dueMinute = dueMinute;
        this.isAfternoon = isAfternoon;
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

    public boolean isAfternoon() {
        return isAfternoon;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
