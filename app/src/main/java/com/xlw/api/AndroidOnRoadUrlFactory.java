package com.xlw.api;

/**
 * Created by xinliwei on 2015/7/8.
 *
 * 工具类,获得各种所需要的Url
 */
public class AndroidOnRoadUrlFactory {

    public static String URL_BASE = "http://192.168.0.15:8080/mars/";
    public static final String LOGIN = "login/";
    public static final String LOGOUT = "logout/";

    public static final String TRIP = "todo/";
    public static final String TRIP_ADD = "add/%s";
    public static final String TRIP_DELETE = "del/%d";

    public static final String ACCEPT = "Accept";
    public static final String POST_METHOD = "POST";
    public static final String GET_METHOD = "GET";
    public static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";

    // 单例模式
    private static AndroidOnRoadUrlFactory instance = null;

    private AndroidOnRoadUrlFactory() {
    }

    public static AndroidOnRoadUrlFactory getInstance() {
        if (instance == null) {
            instance = new AndroidOnRoadUrlFactory();
        }

        return instance;
    }

    public String getLoginUrl() {
        return URL_BASE + LOGIN;
    }

    public String getLoginUrlFmt() {
        return getLoginUrl() + "?username=%s&password=%s";
    }

    public String getLogoutUrl() {
        return URL_BASE + LOGOUT;
    }

    public String getTripUrl() {
        return URL_BASE + TRIP;
    }

    public String getTripAddUrlFmt() {
        return URL_BASE + TRIP + TRIP_ADD;
    }

    public String getTripDeleteUrlFmt() {
        return URL_BASE + TRIP + TRIP_DELETE;
    }
}
