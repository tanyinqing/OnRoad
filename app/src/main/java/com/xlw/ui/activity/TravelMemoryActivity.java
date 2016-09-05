package com.xlw.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.v7.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xlw.db.LocationDBHelper;
import com.xlw.db.PhotoDBHelper;
import com.xlw.db.TripDBHelper;
import com.xlw.model.Photo;
import com.xlw.model.Trip;
import com.xlw.onroad.R;
import com.xlw.presenter.ITravelMemoryView;
import com.xlw.presenter.TravelMemoryPresenter;
import com.xlw.ui.adapter.CoutriesAdapt;
import com.xlw.ui.adapter.SimpleSectionsAdapter;
import com.xlw.ui.adapter.XlwBaseAdapter;
import com.xlw.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TravelMemoryActivity extends BaseActivity implements ITravelMemoryView {

    TravelMemoryPresenter travelMemoryPresenter;

    private ListView lstCoutries;
    ActionBar actionBar;

    ListView listView;
    SimpleSectionsAdapter<TravelMemoryPresenter.TripAndPhotos> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_memory);
        judgeData();
        travelMemoryPresenter = new TravelMemoryPresenter();
        travelMemoryPresenter.setView(this);
        // 设置title 文本
//        actionBar.setTitle("旅行记忆");

        initActionBar();

        addTripCategorySelectLisenter();    // 设置旅游类型选择事件响应

//        listView = new ListView(this);      // 存入旅行记录的列表
        listView = (ListView)findViewById(R.id.trip_memory_list);
        initTripData();
    }


     /*
         对数据库进行判断的函数   // 查询出所有的旅行信息
     */
    public  void judgeData(){
        TripDBHelper tripDBHelper = new TripDBHelper();
        LocationDBHelper locationDBHelper=new LocationDBHelper();
        PhotoDBHelper photoDBHelper=new PhotoDBHelper();
        List<Trip> trips = tripDBHelper.loadAllTrip();
        List<com.xlw.model.Location> locations = locationDBHelper.loadAllLocation();
        List<Photo> photos = photoDBHelper.loadAllPhoto();
        if (trips.size()==0||locations.size()==0||photos.size()==0){
            Toast.makeText(this,"请先规划旅行",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(this,MenuActivity.class);
            startActivity(intent);
        }
    }



    /*
         actionBar中加载布局sppinner
     */
    private void initActionBar(){
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeAsUpIndicator(R.drawable.ball);

        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);

//        actionBar.setHomeButtonEnabled(true);
        actionBar.setCustomView(R.layout.trip_category_actionbar);
    }

    private void initTripData(){
        Spinner spinner = (Spinner)findViewById(R.id.tripCategory);
        ToastUtil.showShortMsg(this, "您选择的是:" + spinner.getSelectedItem().toString());

        adapter = new SimpleSectionsAdapter<TravelMemoryPresenter.TripAndPhotos>(
                listView,   			        // 用于资源填充的上下文 - listview列表
                R.layout.trip_list_header, 	    // 用于header views的布局
                R.layout.trip_list_item 	    // 用于item views的布局
        ) {
            // 当用户单击列表中的项时,调用此事件处理方法
            @Override
            public void onSectionItemClick(TravelMemoryPresenter.TripAndPhotos item) {
                Toast.makeText(TravelMemoryActivity.this, item.trip.getTopic(), Toast.LENGTH_SHORT).show();
                Intent intent=new Intent();
                intent.setClass(TravelMemoryActivity.this,SevenActivity.class);
                Bundle bundle=new Bundle();
                long tripId=item.trip.getId();

                bundle.putLong("tripId",tripId);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        };

        listView.setAdapter(adapter);
    }

    // 为按钮添加事件响应,当选择好旅游类型后,响应按钮单击事件
    private void addTripCategorySelectLisenter(){
        Button button = (Button)actionBar.getCustomView().findViewById(R.id.btn_fetchTripData);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 通过P主导器提取旅游数据
                travelMemoryPresenter.fetchTripData();
            }
        });
    }

    // 更新ListView Adapter数据的方法,由主导器回调实现
    @Override
    public void updateAdapterData(LinkedHashMap<String,List<TravelMemoryPresenter.TripAndPhotos>> tripSection){
        Set<String> headers = tripSection.keySet();

        for(String header : headers){
            adapter.addSection(header,tripSection.get(header));
        }
//        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 通知主导器,本view已经加载显示完毕
        travelMemoryPresenter.viewFinishLoading();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_travel_memory, menu);
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
                Toast.makeText(this,"返回上一页",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(this,MenuActivity.class);
                startActivity(intent);
        }
        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Toast.makeText(this,"开启一个新的旅行",Toast.LENGTH_SHORT).show();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void gotoNextActivity(Class tClass) {

    }




}
