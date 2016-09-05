package com.xlw.application;

import com.amap.api.maps2d.model.BitmapDescriptorFactory;

/**
 * Created by xinliwei on 2015/7/4.
 *
 * 在这个类中,存储一些系统配置,如名称、应用参数等系统级的常量或静态变量
 */
public class AppConfig {
    public static String appName = "乐途记";

    // 照片磁盘缓存路径
//    public static String CACHE_DIR = "/sdcard/images";
    public static String CACHE_DIR = "file:///mnt/sdcard/images/";

    // 网络连接常量
    // 网络测试ip.Google公共DNS-"8.8.8.8", 百度公共DNS-"180.76.76.76";
    public static final String BAIDU_DNS_IP = "180.76.76.76";
    public static final String GOOGLE_DNS_IP = "8.8.8.8";
    // 端口号.若是公共端口号,为53
    public static final int DNS_PORT = 53;

    public static final int HTTP_CONNECT_TIMEOUT = 20000;
    public static final int HTTP_REQUEST_TIMEOUT = 20000;
    public static final int HTTP_UPLOAD_TIMEOUT = 60000;

    // 高德地图URL常量
    public static final String API_KEY = "0ae0d099c74adf4793171ee1268247c4";

    // app数据库名
    public static final String DATABASE_NAME = "onroad.db";

    // 标记颜色常量
    public static final float[] MARKER_COLOR = {
            BitmapDescriptorFactory.HUE_RED,
            BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_YELLOW,
            BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_CYAN,
            BitmapDescriptorFactory.HUE_AZURE,
            BitmapDescriptorFactory.HUE_BLUE,
            BitmapDescriptorFactory.HUE_VIOLET,
            BitmapDescriptorFactory.HUE_MAGENTA,
            BitmapDescriptorFactory.HUE_ROSE
    };

    // 主题Theme
    public final static int THEME_DEFAULT = 0;
    public final static int THEME_WHITE = 1;
    public final static int THEME_BLUE = 2;
}
