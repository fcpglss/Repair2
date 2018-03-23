package fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import application.MyApplication;
import medusa.theone.waterdroplistview.view.WaterDropListView;
import model.Apply;
import model.Response;
import model.ResultBean;
import okhttp3.Call;
import okhttp3.Request;
import repair.com.repair.DetailsActivity;
import repair.com.repair.R;
import repari.com.adapter.MyRepairAdapter;
import util.AESUtil;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.Util;

import static constant.RequestUrl.ApplySearch;
import static constant.RequestUrl.ApplySearchMore;


/**
 * Created by hsp on 2016/11/27.
 */

public class MyRepairFragment extends LazyFragment2 implements WaterDropListView.IWaterDropListViewListener {

    public static final String TAG = "MyRepairFragment";


    private LinearLayout llEmpty, llContain;
    private EditText etName, etPhone;
    private Button btnSearch;

    private static boolean isRefrush = false;

    String appraise = "false";

    private static boolean moreFlag = false;

    private static boolean isMore = false;
    private static boolean ishasData = false;

    private static int start = 0;
    private static int end = 5;

    private String phone = "";
    private String name = "";


    public Response moreResponse = null;

    public ResultBean moreRes = null;

    private WaterDropListView lvMyList = null;

    private MyRepairAdapter adapter = null;

    private List<Apply> moreList = new ArrayList<>();

    private Response myRespon;

    private ResultBean firstRes;

    ResultBean myRes = new ResultBean();
    SVProgressHUD svProgressHUD = new SVProgressHUD(getActivity());
    private View view;
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    closeReflush();
                    // Toast.makeText(MyApplication.getContext(), myRespon.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    closeDiag();
                    Log.d(TAG, "handleMessage: 2");
                    //  updateView(0);
                    break;
                case 3:

                    //   Toast.makeText(getActivity(), "搜索完成", Toast.LENGTH_SHORT).show();
                    lvMyList.setVisibility(View.VISIBLE);
                    closeDiag();
                    ;
                    closeReflush();
                    //先清后填
                    setFirstApply(myRespon.getResultBean());
                    ishasData = myRespon.isEnd();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    break;

                case 5:
                    lvMyList.stopRefresh();
                    ishasData = myRespon.isEnd();
                    //    Toast.makeText(MyApplication.getContext(), "刷新成功", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "handleMessage: 5");
                    break;
                case 6:
                    //   Toast.makeText(getActivity(), "没有记录", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "handleMessage: 6");
                    lvMyList.stopLoadMore();
                    break;
                case 7:
                    moreList = moreRes.getApplys();
                    setMoreApply(moreList);
                    lvMyList.setSelection(start - 1);
                    Log.d(TAG, "handleMessage: response" + moreResponse.isEnd());
                    moreFlag = moreResponse.isEnd();
                    Log.d(TAG, "handleMessage: " + moreFlag);
                    closeReflush();
                    break;
                case 8:
                    //  Toast.makeText(getActivity(), myRespon.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    closeDiag();
                    break;
            }
        }
    };


    private void setMoreApply(List<Apply> applyList) {
        for (Apply apply : applyList) {
            myRes.getApplys().add(apply);
        }
    }

    private void closeReflush() {
        if (isRefrush) {
            lvMyList.stopRefresh();
            isRefrush = false;
        }
        if (isMore) {
            lvMyList.stopLoadMore();
            isMore = false;
        }
    }


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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            loadData();
        }
    }

    private void loadData() {
        adapter = new MyRepairAdapter(myRes, getActivity());
        lvMyList.setAdapter(adapter);

    }


    protected void initViews(View view) {

        myRes.setApplys(new ArrayList<Apply>());
        svProgressHUD = new SVProgressHUD(getActivity());
        lvMyList = (WaterDropListView) view.findViewById(R.id.lv_my_lv);
        lvMyList.setWaterDropListViewListener(MyRepairFragment.this);
        lvMyList.setPullLoadEnable(true);
        lvMyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.putExtra("repairId", myRes.getApplys().get(position - 1).getId());
                startActivity(intent);
            }
        });
