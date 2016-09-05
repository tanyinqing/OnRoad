package com.xlw.utils;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by xinliwei on 2015/7/14.
 *
 * 代表要被绘制的立方体模型对象
 *
 * p78
 */
public class GLCube {

    private static String TAG = "GLCube.class";

    private int width,height;
    Context mContext;

    int one = 65536;
    int half = one / 2;

    private int cubeProgram;            // 着色器程序
    private FloatBuffer cubeVFB;        // 用于记录顶点坐标数据缓冲的成员变量
    private ShortBuffer cubeISB;
    private FloatBuffer cubeTFB;        // 纹理贴图数据缓冲

    private int cubeAPositionLocation;  // 指向着色器中aPosition的index
    private int cubeUMVPLocation;       // 指向着色器中uMVPMatrix的index
    private int cubeUSamplerLocation;
    private int cubeACoordinateLocation;

    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] mvpMatrix = new float[16];
    private float[] rMatrix = new float[16];

    private int textureId;              // 纹理id
    private static volatile float zAngle = 0;   // Z轴转动角度
    private static volatile float yAngle = 45;  // Y轴转动角度
    private static volatile float xAngle = 30;  // X轴转动角度

    private long startTime;             // 保存开始时间

    public GLCube(Context context,int width, int height) {
        this.mContext = context;
        this.width = width;
        this.height = height;

        startTime = System.currentTimeMillis();

        initShapes();       // 初始化顶点坐标数据
        initData();
    }

    private void initShapes(){
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

    // 加载着色器并生成着色器程序和贴图纹理
    private void initData(){
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
        GLES20.glDeleteShader(vertexShader );
        GLES20.glDeleteShader(fragmentShader);

        return program;
    }

    // 用于绘制立方体的方法
    public void draw(){
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

    public static void setZAngle(float angle) {
        GLCube.zAngle = angle;
    }

    public static float getZAngle() {
        return GLCube.zAngle;
    }

    public static void setYAngle(float angle) {
        GLCube.yAngle = angle;
    }

    public static float getYAngle() {
        return GLCube.yAngle;
    }

    public static void setXAngle(float angle) {
        GLCube.xAngle = angle;
    }

    public static float getXAngle() {
        return GLCube.xAngle;
    }

    public void checkError(String function) {
        Log.d("Error : " + function, Integer.valueOf(GLES20.glGetError()).toString());
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
                    +	"} 												\n";

}

/*
Geometric/Modeling transformation
 Matrix.translateM(float[] m, int mOffset, float x, float y, float z):
分别沿着x,y,z轴平移矩阵m距离x,y和z.
(请注意,对于我们所使用的Matrix类的所有方法,我们设置偏移参数(mOffset)为0,这意味着没有偏移.
 Matrix.rotateM(float[] m, int mOffset, float a, float x, float y, float z):
沿指定的轴旋转矩阵m一个角度a(以度为单位)
 Matrix.scaleM(float[] m, int mOffset, float x, float y, float z): Scales
matrix m by x, y, and z along x-axis, y-axis, and z-axis, respectively

Coordinate/Viewing transformation
 Matrix.setLookAtM(float[] m, int mOffset, float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ):
Defines a view matrix m in terms of an eye point (that is, viewer’s position), a center of view, and an up vector.

Perspective/Projection transformation
 Matrix.frustumM(float[] m, int mOffset, float left, float right, float bottom, float top, float near, float far):
Defines a projection matrix m in terms of six clip planes
 */