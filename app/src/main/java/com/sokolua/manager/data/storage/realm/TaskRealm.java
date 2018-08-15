package com.sokolua.manager.data.storage.realm;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class TaskRealm extends RealmObject implements Serializable {
    @Required
    @PrimaryKey
    private String taskId;
    private CustomerRealm customer;
    @Required
    private String text;
    @Index
    private int taskType;
    private boolean done = false;
    private String result="";

    public TaskRealm() {
    }

    public TaskRealm(CustomerRealm customer, String taskId,  String text, int taskType, boolean done, String result) {
        this.taskId = taskId;
        this.customer = customer;
        this.text = text;
        this.taskType = taskType;
        this.done = done;
        this.result = result;
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

    public boolean isDone() {
        return done;
    }

    public String getResult() {
        return result;
    }


    //endregion ================== Getters =========================

    //region ===================== Setters =========================
    public void setResult(String result) {
        this.result = result;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    //endregion ================== Setters =========================

}
