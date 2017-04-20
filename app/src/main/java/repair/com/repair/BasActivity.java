package repair.com.repair;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bigkoo.svprogresshud.SVProgressHUD;

import medusa.theone.waterdroplistview.view.WaterDropListView;
import model.Response;
import model.ResultBean;

/**
 * Created by hsp on 2017/4/19.
 */

public abstract  class BasActivity extends AppCompatActivity implements WaterDropListView.IWaterDropListViewListener,View.OnClickListener{

    private ResultBean adminRes;
    private Response adminResponse;
    private SVProgressHUD svProgressHUD;
    private WaterDropListView waterDropListView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        intViews();
        inViewProgrss();
    }
    public abstract  void intViews();

    protected  void inViewProgrss()
    {
        svProgressHUD = new SVProgressHUD(this);
        svProgressHUD.showWithStatus("加载中");
    }

    public abstract  void queryFromServer();

    protected  void updateView()
    {

    }








    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onClick(View v) {

    }
}
