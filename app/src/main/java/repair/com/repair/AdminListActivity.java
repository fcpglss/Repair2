package repair.com.repair;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.orhanobut.dialogplus.DialogPlus;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import medusa.theone.waterdroplistview.view.WaterDropListView;
import model.Admin;
import model.Apply;
import model.Response;
import model.ResultBean;
import repari.com.adapter.AdminListAdapter;
import repari.com.adapter.SaveEmailAdapter;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.Util;

import static constant.RequestUrl.ADMINLIST_SENDMORE;
import static constant.RequestUrl.JSONFIRST;
import static repair.com.repair.MainActivity.windowHeigth;
import static repair.com.repair.MainActivity.windowWitch;

/**
 * Created by hsp on 2017/4/7.
 */

public class AdminListActivity extends AppCompatActivity implements WaterDropListView.IWaterDropListViewListener {
    private static final String TAG = "AdminListActivity";


    SwitchButton switchButton;

    TextView tvImage;

    //private static final String JSONFIRST="http://192.168.43.128:8888/myserver2/AdminServerApply";

    private ResultBean adminRes;
    private Response adminResponse;
    private SVProgressHUD svProgressHUD;

    private TextView tvHead;

    public static String account;

    private Button btnEmail;

    private ResultBean moreRes;

    private Response moreResponse;

    private static int start = 0;

    private static final int fenye =5;

    private static int end = fenye;


    private List<Apply> moreList = new ArrayList<>();


    private Admin admin;

    public static String onResumeValue = "Init";

    public static boolean hasPic = true;

    private WaterDropListView lvAdmin;
    AdminListAdapter adminListAdapter;
    SaveEmailAdapter saveEmailAdapter;
    public static DialogPlus admindialogPlus;

    private ResultBean refrushRes;

    private static boolean moreFlag = false;

    private static boolean ishasData = false;

    private static boolean isMore=false;


    private static boolean isRefresh = false;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    closeReflush();
                    Toast.makeText(AdminListActivity.this, adminResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    closeReflush();
                    ishasData=adminResponse.isEnd();
                    Toast.makeText(AdminListActivity.this, "第一次加载", Toast.LENGTH_SHORT).show();
                    updateView(0, hasPic);
                    break;
                case 6:
                    Toast.makeText(AdminListActivity.this, "下边已经没有数据了", Toast.LENGTH_SHORT).show();
                    closeReflush();
                    break;
                case 7:
                    moreList = moreRes.getApplys();
                    setMoreApply(moreList);
                    updateView(0, hasPic);
                    lvAdmin.setSelection(start);
                    Log.d(TAG, "handleMessage: response" + moreResponse.isEnd());
                    moreFlag = moreResponse.isEnd();
                    Log.d(TAG, "handleMessage: " + moreFlag);
                    closeReflush();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        admin = Util.loadWriteAdmin(this);
        Log.d(TAG, "onCreate: "+admin.getPassword());
        Log.d(TAG, "onCreate: "+admin.getEmailPassword());
        init();
        svProgressHUD = new SVProgressHUD(this);
        svProgressHUD.showWithStatus("加载中");


        queryFromServer(JSONFIRST, 0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {

       if (onResumeValue.equals("OK")){
           Log.d(TAG, "onResume: OK");
           onResumeValue = "Init";
           queryFromServer(JSONFIRST, 0);
       }

        super.onResume();


    }




    private Admin getAdmin() {
        Admin admin = new Admin();
        admin = (Admin) getIntent().getSerializableExtra("admin");
        hasPic = getIntent().getBooleanExtra("hasPic", false);
        return admin;
    }


    private void init() {

        saveEmailAdapter = new SaveEmailAdapter(this, R.layout.save_email);

        btnEmail = (Button) findViewById(R.id.btn_admin_email);
        admindialogPlus = DialogPlus.newDialog(AdminListActivity.this)
                .setGravity(Gravity.CENTER)
                .setContentWidth((int) (windowWitch / 1.1))
                .setContentHeight(windowHeigth / 3)
                .setAdapter(saveEmailAdapter)
                .create();
//                        .setOnBackPressListener()
//        saveEmailAdapter = new SaveEmailAdapter(this,R.layout.save_email);
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                admindialogPlus
                        .show();
            }
        });


