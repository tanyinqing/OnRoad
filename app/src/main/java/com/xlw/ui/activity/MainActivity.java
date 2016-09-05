package com.xlw.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
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
import com.xlw.db.FeelingDBHelper;
import com.xlw.db.LocationDBHelper;
import com.xlw.db.PhotoDBHelper;
import com.xlw.model.Feeling;
import com.xlw.model.Location;
import com.xlw.model.Photo;
import com.xlw.onroad.R;
import com.xlw.presenter.IMainView;
import com.xlw.presenter.MainPresenter;
import com.xlw.utils.ImagesUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
当前坐标:
    经度: 116.226536
    纬度: 39.954314
 */
public class MainActivity extends AppCompatActivity implements IMainView {
    long tripId;

    EditText content;

    private MainPresenter presenter;    // 主导器 - MVP模式中的P

    private MapView mapView ;
    private AMap aMap;

    Bitmap photoBitmap;

    long locationgId;

//    private Button button;
    final Context context=this;

    private int initZoomLevel = 19; // 初始地图缩放级别
    LatLng currentPosition = new LatLng(39.95435,116.226375); // 当前位置
    String address; // 当前地理位置
    LatLng currentLocation=null;
//    LatLng latLngpaizhao=null;

    Marker marker; // 添加到地图上的标记
    AMap.InfoWindowAdapter infoWindowAdapter;

    List<LatLng> points=new ArrayList<>();// 保存绘制坐标点的数组
    boolean isSelected=false;// 标识当前状态:false选择坐标点,true还是绘图

    boolean isADD=false;//标识当前的状态:fakse表示坐标点写入数据库，并加入折线的数组
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       gettripId();//得到gettripId()的编号

        //一定选V7架包下的ActionBar
        ActionBar actionBar=getSupportActionBar();
        // 使用up 箭头指示器显示home
        actionBar.setDisplayHomeAsUpEnabled(true);
        // 设置title 文本
        actionBar.setTitle("乐途记");

        mapView = (MapView)findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        initMap();
        initPresent();



//        addListener();
        setUpMap();
    }
    private void gettripId(){
        Intent intent = this.getIntent();        //获取已有的intent对象
        Bundle bundle = intent.getExtras();    //获取intent里面的bundle对象
        tripId = bundle.getLong("tripId");//获取Bundle里面的字符串
        Log.d("presenter", "查看接受的tripId是否准确   "+tripId);

    }

    //自定义infowinfow 窗口
    public void render(final Marker marker, View view) {
        // 填充图像部分

        ImageView infoImage = (ImageView)view.findViewById(R.id.info_image);
        infoImage.setImageResource(R.mipmap.ic_launcher);
        // 填充标题部分
        String title = marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(R.id.info_title));
        if (title != null) {
        // 使用SpannableString 给TextView 加上特殊的文本效果
            SpannableString titleText = new SpannableString(title);
        // 第一个参数为需要设定的样式,第二个参数为开始的字符位置,第三个参数是结束的位置
            titleText.setSpan(new ForegroundColorSpan(Color.BLUE), 0,
                    titleText.length(), 0);
            titleUi.setTextSize(14);
            titleUi.setText(titleText);
        } else {
            titleUi.setText("");
        }
        // 填充标记内容部分
        String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.info_snippet));
        if (snippet != null) {
            SpannableString snippetText = new SpannableString(snippet);
            snippetText.setSpan(new ForegroundColorSpan(Color.rgb(100,200,100)), 0,
                    snippetText.length(), 0);
            snippetUi.setTextSize(12);
            snippetUi.setText(snippetText);
        } else {
            snippetUi.setText("空的");
        }
        // 填充城市记内容部分
        String city = marker.getPosition().latitude + "," + marker.getPosition().longitude;
        TextView cityUi = ((TextView) view.findViewById(R.id.info_city));
        if (city != null) {
            SpannableString cityText = new SpannableString(city);
            cityText.setSpan(new ForegroundColorSpan(Color.GREEN), 0,
                    cityText.length(), 0);
            cityUi.setTextSize(10);
            cityUi.setText(cityText);
        } else {
            cityUi.setText("");
        }
        // 为按钮添加事件- 拍照按钮
        ImageButton photoButton = (ImageButton)view.findViewById(R.id.btn_photo);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),"开始拍照",Toast.LENGTH_SHORT).show();
                 LatLng latLng=marker.getPosition();
                selectId(latLng);
                paizhao();
            }
        });
        // 为按钮添加事件- 查看详细信息按钮
        ImageButton detailButton = (ImageButton)view.findViewById(R.id.btn_detail);
        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),
                        "可以转向详细信息窗口查看",Toast.LENGTH_SHORT).show();
                LatLng latLng=marker.getPosition();
                selectId(latLng);
