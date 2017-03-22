package fragment;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import application.MyApplication;
import model.Place;
import model.Response;
import model.ResultBean;
import okhttp3.Call;
import repair.com.repair.AppraiseActivity;
import repair.com.repair.ChangeActivity;
import repair.com.repair.R;
import repari.com.adapter.MyRepairAdapter;
import util.JsonUtil;

import static repair.com.repair.MainActivity.GET_JSON;
import static repair.com.repair.MainActivity.JSON_URL;
import static repair.com.repair.MainActivity.UP_APPLY;
import static repair.com.repair.MainActivity.list_uri;
import static util.NetworkUtils.isNetworkConnected;

/**
 * Created by hsp on 2016/11/27.
 */

public class MyRepairFragment extends Fragment {

    private static final String TAG = "MyRepairFragment";
    private LinearLayout llEmpty;

    private String phone="phone";

    private ListView lvMyList=null;

    private MyRepairAdapter  adapter=null;

    ResultBean res = null;


    private Response myResponse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        return inflater.inflate(R.layout.frg_fragment_myrepair, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadPhone();
        if(phone==null||phone.equals(""))
        {
            Log.d(TAG, "onActivityCreated: "+phone);
        }
        else
        {
            Log.d(TAG, "onActivityCreated: "+phone);
            initData();
        }



    }

    private void initData() {
        upApply();

    }

    private void initView(ResultBean resultbean) {

        lvMyList= (ListView) getActivity().findViewById(R.id.lv_my_lv);
        llEmpty = (LinearLayout) getActivity().findViewById(R.id.lL_my_empty);
        adapter=new MyRepairAdapter(resultbean, MyApplication.getContext());
        if(resultbean!=null)
        {
            adapter.notifyDataSetChanged();
            lvMyList.setAdapter(adapter);
            llEmpty.setVisibility(View.GONE);
        }
        else
        {
            Log.d(TAG, "initView: 没有获取到我的数据");
        }


    }


    private void loadPhone()
    {
        SharedPreferences preferences =getActivity().getSharedPreferences("phoneData",getActivity().MODE_PRIVATE);
        phone=preferences.getString("phone","");
    }

    private void upApply() {


        if (!isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), "请连接网络", Toast.LENGTH_SHORT).show();

        } else {

            if (true) {

                Log.d(TAG, "upApply: phone " +phone);

                List<File> files = new ArrayList<>();

                submit(phone, files).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                     //   Toast.makeText(MyApplication.getContext(), "我的报修页面请求失败", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onError: ");
                    }
                    @Override
                    public void onResponse(String response, int id) {
                       myResponse=JsonUtil.jsonToResponse(response);
                        res=myResponse.getResultBean();
                        Log.d(TAG, "onResponse: "+response);
                        initView(res);
                    }
                });

            } else {
                Toast.makeText(MyApplication.getContext(), "未知错误", Toast.LENGTH_SHORT).show();
            }


        }

    }






    private RequestCall submit(String phone, List<File> files) {
        PostFormBuilder postFormBuilder = OkHttpUtils.post();
        for (int i = 0; i < files.size(); i++) {
            postFormBuilder.addFile("file", "file"+i+".jpg", files.get(i));
            Log.d(TAG, "submit: " +files.get(i).getPath());
        }


        postFormBuilder.addParams("phone", phone);
        if(files.size()>0)
        {
            postFormBuilder.url(UP_APPLY);
        }
        else
        {
            postFormBuilder.url(JSON_URL);
        }

        return postFormBuilder.build();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("MainFragment", "Statisc_onAttach");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("MainFragment", "Statisc_onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("MainFragment", "Statisc_onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MainFragment", "Statisc_onDestroy");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("MainFragment", "Statistics_onResume");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("MainFragment", "Statistics_onStart");
    }


}
