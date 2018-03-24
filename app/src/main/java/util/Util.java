package util;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import camera.CalculateImage;

import model.Apply;
import model.Category;
import model.Flies;
import model.Place;
import model.ResultBean;
import model.Room;
import repair.com.repair.R;

import static android.content.Context.MODE_PRIVATE;
import static constant.RequestUrl.REQUEST_CODE_CAMERA;
import static constant.RequestUrl.REQUEST_CODE_SD_CARD;


public class Util {

    private static final String TAG = "Util";


    public static boolean validateString(String validateString) {
        boolean illegality = false;
        Log.d(TAG, "validateString: " + validateString);
        String regEx = "[@#$%^&*\\[\\]/@#￥%……&*——{}【]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(validateString);
        illegality = m.find();
        Log.d(TAG, "validateString: " + illegality);
        return !illegality;
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


    public static String setContentTitle(Apply apply) {
        String address = apply.getArea();
        String detailAr = apply.getDetailArea();

        String flies = apply.getFlies();
        String room = apply.getRoom();
        String addressDetail = apply.getAddressDetail().replace("\\s*", "");

        StringBuffer sb = new StringBuffer();
        String result = "";

        sb.append(address).append(detailAr).append(flies).append(room);
        result = sb.toString();

        result = result.replace("null", "");
        boolean isNull = (!TextUtils.isEmpty(addressDetail));
        if (result.length() > 18) {
            result = result.substring(0, 18) + "...";
        } else if (result.length() > 10) {
            if (result.contains("其它")) {
                if (isNull) {
                    result = result.replace("其它", "，" + addressDetail);
                } else {
                    result = result.replace("其它", "");
                }

            }
        } else {

            if (result.contains("其它")) {
                if (result.indexOf("其它") != 2) {
                    if (isNull) {
                        result = result + "，" + addressDetail;
                    } else {

                    }

                } else {
                    if (isNull) {
                        result = result.replace("其它", "，" + addressDetail);
                    } else {
                        result = result.replace("其它", "");
                    }

                }

            }
        }

        if (result.length() > 18) {
            result = result.substring(0, 18) + "...";
        }

        return result;
    }

    public static String setClass(Apply apply, int length, boolean isTitle) {
        String result = "";
        String classs = apply.getClasss();
        String detailClas = apply.getDetailClass();
        String repairDetail = apply.getRepairDetails();
        boolean isNull = false;
        if (detailClas == null || detailClas.equals("")) {
            isNull = true;
            result = classs;
        } else {
            String temp = detailClas;
            temp = "(" + temp + ")";
            result = classs + temp;
        }

        if (repairDetail != null) {
            if (repairDetail.equals("null") || repairDetail.equals("")) {

            } else {
                Log.d(TAG, "setClass: " + repairDetail);
                result = result + "," + repairDetail;
            }

        }
        if (isTitle) {
            if (result.length() > length) {
                String temp = result.substring(0, length - 2);
                String end = "...";
                result = temp + end;
            }
        } else {
            if (result.length() > length) {
                String temp = result.substring(0, length - 1);
                String end = result.substring(length - 1, result.length());
                result = temp + "\n" + end;
            }
        }
        return result;
    }


    public static void formatBreakDown(Apply apply, TextView textView) {

        String classs = apply.getClasss();
        String detailClas = apply.getDetailClass();
        String repairDetail = apply.getRepairDetails();

        StringBuffer sb = new StringBuffer();
        sb.append(classs);

        //拼装 故障 和 故障类别 如: 电(空调)
        if (!(TextUtils.isEmpty(detailClas))) {
            sb.append("（").append(detailClas).append("）");
        }
        //拼装 故障描述
        if ((!(TextUtils.isEmpty(repairDetail))) && !("null".equals(repairDetail))) {
            sb.append("，").append(repairDetail);
        }

        String results = sb.toString();

        textView.setText(results);

    }

    public static void formatArea(Apply apply, TextView textView) {

        String address = apply.getArea();
        String detailAr = apply.getDetailArea();
        String flies = apply.getFlies();
        String room = apply.getRoom();
        String addressDetail = apply.getAddressDetail();

        StringBuffer sb = new StringBuffer(address);

        String result = "";


        Log.d(TAG, "formatArea: " + flies);

        //检查楼名是否为空
        if (TextUtils.isEmpty(detailAr) || "null".equals(detailAr)) {
            sb.append("，");
        } else {
            if ("其它".equals(detailAr)) {
                sb.append("，");
            } else {
                sb.append(detailAr);

                //检查层数是否为空
                if (TextUtils.isEmpty(flies) || "null".equals(flies)) {
                    sb.append("，");
                } else {

                    if ("其它".equals(flies)) {

                        sb.append("，");
                    } else {

                        sb.append(flies);
                        Log.d(TAG, "formatArea: " + sb.toString());
                        //检查房间数
                        if (TextUtils.isEmpty(room) || "null".equals(room)) {
                            sb.append("，");
                        } else {
                            if ("其它".equals(room)) {
                                sb.append("，");
                            } else {
                                sb.append(room).append("，");
                            }
                        }
                    }


                }


            }


        }
        if (!(TextUtils.isEmpty(addressDetail))) {
            sb.append(addressDetail);
            result = sb.toString();
        } else {
            result = sb.substring(0, sb.length() - 1);
        }
        textView.setText(result);
    }


    //将服务器第一次获取到的数据，写到文件json_data中，key为json
    public static void writeJsonToLocal(final ResultBean resultBean, final Context mContext) {

        String json = JsonUtil.beanToResultBean(resultBean);
        SharedPreferences.Editor editor = mContext.getSharedPreferences("json_data", mContext.MODE_PRIVATE).edit();
        editor.putString("json", json);
        editor.apply();
        Log.d(TAG, "writeJsonToLocal: 成功将FirstRequest的Json写入本地json_data文件中，key:json");


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


    public static String loadFirstFromLocal(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("json_data", context.MODE_PRIVATE);
        String json = preferences.getString("json", "");

        Log.d(TAG, "loadFirstFromLocal: 从本地文件json_data中读出json:" + json);
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
     * @param noImgUrl     api地址
     * @param files        文件集合
     * @return 用于回调onResponse和onError方法
     */
    public static RequestCall submit(String paramsKey, String paramsValues, String noImgUrl, String ImgUrl, List<File> files, Context context) {
        PostFormBuilder postFormBuilder = OkHttpUtils.post();

        for (int i = 0; i < files.size(); i++) {
            postFormBuilder.addFile("fileImg", "file" + i + ".jpg", files.get(i));
            // Log.d(TAG, "submit: " + files.get(i).getPath());
        }

        postFormBuilder.addParams(paramsKey, paramsValues);

        if (files.size() > 0) {
            postFormBuilder.url(ImgUrl);
        } else {
            postFormBuilder.url(noImgUrl);
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

    public static void getPermission(Activity activity) {
        PermissionUtil.justGetpermission(activity, Manifest.permission.CAMERA, REQUEST_CODE_CAMERA);
        PermissionUtil.justGetpermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_CODE_SD_CARD);
    }


    public static RequestCall submit(String requestURL, Context context) {
        PostFormBuilder postFormBuilder = OkHttpUtils.post();
        postFormBuilder.url(requestURL);

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

    public static void deleteImage(Context context, String imgPath) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = MediaStore.Images.Media.query(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=?",
                new String[]{imgPath}, null);
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

    public static String getMD5(String mingwen) {
        String salt = ",>?<>(?>j(%&$#%U)(gh_^&*$^&*(_+";

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String first = salt + mingwen;
        byte[] bytValue, bytHash;
        byte[] bytValueTwo, bytHashTwo;
        bytValue = first.getBytes();
//		        System.out.println("bytValue :" +bytValue);
        md.update(bytValue);
        bytHash = md.digest();
//		        System.out.println("bytHash :" +bytHash);
        //清理
        // md.reset();

        bytValueTwo = mingwen.getBytes();
//		        System.out.println("bytValueTwo :" +bytValueTwo);
        md.update(bytValueTwo);
        bytHashTwo = md.digest();
//		        System.out.println("bytHashTwo :" +bytHashTwo);
        String sTemp = "";
        String sTwoTemp = "";


        sTemp = new BigInteger(1, bytHash).toString(16);
        System.out.println("sTemp :" + sTemp);
        sTwoTemp = new BigInteger(1, bytHashTwo).toString(16);
//		        System.out.println("sTwoTemp :" +sTwoTemp);
        String totalTemp = sTemp + sTwoTemp;
        return totalTemp;

//	         System.out.println("totalTemp :"+totalTemp.toLowerCase());
    }


    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;

        String regx = "^(0?(13[0-9]|15[012356789]|17[013678]|18[0-9]|14[57])[0-9]{8})|(400|800|0771)([0-9\\\\-]{7,10})|(([0-9]{4}|[0-9]{3})(-| )?)?([0-9]{7,8})((-| |转)*([0-9]{1,4}))?$";
        CharSequence inputStr = phoneNumber;

        Pattern pattern = Pattern.compile(regx);

        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }

        return isValid;


    }


}
