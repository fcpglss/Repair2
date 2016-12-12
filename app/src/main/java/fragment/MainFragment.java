package fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.util.logging.LogManager;

import application.MyApplication;
import medusa.theone.waterdroplistview.view.WaterDropListView;
import repair.com.repair.ConnectionManager;
import repair.com.repair.MinaTestActivity;
import repair.com.repair.SessionManager;
import model.Test2;
import repair.com.repair.AnnocementActivity;
import repair.com.repair.DetailsActivity;
import repair.com.repair.MainActivity;
import repair.com.repair.R;
import repari.com.adapter.ApplysAdapter;
import util.JsonToObject;

/**
 * Created by hsp on 2016/11/27.
 */


public class MainFragment extends Fragment implements WaterDropListView.IWaterDropListViewListener {

    private static final String HTTP_URL = "http://192.168.31.201:81/get_data2.json";

    private static boolean isFirst=true;
    private static String JSON = "";
    private List<ImageButton> mlist;
    private ConvenientBanner convenientBanner;

    private List<Integer> mlist_int = new ArrayList<>();

    private View view;

    private static List<Test2> mlist_Test2 = new ArrayList<>();

    private WaterDropListView waterDropListView;

    private MainActivity mainActivity;

    private ArrayAdapter<String> adapter;

    private ApplysAdapter applysAdapter = null;

    /**
     * 将MessageBroadcast接收到Json转入Handler
     * 在主界面更新UI,执行显示，更新操作
     */
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ShowListView();
                    break;
            }
        }
    };
    private MessageBroadcast receiver =
            new MessageBroadcast();

    /**
     * 最开始直接注册广播
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        registerBroadcast();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MainFragment", "MainFragment_onCreateVIew  mlist_int=" + mlist_int.size());
        return inflater.inflate(R.layout.fragment1, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(isFirst)
        {
            queryFromServer();
        }
        else
        {
            //从SD卡里取内容
        }

        Log.d("MainFragment", "MainFragment_onActivityCreated ");

        if (mlist_int.size() < 3) {
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
        } else {
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
        waterDropListView = (WaterDropListView) getActivity().findViewById(R.id.waterdrop_w);
        Log.d("MainFragment", "Main_Fragment的OnActivityCreated list_Test2是否被更新，mlist_test2" + mlist_Test2.toString());
        //监听下拉刷新事件
        waterDropListView.setWaterDropListViewListener(this);
        waterDropListView.setPullLoadEnable(true);
        waterDropListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (mlist_Test2 != null) {
                    Test2 applys = mlist_Test2.get(position - 1);
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra("applys", applys);
                    startActivity(intent);
                }

            }
        });
    }

    /**
     * 请求服务器数据
     */
    public void queryFromServer() {
        if (SessionManager.getmInstance().writeToServer("query_tb_apply"))
        {
            Toast.makeText(MyApplication.getContext(),"这是最新的数据",Toast.LENGTH_SHORT).show();
        }
        else
        {
            //加载SD卡里的内容：



            Toast.makeText(MyApplication.getContext(),"这是SD卡的数据,请检查网络",Toast.LENGTH_SHORT).show();

        }
        Log.d("MainFragment", "queryFromServer向服务器发出Apply表的请求");
    }


    public void onRefresh() {
        SystemClock.sleep(1000);
        queryFromServer();
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
                final int recource = data;
                imageButton.setBackgroundResource(data);
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), AnnocementActivity.class);
                        Bundle bundle = new Bundle();
                        intent.putExtra("img_id", recource);
                        startActivity(intent);
                    }
                });
            } catch (Exception ee) {
                ee.printStackTrace();
                Log.d("MainFragment", "异常：" + ee.getMessage().toString());
            }

        }
    }

    /**
     * 异步解析JSON，将Json解析为List_apply对象
     */

    private void ShowListView() {
        new AsyncTask<Void, Void, List<Test2>>() {
            @Override
            protected List<Test2> doInBackground(Void... params) {
                Gson gson = new GsonBuilder().create();
                Type listtype2 = new TypeToken<List<Test2>>() {
                }.getType();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                    }
                }).start();
                mlist_Test2 = gson.fromJson(JSON, listtype2);
                Log.d("MainActivity", " 将JSON解析为mlist_Test2对象=" + mlist_Test2.toString());
                return mlist_Test2;
            }

            @Override
            protected void onPostExecute(List<Test2> result) {
                super.onPostExecute(result);
                Log.d("MainActivity", " onPostExecute mlist_test.get(0).getA_name=" + mlist_Test2.get(0).getA_name());
                if (applysAdapter == null) {
                    applysAdapter = new ApplysAdapter(mlist_Test2, getActivity());
                    waterDropListView.setAdapter(applysAdapter);
                } else {
                    applysAdapter.setList_Applys(mlist_Test2);
                    applysAdapter.notifyDataSetChanged();
                    waterDropListView.setAdapter(applysAdapter);
                }
            }
        }.execute();
        /**
         * 做缓存
         */


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
        isFirst=false;
        Log.d("MainFragment", "Main_onStop");
    }

    @Override
    public void onDestroy() {
        unregisterBroadcast();
        isFirst=true;
        SessionManager.getmInstance().removeSession();
        super.onDestroy();
        Log.d("MainFragment", "Main_onDestroy");
    }

    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter("minatest.mina");
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(receiver, filter);
        Log.d("MainFragment", "成功注册广播");
    }

    private void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(receiver);
    }

    private class MessageBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            JSON = intent.getStringExtra("message");
            Log.d("MainFragment", "MainFragment的广播接收到了JSON=" + JSON);
            Message message = mhandler.obtainMessage();
            message.obj = JSON;
            message.what = 1;
            mhandler.sendMessage(message);
        }
    }

}
