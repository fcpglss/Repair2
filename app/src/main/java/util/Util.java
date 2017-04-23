package util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import camera.CalculateImage;
import model.Announcement;
import model.Apply;
import model.Area;
import model.Category;
import model.Flies;
import model.Place;
import model.Response;
import model.ResultBean;
import model.Room;
import repair.com.repair.AdminDetailActivity;
import repair.com.repair.ChangeActivity;
import repair.com.repair.R;

import static android.content.Context.MODE_PRIVATE;
import static repair.com.repair.MainActivity.JSON_URL;
import static repair.com.repair.MainActivity.UP_APPLY;


public class Util {

    private static final String TAG = "Util";

    private  static View.OnTouchListener touch;


    private EdiTTouch ed;

    public static int convertToInt(Object value, int defaultValue) {
        if (value != null || "".equals(value.toString().trim())) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(value.toString());
        } catch (Exception e) {
            try {
                return Double.valueOf(value.toString()).intValue();

            } catch (Exception e2) {
                return defaultValue;
            }
        }
    }

    public static String convertToString(Object value, String defaultString) {
        if (value != null || "".equals(value.toString().trim())) {
            return defaultString;
        }
        try {
            return value.toString();
        } catch (Exception e) {

            return defaultString;
        }

    }

    /**
     * 获取cache目录路径
     */
    public static String getDiskCachePath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            return context.getExternalCacheDir().getPath();
        } else {
            return context.getCacheDir().getPath();
        }
    }

    /**
     * ����¥������ȡ��¥�ŵ�ID
     *
     * @param detailAreaName ,¥����
     * @param listPlace      ,��DetailArea�л�ȡ������
     * @return
     */
    public static int getDetailAreaId(String detailAreaName, List<Place> listPlace) {
        if (detailAreaName != null && !detailAreaName.equals("")) {
            for (Place place : listPlace) {

                return place.getP_id();
            }
            return -2;
        }
        return -2;
    }

    public static int getCategoryId(String className, List<Category> listCategory) {
        if (className != null && !className.equals("")) {
            for (Category category2 : listCategory) {
                if (category2.getC_name().equals(className))
                    return category2.getC_id();
            }
            return -2;
        }
        return -2;
    }

    /**
     * ���ݲ����,¥��ID,��ȡ���ID
     *
     * @param placeId   ¥�ŵ�ID
     * @param fliesName �����
     * @param listFlies Flies���е�����
     * @return
     */
    public static int getFlies(int placeId, String fliesName, List<Flies> listFlies) {
        if (fliesName != null && !fliesName.equals("")) {
            for (Flies flies : listFlies) {
                if (placeId == flies.getaFloor()) {
                    if (fliesName.equals(flies.getFlies())) {
                        return flies.getId();
                    }
                }

            }
            return 0;
        } else {
            return 0;
        }

    }

    public static int getRoomId(int fliesId, String roomName, List<Room> listRoom) {
        if (fliesId == 0) {
            return 0;
        }
        if (roomName != null && !roomName.equals("")) {
            for (Room romm : listRoom) {
                if (fliesId == romm.getFlies()) {
                    if (roomName.equals(romm.getRoomNumber())) {
                        return romm.getId();
                    }
                }

            }
            return 0;
        } else {
            return 0;
        }
    }

    //为放置flies层号，房间号为null设置
    public static String setTitle(Apply apply) {
        String address = "";
        String flies = apply.getFlies();
        String room = apply.getRoom();
        if (flies != null && !flies.equals("")) {
            address = apply.getArea() + " " + apply.getDetailArea() + flies;
            if (room != null && !room.equals("")) {
                address = apply.getArea() + " " + apply.getDetailArea() + flies + apply.getRoom();
            }
        } else {
            address = apply.getArea() + " " + apply.getDetailArea(); //没有层号的时候 可在后面加其他地址
        }
        return address;
    }

    public static String setAddress(Apply apply) {
        String address = "";
        String flies = apply.getFlies();
        String room = apply.getRoom();
        String place = apply.getDetailArea();
        if (place==null||"null".equals(place)||"".equals(place)){
            return apply.getArea()+getAdressDetalil(apply.getAddressDetail());
        }else {
            if (flies != null && !flies.equals("")) {
                address = apply.getDetailArea() + flies;
                if (room != null && !room.equals("")) {
                    address = apply.getArea()+apply.getDetailArea() + flies + apply.getRoom();
                }
            } else {
                address = apply.getArea() + apply.getDetailArea(); //没有层号的时候 可在后面加其他地址
            }
        }
        return address;
    }

    public static String getAdressDetalil(String s){
        Log.d(TAG, "getAdressDetalil: 1");
        Log.d(TAG, "getAdressDetalil: ss: "+s);
        if (s==null||"null".equals(s)||"".equals(s)){
            Log.d(TAG, "getAdressDetalil: 2");
            s="";
        }else {
            Log.d(TAG, "getAdressDetalil: 3");
            s = ","+s;
        }

        return s;
    }

    public static String errorMessage(Response response) {
        switch (response.getErrorType()) {
            case -1:
                return "连接服务器超时或者网络不通";
            case -2:
                return "连接成功，但是服务器返回数据为空或异常";
            default:
                return "";
        }

    }

    public static void writeJsonToLocal(final String jsonString, final Context mContext) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String json = jsonString;
                SharedPreferences.Editor editor = mContext.getSharedPreferences("json_data", mContext.MODE_PRIVATE).edit();
                editor.putString("json", json);
                editor.commit();
            }
        }).start();

    }

    //将服务器第一次获取到的数据，写到文件json_data中，key为json
    public static void writeJsonToLocal(final ResultBean resultBean, final Context mContext) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String json = JsonUtil.beanToResultBean(resultBean);
                SharedPreferences.Editor editor = mContext.getSharedPreferences("json_data", mContext.MODE_PRIVATE).edit();
                editor.putString("json", json);
                editor.apply();
                Log.d(TAG, "writeJsonToLocal: 成功将FirstRequest的Json写入本地json_data文件中，key:json");
            }
        }).start();

    }

    //将从服务器获取到的数据写入address_data文件中，key为address
    public static void writeAddressToLocal(final ResultBean resultBean, final Context mContext) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String json = JsonUtil.beanToResultBean(resultBean);
                SharedPreferences.Editor editor = mContext.getSharedPreferences("address_data", mContext.MODE_PRIVATE).edit();
                editor.putString("address", json);
                editor.apply();
                Log.d(TAG, "writeAddressToLocal: 成功将address的Json写入本地address_data文件中，key:address");
            }
        }).start();
    }

    //将手机号写到phoneData中去
    public static void writePhoneToLocal(Apply apply, Context mcontext) {
        String phone = apply.getTel();
        SharedPreferences.Editor editor = mcontext.getSharedPreferences("phoneData", MODE_PRIVATE).edit();
        editor.putString("phone", apply.getTel());
        editor.apply();
    }



    //从address_data文件中读出地点相关的字符串
    public static String loadAddressFromLocal(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("address_data", context.MODE_PRIVATE);
        String json = preferences.getString("address", "");

        Log.d(TAG, "loadAddressFromLocal:从本地address_data文件中读出json: " + json);
        return json;
    }

    //从
    public static String loadAnnouceFromLocal(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("annoucement_data", context.MODE_PRIVATE);
        String json = preferences.getString("annoucement", "");

        Log.d(TAG, "loadAnnouceFromLocal: 从本地文件annoucement_data中读出json:" + json);
        return json;
    }

    public static void writeAnnouceToLocal(final ResultBean resultBean, final Context mContext) {


        String json = JsonUtil.beanToResultBean(resultBean);
        SharedPreferences.Editor editor = mContext.getSharedPreferences("annoucement_data", mContext.MODE_PRIVATE).edit();
        editor.putString("annoucement", json);
        editor.apply();
        Log.d(TAG, "writeAnnouceToLocal: 成功将Annoucement的Json写入本地annoucement_data文件中，key:myrepair");
    }


    public static String loadFirstFromLocal(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("json_data", context.MODE_PRIVATE);
        String json = preferences.getString("json", "");

        Log.d(TAG, "loadFirstFromLocal: 从本地文件json_data中读出json:" + json);
        return json;
    }

    public static void writeMyResToLocal(final ResultBean resultBean, final Context mContext) {


                String json = JsonUtil.beanToResultBean(resultBean);
                SharedPreferences.Editor editor = mContext.getSharedPreferences("myrepair_data", mContext.MODE_PRIVATE).edit();
                editor.putString("myrepair", json);
                editor.apply();
                Log.d(TAG, "writeJsonToLocal: 成功将MyRepair的Json写入本地myrepair_data文件中，key:myrepair");
    }

    public static String loadMyResFromLocal(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("myrepair_data", context.MODE_PRIVATE);
        String json = preferences.getString("myrepair", "");
        Log.d(TAG, "loadFirstFromLocal: 从本地文件myrepair_data中读出json:" + json);
        return json;
    }

    /**
     * 将datetime类型的数据只精确到秒
     *
     * @param datetime
     * @return
     */
    public static String getDealTime(String datetime) {

        if (datetime != null && !datetime.equals("")) {
            return datetime.split(":")[0] + ":" + datetime.split(":")[1];
        }
        return "尚未处理";
    }

    public static String getFinshTime(String datetime) {

        if (datetime != null && !datetime.equals("")) {
            return datetime.split(":")[0] + ":" + datetime.split(":")[1];
        }
        return "尚未完成";
    }



    public static String createAsterisk(int length) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            stringBuffer.append("*");
        }
        return stringBuffer.toString();
    }

    public static String setNameXX(String name) {

        if (name.length() <= 1) {
            return name;
        } else {
            return name.replaceAll("([\\u4e00-\\u9fa5]{1})(.*)", "$1" + createAsterisk(name.length() - 1));
        }

    }

    /**
     * 使用okHttp框架post请求网络,若有图片文件则使用requestImgURL,
     * 若没有图片则使用requestURL
     *
     * @param paramsKey    参数名
     * @param paramsValues 参数值
     * @param requestURL   api地址
     * @param files        文件集合
     * @return 用于回调onResponse和onError方法
     */
    public static RequestCall submit(String paramsKey, String paramsValues, String requestURL, String requestImgURL, List<File> files) {
        PostFormBuilder postFormBuilder = OkHttpUtils.post();
        for (int i = 0; i < files.size(); i++) {
            postFormBuilder.addFile("file", "file" + i + ".jpg", files.get(i));
            Log.d(TAG, "submit: " + files.get(i).getPath());
        }

        postFormBuilder.addParams(paramsKey, paramsValues);
        if (files.size() > 0) {
            postFormBuilder.url(requestImgURL);
        } else {
            postFormBuilder.url(requestURL);
        }

        return postFormBuilder.build();
    }


    /**
     * 使用okHttp框架post请求网络
     *
     * @param paramsKey    参数名
     * @param paramsValues 参数值
     * @param requestURL   api接口的URL
     * @return
     */
    public static RequestCall submit(String paramsKey, String paramsValues, String requestURL) {
        PostFormBuilder postFormBuilder = OkHttpUtils.post();

        postFormBuilder.addParams(paramsKey, paramsValues);
        postFormBuilder.url(requestURL);
        return postFormBuilder.build();
    }
    public static RequestCall submit(String adminEmail, String adminEmailVules, String password, String passwordVules,
                                     String content ,String contentVules ,String serverEmail,String serverMailVules,
                                     String ID,String IDVules,String imgPath,String imgPathVules,
                                     String URL) {
        PostFormBuilder postFormBuilder = OkHttpUtils.post();
        postFormBuilder.addParams(adminEmail, adminEmailVules);
        postFormBuilder.addParams(password, passwordVules);
        postFormBuilder.addParams(content, contentVules);
        postFormBuilder.addParams(serverEmail, serverMailVules);
        postFormBuilder.addParams(ID, IDVules);
        postFormBuilder.addParams(imgPath, imgPathVules);
        postFormBuilder.url(URL);
        return postFormBuilder.build();
    }




    public static RequestCall submit(String paramsKey, String paramsValues, String paramsKey2, String paramsValues2, String requestURL) {
        PostFormBuilder postFormBuilder = OkHttpUtils.post();
        postFormBuilder.addParams(paramsKey, paramsValues);
        postFormBuilder.addParams(paramsKey2, paramsValues2);
        postFormBuilder.url(requestURL);
        return postFormBuilder.build();
    }

    /**
     * 将传入路径的图片，压缩
     *
     * @param path 传入图片的绝对路径
     * @return 压缩后的图片绝对路径
     */

    public static String compressImage(Context context, String path) {

        String getNewPath = context.getExternalCacheDir()
                + new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());

        String nowPath = path;

        Bitmap b = CalculateImage.getSmallBitmap(path, 200, 200);

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(getNewPath));
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "compressImage: " + getNewPath);

        return getNewPath;
    }

    public static String getPath(Activity context, Uri uri) {

        String[] proj = {MediaStore.Images.Media.DATA};

        //好像是Android多媒体数据库的封装接口，具体的看Android文档

        Cursor cursor = context.managedQuery(uri, proj, null, null, null);
        //按我个人理解 这个是获得用户选择的图片的索引值
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        //将光标移至开头 ，这个很重要，不小心很容易引起越界
        cursor.moveToFirst();
        //最后根据索引值获取图片路径
        String path = cursor.getString(column_index);

        Log.d(TAG, "getPath: " + path);

        return path;
    }

    public static boolean getPhotoUrl(int position, ResultBean rs) {
        List<String> photoList = new ArrayList<>();
        if (rs == null || rs.getApplys() == null || rs.getApplys().get(position).getA_imaes() == null) {
            return false;
        }

        photoList = rs.getApplys().get(position).getA_imaes();
        if (photoList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static void deleteImage(Context context,String imgPath) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = MediaStore.Images.Media.query(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=?",
                new String[] { imgPath }, null);
        boolean result = false;
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uri = ContentUris.withAppendedId(contentUri, id);
            int count = context.getContentResolver().delete(uri, null, null);
            result = count == 1;
        } else {
            File file = new File(imgPath);
            result = file.delete();
        }

    }




    public static String getMD5(String mingwen){

        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(mingwen.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new  BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            throw new RuntimeException("MD5加密出现错误");
        }
    }


    public static void setOnClickBackgroundColor(final View view){

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d(TAG, "onTouch: " + event.getAction());
                    view.setBackgroundResource(R.drawable.button_submit2);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d(TAG, "onTouch: " + event.getAction());
                    view.setBackgroundResource(R.drawable.button_submit);
                }
                return false;
            }
        });
    }
    public static void setEditTextOnTouch(final View view){
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP){
//                    ed.setVisable();
                }



                return false;
            }
        });

    }





}
