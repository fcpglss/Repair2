package network;

import android.util.Log;

import com.zhangym.customview.VerificationCodeView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import okhttp3.Call;

import static constant.RequestUrl.Code;

/**
 * Created by 14221 on 2018/5/21.
 */

public class Api {

    private static final String TAG = "Api";

    public static RequestCall checkCode() {
        return OkHttpUtils.get()
                .url(Code)
                .build();
    }

    public static void changeCode(final VerificationCodeView verificationCodeView) {
        checkCode().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.d(TAG, "onError: 验证码" + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.d(TAG, "onResponse验证码: ");
                verificationCodeView.setVerificationText(response);
            }
        });
    }

}