//        llEmpty = (LinearLayout) view.findViewById(R.id.lL_my_empty);
        llContain = (LinearLayout) view.findViewById(R.id.ll_my_contain);
        etName = (EditText) view.findViewById(R.id.et_my_name);
        etPhone = (EditText) view.findViewById(R.id.et_my_phone);
        btnSearch = (Button) view.findViewById(R.id.btn_my_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreFlag = false;
                start = 0;
                end = 5;

                if (svProgressHUD == null) {
                    svProgressHUD = new SVProgressHUD(getActivity());
                    svProgressHUD.showWithStatus("搜索中");
                } else {
                    svProgressHUD.showWithStatus("搜索中");
                }
                phone = etPhone.getText().toString();
                name = etName.getText().toString();
                queryFromServer("phone", AESUtil.encode(phone), "name", AESUtil.encode(name), ApplySearch);

            }
        });

    }

    private void queryFromServer(String parms, String Vules, String params2, String Vules2, String url) {

        Util.submit(parms, Vules, params2, Vules2, url)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Response rp = new Response();
                        rp.setErrorType(-1);
                        rp.setError(true);
                        rp.setErrorMessage("网络异常,请检查网络");
                        myRespon = rp;
//                        Log.d(TAG, " onEnrror调用:" + e.getMessage());
                        mhandler.sendEmptyMessage(8);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //请求成功后获取到json
                        final String responseJson = response.toString();
//                        Log.d(TAG, "onFinish: " + responseJson);
                        myRespon = JsonUtil.jsonToResponse(responseJson);
                        if (myRespon.getErrorType() == -1) {
                            myRespon.setErrorMessage("没有找到该报修记录");
                            mhandler.sendEmptyMessage(2);
                            return;
                        }
                        if (myRespon.getResultBean().getApplys() == null || myRespon.getResultBean().getApplys().size() < 0) {
                            myRespon.setErrorMessage("没有找到该报修记录");
                            mhandler.sendEmptyMessage(2);
                            return;
                        } else {

                            mhandler.sendEmptyMessage(3);
                        }

                    }
                });
    }


    private void closeDiag() {
        if (svProgressHUD.isShowing()) {
            svProgressHUD.dismiss();
        }
    }


    private void setFirstApply(ResultBean resultBean) {

        if (myRes != null && myRes.getApplys().size() > 0) {
            myRes.getApplys().clear();
        }
        for (int i = 0; i < resultBean.getApplys().size(); i++) {
            myRes.getApplys().add(resultBean.getApplys().get(i));
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
        start = 0;
        end = 5;
        moreFlag = false;
        ishasData = false;

        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }


    @Override
    public void onResume() {

        String appraise2 = getActivity().getIntent().getStringExtra("appraise");
        Log.d(TAG, "onResume: appraise :" + appraise2);

        if (appraise2 != null && appraise2.equals("ok")) {
            appraise2 = "false";
            queryFromServer("phone", AESUtil.encode(phone), "name", AESUtil.encode(name), ApplySearch);
        }
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onRefresh() {
        isRefrush = true;
        moreFlag = false;
        start = 0;
        end = 5;
        queryFromServer("phone", AESUtil.encode(phone), "name", AESUtil.encode(name), ApplySearch);
    }


    @Override
    public void onLoadMore() {
        isMore = true;
        if (moreFlag || ishasData) {
            mhandler.sendEmptyMessage(6);
            Log.d(TAG, "onFinish: moreFlag:" + moreFlag);
            return;
        } else {
            Log.d(TAG, "onFinish: moreFlag:" + moreFlag);
            start = start + 5;
            end = end + 5;
        }
        // String request = SendMyRepairMore + "?start=" + start + "&&end=" + end + "&&phone=" + etPhone.getText().toString() +"&&name="+etName.getText().toString();
//        Log.d(TAG, "onLoadMore: ->" + request);

        String request = ApplySearchMore;

        String phoneNumber = AESUtil.encode(etPhone.getText().toString());
        String name = AESUtil.encode(etName.getText().toString());

        OkHttpUtils.get().
                url(request).
                addParams("start", String.valueOf(start)).
                addParams("end", String.valueOf(end)).
                addParams("phone", phoneNumber).
                addParams("name", name)
                .tag(this)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Response rp = new Response();
                        rp.setErrorType(-1);
                        rp.setError(true);
                        rp.setErrorMessage("网络异常，返回空值");
                        myRespon = rp;
                        Log.d(TAG, " onEnrror调用:" + e.getMessage());
                        mhandler.sendEmptyMessage(2);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        final String responseJson = response.toString();
                        Log.d(TAG, "onFinish: " + responseJson);
                        //解析json获取到Response;
                        moreResponse = JsonUtil.jsonToResponse(responseJson);
                        moreRes = moreResponse.getResultBean();
                        if (moreRes != null && moreRes.getApplys() != null && moreRes.getApplys().size() > 0) {
                            mhandler.sendEmptyMessage(7);
//
                        } else {
                            myRespon.setErrorType(-2);
                            myRespon.setError(false);
                            myRespon.setErrorMessage("没有数据");

                            mhandler.sendEmptyMessage(2);
                        }
                    }
                });

    }


}
