package com.xlw.ui.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.xlw.db.LocationDBHelper;
import com.xlw.model.*;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.xlw.db.TripDBHelper;
import com.xlw.model.Location;
import com.xlw.onroad.R;

import java.util.ArrayList;
import java.util.List;

public class EightActivity extends ActionBarActivity {
    long tripId;
    private MapView mapView;
    private AMap aMap;
    List<LatLng> points = new ArrayList<>(); // 保存绘制坐标点的数组
    boolean isSelected = false; // 标识当前状态:选择坐标点,还是绘图

    private int initZoomLevel = 19; // 初始地图缩放级别
    LatLng currentPosition = new LatLng(39.95435,116.226375); // 当前位置
    String address; // 当前地理位置
    Marker marker; // 添加到地图上的标记
    AMap.InfoWindowAdapter infoWindowAdapter;

    final float[] MARKER_COLOR = {
            BitmapDescriptorFactory.HUE_RED,
            BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_YELLOW,
            BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_CYAN,
            BitmapDescriptorFactory.HUE_AZURE,
            BitmapDescriptorFactory.HUE_BLUE,
            BitmapDescriptorFactory.HUE_VIOLET,
            BitmapDescriptorFactory.HUE_MAGENTA,
            BitmapDescriptorFactory.HUE_ROSE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eight);

        gettripId();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
// 使用up 箭头指示器显示home
        actionBar.setDisplayHomeAsUpEnabled(true);
// 设置title 文本
        actionBar.setTitle("乐途记");


        mapView = (MapView)findViewById(R.id.map1);
        mapView.onCreate(savedInstanceState);
        init();
        getDate();
        addMarksToMap(points);
        drawPolyline();
        addMapListener(); // 向地图添加单击事件监听器
    }



/**
 * 绘制折线图用到的方法
 */
        // 按钮事件响应函数
        public void onClick(View view){
//    int id = view.getId();
//    switch (id){
//        case R.id.btn_latlng:
//        // 选择经纬度
//            selectLatLng();
//            break;
//
//        case R.id.btn_draw:
//        // 绘制选择的折线
//            drawPolyline();
//            break;
//    }
}
//        // 选择经纬度
//        private void selectLatLng(){
//    if(points != null){
//        points.clear(); // 先清空坐标数组
//    }
//    isSelected = true; // 将状态设为"选择坐标"状态
//    findViewById(R.id.btn_draw).setEnabled(true); // 启用绘图按钮
//}
        // 绘制折线
        private void drawPolyline(){
//    isSelected = false; // 将状态设为"绘制折线"状态
    if(points.size()>0){
        // 设置绘制参数
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(points); // 添加要绘制的坐标数组
        polylineOptions.width(15); // 设置线的宽度
        polylineOptions.color(Color.rgb(255, 120, 60)); // 颜色
        Log.d("tag", "看绘制折线能不能执行  " );
        // 将折线绘制到地图上  这个是关键性的操作
        Polyline polyline = aMap.addPolyline(polylineOptions);
//        // 添加标记
//        addMarksToMap( points);
//        points.clear(); // 清空坐标点数组
//        findViewById(R.id.btn_draw).setEnabled(false); // 禁用绘制折线按钮
    }
        }
        // 绘制一组标记
        private void addMarksToMap(List<LatLng> points){
            for(int i=0;i<points.size();i++){
        if(i == 0 || i == points.size()-1){
            addMarkToMap(points.get(i),MARKER_COLOR[0]);
            Log.d("tag", "绘制一组标记1  " + points.get(i).toString());
        }else if(i>=points.size()){
            addMarkToMap(points.get(i),MARKER_COLOR[points.size()-1]);
            Log.d("tag", "绘制一组标记2    " + points.get(i).toString());
        }else{
            addMarkToMap(points.get(i),MARKER_COLOR[i]);
            Log.d("tag", "绘制一组标记3   " + points.get(i).toString());
        }
    }
        }
        // 绘制一个标记,使用指定颜色的气泡
        private void addMarkToMap(LatLng point, float markerColor){
            aMap.addMarker(new MarkerOptions()
                    .position(point)
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                    .draggable(false)
    );
        }
        // 添加监听器
        private void addMapListener(){
            aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (isSelected) {
                        // 如果当前是处理"选择坐标"状态,则响应单击事件
                        // 将选择的坐标点添加到数组中
//                        points.add(latLng);

                    }
                }
            });
        }



    /**
     * 数据库相关的方法
     *
     *
     */
    //从数据库获得tripId
    private void gettripId(){
        Intent intent = this.getIntent();        //获取已有的intent对象
        Bundle bundle = intent.getExtras();    //获取intent里面的bundle对象
        tripId = bundle.getLong("tripId");//获取Bundle里面的字符串

    }
    public void getDate(){
        if(points != null){
            points.clear(); // 先清空坐标数组
        }
        TripDBHelper tripDBHelper=new TripDBHelper();
        Trip  trip=tripDBHelper.loadTrip(tripId);

        List<Location> locations = trip.getLocations();
        for (Location location:locations){
            LatLng latLng = new LatLng(Double.parseDouble(location.getLat()),Double.parseDouble(location.getLng()));
            points.add(latLng);
            Log.d("tag","本次旅行的坐标是"+latLng.toString());
        }
        Log.d("tag   points", String.valueOf(points.size()));

    }



    /**
     * 方法必须重写
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_eight, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//
//            return true;
//        }
        switch (id){
            case android.R.id.home: // 如果单击的是Home 按钮
                Toast.makeText(this,"返回上一页",Toast.LENGTH_SHORT).show();
                        Intent intent=new  Intent(this,MenuActivity.class);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
