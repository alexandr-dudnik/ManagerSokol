package com.sokolua.manager.ui.screens.customer.tasks;

import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.storage.realm.DebtRealm;

public class CustomerDebtItem {
    private String headerText;
    int     debtType;
    private DebtRealm debt;
    private boolean header;

    public CustomerDebtItem(String headerText, int debtType) {
        this.header = true;
        this.debtType = debtType;
        this.headerText = headerText;
    }

    public CustomerDebtItem(DebtRealm debt){
        this.header = false;
        this.debtType = debt.isOutdated()?ConstantManager.DEBT_TYPE_OUTDATED:ConstantManager.DEBT_TYPE_NORMAL;
        this.debt = debt;
    }

    //region ================================ Getters ==================================

    public boolean isHeader() {
        return header;
    }

    public int getDebtType() {
        return debtType;
    }

    public DebtRealm getDebt() {
        return debt;
    }

    public String getHeaderText() {
        return headerText;
    }
    //endregion ============================= Getters ==================================


}
