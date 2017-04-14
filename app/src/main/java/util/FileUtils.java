package util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by hsp on 2017/4/11.
 */

public class FileUtils {

    private static final String TAG = "FileUtils";



    public static String saveBitMapToFile(Context context, String fileName, Bitmap bitmap, boolean isCover,String filePath) {

        if(null == context || null == bitmap) {
            return null;
        }
        if(TextUtils.isEmpty(fileName)) {
            return null;
        }
        FileOutputStream fOut = null;
        try {
            File file = null;
            String fileDstPath = "";

            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                // 保存到sd卡
                fileDstPath = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator
                        +filePath+File.separator+fileName+".jpg";

                File homeDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        +File.separator+filePath);
                if (!homeDir.exists()) {
                    homeDir.mkdirs();
                }
            } else {
                // 保存到file目录
                fileDstPath = context.getFilesDir().getAbsolutePath()
                        + File.separator+filePath+File.separator + fileName+".jpg";

                File homeDir = new File(context.getFilesDir().getAbsolutePath()
                        +File.separator+filePath);
                if (!homeDir.exists()) {
                    homeDir.mkdir();
                }
            }

            file = new File(fileDstPath);

            if (!file.exists() || isCover) {
                // 简单起见，先删除老文件，不管它是否存在。
                file.delete();
                fOut = new FileOutputStream(file);
                //压图的注释掉
//                if (fileName.endsWith(".jpg")) {
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fOut);
//                } else {
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
//                }
                fOut.flush();
                bitmap.recycle();
            }

            Log.i("FileSave", "saveDrawableToFile " + fileName
                    + " success, save path is " + fileDstPath);
            return fileDstPath;
        } catch (Exception e) {
            Log.e("FileSave", "saveDrawableToFile: " + fileName + " , error", e);
            return null;
        } finally {
            if(null != fOut) {
                try {
                    fOut.close();
                } catch (Exception e) {
                    Log.e("FileSave", "saveDrawableToFile, close error", e);
                }
            }
        }
    }
}