//                shouDialog2();
            }
        });
        // 为按钮添加事件- 编辑"说说"按钮
    ImageButton editButton = (ImageButton)view.findViewById(R.id.btn_edit);
    editButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getBaseContext(),
                    "发表说说",Toast.LENGTH_SHORT).show();
            LatLng latLng=marker.getPosition();
            selectId(latLng);
            showDialog();
         }
         });
        }

//        //可以转向详细信息窗口查看
//    public void shouDialog2(){
//        // 自定义对话框
//        final Dialog dialog=new Dialog(context);
//        dialog.setContentView(R.layout.dialog2);
//        dialog.setTitle("乐途记");
//
//        // 设置自定义对话框组件-文本、图像和按钮
//        Button button=(Button)dialog.findViewById(R.id.photoCheck);
//        Button button2=(Button)dialog.findViewById(R.id.shuoCheck);
//
//        // 如果单击了按钮，则关闭自定义对话框
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                presenter.saveContent(s1,locationgId);
//
//                dialog.dismiss();
//
//            }
//        });
//        // 如果单击了按钮，则关闭自定义对话框
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                presenter.saveContent(s1,locationgId);
//
//                dialog.dismiss();
//
//            }
//        });
//        dialog.show();
//    }

    /**
     * 当前编辑的方法
     */
    //    // 按钮事件响应函数
