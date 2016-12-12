package repair.com.repair;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import java.util.ArrayList;
import java.util.List;

import db.RepairDB;
import fragment.ApplyFragment;
import fragment.MainFragment;
import fragment.StatisticsFragment;
import model.Applyss;
import model.Category;
import model.Test2;
import repari.com.adapter.FragmentAdapter;


public class MainActivity extends AppCompatActivity {


    public static final boolean REQUEST=false;

    private ViewPager mviewPager;
    private FragmentAdapter mpagerAdapter;
    private List<Fragment> mList;

    private ImageView miImageView;
    private LinearLayout mLinearLayout;
    private LinearLayout mContactLayout;
    private LinearLayout mFriendLayout;
    private EditText mSeachText; //
    private TextView mchat;
    private TextView mfriend;
    private TextView mcontact;



    private List<Applyss> mlist_applys=null;
    private List<Test2>   mlist_Test2 =new ArrayList<>();


    private ApplyFragment applyFragment;
    private MainFragment mainFragment;
    private StatisticsFragment statisticsFragment;
    private LinearLayout mTop2Layout;
    private List<String> list_string=new ArrayList<>();

    private static int Screen1_3;

    @Override
    protected void onDestroy() {
        stopService(new Intent(this,RuquestServer.class));
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
        Connection();
        initData();
       // queryFromServer(); Fragment里的广播没接收到
        TabListener();

    }
    public void queryFromServer()
    {
        SessionManager.getmInstance().writeToServer("query_tb_apply");
    }

    public void Connection()
    {
        Intent intent = new Intent(this,RuquestServer.class);
        startService(intent);
    }

    /**
     * ????????
     */
    private void init() {
        miImageView = (ImageView) findViewById(R.id.iv_tableline);

        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        Screen1_3 = metrics.widthPixels / 3;
        mviewPager = (ViewPager) findViewById(R.id.view_pager);


        mList = new ArrayList<Fragment>();

        mainFragment  = new MainFragment();
        applyFragment = new ApplyFragment();
        statisticsFragment = new StatisticsFragment();

        mList.add(mainFragment);
        mList.add(applyFragment);
        mList.add(statisticsFragment);

        mpagerAdapter = new FragmentAdapter(mList, getSupportFragmentManager());

        mchat = (TextView) findViewById(R.id.tv_chat);
        mfriend = (TextView) findViewById(R.id.tv_friend);
        mcontact = (TextView) findViewById(R.id.tv_contact);
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_chat);
        mFriendLayout = (LinearLayout) findViewById(R.id.ll_friend);
        mContactLayout = (LinearLayout) findViewById(R.id.ll_contact);
        mTop2Layout = (LinearLayout) findViewById(R.id.ll_top2);
        mSeachText = (EditText) findViewById(R.id.et_seach);

    }

    /**
     * ?????????
     */
    private void initData() {

        LayoutParams params = (LayoutParams) miImageView.getLayoutParams();
        params.width = Screen1_3;
        miImageView.setLayoutParams(params);

        mviewPager.setAdapter(mpagerAdapter);
        mviewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                resetView();
                switch (position) {
                    case 0:
                        mchat.setTextColor(Color.parseColor("#008000"));
                        break;
                    case 1:
                        mcontact.setTextColor(Color.parseColor("#008000"));
                        break;
                    case 2:
                        mfriend.setTextColor(Color.parseColor("#008000"));
                        break;
                }
            }

            //???????
            @Override
            public void onPageScrolled(int position, float offset, int arg2) {
                // TODO Auto-generated method stub
                Log.e("TAG", position + "," + offset + "," + arg2);
                LayoutParams lp = (LayoutParams) miImageView.getLayoutParams();
                lp.leftMargin = (int) ((position + offset) * Screen1_3);
                miImageView.setLayoutParams(lp);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }

    private void TabListener() {
        LinearLayoutListener linearLayoutListener = new LinearLayoutListener();
        mLinearLayout.setOnClickListener(linearLayoutListener);
        mContactLayout.setOnClickListener(linearLayoutListener);
        mFriendLayout.setOnClickListener(linearLayoutListener);
        mSeachText.setOnClickListener(linearLayoutListener);
    }
    /**
     * ?????Tab??TextView???
     */
    protected void resetView() {
        // TODO Auto-generated method stub
        mchat.setTextColor(Color.BLACK);
        mfriend.setTextColor(Color.BLACK);
        mcontact.setTextColor(Color.BLACK);

    }
    private class LinearLayoutListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_chat:
                    mviewPager.setCurrentItem(0);
                    break;
                case R.id.ll_friend:
                    mviewPager.setCurrentItem(2);
                    break;
                case R.id.ll_contact:
                    mviewPager.setCurrentItem(1);
                    break;
                case R.id.et_seach:
                    Intent intent = new Intent(MainActivity.this, SeachActivity.class);
                    startActivity(intent);
            }
        }

    }


}




