package com.example.taskmanager.dataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.taskmanager.adapters.BoxWithTasks;

import java.util.ArrayList;
import java.util.List;

public class MyDbManager {
    private Context context;
    private MyDbHelper myDbHelper;
    private SQLiteDatabase sqLiteDatabase;


    public MyDbManager(Context context) {
        this.context = context;
        myDbHelper = new MyDbHelper(context);
    }

    public void openDb() {
        sqLiteDatabase = myDbHelper.getWritableDatabase();
    }

    public void writeToDb(String nameTask, String textTask, String uriPhotoFromUser) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(MyConstants.NAME_TASK, nameTask);
        contentValues.put(MyConstants.TEXT_TASK, textTask);
        contentValues.put(MyConstants.URI_PHOTO, uriPhotoFromUser);
        sqLiteDatabase.insert(MyConstants.TABLE_NAME, null, contentValues);
    }

    public void readFromDb(String nameTask, OnDataReceived onDataReceived) {
        List<BoxWithTasks> boxWithTasks = new ArrayList<>();
        String searchNameTask = MyConstants.NAME_TASK + " LIKE ?";
        Cursor cursor = sqLiteDatabase.query(MyConstants.TABLE_NAME, null, searchNameTask, new String[]{"%" + nameTask + "%"}, null,
                null, null, null);
        while (cursor.moveToNext()) {
            BoxWithTasks boxWithTasks1 = new BoxWithTasks();
            boxWithTasks1.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MyConstants._ID)));
            boxWithTasks1.setNameTask(cursor.getString(cursor.getColumnIndexOrThrow(MyConstants.NAME_TASK)));
            boxWithTasks1.setMainTextTask(cursor.getString(cursor.getColumnIndexOrThrow(MyConstants.TEXT_TASK)));
            boxWithTasks1.setUriPhotoFromUser(cursor.getString(cursor.getColumnIndexOrThrow(MyConstants.URI_PHOTO)));
            boxWithTasks.add(boxWithTasks1);
        }
        cursor.close();
        onDataReceived.onReceived(boxWithTasks);
    }

    public void updateItemInList(String nameTask, String textTask, String uriPhotoFromUser, int idWhatDelete) {
        String selectFromDb = MyConstants._ID + "=" + idWhatDelete;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyConstants.NAME_TASK, nameTask);
        contentValues.put(MyConstants.TEXT_TASK, textTask);
        contentValues.put(MyConstants.URI_PHOTO, uriPhotoFromUser);
        sqLiteDatabase.update(MyConstants.TABLE_NAME, contentValues, selectFromDb, null);
    }

    public void deleteFromDb(int idWhatDelete) {
        String selectFromDb = MyConstants._ID + "=" + idWhatDelete;
        sqLiteDatabase.delete(MyConstants.TABLE_NAME, selectFromDb, null);
    }

    public void closeDb() {
        myDbHelper.close();
    }


}