//    public void onClick(View view){
//        int id = view.getId();
//        switch (id){
//
//            case R.id.btn_draw:
//// 绘制选择的折线
//                drawPolyline();
//                break;
//        }
//    }
    // 选择经纬度
    private void selectLatLng(){

        if (points!=null){
            points.clear();// 先清空坐标数组
        }
        isSelected=true;// 将状态设为"选择坐标"状态
//        findViewById(R.id.btn_draw).setEnabled(true);// 启用绘图按钮
    }
    // 绘制折线
    private void drawPolyline(){
        isSelected=false;// 将状态设为"绘制折线"状态
        // 设置绘制参数
        if (points.size()>0){
            PolylineOptions polylineOptions=new PolylineOptions();
            polylineOptions.addAll(points);  // 添加要绘制的坐标数组
            polylineOptions.width(15);  // 设置线的宽度
            polylineOptions.color(Color.rgb(255,120,60));  // 颜色

            // 将折线绘制到地图上
            Polyline polyline=aMap.addPolyline(polylineOptions);

            // 添加标记
//            addMarkersToMap(points);

            points.clear();// 清空坐标点数组

//         findViewById(R.id.btn_draw).setEnabled(false);// 禁用绘制折线按钮
        }
    }
    // 绘制一组标记
    private void addMarkersToMap(List<LatLng>points){
        for (int i=0;i<points.size();i++){
            if (i==0||i==points.size()-1){
                addMarkerToMap(points.get(i));
            }else if(i>=points.size()){
                addMarkerToMap(points.get(i));
            }else{
                addMarkerToMap(points.get(i));
            }

        }
    }
    //设置地图监听器，监听点击事件,只要有点击，都可以监听到
    private void setUpMap() {
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // 如果当前是处理"选择坐标"状态,则响应单击事件
                // 将选择的坐标点添加到数组中
                if (isSelected){
                    // 添加标记
                    Log.d("tag", "isSelected变量" + String.valueOf(isSelected));
//                    addMarkersToMap(points);
//                    currentLocation=latLng;
                    //在地图上点击的地方添加标志
                    addMarkerToMap(latLng);//只有规划路线被选中的状态下，才可以再地图上汇标志
                    points.add(latLng);
                 presenter.initLocationDBData(latLng, tripId);
                    Log.d("tag", "保存的地理坐标值" + latLng.toString());
                    Log.d("tag", "保存的地理坐标集合" + points.toString());

                } else {

                    // 点击非marker 区域，将显示的InfoWindow 隐藏
                    if (marker != null) {
                        marker.hideInfoWindow();

                    }
                }
                // 如果当前是处理"选择坐标"状态,则响应单击事件
                // 将选择的坐标点添加到数组中
//                    if (isADD){
//                        Log.d("tag", String.valueOf(isADD));
//                points.add(latLng);
//                saveAdress(latLng);
//                Log.d("tag","保存的地理坐标值"+latLng.toString());
//                    }

            }
        });
        // 设置点击infoWindow 事件监听器  监听该窗口上的点击事件
        aMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Toast.makeText(getBaseContext(),
                        marker.getTitle() + marker.getSnippet(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // 设置自定义InfoWindow 样式，加载该窗口的布局数据和样式
        infoWindowAdapter = new AMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
                render(marker, infoWindow);
                return infoWindow;
            }
            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        };

        // 设置aMap 加载成功事件监听器，实现地图与弹出窗口的关联，点击后
        //弹出自定义的弹出窗口
        aMap.setInfoWindowAdapter(infoWindowAdapter);

        // 设置aMap 加载成功事件监听器   暂时没有实现什么功能
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
                    @Override
                    public void onMapLoaded() {
                        // 设置缩放比例
                        aMap.moveCamera(CameraUpdateFactory.zoomTo(initZoomLevel));
                        // 设置中心点
                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(currentPosition));
                        // ...根据自己的需要在这里写代码
                    }
                });
            }
        });
    }
    private void moveToPoint(LatLng point){
        // 设置中心点
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(point));
        // 设置缩放级别
        aMap.animateCamera(CameraUpdateFactory.zoomTo(initZoomLevel));
    }
    //为按钮设置监听器
