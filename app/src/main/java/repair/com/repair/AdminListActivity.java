package repair.com.repair;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import application.MyApplication;
import butterknife.BindView;
import butterknife.ButterKnife;
import fragment.MainFragment;
import medusa.theone.waterdroplistview.view.WaterDropListView;
import medusa.theone.waterdroplistview.view.WaterDropListViewHeader;
import model.Admin;
import model.Apply;
import model.Response;
import model.ResponseAdmin;
import model.ResultBean;
import repari.com.adapter.AdminListAdapter;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.LocalImageHolderView;
import util.Util;
import util.WaterListViewListener;

import static repair.com.repair.MainActivity.FRIST_URL;
import static repair.com.repair.MainActivity.SENDMORE_URL;

/**
 * Created by hsp on 2017/4/7.
 */

public class AdminListActivity extends AppCompatActivity implements WaterDropListView.IWaterDropListViewListener {
    private static final String TAG = "AdminListActivity";


    //private static final String JSONFIRST = "http://192.168.31.201:8888/myserver2/AdminServerApply";
     private static final String JSONFIRST = "http://192.168.43.128:8888/myserver2/AdminServerApply";

  //  private static final String ADMINLIST_SENDMORE = "http://192.168.31.201:8888/myserver2/SendAdminListMore";
      private static final String ADMINLIST_SENDMORE = "http://192.168.43.128:8888/myserver2/SendAdminListMore";


    SwitchButton switchButton;

    TextView tvImage;

    //private static final String JSONFIRST="http://192.168.43.128:8888/myserver2/AdminServerApply";

    private ResultBean adminRes;
    private Response adminResponse;
    private SVProgressHUD svProgressHUD;

    private TextView tvHead;

    private ResultBean moreRes;

    private Response moreResponse;

    private static int start = 0;
    private static int end = 5;

    private List<Apply> moreList = new ArrayList<>();


    private Admin admin;

    public static  boolean hasPic=true;

    private WaterDropListView lvAdmin;
    AdminListAdapter adminListAdapter;

    private ResultBean refrushRes;

    private static boolean moreFlag = false;


    private static boolean isRefresh = false;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    Toast.makeText(AdminListActivity.this, adminResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(AdminListActivity.this, "第一次加载", Toast.LENGTH_SHORT).show();
                    updateView(0,hasPic);
                    break;
                case 4:
                    lvAdmin.stopRefresh();
                    Toast.makeText(AdminListActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    lvAdmin.stopRefresh();
                    Toast.makeText(AdminListActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();;
                    updateView(0,hasPic);
                    break;
                case 6:
                    Toast.makeText(AdminListActivity.this, "下边已经没有数据了", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "handleMessage: 6");
                    lvAdmin.stopLoadMore();
                    break;
                case 7:
                    moreList = moreRes.getApplys();
                    setMoreApply(moreList);
                    updateView(0,hasPic);
                  //  Util.writeJsonToLocal(res, MyApplication.getContext());
                    lvAdmin.setSelection(start);
                    Log.d(TAG, "handleMessage: response" + moreResponse.isEnd());
                    moreFlag = moreResponse.isEnd();
                    Log.d(TAG, "handleMessage: " + moreFlag);
                    lvAdmin.stopLoadMore();
                    break;
                case 8:
                    Toast.makeText(AdminListActivity.this, "网络异常,请检查网络", Toast.LENGTH_SHORT).show();
                    updateView(0,hasPic);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        admin=getAdmin();
        init();
        svProgressHUD = new SVProgressHUD(this);
        svProgressHUD.showWithStatus("加载中");
        queryFromServer(JSONFIRST,0);

    }


    private Admin getAdmin()
    {
        Admin admin =new Admin();
        admin= (Admin) getIntent().getSerializableExtra("admin");
        hasPic=getIntent().getBooleanExtra("hasPic",false);
        return admin;
    }



    private void init() {

        switchButton = (SwitchButton) findViewById(R.id.switch_button);
        tvHead= (TextView) findViewById(R.id.tv_head);
        tvHead.setText(admin.getAccount());
        tvImage = (TextView) findViewById(R.id.tv_image);
        lvAdmin = (WaterDropListView) findViewById(R.id.lv_admin_list);
        lvAdmin.setWaterDropListViewListener(this);
        lvAdmin.setPullLoadEnable(true);
        setSwitchButton();

    }

    private void setSwitchButton() {
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {

                if (isChecked) {
                    hasPic=true;
                    tvImage.setText("有图");
                    updateView(0,hasPic);
                } else {
                    hasPic=false;
                    tvImage.setText("无图");
                    updateView(0,hasPic);
                }
            }
        });
    }


