package com.android.efforts.model;

import java.util.Date;

/**
 * Created by Jonathan Simananda on 09/05/2017.
 */

public class Task {

    //variables all here
    private String taskName;
    private String taskTitle;
    private String taskContent;
    private Date taskTimestamp;
    private String taskID;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public Date getTaskTimestamp() {
        return taskTimestamp;
    }

    public void setTaskTimestamp(Date taskTimestamp) {
        this.taskTimestamp = taskTimestamp;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }
}
