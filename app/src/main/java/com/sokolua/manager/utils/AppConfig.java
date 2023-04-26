package com.sokolua.manager.utils;

import com.sokolua.manager.data.managers.ConstantManager;

import java.util.ArrayList;
import java.util.List;

public class AppConfig {
    public static String API_URL = "";
    public static List<String> API_SERVERS = new ArrayList<>();
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

    private static String getApiUrl() {
        return API_URL.isEmpty() ? "http://%s" : API_URL;
    }
    public static String getBaseURL() {
        return String.format(getApiUrl(), getDefaultServer());
    }

    public static String getBaseURL(String server) {
        return String.format(getApiUrl(), server);
    }

    public static String getDefaultServer() {
        return API_SERVERS.size() == 0 ? ConstantManager.LOCAL_HOST : API_SERVERS.get(0);
    }
}

