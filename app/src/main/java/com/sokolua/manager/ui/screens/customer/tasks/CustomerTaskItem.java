package com.sokolua.manager.ui.screens.customer.tasks;

import com.sokolua.manager.data.storage.dto.TaskDto;
import com.sokolua.manager.data.storage.realm.TaskRealm;

public class CustomerTaskItem {
    private String headerText;
    private TaskRealm task;
    private boolean header;

    public CustomerTaskItem(String headerText) {
        this.header = true;
        this.headerText = headerText;
    }

    public CustomerTaskItem(TaskRealm task) {
        this.header = false;
        this.task = task;
    }


    //region ================================ Getters ==================================


    public boolean isHeader() {
        return header;
    }

    public String getHeaderText() {
        return headerText;
    }

    public TaskRealm getTask() {
        return task;
    }

//endregion ============================= Getters ==================================


}
