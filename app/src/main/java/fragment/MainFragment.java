package fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.bigkoo.svprogresshud.SVProgressHUD;
import com.jakewharton.rxbinding2.view.RxView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import application.MyApplication;
import io.reactivex.functions.Consumer;
import medusa.theone.waterdroplistview.view.WaterDropListView;
import model.Apply;
import model.Response;
import model.ResultBean;
import okhttp3.Call;
import repair.com.repair.AnnocementActivity;
import repair.com.repair.R;
import repari.com.adapter.ApplysAdapter;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.Util;
import util.WaterListViewListener;

import static constant.RequestUrl.ApplyHomeList;
import static constant.RequestUrl.ApplyLoadMore;
import static constant.RequestUrl.ApplyRrefresh;


/**
 * Created by hsp on 2016/11/27.
 */


public class MainFragment extends LazyFragment2 implements WaterDropListView.IWaterDropListViewListener {

    private static final String TAG = "MainFragment";

    private static final int fenye = 10;
    private static int start = 0;
    private static int end = fenye;

    private static boolean moreFlag = false;
    private static boolean ishasData = false;


    private static boolean isRefresh = false;

    private static boolean isDelete = false;

    private static boolean isMore = false;

    private static boolean isFirst = true;


    public ResultBean res = null;

    public ResultBean moreRes = null;

    public ResultBean firstRes = null;

    public Response moreResponse = null;

    private WaterDropListView waterDropListView;

    private ApplysAdapter applysAdapter;

    public Response response;


    private List<Apply> moreList = new ArrayList<>();

    private SVProgressHUD svProgressHUD;

    private LinearLayout llArrIn;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    closeReflush();
                    Toast.makeText(MyApplication.getContext(), response.getErrorMessage(), Toast.LENGTH_SHORT).show();

                    break;
                case 3:
                    closeReflush();
                    setFirstApply(firstRes);
                    applysAdapter = getBeanFromJson(res, applysAdapter);
                    updateView(0);
                    break;
                case 4:
                    queryFromServer(ApplyHomeList, 0);

                    break;
                case 6:
                    Toast.makeText(MyApplication.getContext(), "下边已经没有数据了", Toast.LENGTH_SHORT).show();
                    closeReflush();
                    break;
                case 7:
                    moreList = moreRes.getApplys();
                    setMoreApply(moreList);
                    updateView(0);
                    Util.writeJsonToLocal(res, MyApplication.getContext());
                    waterDropListView.setSelection(end - fenye);
                    moreFlag = moreResponse.isEnd();
                    closeReflush();
                    break;
                case 8:
                    Toast.makeText(getActivity(), "网络异常,请检查网络", Toast.LENGTH_SHORT).show();
                    updateView(0);
                    closeReflush();
                    break;
                case 5:
                    closeReflush();
                    setFirstApply(firstRes);
                    applysAdapter = getBeanFromJson(res, applysAdapter);
                    updateView(0);
                    break;

            }
        }
    };

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
        return R.layout.fragment1;
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

        if (isFirst) {
            Log.d(TAG, "第一次载入");
            svProgressHUD = new SVProgressHUD(getActivity());
            svProgressHUD.showWithStatus("正在加载");
            queryFromServer(ApplyHomeList, 0);

        }

    }


    @Override
    protected void initViews(View view) {

        waterDropListView = (WaterDropListView) view.findViewById(R.id.waterdrop_w);
        waterDropListView.setWaterDropListViewListener(MainFragment.this);
        waterDropListView.setPullLoadEnable(true);
        waterDropListView.setAdapter(applysAdapter);

        llArrIn = (LinearLayout) view.findViewById(R.id.ll_arr_in);
        RxView.clicks(llArrIn).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Intent intent = new Intent(getActivity(), AnnocementActivity.class);
                        startActivity(intent);
                    }
                });

    }

    /**
     * 请求服务器数据
     */
    public void queryFromServer(String url, final int isRefrush) {
        isFirst = false;
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Response rp = new Response();
                        rp.setErrorType(-1);
                        rp.setError(true);
                        rp.setErrorMessage("连接不到服务器,请检查网络设置");
                        response = rp;
                        mhandler.sendEmptyMessage(4);
                    }

                    @Override
                    public void onResponse(String responseString, int id) {
                        //请求成功后获取到json
                        final String responseJson = responseString.toString();
                        response = JsonUtil.jsonToResponse(responseJson);
                        ishasData = response.isEnd();
                        postMessage(response, isRefrush);
                    }
                });

