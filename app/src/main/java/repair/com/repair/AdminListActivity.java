package repair.com.repair;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.suke.widget.SwitchButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import model.Response;
import model.ResultBean;
import repari.com.adapter.AdminListAdapter;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;

/**
 * Created by hsp on 2017/4/7.
 */

public class AdminListActivity extends AppCompatActivity {
    private static final String TAG = "AdminListActivity";


   // private static final String JSONFIRST = "http://192.168.31.201:8888/myserver2/AdminServerApply";
    private static final String JSONFIRST = "http://192.168.43.128:8888/myserver2/AdminServerApply";
    @BindView(R.id.switch_button)
    SwitchButton switchButton;
    @BindView(R.id.tv_image)
    TextView tvImage;

    //private static final String JSONFIRST="http://192.168.43.128:8888/myserver2/AdminServerApply";

    private ResultBean adminRes;
    private Response adminResponse;
    private ListView mlistView;
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    break;
                case 3:
                    Log.d(TAG, "handleMessage: " + (adminRes == null));
                    Log.d(TAG, "handleMessage: " + adminRes.toString());

                    adminListAdapter = new AdminListAdapter(AdminListActivity.this, adminRes);
                    lvAdmin.setAdapter(adminListAdapter);
                    adminListAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    Button btnChoose;
    Button btnSend;
    ListView lvAdmin;
    AdminListAdapter adminListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ButterKnife.bind(this);
        queryFromServer(JSONFIRST);

        btnSend = (Button) findViewById(R.id.btn_send);
        btnChoose = (Button) findViewById(R.id.btn_choose);
        lvAdmin = (ListView) findViewById(R.id.lv_admin_list);

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
                if (adminResponse != null) {
                    adminRes = adminResponse.getResultBean();
                }
                if (adminRes != null) {
                    Log.d(TAG, "queryFromServer请求成功：res有值，抛到到主线程更新UI,messages=3");
                    Log.d(TAG, "onFinish: " + responseJson);
                    mhandler.sendEmptyMessage(3);

                    //   Util.writeJsonToLocal(adminRes, MyApplication.getContext());//注意刷新和冲突
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

            }
        });
    }
}
