package com.xlw.presenter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.xlw.db.PhotoDBHelper;
import com.xlw.db.TripDBHelper;
import com.xlw.model.Photo;
import com.xlw.model.Trip;
import com.xlw.utils.ImagesUtil;

import java.io.FileNotFoundException;
import java.util.Date;

/**
 * Created by xinliwei on 2015/7/9.
 */
public class TravelNewPresenter extends BasePresenter{

    ITravelNewView view;     // 代表视图activity

    public void setView(ITravelNewView view){
        this.view = view;
    }

    public ITravelNewView getView(){
        return this.view;
    }

    // 保存旅行信息和第一张照片
    public long saveTripInfo(String topic, String desc, Bitmap photoBitmap,String category){
        view.showProgressDialog();

        // 将照片uri保存到数据库中
        Trip trip = new Trip();
        trip.setTopic(topic);
        trip.setDesc(desc);
        trip.setCategory(category);
        trip.setStart(new Date());
        TripDBHelper tripDBHelper = new TripDBHelper();
        long tripId = tripDBHelper.saveTrip(trip);

        Log.d("presenter", "保存首次旅行成功标题是   "+topic+"   描述是  "+desc+"  "+"  旅行的tripId是  "+tripId+category);


        try {
            // 将照片保存在文件系统
            Uri uri = ImagesUtil.saveImage(photoBitmap);

            // 将照片uri保存到数据库中
            Photo photo = new Photo();
            photo.setUri(uri.toString());
            photo.setTakeDate(new Date());
            photo.setLocationId(1);

            // ..... 此处构造照片的其他属性

            PhotoDBHelper photoDBHelper = new PhotoDBHelper();
            photoDBHelper.savePhoto(photo);

            Log.d("presenter", "保存首张图片成功   " + uri.toString()  +"  "+ (new Date()));


        } catch (FileNotFoundException e) {
            e.printStackTrace();

            view.showErrorMsg("照片无法保存");
        }

        view.hideProgressDialog();

        // 跳转到地图页面
        view.gotoNextActivity(tripId);
    return tripId;
    }
}
