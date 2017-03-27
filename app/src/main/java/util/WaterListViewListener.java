package util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import application.MyApplication;
import medusa.theone.waterdroplistview.view.WaterDropListView;
import model.Apply;
import model.Category;
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

        Log.d(TAG, "onItemClick: "+(res==null));
        String  repairID = res.getApplys().get(position - 1).getId();

        Intent intent = new Intent(MyApplication.getContext(), DetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra("repairId",repairID);

        mContext.startActivity(intent);
    }
}
