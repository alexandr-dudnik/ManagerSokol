package com.sokolua.manager.utils;

public class AppConfig {
    public static final String API_URL = "http://%s/upp_general_82/hs/api/";
    public static final String[] API_SERVERS = {"post.sokolua.com:3854", "mail.220tm.com:3854", "10.10.20.103:8080"};
    public static final String BASE_URL = String.format(API_URL, API_SERVERS[0]);
    public static final int MAX_CONNECTION_TIMEOUT = 200;
    public static final int MAX_READ_TIMEOUT = 10000;
    public static final int MAX_WRITE_TIMEOUT = 10000;
    public static final int MAX_CONCURRENT_REQUESTS = 10;

    public static final int MIN_CONSUMER_COUNT = 1;
    public static final int MAX_CONSUMER_COUNT = 5;
    public static final int LOAD_FACTOR = 2;
    public static final int KEEP_ALIVE = 120;
    public static final int INITIAL_BACK_OFF_IN_MS = 100;
    public static final long JOB_UPDATE_DATA_INTERVAL = 300; //SEC
    public static final long JOB_TIMEOUT = 1000 * 60 * 5;
    public static final int GET_DATA_RETRY_COUNT = 5;
    public static final int VAT_VALUE = 20;

    public static final String TEST_USERNAME = "test";
    public static final String TEST_USERPASSWORD = "test1234";

    public static final String SERVICE_ACTION_STOP = "ServiceStop";
}

