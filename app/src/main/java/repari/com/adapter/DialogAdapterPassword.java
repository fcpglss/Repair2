package repari.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.jakewharton.rxbinding2.view.RxView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import constant.RequestUrl;
import io.reactivex.functions.Consumer;
import model.Apply;
import network.Api;
import okhttp3.Call;
import repair.com.repair.AppraiseActivity;
import repair.com.repair.R;
import util.Util;

import static repari.com.adapter.MyRepairAdapter.dialogPlus;

/**
 * Created by hsp on 2017/3/20.
 */

public class DialogAdapterPassword extends BaseAdapter {
    private static final String TAG = "DialogAdapterPassword";
    LayoutInflater layoutInflater;
    Apply apply;
    Context context;
    int layout;
    SweetAlertDialog svProgressHUD;


    public DialogAdapterPassword(Context context, int layout, Apply apply) {
        this.layout = layout;
        this.apply = apply;
        this.context = context;
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

            viewHolder.editText = (EditText) view.findViewById(R.id.et_input_password);
            viewHolder.btnCancel = (Button) view.findViewById(R.id.btn_input_cancel);
            viewHolder.btnConfirm = (Button) view.findViewById(R.id.btn_input_confirm);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        RxView.clicks(viewHolder.btnConfirm).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        String s = viewHolder.editText.getText().toString();

                        //输入为空提醒输入密码
                        if ("".equals(s)) {
                            Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
                        } else {
                            //请求网络判断对错
                            svProgressHUD = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
                            svProgressHUD.setTitleText("验证中");
                            svProgressHUD.show();
                            String MD5 = Util.getMD5(viewHolder.editText.getText().toString());
                            Api.checkPassword(MD5, apply.getId()).execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
                                    closeDiag();
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    if ("OK".equals(response)) {
                                        closeDiag();
                                        //跳转
                                        Intent intent = new Intent(context, AppraiseActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("apply", apply);
                                        intent.putExtras(bundle);
                                        context.startActivity(intent);
                                    } else {
                                        Toast.makeText(context, "密码不正确", Toast.LENGTH_SHORT).show();
                                        closeDiag();
                                    }
                                }
                            });

                        }
                    }
                });


        viewHolder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.editText.setText("");
                dialogPlus.dismiss();
            }
        });
        return view;
    }

    private void closeDiag() {
        if (svProgressHUD != null && svProgressHUD.isShowing()) {
            svProgressHUD.dismiss();
        }
        if (dialogPlus != null) {
            if (dialogPlus.isShowing()) {
                dialogPlus.dismiss();
            }
        }
    }

    private static class ViewHolder {
        EditText editText;
        Button btnCancel, btnConfirm;
    }

}