//        HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
//            @Override
//            public void onFinish(String responseString) {
//                //请求成功后获取到json
//                final String responseJson = responseString.toString();
//                Log.d(TAG, "onFinish: " + responseJson);
//                //解析json获取到Response;
//
//                response = JsonUtil.jsonToResponse(responseJson);
//                ishasData = response.isEnd();
//                postMessage(response, isRefrush);
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Response rp = new Response();
//                rp.setErrorType(-1);
//                rp.setError(true);
//                rp.setErrorMessage("连接不到服务器,请检查网络设置");
//                response = rp;
//                Log.d(TAG, " onEnrror调用:" + e.getMessage());
//                mhandler.sendEmptyMessage(4);
//            }
//        });
    }

    private void postMessage(Response response, int isRefrush) {

        if (response == null) {
            response = new Response();
            response.setErrorType(-2);
            response.setError(false);
            response.setErrorMessage("服务器维护");
            this.response = response;
            mhandler.sendEmptyMessage(2);
        } else {
            firstRes = response.getResultBean();
            if (firstRes != null) {
                Util.writeJsonToLocal(res, MyApplication.getContext());
                mhandler.sendEmptyMessage(3);
            } else {
                response.setErrorMessage("没有获取到数据");
                mhandler.sendEmptyMessage(2);
            }
        }
    }


    //刷新waterListView
    public void onRefresh() {
        isRefresh = true;
        String id = "";
        if (res != null && res.getApplys() != null && res.getApplys().size() > 0) {

            id = res.getApplys().get(res.getApplys().size() - 1).getId();
        }
        Util.submit("mainfragment", id, ApplyRrefresh)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Response responses = new Response();
                        responses.setErrorMessage("网络异常,刷新失败");
                        response = responses;
                        mhandler.sendEmptyMessage(2);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Response responses = JsonUtil.jsonToResponse(response);
                        isDelete = responses.isEnd();
                        firstRes = responses.getResultBean();
                        mhandler.sendEmptyMessage(5);
                    }
                });

    }

    //先尝试从内存中读数据，若没有数据则尝试从本地读
    private void updateView(int isRefrush) {
        //从内存中的数据更新；
        if (applysAdapter != null) {
            if (res != null) {
                setView();
                closeDiag();
            } else {
                String json = Util.loadFirstFromLocal(getActivity());
                res = JsonUtil.jsonToBean(json);
                applysAdapter.setList_Applys(res.getApplys());
                setView();
                closeDiag();
            }
        }
        //内存中的applyAdapters已经被销毁，需重新创建一个
        else {
            String json = Util.loadFirstFromLocal(getActivity());
            res = JsonUtil.jsonToBean(json);
            applysAdapter = getBeanFromJson(res, applysAdapter);
            if (applysAdapter == null) {
                queryFromServer(ApplyHomeList, isRefrush);
            } else {
                Toast.makeText(getActivity(), "网络异常，使用本地数据", Toast.LENGTH_SHORT).show();
                setView();
                closeDiag();
            }
        }
    }

    private void closeDiag() {
        if (svProgressHUD != null && svProgressHUD.isShowing()) {
            svProgressHUD.dismiss();
        }
    }


    private void closeReflush() {
        if (isRefresh) {
            waterDropListView.stopRefresh();
            isRefresh = false;
        }
        if (isMore) {
            waterDropListView.stopLoadMore();
            isMore = false;
        }
    }


    //设置View属性
    private void setView() {
        applysAdapter.notifyDataSetChanged();
        waterDropListView.setAdapter(applysAdapter);
        waterDropListView.setOnItemClickListener(new WaterListViewListener(MyApplication.getContext(), res));
    }


    public ApplysAdapter getBeanFromJson(ResultBean res, ApplysAdapter applysAdapter) {
        if (res == null) {
            return null;
        }

        if (applysAdapter == null) {
            applysAdapter = new ApplysAdapter(res, MyApplication.getContext());
        }
        return applysAdapter;
    }

    //// FIXME: 2017/3/22
    @Override
    public void onLoadMore() {
        isMore = true;
        if (ishasData || moreFlag) {
            mhandler.sendEmptyMessage(6);
            return;
        } else {
            start = start + fenye;
            end = end + fenye;
        }
        HttpUtil.sendHttpRequest(ApplyLoadMore + "?start=" + start + "&&end=" + end, new HttpCallbackListener() {

            @Override
            public void onFinish(String responseString) {
                //请求成功后获取到json
                final String responseJson = responseString.toString();
                moreResponse = JsonUtil.jsonToResponse(responseJson);
                moreRes = moreResponse.getResultBean();
                if (moreResponse.getErrorType() == -3) {
                    moreFlag = moreResponse.isEnd();//服务器维护
                    mhandler.sendEmptyMessage(6);
                }

                if (moreRes.getApplys() != null && moreRes.getApplys().size() > 0) {
                    //有数据
                    mhandler.sendEmptyMessage(7);
                } else {
                    moreResponse.setErrorMessage("下边没有更多数据了");
                    response.setErrorMessage(moreResponse.getErrorMessage());
                    moreFlag = moreResponse.isEnd();
                    mhandler.sendEmptyMessage(2);
                }
            }

            @Override
            public void onError(Exception e) {
                Response rp = new Response();
                rp.setErrorType(-1);
                rp.setError(true);
                rp.setErrorMessage("网络异常，返回空值");
                response = rp;
                mhandler.sendEmptyMessage(2);
            }
        });
    }

    private void setMoreApply(List<Apply> applyList) {
        for (Apply apply : applyList) {
            res.getApplys().add(apply);
        }
    }

    private void setFirstApply(ResultBean resultBean) {
        if (res != null && res.getApplys().size() > 0) {
            if (ishasData || isDelete) {
                res.getApplys().clear();
                moreFlag = false;
                start = 0;
                end = fenye;
                for (int i = 0; i < firstRes.getApplys().size(); i++) {
                    res.getApplys().add(firstRes.getApplys().get(i));
                }
            } else {
                res.getApplys().clear();
                for (int i = 0; i < resultBean.getApplys().size(); i++) {
                    res.getApplys().add(firstRes.getApplys().get(i));
                }
            }
        } else {
            res = resultBean;
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
        end = fenye;
        moreFlag = false;
        ishasData = false;
        OkHttpUtils.getInstance().cancelTag(this);
        super.onDestroy();
    }


}

