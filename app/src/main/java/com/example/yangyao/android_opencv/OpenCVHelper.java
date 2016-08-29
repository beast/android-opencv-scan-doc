package com.example.yangyao.android_opencv;

/**
 * Created by yangyao on 28/08/2016.
 */
public class OpenCVHelper {
    static {
        System.loadLibrary("OpenCV");
    }
    public static native String crop(String filePath);
}
