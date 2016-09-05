package com.xlw.presenter;

import android.graphics.Bitmap;

/**
 * Created by xinliwei on 2015/7/9.
 */
public interface ITravelNewView {

    // 显示拍照的照片
    void displayPhoto(Bitmap bitmap);

    // 跳转到下一个Activity
    void gotoNextActivity(long s);

    // 显示进度条
    void showProgressDialog();

    // 取消进度条
    void hideProgressDialog();

    // 显示错误消息
    void showErrorMsg(String errorMsg);
}