    public void queryFromServer(String url, final int isRefrush) {
        HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String responseString) {
                //请求成功后获取到json
                final String responseJson = responseString.toString();
                //解析json获取到Response;
                adminResponse = JsonUtil.jsonToResponse(responseJson);
                postMessage(adminResponse, isRefrush);
            }

            @Override
            public void onError(Exception e) {
                Response rp = new Response();
                rp.setErrorType(-1);
                rp.setError(true);
                rp.setErrorMessage("网络异常，返回空值");
                adminResponse = rp;
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
            setFirstApply(refrushRes);
            if (adminRes != null) {
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
                if(isRefrush==1)
                {
                    mhandler.sendEmptyMessage(4);
                }
                else
                {
                    mhandler.sendEmptyMessage(2);
                }
            }
        }
    }

    private void setMoreApply(List<Apply> applyList) {
        int isSame=0;
        List<Apply> tempList=adminRes.getApplys();
        for (Apply apply : applyList) {
            for(int i=0;i<tempList.size();i++)
            {
                if(apply.getId().equals(tempList.get(i).getId()))
                {
                    isSame=1;
                    break;
                }
            }
            if(isSame!=1)
            {
                adminRes.getApplys().add(apply);
            }
            isSame=0;
        }
    }



    private void updateView(int isRefrush,boolean isLoadImages) {
        //从内存中的数据更新；
        if (adminListAdapter != null) {
            adminListAdapter.setIsLoadImages(isLoadImages);
            if (adminRes != null) {
                Log.d(TAG, "updateView: 内存中的adminListAdapter没有被销毁,adminRes还在内存中，直接更新listView");
                setView();
                if (svProgressHUD.isShowing()) {
                    svProgressHUD.dismiss();
                }
            } else {
                Log.d(TAG, "updateView: 内存中的adminListAdapter没有被销毁，但是adminRes已经被销毁了,需再次请求网络");
                queryFromServer(JSONFIRST,isRefrush);
            }
        }
        //内存中的applyAdapters已经被销毁，需重新创建一个
        else {
            Log.d(TAG, "updateView: 内存中的adminListAdapter已经被销毁,重新构造adminListAdapter");
            if (adminRes != null) {
                adminListAdapter = new AdminListAdapter(AdminListActivity.this, adminRes,hasPic);
                Log.d(TAG, "updateView: adminListAdapter不为null");
                setView();
                if (svProgressHUD.isShowing()) {
                    svProgressHUD.dismiss();
                }
            } else {
                queryFromServer(JSONFIRST,isRefrush);
            }

        }
    }

    private void setView() {
        adminListAdapter.notifyDataSetChanged();
        lvAdmin.setAdapter(adminListAdapter);
        Log.d(TAG, "setView: ");
        lvAdmin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String repairID = adminRes.getApplys().get(i - 1).getId();
                Intent intent = new Intent(AdminListActivity.this, AdminDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("repairId", repairID);
                intent.putExtra("isIntent", 1);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        queryFromServer(JSONFIRST,1);
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

        String request=ADMINLIST_SENDMORE + "?start=" + start + "&&end=" + end;
        Log.d(TAG, "onLoadMore: "+request);
        HttpUtil.sendHttpRequest(ADMINLIST_SENDMORE + "?start=" + start + "&&end=" + end, new HttpCallbackListener() {

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
                adminResponse = rp;
                Log.d(TAG, " onEnrror调用:" + e.getMessage());
                mhandler.sendEmptyMessage(2);
            }
        });

    }

    private void setFirstApply(ResultBean resultBean) {
        if (adminRes != null && adminRes.getApplys().size() > 0) {
            for (int i = 0; i < resultBean.getApplys().size(); i++) {
                if(adminRes.getApplys().size()>i)
                {
                    adminRes.getApplys().remove(i);
                    adminRes.getApplys().add(i, resultBean.getApplys().get(i));
                    continue;
                }
                  adminRes.getApplys().add(i, resultBean.getApplys().get(i));
            }
        } else {
            adminRes = resultBean;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            Log.d(TAG, "onKeyDown: 返回了main");
            this.finish();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        start = 0;
        end = 5;
        moreFlag=false;
        super.onDestroy();
    }
}
