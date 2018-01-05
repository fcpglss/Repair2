package repair.com.repair;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import medusa.theone.waterdroplistview.view.WaterDropListView;
import model.Announcement;
import model.Response;
import model.ResultBean;
import okhttp3.Call;
import repari.com.adapter.AnnocmentListAdapter;

import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.Util;

import static constant.RequestUrl.ANNCOUCEMENT;
import static constant.RequestUrl.ANNCOUCEMENTMORE;
import static constant.RequestUrl.REFRESH_URL;

/**
 * Created by Administrator on 2016-11-29.
 */

public class AnnocementActivity extends AppCompatActivity implements WaterDropListView.IWaterDropListViewListener{
//



    private static boolean moreFlag = false;

    private static boolean ishasData=false;

    private static boolean isDelete=false;

    private static boolean isRefrush=false;
    private static boolean isMore=false;

    private ResultBean moreRes;

    private Response moreResponse;

    private static final int fenye=10;//一次读取10条和服务端一致
    private static int start = 0;
    private static int end = fenye;

    private List<Announcement> moreList = new ArrayList<>();

    private static final String TAG = "AnnocementActivity";
    Response annoceResponse;
    WaterDropListView lvAnnocment;
    List<Announcement> annoucementList;
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
                    closeDiag();
                    closeReflush();
                    Toast.makeText(AnnocementActivity.this, annoceResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    queryFromServer(ANNCOUCEMENT);
                    Log.d(TAG, "handleMessage: 2");
                    break;
                case 3:
                    closeDiag();
                    closeReflush();
                    setFirstApply(refrushRes);
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "handleMessage: 3");
                    break;
                case 4:
                    closeReflush();
                    Toast.makeText(AnnocementActivity.this, annoceResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "handleMessage: 4");
                    break;
                case 5:
                    moreList = moreRes.getAnnouncements();
                    setMoreApply(moreList);
                   // lvAnnocment.setSelection(start);
                    Log.d(TAG, "handleMessage: response" + moreResponse.isEnd());
                    moreFlag = moreResponse.isEnd();
                    Log.d(TAG, "handleMessage: " + moreFlag);
                    closeReflush();
                    break;
                case 6:
                    Toast.makeText(AnnocementActivity.this, annoceResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "handleMessage: 6");
                    closeReflush();
                    break;
                case 8:
                    closeReflush();
                    setFirstApply(refrushRes);
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "handleMessage: 5");
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
        //初始化
        annoucementList=new ArrayList<>();

        lvAnnocment = (WaterDropListView) findViewById(R.id.lv_annocement_list);
        lvAnnocment.setWaterDropListViewListener(this);
        lvAnnocment.setPullLoadEnable(true);

        svProgressHUD = new SVProgressHUD(this);
        svProgressHUD.showWithStatus("加载中");
        adapter = new AnnocmentListAdapter(annoucementList,this);
        lvAnnocment.setAdapter(adapter);
        lvAnnocment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                Intent intent = new Intent(AnnocementActivity.this,AnnonceDetailActivity.class);
