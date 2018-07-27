package com.sokolua.manager.ui.screens.customer.info;

public class CustomerInfoDataItem {
    public static final int ACTION_TYPE_NO_ACTION = 0;
    public static final int ACTION_TYPE_OPEN_MAP  = 1;
    public static final int ACTION_TYPE_MAKE_CALL = 2;
    public static final int ACTION_TYPE_SEND_MAIL = 3;
    private String header;
    private String data;
    private int actionType = ACTION_TYPE_NO_ACTION;
    private String actionData = "";

    public CustomerInfoDataItem(String header, String data) {
        this.header = header;
        this.data = data;
    }

    //region ================================ Getters ==================================

    public String getHeader() {
        return header;
    }

    public String getData() {
        return data;
    }

    public int getActionType() {
        return actionType;
    }

    public String getActionData() {
        return actionData;
    }


    //endregion ============================= Getters ==================================


    //region ================================ Setter ==================================

    public void setAction(int actionType, String actionData) {
        this.actionType = actionType;
        this.actionData = actionData;
    }



    //endregion ============================= Setter ==================================
}
