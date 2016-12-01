package repari.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import model.Applyss;
import model.Test2;
import repair.com.repair.R;

/**
 * Created by Administrator on 2016-11-30.
 */

public class ApplysAdapter extends BaseAdapter {

    private List<Test2> mlist_test2;

    private LayoutInflater mInflater;

    public ApplysAdapter(List<Test2> mlist_test2, Context context) {
        this.mlist_test2 = mlist_test2;
        mInflater = LayoutInflater.from(context);
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
        ViewHolder viewHolder=null;
        if(convertView==null)
        {
            viewHolder = new ViewHolder();

            convertView=mInflater.inflate(R.layout.item_layout,null);
            viewHolder.ivIcon= (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.ivIcon.setImageResource(R.drawable.actionbar_icon);
        viewHolder.tvTitle.setText(mlist_test2.get(position).getA_name());
        viewHolder.tvContent.setText(mlist_test2.get(position).getA_describe());
        return convertView;
    }
    class ViewHolder{
        TextView tvTitle,tvContent;
        ImageView ivIcon;
    }
}
