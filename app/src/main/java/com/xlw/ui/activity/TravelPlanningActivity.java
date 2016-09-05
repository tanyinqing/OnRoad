package com.xlw.ui.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;


import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;
import com.xlw.onroad.R;
import com.xlw.presenter.ITravelPlanningView;
import com.xlw.presenter.TravelMemoryPresenter;
import com.xlw.presenter.TravelPlanningPresenter;
import com.xlw.utils.ToastUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class TravelPlanningActivity extends BaseActivity implements ITravelPlanningView,AMap.OnMapScreenShotListener {

    TravelPlanningPresenter travelPlanningPresenter;

    private MapView mapView;
    private AMap aMap;
    Marker marker;

    List<LatLng> points = new ArrayList<>(); // 保存绘制坐标点的数组
    boolean isSelected = false; // 标识当前状态:选择坐标点,还是绘图
    // 标记颜色(也可自定义颜色数组)
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
        setContentView(R.layout.activity_travel_planning);

        ActionBar actionBar = getSupportActionBar();
       // 使用up 箭头指示器显示home
        actionBar.setDisplayHomeAsUpEnabled(true);
        // 设置title 文本
        actionBar.setTitle("乐途记");

        travelPlanningPresenter = new TravelPlanningPresenter();
        travelPlanningPresenter.setView(this);
        mapView = (MapView)findViewById(R.id.map1);
        mapView.onCreate(savedInstanceState);
        init();

        addMapListener(); // 向地图添加单击事件监听器
        setUpMap();

    }

    /**
     * 对地图进行截屏
     * @param v
     */
    public void getMapScreenShot(MenuItem v) {
        aMap.getMapScreenShot(this);
        aMap.invalidate();// 刷新地图
    }
    @Override
    public void onMapScreenShot(Bitmap bitmap) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        if(null == bitmap){
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(
                    Environment.getExternalStorageDirectory() + "/test_"
                            + sdf.format(new Date()) + ".png");
            Log.d("tag",sdf.format(new Date()) + ".png");
            Log.d("tag", Environment.getExternalStorageDirectory() + "/test_");
            boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            try {
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (b)
                Toast.makeText(this,"截屏成功",Toast.LENGTH_SHORT).show();
//                ToastUtil.show(ScreenShotActivity.this, "截屏成功");
            else {
//                ToastUtil.show(ScreenShotActivity.this, "截屏失败");
                Toast.makeText(this,"截屏失败",Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    // 按钮事件响应函数
    public void onClick(View view){
        int id = view.getId();
        switch (id){
//            case R.id.btn_latlng:
//// 选择经纬度
//                selectLatLng();
//                break;

//            case R.id.btn_draw:
// 绘制选择的折线
//                drawPolyline();
//                break;
        }
    }
//    // 选择经纬度
//    private void selectLatLng(){
//        if(points != null){
//            points.clear(); // 先清空坐标数组
//        }
//        isSelected = true; // 将状态设为"选择坐标"状态
//        findViewById(R.id.btn_draw).setEnabled(true); // 启用绘图按钮
//    }

    private void setUpMap() {
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
//                if (marker!=null){
                    points.add(latLng);
                    addMarksToMap(points);
//                }

            }
        });
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.remove();
                points.remove(marker.getPosition());
                return false;
            }
        });
    }
    // 绘制折线
    private void drawPolyline(){
//        isSelected = false; // 将状态设为"绘制折线"状态
        if(points.size()>0){
// 设置绘制参数
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.addAll(points); // 添加要绘制的坐标数组
            polylineOptions.width(15); // 设置线的宽度
            polylineOptions.color(Color.rgb(255, 120, 60)); // 颜色
// 将折线绘制到地图上
            Polyline polyline = aMap.addPolyline(polylineOptions);
// 添加标记
//            addMarksToMap(points);
            points.clear(); // 清空坐标点数组
//            findViewById(R.id.btn_draw).setEnabled(false); // 禁用绘制折线按钮
        }
    }
    // 绘制一组标记
    private void addMarksToMap(List<LatLng> points){
        for(int i=0;i<points.size();i++){
            if(i == 0 || i == points.size()-1){
                addMarkToMap(points.get(i),MARKER_COLOR[0]);
            }else if(i>=points.size()){
                addMarkToMap(points.get(i),MARKER_COLOR[points.size()-1]);
            }else{
                addMarkToMap(points.get(i),MARKER_COLOR[i]);
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
    private void addMapListener() {
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

//                if (isSelected) {
//// 如果当前是处理"选择坐标"状态,则响应单击事件
//// 将选择的坐标点添加到数组中
//
//                }


            }
        });
    }
    /**
     * 初始化AMap 对象
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
        // 通知主导器,本view已经加载显示完毕
        travelPlanningPresenter.viewFinishLoading();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_turist_planning, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case android.R.id.home: // 如果单击的是Home 按钮
                Toast.makeText(this,"返回菜单",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(this,MenuActivity.class);
                startActivity(intent);
                break;
//            case R.id.action_settings: // 如果单击的是Home 按钮
//                Toast.makeText(this,"action_settings",Toast.LENGTH_SHORT).show();
//                selectLatLng();
//                break;
            case R.id.SaveBitmap: // 如果单击的是Home 按钮
                Toast.makeText(this,"规划路线",Toast.LENGTH_SHORT).show();
                drawPolyline();
                break;
            case R.id.action_settings: // 如果单击的是Home 按钮
                Toast.makeText(this,"截屏",Toast.LENGTH_SHORT).show();
                aMap.getMapScreenShot(this);
                aMap.invalidate();// 刷新地图
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void gotoNextActivity(Class tClass) {

    }


}
