package util;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import application.MyApplication;
import model.ResultBean;
import repair.com.repair.DetailsActivity;

/**
 * Created by hsp on 2016/12/14.
 */

public class WaterListViewListener implements AdapterView.OnItemClickListener {

    private static final String TAG = "WaterListViewListener";
    private ResultBean res=null;
    private Context mContext=null;

    public WaterListViewListener(Context context,ResultBean res) {

        mContext=context;
        this.res=res;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

//        Log.d(TAG, "onItemClick: "+(res==null));
//        Log.d(TAG, "onItemClick: "+res.getApplys().size());
//        Log.d(TAG, "onItemClick: "+(position-1));
        String  repairID = res.getApplys().get(position - 1).getId();

        Intent intent = new Intent(MyApplication.getContext(), DetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("repairId",repairID);
        mContext.startActivity(intent);
    }
}
