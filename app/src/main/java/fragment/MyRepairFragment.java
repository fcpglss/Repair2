package fragment;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.svprogresshud.SVProgressHUD;
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
import medusa.theone.waterdroplistview.view.WaterDropListView;
import model.Announcement;
import model.Apply;
import model.Place;
import model.Response;
import model.ResultBean;
import okhttp3.Call;
import repair.com.repair.AppraiseActivity;
import repair.com.repair.ChangeActivity;
import repair.com.repair.DetailsActivity;
import repair.com.repair.MainActivity;
import repair.com.repair.R;
import repari.com.adapter.ApplysAdapter;
import repari.com.adapter.MyRepairAdapter;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.LocalImageHolderView;
import util.Util;
import util.WaterListViewListener;

import static repair.com.repair.MainActivity.FRIST_URL;
import static repair.com.repair.MainActivity.GET_JSON;
import static repair.com.repair.MainActivity.JSON_URL;
import static repair.com.repair.MainActivity.SENDMORE_URL;
import static repair.com.repair.MainActivity.UP_APPLY;
import static repair.com.repair.MainActivity.list_uri;
import static util.NetworkUtils.isNetworkConnected;

/**
 * Created by hsp on 2016/11/27.
 */

public class MyRepairFragment extends LazyFragment2 implements WaterDropListView.IWaterDropListViewListener {

    private static final String TAG = "MyRepairFragment";

//    private static final String QUERYMYREPAIR = "http://192.168.31.201:8888/myserver2/QueryRepair";
//
//    private static final String SendMyRepairMore = "http://192.168.31.201:8888/myserver2/SendMyRepairMore";

    public static final String QUERYMYREPAIR = "http://192.168.43.128:8888/myserver2/QueryRepair";

    private static final String SendMyRepairMore = "http://192.168.43.128:8888/myserver2/SendMyRepairMore";
//    private static final String SendMyRepairPassword = "http://192.168.43.128:8888/myserver2/"


    private LinearLayout llEmpty, llContain;
    private EditText etName, etPhone;
    private Button btnSearch;

    private String phone = "phone";

    private SVProgressHUD svProgressHUD;

    private static boolean moreFlag = false;

    private static int start = 0;
    private static int end = 5;

    public Response moreResponse = null;

    public ResultBean moreRes = null;

    private WaterDropListView lvMyList = null;

    private MyRepairAdapter adapter = null;

    private List<Apply> moreList = new ArrayList<>();

    private Response myRespon;

