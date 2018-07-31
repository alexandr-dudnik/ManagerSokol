package com.sokolua.manager.data.storage.realm;

import com.sokolua.manager.data.managers.ConstantManager;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class TaskRealm extends RealmObject implements Serializable {
    @Required
    private String customerId;
    @Required
    private String taskId;
    @Required
    private String text;
    private int taskType;

    public TaskRealm() {
    }

    public TaskRealm(String customerId, String taskId, String text, int taskType) {
        this.customerId = customerId;
        this.taskId = taskId;
        this.text = text;
        this.taskType = taskType;
    }

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
