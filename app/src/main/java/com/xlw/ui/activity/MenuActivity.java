package com.xlw.ui.activity;


import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;

import com.tandong.sa.slideMenu.SlidingMenu;
import com.tandong.sa.view.SmartListView;
import com.xlw.onroad.R;
import com.xlw.presenter.IMenuView;
import com.xlw.presenter.MenuPresenter;
import com.xlw.ui.myactivity.DataActivity;
import com.xlw.utils.ToastUtil;
import com.xlw.utils.UIUtil;

public class MenuActivity extends BaseActivity implements IMenuView, AdapterView.OnItemClickListener {

    ImageView imageView_start;
    ImageView imageView_jh;
    ImageView imageView_photowall;
    ImageView imageView_logo;




    MenuPresenter menuPresenter;

    SlidingMenu menu;    // 侧滑抽屉菜单
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // 设置主导器
        menuPresenter = new MenuPresenter();
        menuPresenter.setView(this);

        init();

        initActionBar();
        slideMenu();
    }

        /*
        侧滑菜单的建立
        */
    //标题
        private void initActionBar(){
            ActionBar actionBar = getSupportActionBar();
      // actionBar.setDefaultDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("乐途记");
        }
        //侧滑菜单
    private void slideMenu(){
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);//左右都可滑出
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);//滑出模式（全屏可滑出还是边界滑出）
        menu.setShadowWidthRes(R.dimen.shadow_width);//阴影宽度
        menu.setShadowDrawable(R.drawable.shadow);//阴影
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);//菜单宽度
        menu.setBehindWidth(480);//主界面剩余宽度
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.layout_menu);//左侧菜单
//        menu.setSecondaryMenu(R.layout.layout_menu);// 设置右侧菜单
//        menu.setSecondaryShadowDrawable(R.drawable.shadow);// 设置右侧菜单阴影的图片资源

        Button button = (Button)menu.findViewById(R.id.btn_change_theme);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTheme();      // 改变主题
            }
        });

    }
        //改变主题
    private void changeTheme(){
        int[] themeColors = UIUtil.getThemeColor(this);
        ToastUtil.showLongMsg(this, "背景色:" + themeColors[0] + ", 文本颜色:" + themeColors[1]);
    }

    private void init() {
        imageView_start();
        imageView_jh();
        imageView_photowall();
        imageView_logo();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 通知主导器,本view已经加载显示完毕
        menuPresenter.viewFinishLoading();
    }

    //照片墙
    private void imageView_photowall() {
        imageView_photowall = (ImageView) findViewById(R.id.imageview_photowall);
        Animation animation3 = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.imageview_start_bg_action);
        imageView_photowall.startAnimation(animation3);
        imageView_photowall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MenuActivity.this, MainActivity.class));
                menuPresenter.gotoNextView(TravelMemoryActivity.class); // 调用主导器
            }
        });
    }
    //旅游规划
    private void imageView_jh() {
        imageView_jh = (ImageView) findViewById(R.id.imageview_jh);
        Animation animation2 = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.imageview_start_bg_action);
        imageView_jh.startAnimation(animation2);
        imageView_jh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MenuActivity.this, MainActivity.class));
                menuPresenter.gotoNextView(TravelPlanningActivity.class); // 调用主导器
            }
        });
    }

    //单击开始一次旅行
    private void imageView_start() {
        imageView_start = (ImageView) findViewById(R.id.imageview_start);
        Animation animation = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.imageview_start_bg_action);
        imageView_start.startAnimation(animation);

        imageView_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MenuActivity.this, MainActivity.class));
                menuPresenter.gotoNextView(TravelNewActivity.class); // 调用主导器
            }
        });

    }

    private void imageView_logo() {
        imageView_logo = (ImageView) findViewById(R.id.imageView_logo);
        int pivot = Animation.RELATIVE_TO_SELF;
        CycleInterpolator interpolator = new CycleInterpolator(3.0f);
        RotateAnimation animation4 = new RotateAnimation(0, 10, pivot, 0.47f, pivot, 0.05f);
        animation4.setStartOffset(500);
        animation4.setDuration(3000);
        animation4.setRepeatCount(3);   // Animation.INFINITE 动画的次数
        animation4.setInterpolator(interpolator);
        imageView_logo.startAnimation(animation4);

    }

    @Override
    public void gotoNextActivity(Class tClass) {
        startActivity(new Intent(this, tClass));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_travel_memory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                menu.toggle();
                break;
            case R.id.action_settings:
                Intent intent=new Intent(this, DataActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
