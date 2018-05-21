package repair.com.repair;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import model.Announcement;
import model.Response;
import model.ResultBean;
import network.Api;
import okhttp3.Call;
import repari.com.adapter.AnnocmentAdapter;


import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.Util;


import static constant.RequestUrl.AnnouceList;
import static constant.RequestUrl.AnnouceLoadMore;
import static constant.RequestUrl.AnnouceRefresh;


/**
 * Created by Administrator on 2016-11-29.
 */

public class AnnocementActivity extends AppCompatActivity {
//


    private static boolean isHasData = false;


    private ResultBean res;

    private Response response;

    private static final int page = 10;//一次读取10条和服务端一致
    private static int start = 0;
    private static int end = page;

    private List<Announcement> list = new ArrayList<>();

    private static final String TAG = "AnnocementActivity";

    ListView listView;

    AnnocmentAdapter adapter;

    private boolean isFirst = true;


    SmartRefreshLayout smartRefreshLayout;


    private SweetAlertDialog svProgressHUD;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annocement);
        initViews();
        loadData();
    }

    protected void initViews() {
        //初始化

        listView = (ListView) findViewById(R.id.listView);
        smartRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        svProgressHUD = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
        svProgressHUD.setTitleText("加载中...");
        svProgressHUD.show();
        adapter = new AnnocmentAdapter(list, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }


    private void closeDiag() {
        if (svProgressHUD != null && svProgressHUD.isShowing()) {
            svProgressHUD.dismiss();
        }
    }


    protected void loadData() {
        if (isFirst) {
            requestServer(true);
            isFirst = false;
        }
    }

    public void requestServer(boolean isRefresh) {
        if (isRefresh) {
            start = 0;
            end = page;
            Api.announcementHome().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    smartRefreshLayout.finishRefresh();
                }

                @Override
                public void onResponse(String resStr, int id) {
                    Gson gson = new Gson();
                    response = gson.fromJson(resStr, Response.class);
                    closeDiag();
                    if (response != null) {
                        isHasData = response.isEnd();
                        res = response.getResultBean();
                        list.removeAll(list);
                        list.addAll(res.getAnnouncements());
                        adapter.notifyDataSetChanged();
                    }
                    smartRefreshLayout.finishRefresh();
                }
            });
        } else {
            Log.d(TAG, "requestServer: " + isHasData);
            if (isHasData) {
                smartRefreshLayout.finishLoadMore();
                return;
            } else {
                start = end;
                end = end + page;
                Api.announcementLoadMore(start, end).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError: " + e.getMessage());
                        smartRefreshLayout.finishLoadMore();
                    }

                    @Override
                    public void onResponse(String resStr, int id) {
                        Log.d(TAG, "onResponse: " + response);
                        Gson gson = new Gson();
                        response = gson.fromJson(resStr, Response.class);
                        if (response != null) {
                            isHasData = response.isEnd();
                            res = response.getResultBean();
                            list.addAll(res.getAnnouncements());
                            adapter.notifyDataSetChanged();
                        }
                        smartRefreshLayout.finishLoadMore();
                    }
                });
            }
        }
    }


    @Override
    protected void onDestroy() {
        start = 0;
        end = page;

        isHasData = false;

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        isFirst = true;
        super.onStop();
    }
}
