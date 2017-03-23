package fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import java.util.ArrayList;
import java.util.List;
import application.MyApplication;
import medusa.theone.waterdroplistview.view.WaterDropListView;
import model.Announcement;
import model.Apply;
import model.Response;
import model.ResultBean;
import repair.com.repair.R;
import repari.com.adapter.ApplysAdapter;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.LocalImageHolderView;
import util.Util;
import util.WaterListViewListener;
import static repair.com.repair.MainActivity.FRIST_URL;
import static repair.com.repair.MainActivity.JSON_URL;
import static repair.com.repair.MainActivity.SENDMORE_URL;

/**
 * Created by hsp on 2016/11/27.
 */


public class MainFragment extends Fragment implements WaterDropListView.IWaterDropListViewListener {

    private static final String TAG = "MainFragment";

    private static int start=0;
    private static int end=5;

    private static boolean moreFlag=false;


    private static final int  ERROR=2; //第一次网络不同标志

    private static final int  SUCCESS=3;//第一次请求网络成功标志

    private static final int NOTDATA=4;//连接服务器成功，但是没有获取到数据

    private static boolean isRefresh=false;

    private static  boolean isFirst=true;

    private  ConvenientBanner convenientBanner=null;

    private List<Integer> mlist_int = new ArrayList<>();

    public  ResultBean res =null;

    public ResultBean moreRes =null;

    public Response  moreResponse=null;

    private  WaterDropListView waterDropListView;

    private  ApplysAdapter applysAdapter;

    private  List<String> viewpager_url = new ArrayList<>();

    private Response response;

    private List<Apply>  firstList=new ArrayList<>();

