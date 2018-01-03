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
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import medusa.theone.waterdroplistview.view.WaterDropListView;
import model.Admin;
import model.Apply;
import model.Response;
import model.ResultBean;
import okhttp3.Call;
import repari.com.adapter.AdminListAdapter;
import repari.com.adapter.SaveEmailAdapter;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.Util;

import static constant.RequestUrl.ADMINLIST_SENDMORE;
import static constant.RequestUrl.JSONFIRST;
import static constant.RequestUrl.REFRESH_URL;
import static repair.com.repair.MainActivity.windowHeigth;
import static repair.com.repair.MainActivity.windowWitch;

/**
 * Created by hsp on 2017/4/7.
 */

public class AdminListActivity extends AppCompatActivity implements WaterDropListView.IWaterDropListViewListener {
    private static final String TAG = "AdminListActivity";


    SwitchButton switchButton;

    TextView tvImage;

    private ResultBean adminRes;
    private Response adminResponse;
    private SVProgressHUD svProgressHUD;

    private TextView tvHead;

    public static String account;

    private Button btnEmail;

    private ResultBean moreRes;

    private Response moreResponse;

    private static int start = 0;

    private static final int fenye = 5;

    private static int end = fenye;


    private List<Apply> moreList = new ArrayList<>();

    private SweetAlertDialog sweetAlertDialog;
    private Admin admin;

    public static String onResumeValue = "init";

    public static boolean hasPic = false;

    private WaterDropListView lvAdmin;
    AdminListAdapter adminListAdapter;
    SaveEmailAdapter saveEmailAdapter;
    public static DialogPlus admindialogPlus;

    private ResultBean refrushRes;
    private Button button;

    private static boolean moreFlag = false;

    private static boolean ishasData = false;

    private static boolean isDelete = false;


    private static boolean isMore = false;


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
                    // Toast.makeText(AdminListActivity.this, "第一次加载", Toast.LENGTH_SHORT).show();
                    setFirstApply(refrushRes);
                    adminListAdapter = getBeanFromJson(adminRes, adminListAdapter);
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
                case 5:
                    closeReflush();
                    setFirstApply(refrushRes);
                    adminListAdapter = getBeanFromJson(adminRes, adminListAdapter);
                    Log.d(TAG, "handleMessage: 5");
                    updateView(0, hasPic);
                    break;

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        admin = Util.loadWriteAdmin(this);
        Log.d(TAG, "onCreate: " + admin.getPassword());
        Log.d(TAG, "onCreate: " + admin.getEmailPassword());

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

