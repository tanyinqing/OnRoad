package com.xlw.ui.activity;


import android.support.v7.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.xlw.db.TripDBHelper;
import com.xlw.model.Trip;
import com.xlw.onroad.R;
import com.xlw.presenter.ITravelNewView;
import com.xlw.presenter.TravelNewPresenter;

import java.util.List;

public class TravelNewActivity extends BaseActivity implements ITravelNewView,ActionBar.OnNavigationListener{

    TravelNewPresenter travelNewPresenter;  // 主导器
    ProgressDialog progressDialog;

    Bitmap photoBitmap;

    long tripId;

    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_new);

        travelNewPresenter = new TravelNewPresenter();
        travelNewPresenter.setView(this);

        init();
        workWithListActionBar();


    }

    protected void workWithListActionBar() {
        ActionBar bar=getSupportActionBar();
        // 使用up 箭头指示器显示home
        bar.setDisplayHomeAsUpEnabled(true);
// 设置title 文本
        bar.setTitle("乐途记");
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"国内游","国外游"});
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//加载adpter，设置回调函数。第一个函数是SpinnerAdatper 接口，ArrayAdapter 已经实现
       // SpinnerAdatper 的getView()函数，可以直接使用
        bar.setListNavigationCallbacks(myAdapter, this);
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        if (i==0){
            category="国内游";
        }else{
            category="国外游";
        }
        Log.d("tag", "List select position " + i + " itemId " + l+category);
        return false;
    }

//    //开启数据库的存储
//    public void cunzifu(String s1,String s2){
//        Trip trip1 = new Trip();
//        trip1.setTopic(s1);
//        trip1.setDesc(s2);
//        TripDBHelper tripDBHelper=new TripDBHelper();
//        tripDBHelper.saveTrip(trip1);
//    }


    private void init() {
        // 拍照-旅行第一照
        ImageButton btnTakeStartPhoto = (ImageButton)findViewById(R.id.btn_take_start_photo);
        btnTakeStartPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();   // 打开设备自带的相机
            }
        });

        // 开启新旅行
        Button btnTrvalNewStart = (Button)findViewById(R.id.btn_trval_new_start);
        btnTrvalNewStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText topic_edit = (EditText)findViewById(R.id.topic_about_trval);
                String topic = topic_edit.getText().toString();
                if(TextUtils.isEmpty(topic)){
                    Toast.makeText(TravelNewActivity.this,"请填写旅行主题",Toast.LENGTH_SHORT).show();
                    topic_edit.requestFocus();
                      return;
                }

                EditText desc_edit = (EditText)findViewById(R.id.desc_about_trval);
                String desc = desc_edit.getText().toString();
                if(TextUtils.isEmpty(desc)){
                    Toast.makeText(TravelNewActivity.this,"请简要描述一下本次旅行",Toast.LENGTH_SHORT).show();
                    desc_edit.requestFocus();
                    return;
                }

                tripId=travelNewPresenter.saveTripInfo(topic, desc, photoBitmap, category);  // 保存旅行信息
            }
        });
    }

    // 打开设备自带的相机
    private void openCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 得到返回的照片
        Bundle bundle = data.getExtras();
        if(bundle != null){
            photoBitmap = (Bitmap) bundle.get("data");
            displayPhoto(photoBitmap);  // 显示照片
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 通知主导器,本view已经加载显示完毕
        travelNewPresenter.viewFinishLoading();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_travel_new, menu);
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
                finish();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void displayPhoto(Bitmap bitmap) {
        ImageView imageView = (ImageView)findViewById(R.id.image_first_photo);
        imageView.setImageBitmap(photoBitmap);
    }

    // 跳转到下一个
    @Override
    public void gotoNextActivity(long tripId) {
        Intent intent = new Intent(this,MainActivity.class);

        Bundle bundle = new Bundle();                           //创建Bundle对象
        bundle.putLong("tripId", tripId);    //装入数据
        Log.d("presenter", "查看发送的tripId是否准确   " + tripId);
        intent.putExtras(bundle);                                //把Bundle塞入
        startActivity(intent);
    }

    @Override
    public void showProgressDialog() {
        progressDialog = new ProgressDialog(this,0);
        progressDialog.setTitle("正在进行人脸识别...");
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    public void showErrorMsg(String errorMsg) {
        Toast.makeText(this,errorMsg,Toast.LENGTH_SHORT).show();
    }

}
