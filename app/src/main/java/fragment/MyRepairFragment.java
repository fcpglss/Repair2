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
import repair.com.repair.DetailsActivity;
import repair.com.repair.MainActivity;
import repair.com.repair.R;
import repari.com.adapter.MyRepairAdapter;
import util.JsonUtil;
import util.Util;

import static repair.com.repair.MainActivity.GET_JSON;
import static repair.com.repair.MainActivity.JSON_URL;
import static repair.com.repair.MainActivity.UP_APPLY;
import static repair.com.repair.MainActivity.list_uri;
import static util.NetworkUtils.isNetworkConnected;

/**
 * Created by hsp on 2016/11/27.
 */

public class MyRepairFragment extends LazyFragment2 {

    private static final String TAG = "MyRepairFragment";
    private LinearLayout llEmpty;

    private String phone = "phone";

    private ListView lvMyList = null;

    private MyRepairAdapter adapter = null;

    ResultBean myRes = null;
    private View view;
    private Response myResponse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayout() {
        return R.layout.frg_fragment_myrepair;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if(isVisible)
        {
            loadPhone();

            if (phone == null || phone.equals("")) {
                Log.d(TAG, "loadData: "+phone);
            } else {
                Log.d(TAG, "loadData: "+phone);
                upApply();
                initData();
            }
        }
    }



    protected void initViews(View view) {
        lvMyList = (ListView) view.findViewById(R.id.lv_my_lv);
        llEmpty = (LinearLayout) view.findViewById(R.id.lL_my_empty);
    }

    private void initData() {

        lvMyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.putExtra("repairId", myRes.getApplys().get(position).getId());
                startActivity(intent);
            }
        });
    }

    private void setView(ResultBean resultbean) {


        adapter = new MyRepairAdapter(resultbean, getActivity());
        if (resultbean.getApplys()==null || resultbean.getApplys().size()<=0) {
            llEmpty.setVisibility(View.VISIBLE);
            Log.d(TAG, "initView: 没有获取到我的数据");
        } else {
            lvMyList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            llEmpty.setVisibility(View.GONE);
            Log.d(TAG, "initView: 不可见");
        }
    }
    private void loadPhone() {
        SharedPreferences preferences = getActivity().getSharedPreferences("phoneData", getActivity().MODE_PRIVATE);
        phone = preferences.getString("phone", "");
    }

    private void upApply() {
        if (!isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), "请连接网络,我的报修页面使用本地数据", Toast.LENGTH_SHORT).show();
            String myjson = Util.loadMyResFromLocal(getActivity());
            myRes = JsonUtil.jsonToBean(myjson);
            if (myRes != null) {

                setView(myRes);
            }
        } else {
            if (true) {
                Log.d(TAG, "upApply: phone " + phone);

                Util.submit("phone",phone,JSON_URL).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        //   Toast.makeText(MyApplication.getContext(), "我的报修页面请求失败", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onError: ");
                        String myjson = Util.loadMyResFromLocal(getActivity());
                        myRes = JsonUtil.jsonToBean(myjson);

                        if (myRes != null) {
//                            Log.d(TAG, "upApply: image:"+myRes.getApplys().get(0).getA_imaes().get(0));
                            setView(myRes);
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        myResponse = JsonUtil.jsonToResponse(response);
                        myRes = myResponse.getResultBean();
                        Util.writeMyResToLocal(myRes, getActivity());
                        Log.d(TAG, "onResponse: " + response);

                        setView(myRes);
                    }
                });

            } else {
                Toast.makeText(MyApplication.getContext(), "未知错误", Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            Log.d(TAG, "onResume: 更新");
            adapter.notifyDataSetChanged();
            lvMyList.setAdapter(adapter);
        }
        Log.d(TAG, "onStart: ");
    }


}
