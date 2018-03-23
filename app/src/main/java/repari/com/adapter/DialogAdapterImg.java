//package repari.com.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import repair.com.repair.R;
//
///**
// * Created by hsp on 2017/3/13.
// */
//
//public class DialogAdapterImg extends BaseAdapter  {
//
//    LayoutInflater layoutInflater;
//    List<String> list = new ArrayList<>();
//    ImageView imageView;
//    int layout;
//
//    public DialogAdapterImg(Context context, ImageView imageView, int layout) {
//        this.imageView = imageView;
//        this.layout = layout;
//        layoutInflater = LayoutInflater.from(context);
//    }
//
//    @Override
//    public int getCount() {
//        return 1;
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return position;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        DialogAdapterImg.ViewHolder viewHolder;
//        View view = convertView;
//
//
//        if (null == view) {
//            view = layoutInflater.inflate(layout, parent, false);
//            viewHolder = new DialogAdapterImg.ViewHolder();
//            viewHolder.imageView = (ImageView) view.findViewById(R.id.detail_show_big_img);
//            view.setTag(viewHolder);
//        } else {
//            viewHolder = (DialogAdapterImg.ViewHolder) view.getTag();
//        }
//        viewHolder.imageView.setImageDrawable(imageView.getDrawable());
////        viewHolder.imageView.setImageResource(R.mipmap.ic_launcher);
//        return view;
//    }
//
//    private static class ViewHolder {
//        ImageView imageView;
//    }
//
//}
