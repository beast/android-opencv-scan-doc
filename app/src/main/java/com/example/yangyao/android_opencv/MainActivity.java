package com.example.yangyao.android_opencv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {

    Bitmap srcBitmap;
    Bitmap grayBitmap;
    Bitmap cannyBitmap;
    Bitmap linesBitmap;
    Bitmap origBitmap;

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
        cannyBitmap.recycle();
        linesBitmap.recycle();
        origBitmap.recycle();
        finish();
    }

    protected void grayScale(ImageView img) {

        Mat rgbMat = new Mat();
        Mat grayMat = new Mat();
        Mat cannyMat;
        Mat linesMat = new Mat();
        BitmapFactory.Options o=new BitmapFactory.Options();

        // TODO: 29/08/2016  May need to check sample size https://developer.android.com/training/displaying-bitmaps/load-bitmap.html
        o.inSampleSize = 4;
        o.inDither=false;


        // resize
        origBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.card2, o);

        int w = origBitmap.getWidth();
        int h = origBitmap.getHeight();
        int min_w = 800;
        double scale = Math.min(10.0, w*1.0/ min_w);
        int w_proc = (int) (w * 1.0 / scale);
        int h_proc = (int) (h * 1.0 / scale);
        srcBitmap = Bitmap.createScaledBitmap(origBitmap, w_proc, h_proc, false);
        grayBitmap = Bitmap.createBitmap(w_proc, h_proc, Bitmap.Config.RGB_565);
        cannyBitmap = Bitmap.createBitmap(w_proc, h_proc, Bitmap.Config.RGB_565);
        linesBitmap = Bitmap.createBitmap(w_proc, h_proc, Bitmap.Config.RGB_565);
        Utils.bitmapToMat(srcBitmap, rgbMat);//convert original bitmap to Mat, R G B.

        // grayscale
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat
        Utils.matToBitmap(grayMat, grayBitmap); //convert mat to bitmap
        img.setImageBitmap(grayBitmap);
    }

    protected void lines(ImageView img) {
        Mat rgbMat = new Mat();
        Mat grayMat = new Mat();
        Mat cannyMat;
        Mat linesMat = new Mat();
        BitmapFactory.Options o=new BitmapFactory.Options();

        // TODO: 29/08/2016  May need to check sample size https://developer.android.com/training/displaying-bitmaps/load-bitmap.html
        o.inSampleSize = 4;
        o.inDither=false;


        // resize
        origBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.card2, o);

        int w = origBitmap.getWidth();
        int h = origBitmap.getHeight();
        int min_w = 800;
        double scale = Math.min(10.0, w*1.0/ min_w);
        int w_proc = (int) (w * 1.0 / scale);
        int h_proc = (int) (h * 1.0 / scale);
        srcBitmap = Bitmap.createScaledBitmap(origBitmap, w_proc, h_proc, false);
        grayBitmap = Bitmap.createBitmap(w_proc, h_proc, Bitmap.Config.RGB_565);
        cannyBitmap = Bitmap.createBitmap(w_proc, h_proc, Bitmap.Config.RGB_565);
        linesBitmap = Bitmap.createBitmap(w_proc, h_proc, Bitmap.Config.RGB_565);
        Utils.bitmapToMat(srcBitmap, rgbMat);//convert original bitmap to Mat, R G B.

        // grayscale
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat

        // canny
        cannyMat = getCanny(grayMat);

        // HoughLinesP


        Imgproc.HoughLinesP(cannyMat,linesMat, 1, Math.PI/180, w_proc/12, w_proc/12, 20 );

        Log.e("opencv","lines.cols " + linesMat.cols() + " w_proc/3: " + w_proc/3);
        for (int x = 0; x < linesMat.rows(); x++)
        {
            double[] vec = linesMat.get(x, 0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);

            Imgproc.line(cannyMat, start, end, new Scalar(255,0,0), 20, Imgproc.LINE_AA, 0);

        }

        Log.e("opencv","completed HoughLines");
        Log.e("opencv","linesMat size: " + linesMat.size());
        Log.e("opencv","linesMat size: " + Double.toString(linesMat.size().height) + " x " +Double.toString(linesMat.size().width));
        Log.e("opencv", "linesBitmap size: " + Integer.toString(linesBitmap.getHeight()) +" x " + Integer.toString(linesBitmap.getWidth()));
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
