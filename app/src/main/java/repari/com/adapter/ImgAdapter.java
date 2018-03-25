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

import com.bm.library.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.List;

import model.Apply;
import model.ResultBean;
import repair.com.repair.R;
import util.UIUtil;
import util.Util;


/**
 * Created by Administrator on 2016-11-30.
 */

public class ImgAdapter extends BaseAdapter {
    private static final String TAG = "ImgAdapter";
    List<String> imgList;
    private LayoutInflater mInflater;
    Context context;

    public ImgAdapter(Context context, List<String> imgList) {
        this.imgList = imgList;
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return imgList.size();
    }

    @Override
    public Object getItem(int position) {
        return imgList.get(position);
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
            convertView = mInflater.inflate(R.layout.img_item, null);
            viewHolder.photoView = (PhotoView) convertView.findViewById(R.id.pv_img);
            viewHolder.photoView.setDrawingCacheEnabled(true);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Log.d(TAG, "getView:  "+imgList.get(position));
        Picasso.with(context)
                .load(imgList.get(position))
                .placeholder(R.drawable.loadimg)
                .noFade()
                .fit()
                .into( viewHolder.photoView);
        return convertView;
    }


    class ViewHolder {

        PhotoView photoView;
    }
}