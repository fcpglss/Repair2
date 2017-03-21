package fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;

import java.util.ArrayList;
import java.util.List;

import application.MyApplication;
import imagehodler.ImageLoader;
import medusa.theone.waterdroplistview.view.WaterDropListView;
import model.Announcement;
import model.Apply;
import model.Response;
import model.ResultBean;
import repair.com.repair.AnnocementActivity;
import repair.com.repair.DetailsActivity;
import repair.com.repair.MainActivity;
import repair.com.repair.R;
import repari.com.adapter.ApplysAdapter;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.LoadListApplys;
import util.LocalImageHolderView;
import util.ShowListApplys;
import util.Util;
import util.WaterListViewListener;

import static repair.com.repair.MainActivity.FRIST_URL;
import static repair.com.repair.MainActivity.JSON_URL;

/**
 * Created by hsp on 2016/11/27.
 */


public class MainFragment extends Fragment implements WaterDropListView.IWaterDropListViewListener {

    private static final String TAG = "MainFragment";
    
    private static  boolean isFirst=true;

    private  ConvenientBanner convenientBanner=null;

    private List<Integer> mlist_int = new ArrayList<>();

    public  ResultBean res =null;

    private  WaterDropListView waterDropListView;

    private  ApplysAdapter applysAdapter;

    private  List<String> viewpager_url = new ArrayList<>();

    private Response response;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Toast.makeText(MyApplication.getContext(), "请检查网络...", Toast.LENGTH_LONG).show();
                    waterDropListView.stopRefresh();
                    break;

            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        Log.d("MainFragment", "Main_onCreateVIew  mlist_int=" + mlist_int.size());

        return inflater.inflate(R.layout.fragment1, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        /**
         * 还需要判断有没有网络,有网络,do 下面的判断,没有网络就从本地获取信息;
         */
        if(isFirst)
        {
            queryFromServer(null,null);
            Log.d(TAG, "第一次载入");
        }
        else
        {
            if(res!=null)
            {
                //封装了图片的缓存,还有更新适配器的方法
                new ShowListApplys(getActivity(), waterDropListView, applysAdapter, convenientBanner,res).execute();
                Log.d(TAG, "不是第一次载入，从内存中读取res");
            }
            else
            {
                new LoadListApplys(getActivity(), waterDropListView, applysAdapter, convenientBanner).execute();
                Log.d(TAG, "不是第一次载入，从本地address_data文件中读取");
            }
        }



    }

    /**
     * 请求服务器数据
     */
    public void queryFromServer(final String code, final String type) {

        HttpUtil.sendHttpRequest(FRIST_URL, new HttpCallbackListener() {
            @Override
            public void onFinish(String responseString) {
                //请求成功后获取到json
                final String responseJson = responseString.toString();
                //解析json获取到Response;
                response=JsonUtil.jsonToResponse(responseJson);

                res=response.getResultBean();
                if(res!=null)
                {
                    //封装了图片的缓存,还有更新适配器的方法
                    new ShowListApplys(getActivity(), waterDropListView, applysAdapter, convenientBanner,res).execute();
                    //将resultbean的数据写入本地"json"的文件。
                    Util.writeJsonToLocal(res,MyApplication.getContext());
                }
                else
                {
                    response.setErrorType(-2);
                    response.setError(false);
                    response.setErrorMessage("连接服务器成功，但返回的数据为空或是异常");
                    checkError();
                }
            }
            @Override
            public void onError(Exception e) {

                Response rp= new Response();
                rp.setErrorType(-1);
                rp.setError(true);
                rp.setErrorMessage("网络异常，返回空值");
                response=rp;
                Log.d("MainActivity", " onEnrror调用:" +e.getMessage());
                checkError();
            }
        });
    }

    //判断请求或返回过程中是否有错，若没有错则为0,则从本地文件里面读出数据
    private void checkError()
    {
        if(response.getErrorType()!=0)
        {
          //  Toast.makeText(MyApplication.getContext(),response.getErrorMessage(),Toast.LENGTH_SHORT).show();
            if(res!=null)
            {
                new ShowListApplys(getActivity(), waterDropListView, applysAdapter, convenientBanner,res).execute();
            }
            else
            {
                new LoadListApplys(getActivity(), waterDropListView, applysAdapter, convenientBanner).execute();
            }
        }

    }



    public void onRefresh() {

        RefreshHttp();

    }

    private void RefreshHttp() {

        HttpUtil.sendHttpRequest(JSON_URL, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                new AsyncTask<Void, Void, ApplysAdapter>() {

                    @Override
                    protected ApplysAdapter doInBackground(Void... voids) {
                        String result_json = response;
                        res = JsonUtil.jsonToBean(result_json);
//                        Log.d("Main", "json:" + result_json + "\n" + "公告:" + res.getAnnouncements().get(0).getImage_url());
                        applysAdapter=getBeanFromJson(res,viewpager_url,applysAdapter);
                       writeJsonToLocal(result_json, MyApplication.getContext());
                        return applysAdapter;
                    }

                    @Override
                    protected void onPostExecute(final ApplysAdapter applysAdapter) {

                        setShowView(convenientBanner,res,waterDropListView,viewpager_url,applysAdapter);
                        waterDropListView.stopRefresh();
                    }
                }.execute();

            }

            @Override
            public void onError(Exception e) {
                Log.d("MainActivity", " onEnrror调用: 请求网络失败\n" + e.getMessage().toString());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);

                            mhandler.sendEmptyMessage(1);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }).start();


            }
        });
    }


    public  void writeJsonToLocal(final String jsonString, final Context mContext) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String json = jsonString;
                SharedPreferences.Editor editor = mContext.getSharedPreferences("json_data", mContext.MODE_PRIVATE).edit();
                editor.putString("json", json);
                editor.commit();
            }
        }).start();

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

public   ApplysAdapter getBeanFromJson(ResultBean res ,List<String> viewpager_url,ApplysAdapter applysAdapter)
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




    @Override
    public void onLoadMore() {

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("MainFragment", "Main_onResume");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("MainFragment", "Main_onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("MainFragment", "Main_onPause");
    }

    @Override
    public void onStop() {
        isFirst=false;

        super.onStop();
        Log.d("MainFragment", "Main_onStop");
    }

    @Override
    public void onDestroy() {
        isFirst=true;
        super.onDestroy();
        Log.d("MainFragment", "Main_onDestroy");
    }

    private void init() {
        convenientBanner = (ConvenientBanner) getActivity().findViewById(R.id.loop);
        convenientBanner.startTurning(5000);
        waterDropListView = (WaterDropListView) getActivity().findViewById(R.id.waterdrop_w);
        waterDropListView.setWaterDropListViewListener(MainFragment.this);
        waterDropListView.setPullLoadEnable(true);

    }


}

