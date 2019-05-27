package com.example.myfirstapplication;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DataManager {
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static void saveItem(Context context, String fileName, ArrayList<Item> itemsToSave) {
        try {

//            String itemString = item.getName() + "," + item.getCategory() + "," + item.getDueHour() + "," + item.getDueMinute() + "," + item.isAfternoon() + "\n";
            String jsonString = gson.toJson(itemsToSave);
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(jsonString.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException while trying to save!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException while trying to save!");
            e.printStackTrace();
        }
    }

    public static ArrayList<Item> readItems(Context context, String fileName) {
        ArrayList<Item> loadedItems = null;
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            loadedItems = gson.fromJson(br, new TypeToken<ArrayList<Item>>(){}.getType());
            fis.close();
            isr.close();
            br.close();
        } catch (IOException e) {
            System.out.println("IOException while trying to load!");
            e.printStackTrace();
        }
        return loadedItems == null ? (new ArrayList<Item>()) : loadedItems;
    }
}