    private List<Apply>  moreList= new ArrayList<>();

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    if(isRefresh)
                    {
                        isRefresh=false;
                        waterDropListView.stopRefresh();
                    }
                    Toast.makeText(MyApplication.getContext(),response.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    updateView();
                    break;
                case 3:
                    if(isRefresh)
                    {
                        isRefresh=false;
                        applysAdapter=getBeanFromJson(res,viewpager_url,applysAdapter);
                        updateView();
                        waterDropListView.stopRefresh();
                        Toast.makeText(MyApplication.getContext(), "刷新成功", Toast.LENGTH_LONG).show();
                        break;
                    }
                        Toast.makeText(getActivity(),"进来第一次请求网络调用,firstRes有值",Toast.LENGTH_SHORT).show();
                        applysAdapter=getBeanFromJson(res,viewpager_url,applysAdapter);
                        updateView();
                    break;
                case 4:
                    if(isRefresh)
                    {
                        isRefresh=false;
                        waterDropListView.stopRefresh();
                    }
                    Toast.makeText(getActivity(),"请求网络成功但，firstRes没有值",Toast.LENGTH_SHORT).show();
                    updateView();
                    break;
                case 5:
                    Toast.makeText(MyApplication.getContext(), "刷新成功", Toast.LENGTH_LONG).show();
                    waterDropListView.stopRefresh();
                    break;
                case 6:
                    Toast.makeText(getActivity(),"下边已经没有数据了",Toast.LENGTH_SHORT).show();
                    waterDropListView.stopLoadMore();
                    break;
                case 7:
                    updateView();
                    waterDropListView.stopLoadMore();
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

        Log.d(TAG, "Main_onCreateVIew  mlist_int=" + mlist_int.size());

        return inflater.inflate(R.layout.fragment1, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: "+isRefresh);
        init();
        /**
         * 还需要判断有没有网络,有网络,do 下面的判断,没有网络就从本地获取信息;
         */
        if(isFirst)
        {
            queryFromServer(FRIST_URL,SUCCESS);
            Log.d(TAG, "第一次载入");
        }
        else
        {
            Log.d(TAG, "不是第一次载入，从内存或本地中读取res");
            updateView();
        }
    }

    /**
     * 请求服务器数据
     */
    public void queryFromServer(String url, final int handlerType) {
        HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String responseString) {
                //请求成功后获取到json
                final String responseJson = responseString.toString();
                //解析json获取到Response;
                response=JsonUtil.jsonToResponse(responseJson);
                res=response.getResultBean();
                if(res!=null)
                {
                    Log.d(TAG, "queryFromServer请求成功：res有值，抛到到主线程更新UI,messages=3");
                    mhandler.sendEmptyMessage(handlerType);
                    Util.writeJsonToLocal(res,MyApplication.getContext());
                }
                else
                {
                    response.setErrorType(-2);
                    response.setError(false);
                    response.setErrorMessage("连接服务器成功，但返回的数据为空或是异常");
                    Log.d(TAG, "queryFromServer请求成功：但res没有值，抛到到主线程尝试从本地加载res更新UI,messages=4");
                    mhandler.sendEmptyMessage(NOTDATA);
                }
            }
            @Override
            public void onError(Exception e) {
                Response rp= new Response();
                rp.setErrorType(-1);
                rp.setError(true);
                rp.setErrorMessage("网络异常，返回空值");
                response=rp;
                Log.d(TAG, " onEnrror调用:" +e.getMessage());
                checkError();
            }
        });
    }

    //判断请求或返回过程中是否有错，若没有错则为0,则从本地文件里面读出数据
    private void checkError()
    {
        if(response.getErrorType()!=0)
        {
            mhandler.sendEmptyMessage(ERROR);
        }
    }

    //刷新waterListView
    public void onRefresh() {
        isRefresh=true;
        queryFromServer(FRIST_URL,SUCCESS);
    }

    //先尝试从内存中读数据，若没有数据则尝试从本地读
    private void updateView()
    {
        //从内存中的数据更新；
        if(applysAdapter!=null)
        {
            if(res!=null&&convenientBanner!=null)
            {
                Log.d(TAG, "updateView: 内存中的ApplyAdapters没有被销毁,fistRes还在内存中，直接更新Water,Conven两个View");
                setView();
            }
            else
            {
                Log.d(TAG, "updateView: 内存中的ApplyAdapters没有被销毁，但是firstRes已经被销毁了,需从本地读取firstRes");
                String json=Util.loadFirstFromLocal(getActivity());
                res=JsonUtil.jsonToBean(json);
                applysAdapter.setList_Applys(res.getApplys());
                setView();
            }
        }
        //内存中的applyAdapters已经被销毁，需重新创建一个
        else
        {
            Log.d(TAG, "updateView: 内存中的ApplyAdapters已经被销毁,重新构造ApplyAdapter,并且读本地数据更新View");
            String json=Util.loadFirstFromLocal(getActivity());
            res=JsonUtil.jsonToBean(json);
            applysAdapter=getBeanFromJson(res,viewpager_url,applysAdapter);
            updateView();
        }
    }
    //设置View属性
    private void setView()
    {
        convenientBanner.setPageIndicator(new int[]{R.drawable.dot_unselected, R.drawable.dot_selected});
        convenientBanner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
        Log.d(TAG, "setPage之前");
        convenientBanner.setPages(
                new CBViewHolderCreator<LocalImageHolderView>() {
                    @Override
                    public LocalImageHolderView createHolder() {
                        return new LocalImageHolderView(MyApplication.getContext(),applysAdapter,res);
                    }
                }, viewpager_url);

        applysAdapter.notifyDataSetChanged();
        waterDropListView.setAdapter(applysAdapter);
        waterDropListView.setOnItemClickListener(new WaterListViewListener(MyApplication.getContext(), res));
    }


    public  ApplysAdapter getBeanFromJson(ResultBean res ,List<String> viewpager_url,ApplysAdapter applysAdapter)
    {
        if (res == null) {
            return null;
        }
        for (Announcement announce : res.getAnnouncements()) {
            if (viewpager_url.size() > 3) {
                viewpager_url.remove(0);
            }
            viewpager_url.add(announce.getImage_url());
        }
        if (applysAdapter == null) {
            applysAdapter = new ApplysAdapter(res, MyApplication.getContext());
            applysAdapter.notifyDataSetChanged();
        } else {
            applysAdapter.notifyDataSetChanged();
    }
    return applysAdapter;
    }

    //// FIXME: 2017/3/22
    @Override
    public void onLoadMore() {

        if(moreFlag)
        {
           mhandler.sendEmptyMessage(6);
            Log.d(TAG, "onFinish: moreFlag:"+moreFlag);
            return ;
        }
        else
        {
            Log.d(TAG, "onFinish: moreFlag:"+moreFlag);
            start=start+5;
            end=end+5;
        }

        HttpUtil.sendHttpRequest(SENDMORE_URL+"?start="+start+"&&end="+end, new HttpCallbackListener() {

                @Override
                public void onFinish(String responseString) {
                    //请求成功后获取到json
                    final String responseJson = responseString.toString();
                    //解析json获取到Response;
                    moreResponse=JsonUtil.jsonToResponse(responseJson);
                    moreRes=moreResponse.getResultBean();
                    if(moreRes!=null)
                    {
                        moreList=moreRes.getApplys();
                        setMoreApply(moreList);
                        mhandler.sendEmptyMessage(7);
                        Util.writeJsonToLocal(res,MyApplication.getContext());
                    }
                    else
                    {
                        response.setErrorType(-2);
                        response.setError(false);
                        response.setErrorMessage("连接服务器成功，但返回的数据为空或是异常");
                        Log.d(TAG, "queryFromServer请求成功：但res没有值，抛到到主线程尝试从本地加载res更新UI,messages=4");
                        mhandler.sendEmptyMessage(NOTDATA);
                    }
                }
                @Override
                public void onError(Exception e) {
                    Response rp= new Response();
                    rp.setErrorType(-1);
                    rp.setError(true);
                    rp.setErrorMessage("网络异常，返回空值");
                    response=rp;
                    Log.d(TAG, " onEnrror调用:" +e.getMessage());
                    checkError();
                }
            });
}
    private void setMoreApply(List<Apply> applyList)
    {
        for(Apply apply :applyList)
        {
            res.getApplys().add(apply);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Main_onResume");
    }
    @Override
    public void onStart() {

        super.onStart();
        Log.d(TAG, "Main_onStart");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Main_onPause");
    }

    @Override
    public void onStop() {
        isFirst=false;
        isRefresh=false;
        super.onStop();
        Log.d(TAG, "Main_onStop");
    }

    @Override
    public void onDestroy() {
        isFirst=true;
        super.onDestroy();
        Log.d(TAG, "Main_onDestroy");
    }

    private void init() {
        convenientBanner = (ConvenientBanner) getActivity().findViewById(R.id.loop);
        convenientBanner.startTurning(5000);
        waterDropListView = (WaterDropListView) getActivity().findViewById(R.id.waterdrop_w);
        waterDropListView.setWaterDropListViewListener(MainFragment.this);
        waterDropListView.setPullLoadEnable(true);
    }


}

