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

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import application.MyApplication;
import io.reactivex.functions.Consumer;
import medusa.theone.waterdroplistview.view.WaterDropListView;
import model.Apply;
import model.Response;
import model.ResultBean;
import repair.com.repair.AnnocementActivity;
import repair.com.repair.R;
import repari.com.adapter.ApplysAdapter;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.LocalImageHolderView;
import util.Util;
import util.WaterListViewListener;

import static constant.RequestUrl.FRIST_URL;
import static constant.RequestUrl.SENDMORE_URL;

/**
 * Created by hsp on 2016/11/27.
 */


public class MainFragment extends LazyFragment2 implements WaterDropListView.IWaterDropListViewListener {

    private static final String TAG = "MainFragment";

    private static final int fenye=10;
    private static int start = 0;
    private static int end = fenye;

    private static boolean moreFlag = false;
    private static boolean ishasData = false;

    private static final int ERROR = 2; //第一次网络不同标志

    private static final int SUCCESS = 3;//第一次请求网络成功标志

    private static final int NOTDATA = 4;//连接服务器成功，但是没有获取到数据

    private static boolean isRefresh = false;

    private static boolean isMore=false;

    private static boolean isFirst = true;

    private ConvenientBanner convenientBanner = null;

    public ResultBean res = null;

    public ResultBean moreRes = null;

    public ResultBean firstRes = null;

    public Response moreResponse = null;

    private WaterDropListView waterDropListView;

    private ApplysAdapter applysAdapter;

    private Response response;

    private List<Apply> moreList = new ArrayList<>();

    private SVProgressHUD svProgressHUD;

    private LinearLayout llArrIn;

    View view;
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    closeReflush();
                    Toast.makeText(MyApplication.getContext(), response.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "handleMessage: 2");
                    break;
                case 3:
                  //  Toast.makeText(getActivity(), , Toast.LENGTH_SHORT).show();
                    closeReflush();;
                    ishasData=response.isEnd();
                    applysAdapter = getBeanFromJson(res, applysAdapter);
                    Log.d(TAG, "handleMessage: 3");
                    updateView(0);
                    break;
                case 4:
               //     Toast.makeText(MyApplication.getContext(), "网络异常,尝试重连", Toast.LENGTH_SHORT).show();
                    queryFromServer(FRIST_URL,SUCCESS,0);
                    Log.d(TAG, "handleMessage: 2");
                    break;
                case 6:
                    Toast.makeText(MyApplication.getContext(), "下边已经没有数据了", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "handleMessage: 6");
                    closeReflush();
                    break;
                case 7:
                    moreList = moreRes.getApplys();
                    setMoreApply(moreList);
                    updateView(0);
                    Util.writeJsonToLocal(res, MyApplication.getContext());
//                    waterDropListView.setSelection(start);
                    waterDropListView.setSelection(end-fenye);

