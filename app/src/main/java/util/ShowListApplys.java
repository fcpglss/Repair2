package util;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import application.MyApplication;
import fragment.MainFragment;
import medusa.theone.waterdroplistview.view.WaterDropListView;
import model.Test2;
import repair.com.repair.DetailsActivity;
import repari.com.adapter.ApplysAdapter;

import static fragment.MainFragment.writeJsonToLocal;

/**
 * Created by hsp on 2016/12/12.
 */

public class ShowListApplys extends AsyncTask<String,Void,ApplysAdapter> {
    List<Test2> mlist_apply=null;
    WaterDropListView waterListView=null;
    ApplysAdapter adapter=null;
    Context context;

    public ShowListApplys(Context mcontext,List<Test2> mlist,WaterDropListView listview, ApplysAdapter adapter)
    {
        context=mcontext;
        mlist_apply=mlist;
        waterListView =listview;
        this.adapter =adapter;
    }

    @Override
    protected ApplysAdapter doInBackground(String... voids) {

        String result_json=voids[0];
        mlist_apply=JsonUtil.JsonToApply(result_json,mlist_apply);

        if(adapter==null)
        {
            adapter=new ApplysAdapter(mlist_apply,context);

        }else
        {
            adapter.setList_Applys(mlist_apply);
            adapter.notifyDataSetChanged();
        }

        MainFragment.writeJsonToLocal(result_json, MyApplication.getContext());
        return adapter;
    }

    @Override
    protected void onPostExecute(ApplysAdapter adapter) {
        super.onPostExecute(adapter);

        waterListView.setAdapter(adapter);
        waterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Test2 applys= mlist_apply.get(position-1);
                Intent intent =new Intent(context,DetailsActivity.class);
                if(applys==null)
                {
                    Log.d("MainFragment","applys为空");
                }
                intent.putExtra("applys",applys);
                context.startActivity(intent);
            }
        });
    }

}

