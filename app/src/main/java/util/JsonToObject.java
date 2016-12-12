package util;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import model.Test2;

/**
 * Created by Administrator on 2016-12-7.
 */

public class JsonToObject {

    private static List<Test2> list_applys=new ArrayList<>();
    public static List<Test2> getApplyList(String jsonString)
    {
        final String json=jsonString;
        if(json==""||json==null)
            return list_applys;
        new AsyncTask<Void, Void, List<Test2>>()
        {
            @Override
            protected List<Test2> doInBackground(Void... voids) {
                List<Test2> apply=null;
                Gson gson = new GsonBuilder().create();
                try
                {
                    Type listtype2 = new TypeToken<List<Test2>>() {}.getType();
                    apply=gson.fromJson(json,listtype2);
                }catch (Exception e)
                {
                    Log.d("Main","JsonToObeject����apply�쳣:"+e.getMessage().toString());
                    return list_applys;
                }
                return apply;
            }
            @Override
            protected void onPostExecute(List<Test2> apply) {
                super.onPostExecute(apply);
                list_applys=apply;
            }
        }.execute();
        return list_applys;
    }
}
