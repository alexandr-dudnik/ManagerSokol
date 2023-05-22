package com.sokolua.manager.utils;

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
    public static final int LOAD_FACTOR = 5;
    public static final int KEEP_ALIVE = 120;
    public static final int INITIAL_BACK_OFF_IN_MS = 100;
    public static final long JOB_UPDATE_DATA_INTERVAL = 300; //SEC
    public static final int GET_DATA_RETRY_COUNT = 5;
    public static final int VAT_VALUE = 20;

    public static final String TEST_USERNAME = "test";
    public static final String TEST_USERPASSWORD = "test1234";

    public static final String SERVICE_ACTION_STOP = "ServiceStop";

    public static String getBaseURL(String server) {
        return API_URL.contains("%s") && !server.isEmpty() ? String.format(API_URL, server) : "";
    }

    public static String getDefaultServer() {
        return API_SERVERS.size() == 0 ? "" : API_SERVERS.get(0);
    }

    public static String getBaseURL() {
        return getBaseURL(getDefaultServer());
    }
}

