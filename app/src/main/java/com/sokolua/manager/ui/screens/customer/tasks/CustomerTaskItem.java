package com.sokolua.manager.ui.screens.customer.tasks;

public class CustomerTaskItem {
    private String taskText;
    private int taskType;
    private boolean header;

    public CustomerTaskItem(String taskText, int taskType, boolean header) {
        this.header = header;
        this.taskText = taskText;
        this.taskType = taskType;
    }


    //region ================================ Getters ==================================

    public String getTaskText() {
        return taskText;
    }

    public int getTaskType() {
        return taskType;
    }

    public boolean isHeader() {
        return header;
    }


    //endregion ============================= Getters ==================================


}
