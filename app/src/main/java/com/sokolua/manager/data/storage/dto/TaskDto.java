package com.sokolua.manager.data.storage.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.sokolua.manager.data.storage.realm.TaskRealm;

public class TaskDto implements Parcelable{
    private String customerId;
    private String taskId;
    private String text;
    private int taskType;

    public TaskDto(String taskId, String task, int taskType) {
        this.taskId = taskId;
        this.text = task;
        this.taskType = taskType;
    }

    public TaskDto(Parcel parcel) {
        this.customerId = parcel.readString();
        this.taskId = parcel.readString();
        this.text = parcel.readString();
        this.taskType = parcel.readInt();
    }

    public TaskDto(TaskRealm realm) {
        this.customerId = realm.getCustomer().getCustomerId();
        this.taskId = realm.getTaskId();
        this.text = realm.getText();
        this.taskType = realm.getTaskType();
    }

    //region ===================== Parcelable =========================

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.customerId);
        dest.writeString(this.taskId);
        dest.writeString(this.text);
        dest.writeInt(this.taskType);
    }

    public static final Creator<TaskDto> CREATOR = new Creator<TaskDto>() {
        @Override
        public TaskDto createFromParcel(Parcel in) {
            return new TaskDto(in);
        }

        @Override
        public TaskDto[] newArray(int size) {
            return new TaskDto[size];
        }
    };

    //endregion ================== Parcelable =========================

    //region ===================== Getters =========================

    public String getCustomerId() {
        return customerId;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getText() {
        return text;
    }

    public int getTaskType() {
        return taskType;
    }


    //endregion ================== Getters =========================
}
