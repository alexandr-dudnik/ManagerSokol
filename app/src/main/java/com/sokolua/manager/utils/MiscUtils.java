package com.sokolua.manager.utils;

public class MiscUtils {
    private static int findMaxCommonDivider(int a, int b){
        final int small = a>=b?b:a;
        final int big = a>=b?a:b;
        final int rest = big % small;
        if (rest > 0){
            return findMaxCommonDivider(small, rest);
        }else{
            return small;
        }
    }


    public static float roundPrice(float price){
        final float mPrice= Math.round(price*100 + .499999999);
        final int mcd = AppConfig.VAT_VALUE == 0 ? 100 : findMaxCommonDivider(AppConfig.VAT_VALUE, 100);
        final float divider = 100/mcd;
        return divider * Math.round(mPrice/divider+.4999999999)/100;
    }
}
