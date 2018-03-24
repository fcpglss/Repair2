package repari.com.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import model.Announcement;
import repair.com.repair.R;
import util.Util;

/**
 * Created by hsp on 2017/3/27.
 */

public class AnnocmentListAdapter extends BaseAdapter {
    private static final String TAG = "AnnocmentListAdapter";

    LayoutInflater layoutInflater;
    ViewHolder viewHolder = null;
    public List<Announcement> list = new ArrayList<>();

    public AnnocmentListAdapter(List<Announcement> list, Context context) {
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
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


        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.annocment_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_annocment_title);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_annocment_time);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_annocment_content);
            viewHolder.llTitle = (LinearLayout) convertView.findViewById(R.id.ll_annocement_title);
            viewHolder.llContent = (LinearLayout) convertView.findViewById(R.id.ll_annocement_content);
            viewHolder.tvAdmin = (TextView) convertView.findViewById(R.id.tv_annocment_admin);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvTitle.setText(list.get(position).getTitle());
        viewHolder.tvTime.setText(list.get(position).getCreate_at());
        viewHolder.tvContent.setText(list.get(position).getContent());
        viewHolder.tvAdmin.setText(list.get(position).getAdmin_name());
        viewHolder.llTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.llContent.setVisibility(View.VISIBLE);
            }
        });
        return convertView;
    }


    private static class ViewHolder {
        TextView tvTitle, tvTime, tvContent, tvAdmin;
        LinearLayout llTitle, llContent;
    }


}
