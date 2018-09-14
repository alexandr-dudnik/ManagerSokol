package com.sokolua.manager.data.network.res;

import android.support.annotation.Keep;

@Keep
public class CustomerDiscountRes {
    public int type;
    public String target_id;
    public String target_name;
    public Float percent = 0f;

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


    //region =======================  Setters  =========================

    public void setType(int type) {
        this.type = type;
    }

    public void setTarget_id(String target_id) {
        this.target_id = target_id;
    }

    public void setTarget_name(String target_name) {
        this.target_name = target_name;
    }

    public void setPercent(Float percent) {
        this.percent = percent;
    }


    //endregion ====================  Setters  =========================
}
