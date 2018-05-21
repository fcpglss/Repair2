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

import model.Apply;
import model.ResultBean;
import repair.com.repair.R;
import util.UIUtil;
import util.Util;


/**
 * Created by Administrator on 2016-11-30.
 */

public class ApplysAdapter extends BaseAdapter {

    private List<Apply> list;
    private LayoutInflater mInflater;


    Context context;

    public ApplysAdapter(List<Apply> list, Context context) {
        this.list = list;
        mInflater = LayoutInflater.from(context);

        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        Apply apply = list.get(position);

        viewHolder.tvTitle.setText(Util.setContentTitle(apply));
        viewHolder.tvContent.setText(Util.setClass(apply, 22, true));

        String temp = list.get(position).getRepairTime();
        viewHolder.tvTime.setText(temp.split(":")[0] + ":" + temp.split(":")[1]);

        int state = list.get(position).getState();

        viewHolder.ivRightDownIcon.setImageResource(UIUtil.getStatusIcon(state));

        viewHolder.ivIcon.setImageResource(UIUtil.getCategoryIcon(list.get(position).getClasss()));

        return convertView;
    }


    class ViewHolder {
        TextView tvTitle, tvContent, tvTime;
        ImageView ivIcon, ivRightDownIcon;
    }
}