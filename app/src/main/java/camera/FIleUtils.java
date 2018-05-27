package camera;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import util.Util;

/**
 * Created by hsp on 2016/12/16.
 */

public class FIleUtils {

    private static final String TAG = "FIleUtils";

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");

    public static File createImageFile() {

        Date date = new Date();
        String timeStamp = simpleDateFormat.format(date);
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String imageFileName = "JPEG_" + timeStamp + "_.jpg";
        //      Log.d(TAG, "createImageFile: " +imageFileName);
        try {

            File imageFile = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/"
                    + imageFileName);
            return imageFile;
        } catch (Exception e) {
            Log.d(TAG, "获取文件路径的时候出错:" + e.getMessage());
            e.printStackTrace();
            ;
            return null;
        }
    }

    public static File createDownLoadFile(String fileName) {

        try {
            File imageFile = new File(Environment.getExternalStorageDirectory().toString() + "/apkdownload/"
                    + fileName);
            return imageFile;
        } catch (Exception e) {
            Log.d(TAG, "获取文件路径的时候出错:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static String createDownLoadFilePath() {

        try {
            File imageFile = new File(Environment.getExternalStorageDirectory().toString() + "/apkdownload/");
            return imageFile.getAbsolutePath();
        } catch (Exception e) {
            Log.d(TAG, "获取文件路径的时候出错:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
