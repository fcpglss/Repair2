package fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import repari.com.adapter.ApplysAdapter2;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.LoadListApplys;
import util.ShowListApplys;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * Created by hsp on 2016/11/27.
 */


public class MainFragment extends Fragment implements  WaterDropListView.IWaterDropListViewListener{

    private static final String HTTP_URL="http://192.168.31.201:81/get_data2.json";

    private List<ImageButton> mlist;
    private ConvenientBanner convenientBanner;

    private List<Integer> mlist_int = new ArrayList<>();

    private View view;

    private List<String> mUrList = new ArrayList<String>();


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
        waterDropListView.setWaterDropListViewListener(this);
        waterDropListView.setPullLoadEnable(true);

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
                //封装了 Json的解析还有图片的缓存,还有更新适配器的方法
                new ShowListApplys(getActivity(),mlist_Test2,waterDropListView,applysAdapter).execute(result_json);
            }
            @Override
            public void onError(Exception e) {
                Log.d("MainActivity"," onEnrror调用: 请求网络失败\n"+e.getMessage().toString());

               new LoadListApplys(getActivity(),mlist_Test2,waterDropListView,applysAdapter).execute();
            }
        });
    }
    public void onRefresh() {
        // 刷新时执行异步任务

        RefreshHttp();

    }
private void RefreshHttp()
{
    String url;
    HttpUtil.sendHttpRequest(HTTP_URL, new HttpCallbackListener() {
        @Override
        public void onFinish(final  String response) {
            new AsyncTask<Void, Void, ApplysAdapter>()
            {

                @Override
                protected ApplysAdapter doInBackground(Void... voids) {
                    final String result_json =response.toString();
                    mlist_Test2=JsonUtil.JsonToApply(result_json,mlist_Test2);
                    if(applysAdapter==null)
                    {
                        applysAdapter=new ApplysAdapter(mlist_Test2,getActivity());

                    }else
                    {
                        applysAdapter.setList_Applys(mlist_Test2);
//                        applysAdapter.notifyDataSetChanged();
                    }
                    return applysAdapter;
                }

                @Override
                protected void onPostExecute(ApplysAdapter applysAdapter) {
                    super.onPostExecute(applysAdapter);
                    applysAdapter.notifyDataSetChanged();
                    waterDropListView.setAdapter(applysAdapter);
                    waterDropListView.stopRefresh();
                }
            }.execute();

        }
        @Override
        public void onError(Exception e) {
            Log.d("MainActivity"," onEnrror调用: 请求网络失败\n"+e.getMessage().toString());
            waterDropListView.stopRefresh();
        }
    });
}


     public  static void  writeJsonToLocal(final String jsonString,final Context mContext)
     {
         new Thread(new Runnable() {
             @Override
             public void run() {
                 String json = jsonString;
                 SharedPreferences.Editor editor =mContext.getSharedPreferences("json_data",mContext.MODE_PRIVATE).edit();
                 editor.putString("json",json);
                 editor.commit();
             }
         }).start();

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

