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

    public static File createImageFile() {
        //文件路径
        //File file = new File(Environment.getExternalStorageDirectory().toString()+"/Pictures/");
//        File file = new File(Environment.getExternalStorageDirectory().toString());
        //Log.d(TAG, "createImageFile: "+file.toString());
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_.jpg";
        try {
//            File imageFile = File.createTempFile(imageFileName,  /* prefix */
//                    ".jpg",         /* suffix */
//                    file/* directory */);
//            Log.d(TAG,"FileUtils获取文件url :"+imageFile.getPath());
            File imageFile =new File(Environment.getExternalStorageDirectory().toString()+"/Pictures/"
            +imageFileName);


            return imageFile;
        } catch (Exception e) {
            Log.d(TAG,"获取文件路径的时候出错:"+e.getMessage());
            e.printStackTrace();;
            return null;
        }

    }
    public static File createImageFile2(Context context) {
        //文件路径
        File file = new File(Util.getDiskCachePath(context));

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        try {
            File imageFile = File.createTempFile(imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    file/* directory */);
            Log.d("Apply_Fragment","FileUtils获取文件url"+imageFile.getPath());
            return imageFile;
        } catch (IOException e) {
            Log.d("Apply_Fragment","获取文件路径的时候出错");
            return null;
        }

    }

}
