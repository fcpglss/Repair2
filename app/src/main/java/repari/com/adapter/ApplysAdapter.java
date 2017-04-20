package repari.com.adapter;

import android.content.Context;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import imagehodler.ImageLoader;
import model.Apply;
import model.Category;
import model.ResultBean;
import repair.com.repair.R;
import util.Util;


/**
 * Created by Administrator on 2016-11-30.
 */

public class ApplysAdapter extends BaseAdapter {
    private static final String TAG = "ApplysAdapter";
    private ResultBean res = null;
    private LayoutInflater mInflater;
    private Drawable mDefaultBitmapDrawable;

    private static String categoryProprety = "";

    private boolean mCanGetBitmapFromNetWork = true;
    Context context;

    public ApplysAdapter(ResultBean res, Context context) {
        this.res = res;
        mInflater = LayoutInflater.from(context);
        mDefaultBitmapDrawable = context.getResources().getDrawable(R.mipmap.ic_launcher);
        this.context = context;
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
            viewHolder.img_emergent = (ImageView) convertView.findViewById(R.id.img_emergent);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ImageView imageView = viewHolder.ivIcon;
        final String tag = (String) imageView.getTag();
        String c_url = "";
        String a_details = "";
        c_url = getCategoryId(position, res);
        Apply apply = res.getApplys().get(position);
        a_details = Util.setTitle(apply);

        final String uri = c_url;

        if (!uri.equals(tag)) {
            imageView.setImageDrawable(mDefaultBitmapDrawable);
        }

        if (mCanGetBitmapFromNetWork) {
            imageView.setTag(c_url);
            Picasso.with(context).load(c_url).into(imageView);
        }
        viewHolder.tvTitle.setText(a_details);
        viewHolder.tvContent.setText(apply.getRepairDetails());

        String temp = res.getApplys().get(position).getRepairTime();
        viewHolder.tvTime.setText(temp.split(":")[0] + ":" + temp.split(":")[1]);
        setIcon(viewHolder);
        viewHolder.ivRightDownIcon.setImageResource(getRightIcon(position, res));
        return convertView;
    }

    /**
     * 获得相应的category的Image_url
     */


    private String getCategoryId(int position, ResultBean rs) {
        String appyly_cid = rs.getApplys().get(position).getClasss();

        String c_url = "";

        for (Category category : rs.getCategory()) {
            if (appyly_cid.equals(category.getC_name())) {
                categoryProprety = category.getC_priority();
                c_url = category.getC_imageurl();
                break;
            }
        }
        return c_url;
    }
    private int getRightIcon(int position, ResultBean rs) {
        int image = 0;
        int a_status = rs.getApplys().get(position).getState();
        switch (a_status) {
            case 1:
                image = R.drawable.daichuli;
                break;
            case 2:

                image = R.drawable.chulizhong;
                 break;
            case 3:
                image = R.drawable.yishixiao;
                break;

            case 4:
                image = R.drawable.finish;
                break;
            default:
                image = R.drawable.daichuli;
        }
        return image;
    }
    private void setIcon(ViewHolder view) {
        switch (categoryProprety) {
            case "1":
                view.img_emergent.setImageResource(R.drawable.emergent3);
                break;
            default:
        }
    }

    public void setList_Applys(List<Apply> apply) {
        res.setApplys(apply);
    }

    class ViewHolder {
        TextView tvTitle, tvContent, tvTime;
        ImageView ivIcon, ivRightDownIcon, img_emergent;
    }
}