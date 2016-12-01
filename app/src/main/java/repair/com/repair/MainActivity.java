package repair.com.repair;

import android.content.Intent;
import android.graphics.Color;
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
    private LinearLayout mTop2Layout;
    private EditText mSeachText; //
    private TextView mchat;
    private TextView mfriend;
    private TextView mcontact;

    private RepairDB repairDB; // 操作数据类

    private List<Applyss> mlist_applys=null;  //申请表数据集合

    private List<Test2>   mlist_Test2 =new ArrayList<>();

    private List<Category> mlist_categorys; //申报类型的数据集合

    private ApplyFragment applyFragment;
    private MainFragment mainFragment;
    private StatisticsFragment statisticsFragment;

    private List<String> list_string=new ArrayList<>();

    private static int Screen1_3;//��Ļ��ȵ�1/3//


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
        initData();

        repairDB=RepairDB.getInstance(this);
        TabListener();

    }

    /**
     * ��ʼ��ʵ��
     */
    private void init() {
        miImageView = (ImageView) findViewById(R.id.iv_tableline);

        //��ȡ��Ļ�Ŀ��
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
     * ��ʼ������
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

            //�ı�ָʾ��
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
     * ��ʼ��Tab��TextView��ɫ
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




