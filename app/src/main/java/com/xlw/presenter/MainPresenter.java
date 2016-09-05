package com.xlw.presenter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.maps2d.model.LatLng;
import com.xlw.application.StatusFlag;
import com.xlw.db.FeelingDBHelper;
import com.xlw.db.LocationDBHelper;
import com.xlw.db.PhotoDBHelper;
import com.xlw.db.TripDBHelper;
import com.xlw.model.Feeling;
import com.xlw.model.Location;
import com.xlw.model.Photo;
import com.xlw.model.Trip;
import com.xlw.utils.ImagesUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xinliwei on 2015/7/5.
 *
 * MVP模式中的 P - presenter
 */
public class MainPresenter extends BasePresenter{

    IMainView view;     // 代表视图activity

    public void setView(IMainView view){
        this.view = view;
    }

    public IMainView getView(){
        return this.view;
    }

    @Override
    public void viewFinishLoading() {
        // 在这里调用
    }

    public void saveContent(String content,long locationId){

        // 说说保存到数据库中
        if (content!=null){
            Feeling feeling=new Feeling();
            feeling.setContent(content);
            feeling.setLocationId(locationId);
            FeelingDBHelper feelingDBHelper=new FeelingDBHelper();
            feelingDBHelper.saveFeeling(feeling);
            Log.d("presenter", "包存的说说的内容是  " + content + "   说说的locationId是    " + locationId);
            view.showMessage("保存成功");
        }else {
//            Toast.makeText(this,"您没有发表",Toast.LENGTH_SHORT).show();
            view.showMessage("您没有发表");
        }

    }

    // 保存照片地址到数据库
    public void viewsaveTripPhoto(Bitmap photoBitmap,long locationId){
        // 将照片保存在文件系统
        try {
            Uri uri= ImagesUtil.saveImage(photoBitmap);
            LocationDBHelper locationDBHelper=new LocationDBHelper();


            // 将照片uri保存到数据库中
            Photo photo=new Photo();
            photo.setUri(uri.toString());
         photo.setLocationId(locationId);

            // ..... 此处构造照片的其他属性
            PhotoDBHelper photoDBHelper=new PhotoDBHelper();
            photoDBHelper.savePhoto(photo);
            Log.d("presenter", "保存图片的内存卡地址是   " + uri.toString()  +"   保存图片的locationId是   "+ locationId);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("tag","照片无法保存");
        }
    }

    //写一个函数，它可以将坐标点的位置保存在数据库
    public void initLocationDBData(LatLng latLng,long tripId){

        LocationDBHelper locationDBHelper=new LocationDBHelper();
        Location location01 = new Location();
        //LatLng转化成经度和纬度的方法
        location01.setLat(latLng.latitude + "");
        location01.setLng(latLng.longitude + "");
        location01.setLocDate(new Date());
        location01.setStatusFlag(StatusFlag.ADD);
        location01.setTripId(tripId);
        long locationId =locationDBHelper.saveLocation(location01);
        locationDBHelper.saveLocation(location01);
        Log.d("presenter", "保存旅行地址成功经度是   " + latLng.latitude + "   纬度是  " + latLng.longitude + " 旅行ID是 " + +tripId);
//        return locationId;
    }

    //写一个函数，它可以将坐标点的位置保存在数据库
//    public void saveAdress(LatLng latLng){
//        LocationDBHelper locationDBHelper=new LocationDBHelper();
//        Location location=new Location();
//        //LatLng转化成经度和纬度的方法
//        location.setLat(latLng.latitude+"");
//        location.setLng(latLng.longitude+"");
//        location.setLocDate(new Date());
//        location.setTripId(1);
//        locationDBHelper.saveLocation(location);
//    }

    // 测试dao访问
    public void tripDaoTest(){
        Trip trip1 = new Trip();
        trip1.setTopic("夏威夷七日游");
        trip1.setDesc("公司福利,夏威夷七日游,豪华团,包吃包住包飞机包潜水,开心乐翻天!");
        trip1.setStart(new Date());
        trip1.setCategory("出境游");

        Trip trip2 = new Trip();
        trip2.setTopic("韩国济洲岛三日游");
        trip2.setDesc("公司福利,韩国济洲岛三日游,豪华团,包吃包住包飞机包泡菜,开心乐翻天!");
        trip2.setStart(new Date());
        trip2.setCategory("出境游");

        Trip trip3 = new Trip();
        trip3.setTopic("杏石口路半日游");
        trip3.setDesc("个人福利,杏石口路半日游,看看火星时代,尝尝大同削面,开心乐翻天!");
        trip3.setStart(new Date());
        trip3.setCategory("境内游");

//        TripDBHelper tripDBHelper = new TripDBHelper(this);
        TripDBHelper tripDBHelper = new TripDBHelper();
        tripDBHelper.saveTrip(trip1);
        tripDBHelper.saveTrip(trip2);
        tripDBHelper.saveTrip(trip3);

        // 回调,更新视图
        view.showDaoTestResult("ok ok");
    }
}
