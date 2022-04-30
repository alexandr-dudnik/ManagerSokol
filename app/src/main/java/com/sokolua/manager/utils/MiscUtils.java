package com.sokolua.manager.utils;

public class MiscUtils {
    private static int findMaxCommonDivider(int a, int b){
        final int small = Math.min(a, b);
        final int big = Math.max(a, b);
        final int rest = big % small;
        if (rest > 0){
            return findMaxCommonDivider(small, rest);
        }else{
            return small;
        }
    }


    public static float roundPrice(float price){
        final float mPrice= Math.round(price*100*100/(100+AppConfig.VAT_VALUE) + .499999999);
        final int mcd = AppConfig.VAT_VALUE == 0 ? 100 : findMaxCommonDivider(AppConfig.VAT_VALUE, 100);
        final float divider = 100f/mcd;
        return divider * (Math.round(mPrice/divider + .4999999999)/100f) *(100+AppConfig.VAT_VALUE)/100f;
    }
}
