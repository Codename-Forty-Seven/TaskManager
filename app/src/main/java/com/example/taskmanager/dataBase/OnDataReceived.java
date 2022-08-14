package com.example.taskmanager.dataBase;

import com.example.taskmanager.adapters.BoxWithTasks;

import java.util.List;

public interface OnDataReceived {
    void onReceived(List<BoxWithTasks> boxWithTasks);
}
