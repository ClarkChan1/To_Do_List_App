package com.example.myfirstapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
    private String name;
    private String category;
    private long timeStamp;
    private int notificationID;
    private int repeat;


    public Item(String name, String category, long timeStamp, int notificationID, int repeat) {
        this.name = name;
        this.category = category;
        this.timeStamp = timeStamp;
        this.notificationID = notificationID;
        this.repeat = repeat;
    }

    protected Item(Parcel in) {
        name = in.readString();
        category = in.readString();
        timeStamp = in.readLong();
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

    public long getTimeStamp() { return timeStamp; }

    public void setTimeStamp(long timeStamp) { this.timeStamp = timeStamp; }

    public int getNotificationID() {
        return notificationID;
    }

    public int getRepeat() { return repeat; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(category);
        dest.writeLong(timeStamp);
    }
}
