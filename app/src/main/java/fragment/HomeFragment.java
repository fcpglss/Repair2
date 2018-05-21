package fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;



import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import application.MyApplication;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.functions.Consumer;
import model.Apply;
import model.Response;
import model.ResultBean;
import network.Api;
import okhttp3.Call;
import repair.com.repair.AnnocementActivity;
import repair.com.repair.DetailsActivity;
import repair.com.repair.R;
import repari.com.adapter.ApplysAdapter;
import util.JsonUtil;


/**
 * Created by hsp on 2016/11/27.
 */


public class HomeFragment extends LazyFragment2 {

    private static final String TAG = "HomeFragment";

    private static final int page = 10;
    private static int start = 0;
    private static int end = page;

    //后来是否还有数据
    private static boolean isHasData = false;
    //是否是第一次进入
    private static boolean isFirst = true;

    public ResultBean res = null;


    private ListView listView;

    private SmartRefreshLayout smartRefreshLayout;

    private ApplysAdapter applysAdapter;

    public Response response;


    private SweetAlertDialog svProgressHUD;



    private LinearLayout llArrIn;

    private List<Apply> applyList = new ArrayList<>();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayout() {
        return R.layout.homefragment;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            loadData();
        }
    }

    protected void loadData() {
        ////第一次进入就请求首页
        if (isFirst) {
            isFirst = false;

            svProgressHUD = new SweetAlertDialog(getActivity(),SweetAlertDialog.PROGRESS_TYPE);
            svProgressHUD.setTitleText("加载中...");
            svProgressHUD.show();
            requestServer(true);
        }

    }


    @Override
    protected void initViews(View view) {

        llArrIn = (LinearLayout) view.findViewById(R.id.ll_arr_in);
        //防抖
        RxView.clicks(llArrIn).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Intent intent = new Intent(getActivity(), AnnocementActivity.class);
                        startActivity(intent);
                    }
                });
        listView = (ListView) view.findViewById(R.id.listView);
        smartRefreshLayout = (SmartRefreshLayout) view.findViewById(R.id.refreshLayout);
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                requestServer(false);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                requestServer(true);
            }
        });
        applysAdapter = new ApplysAdapter(applyList, getActivity());
        listView.setAdapter(applysAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String repairID = applyList.get(i).getId();

                Intent intent = new Intent(MyApplication.getContext(), DetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("repairId", repairID);
                getActivity().startActivity(intent);
            }
        });



    }

    /**
     * 请求服务器数据
     */
    public void requestServer(boolean isRefresh) {
        if (isRefresh) {
            start = 0;
            end = page;
            Api.home().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    smartRefreshLayout.finishRefresh();
                }

                @Override
                public void onResponse(String resStr, int id) {
                  response = JsonUtil.jsonToResponse(resStr);
                    closeDiag();
                    if (response != null) {
                        isHasData = response.isEnd();
                        res = response.getResultBean();
                        applyList.removeAll(applyList);
                        applyList.addAll(res.getApplys());
                        applysAdapter.notifyDataSetChanged();
                    }
                    smartRefreshLayout.finishRefresh();
                }
            });
        } else {
            Log.d(TAG, "requestServer: " + isHasData);
            if (isHasData) {
                smartRefreshLayout.finishLoadMore();
//                Toast.makeText(getActivity(), "没有报修记录了", Toast.LENGTH_SHORT).show();
                return;
            } else {
                start = end;
                end = end + page;
                Api.loadMore(start, end).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError: " + e.getMessage());
                        smartRefreshLayout.finishLoadMore();
                    }

                    @Override
                    public void onResponse(String resStr, int id) {
                        Log.d(TAG, "onResponse: " + response);
                        response = JsonUtil.jsonToResponse(resStr);
                        if (response != null) {
                            isHasData = response.isEnd();
                            res = response.getResultBean();
                            applyList.addAll(res.getApplys());
                            applysAdapter.notifyDataSetChanged();
                        }
                        smartRefreshLayout.finishLoadMore();
                    }
                });
            }
        }
    }


    private void closeDiag() {
        if (svProgressHUD != null && svProgressHUD.isShowing()) {
            svProgressHUD.dismiss();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        isFirst = false;
        super.onStop();
    }

    @Override
    public void onDestroy() {
        isFirst = true;
        start = 0;
        end = page;
        isHasData = false;
        OkHttpUtils.getInstance().cancelTag(this);
        super.onDestroy();
    }


}

