package util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;

import camera.FIleUtils;

import static repair.com.repair.MainActivity.TAKE_PHOTO_RAW;

public class APKUtils {
    /**
     * 获取当前本地apk的版本
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    /***
     * 调起安装APP窗口  安装APP
     * @param context
     */
    public static void showSelectAPK(Context context, String fileName) {
        File fileLocation = new File(Environment.getExternalStorageDirectory(), fileName);//APK名称
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(fileLocation), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    private static final String TAG = "APKUtils";

    public static void installApk(Activity context, String fileName) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        File fileUri;
        fileUri = FIleUtils.createDownLoadFile(fileName);
        Log.d(TAG, "installApk path : " + fileUri.getAbsolutePath());

        Uri uri;
        int currentVersion = android.os.Build.VERSION.SDK_INT;

        //低于24为6.0以下
        if (currentVersion < 24) {
            uri = Uri.fromFile(fileUri);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            uri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName(),
                    fileUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);

    }
}