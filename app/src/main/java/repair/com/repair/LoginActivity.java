package repair.com.repair;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.functions.Consumer;
import model.Admin;
import model.ResponseAdmin;
import okhttp3.Call;
import util.JsonUtil;
import util.Util;

import static constant.RequestUrl.LOGIN;

/**
 * Created by hsp on 2017/4/10.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";


    private String account;

    private String password;

    private ResponseAdmin responseAdmin;

    private Admin admin;
    private SweetAlertDialog sweetAlertDialog;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "msg=" + msg.what);
            switch (msg.what) {
                case 2:
                    sweetAlertDialog
                            .setTitleText("服务器维护")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
//                    Toast.makeText(LoginActivity.this, responseAdmin.getErrorMessage().toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(LoginActivity.this, admin.getAccount() + ",欢迎回来!", Toast.LENGTH_SHORT).show();
                    saveUser();
                    Intent intent = new Intent(LoginActivity.this, AdminListActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("admin", admin);
//                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case 4:
                    sweetAlertDialog
                            .setTitleText("账户或密码错误")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);

//                    Toast.makeText(LoginActivity.this,responseAdmin.getErrorMessage().toString() , Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    sweetAlertDialog
                            .setTitleText("网络异常")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
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

        RxView.clicks(btnAdminLogin).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {

                        sweetAlertDialog = new SweetAlertDialog(LoginActivity.this)
                                .setTitleText("登陆中");
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        sweetAlertDialog.show();
                        account = etAdminName.getText().toString();
                        password = Util.getMD5(etAdminPassword.getText().toString());
                        Log.d(TAG, "onClick: account :" + account);
                        Log.d(TAG, "onClick: password :" + password);
                        upply();
                    }
                });
        Util.setOnClickBackgroundColor(btnAdminLogin);

    }

    private void saveUser() {
        String username = etAdminName.getText().toString();
        String password = etAdminPassword.getText().toString();
        Log.d(TAG, "saveUser: " + username);
        Log.d(TAG, "saveUser: " + password);
        Util.writeJsonAdmin(admin, LoginActivity.this);
    }


    private void upply() {
        if (account.isEmpty() || password.isEmpty()) {

            sweetAlertDialog.setTitleText("请输入账号密码")
                    .setConfirmText("OK")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                        }
                    }).changeAlertType(SweetAlertDialog.ERROR_TYPE);
        } else {

            Util.submit("account", account, "password", password, LOGIN).execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {

                    mhandler.sendEmptyMessage(5);
                }

                @Override
                public void onResponse(String response, int id) {
                    Log.d(TAG, "onResponse: " + response);
                    if (response != null) {
                        responseAdmin = JsonUtil.jsonToResponseAdmin(response);
                        checkLoginResult();
                    }
                }
            });

        }
    }

    private void checkLoginResult() {
        if (responseAdmin != null) {
            if (responseAdmin.isEnd() || responseAdmin.getErrorType() == 1) {
                //联网成功，但是匹配错误
                mhandler.sendEmptyMessage(4);
            } else {
                //匹配成功
                admin = responseAdmin.getAdmin();
                mhandler.sendEmptyMessage(3);
            }
        } else {
            responseAdmin = new ResponseAdmin();
            responseAdmin.setEnd(false);
            responseAdmin.setErrorMessage("账户或者密码错误");
            //请求网络失败
            mhandler.sendEmptyMessage(4);
        }
    }
}
