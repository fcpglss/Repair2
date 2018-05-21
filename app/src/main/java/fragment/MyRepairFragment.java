package fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;


import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;


import cn.pedant.SweetAlert.SweetAlertDialog;
import model.Apply;
import model.Response;
import model.ResultBean;
import network.Api;
import okhttp3.Call;
import okhttp3.Request;
import repair.com.repair.DetailsActivity;
import repair.com.repair.R;
import repari.com.adapter.MyRepairAdapter;
import util.AESUtil;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.RxBindingUtil;
import util.Util;

import static constant.RequestUrl.ApplySearch;
import static constant.RequestUrl.ApplySearchMore;


/**
 * Created by hsp on 2016/11/27.
 */

public class MyRepairFragment extends LazyFragment2 {

    public static final String TAG = "MyRepairFragment";

    private EditText etName, etPhone;
    private Button btnSearch;


    String appraise = "false";


    private static int page = 5;
    private static int start = 0;
    private static int end = page;


    private String phone = "";
    private String name = "";


    private ListView listView = null;

    SmartRefreshLayout smartRefreshLayout;

    private List<Apply> applyList = new ArrayList<>();

    private MyRepairAdapter adapter = null;


    private Response response;
    private ResultBean res;


    SweetAlertDialog svProgressHUD;


    private boolean isHasData;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        appraise = "faile";
        phone = "";
        name = "";
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayout() {
        return R.layout.frg_fragment_myrepair;
    }


    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            loadData();
        }
    }

    private void loadData() {
        adapter = new MyRepairAdapter(applyList, getActivity());
        listView.setAdapter(adapter);

    }


    protected void initViews(View view) {

        svProgressHUD = new SweetAlertDialog(getActivity(),SweetAlertDialog.PROGRESS_TYPE);
        smartRefreshLayout = (SmartRefreshLayout) view.findViewById(R.id.refreshLayout);
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMore();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                smartRefreshLayout.finishRefresh();
            }
        });
        listView = (ListView) view.findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.putExtra("repairId", applyList.get(position).getId());
                startActivity(intent);
            }
        });

        etName = (EditText) view.findViewById(R.id.et_my_name);
        etPhone = (EditText) view.findViewById(R.id.et_my_phone);
        btnSearch = (Button) view.findViewById(R.id.btn_my_search);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                start = 0;
                end = page;
                if (svProgressHUD == null) {
                    svProgressHUD = new SweetAlertDialog(getActivity(),SweetAlertDialog.PROGRESS_TYPE);
                    svProgressHUD.setTitleText("搜索中");
                } else {
                    svProgressHUD.setTitleText("搜索中");
                }
                svProgressHUD.show();
                phone = etPhone.getText().toString();
                name = etName.getText().toString();
                search(AESUtil.encode(phone), AESUtil.encode(name));

            }
        });

    }

    //搜索记录
    private void search(String phone, String name) {
        Api.search(phone, name).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.d(TAG, "onError: " + e.getMessage());
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                closeDiag();
            }

            @Override
            public void onResponse(String resStr, int id) {
                response = JsonUtil.jsonToResponse(resStr);
                closeDiag();
                if (response != null) {
                    res = response.getResultBean();
                    isHasData = response.isEnd();
                    if (res != null) {
                        if (res.getApplys() != null) {
                            applyList.removeAll(applyList);
                            applyList.addAll(res.getApplys());
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), "没找到报修记录", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }


    private void closeDiag() {
        if (svProgressHUD != null && svProgressHUD.isShowing()) {
            svProgressHUD.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        start = 0;
        end = page;
        isHasData = false;
        super.onDestroy();
    }


    @Override
    public void onResume() {

        String appraise2 = getActivity().getIntent().getStringExtra("appraise");
        if (appraise2 != null && appraise2.equals("ok")) {
            appraise2 = "false";
            //重新显示
            search(AESUtil.encode(phone), AESUtil.encode(name));
        }
        super.onResume();
    }


    public void loadMore() {

        if (isHasData) {
            smartRefreshLayout.finishLoadMore();
            return;
        } else {
            start = end;
            end = end + page;
            String phoneNumber = AESUtil.encode(etPhone.getText().toString());
            String name = AESUtil.encode(etName.getText().toString());
            Api.loadMore(phoneNumber, name, start, end, getActivity()).execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.d(TAG, "onError: " + e.getMessage());
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    smartRefreshLayout.finishLoadMore();
                }

                @Override
                public void onResponse(String resStr, int id) {
                    response = JsonUtil.jsonToResponse(resStr);
                    if (response != null) {
                        res = response.getResultBean();
                        isHasData = response.isEnd();
                        if (res != null) {
                            if (res.getApplys() != null) {
                                applyList.addAll(res.getApplys());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                    smartRefreshLayout.finishLoadMore();
                }
            });
        }


    }
}