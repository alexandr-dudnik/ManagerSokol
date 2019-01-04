package com.sokolua.manager.data.network.res;

import android.support.annotation.Keep;

@Keep
public class CustomerDiscountRes {
    private int type;
    private String target_id;
    private String target_name;
    private Float percent = 0f;

    public CustomerDiscountRes(int type, String target_id, String target_name, Float percent) {
        this.type = type;
        this.target_id = target_id;
        this.target_name = target_name;
        this.percent = percent;
    }

    //region =======================  Getters  =========================

    public int getType() {
        return type;
    }

    public String getTargetId() {
        return target_id;
    }

    public String getTargetName() {
        return target_name;
    }

    public Float getPercent() {
        return percent;
    }


    //endregion ====================  Getters  =========================
}
