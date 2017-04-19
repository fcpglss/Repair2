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

public class AdminListActivity extends AppCompatActivity implements WaterDropListView.IWaterDropListViewListener {
    private static final String TAG = "AdminListActivity";


    private static final String JSONFIRST = "http://192.168.31.201:8888/myserver2/AdminServerApply";
    // private static final String JSONFIRST = "http://192.168.43.128:8888/myserver2/AdminServerApply";

    SwitchButton switchButton;

    TextView tvImage;

    //private static final String JSONFIRST="http://192.168.43.128:8888/myserver2/AdminServerApply";

    private ResultBean adminRes;
    private Response adminResponse;
    private SVProgressHUD svProgressHUD;

    private boolean hasPic=true;

    private WaterDropListView lvAdmin;
    AdminListAdapter adminListAdapter;

    private ResultBean refrushRes;

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
                    updateView(0);
                    break;
                case 4:
                    lvAdmin.stopRefresh();
                    Toast.makeText(AdminListActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    lvAdmin.stopRefresh();
                    Toast.makeText(AdminListActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();;
                    updateView(0);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        init();
        svProgressHUD = new SVProgressHUD(this);
        svProgressHUD.showWithStatus("加载中");
        queryFromServer(JSONFIRST,0);

    }

    private void init() {

        switchButton = (SwitchButton) findViewById(R.id.switch_button);
        lvAdmin = (WaterDropListView) findViewById(R.id.lv_admin_list);
        lvAdmin.setWaterDropListViewListener(this);
        lvAdmin.setPullLoadEnable(true);
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    hasPic=true;
                    tvImage.setText("有图");
                } else {
                    hasPic=false;
                    tvImage.setText("无图");
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
                Log.d(TAG, "onFinish: " + responseJson);
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
                    mhandler.sendEmptyMessage(2);
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
            mhandler.sendEmptyMessage(2);
        }
        else
        {
            refrushRes = response.getResultBean();
            setFirstApply(refrushRes);
            if (adminRes != null) {
                if(isRefrush==1)
                {
                    Log.d(TAG, "刷新调用 queryFromServer请求成功：res有值，抛到到主线程更新UI,messages=3");
                    mhandler.sendEmptyMessage(5);
                }
                else
                {

                    mhandler.sendEmptyMessage(3);
                }
            }
            else
            {
                response.setErrorMessage("从服务器获取数据,出现异常");
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

    private void updateView(int isRefrush) {
        //从内存中的数据更新；
        if (adminListAdapter != null) {
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
                adminListAdapter = new AdminListAdapter(AdminListActivity.this, adminRes);
                if (adminListAdapter == null) {
                    Log.d(TAG, "updateView: adminListAdapter为null");
                }
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

    }

    private void setFirstApply(ResultBean resultBean) {
        if (adminRes != null && adminRes.getApplys().size() > 0) {
            for (int i = 0; i < resultBean.getApplys().size(); i++) {
                adminRes.getApplys().remove(i);
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
}
