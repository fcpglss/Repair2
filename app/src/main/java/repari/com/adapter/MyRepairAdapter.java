package repari.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.orhanobut.dialogplus.DialogPlus;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import fragment.ApplyFragment;
import io.reactivex.functions.Consumer;
import model.Apply;
import model.ResultBean;
import repair.com.repair.ChangeActivity;
import repair.com.repair.R;
import util.AESUtil;
import util.JsonUtil;
import util.UIUtil;
import util.Util;

import static repair.com.repair.MainActivity.windowHeigth;
import static repair.com.repair.MainActivity.windowWitch;


/**
 * Created by Administrator on 2016-11-30.
 */

public class MyRepairAdapter extends BaseAdapter {

    private static final String TAG = "MyRepairAdapter";

    private ResultBean myRes = null;

    private LayoutInflater mInflater;

    private Drawable mDefaultBitmapDrawable;

    private String applyName ="";


    private static final int mImageWidth = 150;

    private static final int mImageHeigth = 150;

    private boolean mCanGetBitmapFromNetWork = true;

    private String area_name = "";

    private String categoryName = "";

    private Context context;

    public MyRepairAdapter(ResultBean res, Context context) {
        this.myRes = res;

        mInflater = LayoutInflater.from(context);
        mDefaultBitmapDrawable = context.getResources().getDrawable(R.mipmap.ic_launcher);
        this.context = context;
    }

    @Override
    public int getCount() {
        return myRes.getApplys().size();
    }

    @Override
    public Object getItem(int position) {
        return myRes.getApplys().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        Apply apply = myRes.getApplys().get(position);



        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.my_apply_list, null);
            viewHolder.ivPhoto = (ImageView) convertView.findViewById(R.id.iv_my_pic);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_my_name);
            viewHolder.ivState = (ImageView) convertView.findViewById(R.id.iv_my_state);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_my_time);
            viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tv_my_area);
            viewHolder.tvType = (TextView) convertView.findViewById(R.id.tv_my_type);

            //评价和修改
            viewHolder.tvAppraise = (TextView) convertView.findViewById(R.id.tv_my_to_appraise);
            viewHolder.tvChange = (TextView) convertView.findViewById(R.id.tv_my_to_change);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ImageView imageView = viewHolder.ivPhoto;
        final String tag = (String) imageView.getTag();
        String photoUrl = "";
        String a_details = "";

        if(Util.getPhotoUrl(position, myRes))
        {
            photoUrl=myRes.getApplys().get(position).getA_imaes().get(0);
        }

        categoryName = apply.getClasss();
        a_details = Util.setAddress(apply,20,false);
        final String uri = photoUrl;

        if (!uri.equals(tag)) {
            imageView.setImageDrawable(mDefaultBitmapDrawable);
        }

        if (mCanGetBitmapFromNetWork&&!photoUrl.equals("")) {
            imageView.setTag(photoUrl);
//            mImageLoader.bindBitmap(photoUrl, imageView, mImageWidth, mImageHeigth);
            Picasso.with(context).load(photoUrl).into(imageView);
        }

        viewHolder.tvTime.setText(setTime(apply.getRepairTime()));
        viewHolder.tvName.setText(AESUtil.decode(apply.getRepair()));

        int status = myRes.getApplys().get(position).getState();
        viewHolder.ivState.setImageResource(UIUtil.getStatusIcon(status));
        viewHolder.tvAddress.setText(Util.setContentTitle(apply));
        viewHolder.tvType.setText(Util.setClass(apply,18,true));

        //判断是否能修改和评价然后跳转
        JumpApprise(viewHolder.tvAppraise, apply.getState(), position);
        JumpChange(viewHolder.tvChange, apply.getState(), position);

        return convertView;
    }

    private void JumpChange(final TextView tvChange, final int state, final int position) {


        if (state ==1){
            tvChange.setVisibility(View.VISIBLE);
        }else {
            tvChange.setVisibility(View.GONE);
        }

        RxView.clicks(tvChange).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (state == 1) {
                            // 放入被点击的item的Id ,跳转修改Activity
                            Intent intent = new Intent(context, ChangeActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("apply", myRes.getApplys().get(position));
                            bundle.putSerializable("address", getAddressRes());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "维修单已开始处理，不能再修改", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        tvChange.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    tvChange.setBackgroundResource(R.drawable.button_submit2);
                }else {
                    tvChange.setBackgroundResource(R.drawable.button_submit);
                }
                return false;
            }
        });
    }


    public static DialogPlus dialogPlus = null;

    private void JumpApprise(final TextView tvAppraise, final int state, final int position) {

        Log.d(TAG, "JumpApprise: 状态 "+state);
        Log.d(TAG, "JumpApprise: "+(myRes.getApplys().get(position).getEvalText()==null));


        if (state == 4 && myRes.getApplys().get(position).getEvalText()==null){
            Log.d(TAG, "JumpApprise: 状态 里面"+state);
            tvAppraise.setVisibility(View.VISIBLE);
        }else {
            tvAppraise.setVisibility(View.GONE);
        }

        tvAppraise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + state);
                if (state == 4) {
                    Log.d(TAG, "JumpApprise  onClick: 点击事件");
                    Log.d(TAG, "onClick: position: " + position);
                    DialogAdapterPassword dialogAdapterPassword = new DialogAdapterPassword(context, R.layout.dialog_input_password,myRes.getApplys().get(position));
                    //点击弹出对话框输入密码
                    dialogPlus = DialogPlus.newDialog(context)
                            .setAdapter(dialogAdapterPassword)
                            .setGravity(Gravity.CENTER)
                            .setContentWidth((int) (windowWitch/1.5))
                            .setContentHeight(windowHeigth/3)
                            .setHeader(R.layout.dialog_head5)
                            .create();
                    dialogPlus.show();
                    Log.d(TAG, "JumpApprise onClick: show()已被调用");
                } else {
                    Toast.makeText(context, "维修单未完成不能评价", Toast.LENGTH_SHORT).show();
                }

            }
        });
        tvAppraise.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    tvAppraise.setBackgroundResource(R.drawable.button_submit2);
                }else {
                    tvAppraise.setBackgroundResource(R.drawable.button_submit);
                }
                return false;
            }
        });
    }


    private String setTime(String datetime) {

        if (datetime != null && !datetime.equals("")) {
            return datetime.split(":")[0] + ":" + datetime.split(":")[1];
        }
        return "";
    }

    private ResultBean getAddressRes() {
        ResultBean addressRes = ApplyFragment.addressRes;
        if (addressRes == null) {
            Log.d(TAG, "getAddressRes: 内存中的addresRes为null，从本地address_data中读取");
            String address = Util.loadAddressFromLocal(context);
            addressRes = JsonUtil.jsonToBean(address);
        }
        return addressRes;
    }


    class ViewHolder {
        TextView tvName, tvTime, tvAddress, tvType;
        ImageView ivPhoto, ivState;
        //评价和修改
        TextView tvAppraise, tvChange;

    }
}