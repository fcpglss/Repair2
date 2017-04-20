package repair.com.repair;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.svprogresshud.SVProgressHUD;

import java.util.ArrayList;
import java.util.List;

import application.MyApplication;
import fragment.MainFragment;
import imagehodler.ImageLoader;
import medusa.theone.waterdroplistview.view.WaterDropListView;
import model.Announcement;
import model.Apply;
import model.Response;
import model.ResultBean;
import repari.com.adapter.AdminListAdapter;
import repari.com.adapter.AnnocmentListAdapter;
import repari.com.adapter.ApplysAdapter;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.Util;
import util.WaterListViewListener;

import static android.os.Build.VERSION_CODES.M;
import static repair.com.repair.MainActivity.FRIST_URL;

/**
 * Created by Administrator on 2016-11-29.
 */

public class AnnocementActivity extends AppCompatActivity implements WaterDropListView.IWaterDropListViewListener{

    private final static String  ANNCOUCEMENT="http://192.168.31.201:8888/myserver2/SendAnnoucement?annoucementFirst";

    private final static String  ANNCOUCEMENTMORE="http://192.168.31.201:8888/myserver2/SendAnnoucement?annoucementMore";


    private static boolean moreFlag = false;

    private ResultBean moreRes;

    private Response moreResponse;

    private static int start = 0;
    private static int end = 5;

    private List<Apply> moreList = new ArrayList<>();

    private static final String TAG = "AnnocementActivity";
    Response annoceResponse;
    ResultBean annoceResult;
    WaterDropListView lvAnnocment;
    List<Announcement> list;
    AnnocmentListAdapter adapter;

    private boolean isFirst=true;

    private SVProgressHUD svProgressHUD;

