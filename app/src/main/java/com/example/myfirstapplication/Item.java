package com.example.myfirstapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
    private String name;
    private String category;
    private int dueHour;
    private int formattedDueHour;
    private int dueMinute;
    private boolean isAfternoon = false;

    public Item(String name, String category, int dueHour, int dueMinute, boolean isAfternoon) {
        this.name = name;
        this.category = category;
        this.dueHour = dueHour;
        this.dueMinute = dueMinute;
        this.isAfternoon = isAfternoon;
    }

    protected Item(Parcel in) {
        name = in.readString();
        category = in.readString();
        dueHour = in.readInt();
        dueMinute = in.readInt();
        isAfternoon = in.readByte() != 0;
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

    public int getFormattedDueHour() { return formattedDueHour; }

    public void setFormattedDueHour(int formattedDueHour) { this.formattedDueHour = formattedDueHour; }

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
        dest.writeString(name);
        dest.writeString(category);
        dest.writeInt(dueHour);
        dest.writeInt(dueMinute);
        dest.writeByte((byte) (isAfternoon ? 1 : 0));
    }
}