        switchButton = (SwitchButton) findViewById(R.id.switch_button);
        tvHead = (TextView) findViewById(R.id.tv_head);
        tvHead.setText(admin.getAccount());
        account = admin.getAccount();
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
                    hasPic = true;
                    tvImage.setText("有图");
                    updateView(0, hasPic);
                } else {
                    hasPic = false;
                    tvImage.setText("无图");
                    updateView(0, hasPic);
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
                Log.d(TAG, "onFinish: responseJson: " + responseJson);
                //解析json获取到Response;
                adminResponse = JsonUtil.jsonToResponse(responseJson);
                postMessage(adminResponse, isRefrush);
            }

            @Override
            public void onError(Exception e) {
                Response rp = new Response();
                rp.setErrorType(-1);
                rp.setError(true);
                rp.setErrorMessage("连接不到服务器,请检查网络设置");
                adminResponse = rp;
                mhandler.sendEmptyMessage(2);
            }
        });
    }


    private void closeReflush()
    {
        if(isRefresh)
        {
            lvAdmin.stopRefresh();
            isRefresh=false;
        }
        if(isMore)
        {
            lvAdmin.stopLoadMore();
            isMore=false;
        }
    }




    private void postMessage(Response response, int isRefrush) {
        if (response == null) {
            response = new Response();
            response.setErrorType(-2);
            response.setError(false);
            response.setErrorMessage("服务器维护");
            Log.d(TAG, "queryFromServer请求成功：但res没有值，抛到到主线程尝试从本地加载res更新UI,messages=4");
          mhandler.sendEmptyMessage(2);
        } else {
            refrushRes = response.getResultBean();
            setFirstApply(refrushRes);
            if (adminRes != null) {
               mhandler.sendEmptyMessage(3);
            } else {
               mhandler.sendEmptyMessage(2);
            }
        }
    }

    private void setMoreApply(List<Apply> applyList) {

        for (Apply apply : applyList) {
            adminRes.getApplys().add(apply);
        }
    }


    private void updateView(int isRefrush, boolean isLoadImages) {
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
                queryFromServer(JSONFIRST, isRefrush);
            }
        }
        //内存中的applyAdapters已经被销毁，需重新创建一个
        else {
            Log.d(TAG, "updateView: 内存中的adminListAdapter已经被销毁,重新构造adminListAdapter");
            if (adminRes != null) {
                adminListAdapter = new AdminListAdapter(AdminListActivity.this, adminRes, hasPic);
                Log.d(TAG, "updateView: adminListAdapter不为null");
                setView();
                if (svProgressHUD.isShowing()) {
                    svProgressHUD.dismiss();
                }
            } else {
                queryFromServer(JSONFIRST, isRefrush);
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
        queryFromServer(JSONFIRST, 1);
    }

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

        String request = ADMINLIST_SENDMORE + "?start=" + start + "&&end=" + end;
        Log.d(TAG, "onLoadMore: " + request);
        HttpUtil.sendHttpRequest(ADMINLIST_SENDMORE + "?start=" + start + "&&end=" + end, new HttpCallbackListener() {

            @Override
            public void onFinish(String responseString) {
                //请求成功后获取到json
                final String responseJson = responseString.toString();
                //解析json获取到Response;
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
                    adminResponse.setErrorMessage(moreResponse.getErrorMessage());
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
                mhandler.sendEmptyMessage(2);
            }
        });

    }

    private void setFirstApply(ResultBean resultBean) {
        if (adminRes != null && adminRes.getApplys().size() > 0) {
            for (int i = 0; i < resultBean.getApplys().size(); i++) {
                if (adminRes.getApplys().size() > i) {
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
        end = fenye;
        moreFlag = false;
        ishasData=false;
        super.onDestroy();
    }
}
