package fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import application.MyApplication;
import medusa.theone.waterdroplistview.view.WaterDropListView;
import model.Test2;
import repair.com.repair.AnnocementActivity;
import repair.com.repair.DetailsActivity;
import repair.com.repair.MainActivity;
import repair.com.repair.R;
import repari.com.adapter.ApplysAdapter;
import util.HttpCallbackListener;
import util.HttpUtil;

/**
 * Created by hsp on 2016/11/27.
 */


public class MainFragment extends Fragment implements  WaterDropListView.IWaterDropListViewListener{

    private static final String HTTP_URL="http://192.168.31.201:81/get_data2.json";

    private List<ImageButton> mlist;
    private ConvenientBanner convenientBanner;

    private List<Integer> mlist_int = new ArrayList<>();

    private View view;

    private ListView mlistView;

    private  static List<Test2> mlist_Test2=new ArrayList<>();

    private List<String> list_string;

    private WaterDropListView waterDropListView;

    private MainActivity mainActivity;

    private ArrayAdapter<String>  adapter;

    private ApplysAdapter applysAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity=(MainActivity)context;
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

        if(mlist_int.size()<3)
        {
            mlist_int.add(R.drawable.fo);
            mlist_int.add(R.drawable.winter);
            mlist_int.add(R.drawable.home);

            convenientBanner = (ConvenientBanner) getActivity().findViewById(R.id.loop);

            convenientBanner.startTurning(2000);

             convenientBanner.setPageIndicator(new int[]{R.drawable.dot_unselected, R.drawable.dot_selected});

              convenientBanner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);

            convenientBanner.setPages(
                    new CBViewHolderCreator<LocalImageHolderView>() {
                        @Override
                        public LocalImageHolderView createHolder() {
                            return new LocalImageHolderView();
                        }
                    }, mlist_int);
        }else
        {
            convenientBanner = (ConvenientBanner) getActivity().findViewById(R.id.loop);
            convenientBanner.startTurning(2000);
             convenientBanner.setPageIndicator(new int[]{R.drawable.dot_unselected, R.drawable.dot_selected});
              convenientBanner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
            convenientBanner.setPages(
                    new CBViewHolderCreator<LocalImageHolderView>() {
                        @Override
                        public LocalImageHolderView createHolder() {
                            return new LocalImageHolderView();
                        }
                    }, mlist_int);
        }
        waterDropListView= (WaterDropListView)getActivity().findViewById(R.id.waterdrop_w);
        Log.d("MainFragment","Main_Fragment的OnActivityCreated list_Test2是否被更新，mlist_test2"+mlist_Test2.toString());

        //监听下拉刷新事件
        waterDropListView.setWaterDropListViewListener(this);
        waterDropListView.setPullLoadEnable(true);
        waterDropListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Test2 applys= mlist_Test2.get(position-1);
                Intent intent  = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("applys",applys);
                startActivity(intent);
            }
        });
        queryFromServer(null,null);
    }

    /**
     * 请求服务器数据
     *
     *
     */
    public void queryFromServer(final String code,final String type)
    {
        String url;
        HttpUtil.sendHttpRequest(HTTP_URL, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                final String result_json =response.toString();

                Log.d("Main_Fragment"," onFinish()调用: result="+response.toString());
                new AsyncTask<Void, Void, List<Test2>>() {
                    @Override
                    protected List<Test2> doInBackground(Void... params) {
                        Gson gson = new GsonBuilder().create();
                        Log.d("MainActivity"," gson对象="+gson.toString());
                        Type listtype2 = new TypeToken<List<Test2>>() {}.getType();
                        mlist_Test2 =gson.fromJson(result_json,listtype2);
                        Log.d("MainActivity"," mlist_applys="+mlist_Test2.toString());
                        return mlist_Test2;
                    }
                    @Override
                    protected void onPostExecute(List<Test2> result) {
                        super.onPostExecute(result);
                        Log.d("MainActivity"," onPostExecute mlist_test.get(0).getA_name="+mlist_Test2.get(0).getA_name());
                        if(applysAdapter==null)
                        {
                            applysAdapter=new ApplysAdapter(mlist_Test2,getActivity());
                            waterDropListView.setAdapter(applysAdapter);
                        }else
                        {
                            applysAdapter.setList_Applys(mlist_Test2);
                            applysAdapter.notifyDataSetChanged();
                            waterDropListView.setAdapter(applysAdapter);
                        }
                    }
                }.execute();
            }
            @Override
            public void onError(Exception e) {
                Log.d("MainActivity"," onEnrror调用: 请求网络失败\n"+e.getMessage().toString());
            }
        });
    }
    public void onRefresh() {
        // 刷新时执行异步任务
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SystemClock.sleep(1000);
                // 构造新的信息!
                queryFromServer(null,null);
                return null;
            }
            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                // 刷新列表
                waterDropListView.setAdapter(applysAdapter);
                applysAdapter.notifyDataSetChanged();
                waterDropListView.stopRefresh();
            }
        }.execute();
    }


    @Override
    public void onLoadMore() {

    }

    public class LocalImageHolderView implements Holder<Integer> {
        private ImageButton imageButton;

        @Override
        public View createView(Context context) {
            imageButton = new ImageButton(context);
            imageButton.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageButton;
        }


        @Override
        public void UpdateUI(Context context, int position, Integer data) {
        try {
            final int recource=data;
            imageButton.setBackgroundResource(data);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent =new Intent(getActivity(), AnnocementActivity.class);
                    Bundle bundle =new Bundle();
                //    bundle.putInt("img_id",recource);
                //    this.setArguments(bundle);
                    intent.putExtra("img_id",recource);
                    startActivity(intent);
                }
            });
        }
        catch (Exception ee)
        {
            ee.printStackTrace();
        }

        }
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
        super.onStop();
        Log.d("MainFragment", "Main_onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MainFragment", "Main_onDestroy");
    }

}
