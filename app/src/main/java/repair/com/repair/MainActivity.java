package repair.com.repair;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fragment.ApplyFragment;
import fragment.MainFragment;
import fragment.MyRepairFragment;
import model.Response;
import repari.com.adapter.FragmentAdapter;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";


    public static int count = 5;
    public static final String JSON_URL = "http://192.168.31.201:8888/myserver2/servlet/action";
    public static final String UP_APPLY = "http://192.168.31.201:8888/myserver2/Upload2";//
    public static final String GET_JSON = "http://192.168.31.201:8888/myserver2/ResponseClient";
    public static final String FRIST_URL = "http://192.168.31.201:8888/myserver2/FirstRequest";
    public static final String SENDMORE_URL = "http://192.168.31.201:8888/myserver2/sendmore";

   // public static final String FRIST_URL="http://192.168.43.128:8888/myserver2/FirstRequest";
   // public static final String JSON_URL = "http://192.168.43.128:8888/myserver2/servlet/action";
   // public static final String UP_APPLY="http://192.168.43.128:8888/myserver2/Upload2";//
  //  public static final String GET_JSON="http://192.168.43.128:8888/myserver2/ResponseClient";
  //  public static final String SENDMORE_URL = "http://192.168.43.128:8888/myserver2/sendmore";

    public static final int TAKE_PHOTO_RAW = 1;
    public static final int REQUEST_IMAGE = 2;


    public static int windowWitch;
    public static int windowHeigth;


    public static Uri photoUri;

    public static List<Uri> list_uri = new ArrayList<>();
    public static Uri[] arrayUri2 = new Uri[3];

    public static final boolean REQUEST = false;
    @BindView(R.id.iv_home)
    ImageView ivHome;
    @BindView(R.id.iv_repair)
    ImageView ivRepair;
    @BindView(R.id.iv_my)
    ImageView ivMy;




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

    private ApplyFragment applyFragment;
    private MainFragment mainFragment;
    // private StatisticsFragment statisticsFragment;
    private MyRepairFragment myRepairFragment;

    private static int Screen1_3;

    private TextView tvHead;

    private ImageView ivMenu;


    //菜单项
    private TextView itemAdmin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate");
        //获取屏幕宽高
        setWitchAndHeigth();
        init();
        initData();
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

        mainFragment = new MainFragment();
        applyFragment = new ApplyFragment();
        //  statisticsFragment = new StatisticsFragment();
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
        mTop2Layout = (LinearLayout) findViewById(R.id.ll_top2);
        mSeachText = (EditText) findViewById(R.id.et_seach);

        tvHead = (TextView) findViewById(R.id.tv_head);

        //菜单选项

        ivMenu = (ImageView) findViewById(R.id.iv_menu);
        //菜单图片 现在先直接跳转管理页面
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


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

            //�ı�ָʾ��
            @Override
            public void onPageScrolled(int position, float offset, int arg2) {
                // TODO Auto-generated method stub
                Log.e("TAG", position + "," + offset + "," + arg2);
                LayoutParams lp = (LayoutParams) miImageView.getLayoutParams();
                lp.leftMargin = (int) ((position + offset) * Screen1_3);
                miImageView.setLayoutParams(lp);

                switch (position) {
                    case 0:
                        tvHead.setText("首页");
                        break;
                    case 1:
                        tvHead.setText("我的报修");
                        break;
                    case 2:
                        tvHead.setText("报修列表");
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
//        mSeachText.setOnClickListener(linearLayoutListener);
    }

    /**
     * ��ʼ��Tab��TextView��ɫ
     */
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
                case R.id.et_seach:
                    //Intent intent = new Intent(MainActivity.this, SeachActivity.class);
                    //startActivity(intent);

            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Apply_Activity", " resultCode=" + RESULT_OK + "  requestCode=" + requestCode);
        if (resultCode == RESULT_OK && requestCode == TAKE_PHOTO_RAW) {
            MainActivity.list_uri.add(Uri.fromFile(ApplyFragment.fileUri));
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE) {
            list_uri.add(data.getData());
            Log.d(TAG, "addItem");
            Log.i("Apply_Activity", "GalleryUri:    " + data.getData().getPath());
        }
    }

    private void setWitchAndHeigth() {
        //获取屏幕宽高dm
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        windowWitch = dm.widthPixels;
        windowHeigth = dm.heightPixels;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {


            if (applyFragment.RlIsVisable() != null && applyFragment.RlIsVisable().getVisibility() == View.VISIBLE) {
                applyFragment.RlIsVisable().setVisibility(View.GONE);
//                linearLayoutDetail.setBackgroundColor(Color.rgb(211,211,211));
            } else {
                Log.d(TAG, "onKeyDown: 返回了main");
                this.finish();
            }
        }


//        return super.onKeyDown(keyCode,event);
        return true;
    }


}




