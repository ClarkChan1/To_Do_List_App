package com.example.myfirstapplication;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DataManager {
    public static void saveItem(Context context, String fileName, Item item) {
        try {
            String itemString = item.getName() + "," + item.getCategory() + "," + item.getDueHour() + "," + item.getDueMinute() + "," + item.isAfternoon() + "\n";
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_APPEND);
            fos.write(itemString.getBytes());
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
        ArrayList<Item> loadedItems = new ArrayList<Item>();
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader irs = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(irs);
            String line;
            while ((line = br.readLine()) != null) {
                String[] itemParts = line.split(",");
                loadedItems.add(new Item(itemParts[0], itemParts[1],Integer.parseInt(itemParts[2]), Integer.parseInt(itemParts[3]), Boolean.parseBoolean(itemParts[4])));
            }
            fis.close();
            irs.close();
            br.close();
        } catch (IOException e) {
            System.out.println("IOException while trying to load!");
            e.printStackTrace();
        }
        return loadedItems;
    }
}
