package com.xlw.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.xlw.onroad.R;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by hxsd on 2015/7/17.
 */
public class GLES20Renderer implements GLSurfaceView.Renderer{
    private static String TAG = "GLES20Renderer.class";

    GLCube cube;
    public Context mContext;

    public GLES20Renderer(Context context) {
        mContext = context;
    }

    // 当GLSurfaceView被创建时回调该方法
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 设置窗体的背景颜色.实际上是指定清除屏幕时使用的颜色.
        // 4个参数分别用于设置红、绿、蓝和透明度的值，值的范围是0.0f~1.0f
        gl.glClearColor(0.9f, 0.9f, 0.8f, 1.0f);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);  // 启用深度测试
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);   // 设置深度测试的类型
//        GLES20.glDepthRangef(1, 0);
    }

    // 当GLSurfaceView的大小改变时回调该方法
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置3D场景的大小。4个参数分别为左下角(bottom-left)坐标和宽高
        GLES20.glViewport(0, height/6, width, height);

        cube = new GLCube(mContext,width,height);

        // 从res/drawable/下加载纹理图片
        loadTexture1(mContext);

        // 从res/raw/下加载纹理图片
//        loadTexture2(mContext);
    }

    // Render对象调用该方法绘制GLSurfaceView的当前帧
    // onDrawFrame方法的调用是有系统调用的，不需要手动调用。系统会以一定的频率不断的回调。
    @Override
    public void onDrawFrame(GL10 gl) {
        // 重设背景颜色,清除颜色缓存和深度缓存
        gl.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        cube.draw();
    }

    // 加载纹理贴图的方法 - 纹理贴图放res下
    public void loadTexture1(Context context){
        // 加载位图
        Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.mipmap.photo1);
        // 使用图片生成纹理
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, bitmap1, 0);
        // 释放资源
        bitmap1.recycle();

        // 加载位图
        Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.mipmap.photo2);
        // 使用图片生成纹理
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, bitmap2, 0);
        // 释放资源
        bitmap2.recycle();

        // 加载位图
        Bitmap bitmap3 = BitmapFactory.decodeResource(context.getResources(), R.mipmap.photo3);
        // 使用图片生成纹理
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, bitmap3, 0);
        // 释放资源
        bitmap3.recycle();

        // 加载位图
        Bitmap bitmap4 = BitmapFactory.decodeResource(context.getResources(), R.mipmap.photo4);
        // 使用图片生成纹理
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, bitmap4, 0);
        // 释放资源
        bitmap4.recycle();

        // 加载位图
        Bitmap bitmap5 = BitmapFactory.decodeResource(context.getResources(), R.mipmap.photo5);
        // 使用图片生成纹理
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, bitmap5, 0);
        // 释放资源
        bitmap5.recycle();

        // 加载位图
        Bitmap bitmap6 = BitmapFactory.decodeResource(context.getResources(), R.mipmap.photo6);
        // 使用图片生成纹理
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, bitmap6, 0);
        // 释放资源
        bitmap6.recycle();
    }

    // 加载纹理贴图的方法 - 纹理贴图放raw下
    public void loadTexture2(Context context){
        InputStream is1 = context.getResources().openRawResource(R.raw.brick1);
        Bitmap img1;
        try {
            img1 = BitmapFactory.decodeStream(is1);
        } finally {
            try {
                is1.close();
            } catch(IOException e) {
                //e.printStackTrace();
            }
        }
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, img1, 0);

        // 释放资源
        img1.recycle();

        InputStream is2 = context.getResources().openRawResource(R.raw.brick2);
        Bitmap img2;
        try {
            img2 = BitmapFactory.decodeStream(is2);
        } finally {
            try {
                is2.close();
            } catch(IOException e) {
                //e.printStackTrace();
            }
        }
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, img2, 0);

        // 释放资源
        img2.recycle();

        InputStream is3 = context.getResources().openRawResource(R.raw.brick3);
        Bitmap img3;
        try {
            img3 = BitmapFactory.decodeStream(is3);
        } finally {
            try {
                is3.close();
            } catch(IOException e) {
                //e.printStackTrace();
            }
        }
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, img3, 0);

        // 释放资源
        img3.recycle();

        InputStream is4 = context.getResources().openRawResource(R.raw.brick4);
        Bitmap img4;
        try {
            img4 = BitmapFactory.decodeStream(is4);
        } finally {
            try {
                is4.close();
            } catch(IOException e) {
                //e.printStackTrace();
            }
        }
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, img4, 0);

        // 释放资源
        img4.recycle();

        InputStream is5 = context.getResources().openRawResource(R.raw.brick5);
        Bitmap img5;
        try {
            img5 = BitmapFactory.decodeStream(is5);
        } finally {
            try {
                is5.close();
            } catch(IOException e) {
                //e.printStackTrace();
            }
        }
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, img5, 0);

        // 释放资源
        img5.recycle();

        InputStream is6 = context.getResources().openRawResource(R.raw.brick6);
        Bitmap img6;
        try {
            img6 = BitmapFactory.decodeStream(is6);
        } finally {
            try {
                is6.close();
            } catch(IOException e) {
                //e.printStackTrace();
            }
        }
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, img6, 0);

        // 释放资源
        img6.recycle();
    }
}
