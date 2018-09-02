package com.sokolua.manager.utils;

public class AppConfig {
    //public static final String BASE_URL = "http://"+(BuildConfig.DEBUG?"10.10.20.58":"mail.sokolua.com:8880")+"/upp_general_82/hs/api/";
    //public static final String BASE_URL = "http://mail.sokolua.com:3854/upp_general_82/hs/api/";
    public static final String BASE_URL = "http://mail.220tm.com:3854/upp_general_82/hs/api/";
    public static final int MAX_CONNECTION_TIMEOUT = 5000;
    public static final int MAX_READ_TIMEOUT = 5000;
    public static final int MAX_WRITE_TIMEOUT = 5000;
    public static final int MAX_CONCURRENT_REQUESTS = 10;

    public static final int MIN_CONSUMER_COUNT = 1;
    public static final int MAX_CONSUMER_COUNT = 3;
    public static final int LOAD_FACTOR = 3;
    public static final int KEEP_ALIVE = 120;
    public static final int INITIAL_BACK_OFF_IN_MS = 1000;
    public static final long JOB_UPDATE_DATA_INTERVAL = 30;
    public static final int GET_DATA_RETRY_COUNT = 5;
}