    ResultBean  refrushRes;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    Toast.makeText(AnnocementActivity.this, annoceResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "handleMessage: 2");
                    break;
                case 3:
                    Toast.makeText(AnnocementActivity.this, "第一次加载", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "handleMessage: 3");
                    updateView(0);
                    break;
                case 4:
                    lvAnnocment.stopRefresh();
                    Toast.makeText(AnnocementActivity.this, annoceResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "handleMessage: 4");
                    break;
                case 5:
                    lvAnnocment.stopRefresh();
                    Toast.makeText(AnnocementActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();;
                    Log.d(TAG, "handleMessage: 5");
                    updateView(0);
                    break;
                case 6:
                    Toast.makeText(AnnocementActivity.this, "下边已经没有数据了", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "handleMessage: 6");
                    lvAnnocment.stopLoadMore();
                    break;
                case 7:
                    moreList = moreRes.getApplys();
                    setMoreApply(moreList);
                    updateView(0);
                      Util.writeAnnouceToLocal(annoceResult, MyApplication.getContext());
                    lvAnnocment.setSelection(start);
                    Log.d(TAG, "handleMessage: response" + moreResponse.isEnd());
                    moreFlag = moreResponse.isEnd();
                    Log.d(TAG, "handleMessage: " + moreFlag);
                    lvAnnocment.stopLoadMore();
                    break;
                case 8:
                    Toast.makeText(AnnocementActivity.this, "网络异常,请检查网络", Toast.LENGTH_SHORT).show();
                    updateView(0);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annocement);
        initViews();
        loadData();
    }

    protected void initViews() {
        lvAnnocment = (WaterDropListView) findViewById(R.id.lv_annocement_list);
        lvAnnocment.setWaterDropListViewListener(this);
        lvAnnocment.setPullLoadEnable(true);
    }

    private void setView()
    {
        adapter.notifyDataSetChanged();
        lvAnnocment.setAdapter(adapter);
        lvAnnocment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(AnnocementActivity.this,AnnonceDetailActivity.class);
                Bundle bundle =new Bundle();
                bundle.putSerializable("annoucement",adapter.list.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void updateView(int isRefrush) {
        //从内存中的数据更新；
        if (adapter != null) {
            if (annoceResult != null ) {
                Log.d(TAG, "updateView: 内存中的ApplyAdapters没有被销毁,fistRes还在内存中，直接更新Water,Conven两个View");
                setView();
                Log.d(TAG, "handleMessage: show");
                closeDiag();
            } else {
                Log.d(TAG, "updateView: 内存中的ApplyAdapters没有被销毁，但是firstRes已经被销毁了,需从本地读取firstRes");
                String json = Util.loadAnnouceFromLocal(this);
                annoceResult = JsonUtil.jsonToBean(json);
                setView();
                Log.d(TAG, "handleMessage: show");
                closeDiag();
            }
        }
        //内存中的applyAdapters已经被销毁，需重新创建一个
        else {
            Log.d(TAG, "updateView: 内存中的ApplyAdapters已经被销毁,重新构造ApplyAdapter,并且读本地数据更新View");
            String json = Util.loadFirstFromLocal(AnnocementActivity.this);
            annoceResult = JsonUtil.jsonToBean(json);
            adapter = getBeanFromJson(annoceResult, adapter);
            if (adapter == null) {
                Toast.makeText(this, "网络异常，本地也没有数据,重新请求", Toast.LENGTH_SHORT).show();
                queryFromServer(FRIST_URL,isRefrush);
            } else {
                Toast.makeText(this, "网络异常，使用本地数据", Toast.LENGTH_SHORT).show();
                setView();
                closeDiag();
            }
        }
    }


    private void closeDiag() {
        if (svProgressHUD.isShowing()){
            svProgressHUD.dismiss();
        }
    }

    public AnnocmentListAdapter getBeanFromJson(ResultBean res, AnnocmentListAdapter applysAdapter) {
        if (res == null) {
            return null;
        }

        if (applysAdapter == null) {
            applysAdapter = new AnnocmentListAdapter(res.getAnnouncements(), MyApplication.getContext());
        } else {
            applysAdapter.notifyDataSetChanged();
        }
        return applysAdapter;
    }


    private void setMoreApply(List<Apply> applyList) {
        for (Apply apply : applyList) {
            annoceResult.getApplys().add(apply);
        }
    }



    protected void loadData() {
        Log.d(TAG, "loadData: ");
        if(isFirst)
        {
            queryFromServer(ANNCOUCEMENT ,0);
            //new SVProgressHUD(getActivity()).showInfoWithStatus();
            svProgressHUD = new SVProgressHUD(this);
            svProgressHUD.showWithStatus("加载中");
            Log.d(TAG, "第一次载入");
            isFirst=false;
        }

    }

    public void queryFromServer(String url,final int isRefrush) {
        //isFirst=false;
        HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String responseString) {
                //请求成功后获取到json
                final String responseJson = responseString.toString();
                Log.d(TAG, "onFinish: "+responseJson);
                //解析json获取到Response;
                annoceResponse = JsonUtil.jsonToResponse(responseJson);
                Log.d(TAG, "onFinish: annoceResponse "+annoceResponse.toString());
                postMessage(annoceResponse,isRefrush);
            }

            @Override
            public void onError(Exception e) {
                Response rp = new Response();
                rp.setErrorType(-1);
                rp.setError(true);
                rp.setErrorMessage("网络异常，返回空值");
                annoceResponse = rp;
                Log.d(TAG, " onEnrror调用:" + e.getMessage());
                if(isRefrush==1)
                {
                    mhandler.sendEmptyMessage(4);
                }
                else
                {
                    mhandler.sendEmptyMessage(8);
                }
            }
        });
    }





    private void postMessage(Response response, int isRefrush) {
        Log.d(TAG, "postMessage:  -> 1 ");
        if(response==null)
        {
            response=new Response();
            response.setErrorType(-2);
            response.setError(false);
            response.setErrorMessage("连接服务器成功，但返回的数据为空或是异常");
            Log.d(TAG, "queryFromServer请求成功：但res没有值，抛到到主线程尝试从本地加载res更新UI,messages=4");
            if(isRefrush==1)
            {
                mhandler.sendEmptyMessage(4);
            }
            else
            {
                mhandler.sendEmptyMessage(2);
            }
        }
        else
        {
            refrushRes=response.getResultBean();
            Log.d(TAG, "postMessage: -> 4");
            setFirstApply(refrushRes);
            Util.writeAnnouceToLocal(annoceResult, MyApplication.getContext());
            if (annoceResult != null) {
                if(isRefrush==1)
                {
                    Log.d(TAG, "刷新调用 queryFromServer请求成功：res有值，抛到到主线程更新UI,messages=3");
                    mhandler.sendEmptyMessage(5);
                }
                else
                {
                    Log.d(TAG, "postMessage: 第一次进来调用,不需要停止刷新");
                    mhandler.sendEmptyMessage(3);
                }
            }
            else
            {
                Log.d(TAG, "postMessage:  -> 2");
                if(isRefrush==1)
                {
                    Log.d(TAG, "postMessage:  -> 3");
                    mhandler.sendEmptyMessage(4);

                }
                else
                {
                    Log.d(TAG, "postMessage:  -> 3");
                    mhandler.sendEmptyMessage(2);

                }
            }
        }
    }



    private void setFirstApply(ResultBean resultBean)
    {
        if(annoceResult!=null&&annoceResult.getAnnouncements().size()>0)
        {
            for(int i=0;i<resultBean.getAnnouncements().size();i++)
            {
                if(annoceResult.getAnnouncements().size()>i)
                {
                    annoceResult.getAnnouncements().remove(i);
                    annoceResult.getAnnouncements().add(i,resultBean.getAnnouncements().get(i));
                    continue;
                }
                annoceResult.getAnnouncements().add(i,resultBean.getAnnouncements().get(i));

            }
        }
        else
        {
            annoceResult=resultBean;
        }

    }



    @Override
    public void onRefresh() {
        queryFromServer(ANNCOUCEMENT,1);
    }

    @Override
    public void onLoadMore() {
        Log.d(TAG, "onLoadMore: " + moreFlag);
        if (moreFlag) {
            mhandler.sendEmptyMessage(6);
            Log.d(TAG, "onFinish: moreFlag:" + moreFlag);
            return;
        } else {
            Log.d(TAG, "onFinish: moreFlag:" + moreFlag);
            start = start + 5;
            end = end + 5;
        }

        String request=ANNCOUCEMENTMORE + "?start=" + start + "&&end=" + end;
        Log.d(TAG, "onLoadMore: "+request);
        HttpUtil.sendHttpRequest(ANNCOUCEMENTMORE + "?start=" + start + "&&end=" + end, new HttpCallbackListener() {

            @Override
            public void onFinish(String responseString) {
                //请求成功后获取到json
                final String responseJson = responseString.toString();
                //解析json获取到Response;
                moreResponse = JsonUtil.jsonToResponse(responseJson);
                moreRes = moreResponse.getResultBean();
                if (moreRes != null) {
                    mhandler.sendEmptyMessage(7);
//                    Util.writeJsonToLocal(res, MyApplication.getContext());
                } else {
                    moreResponse.setErrorType(-2);
                    moreResponse.setError(false);
                    moreResponse.setErrorMessage("连接服务器成功，但返回的数据为空或是异常");
                    Log.d(TAG, "queryFromServer请求成功：但res没有值，抛到到主线程尝试从本地加载res更新UI,messages=4");
                    mhandler.sendEmptyMessage(8);
                }
            }

            @Override
            public void onError(Exception e) {
                Response rp = new Response();
                rp.setErrorType(-1);
                rp.setError(true);
                rp.setErrorMessage("网络异常，返回空值");
                annoceResponse = rp;
                Log.d(TAG, " onEnrror调用:" + e.getMessage());
                mhandler.sendEmptyMessage(2);
            }
        });
    }

    @Override
    protected void onDestroy() {
        start = 0;
        end = 5;
        moreFlag=false;
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        isFirst=true;
        super.onStop();
    }
}
