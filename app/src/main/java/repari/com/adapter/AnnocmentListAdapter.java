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

/**
 * Created by hsp on 2017/3/27.
 */

public class AnnocmentListAdapter extends BaseAdapter {
    private static final String TAG = "AnnocmentListAdapter";

    LayoutInflater layoutInflater;
    List<Announcement> list = new ArrayList<>();
    ViewHolder viewHolder = null;

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

        View view = convertView;

        if (null == view) {
            view = layoutInflater.inflate(R.layout.annocment_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_annocment_title);
            viewHolder.tvTime = (TextView) view.findViewById(R.id.tv_annocment_time);
            viewHolder.tvContent = (TextView) view.findViewById(R.id.tv_annocment_content);
            viewHolder.llTitle = (LinearLayout) view.findViewById(R.id.ll_annocement_title);
            viewHolder.llContent = (LinearLayout) view.findViewById(R.id.ll_annocement_content);
            view.setTag(viewHolder);
        } else {
            view.getTag();
        }

        assert viewHolder != null;
        viewHolder.tvTitle.setText(list.get(position).getTitle());
        viewHolder.tvTime.setText(list.get(position).getCreate_at());
        viewHolder.tvContent.setText(list.get(position).getContent());
        viewHolder.llTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 点击事件设置内容可见");
                viewHolder.llContent.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }

    private static class ViewHolder {
        TextView tvTitle, tvTime, tvContent;
        LinearLayout llTitle, llContent;

    }
}
