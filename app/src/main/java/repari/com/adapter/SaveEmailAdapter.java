package repari.com.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.orhanobut.dialogplus.DialogPlus;
import com.zhy.http.okhttp.callback.StringCallback;

import constant.RequestUrl;
import model.Apply;
import okhttp3.Call;
import repair.com.repair.R;
import util.Util;

import static repair.com.repair.AdminListActivity.admindialogPlus;


/**
 * Created by hsp on 2017/3/20.
 */

public class SaveEmailAdapter extends BaseAdapter {
    LayoutInflater layoutInflater;
    Apply apply;
    Context context;
    int position;
    int layout;
    SVProgressHUD svProgressHUD;
//    DialogPlus dialogPlus;

    String email;
    String emailPassword;
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    closeDiag();
                    break;
                case 2:
                    Toast.makeText(context, "账号密码不匹配", Toast.LENGTH_SHORT).show();
                    closeDiag();
                    break;
                case 3:
                    Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
                    closeDiag();
                    break;
            }
        }
    };

    public SaveEmailAdapter(Context context, int layout, DialogPlus dialogPlus) {
        this.layout = layout;
        this.context = context;

        layoutInflater = LayoutInflater.from(context);
    }

    public SaveEmailAdapter(Context context, int layout) {
        this.context = context;
        this.layout = layout;
        svProgressHUD = new SVProgressHUD(context);

        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        View view = convertView;

        if (null == view) {
            view = layoutInflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.etEmail = (EditText) view.findViewById(R.id.et_admin_email);
            viewHolder.etPassword = (EditText) view.findViewById(R.id.et_admin_email_password);
            viewHolder.btnCancel = (Button) view.findViewById(R.id.btn_admin_save_cancel);
            viewHolder.btnSaveEmail = (Button) view.findViewById(R.id.btn_admin_save_email);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.btnSaveEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                svProgressHUD.showWithStatus("验证中");
                svProgressHUD.show();
                //获取账号密码
                String email = viewHolder.etEmail.getText().toString();
                String password = viewHolder.etPassword.getText().toString();
                //加密密码
                Util.submit("adminEmail", email, "password", password, RequestUrl.ADMIN_EMAIL_CHECK).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                        mhandler.sendEmptyMessage(3);
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        if ("OK".equals(response)) {
                            //存到本地
                            SharedPreferences.Editor editor = context.getSharedPreferences("adminEmail", Context.MODE_PRIVATE).edit();
                            editor.putString("email", viewHolder.etEmail.getText().toString());
                            editor.putString("password", Util.getMD5(viewHolder.etPassword.getText().toString()));
                            editor.apply();
                            mhandler.sendEmptyMessage(1);
                        } else {
                            mhandler.sendEmptyMessage(2);
                        }
                    }
                });

            }
        });
        viewHolder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.etPassword.setText("");
                closeDiag();
            }
        });


        return view;
    }

    private void closeDiag() {
        if (svProgressHUD.isShowing()) {
            svProgressHUD.dismiss();

        }
        if (admindialogPlus!=null){
            if (admindialogPlus.isShowing()){
                admindialogPlus.dismiss();
            }
        }
    }

    private static class ViewHolder {
        EditText etEmail, etPassword;
        Button btnSaveEmail, btnCancel;
    }

}
