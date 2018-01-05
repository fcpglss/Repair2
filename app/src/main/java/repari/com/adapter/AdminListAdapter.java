//package repari.com.adapter;
//
//import android.content.Context;
//import android.graphics.drawable.Drawable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.squareup.picasso.Picasso;
//
//import model.Apply;
//import model.ResultBean;
//import repair.com.repair.R;
//import util.Util;
//
///**
// * Created by hsp on 2017/4/8.
// */
//
//public class AdminListAdapter extends BaseAdapter {
//
//    private static final String TAG = "AdminListAdapter";
//    private static boolean isLoadImages;
//
//
//    private Context context;
//    private ResultBean resultBean;
//    private LayoutInflater inflater;
//
//
//    private boolean mCanGetBitmapFromNetWork = true;
//
//    private Drawable mDefaultBitmapDrawable;
//
//    public AdminListAdapter(Context context, ResultBean resultBean, boolean hasPic) {
//        this.context = context;
//        inflater = LayoutInflater.from(context);
//        mDefaultBitmapDrawable = context.getResources().getDrawable(R.mipmap.ic_launcher);
//        this.resultBean = resultBean;
//        isLoadImages = hasPic;
//    }
//
//    @Override
//    public int getCount() {
//        return resultBean.getApplys().size();
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
//        ViewHolder viewHolder;
//        View view = convertView;
//        Apply apply = resultBean.getApplys().get(position);
//        if (null == view) {
//            view = inflater.inflate(R.layout.admin_item_list, null);
//            viewHolder = new ViewHolder();
//
//            viewHolder.tvAdress = (TextView) view.findViewById(R.id.tv_admin_item_address);
//        //    viewHolder.tvcategory = (TextView) view.findViewById(R.id.tv_admin_item_category);
//            viewHolder.tvName = (TextView) view.findViewById(R.id.tv_admin_item_name);
//
//            viewHolder.tvTime = (TextView) view.findViewById(R.id.tv_admin_item_time);
//            viewHolder.tvDescribe = (TextView) view.findViewById(R.id.textView8);
//            viewHolder.tvTel = (TextView) view.findViewById(R.id.tv_admin_item_tel);
//            viewHolder.imgView = (ImageView) view.findViewById(R.id.img_admin_pic);
//            view.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) view.getTag();
//        }
//        viewHolder.tvName.setText(apply.getRepair());
//        viewHolder.tvTime.setText(Util.getDealTime(apply.getRepairTime()));
//      //  viewHolder.tvcategory.setText(apply.getClasss());
//        String addressTemp=Util.setAddress(apply,11,true);
//        viewHolder.tvAdress.setText(addressTemp);
//        viewHolder.tvTel.setText(apply.getTel());
//        String describeTemp=Util.setClass(apply,11,true);
//        viewHolder.tvDescribe.setText(describeTemp);
//
//        ImageView imageView = viewHolder.imgView;
//        final String tag = (String) imageView.getTag();
//        String photoUrl = "";
//
//        if (Util.getPhotoUrl(position, resultBean)) {
//            photoUrl = resultBean.getApplys().get(position).getA_imaes().get(0);
//        }
//         String uri = photoUrl;
//        if (!uri.equals(tag)) {
//            imageView.setImageDrawable(mDefaultBitmapDrawable);
//        }
//        setViewHldImg(photoUrl,imageView);
//
////        if (mCanGetBitmapFromNetWork && !photoUrl.equals("")) {
////            imageView.setTag(photoUrl);
////
////            Picasso.with(context).load(photoUrl).into(imageView);
////        }
//
//        return view;
//    }
//
//    public void setIsLoadImages(boolean hasPic) {
//        isLoadImages = hasPic;
//    }
//
//    private void setViewHldImg(String ImageString,ImageView imageView)
//    {
//        if(isLoadImages)
//        {
//
//            if (mCanGetBitmapFromNetWork && !ImageString.equals("")) {
//                imageView.setTag(ImageString);
//
//                Picasso.with(context).load(ImageString).into(imageView);
//            }
//        }
//    }
//
//
//
//    class ViewHolder {
//        TextView tvName, tvTime, tvAdress, tvDescribe, tvTel;
//        ImageView imgView;
//    }
//
//}
//
//
