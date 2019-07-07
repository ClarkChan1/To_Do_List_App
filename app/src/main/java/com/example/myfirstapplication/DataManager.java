package com.example.myfirstapplication;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DataManager {
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void saveItems(Context context, String fileName, ArrayList<Item> itemsToSave) {
        try {
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
            //Create a FileOutputStream for the case where the device doesn't have the json file yet
            File currentJSONFile = new File(context.getFilesDir().getAbsolutePath() + "/" + fileName);
            if (!currentJSONFile.exists()) {
                FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                fos.close();
            }
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            loadedItems = gson.fromJson(br, new TypeToken<ArrayList<Item>>() {
            }.getType());
            fis.close();
            isr.close();
            br.close();
        } catch (IOException e) {
            System.out.println("IOException while trying to load in readItems()!");
            e.printStackTrace();
        }
        return loadedItems == null ? (new ArrayList<Item>()) : loadedItems;
    }

//    public static void checkDate(Context context, String[] fileNames, String dateString) {
//        try {
//            //Create a FileOutputStream for the case where the device doesn't have the text file yet
//            String dateFileName = "CurrentDate.txt";
//            File currentDateFile = new File(context.getFilesDir().getAbsolutePath() + "/" + dateFileName);
//            if (!currentDateFile.exists()) {
//                FileOutputStream fos = context.openFileOutput(dateFileName, Context.MODE_PRIVATE);
//                fos.close();
//            }
//            FileInputStream fis = context.openFileInput(dateFileName);
//            InputStreamReader isr = new InputStreamReader(fis);
//            BufferedReader br = new BufferedReader(isr);
//            String storedDate = br.readLine();
//
//            fis.close();
//            isr.close();
//            br.close();
//
//            //to create the file we need the FileOutputStream
//            FileOutputStream fos = context.openFileOutput(dateFileName, Context.MODE_PRIVATE);
//
//            if (storedDate == null) {
//                fos.write(dateString.getBytes());
//            } else {
//                if (!storedDate.equals(dateString)) {
//                    for (int a = 0; a < fileNames.length; a++) {
//                        clearData(context, fileNames[a]);
//                    }
//                    //reset notificationID to 0
//                    FileOutputStream fosID = context.openFileOutput("NotificationID.json", Context.MODE_PRIVATE);
//                    fosID.write("0".getBytes());
//                    fos.write(dateString.getBytes());
//                } else {
//                    //since we are rewriting the file, we need to put the date back if it is equal
//                    fos.write(storedDate.getBytes());
//                }
//            }
//            fos.close();
//        } catch (IOException e) {
//            System.out.println("IOException while trying to load in checkDate()!");
//            e.printStackTrace();
//        }
//    }
//
//    public static void clearData(Context context, String fileName) {
//        try {
//            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
//            fos.close();
//        } catch (FileNotFoundException e) {
//            System.out.println("FileNotFoundException while trying to save!");
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.out.println("IOException while trying to save!");
//            e.printStackTrace();
//        }
//    }

    public static int readNotificationID(Context context, String fileName) {
        int notificationID = 0;
        try {
            //Create a FileOutputStream for the case where the device doesn't have the json file yet
            File currentJSONFile = new File(context.getFilesDir().getAbsolutePath() + "/" + fileName);
            if (!currentJSONFile.exists()) {
                FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                fos.write(Integer.toString(0).getBytes());
                fos.close();
            }
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            notificationID = Integer.parseInt(br.readLine());

            fis.close();
            isr.close();
            br.close();
        } catch (IOException e) {
            System.out.println("IOException while trying to load in readItems()!");
            e.printStackTrace();
        }
        return notificationID;
    }

    public static void saveNotificationID(Context context, int toSave) {
        try {
            FileOutputStream fos = context.openFileOutput("NotificationID.json", Context.MODE_PRIVATE);
            fos.write(String.valueOf(toSave).getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException while trying to save!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException while trying to save!");
            e.printStackTrace();
        }
    }

    public static void saveListOrders(Context context, ListOrderTracker listOrdersToSave) {
        try {
            String jsonString = gson.toJson(listOrdersToSave);
            FileOutputStream fos = context.openFileOutput("ListOrders.json", Context.MODE_PRIVATE);
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

    public static ListOrderTracker readListOrders(Context context, String fileName) {
        ListOrderTracker listOrders = null;
        try {
            //Create a FileOutputStream for the case where the device doesn't have the json file yet
            File currentJSONFile = new File(context.getFilesDir().getAbsolutePath() + "/" + fileName);
            if (!currentJSONFile.exists()) {
                FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                fos.close();
            }
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            listOrders = gson.fromJson(br, ListOrderTracker.class);
            fis.close();
            isr.close();
            br.close();
        } catch (IOException e) {
            System.out.println("IOException while trying to load in readItems()!");
            e.printStackTrace();
        }
        return listOrders == null ? (new ListOrderTracker(true, false, false)) : listOrders;
    }
}
