package network;

import android.content.Context;
import android.util.Log;


import com.zhangym.customview.VerificationCodeView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.util.List;


import okhttp3.Call;
import util.AESUtil;

import static constant.RequestUrl.AnnouceList;
import static constant.RequestUrl.AnnouceLoadMore;
import static constant.RequestUrl.AnnouceRefresh;
import static constant.RequestUrl.ApplyAppraise;
import static constant.RequestUrl.ApplyDetail;
import static constant.RequestUrl.ApplyHomeList;
import static constant.RequestUrl.ApplyInsert;
import static constant.RequestUrl.ApplyLoadMore;
import static constant.RequestUrl.ApplyNoImgInsert;
import static constant.RequestUrl.ApplyNoImgUpdate;
import static constant.RequestUrl.ApplyPassword;
import static constant.RequestUrl.ApplySearch;
import static constant.RequestUrl.ApplySearchMore;
import static constant.RequestUrl.ApplyUpdate;
import static constant.RequestUrl.Code;

/**
 * Created by 14221 on 2018/5/21.
 */

public class Api {

    private static final String TAG = "Api";

    //首页接口
    public static RequestCall home() {
        return OkHttpUtils.get()
                .url(ApplyHomeList)
                .build();
    }

    //刷新接口
    public static RequestCall refresh() {
        return OkHttpUtils.get()
                .url("")
                .build();
    }

    //加载更多接口
    public static RequestCall loadMore(int start, int end) {
        String strStart = String.valueOf(start);
        String strEnd = String.valueOf(end);

        return OkHttpUtils.get()
                .addParams("start", strStart)
                .addParams("end", strEnd)
                .url(ApplyLoadMore)
                .build();
    }

    /**
     * 使用okHttp框架post请求网络,若有图片文件则使用requestImgURL,
     * 若没有图片则使用requestURL
     *
     * @param files 文件集合
     * @return 用于回调onResponse和onError方法
     */
    public static RequestCall submit(String jsonValues, String codeValue, List<File> files) {
        PostFormBuilder postFormBuilder = OkHttpUtils.post();

        for (int i = 0; i < files.size(); i++) {
            postFormBuilder.addFile("fileImg", "file" + i + ".jpg", files.get(i));
        }

        postFormBuilder.addParams("apply", jsonValues);
        postFormBuilder.addParams("code", codeValue);
        Log.d(TAG, "submit: " + codeValue);
        if (files.size() > 0) {
            postFormBuilder.url(ApplyInsert);
        } else {
            postFormBuilder.url(ApplyNoImgInsert);
        }
        return postFormBuilder.build();
    }


    //修改
    public static RequestCall change(String jsonValues, String codeValue, List<File> files) {
        PostFormBuilder postFormBuilder = OkHttpUtils.post();

        for (int i = 0; i < files.size(); i++) {
            postFormBuilder.addFile("fileImg", "file" + i + ".jpg", files.get(i));
        }

        postFormBuilder.addParams("update", jsonValues);
        postFormBuilder.addParams("code", codeValue);
        Log.d(TAG, "submit: " + codeValue);
        if (files.size() > 0) {
            postFormBuilder.url(ApplyUpdate);
        } else {
            postFormBuilder.url(ApplyNoImgUpdate);
        }
        return postFormBuilder.build();
    }


    //搜索我的报修记录
    public static RequestCall search(String phone, String name) {
        return OkHttpUtils.post()
                .addParams("phone", phone)
                .addParams("name", name)
                .url(ApplySearch)
                .build();
    }

    //搜索更多更多接口
    public static RequestCall loadMore(String phone, String name, int start, int end, Context mContext) {
        String strStart = String.valueOf(start);
        String strEnd = String.valueOf(end);

        return OkHttpUtils.get().
                url(ApplySearchMore).
                addParams("start", strStart).
                addParams("end", strEnd).
                addParams("phone", phone).
                addParams("name", name)
                .tag(mContext)
                .build();
    }


    public static RequestCall detail(String applyId, Context context) {
        return OkHttpUtils.get().
                url(ApplyDetail).
                addParams("detail", applyId)
                .tag(context)
                .build();
    }


    //验证码接口
    public static RequestCall checkCode() {
        return OkHttpUtils.get()
                .url(Code)
                .build();
    }

    //将验证码字符串显示到验证码控件上
    public static void changeCode(final VerificationCodeView verificationCodeView) {
        checkCode().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.d(TAG, "onError: 验证码" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.d(TAG, "onResponse验证码: " + response);
                try {
                    String code = AESUtil.decode(response);
                    verificationCodeView.setVerificationText(code);
                } catch (Exception e) {
                    verificationCodeView.setVerificationText("错误码");
                }

            }
        });
    }

    //公告首页
    public static RequestCall announcementHome() {
        return OkHttpUtils.post()
                .url(AnnouceList)
                .build();
    }

    //公告刷新
    public static RequestCall announcementRreshFresh() {
        return OkHttpUtils.get()
                .url(AnnouceRefresh)
                .build();
    }

    //公告加载更多
    public static RequestCall announcementLoadMore(int start, int end) {
        String strStart = String.valueOf(start);
        String strEnd = String.valueOf(end);

        return OkHttpUtils.get()
                .addParams("start", strStart)
                .addParams("end", strEnd)
                .url(AnnouceLoadMore)
                .build();
    }


    //评价
    public static RequestCall appraise(String json) {
        return OkHttpUtils.post()
                .addParams("appraise", json)
                .url(ApplyAppraise)
                .build();
    }


    //报修密码
    public static RequestCall checkPassword(String password, String id) {
        return OkHttpUtils.post()
                .addParams("password", password)
                .addParams("ID", id)
                .url(ApplyPassword)
                .build();
    }

}
