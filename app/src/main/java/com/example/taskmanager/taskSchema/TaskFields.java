package com.example.taskmanager.taskSchema;

import java.io.Serializable;

public class TaskFields implements Serializable {
    private String nameTask, mainTextInTask;

    public TaskFields() { }

    public TaskFields(String nameTask, String mainTextInTask) {
        this.nameTask = nameTask;
        this.mainTextInTask = mainTextInTask;
    }

    public String getNameTask() {
        return nameTask;
    }

    public void setNameTask(String nameTask) {
        this.nameTask = nameTask;
    }

    public String getMainTextInTask() {
        return mainTextInTask;
    }

    public void setMainTextInTask(String mainTextInTask) {
        this.mainTextInTask = mainTextInTask;
    }
}
