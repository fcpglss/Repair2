package repari.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;

import java.util.ArrayList;
import java.util.List;

import application.MyApplication;
import fragment.ApplyFragment;
import imagehodler.ImageLoader;
import model.Apply;
import model.Area;
import model.Category;
import model.Place;
import model.ResultBean;
import repair.com.repair.AppraiseActivity;
import repair.com.repair.ChangeActivity;
import repair.com.repair.DetailsActivity;
import repair.com.repair.R;
import util.JsonUtil;
import util.Util;


/**
 * Created by Administrator on 2016-11-30.
 */

public class MyRepairAdapter extends BaseAdapter {

    private static final String TAG = "MyRepairAdapter";

    private ResultBean myRes =null;

    private LayoutInflater mInflater;

    private Drawable mDefaultBitmapDrawable;

    public ImageLoader mImageLoader;

    private static final int mImageWidth=150;

    private static final int mImageHeigth=150;

    private boolean mCanGetBitmapFromNetWork = true;

    private String area_name="";

    private String categoryName="";

    private Context context;

    public MyRepairAdapter(ResultBean res, Context context) {
        this.myRes = res;
        mInflater = LayoutInflater.from(context);
        mDefaultBitmapDrawable = context.getResources().getDrawable(R.mipmap.ic_launcher);
        this.context = context;
        mImageLoader = ImageLoader.build(context);
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
        Apply apply=myRes.getApplys().get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.my_apply_list, null);
            viewHolder.ivPhoto = (ImageView) convertView.findViewById(R.id.iv_my_pic);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_my_name);
            viewHolder.ivState = (ImageView) convertView.findViewById(R.id.iv_my_state);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_my_time);
            viewHolder.tvAddress=(TextView)convertView.findViewById(R.id.tv_my_area);
            viewHolder.tvType=(TextView)convertView.findViewById(R.id.tv_my_type);

            //评价和修改
            viewHolder.tvAppraise = (TextView) convertView.findViewById(R.id.tv_my_to_appraise);
            viewHolder.tvChange = (TextView) convertView.findViewById(R.id.tv_my_to_change);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ImageView imageView =viewHolder.ivPhoto;
        final  String tag= (String) imageView.getTag();
        String photoUrl="";
        String a_details="";

        photoUrl=getPhotoUrl(position, myRes).get(0).toString();
        Log.d(TAG, "getView: photoUrl"+photoUrl);

        categoryName= apply.getClasss();
        a_details=Util.setTitle(apply);
        final  String uri = photoUrl;

        if(!uri.equals(tag))
        {
            imageView.setImageDrawable(mDefaultBitmapDrawable);
        }

        if(mCanGetBitmapFromNetWork)
        {
            imageView.setTag(photoUrl);
            mImageLoader.bindBitmap(photoUrl,imageView,mImageWidth,mImageHeigth);
        }

        viewHolder.tvTime.setText(setTime(apply.getRepairTime()));
        viewHolder.tvName.setText(apply.getRepair());
        viewHolder.ivState.setImageResource(getState(position, myRes));
        viewHolder.tvAddress.setText(area_name+a_details);
        viewHolder.tvType.setText(categoryName);

        //判断是否能修改和评价然后跳转
        JumpApprise(viewHolder.tvAppraise,position, apply.getState());
        JumpChange(viewHolder.tvChange,position, apply.getState());

        return convertView;
    }

    private void JumpChange(TextView tvChange, final int state, final int position) {

            tvChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (state == 1) {
                        // 放入被点击的item的Id ,跳转修改Activity
                        Intent intent = new Intent(context, ChangeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("apply",myRes.getApplys().get(position));
                        bundle.putSerializable("address",getAddressRes());
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }else {
                        Toast.makeText(context, "维修单已开始处理，不能再修改", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }


    public  static DialogPlus dialogPlus =null;

    private void JumpApprise(TextView tvAppraise, final int position, final int state) {
        Log.d(TAG, "JumpApprise: if 判断之前");


            tvAppraise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //fixme  先改为true随时可以跳转
                    Log.d(TAG, "onClick: "+state);
                    if (state == 4){
                        Log.d(TAG, "JumpApprise  onClick: 点击事件");
                        Log.d(TAG, "onClick: position: "+position);
                        DialogAdapterPassword dialogAdapterPassword = new DialogAdapterPassword(context,R.layout.dialog_input_password,
                                myRes.getApplys().get(position));
                        //点击弹出对话框输入密码
                        dialogPlus = DialogPlus.newDialog(context)
                                .setAdapter(dialogAdapterPassword)
                                .setGravity(Gravity.CENTER)
                                .setContentWidth(800)
                                .setContentHeight(500)
                                .setHeader(R.layout.dialog_head5)
                                .create();
                        dialogPlus.show();
                        Log.d(TAG, "JumpApprise onClick: show()已被调用");
                    }else {
                        Toast.makeText(context, "维修单未完成不能评价", Toast.LENGTH_SHORT).show();
                    }

                }
            });
    }


    private String setTime(String datetime) {

        if (datetime != null && !datetime.equals(""))
        {
            return datetime.split(":")[0]+":"+datetime.split(":")[1];
        }
        return "";
    }


    private List<String> getPhotoUrl(int position, ResultBean rs) {
        List<String> photoList=new ArrayList<>();
        if(rs==null)
        {
            photoList.add("myRes没有值");
            return photoList;
        }
        photoList=rs.getApplys().get(position).getA_imaes();
        if(photoList.size()==0)
        {
            photoList.add("未知图片");
        }
        return photoList;
    }

    private int getState(int position,ResultBean rs){
        int image = 0;
        int a_status = rs.getApplys().get(position).getState();
        switch (a_status){
            case 1:
                image = R.drawable.chulizhong;
                break;
            case 2:
                image = R.drawable.daichuli;
                break;
            case 3:
                image = R.drawable.finish;
                break;
            case 4:
                image = R.drawable.yishixiao;
                break;
            default:
                image = R.drawable.daichuli;
        }
        return image;
    }

    private ResultBean getAddressRes()
    {
        ResultBean addressRes= ApplyFragment.addressRes;
        if(addressRes==null) {
            Log.d(TAG, "getAddressRes: 内存中的addresRes为null，从本地address_data中读取");
            String address = Util.loadAddressFromLocal(context);
            addressRes = JsonUtil.jsonToBean(address);
        }
        return addressRes;
    }

    class ViewHolder{
        TextView tvName,tvTime,tvAddress,tvType;
        ImageView ivPhoto,ivState;

        //评价和修改
        TextView tvAppraise,tvChange;

    }
}