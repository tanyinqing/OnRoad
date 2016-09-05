package com.xlw.presenter;

/**
 * Created by xinliwei on 2015/7/9.
 */
public class MenuPresenter extends BasePresenter{

    IMenuView view;     // 代表视图activity

    public void setView(IMenuView view){
        this.view = view;
    }

    public IMenuView getView(){
        return this.view;
    }

    // 跳转到视图的下一个activity
    public void gotoNextView(Class tClass){
        view.gotoNextActivity(tClass);
    }
}
