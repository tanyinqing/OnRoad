package com.xlw.ui.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.xlw.onroad.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragmentB extends Fragment {

    Animation animatBall;       // 动画
    ImageView animImageView;    // 应用动画的组件

    public WelcomeFragmentB() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

        initAnim();
    }

    @Override
    public void onResume() {
        super.onResume();

        animImageView = (ImageView)this.getActivity().findViewById(R.id.animImageView);
        animImageView.startAnimation(animatBall);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome_b, container, false);
    }


    // 设置动画监听器
    private void initAnim(){
        // 加载动画资源
        animatBall = AnimationUtils.loadAnimation(this.getActivity(),R.anim.ball_bounce);
        animatBall.setRepeatMode(Animation.RESTART);
        animatBall.setRepeatCount(Animation.INFINITE);
        animatBall.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animImageView.setImageResource(R.drawable.ball);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animImageView.setImageResource(R.drawable.angel);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                animImageView.setImageResource(R.drawable.angel);
            }
        });
    }

}
