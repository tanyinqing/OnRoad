package com.xlw.ui.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.xlw.ui.fragment.WelcomeFragmentA;
import com.xlw.ui.fragment.WelcomeFragmentB;
import com.xlw.onroad.R;
import com.xlw.ui.myactivity.DataActivity;


public class WelcomeActivity extends Activity {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    static final int PAGE_NUMBER = 2;	//引导页面数量
    private ImageView[] points;		    // 底部小点的图片
    private int currentIndex;			// 记录当前选中位置

    LinearLayout linearLayout;
    Button goButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
//        Intent intent=new Intent(this,MenuActivity.class);
//        startActivity(intent);


        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // 当滑动状态改变时调用
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // 当当前页面被滑动时调用
            }

            @Override
            public void onPageSelected(int position) {
                // 当新的页面被选中时调用
                // 设置底部小点选中状态
                setCurDot(position);
            }
        });

        // 初始化底部小点
        initPoint();

        goButton = (Button)findViewById(R.id.btn_go);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go();
            }
        });
    }

    // 进入主Activity
    private void go(){
        startActivity(new Intent(this,MenuActivity.class));
        finish();
    }

    // 初始化底部小点
    private void initPoint() {
        linearLayout = (LinearLayout) findViewById(R.id.ll);
        points = new ImageView[PAGE_NUMBER];

        // 循环取得小点图片
        for (int i = 0; i < PAGE_NUMBER; i++) {
            // 得到一个LinearLayout下面的每一个子元素
            points[i] = (ImageView) linearLayout.getChildAt(i);
            // 默认都设为灰色
            points[i].setEnabled(true);
            // 给每个小点设置监听
            points[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 单击某个小点，切换到对应的页面
                    int position = (Integer) v.getTag();
                    setCurView(position);
                    setCurDot(position);
                }
            });
            // 设置位置标签，方便取出与当前位置对应
            points[i].setTag(i);
        }

        // 设置当前默认的位置
        currentIndex = 0;
        // 设置为白色，即选中状态
        points[currentIndex].setEnabled(false);
        points[currentIndex].setImageDrawable(getResources().getDrawable(R.mipmap.point_select));
    }

    // 设置当前页面的位置
    private void setCurView(int position) {
        if (position < 0 || position >= PAGE_NUMBER) {
            return;
        }
        mViewPager.setCurrentItem(position);
    }

    // 设置当前的小点的位置
    private void setCurDot(int position) {
        if (position < 0 || position > PAGE_NUMBER - 1 || currentIndex == position) {
            return;
        }
        points[position].setEnabled(false);
        points[position].setImageDrawable(getResources().getDrawable(R.mipmap.point_select));

        points[currentIndex].setEnabled(true);
        points[currentIndex].setImageDrawable(getResources().getDrawable(R.mipmap.point_normal));

        if(position == PAGE_NUMBER-1){
            // 在最后一个引导页
            goButton.setVisibility(View.VISIBLE);
        }else{
            goButton.setVisibility(View.INVISIBLE);
        }

        currentIndex = position;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new WelcomeFragmentA();
                case 1:
                    return new WelcomeFragmentB();
            }
            return null;
        }

        @Override
        public int getCount() {
            // 总共显示5个页面
            return PAGE_NUMBER;
        }

        @Override
        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return getString(R.string.title_section1);
//                case 1:
//                    return getString(R.string.title_section2);
//            }

            return null;
        }
    }

}
