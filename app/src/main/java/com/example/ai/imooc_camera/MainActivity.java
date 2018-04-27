package com.example.ai.imooc_camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * ****调用系统相机注意事项
 * 使用系统Intent
 *     ACTION_IMAGE_CAPTURE
 * 注册Camera功能
 *      <intent-filter>
           <action android:name="android.media.action.IMAGE_CAPTURE"/>
           <category android:name="android.intent.category.DEFAULT"/>
        </intent-filter>
 */
public class MainActivity extends AppCompatActivity {

    private static final int request_1=1;
    private static final int request_2=2;

    /**
     * 显示缩略图的相机
     */
    private Button mStartCamera;
    /**
     * 显示原图的相机
     */
    private Button mStartCamera1;

    /**
     * 自定义相机
     */
    private Button CustomCamera;

    private ImageView mImageView;

    /**
     * 原图存贮路径
     */
    private String mFilePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartCamera=findViewById(R.id.start_camera);
        mStartCamera1=findViewById(R.id.start_camera1);

        CustomCamera=findViewById(R.id.custom_camera);

        mImageView=findViewById(R.id.image_view);
        /**
         * 获得sd卡路径
         */
        mFilePath= Environment.getExternalStorageDirectory().getPath();
        /**
         * 图片存贮路劲
         */
        mFilePath=mFilePath+"/"+"temp.png";

        /**
         * 拍照后直接读取图片的二进制流后显示
         */
        mStartCamera.setOnClickListener(view->{
            /**
             * 隐式启动
             */
            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,request_1);
        });

        /**
         * 拍完照后，先存储图片到指定路径下，再读取显示
         */
        mStartCamera1.setOnClickListener(v->{
            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            /**
             *将uri指向图片存储路径
             */
            Uri photoUri=Uri.fromFile(new File(mFilePath));
            /**
             * 将图片输出路径MediaStore.EXTRA_OUTPUT指定为photoUri
             */
            intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
            startActivityForResult(intent,request_2);
        });

        /**
         * 自定义相机
         */
        CustomCamera.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this,CustomCameraActivity.class));
        });


    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK){
            if (requestCode==request_1){
                /**
                 * 包含了图片的二进制流,data返回的只是一张缩略图，像素比真实图片低很多
                 */
                /**
                 * 显示缩略图
                 */
                Bundle bundle=data.getExtras();

                Bitmap bitmap=(Bitmap)bundle.get("data");
                mImageView.setImageBitmap(bitmap);

            }else if(requestCode==request_2){
                /**
                 * 显示原图
                 */
                FileInputStream fis=null;
                try {

                    fis=new FileInputStream(mFilePath);
                    Bitmap bitmap= BitmapFactory.decodeStream(fis);
                    mImageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }finally {
                    if (fis!=null){
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }


    }
}
