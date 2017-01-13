package repari.com.adapter;

import android.content.Context;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import imagehodler.ImageLoader;
import model.Apply;
import model.Category;
import model.Place;
import model.ResultBean;
import repair.com.repair.R;


/**
 * Created by Administrator on 2016-11-30.
 */

public class ApplysAdapter extends BaseAdapter {
    private ResultBean res=null;

    private LayoutInflater mInflater;

    private Drawable mDefaultBitmapDrawable;

    public ImageLoader mImageLoader;

    private static String categoryProprety="";

    private static final int mImageWidth=150;

    private static final int mImageHeigth=150;

    private boolean mCanGetBitmapFromNetWork = true;


    public ApplysAdapter(ResultBean res, Context context) {
        this.res = res;
        mInflater = LayoutInflater.from(context);
        mDefaultBitmapDrawable = context.getResources().getDrawable(R.mipmap.ic_launcher);

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

            convertView = mInflater.inflate(R.layout.item_layout, null);
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.ivRightDownIcon = (ImageView) convertView.findViewById(R.id.iv_right_down_icon);
            viewHolder.img_emergent= (ImageView) convertView.findViewById(R.id.img_emergent);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ImageView imageView =viewHolder.ivIcon;
        final  String tag= (String) imageView.getTag();
        String c_url="";
        String p_name="";
        String a_details="";

        //获取当前Apply中的categoryID
        c_url = getCategoryId(position, res);
        p_name =getPlaceId(position,res);
        a_details=res.getApplys().get(position).getA_detalis();

        final  String uri = c_url;

        if(!uri.equals(tag))
        {
            imageView.setImageDrawable(mDefaultBitmapDrawable);
        }

        if(mCanGetBitmapFromNetWork)
        {

            imageView.setTag(c_url);
            mImageLoader.bindBitmap(c_url,imageView,mImageWidth,mImageHeigth);

        }

        viewHolder.tvTitle.setText(p_name+"─"+a_details);
        viewHolder.tvContent.setText(res.getApplys().get(position).getA_describe());

        viewHolder.tvTime.setText(res.getApplys().get(position).getA_no());
        setIcon(viewHolder);
        viewHolder.ivRightDownIcon.setImageResource(getRightIcon(position,res));
        return convertView;
    }

    /**
     *
     * 获得相应的category的Image_url
     */

    private String getCategoryId(int position, ResultBean rs) {
        int appyly_cid=rs.getApplys().get(position).getA_category();

        String c_url="";

        for(Category category:rs.getCategory())
        {
            if(appyly_cid==category.getC_id())
            {
                categoryProprety=category.getC_priority();
                c_url=category.getC_imageurl();
                break;
            }
        }
        return c_url;
    }

    private String getPlaceId(int position,ResultBean rs)
    {
        int appyly_pid=rs.getApplys().get(position).getA_place();

        String p_name="";

        for(Place place:rs.getPlaces())
        {
            if(appyly_pid==place.getP_id())
            {
                p_name=place.getP_name();
                break;
            }
        }
        return p_name;
    }

    private int getRightIcon(int position,ResultBean rs){
        int image = 0;
        String a_status = rs.getApplys().get(position).getA_status();
           switch (a_status){
               case "处理中":
                   image = R.drawable.chulizhong;
                   break;
               case "待处理":
                   image = R.drawable.daichuli;
                   break;
               case "已完成":
                   image = R.drawable.finish;
                   break;
               case "已失效":
                   image = R.drawable.yishixiao;
                   break;
               default:
                   image = R.drawable.daichuli;
           }
        return image;
    }
    private void setIcon(ViewHolder view)
    {
        switch (categoryProprety) {
            case "1":
                view.img_emergent.setImageResource(R.drawable.emergent3);
                break;
            default:
        }
    }





    public void setList_Applys(List<Apply> apply)
    {
        res.setApplys(apply);
    }

    class ViewHolder{
        TextView tvTitle,tvContent,tvTime;
        ImageView ivIcon,ivRightDownIcon,img_emergent;

    }
}