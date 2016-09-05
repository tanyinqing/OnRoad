package com.xlw.ui.activity;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xlw.onroad.R;
import com.xlw.utils.GLCube;
import com.xlw.utils.GLES20Renderer2;

public class FirstActivity extends BaseActivity  implements View.OnTouchListener {
    private final float TOUCH_SENSITIVITY = 0.25f;
    private final float ANGLE_SPAN = 90.0f;
    private float dxFiltered = 0.0f;
    private float zAngle = 0.0f;
    private float filterSensitivity = 0.1f;
    private float zAngleFiltered = 0.0f;
    private int width;
    private float touchedX;

    MyGLSurfaceView myGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);


        android.support.v7.app. ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        myGLSurfaceView = (MyGLSurfaceView)findViewById(R.id.surfaceView);
        myGLSurfaceView.setOnTouchListener(this);

        ImageButton button = (ImageButton)findViewById(R.id.btn_load);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextActivity();
            }
        });

        TextView tv = (TextView)findViewById(R.id.tv_kh2);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextActivity();
            }
        });

        if (isSupportsEs2()) {
            getDeviceWidth();
        } else {
            Toast.makeText(this,"是时候换个新手机了-您的电话不支持OpenGL ES 2.0",Toast.LENGTH_LONG).show();
        }
    }

    private void gotoNextActivity(){
//        Toast.makeText(MainActivity.this,"hello",Toast.LENGTH_SHORT).show();
        /* 在一个时间延迟线程中启动闪屏和主菜单 */
        new Handler().postDelayed(new Thread() {
            @Override
            public void run() {
                Intent second = new Intent(FirstActivity.this, WelcomeActivity.class);
                FirstActivity.this.startActivity(second);
                Log.d("tag","在一个时间延迟线程中启动闪屏和主菜单");
                FirstActivity.this.finish();
                // 第一个参数为进入动画,第二个参数为退出动画
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        }, 500);		//延迟4秒后执行线程
    }

    // 判断是否支持OpenGL ES 2.o
    private boolean isSupportsEs2(){
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        return supportsEs2;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(myGLSurfaceView != null) {
            myGLSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        if(myGLSurfaceView != null) {
            myGLSurfaceView.onPause();
        }
        super.onPause();
        if (isFinishing()) {
            GLCube.setZAngle(0);
            dxFiltered = 0.0f;
            zAngle = 0.0f;
            zAngleFiltered = 0.0f;
            this.finish();
        }
    }

    // 获取设备宽度
    public void getDeviceWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        if (width > height) {
            this.width = width;
        } else {
            this.width = height;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.touchedX = event.getX();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float touchedX = event.getX();
            float dx = Math.abs(this.touchedX - touchedX);
            dxFiltered = dxFiltered * (1.0f - filterSensitivity) + dx * filterSensitivity;

            if (touchedX < this.touchedX) {
                zAngle = (2 * dxFiltered / this.width) * this.TOUCH_SENSITIVITY * this.ANGLE_SPAN;
                zAngleFiltered = zAngleFiltered * (1.0f - filterSensitivity) + zAngle * filterSensitivity;
                GLES20Renderer2.setZAngle(GLES20Renderer2.getZAngle() + zAngleFiltered);
//                GLCube.setZAngle(GLCube.getZAngle() + zAngleFiltered);
            } else {
                zAngle = (2 * dxFiltered / this.width) * this.TOUCH_SENSITIVITY * this.ANGLE_SPAN;
                zAngleFiltered = zAngleFiltered * (1.0f - filterSensitivity) + zAngle * filterSensitivity;
                GLES20Renderer2.setZAngle(GLES20Renderer2.getZAngle() - zAngleFiltered);
//                GLCube.setZAngle(GLCube.getZAngle() - zAngleFiltered);
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
