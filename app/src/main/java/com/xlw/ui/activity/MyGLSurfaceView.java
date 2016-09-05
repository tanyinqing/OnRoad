package com.xlw.ui.activity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.xlw.utils.GLES20Renderer;
import com.xlw.utils.GLES20Renderer2;

/**
 * Created by hxsd on 2015/7/17.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    public MyGLSurfaceView(Context context) {
        this(context,null);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setZOrderOnTop(true);
        this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);

//        this.requestFocus();                //获取焦点
//        this.setFocusableInTouchMode(true); //设置为可触控

//        this.getHolder().setFormat(PixelFormat.TRANSPARENT);  // 设置背景为透明

        // 告诉glSurfaceView我们想要创建一个OpenGL ES 2.0兼容的环境
        // 并调协一个OpenGL ES 2.0兼容的渲染器
        this.setEGLContextClientVersion(2);

        // 在Honeycomb+设备上,这能提高性能,当离开和恢复时
        this.setPreserveEGLContextOnPause(true);

        // 为glSuraceView指定使用的Renderer对象
//        this.setRenderer(new GLES20Renderer(context));
        this.setRenderer(new GLES20Renderer2(context));
    }
}
