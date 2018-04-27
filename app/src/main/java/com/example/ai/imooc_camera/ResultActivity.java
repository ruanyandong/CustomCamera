package com.example.ai.imooc_camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ResultActivity extends AppCompatActivity {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_result);
        mImageView=findViewById(R.id.pic);

        String path=getIntent().getStringExtra("picPath");
        try {
            FileInputStream fis=new FileInputStream(path);
            Bitmap bitmap=BitmapFactory.decodeStream(fis);

            Matrix matrix=new Matrix();
            /**
             * 调整角度，使图片由横屏显示变为竖屏显示
             */
            matrix.setRotate(90);

            bitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            mImageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Bitmap bitmap=BitmapFactory.decodeFile(path);

        //mImageView.setImageBitmap(bitmap);

    }
}
