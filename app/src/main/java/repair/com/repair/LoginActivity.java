package repair.com.repair;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hsp on 2017/4/10.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

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
                if (etAdminName.getText().toString().isEmpty() || etAdminPassword.getText().toString().isEmpty()) {
                    return;
                } else {

                    saveUser();

                    Intent intent = new Intent(LoginActivity.this, AdminListActivity.class);
                    startActivity(intent);
                }
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



    private void EncoderByMd5(String password) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");

    }


}
