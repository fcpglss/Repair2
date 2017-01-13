package repair.com.repair;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
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

import application.MyApplication;
import camera.CalculateImage;
import fragment.ApplyFragment;
import fragment.MainFragment;
import fragment.StatisticsFragment;
import repari.com.adapter.FragmentAdapter;

import static repair.com.repair.R.id.imageView;


public class MainActivity extends AppCompatActivity {

    public static final String JSON_URL = "http://192.168.43.128:8888/myserver1/servlet/action";
    public static final String UP_APPLY="http://192.168.43.128:8888/myserver1/Upload2";
    public static final String GET_JSON="http://192.168.43.128:8888/myserver1/GetJson";

    public static final int TAKE_PHOTO_RAW = 1;
    public static final int REQUEST_IMAGE =2 ;
    public static List<Uri> list_uri=new ArrayList<>();

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

    private ApplyFragment applyFragment;
    private MainFragment mainFragment;
    private StatisticsFragment statisticsFragment;

    private static int Screen1_3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.d("Apply_Fragment","onCreate");
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Apply_Activity"," resultCode="+RESULT_OK+"  requestCode="+requestCode);
        if(resultCode == RESULT_OK && requestCode== TAKE_PHOTO_RAW){
            Log.d("Apply_Activity", "outputFileUri:    " + list_uri.get(0).toString());
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE) {
            list_uri.add(data.getData());
            Log.i("Apply_Activity", "GalleryUri:    " + data.getData().getPath());
        }
    }


}




