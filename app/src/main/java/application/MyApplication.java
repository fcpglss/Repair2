package application;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;


public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }


}
