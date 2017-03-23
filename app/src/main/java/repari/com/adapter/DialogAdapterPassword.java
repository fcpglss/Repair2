package repari.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import imagehodler.ImageLoader;
import model.Apply;
import model.Category;
import repair.com.repair.AppraiseActivity;
import repair.com.repair.R;

import static repari.com.adapter.MyRepairAdapter.dialogPlus;

/**
 * Created by hsp on 2017/3/20.
 */

public class DialogAdapterPassword extends BaseAdapter {
    LayoutInflater layoutInflater;
    ImageLoader imageLoader;
    List<Apply> apply;
    Context context;
    int position;
    int layout;

    public DialogAdapterPassword(Context context, int layout, List<Apply> apply, int position) {
        this.layout = layout;
        this.apply = apply;
        this.position = position;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.build(context);
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

        viewHolder.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = viewHolder.editText.getText().toString();

                //输入为空提醒输入密码
                if ("".equals(s)) {
                    Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
                } else {
                    //正确就跳转
                    if (s.equals(apply.get(position).getPassword())) {
                        dialogPlus.dismiss();
                        Intent intent = new Intent(context, AppraiseActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("apply", apply.get(position));
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    } else {
                        //错误就提示
                        Toast.makeText(context, "输入的密码错误", Toast.LENGTH_SHORT).show();
                        //并且清空
                        viewHolder.editText.setText("");
                    }
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

    private static class ViewHolder {
        EditText editText;
        Button btnCancel, btnConfirm;
    }

}
