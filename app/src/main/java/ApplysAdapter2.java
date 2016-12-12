package repari.com.adapter;

import android.content.Context;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import imagehodler.ImageLoader;
import model.Applyss;
import model.Test2;
import repair.com.repair.R;

/**
 * Created by Administrator on 2016-11-30.
 */

public class ApplysAdapter2 extends BaseAdapter {

    private List<Test2> mlist_test2;

    private LayoutInflater mInflater;

    private Drawable mDefaultBitmapDrawable;

    private ImageLoader mImageLoader;

    private static final int mImageWidth=150;

    private static final int mImageHeigth=150;

    private boolean mCanGetBitmapFromNetWork = true;


    public ApplysAdapter2(List<Test2> mlist_test2, Context context) {
        this.mlist_test2 = mlist_test2;
        mInflater = LayoutInflater.from(context);
        mDefaultBitmapDrawable = context.getResources().getDrawable(R.mipmap.ic_launcher);

        mImageLoader = ImageLoader.build(context);
    }

    @Override
    public int getCount() {
        return mlist_test2.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist_test2.get(position);
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
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ImageView imageView =viewHolder.ivIcon;
        final  String tag= (String) imageView.getTag();
        final  String uri = mlist_test2.get(position).getA_image();
        if(!uri.equals(tag))
        {
            imageView.setImageDrawable(mDefaultBitmapDrawable);
        }

        if(mCanGetBitmapFromNetWork)
        {
            imageView.setTag(uri);
            mImageLoader.bindBitmap(uri,imageView,mImageWidth,mImageHeigth);

        }

        viewHolder.tvTitle.setText(mlist_test2.get(position).getA_name());
        viewHolder.tvContent.setText(mlist_test2.get(position).getA_describe());
        return convertView;
    }
    public void setList_Applys(List<Test2> test2)
    {
     mlist_test2=test2;
    }

    class ViewHolder{
        TextView tvTitle,tvContent;
        ImageView ivIcon;
    }
}
