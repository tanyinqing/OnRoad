package com.xlw.ui.myactivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xlw.application.StatusFlag;
import com.xlw.db.FeelingDBHelper;
import com.xlw.db.LocationDBHelper;
import com.xlw.db.PhotoDBHelper;
import com.xlw.db.TripDBHelper;
import com.xlw.model.Photo;
import com.xlw.model.Feeling;
import com.xlw.model.Location;
import com.xlw.model.Trip;
import com.xlw.onroad.R;
import com.xlw.utils.DateUtil;
import com.xlw.utils.ImagesUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataActivity extends ActionBarActivity implements View.OnClickListener{
        private ImageView  imageView;
        private Button button;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        button=(Button)findViewById(R.id.button);
        button.setOnClickListener(this);
        imageView=(ImageView)findViewById(R.id.image);
        textView=(TextView)findViewById(R.id.textView);
    }

    //向数据库写入固定的模拟的数据，提供测试
    public  void write() {
        TripDBHelper tripDBHelper=new TripDBHelper();
        Trip trip=new Trip();
        trip.setCategory("国外游");
        trip.setTopic("浪漫黄山游");
        trip.setDesc("利用假期，到著名的黄山看看，放松一下行情，希望一切顺利。");
        tripDBHelper.saveTrip(trip);

        // 将照片保存在文件系统
        // 将照片保存在文件系统

        try {
            //将mipmap中的本地文件转化为图像文件
            Bitmap photoBitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.g);
            // 将照片保存在文件系统
            Uri uri=ImagesUtil.saveImage(photoBitmap);

            // 将照片uri保存到数据库中
            Photo photo=new Photo();
            photo.setUri(uri.toString());
            PhotoDBHelper photoDBHelper=new PhotoDBHelper();
            photoDBHelper.savePhoto(photo);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


        public void read(){
            Photo photo;
            PhotoDBHelper photoDBHelper=new PhotoDBHelper();
            photo=photoDBHelper.loadPhoto(1);
            String s=photo.getUri();
            Log.d("tag","图片路径读取成功"+s);
           Bitmap bitmap=ImagesUtil.loadBitmap(s.substring(7), 200, 350);
//           Bitmap bitmap=ImagesUtil.loadBitmap(this, Uri.parse(s), 300, 450);
            Log.d("tag", "图片路径读取成功" + s.substring(7));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setAdjustViewBounds(true);
          imageView.setImageBitmap(bitmap);
            Log.d("tag", "图片显示成功");

        }

    @Override
    public void onClick(View view) {
        initDBData();
    }




// 初始化数据表数据(插入一些模拟数据)
    protected void initDBData(){
        List<Long>tripIds=initTripDBData();//插入旅行线路的链表
        List<Long> locationIds=initLocationDBData(tripIds);
        initPhotoDBData(locationIds);
        initFeelingDBData(locationIds);
    }

    //插入旅行线路的链表
    private List<Long>initTripDBData(){
        List<Long>tripIds=new ArrayList<>();
        //旅行线路的数据库辅助类
        TripDBHelper tripDBHelper = new TripDBHelper();

        Trip trip01 = new Trip();
        trip01.setTopic("2015五一游");
        trip01.setDesc("2015年,五一放假,我们去旅游.");
        trip01.setStart(DateUtil.createDate(2015, 5, 1));
        trip01.setCategory("境内游");
        trip01.setStatusFlag(StatusFlag.ADD);
        long tripId01 = tripDBHelper.saveTrip(trip01);
        tripIds.add(tripId01);

        Trip trip02 = new Trip();
        trip02.setTopic("2015十一游");
        trip02.setDesc("2015年,十一放假,我们去旅游.");
        trip02.setStart(DateUtil.createDate(2015,10,1));
        trip02.setCategory("境内游");
        trip02.setStatusFlag(StatusFlag.ADD);

        long tripId02 = tripDBHelper.saveTrip(trip02);
        tripIds.add(tripId02);

        Trip trip03 = new Trip();
        trip03.setTopic("2016五一游");
        trip03.setDesc("2016年,五一放假,我们去旅游.");
        trip03.setStart(DateUtil.createDate(2016,5,1));
        trip03.setCategory("境外游");
        trip03.setStatusFlag(StatusFlag.ADD);

        long tripId03 = tripDBHelper.saveTrip(trip03);
        tripIds.add(tripId03);

        return tripIds;
    }
    private List<Long> initLocationDBData(List<Long> tripIds){
        List<Long> locationIds = new ArrayList<>();//地址ID的链表

        //地址的数据库辅助类
        LocationDBHelper locationDBHelper = new LocationDBHelper();
        for(int i = 0;i<tripIds.size();i++){
            for(int j=0;j<3;j++){
                Location location01 = new Location();
                location01.setLat(39.954300 + j*0.100 + "");
                location01.setLng(116.226000 + i*0.100 + "");
                location01.setLocDate(new Date());
                location01.setTripId(tripIds.get(i));
                location01.setStatusFlag(StatusFlag.ADD);//同步状态
                long locationId01 =locationDBHelper.saveLocation(location01);
                locationIds.add(locationId01);
            }
        }
        return locationIds;
    }
    private void initPhotoDBData(List<Long> locationIds){
        PhotoDBHelper photoDBHelper = new PhotoDBHelper();
        List<Photo> photos = new ArrayList<>();
        for(int i=0;i<locationIds.size();i++){
            for(int j=0;j<2;j++){
                Photo photo01 = new Photo();

                photo01.setUri("/sdcard/20150713" + i + j + ".jpg");
                photo01.setTakeDate(new Date());
                photo01.setTitle("title:" + i + j);
                photo01.setAbout("where:" + i + j);
                photo01.setStatusFlag(StatusFlag.ADD);
                photo01.setLocationId(locationIds.get(i));

                photos.add(photo01);
                Log.d("tag", "initPhotoDBData保存完毕，很成功");
            }
        }

        photoDBHelper.savePhotoLists(photos);
    }
    private void initFeelingDBData(List<Long> locationIds){
        FeelingDBHelper feelingDBHelper = new FeelingDBHelper();
        for(int i=0;i<locationIds.size();i++){
            Feeling feeling = new Feeling();
            feeling.setContent("我的心情很happy #" + i);
            feeling.setLocationId(locationIds.get(i));
            feeling.setStatusFlag(StatusFlag.ADD);
            feelingDBHelper.saveFeeling(feeling);
            Log.d("tag","initFeelingDBData保存完毕，很成功");
        }
}
}
