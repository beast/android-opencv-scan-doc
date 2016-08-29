package com.example.yangyao.android_opencv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {

    Bitmap srcBitmap;
    Bitmap grayBitmap;
    Bitmap cannyBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();


    }

    private void initView() {
        final ImageView img = (ImageView) findViewById(R.id.imageView);
        Button greyScaleBtn= (Button) findViewById(R.id.button);
        Button linesBtn= (Button) findViewById(R.id.button2);
        // init OpenCV
        // https://github.com/quanhua92/NDK_OpenCV_AndroidStudio
        // http://blog.csdn.net/sbsujjbcy/article/details/49520791
        // https://blog.nishtahir.com/2015/11/11/setting-up-for-android-ndk-development/
        // https://github.com/daisygao/ScannerLites
        OpenCVLoader.initDebug();

        greyScaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grayScale(img);
            }
        });

        linesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lines(img);
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        srcBitmap.recycle();
        grayBitmap.recycle();
        finish();
    }

    protected void grayScale(ImageView img) {

        Mat rgbMat = new Mat();
        Mat grayMat = new Mat();
        BitmapFactory.Options o=new BitmapFactory.Options();

        // TODO: 29/08/2016  May need to check sample size https://developer.android.com/training/displaying-bitmaps/load-bitmap.html
        o.inSampleSize = 6;
        o.inDither=false;
        srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.card, o);
        grayBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.RGB_565);
        Utils.bitmapToMat(srcBitmap, rgbMat);//convert original bitmap to Mat, R G B.
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat
        Utils.matToBitmap(grayMat, grayBitmap); //convert mat to bitmap
        img.setImageBitmap(grayBitmap);
    }

    protected void lines(ImageView img) {
        Mat rgbMat = new Mat();
        Mat grayMat = new Mat();
        Mat cannyMat = new Mat();
        BitmapFactory.Options o=new BitmapFactory.Options();

        // TODO: 29/08/2016  May need to check sample size https://developer.android.com/training/displaying-bitmaps/load-bitmap.html
        o.inSampleSize = 6;
        o.inDither=false;

        // resize
        srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.card, o);
        grayBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.RGB_565);
        cannyBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.RGB_565);
        Utils.bitmapToMat(srcBitmap, rgbMat);//convert original bitmap to Mat, R G B.

        // grayscale
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat

        // canny
        cannyMat = getCanny(grayMat);

        // draw lines


        Utils.matToBitmap(cannyMat, cannyBitmap); //convert mat to bitmap
        img.setImageBitmap(cannyBitmap);
    }

    protected Mat getCanny(Mat gray) {
        Mat threshold = new Mat();
        Mat canny = new Mat();
        // last paramter 8 is using OTSU algorithm
        double high_threshold = Imgproc.threshold(gray, threshold, 0, 255, 8);
        double low_threshold = high_threshold * 0.5;
        Imgproc.Canny(gray, canny, low_threshold, high_threshold);
        return canny;
    }
}
