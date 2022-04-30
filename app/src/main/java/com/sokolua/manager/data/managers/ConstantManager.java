package com.sokolua.manager.data.managers;

import androidx.annotation.Keep;

import com.sokolua.manager.R;
import com.sokolua.manager.utils.App;

@Keep
public class ConstantManager {
    public static final int UPDATE_REQUEST_CODE = 556677;

    public static final int REQUEST_PERMISSION_READ_EXTERNAL_MEMORY = 3001;
    public static final int REQUEST_PERMISSION_USE_CAMERA = 3002;
    public static final int REQUEST_CHECK_SETTINGS = 3003;

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
    public static final int ORDER_PAYMENT_FOP = 2;

    public static final int DISCOUNT_TYPE_ITEM = 0;
    public static final int DISCOUNT_TYPE_CATEGORY = 1;

    public static final String PRICE_BASE_PRICE_ID = "BASE";
    public static final String PRICE_LOW_PRICE_ID = "MIN";

    public static final String MAIN_CURRENCY = App.getStringRes(R.string.national_currency);
    public static final String MAIN_CURRENCY_CODE = "980";

    public static final String STATE_GOODS_ORDER_KEY = "CART_ORDER";
    public static final String STATE_GOODS_CATEGORY_KEY = "CART_CATEGORY";
    public static final String STATE_GOODS_PRICE_KEY = "CART_PRICE";
    public static final String STATE_GOODS_TRADE_KEY = "CART_TRADE";
    public static final String STATE_GOODS_CURRENCY_KEY = "CART_CURRENCY";

    public static final String UPDATE_JOB_TAG = App.getContext().getPackageName() + ".update_job";

    public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    public static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
}
