package com.sokolua.manager.ui.screens.customer.tasks;

public class CustomerDebtItem {
    private String currency;
    private Float value = 0f;
    private int debtType;
    private boolean header;

    public CustomerDebtItem(String currency, Float value, int debtType) {
        this.header = false;
        this.currency = currency;
        this.debtType = debtType;
        this.value = value;
    }

    public CustomerDebtItem(String title, int debtType){
        this.header = true;
        this.debtType = debtType;
        this.currency = title;
    }

    //region ================================ Getters ==================================

    public String getCurrency() {
        return currency;
    }

    public Float getValue() {
        return value;
    }

    public boolean isHeader() {
        return header;
    }

    public int getDebtType() {
        return debtType;
    }

    //endregion ============================= Getters ==================================


}
