package com.sokolua.manager.data.managers;

import com.sokolua.manager.R;
import com.sokolua.manager.utils.App;

public class ConstantManager {
    public static final int REQUEST_PERMISSION_READ_EXTERNAL_MEMORY = 3001;
    public static final String FILE_PROVIDER_AUTHORITY = "com.sokolua.manager.fileprovider";
    public static final String HEADER_LAST_MODIFIED = "Last-Modified";
    public static final String HEADER_IF_MODIFIED_SINCE = "Is-Modified-Since";
    public static final String HEADER_TOKEN = "token";

    public static final int DEBT_TYPE_WHOLE = 3;
    public static final int DEBT_TYPE_OUTDATED = 2;
    public static final int DEBT_TYPE_NORMAL = 1;
    public static final int DEBT_TYPE_NO_DEBT = 0;

    public static final int TASK_TYPE_RESEARCH = 0;
    public static final int TASK_TYPE_INDIVIDUAL = 1;

    public static final int RECYCLER_VIEW_TYPE_HEADER = 1;
    public static final int RECYCLER_VIEW_TYPE_ITEM = 0;
    public static final int RECYCLER_VIEW_TYPE_EMPTY = -1;

    public static final int MENU_ITEM_TYPE_ITEM = 0;
    public static final int MENU_ITEM_TYPE_ACTION = 1;
    public static final int MENU_ITEM_TYPE_SEARCH = 2;

    public static final int ORDER_STATUS_CART = 0;
    public static final int ORDER_STATUS_IN_PROGRESS = 1;
    public static final int ORDER_STATUS_SENT = 2;
    public static final int ORDER_STATUS_DELIVERED = 3;

    public static final int ORDER_PAYMENT_CASH = 0;
    public static final int ORDER_PAYMENT_OFFICIAL = 1;

    public static final int DISCOUNT_TYPE_ITEM = 0;
    public static final int DISCOUNT_TYPE_CATEGORY = 1;

    public static final String MAIN_CURRENCY = App.getStringRes(R.string.national_currency);

}
