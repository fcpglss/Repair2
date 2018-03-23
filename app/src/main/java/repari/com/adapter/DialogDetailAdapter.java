//package repari.com.adapter;
//
//import android.content.Context;
//import android.util.Log;
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
//import model.Category;
//import repair.com.repair.R;
//
///**
// * Created by hsp on 2017/3/7.
// */
//
//public class DialogDetailAdapter extends BaseAdapter {
//    private static final String TAG = "DialogDetailAdapter";
//
//    LayoutInflater layoutInflater;
//    List<String> list = new ArrayList<>();
//    List<Category> listCategory = new ArrayList<>();
//    int layout;
//
//    public DialogDetailAdapter(Context context, List<String> list, List<Category> listCategory  , int layout) {
//        this.list = list;
//        this.layout = layout;
//        this.listCategory = listCategory;
//        layoutInflater = LayoutInflater.from(context);
//    }
//
//    @Override
//    public int getCount() {
//        return list.size();
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
//        ViewHolder viewHolder;
//        View view = convertView;
//
//        if (null == view) {
//            view = layoutInflater.inflate(layout, parent, false);
//            viewHolder = new ViewHolder();
//            viewHolder.textView = (TextView) view.findViewById(R.id.dialog_detail_text1);
//            viewHolder.imageView = (ImageView) view.findViewById(R.id.dialog_detail_pic1);
//            view.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) view.getTag();
//        }
//
//            Log.d(TAG, "getView: "+list.get(position));
//            viewHolder.textView.setText(list.get(position));
//            viewHolder.imageView.setTag(listCategory.get(position).getC_imageurl());
//
//        return view;
//    }
//
//    private static class ViewHolder {
//        TextView textView;
//        ImageView imageView;
//    }
//
//}
