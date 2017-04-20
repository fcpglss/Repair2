package repari.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application.MyApplication;


import camera.FIleUtils;
import model.Apply;
import model.Employee;
import model.Response;
import model.ResultBean;
import okhttp3.Call;
import repair.com.repair.AdminListActivity;

import repair.com.repair.R;
import util.JsonUtil;
import util.Util;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.ACTION_SEND_MULTIPLE;
import static repair.com.repair.MainActivity.UP_APPLY;
import static repair.com.repair.MainActivity.windowWitch;
import static repair.com.repair.MainActivity.windowHeigth;

/**
 * Created by hsp on 2017/4/8.
 */

public class AdminListAdapter extends BaseAdapter {

    private static final String TAG = "AdminListAdapter";
    private static boolean isLoadImages;


    private Context context;
    private ResultBean resultBean;
    private LayoutInflater inflater;
    private List<Apply> list = new ArrayList<>();

    private boolean mCanGetBitmapFromNetWork = true;

    private Drawable mDefaultBitmapDrawable;

    public AdminListAdapter(Context context, ResultBean resultBean, boolean hasPic) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        mDefaultBitmapDrawable = context.getResources().getDrawable(R.mipmap.ic_launcher);
        this.resultBean = resultBean;
        list = resultBean.getApplys();
        isLoadImages = hasPic;
    }

    @Override
    public int getCount() {
        return list.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;
        Apply apply = list.get(position);
        if (null == view) {
            view = inflater.inflate(R.layout.admin_item_list, null);
            viewHolder = new ViewHolder();

            viewHolder.tvAdress = (TextView) view.findViewById(R.id.tv_admin_item_address);
            viewHolder.tvcategory = (TextView) view.findViewById(R.id.tv_admin_item_category);
            viewHolder.tvName = (TextView) view.findViewById(R.id.tv_admin_item_name);

            viewHolder.tvTime = (TextView) view.findViewById(R.id.tv_admin_item_time);
            viewHolder.tvDescribe = (TextView) view.findViewById(R.id.textView8);
            viewHolder.tvTel = (TextView) view.findViewById(R.id.tv_admin_item_tel);
            viewHolder.imgView = (ImageView) view.findViewById(R.id.img_admin_pic);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tvName.setText(apply.getRepair());
        viewHolder.tvTime.setText(Util.getDealTime(apply.getRepairTime()));
        viewHolder.tvcategory.setText(apply.getClasss());
        viewHolder.tvAdress.setText(Util.setTitle(apply));
        viewHolder.tvTel.setText(apply.getTel());
        viewHolder.tvDescribe.setText(apply.getRepairDetails());

        ImageView imageView = viewHolder.imgView;
        final String tag = (String) imageView.getTag();
        String photoUrl = "";

        if (Util.getPhotoUrl(position, resultBean)) {
            photoUrl = resultBean.getApplys().get(position).getA_imaes().get(0);
        }
         String uri = photoUrl;
        if (!uri.equals(tag)) {
            imageView.setImageDrawable(mDefaultBitmapDrawable);
        }
        setViewHldImg(photoUrl,imageView);

//        if (mCanGetBitmapFromNetWork && !photoUrl.equals("")) {
//            imageView.setTag(photoUrl);
//
//            Picasso.with(context).load(photoUrl).into(imageView);
//        }

        return view;
    }

    public void setIsLoadImages(boolean hasPic) {
        isLoadImages = hasPic;
    }

    private void setViewHldImg(String ImageString,ImageView imageView)
    {
        if(isLoadImages)
        {

            if (mCanGetBitmapFromNetWork && !ImageString.equals("")) {
                imageView.setTag(ImageString);

                Picasso.with(context).load(ImageString).into(imageView);
            }
        }
    }



    class ViewHolder {
        TextView tvName, tvTime, tvAdress, tvcategory, tvDescribe, tvTel;
        ImageView imgView;
    }


}