//                Bundle bundle =new Bundle();
//                bundle.putSerializable("annoucement",adapter.list.get(position));
//                intent.putExtras(bundle);
//                startActivity(intent);
            }
        });
    }


    private void closeDiag() {
        if (svProgressHUD!=null&&svProgressHUD.isShowing()){
            svProgressHUD.dismiss();
        }
    }


    private void setMoreApply(List<Announcement> annoucement) {
        for (Announcement annouce : annoucement) {
           annoucementList.add(annouce);
        }
    }



    protected void loadData() {
        Log.d(TAG, "loadData: ");
        if(isFirst)
        {
            queryFromServer(ANNCOUCEMENT);
            Log.d(TAG, "第一次载入");
            isFirst=false;
        }

    }


    public void queryFromServer(String url) {
        Util.submit(url,this).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Response rp = new Response();
                rp.setErrorType(-1);
                rp.setError(true);
                rp.setErrorMessage("网络异常,尝试重连");
                annoceResponse = rp;
                Log.d(TAG, " onEnrror调用:" + e.getMessage());
                mhandler.sendEmptyMessage(2);
            }

            @Override
            public void onResponse(String responses, int id) {
                final String responseJson = responses.toString();
                Log.d(TAG, "onResponse: "+responseJson);
                annoceResponse = JsonUtil.jsonToResponse(responseJson);
                Log.d(TAG, "onResponse: annoceResponse "+annoceResponse.toString());
                if(annoceResponse==null)
                {
                    annoceResponse=new Response();
                    annoceResponse.setErrorMessage("服务器维护");
                    moreFlag=moreResponse.isEnd();
                    mhandler.sendEmptyMessage(4);
                    return;
                }
                ishasData=annoceResponse.isEnd();
                if(annoceResponse.getResultBean()==null||annoceResponse.getResultBean().getAnnouncements().size()<=0)
                {
                    annoceResponse.setErrorMessage("没有公告");
                    mhandler.sendEmptyMessage(4);
                    return ;
                }

                postMessage(annoceResponse);
            }
        });
    }





    private void postMessage(Response response) {

            refrushRes=response.getResultBean();

            if (refrushRes != null) {
               mhandler.sendEmptyMessage(3);
            }
            else
            {
                response.setErrorMessage("没有公告了");
                mhandler.sendEmptyMessage(4);
            }

    }



    private void setFirstApply(ResultBean resultBean) {

        if (annoucementList != null && annoucementList.size() > 0) {
            if (ishasData||isDelete) {
                annoucementList.clear();
                moreFlag=false;
                start=0;
                end=fenye;
                for (int i = 0; i < resultBean.getAnnouncements().size(); i++) {
                    annoucementList.add(resultBean.getAnnouncements().get(i));
                }
            }
            else
            {
                annoucementList.clear();
                for (int i = 0; i < resultBean.getAnnouncements().size(); i++) {
                    annoucementList.add(resultBean.getAnnouncements().get(i));
                }
            }
        }
        else
        {
            for (int i = 0; i < resultBean.getAnnouncements().size(); i++) {
                annoucementList.add(resultBean.getAnnouncements().get(i));
            }
        }
        Log.d(TAG, "setFirstApply: "+annoucementList.toString());

    }



    private void closeReflush()
    {
        if(isRefrush)
        {
            lvAnnocment.stopRefresh();
            isRefrush=false;
        }
        if(isMore)
        {
            lvAnnocment.stopLoadMore();
            isMore=false;
        }
    }



    @Override
    public void onRefresh() {
        isRefrush=true;
        //queryFromServer(ANNCOUCEMENT);
        String id="";
        if(annoucementList!=null&&annoucementList.size()>0)
        {

            id=annoucementList.get(annoucementList.size()-1).getCreate_at();
        }
        Util.submit("annoucement",id,REFRESH_URL)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Response re=new Response();
                        re.setErrorMessage("网络异常");
                        mhandler.sendEmptyMessage(4);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse: onrefresh ->"+response);
                        Response responses=JsonUtil.jsonToResponse(response);
                        isDelete=responses.isEnd();
                        refrushRes=responses.getResultBean();
                        mhandler.sendEmptyMessage(8);
                    }
                });

    }

    @Override
    public void onLoadMore() {
        Log.d(TAG, "onLoadMore: " + moreFlag);
        isMore=true;
        if (moreFlag||ishasData) {
            annoceResponse.setErrorMessage("下边没有数据了");
            mhandler.sendEmptyMessage(6);
            Log.d(TAG, "onFinish: moreFlag:" + moreFlag);
            return;
        } else {
            Log.d(TAG, "onFinish: moreFlag:" + moreFlag);
            start = start + fenye;
            end = end + fenye;
        }

        String request=ANNCOUCEMENTMORE + "?start=" + start + "&&end=" + end;
        Log.d(TAG, "onLoadMore: "+request);






        HttpUtil.sendHttpRequest(ANNCOUCEMENTMORE + "?start=" + start + "&&end=" + end, new HttpCallbackListener() {
            @Override
            public void onFinish(String responseString) {
                final String responseJson = responseString.toString();
                moreResponse = JsonUtil.jsonToResponse(responseJson);
                moreRes = moreResponse.getResultBean();
                if(moreResponse.getErrorType()==-3)
                {
                    moreFlag=moreResponse.isEnd();//服务器维护
                    annoceResponse.setErrorMessage("服务器维护");
                    mhandler.sendEmptyMessage(4);
                    return ;
                }

                if (moreRes.getAnnouncements()!=null&&moreRes.getAnnouncements().size()>0) {
                    //有数据
                    mhandler.sendEmptyMessage(5);
                }
                else
                {
                    moreResponse.setErrorMessage("下边没有更多数据了");
                    annoceResponse.setErrorMessage(moreResponse.getErrorMessage());
                    moreFlag=moreResponse.isEnd();
                    mhandler.sendEmptyMessage(6);
                }
            }

            @Override
            public void onError(Exception e) {
                Response rp = new Response();
                rp.setErrorType(-1);
                rp.setError(true);
                rp.setErrorMessage("网络异常");
                annoceResponse = rp;
                Log.d(TAG, " onEnrror调用:" + e.getMessage());
                mhandler.sendEmptyMessage(6);
            }
        });
    }

    @Override
    protected void onDestroy() {
        start = 0;
        end = fenye;
        moreFlag=false;
        ishasData=false;

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        isFirst=true;
        super.onStop();
    }
}
