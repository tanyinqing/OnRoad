package com.xlw.presenter;

import android.graphics.Bitmap;
import com.xlw.model.Photo;
import com.xlw.model.Trip;

import java.util.List;

/**
 * Created by xinliwei on 2015/7/9.
 */
public class TravelPlanningPresenter extends BasePresenter{

    ITravelPlanningView view;

    public void setView(ITravelPlanningView view){
        this.view = view;
    }

    public ITravelPlanningView getView(){
        return this.view;
    }

    // 跳转到视图的下一个activity
    public void gotoNextView(Class tClass){
        view.gotoNextActivity(tClass);
    }
    public class TripAndPhotos{
        public Trip trip;
        public List<Photo> photos;
        public Bitmap bitmap;
    }
}
