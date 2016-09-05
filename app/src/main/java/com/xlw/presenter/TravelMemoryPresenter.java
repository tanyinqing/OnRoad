package com.xlw.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.xlw.db.TripDBHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;

import com.xlw.model.Trip;
import com.xlw.model.Location;
import com.xlw.model.Photo;
import com.xlw.ui.activity.MenuActivity;
import com.xlw.utils.DateUtil;
import com.xlw.utils.ImagesUtil;

/**
 * Created by xinliwei on 2015/7/9.
 */
public class TravelMemoryPresenter extends BasePresenter {

    ITravelMemoryView view;
    public String firstPhotoUri;

    public void setView(ITravelMemoryView view){
        this.view = view;
    }

    public ITravelMemoryView getView(){
        return this.view;
    }

    // 跳转到视图的下一个activity
    public void gotoNextView(Class tClass){
        view.gotoNextActivity(tClass);
    }
    public void fetchTripData(){
        // 由两部分组成: header和section
        LinkedHashMap<String,List<TripAndPhotos>> tripSectionsMap = new LinkedHashMap<>();

        TripDBHelper tripDBHelper = new TripDBHelper();

        // 查询出所有的旅行信息
        List<Trip> trips = tripDBHelper.loadAllTrip();
       if (trips.size()==0){
           Toast.makeText((Context) view,"请先规划旅行路线",Toast.LENGTH_SHORT).show();
          return;
       }
        ListIterator<Trip> iterator = trips.listIterator(trips.size());//旅行信息的数量
        Log.d("tag", "旅行信息的数量" + trips.size());
        while (iterator.hasPrevious()){
            Trip trip = iterator.previous();

            String header = DateUtil.parserYear(trip.getStart()) + "年";
            // 如果是新的年份
            if(!tripSectionsMap.containsKey(header)){
                List<TripAndPhotos> tripAndPhotosesList = new ArrayList<>();
                tripSectionsMap.put(header,tripAndPhotosesList);
            }

            Location location = trip.getLocations().get(0);
//            Log.d("tag", "旅行信息的数量location" +location.getId());
            List<Photo> photos = location.getPhotos();
            Log.d("tag", "旅行信息的数量旅行次数" +trip.getId());
            Log.d("tag", "旅行信息的数量对应的地点    " +location.getId());
            Log.d("tag", "旅行信息的数量photos.size()    " +photos.size());
            if (null!=photos&&photos.size()!=0) { 
                firstPhotoUri = photos.get(0).getUri();
            }
            Log.d("tag", "旅行信息的数量firstPhotoUri    " +firstPhotoUri);
            Bitmap photo = ImagesUtil.loadBitmap(firstPhotoUri.substring(7) , 150, 200);
            Log.d("tag", "照片读取的真实路径    " +firstPhotoUri.substring(7));
            TripAndPhotos tripAndPhotos = new TripAndPhotos();
            tripAndPhotos.trip = trip;
            tripAndPhotos.photos = photos;
            tripAndPhotos.bitmap = photo;

            tripSectionsMap.get(header).add(tripAndPhotos);

        }

        view.updateAdapterData(tripSectionsMap);
    }

    @Override
    public void viewFinishLoading() {
        fetchTripData();
    }

    public class TripAndPhotos{
        public Trip trip;
        public List<Photo> photos;
        public Bitmap bitmap;
    }
}
