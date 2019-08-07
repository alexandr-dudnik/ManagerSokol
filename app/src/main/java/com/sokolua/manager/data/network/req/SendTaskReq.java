package com.sokolua.manager.data.network.req;

import com.sokolua.manager.data.storage.realm.TaskRealm;

import io.realm.internal.Keep;

@Keep
public class SendTaskReq {
    private String id;
    private String task;
    private boolean completed;
    private String result;

    public SendTaskReq(String id, String task, boolean completed, String result) {
        this.id = id;
        this.completed = completed;
        this.result = result;
        this.task = task;
    }

    public SendTaskReq(TaskRealm task){
        this.id = task.getTaskId();
        this.completed = task.isDone();
        this.result = task.getResult();
        this.task = task.getText();
    }

    //region =======================  Getters  =========================

    public boolean isCompleted() {
        return completed;
    }

    public String getResult() {
        return result;
    }

    public String getId() {
        return id;
    }

    public String getTask() {
        return task;
    }

//endregion ====================  Getters  =========================
}
