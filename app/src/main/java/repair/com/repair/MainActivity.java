package repair.com.repair;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


import fragment.ApplyFragment;
import fragment.HomeFragment;
import fragment.MyRepairFragment;
import repari.com.adapter.FragmentAdapter;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";


    public static int count = 5;

    public static final int TAKE_PHOTO_RAW = 1;
    public static final int REQUEST_IMAGE = 2;


    public static int windowWitch;
    public static int windowHeigth;


    public static List<Uri> list_uri = new ArrayList<>();


    ImageView ivHome;

    ImageView ivRepair;

    ImageView ivMy;

    private ViewPager mviewPager;
    private FragmentAdapter mpagerAdapter;
    private List<Fragment> mList;


    private ImageView miImageView;
    private LinearLayout mLinearLayout;
    private LinearLayout mContactLayout;
    private LinearLayout mFriendLayout;

    private TextView mchat;
    private TextView mfriend;
    private TextView mcontact;

    private ApplyFragment applyFragment;
    private HomeFragment mainFragment;

    private MyRepairFragment myRepairFragment;

    private static int Screen1_3;

    private TextView tvHead;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //获取屏幕宽高
        setWitchAndHeigth();

        init();
        initData();
        TabListener();


    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    private void init() {
        miImageView = (ImageView) findViewById(R.id.iv_tableline);
        ivHome = (ImageView) findViewById(R.id.iv_home);
        ivRepair = (ImageView) findViewById(R.id.iv_repair);
        ivMy = (ImageView) findViewById(R.id.iv_my);
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        Screen1_3 = metrics.widthPixels / 3;
        mviewPager = (ViewPager) findViewById(R.id.view_pager);


        mList = new ArrayList<Fragment>();

        mainFragment = new HomeFragment();
        applyFragment = new ApplyFragment();

        myRepairFragment = new MyRepairFragment();

        mList.add(mainFragment);
        mList.add(applyFragment);
        mList.add(myRepairFragment);


        mpagerAdapter = new FragmentAdapter(mList, getSupportFragmentManager());

        mchat = (TextView) findViewById(R.id.tv_chat);
        mfriend = (TextView) findViewById(R.id.tv_friend);
        mcontact = (TextView) findViewById(R.id.tv_contact);
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_chat);
        mFriendLayout = (LinearLayout) findViewById(R.id.ll_friend);
        mContactLayout = (LinearLayout) findViewById(R.id.ll_contact);


        tvHead = (TextView) findViewById(R.id.tv_head);


    }


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
                resetPic();
                switch (position) {
                    case 0:
                        mchat.setTextColor(Color.parseColor("#1D89E5"));
                        resetPic();
                        ivHome.setBackgroundResource(R.drawable.home_fill_light);
                        break;
                    case 1:

                        mcontact.setTextColor(Color.parseColor("#1D89E5"));
                        resetPic();
                        ivRepair.setBackgroundResource(R.drawable.form_fill_light);
                        break;
                    case 2:
                        mfriend.setTextColor(Color.parseColor("#1D89E5"));
                        resetPic();
                        ivMy.setBackgroundResource(R.drawable.my_fill);
                        break;
                }
            }


            @Override
            public void onPageScrolled(int position, float offset, int arg2) {

                LayoutParams lp = (LayoutParams) miImageView.getLayoutParams();
                lp.leftMargin = (int) ((position + offset) * Screen1_3);
                miImageView.setLayoutParams(lp);

                switch (position) {
                    case 0:
                        tvHead.setText("首页");
                        break;
                    case 1:
                        tvHead.setText("我要报修");
                        break;
                    case 2:
                        tvHead.setText("我的报修");
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }

    private void resetPic() {
        ivMy.setBackgroundResource(R.drawable.my);
        ivHome.setBackgroundResource(R.drawable.home_light);
        ivRepair.setBackgroundResource(R.drawable.form_light);
    }

    private void TabListener() {
        LinearLayoutListener linearLayoutListener = new LinearLayoutListener();
        mLinearLayout.setOnClickListener(linearLayoutListener);
        mContactLayout.setOnClickListener(linearLayoutListener);
        mFriendLayout.setOnClickListener(linearLayoutListener);

    }


    protected void resetView() {
        // TODO Auto-generated method stub
        mchat.setTextColor(Color.parseColor("#757575"));
        mfriend.setTextColor(Color.parseColor("#757575"));
        mcontact.setTextColor(Color.parseColor("#757575"));
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
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == TAKE_PHOTO_RAW) {
            MainActivity.list_uri.add(Uri.fromFile(ApplyFragment.fileUri));
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE) {
            list_uri.add(data.getData());

        }
    }

    private void setWitchAndHeigth() {
        //获取屏幕宽高dm
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        windowWitch = dm.widthPixels;
        windowHeigth = dm.heightPixels;
    }


    /**
     * 第一种解决办法 通过监听keyUp
     *
     * @param keyCode
     * @param event
     * @return
     */
    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {

            //图片放大 变小
            if (applyFragment.RlIsVisable() != null && applyFragment.RlIsVisable().getVisibility() == View.VISIBLE) {
                if (applyFragment.bigImageView() != null) {
                    applyFragment.bigImageView().setBackground(null);
                }
                applyFragment.RlIsVisable().setVisibility(View.GONE);
                return true;
            } else {

                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;
                    return true;
                } else {
                    this.finish();
                    System.exit(0);
                }
            }

        }

        return super.onKeyUp(keyCode, event);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        super.onNewIntent(intent);
    }
}




