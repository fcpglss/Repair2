package util;

import android.content.Context;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;


import java.util.ArrayList;
import java.util.List;

import application.MyApplication;

import medusa.theone.waterdroplistview.view.WaterDropListView;
import model.Announcement;

import model.ResultBean;

import repair.com.repair.R;
import repari.com.adapter.ApplysAdapter;

/**
 * Created by hsp on 2016/12/12.
 * 从SharedPreference中加载json
 */

public class LoadListApplys extends AsyncTask<Void,Void,ApplysAdapter> {

    private static final String TAG = "LoadListApplys";

    List<String> viewpager_url = new ArrayList<>();
    ResultBean res = null;
    ConvenientBanner convenientBanner = null;
    WaterDropListView waterListView = null;
    ApplysAdapter adapter = null;
    Context context;

    public LoadListApplys(Context mcontext, WaterDropListView listview, ApplysAdapter adapter, ConvenientBanner convenientBanner) {
        context = mcontext;
        this.convenientBanner = convenientBanner;
        waterListView = listview;
        this.adapter = adapter;
    }


    @Override
    protected ApplysAdapter doInBackground(Void... voids) {
        try {
            String json=Util.loadFirstFromLocal(context);
            res = JsonUtil.jsonToBean(json);
            adapter = getBeanFromJson(res, viewpager_url, adapter);

        } catch (Exception e) {
            Log.d(TAG, "doInBackground: "+e.getMessage().toString());
        }
        return adapter;
    }

    @Override
    protected void onPostExecute(ApplysAdapter adapter) {
        setShowView(convenientBanner, res, waterListView, viewpager_url, adapter);
    }

    public void setShowView(ConvenientBanner convenientBanner,final ResultBean res, WaterDropListView waterDropListView, List<String> viewpager_url, final ApplysAdapter applysAdapters) {
        if (convenientBanner != null && res != null) {
            convenientBanner.setPageIndicator(new int[]{R.drawable.dot_unselected, R.drawable.dot_selected});
            convenientBanner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);


            convenientBanner.setPages(
                    new CBViewHolderCreator<LocalImageHolderView>() {
                        @Override
                        public LocalImageHolderView createHolder() {
                            return new LocalImageHolderView(MyApplication.getContext(), applysAdapters,res);
                        }
                    }, viewpager_url);
            applysAdapters.notifyDataSetChanged();
            waterDropListView.setAdapter(applysAdapters);
            waterDropListView.setOnItemClickListener(new WaterListViewListener(MyApplication.getContext(), res));
        } else {
            Toast.makeText(MyApplication.getContext(), "本地也没有数据，请检查网络", Toast.LENGTH_LONG).show();
        }

    }
    public  ApplysAdapter getBeanFromJson(ResultBean res ,List<String> viewpager_url,ApplysAdapter applysAdapter)
    {
        if (res == null) {
            return null;
        } else {
            for (Announcement announce : res.getAnnouncements()) {
                if (viewpager_url.size() > 3) {
                    viewpager_url.remove(0);
                }
                viewpager_url.add(announce.getImage_url());
            }
            if (applysAdapter == null && res != null) {
                applysAdapter = new ApplysAdapter(res, MyApplication.getContext());

            } else {
                applysAdapter.setList_Applys(res.getApplys());

            }

        }
        return applysAdapter;
    }


}