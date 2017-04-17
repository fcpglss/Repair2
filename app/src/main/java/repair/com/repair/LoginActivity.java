package repair.com.repair;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.http.okhttp.callback.StringCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import application.MyApplication;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.Admin;
import model.Response;
import model.ResponseAdmin;
import okhttp3.Call;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.Util;

/**
 * Created by hsp on 2017/4/10.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

   private static final String LOGIN="http://192.168.31.201:8888/myserver2/AdminLogin";
    //  private static final String LOGIN="http://192.168.43.128:8888/myserver2/AdminLogin";

    private String account;

    private String password;

    private ResponseAdmin responseAdmin;

    private Admin admin;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "msg="+msg.what);
            switch (msg.what) {
                case 2:
                    Toast.makeText(LoginActivity.this, responseAdmin.getErrorMessage().toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(LoginActivity.this,admin.getAccount()+",欢迎回来!" , Toast.LENGTH_SHORT).show();
                    saveUser();
                    Intent intent = new Intent(LoginActivity.this, AdminListActivity.class);
                    startActivity(intent);
                    break;
                case 4:
                    Toast.makeText(LoginActivity.this,responseAdmin.getErrorMessage().toString() , Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };


    @BindView(R.id.et_admin_name)
    EditText etAdminName;
    @BindView(R.id.et_admin_password)
    EditText etAdminPassword;
    @BindView(R.id.btn_admin_login)
    Button btnAdminLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        bindView();


    }

    private void bindView() {
        btnAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account=etAdminName.getText().toString();
                password=Util.getMD5(etAdminPassword.getText().toString());
                upply();
            }
        });

    }

    private void saveUser() {
        String username = etAdminName.getText().toString();
        String password = etAdminPassword.getText().toString();
        Log.d(TAG, "saveUser: "+username);
        Log.d(TAG, "saveUser: "+password);
        SharedPreferences.Editor editor = getSharedPreferences("user",MODE_PRIVATE).edit();
        editor.putString("username",username);
        editor.putString("password",password);
        editor.apply();

    }


    private void upply()
    {
        if (account.isEmpty() || password.isEmpty()) {

            return;
        } else {

            Util.submit("account",account,"password",password,LOGIN).execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
//
                }

                @Override
                public void onResponse(String response, int id) {
                    Log.d(TAG, "onResponse: "+response);
                    if(response!=null)
                    {
                        responseAdmin= JsonUtil.jsonToResponseAdmin(response);
                        checkLoginResult();
                    }
                }
            });

        }
    }
    private void checkLoginResult()
    {
        if(responseAdmin!=null)
        {
            if(responseAdmin.isEnd()||responseAdmin.getErrorType()==1)
            {
                //联网成功，但是匹配错误
                mhandler.sendEmptyMessage(4);
            }
            else
            {
                //匹配成功
                admin=responseAdmin.getAdmin();
                mhandler.sendEmptyMessage(3);
            }
        }
        else
        {
            responseAdmin=new ResponseAdmin();
            responseAdmin.setEnd(false);
            responseAdmin.setErrorMessage("网络连接超时，请检查网络");
            //请求网络失败
            mhandler.sendEmptyMessage(2);
        }
    }



}
