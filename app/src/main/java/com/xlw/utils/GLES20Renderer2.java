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
 * Created by xinliwei on 2015/7/15.
 */
public class GLES20Renderer2 implements GLSurfaceView.Renderer {

    private static String TAG = "GLES20Renderer.class";

    private int cubeProgram;
    private FloatBuffer cubeVFB;
    private ShortBuffer cubeISB;
    private FloatBuffer cubeTFB;
    private int cubeAPositionLocation;
    private int cubeUMVPLocation;
    private int cubeUSamplerLocation;
    private int cubeACoordinateLocation;

    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] mvpMatrix = new float[16];
    private float[] rMatrix = new float[16];

    private int textureId;
    public Context mContext;
    private static volatile float zAngle = 0;   // Z轴转动角度
    private static volatile float yAngle = 45;  // Y轴转动角度
    private static volatile float xAngle = 30;  // X轴转动角度

    private long startTime;             // 保存开始时间

    public GLES20Renderer2(Context context) {
        this.mContext = context;

        startTime = System.currentTimeMillis();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 设置窗体的背景颜色.实际上是指定清除屏幕时使用的颜色.
        // 4个参数分别用于设置红、绿、蓝和透明度的值，值的范围是0.0f~1.0f

        gl.glClearColor(0f, 0f, 0f, 0f);
//        gl.glClearColor(0.9f, 0.9f, 0.8f, 1.0f);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);  // 启用深度测试
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);   // 设置深度测试的类型
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置3D场景的大小。4个参数分别为左下角(bottom-left)坐标和宽高
        GLES20.glViewport(0, height/6, width, height);

        initShapes();

        // 加载着色器并创建着色器程序
        cubeProgram = loadProgram(cubeVertexShaderCode, cubeFragmentShaderCode);

        // 获取指向着色器程序内相应成员成员变量的各个id(也可理解为句柄、指针)
        // 获取着色器程序中，指定为attribute类型变量的id(获取指向着色器中aPosition的index)
        cubeAPositionLocation = GLES20.glGetAttribLocation(cubeProgram, "aPosition");
        // 获取着色器程序中，指定为uniform类型变量的id(获取指向着色器中uMVPMatrix的index)
        cubeUMVPLocation = GLES20.glGetUniformLocation(cubeProgram, "uMVP");
        // 获取指向着色器中aCoord的index
        cubeACoordinateLocation = GLES20.glGetAttribLocation(cubeProgram, "aCoord");
        // 获取指向着色器中uSampler的index
        cubeUSamplerLocation = GLES20.glGetUniformLocation(cubeProgram, "uSampler");

        float ratio	= (float) width / height;
        float zNear = 0.1f;
        float zFar = 1000;
        float fov = 0.4f; // 0.2 to 1.0
        float size = (float) (zNear * Math.tan(fov / 2));
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 8, 0, 0, 0, 0, 1, 0);
        Matrix.frustumM(projectionMatrix, 0, -size, size, -size / ratio, size / ratio, zNear, zFar);
        Matrix.setIdentityM(rMatrix, 0);

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);       // 生成纹理
        textureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureId);
        GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST); // GLES20.GL_LINEAR
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

        // 从res/drawable/下加载纹理图片
       loadTexture1(mContext);

        // 从res/raw/下加载纹理图片
        //        loadTexture2(mContext);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // 让立方体不断旋转
        long elapsed = System.currentTimeMillis() - startTime;  // 计算逝去的时间
        yAngle = elapsed * (30f/1000f);     // 沿y轴旋转30度
        xAngle = elapsed * (15f/1000f);     // 沿x轴旋转15度

        Matrix.setIdentityM(rMatrix, 0);
        // 方法签名:Matrix.rotateM(float[] m, int mOffset, float degree, float x, float y, float z)
        Matrix.rotateM(rMatrix, 0, xAngle, 1, 0, 0);
        Matrix.rotateM(rMatrix, 0, yAngle, 0, 1, 0);
        Matrix.rotateM(rMatrix, 0, zAngle, 0, 0, 1);
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, rMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

        // 使用shader程序
        GLES20.glUseProgram(cubeProgram);

        /* 绑定纹理
        函数原型：void glActiveTexture (int texture)
        参数含义：
        texture指定哪一个纹理单元被置为活动状态。texture必须是GL_TEXTUREi之一，其中0 <= i < GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS，初始值为GL_TEXTURE0。
         */
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        checkError("glActiveTexture");
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureId);
        checkError("glBindTexture");
        GLES20.glUniform1i(cubeUSamplerLocation, 0);
        checkError("glUniform1i");

        // 使用前面获取的指向着色器相应数据成员的各个id，
        // 将我们自己定义的顶点数据、颜色数据等等各种数据传递到着色器当中

        /* 顶点位置数据传入着色器
        函数原型：
        void glVertexAttribPointer (int index, int size, int type, boolean normalized, int stride, Buffer ptr )
        参数含义：
          index  指定要修改的顶点着色器中顶点变量id；
          size   指定每个顶点属性的组件数量。必须为1、2、3或者4。如position是由3个（x,y,z）组成，而颜色是4个（r,g,b,a））；
          type   指定数组中每个组件的数据类型。可用的符号常量有GL_BYTE, GL_UNSIGNED_BYTE, GL_SHORT,GL_UNSIGNED_SHORT, GL_FIXED, 和 GL_FLOAT，初始值为GL_FLOAT；
          normalized  指定当被访问时，固定点数据值是否应该被归一化（GL_TRUE）或者直接转换为固定点值（GL_FALSE）；
          stride	  指定连续顶点属性之间的偏移量。如果为0，那么顶点属性会被理解为：它们是紧密排列在一起的。初始值为0。如果normalized被设置为GL_TRUE，意味着整数型的值会被映射至区间[-1,1](有符号整数)，或者区间[0,1]（无符号整数），反之，这些值会被直接转换为浮点值而不进行归一化处理；
          ptr  顶点的缓冲数据。
         */
        GLES20.glVertexAttribPointer(cubeAPositionLocation, 3, GLES20.GL_FLOAT, false, 12, cubeVFB);

        // 允许使用顶点坐标数组.如果启用，那么当glDrawArrays或者glDrawElements被调用时，顶点属性数组会被使用
        GLES20.glEnableVertexAttribArray(cubeAPositionLocation);

        // 纹理贴图顶点坐标数据传递到顶点着色器中
        GLES20.glVertexAttribPointer(cubeACoordinateLocation, 3, GLES20.GL_FLOAT, false, 12, cubeTFB);
        // 允许使用顶点纹理数组
        GLES20.glEnableVertexAttribArray(cubeACoordinateLocation);

        // 将最终变换矩阵传入shader程序
        GLES20.glUniformMatrix4fv(cubeUMVPLocation, 1, false, mvpMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 33, GLES20.GL_UNSIGNED_SHORT, cubeISB);
    }

    private void initShapes()  {
        // 记录六面体顶点位置的数组
        float[] cubeVFA = {
                -0.5f,-0.5f,0.5f,   0.5f,-0.5f,0.5f,   0.5f,0.5f,0.5f,   -0.5f,0.5f,0.5f,
                -0.5f,-0.5f,-0.5f,  0.5f,-0.5f,-0.5f,  0.5f,0.5f,-0.5f,  -0.5f,0.5f,-0.5f
        };

        // 记录六面体顶点位置索引的数组
        short[] cubeISA = {
                0,4,5,  0,1,5,  5,6,2,  5,1,2,
                5,6,7,  5,4,7,  7,6,2,  7,3,2,
                7,3,0,  7,4,0,  0,3,2,  0,1,2
        };

        // 记录六面体纹理位置的数组
        float[] cubeTFA = {
                -1,-1,1,   1,-1,1,   1,1,1,   -1,1,1,
                -1,-1,-1,  1,-1,-1,  1,1,-1,  -1,1,-1
        };

        // 创建顶点坐标数据缓冲,在GPU内存中缓存顶点数据,避免从主存拷贝
        ByteBuffer cubeVBB = ByteBuffer.allocateDirect(cubeVFA.length * 4);
        cubeVBB.order(ByteOrder.nativeOrder()); // 设置字节顺序
        cubeVFB = cubeVBB.asFloatBuffer();     // 转换为float型缓冲
        cubeVFB.put(cubeVFA);                  // 向缓冲中放入顶点坐标数据
        cubeVFB.position(0);                   // 设置缓冲区的起始位置

        // 创建顶点坐标数据缓冲,在GPU内存中缓存顶点数据,避免从主存拷贝
        ByteBuffer cubeIBB = ByteBuffer.allocateDirect(cubeISA.length * 2);
        cubeIBB.order(ByteOrder.nativeOrder());
        cubeISB = cubeIBB.asShortBuffer();
        cubeISB.put(cubeISA);
        cubeISB.position(0);

        // 创建顶点坐标数据缓冲,在GPU内存中缓存顶点数据,避免从主存拷贝
        ByteBuffer cubeTBB = ByteBuffer.allocateDirect(cubeTFA.length * 4);
        cubeTBB.order(ByteOrder.nativeOrder());
        cubeTFB = cubeTBB.asFloatBuffer();
        cubeTFB.put(cubeTFA);
        cubeTFB.position(0);
    }

    // 1.加载着色器代码
    private int loadShader(int shaderType, String source)  {
        /* 创建一个新的shader容器,它是能容纳shader的容器
           方法参数:
           GLES20.GL_VERTEX_SHADER - 顶点shader
           GLES20.GL_FRAGMENT_SHADER - 片元shader
           返回值:
           shader容器的id,正整数,可理解为c++中的指针或句柄
         */
        int shader = GLES20.glCreateShader(shaderType);

        // 若创建成功,则加载shader
        if(shader != 0){
            /* 添加shader的源代码。源代码应该以字符串数组的形式表示。
            当然，也可以只用一个字符串来包含所有的源代码。
            方法参数:
            shader是代表shader容器的id(由glCreateShader返回的整型数)
            source是包含源程序的字符串数组
             */
            GLES20.glShaderSource(shader, source);
            checkError("glShaderSource");

            /* 对shader容器中的源代码进行编译
             方法参数:shader是代表shader容器的id
              */
            GLES20.glCompileShader(shader);
            checkError("glCompileShader");
        }
        return shader;
    }

    // 2.创建着色器程序代码
    private int loadProgram(String vertexShaderCode, String fragmentShaderCode) {
        // 加载顶点shader
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        if(vertexShader == 0){
            return 0;
        }

        // 加载片元shader
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        if(fragmentShader == 0){
            return 0;
        }

        /* 创建着色器程序
        在连接shader之前,首先要创建一个容纳程序的容器,称为着色器程序容器.
        如果创建成功,则返回一个正整数作为该着色器程序的id.
         */
        int program = GLES20.glCreateProgram();
        // 若着色器程序创建成功则向程序中加入顶点着色器与片元着色器
        if(program != 0){
            // 向着色器程序中加入顶点着色器
            GLES20.glAttachShader(program, vertexShader);
            // 向着色器程序中加入片元着色器
            GLES20.glAttachShader(program, fragmentShader);
            // 链接程序
            GLES20.glLinkProgram(program);

            // 存放链接成功program数量的数组
            int[] linkStatus = new int[1];
            // 获取program的链接情况
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            // 若链接失败则报错并删除程序
            if (linkStatus[0] != GLES20.GL_TRUE){
                Log.e(TAG, "无法链接到着色器程序: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }

        // 释放shader资源
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);

        return program;
    }

    public static void setZAngle(float angle) {
        GLES20Renderer2.zAngle = angle;
    }

    public static float getZAngle() {
        return GLES20Renderer2.zAngle;
    }

    public void checkError(String function) {
        Log.d("Error : " + function, Integer.valueOf(GLES20.glGetError()).toString());
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

    private final String cubeVertexShaderCode =
            "attribute vec4 aPosition;			\n"
                    +	"attribute vec3 aCoord;				\n"
                    +	"varying vec3 vCoord;				\n"
                    +	"uniform mat4 uMVP;					\n"
                    +	"void main() {						\n"
                    +	" gl_Position = uMVP * aPosition;	\n"
                    +	" vCoord = aCoord;					\n"
                    +	"}									\n";

    private final String cubeFragmentShaderCode =
            "#ifdef GL_FRAGMENT_PRECISION_HIGH				\n"
                    +	"precision highp float;							\n"
                    +	"#else											\n"
                    +	"precision mediump float;						\n"
                    +	"#endif											\n"
                    +	"varying vec3 vCoord;							\n"
                    +	"uniform samplerCube uSampler;					\n"
                    +	"void main() {									\n"
                    +	" gl_FragColor = textureCube(uSampler,vCoord);	\n"
                    +	"}												\n";
}
