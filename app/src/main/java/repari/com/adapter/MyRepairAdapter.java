package repari.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import imagehodler.ImageLoader;
import model.Apply;
import model.Area;
import model.Category;
import model.Photo;
import model.Place;
import model.ResultBean;
import repair.com.repair.AppraiseActivity;
import repair.com.repair.ChangeActivity;
import repair.com.repair.R;


/**
 * Created by Administrator on 2016-11-30.
 */

public class MyRepairAdapter extends BaseAdapter {

    private static final String TAG = "MyRepairAdapter";
    
    private ResultBean res=null;

    private LayoutInflater mInflater;

    private Drawable mDefaultBitmapDrawable;

    public ImageLoader mImageLoader;

    private static String categoryProprety="";

    private static final int mImageWidth=150;

    private static final int mImageHeigth=150;

    private boolean mCanGetBitmapFromNetWork = true;

    private String area_name="";

    private String categoryName="";

    private Context context;


    public MyRepairAdapter(ResultBean res, Context context) {
        this.res = res;
        mInflater = LayoutInflater.from(context);
        mDefaultBitmapDrawable = context.getResources().getDrawable(R.mipmap.ic_launcher);

        this.context = context;
        mImageLoader = ImageLoader.build(context);
    }

    @Override
    public int getCount() {
        return res.getApplys().size();
    }

    @Override
    public Object getItem(int position) {
        return res.getApplys().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
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
        String p_name="";
        String a_details="";


        photoUrl=getPhotoUrl(position,res).get(0).toString();
        p_name =getPlaceId(position,res);
        categoryName=getCategoryId(position,res);
        a_details=res.getApplys().get(position).getRoom();

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

        viewHolder.tvTime.setText(setTime(res.getApplys().get(position).getRepairTime()));

        viewHolder.tvName.setText(res.getApplys().get(position).getRepair());
        viewHolder.ivState.setImageResource(getState(position,res));
        viewHolder.tvAddress.setText(area_name+p_name+" "+a_details);
        viewHolder.tvType.setText(categoryName);

        //判断是否能修改和评价然后跳转
        JumpApprise(viewHolder.tvAppraise,res.getApplys().get(position).getState(),position);
        JumpChange(viewHolder.tvChange,res.getApplys().get(position).getState(),position);

        return convertView;
    }

    private void JumpChange(TextView tvChange, int state, final int position) {
        // FIXME: 2017/3/14 已完工状态是数字几来着？我默认放了1
        if (state == 1){
            tvChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 放入被点击的item的Id ,跳转修改Activity
                    Intent intent = new Intent(context, ChangeActivity.class);
                    intent.putExtra("apply",res.getApplys().get(position));
                    context.startActivity(intent);
                }
            });
        }

    }

    private void JumpApprise(TextView tvAppraise, final int position, int state) {
        // FIXME: 2017/3/14 已完工状态是数字几来着？我默认放了4
        if (state == 4){
            tvAppraise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //放入被点击的item的Id,跳转评价Activity
                    Intent intent = new Intent(context, AppraiseActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("apply",res.getApplys().get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }
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
        photoList=rs.getApplys().get(position).getA_imaes();

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


    private String getCategoryId(int position, ResultBean rs) {
        String appyly_cid=rs.getApplys().get(position).getClasss();

        for(Category category:rs.getCategory())
        {
            if(appyly_cid.equals(category.getC_name()))
            {
                categoryName=category.getC_name();
                break;
            }
            else
            {
                categoryName="其他";
            }
        }

        return categoryName;
    }






    private String getPlaceId(int position,ResultBean rs)
    {
        String appyly_pid=rs.getApplys().get(position).getDetailArea();//applyID 要改为String

        String p_name="";

        for(Place place:rs.getPlaces())
        {
            if(appyly_pid.equals(place.getP_name()))
            {
                Log.d(TAG, "getPlaceId: "+appyly_pid);

                for(Area a :rs.getAreas())
                {

                    if(a.getId()==place.getAreaID())
                    {
                        area_name=a.getArea();
                    }
                }
                p_name=place.getP_name();
                break;
            }
        }
        return p_name;
    }


    public void setList_Applys(List<Apply> apply)
    {
        res.setApplys(apply);
    }

    class ViewHolder{
        TextView tvName,tvTime,tvAddress,tvType;
        ImageView ivPhoto,ivState;

        //评价和修改
        TextView tvAppraise,tvChange;

    }
}