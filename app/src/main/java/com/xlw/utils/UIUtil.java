package com.xlw.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.xlw.application.AppConfig;
import com.xlw.application.XlwApplication;
import com.xlw.onroad.R;

/**
 * Created by xinliwei on 2015/7/9.
 */
public class UIUtil {

    // 将dip转为px像素单位
    public int dip2px(int dip){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics.setToDefaults();
        // 根据屏幕分辨率将dp转换成像素点
        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dip,displayMetrics);
        return pixel;
    }

    /**
     * 运行时动态改变面Activity的theme,并通过创建一个新的同类型的Activity来重启它
     */
    public static void changeToTheme(Activity activity, int theme){
        XlwApplication.getInstance().currentTheme = theme;
        activity.finish();

        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    /** 根据全局配置来设置Activity的theme. */
    public static void onActivityCreateSetTheme(Activity activity){
        Log.i("theme", XlwApplication.getInstance().currentTheme + "");
        switch (XlwApplication.getInstance().currentTheme){
            default:
            case AppConfig.THEME_DEFAULT:
                activity.setTheme(R.style.AppTheme);
                break;
            case AppConfig.THEME_WHITE:
                activity.setTheme(R.style.myAppTheme);
                break;
            case AppConfig.THEME_BLUE:
                activity.setTheme(R.style.myAppTheme2);
                break;
        }
    }

    // 动态获取theme的颜色
    public static int[] getThemeColor(Activity activity){
        TypedArray array = activity.getTheme().obtainStyledAttributes(new int[] {
                android.R.attr.colorBackground,
                android.R.attr.textColorPrimary,
        });
        int backgroundColor = array.getColor(0, 0xFF00FF);
        int textColor = array.getColor(1, 0xFF00FF);
        array.recycle();

        int[] colors = new int[2];
        colors[0] = backgroundColor;
        colors[1] = textColor;
        return colors;
    }

    public static void setThemeColor(Activity activity, int[] colors){
        TypedArray array = activity.getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.colorBackground,
                android.R.attr.textColorPrimary,
        });

//        XlwApplication.getInstance().currentTheme
    }
}
