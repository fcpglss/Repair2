package repair.com.repair;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import application.MyApplication;
import butterknife.BindView;
import butterknife.ButterKnife;
import fragment.MainFragment;
import medusa.theone.waterdroplistview.view.WaterDropListView;
import medusa.theone.waterdroplistview.view.WaterDropListViewHeader;
import model.Response;
import model.ResultBean;
import repari.com.adapter.AdminListAdapter;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.LocalImageHolderView;
import util.Util;
import util.WaterListViewListener;

import static repair.com.repair.MainActivity.FRIST_URL;

/**
 * Created by hsp on 2017/4/7.
 */

public class AdminListActivity extends AppCompatActivity implements  WaterDropListView.IWaterDropListViewListener {
    private static final String TAG = "AdminListActivity";


    private static final String JSONFIRST = "http://192.168.31.201:8888/myserver2/AdminServerApply";
   // private static final String JSONFIRST = "http://192.168.43.128:8888/myserver2/AdminServerApply";

    SwitchButton switchButton;

    TextView tvImage;

    //private static final String JSONFIRST="http://192.168.43.128:8888/myserver2/AdminServerApply";

    private ResultBean adminRes;
    private Response adminResponse;
    private SVProgressHUD svProgressHUD;

    Button btnChoose;
    Button btnSend;
    private WaterDropListView  lvAdmin;
    AdminListAdapter adminListAdapter;

    private ResultBean refrushRes;

    private static boolean isRefresh = false;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    stopRefrushs();
                    Toast.makeText(AdminListActivity.this, "数据有误", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    stopRefrushs();
                    Toast.makeText(AdminListActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
                    updateView();
                    break;
                case 4:
                    stopRefrushs();
                    Toast.makeText(AdminListActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void stopRefrushs() {
        if(isRefresh)
        {
            isRefresh=false;
            Log.d(TAG, "handleMessage: 停止刷新按钮");
            lvAdmin.stopRefresh();
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        init();
        svProgressHUD = new SVProgressHUD(this);
        svProgressHUD.showWithStatus("加载中");
        queryFromServer(JSONFIRST);

    }

    private void init() {

        switchButton= (SwitchButton) findViewById(R.id.switch_button);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnChoose = (Button) findViewById(R.id.btn_choose);
        lvAdmin = (WaterDropListView) findViewById(R.id.lv_admin_list);
        lvAdmin.setWaterDropListViewListener(this);
        lvAdmin.setPullLoadEnable(true);
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked){
                    tvImage.setText("有图");
                }else {
                    tvImage.setText("无图");
                }
            }
        });
    }


    public void queryFromServer(String url) {
        HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String responseString) {
                //请求成功后获取到json
                final String responseJson = responseString.toString();
                //解析json获取到Response;
                adminResponse = JsonUtil.jsonToResponse(responseJson);
                refrushRes = adminResponse.getResultBean();
                setFirstApply(refrushRes);
                if (adminRes != null) {
                    Log.d(TAG, "queryFromServer请求成功：res有值，抛到到主线程更新UI,messages=3");
                    Log.d(TAG, "onFinish: " + responseJson);
                    mhandler.sendEmptyMessage(3);
                } else {
                    adminResponse.setErrorType(-2);
                    adminResponse.setError(false);
                    adminResponse.setErrorMessage("连接服务器成功，但返回的数据为空或是异常");
                    Log.d(TAG, "queryFromServer请求成功：但res没有值，抛到到主线程尝试从本地加载res更新UI,messages=4");
                    mhandler.sendEmptyMessage(2);
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
                mhandler.sendEmptyMessage(4);

            }
        });
    }
    private void updateView() {
        //从内存中的数据更新；
        if (adminListAdapter != null) {
            if (adminRes != null ) {
                Log.d(TAG, "updateView: 内存中的adminListAdapter没有被销毁,adminRes还在内存中，直接更新listView");
                setView();
                if (svProgressHUD.isShowing()){
                    svProgressHUD.dismiss();
                }
            } else {
                Log.d(TAG, "updateView: 内存中的adminListAdapter没有被销毁，但是adminRes已经被销毁了,需再次请求网络");
                queryFromServer(JSONFIRST);
            }
        }
        //内存中的applyAdapters已经被销毁，需重新创建一个
        else {
            Log.d(TAG, "updateView: 内存中的adminListAdapter已经被销毁,重新构造adminListAdapter");
            if(adminRes!=null)
            {
                adminListAdapter = new AdminListAdapter(AdminListActivity.this, adminRes);
                if(adminListAdapter==null)
                {
                    Log.d(TAG, "updateView: adminListAdapter为null");
                }
                Log.d(TAG, "updateView: adminListAdapter不为null");
                setView();
                if (svProgressHUD.isShowing()){
                    svProgressHUD.dismiss();
                }
            }
            else
            {
                queryFromServer(JSONFIRST);
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
                Log.d(TAG, "onItemClick: "+i);
                Toast.makeText(AdminListActivity.this, "ssssss", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onRefresh() {
        isRefresh=true;
        queryFromServer(JSONFIRST);
    }

    @Override
    public void onLoadMore() {

    }
    private void setFirstApply(ResultBean resultBean)
    {
        if(adminRes!=null&&adminRes.getApplys().size()>0)
        {
            for(int i=0;i<resultBean.getApplys().size();i++)
            {
                adminRes.getApplys().remove(i);
                adminRes.getApplys().add(i,resultBean.getApplys().get(i));
            }
        }
        else
        {
            adminRes=resultBean;
        }

    }
}
