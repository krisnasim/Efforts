package com.android.efforts.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Jonathan Simananda on 09/05/2017.
 */

public class Task implements Parcelable {

    //variables all here
    private String taskName;
    private String taskTitle;
    private String taskContent;
    private String taskStatus;
    private Date taskTimestamp;
    private Date taskStartDate;
    private Date taskEndDate;
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

    public Date getTaskStartDate() {
        return taskStartDate;
    }

    public void setTaskStartDate(Date taskStartDate) {
        this.taskStartDate = taskStartDate;
    }

    public Date getTaskEndDate() {
        return taskEndDate;
    }

    public void setTaskEndDate(Date taskEndDate) {
        this.taskEndDate = taskEndDate;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.taskName);
        dest.writeString(this.taskTitle);
        dest.writeString(this.taskContent);
        dest.writeString(this.taskStatus);
        dest.writeLong(this.taskTimestamp != null ? this.taskTimestamp.getTime() : -1);
        dest.writeLong(this.taskStartDate != null ? this.taskStartDate.getTime() : -1);
        dest.writeLong(this.taskEndDate != null ? this.taskEndDate.getTime() : -1);
        dest.writeString(this.taskID);
    }

    public Task() {
    }

    protected Task(Parcel in) {
        this.taskName = in.readString();
        this.taskTitle = in.readString();
        this.taskContent = in.readString();
        this.taskStatus = in.readString();
        long tmpTaskTimestamp = in.readLong();
        this.taskTimestamp = tmpTaskTimestamp == -1 ? null : new Date(tmpTaskTimestamp);
        long tmpTaskStartDate = in.readLong();
        this.taskStartDate = tmpTaskStartDate == -1 ? null : new Date(tmpTaskStartDate);
        long tmpTaskEndDate = in.readLong();
        this.taskEndDate = tmpTaskEndDate == -1 ? null : new Date(tmpTaskEndDate);
        this.taskID = in.readString();
    }

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