        if (onResumeValue.equals("OK")) {
            Log.d(TAG, "onResume: " + onResumeValue);
            onResumeValue = "init";
            queryFromServer(JSONFIRST, 0);
        }
        super.onResume();
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

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                admindialogPlus
                        .show();
            }
        });


        switchButton = (SwitchButton) findViewById(R.id.switch_button);
        tvHead = (TextView) findViewById(R.id.tv_head);
        tvHead.setText(admin.getName());
        button = (Button) findViewById(R.id.btn_zhuxiao);
        account = admin.getAccount();
        tvImage = (TextView) findViewById(R.id.tv_image);
        lvAdmin = (WaterDropListView) findViewById(R.id.lv_admin_list);
        lvAdmin.setWaterDropListViewListener(this);
        lvAdmin.setPullLoadEnable(true);

        setSwitchButton();
        setZhuxiao();
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

    private void setZhuxiao() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog = new SweetAlertDialog(AdminListActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(account + ",是否选择注销")
                        .setConfirmText("注销")
                        .setCancelText("取消")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.setConfirmText(null);
                                sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                sweetAlertDialog.setTitle("注销成功，退出登录");
                                start = 0;
                                end = fenye;
                                moreFlag = false;
                                hasPic = false;
                                sweetAlertDialog.dismiss();
                                Intent intent = new Intent(AdminListActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                sweetAlertDialog.show();
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
                ishasData = adminResponse.isEnd();
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


    private void closeReflush() {
        if (isRefresh) {
            lvAdmin.stopRefresh();
            isRefresh = false;
        }
        if (isMore) {
            lvAdmin.stopLoadMore();
            isMore = false;
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
            if (refrushRes != null) {
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
        Log.d(TAG, "updateView: start ");
        if (adminListAdapter != null) {
            adminListAdapter.setIsLoadImages(isLoadImages);
            Log.d(TAG, "updateView: 1");
            if (adminRes != null) {
                Log.d(TAG, "updateView: 内存中的adminListAdapter没有被销毁,adminRes还在内存中，直接更新listView");
                setView();
                closeDiag();
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
                closeDiag();
            } else {
                queryFromServer(JSONFIRST, isRefrush);
            }

        }
    }

    private void setView() {
        Log.d(TAG, "setView: start ");
        adminListAdapter.notifyDataSetChanged();
        lvAdmin.setAdapter(adminListAdapter);


        Log.d(TAG, "setView: end ");
    }

    private void closeDiag() {
        if (svProgressHUD != null && svProgressHUD.isShowing()) {
            svProgressHUD.dismiss();
        }
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
//        queryFromServer(JSONFIRST, 1);
        String id = "";
        if (adminRes != null && adminRes.getApplys() != null && adminRes.getApplys().size() > 0) {

            id = adminRes.getApplys().get(adminRes.getApplys().size() - 1).getId();
        }
        Util.submit("adminList", id, REFRESH_URL)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        adminResponse = new Response();
                        adminResponse.setErrorMessage("网络异常，刷新失败");
                        mhandler.sendEmptyMessage(2);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse: onrefresh ->" + response);
                        Response responses = JsonUtil.jsonToResponse(response);
                        isDelete = responses.isEnd();
                        refrushRes = responses.getResultBean();
                        mhandler.sendEmptyMessage(5);
                    }
                });
    }

    @Override
    public void onLoadMore() {
        isMore = true;
        if (moreFlag || ishasData) {
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
                if (moreResponse.getErrorType() == -3) {
                    moreFlag = moreResponse.isEnd();//服务器维护
                    mhandler.sendEmptyMessage(6);
                }

                if (moreRes.getApplys() != null && moreRes.getApplys().size() > 0) {
                    //有数据
                    mhandler.sendEmptyMessage(7);
                } else {
                    moreResponse.setErrorMessage("下边没有更多数据了");
                    adminResponse.setErrorMessage(moreResponse.getErrorMessage());
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
                adminResponse = rp;
                Log.d(TAG, " onEnrror调用:" + e.getMessage());
                mhandler.sendEmptyMessage(2);
            }
        });

    }

    private void setFirstApply(ResultBean resultBean) {
        Log.d(TAG, "setFirstApply: start");
        if (resultBean.getApplys() != null) {
            if (adminRes != null && adminRes.getApplys().size() > 0) {
                if (ishasData || isDelete) {
                    adminRes.getApplys().clear();
                    moreFlag = false;
                    start = 0;
                    end = fenye;
                    for (int i = 0; i < resultBean.getApplys().size(); i++) {
                        adminRes.getApplys().add(resultBean.getApplys().get(i));
                    }
                } else {
                    adminRes.getApplys().clear();

                    for (int i = 0; i < resultBean.getApplys().size(); i++) {
                        adminRes.getApplys().add(resultBean.getApplys().get(i));
                    }
                }
            } else {
                adminRes = resultBean;
            }
        } else {
            if (adminRes != null) {
                adminRes.getApplys().clear();
            }

        }
        Log.d(TAG, "setFirstApply: end");

    }

    public AdminListAdapter getBeanFromJson(ResultBean res, AdminListAdapter applysAdapters) {
        if (res == null) {
            return null;
        }

        if (applysAdapters == null) {
            applysAdapters = new AdminListAdapter(AdminListActivity.this, adminRes, hasPic);
        }
        return applysAdapters;
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Intent intent = new Intent(this, MainActivity.class);
//            hasPic=false;
//            startActivity(intent);
//            Log.d(TAG, "onKeyDown: 返回了main");
//            this.finish();
//        }
//        return true;
//    }



    /**
     * 第一种解决办法 通过监听keyUp
     *
     * @param keyCode
     * @param event
     * @return
     */
    //记录用户首次点击返回键的时间
    private long firstTime = 0;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(AdminListActivity.this, "再按一次退出管理页面", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                hasPic = false;
                startActivity(intent);
            }
        }

        return super.onKeyUp(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        start = 0;
        end = fenye;
        moreFlag = false;
        hasPic = false;
        ishasData = false;
        super.onDestroy();
    }

}
