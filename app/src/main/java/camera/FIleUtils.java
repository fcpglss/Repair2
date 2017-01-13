package camera;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hsp on 2016/12/16.
 */

public class FIleUtils {

    public static File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        try {
            File imageFile = File.createTempFile(imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    Environment.getExternalStorageDirectory()      /* directory */);
            Log.d("Apply_Fragment","FileUtils获取文件url"+imageFile.getPath());
            return imageFile;
        } catch (IOException e) {
            Log.d("Apply_Fragment","获取文件路径的时候出错");
            return null;
        }
    }
}
