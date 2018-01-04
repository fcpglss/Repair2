package util;

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
import model.Admin;
import model.Apply;
import model.Category;
import model.Flies;
import model.Place;
import model.ResultBean;
import model.Room;
import repair.com.repair.R;

import static android.content.Context.MODE_PRIVATE;


public class Util {

    private static final String TAG = "Util";

    private  static View.OnTouchListener touch;


    private EdiTTouch ed;


    public static boolean validateString(String validateString){
        boolean illegality =false;
        Log.d(TAG, "validateString: "+validateString);
        String regEx = "[@#$%^&*\\[\\]/@#￥%……&*——{}【]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(validateString);
        illegality=m.find();
        Log.d(TAG, "validateString: "+illegality);
        return !illegality;
    }

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

//    //为放置flies层号，房间号为null设置
//    public static String setTitle(Apply apply) {
//        String address = "";
//        String detailAr=apply.getDetailArea();
//        if(TextUtils.isEmpty(detailAr)||detailAr.equals("null"))
//        {
//            detailAr="";
//        }
//        String flies = apply.getFlies();
//        String room = apply.getRoom();
//        if (flies != null && !flies.equals("")) {
//            address = apply.getArea() + " " + detailAr + flies;
//            if (room != null && !room.equals("")) {
//                address = apply.getArea() + " " + detailAr + flies + apply.getRoom();
//            }
//        } else {
//            address = apply.getArea() + " " + detailAr; //没有层号的时候 可在后面加其他地址
//        }
//        return address;
//    }

