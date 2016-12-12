package util;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import model.Test2;

/**
 * Created by hsp on 2016/12/12.
 */

public class JsonUtil {


    public static List<Test2> JsonToApply(String jsonString, List<Test2> test2) {
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                Gson gson = new GsonBuilder().create();
                Log.d("MainActivity", " gson对象=" + gson.toString());
                Type listtype2 = new TypeToken<List<Test2>>() {
                }.getType();
                test2 = gson.fromJson(jsonString, listtype2);
                return test2;

            } catch (Exception e) {
                Log.d("Main", "loadJsonFromLocal解析本地json出错" + e.getMessage().toString());
            }

        }
        return test2;
    }
}
