package util;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by hsp on 2017/5/10.
 */

public class PermissionUtil {


    public static boolean hasPermission(Context context, String... permissions) {

        //版本大于6.0
        if (Build.VERSION.SDK_INT >= 23) {
            //检查权限
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) !=
                        PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 根据有没有权限执行相应动作
     *
     * @param activity   哪个activity
     * @param permission 需要什么权限
     * @param code       回调识别码
     * @param d          动作
     */
    public static void permission(Activity activity, String permission, int code, DoSomething d) {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(activity, permission);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, code);
            } else {
                d.doSomething();
            }
        } else {
            d.doSomething();
        }
    }

    public static void justGetpermission(Activity activity, String permission, int code) {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(activity, permission);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, code);
            }
        }
    }


    public void requestPermission(int code, Activity activity, String... permissions) {
        ActivityCompat.requestPermissions(activity, permissions, code);

    }

}
