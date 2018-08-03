package com.sokolua.manager.data.storage.realm;

import com.sokolua.manager.data.managers.ConstantManager;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class TaskRealm extends RealmObject implements Serializable {
    @Required
    @PrimaryKey
    private String taskId;
    private CustomerRealm customer;
    @Required
    private String text;
    private int taskType;
    private boolean done = false;
    private String result="";

    public TaskRealm() {
    }

    public TaskRealm(CustomerRealm customer, String taskId, String text, int taskType) {
        this.customer = customer;
        this.taskId = taskId;
        this.text = text;
        this.taskType = taskType;
    }

    //region ===================== Getters =========================

    public CustomerRealm getCustomer() {
        return customer;
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
