package com.sokolua.manager.data.storage.realm;


import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import io.realm.internal.Keep;

@Keep
public class TaskRealm extends RealmObject implements Serializable {
    @Required
    @PrimaryKey
    private String taskId;
    private CustomerRealm customer;
    @Required
    private String text;
    @Index
    private int taskType;
    private Date date;
    private boolean done = false;
    private String result = "";
    private boolean toSync = false;

    public TaskRealm() {
    }

    public TaskRealm(CustomerRealm customer, String taskId,  String text, int taskType, Date date, boolean done, String result) {
        this.taskId = taskId;
        this.customer = customer;
        this.text = text;
        this.taskType = taskType;
        this.done = done;
        this.result = result;
        this.date = date;
    }

    public TaskRealm(CustomerRealm customer, String taskId,  String text, int taskType) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        this.taskId = taskId;
        this.customer = customer;
        this.date = cal.getTime();
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

    public boolean isDone() {
        return done;
    }

    public String getResult() {
        return result;
    }

    public boolean isToSync() {
        return toSync;
    }

    public Date getDate() {
        return date;
    }

//endregion ================== Getters =========================

    //region ===================== Setters =========================
    public void setResult(String result) {
        this.result = result;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setToSync(boolean toSync) {
        this.toSync = toSync;
    }

    //endregion ================== Setters =========================

}