    public static String setAddress(Apply apply,int length,boolean isTitle) {
        String address = "";
        String detailAr=apply.getDetailArea();
        if(TextUtils.isEmpty(detailAr)||detailAr.equals("null"))
        {
            detailAr="";
        }
        String flies = apply.getFlies();
        String room = apply.getRoom();
        String addressDetail=apply.getAddressDetail();
        if (detailAr==null||"null".equals(detailAr)||"".equals(detailAr)){
          address=apply.getArea();
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
        if(addressDetail!=null)
        {
            if(addressDetail.equals("null")||addressDetail.equals(""))
            {

            }
            else
            {
                address=address+","+addressDetail;
            }
        }
        if(isTitle)
        {
            if(address.length()>length)
            {
                String temp=address.substring(0,length-2);
                String end="...";
                address=temp+end;
            }
        }
        else
        {
            if(address.length()>length)
            {
                String temp=address.substring(0,length-1);
                String end=address.substring(length-1,address.length());
                address=temp+"\n"+end;
            }
        }
        return address;
    }

    public static String setContentTitle(Apply apply) {
        String address = apply.getArea();
        String detailAr=apply.getDetailArea();

        String flies = apply.getFlies();
        String room = apply.getRoom();
        String addressDetail=apply.getAddressDetail();

        StringBuffer sb =new StringBuffer(address);

        String result="";

        int rawLineLen = 18;


        Log.d(TAG, "formatArea: "+flies);

        //检查楼名是否为空
        if(TextUtils.isEmpty(detailAr) || "null".equals(detailAr) ) {
            sb.append("，");
        } else {
            if("其它".equals(detailAr)) {
                sb.append("，");
            } else {
                sb.append(detailAr);

                //检查层数是否为空
                if(TextUtils.isEmpty(flies) || "null".equals(flies)){
                    sb.append("，");
                } else {

                    if("其它".equals(flies)) {

                        sb.append("，");
                    } else {

                        sb.append(flies);
                        Log.d(TAG, "formatArea: " +sb.toString());
                        //检查房间数
                        if(TextUtils.isEmpty(room) || "null".equals(room)){
                            sb.append("，");
                        } else {
                            if("其它".equals(room)) {
                                sb.append("，");
                            } else {
                                sb.append(room).append("，");
                            }
                        }
                    }


                }


            }


        }
        if(!(TextUtils.isEmpty(addressDetail))){
            sb.append(addressDetail);
            result=sb.toString();
        } else {
            result= sb.substring(0,sb.length()-1);
        }
        Log.d(TAG, "setContentTitle: "+result);


        if(result.length()>18){
            result=result.substring(0,18)+"...";
        }
        Log.d(TAG, "setContentTitle: "+result);
        return result;
    }

    public static String setClass(Apply apply,int length,boolean isTitle)
    {
        String result="";
        String classs=apply.getClasss();
        String detailClas=apply.getDetailClass();
        String repairDetail=apply.getRepairDetails();
        boolean isNull=false;
        if(detailClas==null||detailClas.equals(""))
        {
            isNull=true;
           result=classs;
        }
        else
        {
            String temp=detailClas;
                temp="("+temp+")";
                result=classs+temp;
        }

        if(repairDetail!=null)
        {
            if(repairDetail.equals("null")||repairDetail.equals(""))
            {

            }
            else
            {
                Log.d(TAG, "setClass: "+repairDetail);
                    result=result+","+repairDetail;
            }

        }
        if(isTitle)
        {
            if(result.length()>length)
            {
                String temp=result.substring(0,length-2);
                String end="...";
                result=temp+end;
            }
        }
        else
        {
            if(result.length()>length)
            {
                String temp=result.substring(0,length-1);
                String end=result.substring(length-1,result.length());
                result=temp+"\n"+end;
            }
        }
        return result;
    }

    /**
     * 获取字符双字节的长度
     * @param str
     * @return
     */
    private static int checkStringNumberLen(String str) {
        String regx ="[^\\x00-\\xff]";
        Pattern p = Pattern.compile(regx);
        Matcher m = p.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(m.find()) {
            sb.append(m.group());

        }

        System.out.println(sb.toString());

        int sbLen =sb.length();

        return sbLen;
    }


    public static void formatBreakDown(Apply apply , TextView textView ){

        String classs=apply.getClasss();
        String detailClas=apply.getDetailClass();
        String repairDetail=apply.getRepairDetails();

        int rawLineLen = 18;

        StringBuffer sb =new StringBuffer();
        sb.append(classs);

        //拼装 故障 和 故障类别 如: 电(空调)
        if(!(TextUtils.isEmpty(detailClas))) {
            sb.append("（").append(detailClas).append("）");
        }
        //拼装 故障描述
        if((!(TextUtils.isEmpty(repairDetail))) && !("null".equals(repairDetail))) {
            sb.append("，").append(repairDetail);
        }
        int len = sb.length();

        String results =sb.toString();

        Log.d(TAG, "formatBreakDown: "+results);

        int sbLen =checkStringNumberLen(results);

        int numberLen =len-sbLen;

        float resulteLen =0;

        if(len%2==0){
            resulteLen=sbLen+numberLen/2;
        } else {
            resulteLen=sbLen+(numberLen/2)+0.5f;
        }


        //如果字符不超过rawLineLen 单行显示
        if ( resulteLen<=rawLineLen) {
            textView.setText(results);
            textView.setGravity(Gravity.RIGHT);
        } else {
            //数字少,字符多情况
            if(numberLen<=6 && resulteLen<=20){

                textView.setGravity(Gravity.RIGHT);
            }
            textView.setText(results);
        }




    }

    public static void formatArea(Apply apply , TextView textView ){

        String address = apply.getArea();
        String detailAr=apply.getDetailArea();

        String flies = apply.getFlies();
        String room = apply.getRoom();
        String addressDetail=apply.getAddressDetail();

        StringBuffer sb =new StringBuffer(address);

        String result="";

        int rawLineLen = 18;


        Log.d(TAG, "formatArea: "+flies);

        //检查楼名是否为空
        if(TextUtils.isEmpty(detailAr) || "null".equals(detailAr) ) {
            sb.append("，");
        } else {
            if("其它".equals(detailAr)) {
                sb.append("，");
            } else {
                sb.append(detailAr);

                //检查层数是否为空
                if(TextUtils.isEmpty(flies) || "null".equals(flies)){
                    sb.append("，");
                } else {

                    if("其它".equals(flies)) {

                        sb.append("，");
                    } else {

                        sb.append(flies);
                        Log.d(TAG, "formatArea: " +sb.toString());
                        //检查房间数
                        if(TextUtils.isEmpty(room) || "null".equals(room)){
                            sb.append("，");
                        } else {
                            if("其它".equals(room)) {
                                sb.append("，");
                            } else {
                                sb.append(room).append("，");
                            }
                        }
                    }


                }


            }


        }
        if(!(TextUtils.isEmpty(addressDetail))){
            sb.append(addressDetail);
            result=sb.toString();
        } else {
            result= sb.substring(0,sb.length()-1);
        }

//        int numberLen =checkStringNumberLen(result);
//
//        int tempLen =sb.length()-numberLen;;
//
//        int resultLen =0;
//
//        if(tempLen%2==0){
//            resultLen=numberLen+(tempLen/2);
//        } else {
//            resultLen=numberLen+(tempLen/2)+1;
//        }


        if(result.length()<=rawLineLen) {
            Log.d(TAG, "formatArea:  sb.len ="+result.length()+" < "+rawLineLen);
            textView.setText(result);
            textView.setGravity(Gravity.RIGHT);
        } else{
            Log.d(TAG, "formatArea:  sb.lenth ="+result.length()+"+> "+rawLineLen);
            textView.setText(result);
        }



    }



    //将服务器第一次获取到的数据，写到文件json_data中，key为json
    public static void writeJsonToLocal(final ResultBean resultBean, final Context mContext) {

                String json = JsonUtil.beanToResultBean(resultBean);
                SharedPreferences.Editor editor = mContext.getSharedPreferences("json_data", mContext.MODE_PRIVATE).edit();
                editor.putString("json", json);
                editor.apply();
                Log.d(TAG, "writeJsonToLocal: 成功将FirstRequest的Json写入本地json_data文件中，key:json");


    }

    public static void writeJsonAdmin(final Admin admin, final Context mContext) {

        Log.d(TAG, "writeJsonAdmin: "+admin.getEmailPassword().toString());
        Gson gson = new Gson();
        String adminJson = gson.toJson(admin);
        SharedPreferences.Editor editor = mContext.getSharedPreferences("admin_inf", mContext.MODE_PRIVATE).edit();
        editor.putString("admin_inf", adminJson);
        editor.apply();
        Log.d(TAG, "writeJsonAdmin: 成功将Admin信息的Json写入本地admin_inf文件中，key:admin_inf");
    }

    public static Admin loadWriteAdmin(Context mContext) {

        SharedPreferences preferences = mContext.getSharedPreferences("admin_inf", mContext.MODE_PRIVATE);
        String json = preferences.getString("admin_inf", null);
        Admin admin=JsonUtil.jsonToAdmin(json);
        Log.d(TAG, "loadWriteAdmin: "+admin.getEmailPassword().toString());
        return admin;
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

    public static RequestCall submit(String requestURL) {
        PostFormBuilder postFormBuilder = OkHttpUtils.post();
        postFormBuilder.url(requestURL);
        return postFormBuilder.build();
    }
    public static RequestCall submit(String paramsKey, String paramsValues,String parmasKey,String v2,String p3,String v3, String p4 ,String v4,String requestURL) {
        PostFormBuilder postFormBuilder = OkHttpUtils.post();

        postFormBuilder.addParams(paramsKey, paramsValues);
        postFormBuilder.addParams(parmasKey, v2);
        postFormBuilder.addParams(p3, v3);
        postFormBuilder.addParams(p4 , v4);
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

    public static RequestCall submits(String FLAGW, String adminEmailVules, String ID, String repairID,
                                     String servserMan ,String serverAccountVules ,String AD,String adminAccount,
                                     String URL) {
        PostFormBuilder postFormBuilder = OkHttpUtils.post();
        postFormBuilder.addParams(FLAGW, adminEmailVules);
        postFormBuilder.addParams(ID, repairID);
        postFormBuilder.addParams(servserMan, serverAccountVules);
        postFormBuilder.addParams(AD, adminAccount);
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



    public static  byte[] desCrypto(byte[] datasource, String password) {
        try{
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(password.getBytes());
            //创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            //Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
            //用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            //现在，获取数据并加密
            //正式执行加密操作
            return cipher.doFinal(datasource);
        }catch(Throwable e){
            e.printStackTrace();
        }
        return null;
    }
    //base 64 加密
    public static String encryptStr(String strMing,String key) {
        byte[] byteMi = null;
        byte[] byteMing = null;
        String strMi = "";
        try {
            byteMing = strMing.getBytes("utf-8");

            byteMi = desCrypto(byteMing, key);

            strMi = new String(Base64.encode(byteMi,Base64.DEFAULT));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            byteMing = null;
            byteMi = null;
        }
        return strMi;
    }


    //des 解密
    public static byte[] decrypt(byte[] src, String password) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom random = new SecureRandom();
        // 创建一个DESKeySpec对象
        DESKeySpec desKey = new DESKeySpec(password.getBytes());
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);
        // 真正开始解密操作
        return cipher.doFinal(src);
    }


    //base64 加密
    public static String decryptStr(String strMi, String key) {
        byte[] byteMing = null;
        String strMing = "";
        try {

            byteMing = Base64.decode(strMi,Base64.DEFAULT);

            byteMing = decrypt(byteMing, key);
            strMing = new String(byteMing);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            byteMing = null;
        }
        return strMing;
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
        String salt=",>?<>(?>j(%&$#%U)(gh_^&*$^&*(_+";

        MessageDigest md=null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String first =salt+mingwen;
        byte[] bytValue, bytHash;
        byte[] bytValueTwo, bytHashTwo;
        bytValue =first.getBytes();
//		        System.out.println("bytValue :" +bytValue);
        md.update(bytValue);
        bytHash=md.digest();
//		        System.out.println("bytHash :" +bytHash);
        //清理
        // md.reset();

        bytValueTwo = mingwen.getBytes();
//		        System.out.println("bytValueTwo :" +bytValueTwo);
        md.update(bytValueTwo);
        bytHashTwo =md.digest();
//		        System.out.println("bytHashTwo :" +bytHashTwo);
        String sTemp = "";
        String sTwoTemp = "";




        sTemp=new  BigInteger(1, bytHash).toString(16);
        System.out.println("sTemp :" +sTemp);
        sTwoTemp= new   BigInteger(1, bytHashTwo).toString(16);
//		        System.out.println("sTwoTemp :" +sTwoTemp);
        String totalTemp = sTemp + sTwoTemp;
        return totalTemp;

//	         System.out.println("totalTemp :"+totalTemp.toLowerCase());
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


    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;

        String regx="^(0?(13[0-9]|15[012356789]|17[013678]|18[0-9]|14[57])[0-9]{8})|(400|800|0771)([0-9\\\\-]{7,10})|(([0-9]{4}|[0-9]{3})(-| )?)?([0-9]{7,8})((-| |转)*([0-9]{1,4}))?$";
        CharSequence inputStr = phoneNumber;

        Pattern pattern = Pattern.compile(regx);

        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }

        return isValid;



    }


}
