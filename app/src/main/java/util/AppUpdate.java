package util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import cn.pedant.SweetAlert.SweetAlertDialog;
import constant.RequestUrl;

/**
 * Created by 14221 on 2018/1/4.
 */

public class AppUpdate {

    private static final String TAG = "AppUpdate";

    /**
     * 检测App是否需要更新
     *
     * @param mContext
     * @param
     */
    public static void queryAppBaseVersionInfo(final Activity mContext, float appNewsVersion, final  DownloadUtil.OnDownloadListener onDownloadListener) {
        try {
            float currentVersion =Float.valueOf(getVerisonName(mContext));
            if(appNewsVersion>currentVersion){
                Log.d(TAG, "queryAppBaseVersionInfo:  新版本 "+appNewsVersion );
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext,SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog
                        .setConfirmClickListener(null)
                        .setTitleText("更新最新版本")
                        .setCancelText("取消")
                        .setConfirmText("更新")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                DownloadUtil downloadUtil = DownloadUtil.get();
                                downloadUtil.download(RequestUrl.updateApp,"repair",onDownloadListener);
                            }
                        });
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public  static String getVerisonName(Activity context) throws PackageManager.NameNotFoundException {
        PackageManager packageManager =context.getPackageManager();
        PackageInfo pi =packageManager.getPackageInfo(context.getPackageName(),0);
        String version  =pi.versionName;
        return version;
    }


    public  static int getVerisonCode(Activity context) throws PackageManager.NameNotFoundException {
        PackageManager packageManager =context.getPackageManager();
        PackageInfo pi =packageManager.getPackageInfo(context.getPackageName(),0);
        int version  =pi.versionCode;
        return version;
    }

}
