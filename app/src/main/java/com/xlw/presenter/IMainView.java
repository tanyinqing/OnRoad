package com.xlw.presenter;

/**
 * Created by xinliwei on 2015/7/5.
 *
 * MVP模式中的 View - 视图
 */
public interface IMainView {
    void showProgress();
    void hideProgress();
    void showNoInetErrorMsg();
    void moveToNextActivity();

    void showMessage(String msg);

    void showDaoTestResult(String resultS);
}
