package application;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;

import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
            super.onCreate();

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                    .connectTimeout(20000L, TimeUnit.MILLISECONDS)
                    .readTimeout(20000L, TimeUnit.MILLISECONDS)
                    .writeTimeout(20000L,TimeUnit.MILLISECONDS)
                    //其他配置
                    .build();

            OkHttpUtils.initClient(okHttpClient);

      context = getApplicationContext();

    }

    public static Context getContext() {
        return context;
    }


}
