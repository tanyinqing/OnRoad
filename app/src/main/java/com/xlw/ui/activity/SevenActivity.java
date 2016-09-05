package com.xlw.ui.activity;

import com.xlw.db.LocationDBHelper;
import com.xlw.db.PhotoDBHelper;
import com.xlw.db.TripDBHelper;
import com.xlw.model.*;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.xlw.onroad.R;
import com.xlw.utils.ImagesUtil;

import java.util.ArrayList;
import java.util.List;

public class SevenActivity extends ActionBarActivity implements ViewSwitcher.ViewFactory{
        long tripId;
    public Trip trip;
    public List<Photo> photos;
    public List<Location> locations;
    public List<Bitmap> bitmaps;
    TextView topic_memory;
    TextView desc_memory;


    ImageView[] imageViews ;
    private ImageSwitcher imageSwitcher;
// 自定义用于管理Gallery 中数据的适配器
//    ImageView imageView1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seven);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
// 使用up 箭头指示器显示home
        actionBar.setDisplayHomeAsUpEnabled(true);
// 设置title 文本
        actionBar.setTitle("旅行的记忆");

//        imageView1=(ImageView)findViewById(R.id.image);
        bitmaps=new ArrayList<Bitmap>();
        getTripId();
//        new ProgressAsyncTask().execute(); // 启动异步任务类
//        imageView1.setImageResource(R.mipmap.ic_launcher);
//        imageView1.setImageBitmap(bitmaps.get(1));
        imageViews = new ImageView[bitmaps.size()];

        topic_memory=(TextView)findViewById(R.id.topic_memory);
        topic_memory.setText(trip.getTopic());
        Log.d("tag", "得到的数据时" + trip.getTopic());
        desc_memory=(TextView)findViewById(R.id.desc_memory);
        desc_memory.setText(trip.getDesc());
        Log.d("tag", "得到的数据时" + trip.getDesc());


        LinearLayout mGalleryLinearLayout = (LinearLayout) findViewById(R.id.galleryLinearLayout);
        imageSwitcher = (ImageSwitcher) findViewById(R.id.switcher1);
        imageSwitcher.setFactory(this);
        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
// 为每个图片构造一个ImageView 视图组件用来显示该图片
        for(int i=0; i<bitmaps.size(); i++){
            ImageView image = new ImageView(this);
            image.setPadding(10, 5, 10, 5); // 设置内边距
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            image.setLayoutParams(new LinearLayout.LayoutParams(200, 160));
            image.setImageBitmap(bitmaps.get(i));
            image.setImageBitmap(bitmaps.get(i));
            imageViews[i] = image; // 放入数组中
        }
// 为每个ImageView 组件添加触屏事件监听器
        for (int i = 0; i < imageViews.length; i++) {
            final int index = i;
            imageViews[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            Toast.makeText(SevenActivity.this, "您选中了第" + (index+1) + "张图片!",
                                    Toast.LENGTH_SHORT).show();
// 突出显示被选中的图片
                            imageViews[index].setBackgroundColor(Color.CYAN);
// 在下方显示大图,带有淡入淡出动画效果
                            BitmapDrawable drawable = new BitmapDrawable(bitmaps.get(index));
                            imageSwitcher.setImageDrawable(drawable);
// 恢复其它图片的背景颜色
                            resetOtherImageBgcolor(index);
                            return true;
                    }
                    return false;
                }
            }); // end listener
// 将图像添加到LinearLayout 布局组件中
            mGalleryLinearLayout.addView(imageViews[i]);
        }
        }
    // 辅助方法,恢复基它视图(当前未被选中的视图)原来的背景颜色
    private void resetOtherImageBgcolor(int index) {
        for (int i = 0; i < bitmaps.size(); i++) {
            if (i != index) {
                imageViews[i].setBackgroundColor(
                        getResources().getColor(android.R.color.holo_orange_light));
            }
        }}
    @Override
    public View makeView() {
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundColor(0xFF000000);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                ImageSwitcher.LayoutParams.MATCH_PARENT,
                ImageSwitcher.LayoutParams.MATCH_PARENT));
        return imageView;
    }
    public void getTripId(){

        Intent intent=this.getIntent();
        Bundle bundle=intent.getExtras();
        tripId=bundle.getLong("tripId");

        TripDBHelper tripDBHelper=new TripDBHelper();
        trip=tripDBHelper.loadTrip(tripId);

        List<Location> locations = trip.getLocations();
        Log.d("tag", "图片集合的数量locations     " + locations.size());
        long locationId=locations.get(0).getId();

        PhotoDBHelper photoDBHelper=new PhotoDBHelper();
        photos=photoDBHelper.queryPhoto("where LOCATION_ID=?",String.valueOf(locationId));

        Log.d("tag", "图片集合的数量photos     " + photos.size());
        for (Photo photo:photos){
            String phoneUri=photo.getUri();

            Log.d("tag","phoneUri   "+phoneUri);

            Bitmap bitmap= ImagesUtil.loadBitmap(phoneUri.substring(7), 100, 80);
            Log.d("tag","图片路径bitmaps  "+phoneUri.substring(7));

//                BitmapDrawable drawable = new BitmapDrawable(bitmap);
            bitmaps.add(bitmap);
            Log.d("tag","得到图片的数量bitmaps   "+bitmaps.size());
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_seven, menu);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case android.R.id.home: // 如果单击的是Home 按钮
                Toast.makeText(this,"返回上一页",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(this,TravelMemoryActivity.class);
                startActivity(intent);
                break;
            case R.id.action_settings:
                Toast.makeText(this,"打开旅游路线",Toast.LENGTH_SHORT).show();
                Intent intent1=new Intent(this,EightActivity.class);
                Bundle bundle = new Bundle();                           //创建Bundle对象
                bundle.putLong("tripId",tripId);    //装入数据
                intent1.putExtras(bundle);                                //把Bundle塞入
                startActivity(intent1);
                break;


        }

        return super.onOptionsItemSelected(item);
    }


}
