package repair.com.repair;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import model.Announcement;
import model.Test2;
import repari.com.adapter.ApplysAdapter;
import util.HttpUtil;

/**
 * Created by Administrator on 2016-12-8.
 */

public class TestImage extends AppCompatActivity {
    private static String JSON="";
    private ImageView img_test;


    private List<Announcement>  model =new ArrayList<>();
    private List<Announcement> announce=new ArrayList<>();
    private Handler mhandler= new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 1 :

                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_image_layout);
        bindServices();
        initView();
    }

    private void bindServices() {
        img_test= (ImageView) findViewById(R.id.img_test);
        Intent intent =new Intent(this,RuquestServer.class);
        startService(intent);
    }

    private void initView() {
        SessionManager.getmInstance().writeToServer("query_announcement");
    }
    private void ShowListView() {
        new AsyncTask<Void, Void, List<Announcement>>() {
            @Override
            protected List<Announcement> doInBackground(Void... params) {
                Gson gson = new GsonBuilder().create();
                Type listtype2 = new TypeToken<List<Test2>>() {
                }.getType();
               announce = gson.fromJson(JSON, listtype2);
                model=announce;
                Log.d("MainActivity", " 将JSON解析为mlist_Test2对象=" + announce.toString());
                for(Announcement an : model)
                {
                    try {
                        HttpURLConnection conn =(HttpURLConnection) new URL(an.getImage_url()).openConnection();
                        conn.setRequestMethod("GET");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return announce;
            }

            @Override
            protected void onPostExecute(List<Announcement> result) {
                super.onPostExecute(result);
                Log.d("MainActivity", " onPostExecute mlist_test.get(0).getA_name=" + announce.get(0).getImage_url());
            }
        }.execute();
        /**
         * 做缓存
         */


    }
    class MessgaeBroadcast extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            String json=intent.getStringExtra("message");
            Message ms= mhandler.obtainMessage();
            ms.obj=json;
            ms.what=1;
            mhandler.sendMessage(ms);

        }
    }
}
