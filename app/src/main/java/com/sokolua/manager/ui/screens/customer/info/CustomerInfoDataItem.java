package com.sokolua.manager.ui.screens.customer.info;

public class CustomerInfoDataItem {
    public static final int ACTION_TYPE_NO_ACTION = 0;
    public static final int ACTION_TYPE_OPEN_MAP  = 1;
    public static final int ACTION_TYPE_MAKE_CALL = 2;
    public static final int ACTION_TYPE_SEND_MAIL = 3;
    private String header;
    private String data;
    private int actionType;

    public CustomerInfoDataItem(String header, String data, int actionType) {
        this.header = header;
        this.data = data;
        this.actionType = actionType;
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


    //endregion ============================= Getters ==================================

    public String getId(){
        return this.header+"#"+this.data;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getId().equals(((CustomerInfoDataItem)obj).getId());
    }
}