//        public void addListener(){
//        // 当单击按钮时,添加标记
//            Button btnMarkerCurrent = (Button)findViewById(R.id.btn_marker_current);
//            btnMarkerCurrent.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    currentPosition = new LatLng(39.95435, 116.226375); // 设为火星时代地址
//                    getAddress(currentPosition);
//                }
//            });
//        }

    //通过经纬度查询locationgId
    public void selectId(LatLng latlng){
        String lat= String.valueOf(latlng.latitude);
        String lng=String.valueOf(latlng.longitude);
        LocationDBHelper locationDBHelper=new LocationDBHelper();
     List<Location> location=locationDBHelper.queryLocation("where lat=? and lng=?",lat,lng);
        locationgId=location.get(0).getId();
        Log.d("tag", "查询到的locationgId是" + locationgId);
    }
    // 得到返回的照片   并把照片保存到数据库
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle=data.getExtras();
        if (bundle!=null){
            photoBitmap=(Bitmap)bundle.get("data");
            displayPhoto(photoBitmap);// 显示照片
        presenter.viewsaveTripPhoto(photoBitmap, locationgId);// 保存照片
            Toast.makeText(this,"照片保存成功",Toast.LENGTH_SHORT).show();
        }
    }
    // 拍照-旅行
    private void paizhao(){
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }
    //反向地理编码 要进行反向编码的经纬度
    private void getAddress(LatLng latLon){
        // 根据经纬度反向编码地址
        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        // 反向地理编码回调方法
                if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null
                        && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {
                    address = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                    if(address == null){
                        address = "中国";
                    }
                    moveToPoint(currentPosition); // 将中心点移到这里
                    addMarkerToMap(currentPosition);// 在这里添加标记
                }
            }
            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        // 正向地理编码回调方法
            }
        });
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS 原生 坐标系
        LatLonPoint latLonPoint = new LatLonPoint(latLon.latitude,latLon.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query); // 异步查询
    }
    //     * 自定义marker 图标.实现原理:MarkerOptions 有一个方法icon(BitmapDescriptorFactory.fromBitmap())，
    //     * 里面的参数是Bitmap，可以在Bitmap 中进行绘制文字，间接地在marker 上面显示文字。
    //     * @param text 要绘制的文件
    public Bitmap getMarkerBitMap(String text) {
        Bitmap markerBitmap = BitmapDescriptorFactory.defaultMarker()
                .getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        Bitmap bitmap = Bitmap.createBitmap(markerBitmap, 0, 0,
                markerBitmap.getWidth(), markerBitmap.getHeight());
        // TextPaint 是Paint 的子类
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(20f);
        textPaint.setColor(Color.RED);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text, 5, 35, textPaint); // 设置bitmap 上面的文字位置
        return bitmap;
    }
    //在地图上添加自定义的marker
    private void addMarkerToMap(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("我在这里");
        markerOptions.snippet(address);
        // markerOptions.icon(BitmapDescriptorFactory
        // .defaultMarker(BitmapDescriptorFactory.HUE_RED));
        // 改为使用自定义的图标
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitMap("mars")));
        markerOptions.draggable(false);
        marker = aMap.addMarker(markerOptions);
        marker.setObject("1001"); // 这里可以存储用户数据,例如id
        // marker.setRotateAngle(90); // 设置marker 旋转90 度
//        marker.showInfoWindow(); // 设置默认显示一个infowinfow,去掉
    }
    //发表说说的对话框
    public void showDialog(){
        // 自定义对话框
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.customdialog);
        dialog.setTitle("乐途记");



        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);

        // 如果单击了按钮，则关闭自定义对话框
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 设置自定义对话框组件-文本、图像和按钮
                content = (EditText) dialog.findViewById(R.id.content);
                final String ss=content.getText().toString();
                Log.d("presenter", "得到说说的内容是dialog  "+ss);
                presenter.saveContent(ss, locationgId);

                dialog.dismiss();

            }
        });
        dialog.show();
    }
    // 显示照片
    public void displayPhoto(Bitmap bitmap){
        ImageView imageView =
                (ImageView)findViewById(R.id.info_image);
        imageView.setImageBitmap(photoBitmap);
    }
    /**
     * 为当前Activity初始化主导器
     */
    private void initPresent(){
        presenter = new MainPresenter(); //初始化
        presenter.setView(this);//把view实例传到主导器
    }
    /**
     * 地图重写的方法
     */
    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

        // 通知主导器,本view已经加载显示完毕
        presenter.viewFinishLoading();  // 执行主导器代码
    }

     /*
      *菜单的工具
      */
     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.menu_main, menu);
         return true;
     }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.test_dao:
                presenter.tripDaoTest();    // 由主导器处理dao数据访问,并回调显示结果
                break;
            case  android.R.id.home:// 如果单击的是Home 按钮
                Toast.makeText(this,"返回菜单页",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(this,MenuActivity.class);
                startActivity(intent);
                break;
            case  R.id.Add_memory:
                // 选择经纬度

                selectLatLng();
                Toast.makeText(this,"开始规划出行的路线图",Toast.LENGTH_SHORT).show();
                break;

        }
        return true;
    }

    /*
    implements IMainView 接口中重写的方法
     */

    @Override
    public void showDaoTestResult(String resultCode) {
        // 显示dao测试结果
        Toast.makeText(this, resultCode, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showNoInetErrorMsg() {

    }

    @Override
    public void moveToNextActivity() {

    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
