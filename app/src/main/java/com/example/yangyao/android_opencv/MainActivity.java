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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class Line{
    Point _p1;
    Point _p2;
    Point _center;

    Line(Point p1, Point p2) {
        _p1 = p1;
        _p2 = p2;
        _center = new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }
}

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

        // Calculate horizontal lines and vertical lines
        Log.e("opencv","lines.cols " + linesMat.cols() + " w_proc/3: " + w_proc/3);
        List<Line> horizontals = new ArrayList<>();
        List<Line> verticals = new ArrayList<>();
        for (int x = 0; x < linesMat.rows(); x++)
        {
            double[] vec = linesMat.get(x, 0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);
            Line line = new Line(start, end);
            if (Math.abs(x1 - x2) > Math.abs(y1-y2)) {
                horizontals.add(line);
            } else {
                verticals.add(line);
            }

            // for visualization in debug mode
            if (BuildConfig.DEBUG) {
//                Imgproc.line(cannyMat, start, end, new Scalar(255,0,0), 10, Imgproc.LINE_AA, 0);
            }
        }

        // if we don't have at least 2 horizontal lines or vertical lines
        if (horizontals.size() < 2) {
            if (horizontals.size() == 0 || horizontals.get(0)._center.y > h_proc /2) {
                horizontals.add(new Line(new Point(0,0),new Point(w_proc-1, 0)));
            }
            if (horizontals.size() == 0 || horizontals.get(0)._center.y <= h_proc /2) {
                horizontals.add(new Line(new Point(0,h_proc-1),new Point(w_proc-1, h_proc-1)));
            }
        }
        if (verticals.size() < 2) {
            if (verticals.size() == 0 || verticals.get(0)._center.x > w_proc / 2) {
                verticals.add(new Line(new Point(0, 0), new Point(h_proc - 1, 0)));
            }
            if (verticals.size() == 0 || verticals.get(0)._center.x <= w_proc / 2) {
                verticals.add(new Line(new Point(w_proc - 1, 0), new Point(w_proc - 1, h_proc - 1)));
            }
        }

        Collections.sort(horizontals, new Comparator<Line>() {
            @Override
            public int compare(Line lhs, Line rhs) {
                return (int)(lhs._center.y - rhs._center.y);
            }
        });

        Collections.sort(verticals, new Comparator<Line>() {
            @Override
            public int compare(Line lhs, Line rhs) {
                return (int)(lhs._center.x - rhs._center.x);
            }
        });


        // for visualization in debug mode
        if (BuildConfig.DEBUG) {
            Imgproc.line(rgbMat, horizontals.get(0)._p1, horizontals.get(0)._p2, new Scalar(0,255,0), 10, Imgproc.LINE_AA, 0);
            Imgproc.line(rgbMat, horizontals.get(horizontals.size()-1)._p1, horizontals.get(horizontals.size()-1)._p2, new Scalar(0,255,0), 10, Imgproc.LINE_AA, 0);
            Imgproc.line(rgbMat, verticals.get(0)._p1, verticals.get(0)._p2, new Scalar(255,0,0), 10, Imgproc.LINE_AA, 0);
            Imgproc.line(rgbMat, verticals.get(verticals.size()-1)._p1, verticals.get(verticals.size()-1)._p2, new Scalar(255,0,0), 10, Imgproc.LINE_AA, 0);
        }


        Log.e("opencv","completed HoughLines");
        Log.e("opencv","linesMat size: " + linesMat.size());
        Log.e("opencv", "linesBitmap size: " + Integer.toString(linesBitmap.getHeight()) +" x " + Integer.toString(linesBitmap.getWidth()));
        Utils.matToBitmap(rgbMat, srcBitmap); //convert mat to bitmap
        img.setImageBitmap(srcBitmap);
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
