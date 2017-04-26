package util;

import repair.com.repair.R;

/**
 * Created by hsp on 2017/4/25.
 */

public class UIUtil {
    public static int getStatusIcon(int status) {
        int image = 0;
        switch (status) {
            case 1:
                image = R.drawable.weichuli;
                break;
            case 2:
                image = R.drawable.yipaigong;
                break;
            case 3:
                image = R.drawable.yizuofei;
                break;

            case 4:
                image = R.drawable.yiwanjie;
                break;
            case 5:
                image = R.drawable.weixiuzhong;
                break;
            default:
                image = R.drawable.weichuli;
        }
        return image;
    }

    public static int getCategoryIcon(String name) {

        switch (name) {
            case "电工类":
                return   R.drawable.dian2;
            case "水工类":
                return   R.drawable.shui;

            case "家具类":

                return  R.drawable.jiaju;
            case "土建类":
                return  R.drawable.tujian;
            default:
                return   R.drawable.other;
        }
    }
}