                    Log.d(TAG, "handleMessage: response" + moreResponse.isEnd());
                    moreFlag = moreResponse.isEnd();
                    Log.d(TAG, "handleMessage: " + moreFlag);
                    closeReflush();
                    break;
                case 8:
                    Toast.makeText(getActivity(), "网络异常,请检查网络", Toast.LENGTH_SHORT).show();
                    updateView(0);
                    closeReflush();
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
        if(isVisible)
        {
            loadData();
        }
    }

    protected void loadData() {
        Log.d(TAG, "loadData: ");
        /**
         * 还需要判断有没有网络,有网络,do 下面的判断,没有网络就从本地获取信息;
         */

        if(isFirst)
        {
            svProgressHUD = new SVProgressHUD(getActivity());
            svProgressHUD.showWithStatus("正在加载");
            queryFromServer(FRIST_URL, SUCCESS,0);

            Log.d(TAG, "第一次载入");
        }

    }


    @Override
    protected void initViews(View view) {
        convenientBanner = (ConvenientBanner) view.findViewById(R.id.loop);
        convenientBanner.startTurning(5000);
        waterDropListView = (WaterDropListView) view.findViewById(R.id.waterdrop_w);
        waterDropListView.setWaterDropListViewListener(MainFragment.this);
        waterDropListView.setPullLoadEnable(true);
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
    public void queryFromServer(String url, final int handlerType, final int isRefrush) {
        isFirst=false;
        HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String responseString) {
                //请求成功后获取到json
                final String responseJson = responseString.toString();
                Log.d(TAG, "onFinish: "+responseJson);
                //解析json获取到Response;

                response = JsonUtil.jsonToResponse(responseJson);
                postMessage(response,isRefrush);
            }

            @Override
            public void onError(Exception e) {
                Response rp = new Response();
                rp.setErrorType(-1);
                rp.setError(true);
                rp.setErrorMessage("连接不到服务器,请检查网络设置");
                response = rp;
                Log.d(TAG, " onEnrror调用:" + e.getMessage());
               mhandler.sendEmptyMessage(4);
            }
        });
    }

    private void postMessage(Response response, int isRefrush) {

        if(response==null)
        {
            response=new Response();
            response.setErrorType(-2);
            response.setError(false);
            response.setErrorMessage("服务器维护");
            Log.d(TAG, "queryFromServer请求成功：但res没有值，抛到到主线程尝试从本地加载res更新UI,messages=4");
           mhandler.sendEmptyMessage(2);
        }
        else
        {
            firstRes=response.getResultBean();
            setFirstApply(firstRes);

            if (res != null) {
                Util.writeJsonToLocal(res,MyApplication.getContext());
               mhandler.sendEmptyMessage(3);
            }
            else
            {
                response.setErrorMessage("没有获取到数据");
                mhandler.sendEmptyMessage(2);
            }
        }
    }


    //刷新waterListView
    public void onRefresh() {
        isRefresh = true;

        queryFromServer(FRIST_URL, SUCCESS,1);
    }

    //先尝试从内存中读数据，若没有数据则尝试从本地读
    private void updateView(int isRefrush) {
        //从内存中的数据更新；
        if (applysAdapter != null) {
            if (res != null && convenientBanner != null) {
                Log.d(TAG, "updateView: 内存中的ApplyAdapters没有被销毁,fistRes还在内存中，直接更新Water,Conven两个View");
                setView();
                Log.d(TAG, "handleMessage: show");
                closeDiag();
            } else {
                Log.d(TAG, "updateView: 内存中的ApplyAdapters没有被销毁，但是firstRes已经被销毁了,需从本地读取firstRes");
                String json = Util.loadFirstFromLocal(getActivity());
                res = JsonUtil.jsonToBean(json);
                applysAdapter.setList_Applys(res.getApplys());
                setView();
                Log.d(TAG, "handleMessage: show");
                closeDiag();
            }
        }
        //内存中的applyAdapters已经被销毁，需重新创建一个
        else {
            Log.d(TAG, "updateView: 内存中的ApplyAdapters已经被销毁,重新构造ApplyAdapter,并且读本地数据更新View");
            String json = Util.loadFirstFromLocal(getActivity());
            res = JsonUtil.jsonToBean(json);
            applysAdapter = getBeanFromJson(res, applysAdapter);
            if (applysAdapter == null) {
                Toast.makeText(getActivity(), "网络异常，本地也没有数据,重新请求", Toast.LENGTH_SHORT).show();
                queryFromServer(FRIST_URL, SUCCESS,isRefrush);
            } else {
                Toast.makeText(getActivity(), "网络异常，使用本地数据", Toast.LENGTH_SHORT).show();
                setView();
                Log.d(TAG, "handleMessage: show");
               closeDiag();
            }
        }
    }

    private void closeDiag() {
        if (svProgressHUD.isShowing()){
            svProgressHUD.dismiss();
        }
    }


    private void closeReflush()
    {
        if(isRefresh)
        {
            waterDropListView.stopRefresh();
            isRefresh=false;
        }
        if(isMore)
        {
            waterDropListView.stopLoadMore();
            isMore=false;
        }
    }


    //设置View属性
    private void setView() {
        convenientBanner.setPageIndicator(new int[]{R.drawable.dot_unselected, R.drawable.dot_selected});
        convenientBanner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
        List<Integer> stringList=new ArrayList<>();
        stringList.add(R.drawable.yejing);
        stringList.add(R.drawable.gonggao1);
        stringList.add(R.drawable.gonggao2);
            Log.d(TAG, "setPage之前");
            convenientBanner.setPages(
                    new CBViewHolderCreator<LocalImageHolderView>() {
                        @Override
                        public LocalImageHolderView createHolder() {
                            return new LocalImageHolderView(getActivity(),applysAdapter,res);
                        }
                    }, stringList);
        Log.d(TAG, "setView: 执行了");
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
        } else {
            applysAdapter.notifyDataSetChanged();
        }
        return applysAdapter;
    }

    //// FIXME: 2017/3/22
    @Override
    public void onLoadMore() {
        isMore=true;

        if (moreFlag||ishasData) {
            mhandler.sendEmptyMessage(6);
            Log.d(TAG, "onFinish: moreFlag:" + moreFlag);
            return;
        } else {
            Log.d(TAG, "onFinish: moreFlag:" + moreFlag);
            start = start + fenye;
            end = end + fenye;
        }
        HttpUtil.sendHttpRequest(SENDMORE_URL + "?start=" + start + "&&end=" + end, new HttpCallbackListener() {

            @Override
            public void onFinish(String responseString) {
                //请求成功后获取到json
                final String responseJson = responseString.toString();
                moreResponse = JsonUtil.jsonToResponse(responseJson);
                moreRes = moreResponse.getResultBean();
                if(moreResponse.getErrorType()==-3)
                {
                    moreFlag=moreResponse.isEnd();//服务器维护
                    mhandler.sendEmptyMessage(6);
                }

                if (moreRes.getApplys()!=null&&moreRes.getApplys().size()>0) {
                    //有数据
                    mhandler.sendEmptyMessage(7);
                }else
                {
                    moreResponse.setErrorMessage("下边没有更多数据了");
                    response.setErrorMessage(moreResponse.getErrorMessage());
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
                Log.d(TAG, " onEnrror调用:" + e.getMessage());
                mhandler.sendEmptyMessage(2);
            }
        });
    }

    private void setMoreApply(List<Apply> applyList) {
        for (Apply apply : applyList) {
            res.getApplys().add(apply);
        }
    }
    private void setFirstApply(ResultBean resultBean)
    {
        if(res!=null&&res.getApplys().size()>0)
        {
            for(int i=0;i<resultBean.getApplys().size();i++)
            {
                if(res.getApplys().size()>i)
                {
                    res.getApplys().remove(i);
                    res.getApplys().add(i,resultBean.getApplys().get(i));
                    continue;
                }

                res.getApplys().add(i,resultBean.getApplys().get(i));
            }
        }
        else
        {
            res=resultBean;
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Main_onResume");
    }

    @Override
    public void onStart() {

        super.onStart();
        Log.d(TAG, "Main_onStart");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        isFirst = false;
        super.onStop();
        Log.d(TAG, "Main_onStop");
    }

    @Override
    public void onDestroy() {
        isFirst = true;
        start = 0;
        end = fenye;
        moreFlag=false;
        ishasData=false;
        super.onDestroy();
        Log.d(TAG, "Main_onDestroy");
    }


}

