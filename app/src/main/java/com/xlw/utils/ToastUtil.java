package com.xlw.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by xinliwei on 2015/7/9.
 */
public class ToastUtil {

    public static void showShortMsg(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    public static void showLongMsg(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
}
