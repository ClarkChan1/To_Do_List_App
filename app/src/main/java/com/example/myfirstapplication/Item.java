package com.example.myfirstapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
    private String name;
    private String category;
    private int dueHour;
    private int dueMinute;
    private int notificationID;


    public Item(String name, String category, int dueHour, int dueMinute, int notificationID) {
        this.name = name;
        this.category = category;
        this.dueHour = dueHour;
        this.dueMinute = dueMinute;
        this.notificationID = notificationID;
    }

    protected Item(Parcel in) {
        name = in.readString();
        category = in.readString();
        dueHour = in.readInt();
        dueMinute = in.readInt();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

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

    public int getNotificationID() {
        return notificationID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(category);
        dest.writeInt(dueHour);
        dest.writeInt(dueMinute);
    }
}
