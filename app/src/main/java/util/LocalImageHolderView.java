package util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;

import java.io.Serializable;
import java.util.List;

import model.Announcement;
import model.ResultBean;
import repair.com.repair.AnnocementActivity;
import repari.com.adapter.ApplysAdapter;

/**
 * Created by hsp on 2016/12/15.
 */
public class LocalImageHolderView implements Holder<Integer> {
    private ImageView imageview;
    private Context mContext;
    private ResultBean res = null;

    public LocalImageHolderView(Context context, ApplysAdapter applysAdapter, ResultBean resultBean) {
        mContext = context;
        res = resultBean;

    }


    @Override
    public View createView(Context context) {
        imageview = new ImageView(context);
        imageview.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageview;
    }



    @Override
    public void UpdateUI(final Context context, final int position, Integer data) {
        try {
//            final  String url = data;
//            mImageLoader.bindBitmap(data,imageview,150,150);
//            Picasso.with(context).load(data).into(imageview);

            imageview.setImageResource(data);
            imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Announcement announcement = res.getAnnouncements().get(position);
                    List<Announcement> list = res.getAnnouncements();

                    Intent intent = new Intent(mContext, AnnocementActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("list", (Serializable) list);
                    mContext.startActivity(intent);
                }
            });
        } catch (Exception ee) {
            Log.d(TAG, "UpdateUI有异常:" + ee.getMessage().toString());
        }

    }

    private static final String TAG = "LocalImageHolderView";
}