package util;

import android.content.Context;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import android.util.Log;

import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;


import java.util.ArrayList;
import java.util.List;

import application.MyApplication;

import medusa.theone.waterdroplistview.view.WaterDropListView;
import model.*;

import repair.com.repair.R;
import repari.com.adapter.ApplysAdapter;

/**
 * Created by hsp on 2016/12/12.
 */

public class ShowListApplys extends AsyncTask<String,Void,ApplysAdapter> {

     List<String> viewpager_url=new ArrayList<>();
    ConvenientBanner convenientBanner=null;
    ResultBean res=null;
    WaterDropListView waterListView=null;
    ApplysAdapter adapter=null;
    Context context=null;

    public ShowListApplys(Context mcontext,WaterDropListView listview, ApplysAdapter adapter,ConvenientBanner convenientBanner,ResultBean resultBean)
    {
        this.convenientBanner=convenientBanner;
        context=mcontext;
        waterListView =listview;
        this.adapter =adapter;
        res=resultBean;
    }

    @Override
    protected ApplysAdapter doInBackground(String... voids) {


       if(res==null)
       {
           return adapter;
       }
        adapter=getBeanFromJson(res,viewpager_url,adapter);
       // writeJsonToLocal(result_json, MyApplication.getContext());
        return adapter;
    }

    @Override
    protected void onPostExecute(ApplysAdapter adapter) {

       setShowView(convenientBanner,res,waterListView,viewpager_url,adapter);

    }
    public   void setShowView(ConvenientBanner convenientBanner,final ResultBean res,WaterDropListView waterDropListView,List<String> viewpager_url,final ApplysAdapter applysAdapters) {
        if (convenientBanner != null && res != null) {
            convenientBanner.setPageIndicator(new int[]{R.drawable.dot_unselected, R.drawable.dot_selected});
            convenientBanner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
            Log.d("Main", "setPage之前");

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
            Toast.makeText(MyApplication.getContext(), "请检查网络...", Toast.LENGTH_LONG).show();
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

