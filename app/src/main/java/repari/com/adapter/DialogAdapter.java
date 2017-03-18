package repari.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import repair.com.repair.R;

/**
 * Created by hsp on 2017/3/7.
 */

public class DialogAdapter extends BaseAdapter {

    LayoutInflater layoutInflater;
    List<String> list = new ArrayList<>();
    int layout;

    public DialogAdapter(Context context, List<String> list, int layout) {
        this.list = list;
        this.layout = layout;
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

        ViewHolder viewHolder;
        View view = convertView;


        if (null == view) {
            view = layoutInflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.tv_dialog_detail_text);
//            viewHolder.imageView1 = (ImageView) view.findViewById(R.id.image_view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.textView.setText(list.get(position));
//        viewHolder.imageView.setImageResource(R.mipmap.ic_launcher);
        return view;
    }

    private static class ViewHolder {
        TextView textView;
//        ImageView imageView1, imageView2;
    }

}