    private ResultBean firstRes;
    ResultBean myRes = null;
    private View view;
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    Toast.makeText(MyApplication.getContext(), myRespon.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    closeDiag();
                    ;
                    Log.d(TAG, "handleMessage: 2");
                    //  updateView(0);
                    break;
                case 3:
                    Toast.makeText(getActivity(), "进来第一次请求网络调用,firstRes有值", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "handleMessage: 3");
                    updateView(0);
                    break;
                case 4:
                    lvMyList.stopRefresh();
//                    Toast.makeText(getActivity(), myRespon.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "handleMessage: 4");
                    updateView(0);
                    break;
                case 5:
                    lvMyList.stopRefresh();
                    Toast.makeText(MyApplication.getContext(), "刷新成功", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "handleMessage: 5");
                    break;
                case 6:
                    Toast.makeText(getActivity(), "下边已经没有数据了", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "handleMessage: 6");
                    lvMyList.stopLoadMore();
                    break;
                case 7:
                    moreList = moreRes.getApplys();
                    setMoreApply(moreList);
                    updateView(0);
                    lvMyList.setSelection(start);
                    Log.d(TAG, "handleMessage: response" + moreResponse.isEnd());
                    moreFlag = moreResponse.isEnd();
                    Log.d(TAG, "handleMessage: " + moreFlag);
                    lvMyList.stopLoadMore();
                    break;
                case 8:
                    Toast.makeText(getActivity(), myRespon.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    closeDiag();
                    break;
            }
        }
    };

    public MyRepairAdapter getBeanFromJson(ResultBean res, MyRepairAdapter applysAdapter) {
        if (res == null) {
            return null;
        }
        if (applysAdapter == null) {
            applysAdapter = new MyRepairAdapter(res, getActivity());
        } else {
            applysAdapter.notifyDataSetChanged();
        }
        return applysAdapter;
    }


    private void setMoreApply(List<Apply> applyList) {
        for (Apply apply : applyList) {
            myRes.getApplys().add(apply);
        }
    }


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
        if (isVisible) {
            loadData();
        }
    }

    private void loadData() {
//        loadPhone();
//        if (phone == null || phone.equals("")) {
//            Log.d(TAG, "loadData: "+phone);
//        } else {
//            Log.d(TAG, "loadData: "+phone);
//                upApply(0);
//               // initData();
//                svProgressHUD = new SVProgressHUD(getActivity());
//                svProgressHUD.showWithStatus("加载中");
//                Log.d(TAG, "第一次载入");
//        }
    }


    protected void initViews(View view) {
        lvMyList = (WaterDropListView) view.findViewById(R.id.lv_my_lv);
        lvMyList.setWaterDropListViewListener(MyRepairFragment.this);
        lvMyList.setPullLoadEnable(true);
//        llEmpty = (LinearLayout) view.findViewById(R.id.lL_my_empty);
        llContain = (LinearLayout) view.findViewById(R.id.ll_my_contain);
        etName = (EditText) view.findViewById(R.id.et_my_name);
        etPhone = (EditText) view.findViewById(R.id.et_my_phone);
        btnSearch = (Button) view.findViewById(R.id.btn_my_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (svProgressHUD == null) {
                    svProgressHUD = new SVProgressHUD(getActivity());
                    svProgressHUD.showWithStatus("搜索中");
                } else {
                    svProgressHUD.showWithStatus("搜索中");
                }

                Util.submit("phone", etPhone.getText().toString(), "name", etName.getText().toString(), QUERYMYREPAIR)
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Response rp = new Response();
                                rp.setErrorType(-1);
                                rp.setError(true);
                                rp.setErrorMessage("网络异常,请检查网络");
                                myRespon = rp;
                                Log.d(TAG, " onEnrror调用:" + e.getMessage());
                                mhandler.sendEmptyMessage(8);
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                //请求成功后获取到json
                                final String responseJson = response.toString();
                                Log.d(TAG, "onFinish: " + responseJson);
                                myRespon = JsonUtil.jsonToResponse(responseJson);
                                if (myRespon.getErrorType() == -1) {
                                    myRespon.setErrorMessage("没有找到该报修记录");
                                    mhandler.sendEmptyMessage(2);
                                    return;
                                }
                                postMessage(myRespon, 0);
                            }
                        });
            }
        });

    }

    private void updateView(int isRefrush) {
        //从内存中的数据更新；
        if (adapter != null) {
            if (myRes != null) {
                Log.d(TAG, "updateView: 内存中的Adapters没有被销毁,Res还在内存中，直接更新Water两个View");
                setView(myRes);
                Log.d(TAG, "handleMessage: show");
            } else {
                Toast.makeText(getActivity(), "没有该报修记录", Toast.LENGTH_SHORT).show();
            }
        }
        //内存中的applyAdapters已经被销毁，需重新创建一个
        else {
            Log.d(TAG, "updateView: 内存中的ApplyAdapters已经被销毁,重新构造ApplyAdapter,并且读本地数据更新View");
            adapter = getBeanFromJson(myRes, adapter);
            if (adapter == null) {
                Toast.makeText(getActivity(), "没有该报修记录", Toast.LENGTH_SHORT).show();
            } else {

                setView(myRes);
            }
        }
    }


    private void closeDiag() {
        if (svProgressHUD.isShowing()) {
            svProgressHUD.dismiss();
        }
    }


    private void setView(ResultBean resultbean) {
        closeDiag();
        if (resultbean.getApplys() == null || resultbean.getApplys().size() <= 0) {
//            llEmpty.setVisibility(View.VISIBLE);
            Log.d(TAG, "initView: 没有获取到我的数据");
        } else {
            adapter.notifyDataSetChanged();
            lvMyList.setAdapter(adapter);
            lvMyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getContext(), DetailsActivity.class);
                    intent.putExtra("repairId", myRes.getApplys().get(position - 1).getId());
                    startActivity(intent);
                }
            });
