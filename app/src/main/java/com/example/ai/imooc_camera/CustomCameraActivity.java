package com.example.ai.imooc_camera;

import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 自定义相机
 * 注意：Camera是一个独立的系统资源，要与Activity的生命周期绑定，否则占用资源，而且还会出错
 */

/**
 * 自定义相机步骤：
 *
 *     1、得到Camera对象
 *     2、得到SurfaceView对象
 *     3、将Camera对象与SurfaceView对象进行关联
 *     4、调整相机的显示效果
 *     5、自定义相机预览界面
 *
 */
public class CustomCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback{


    private Camera mCamera;

    private SurfaceView mPreview;

    private SurfaceHolder mHolder;

    private Camera.PictureCallback mPictureCallback=new Camera.PictureCallback(){
        /**
         *
         * @param data 完整的图片数据
         * @param camera
         */
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            /**
             * 图片存贮路径
             */
            File tempFile=new File("/sdcard/temp.png");

            try {
                FileOutputStream fos=new FileOutputStream(tempFile);
                fos.write(data);

                fos.close();

                /**
                 * 把图片传到另外一个活动
                 */
                Intent intent=new Intent(CustomCameraActivity.this,ResultActivity.class);
                intent.putExtra("picPath",tempFile.getAbsolutePath());
                startActivity(intent);
                CustomCameraActivity.this.finish();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        mPreview=findViewById(R.id.surface_view);
        mHolder=mPreview.getHolder();
        mHolder.addCallback(this);

        /**
         * 实现点击屏幕就能自动对焦，而不是点击拍照后才会自动对焦
         */
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 不需要有回调，所以传入null
                 */
                mCamera.autoFocus(null);
            }
        });


    }

    /**
     * 拍照方法
     * @param view
     */
    public void capture(View view){
        /**
         * 相机参数
         */
        Camera.Parameters parameters=mCamera.getParameters();
        /**
         * 图片格式
         */
        parameters.setPictureFormat(ImageFormat.JPEG);
        /**
         * 预览大小
         */
        parameters.setPreviewSize(800,400);
        /**
         * 设置自动对焦，前提是相机支持自动对焦
         */
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                /**
                 * 判断对焦是否准确，如果准确，就进行拍照
                 */
                if (success){

                    mCamera.takePicture(null, null,mPictureCallback);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCamera==null){
            mCamera=getCamera();
            if(mHolder!=null){
                setStartPreview(mCamera,mHolder);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        releaseCamera();


    }
    /**
     * Camera的生命周期方法：
     *       getCamera()
     *       setStartPreview(Camera camera,SurfaceHolder holder)
     *       releaseCamera()
     *
     */

    /**
     * 获取系统的Camera对象
     * @return
     */
    private Camera getCamera(){
        Camera camera;
        try {

            camera= Camera.open();

        } catch (Exception e) {
            camera=null;
            e.printStackTrace();
        }

        return camera;
    }

    /**
     * 开始预览相机内容
     */
    private void setStartPreview(Camera camera,SurfaceHolder holder){
        try {
            /**
             * 把camera与holder进行关联
             */
            camera.setPreviewDisplay(holder);
            /**
             * 系统默认的camera预览是横屏，设置旋转角度，使camera预览变为竖屏
             */
            camera.setDisplayOrientation(90);

            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 释放系统相机占用的资源
     */
    private void releaseCamera(){
        if (mCamera!=null){
            /**
             * 将相机预览回调置空
             */
            mCamera.setPreviewCallback(null);
            /**
             * 停止预览效果
             */
            mCamera.stopPreview();
            mCamera.release();
            mCamera=null;
        }



    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        /**
         * 启动预览
         */
        setStartPreview(mCamera,mHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        /**
         * 停止预览
         */
        mCamera.stopPreview();
        /**
         * 启动预览
         */
        setStartPreview(mCamera,mHolder);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        releaseCamera();
    }
}
