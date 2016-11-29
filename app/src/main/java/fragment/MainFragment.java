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
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;

import java.util.ArrayList;
import java.util.List;

import medusa.theone.waterdroplistview.view.WaterDropListView;
import repair.com.repair.AnnocementActivity;
import repair.com.repair.R;

/**
 * Created by hsp on 2016/11/27.
 */


public class MainFragment extends Fragment implements  WaterDropListView.IWaterDropListViewListener{

    private List<ImageButton> mlist;
    private ConvenientBanner convenientBanner;

    private List<Integer> mlist_int = new ArrayList<>();

    private View view;

    private ListView mlistView;

    private List<String> list_string;
    private WaterDropListView waterDropListView;


    private ArrayAdapter<String>  adapter;

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
      //  mlistView= (ListView) getActivity().findViewById(R.id.lv_listView);
      //  mlistView.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,list_string));
        waterDropListView= (WaterDropListView)getActivity().findViewById(R.id.waterdrop_w);
        adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,list_string);
        waterDropListView.setAdapter(adapter);
        waterDropListView.setWaterDropListViewListener(this);
        waterDropListView.setPullLoadEnable(true);
        Log.d("MainFragment", "onActivityCreated  mlist_string=" + list_string.size());

    }
    public void setList(List<String> list)
    {
        list_string=list;
    }

    public void onRefresh() {
        // 刷新时执行异步任务
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SystemClock.sleep(1000);
                // 构造新的信息!

                list_string.add("Test");
                return null;
            }
            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                // 刷新列表
                adapter.notifyDataSetChanged();
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