//            llEmpty.setVisibility(View.GONE);
            Log.d(TAG, "initView: 不可见");
        }
    }

    private void loadPhone() {
        SharedPreferences preferences = getActivity().getSharedPreferences("phoneData", getActivity().MODE_PRIVATE);
        phone = preferences.getString("phone", "");
    }

    private void upApply(final int isRefrush) {
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

                Util.submit("phone", phone, JSON_URL).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Response rp = new Response();
                        rp.setErrorType(-1);
                        rp.setError(true);
                        rp.setErrorMessage("网络异常，返回空值");
                        myRespon = rp;
                        Log.d(TAG, " onEnrror调用:" + e.getMessage());
                        if (isRefrush == 1) {
                            mhandler.sendEmptyMessage(4);
                        } else {
                            mhandler.sendEmptyMessage(8);
                        }

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //请求成功后获取到json
                        final String responseJson = response.toString();
                        Log.d(TAG, "onFinish: " + responseJson);
                        //解析json获取到Response;
                        myRespon = JsonUtil.jsonToResponse(responseJson);
                        postMessage(myRespon, isRefrush);
                    }
                });

            } else {
                Toast.makeText(MyApplication.getContext(), "未知错误", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void postMessage(Response response, int isRefrush) {

        if (response == null) {
            response = new Response();
            response.setErrorType(-2);
            response.setError(false);
            response.setErrorMessage("服务器维护");
            Log.d(TAG, "queryFromServer请求成功：但res没有值，抛到到主线程尝试从本地加载res更新UI,messages=4");
            if (isRefrush == 1) {
                mhandler.sendEmptyMessage(4);
            } else {
                mhandler.sendEmptyMessage(2);
            }
        } else {
            firstRes = response.getResultBean();
            setFirstApply(firstRes);
            if (myRes != null) {
                if (isRefrush == 1) {
                    Log.d(TAG, "刷新调用 queryFromServer请求成功：res有值，抛到到主线程更新UI,messages=3");
                    mhandler.sendEmptyMessage(5);
                } else {
                    Log.d(TAG, "postMessage: 第一次进来调用,不需要停止刷新");
                    mhandler.sendEmptyMessage(3);
                }
            } else {
                if (isRefrush == 1) {
                    mhandler.sendEmptyMessage(4);
                } else {
                    mhandler.sendEmptyMessage(2);
                }
            }
        }
    }


    private void setFirstApply(ResultBean resultBean) {
        if (myRes != null && myRes.getApplys().size() > 0) {
            for (int i = 0; i < resultBean.getApplys().size(); i++) {
                if (myRes.getApplys().size() > i) {
                    myRes.getApplys().remove(i);
                    myRes.getApplys().add(i, resultBean.getApplys().get(i));
                    continue;
                }
                myRes.getApplys().add(i, resultBean.getApplys().get(i));
            }
        } else {
            myRes = resultBean;
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
//        if (adapter != null) {
//            Log.d(TAG, "onResume: 更新");
//            adapter.notifyDataSetChanged();
//            lvMyList.setAdapter(adapter);
//        }
//        Log.d(TAG, "onStart: ");
    }


    @Override
    public void onRefresh() {
        upApply(1);
    }

    @Override
    public void onLoadMore() {
        if (moreFlag) {
            mhandler.sendEmptyMessage(6);
            Log.d(TAG, "onFinish: moreFlag:" + moreFlag);
            return;
        } else {
            Log.d(TAG, "onFinish: moreFlag:" + moreFlag);
            start = start + 5;
            end = end + 5;
        }
        String request = SendMyRepairMore + "?start=" + start + "&&end=" + end + "&&phone=" + phone;
        Log.d(TAG, "onLoadMore: ->" + request);

        HttpUtil.sendHttpRequest(SendMyRepairMore + "?start=" + start + "&&end=" + end + "&&phone=" + phone, new HttpCallbackListener() {
            @Override
            public void onFinish(String responseString) {
                //请求成功后获取到json
                final String responseJson = responseString.toString();
                Log.d(TAG, "onFinish: " + responseJson);
                //解析json获取到Response;
                moreResponse = JsonUtil.jsonToResponse(responseJson);
                moreRes = moreResponse.getResultBean();
                if (moreRes != null) {
                    mhandler.sendEmptyMessage(7);
//                    Util.writeJsonToLocal(res, MyApplication.getContext());
                } else {
                    myRespon.setErrorType(-2);
                    myRespon.setError(false);
                    myRespon.setErrorMessage("连接服务器成功，但返回的数据为空或是异常");
                    Log.d(TAG, "queryFromServer请求成功：但res没有值，抛到到主线程尝试从本地加载res更新UI,messages=4");
                    mhandler.sendEmptyMessage(6);
                }
            }

            @Override
            public void onError(Exception e) {
                Response rp = new Response();
                rp.setErrorType(-1);
                rp.setError(true);
                rp.setErrorMessage("网络异常，返回空值");
                myRespon = rp;
                Log.d(TAG, " onEnrror调用:" + e.getMessage());
                mhandler.sendEmptyMessage(2);
            }
        });
    }

}
