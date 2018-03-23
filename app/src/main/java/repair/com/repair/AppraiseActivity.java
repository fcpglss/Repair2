package repair.com.repair;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import application.MyApplication;
import model.Apply;
import okhttp3.Call;
import util.AESUtil;
import util.JsonUtil;
import util.Util;

import static constant.RequestUrl.ApplyAppraise;


/**
 * Created by hsp on 2017/3/14.
 */

public class AppraiseActivity extends AppCompatActivity {
    private static final String TAG = "AppraiseActivity";


    //报修人姓名，电话，报修地址（从区域到房间），其他信息，维修人员
    TextView tvAppraiseName,tvAppraisePhone,tvAppraiseAddress,tvAppraiseOther,tvAppraiseServerName;
    //星星
    ImageView star1,star2,star3,star4,star5;
    ImageView speedstar1,speedstar2,speedstar3,speedstar4,speedstar5;
    ImageView qualitystar1,qualitystar2,qualitystar3,qualitystar4,qualitystar5;
    ImageView attutideystar1,attutideystar2,attutideystar3,attutideystar4,attutideystar5;

    StarCount all = new StarCount();
    StarCount speed = new StarCount();
    StarCount quality = new StarCount();
    StarCount attitude = new StarCount();
    //维修质量
    int qualitCount=5;
    //维修速度
    int speedCount=5;
    //维修态度
    int attitudeCount=5;
    //总 默认好评
    public int starCount=5;


    String accpetServer="";
    //评价内容
    EditText etAppraiseContent;
    //提交
    Button btnSubmit;
    Apply apply=null;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    Toast.makeText(AppraiseActivity.this,accpetServer, Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent();

                    break;
                case 3:
                    Toast.makeText(AppraiseActivity.this,accpetServer, Toast.LENGTH_SHORT).show();
                    break;

        }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appraise);
        //初始化控件
        init();
        //获取apply,主要是需要 报修人姓名，电话，报修地址（从区域到房间），其他信息，维修人员，放入全局变量
        getApply();
        //绑定数据
        bindData();
        //
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    //绑定数据 设置页面显示和点击事件
    private void bindData() {
        //直接设置text
        tvAppraiseName.setText(AESUtil.decode(apply.getRepair()));
        tvAppraisePhone.setText(AESUtil.decode(apply.getTel()));
        //地址由四部分组成
        tvAppraiseAddress.setText(getAddress());
        //其他可以不填写 所以没有数据显示为空
        tvAppraiseOther.setText(apply.getRepairDetails());
        tvAppraiseServerName.setText(apply.getLogisticMan());
        //星星点击
        starOnClick();
        //提交按钮点击
        btnSubmitOnClick();
    }

    //获取维修地点
    private String getAddress() {

        StringBuilder sb = new StringBuilder();
        sb.append(apply.getArea());
        sb.append(apply.getDetailArea());
        sb.append(apply.getRoom());
        return sb.toString();
    }

    //获取数据Apply 这个页面只用到apply
    private void getApply() {
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        apply = (Apply) b.get("apply");

    }

    //初始化
    private void init() {
        tvAppraiseName = (TextView) findViewById(R.id.tv_appraise_name);
        tvAppraisePhone = (TextView) findViewById(R.id.tv_appraise_phone);
        tvAppraiseAddress = (TextView) findViewById(R.id.tv_appraise_address);
        tvAppraiseOther = (TextView) findViewById(R.id.tv_appraise_other);
        tvAppraiseServerName = (TextView) findViewById(R.id.tv_appraise_server_name);
        star1 = (ImageView) findViewById(R.id.iv_appraise_star1);
        star2 = (ImageView) findViewById(R.id.iv_appraise_star2);
        star3 = (ImageView) findViewById(R.id.iv_appraise_star3);
        star4 = (ImageView) findViewById(R.id.iv_appraise_star4);
        star5 = (ImageView) findViewById(R.id.iv_appraise_star5);

        speedstar1 = (ImageView) findViewById(R.id.iv_speed_star1);
        speedstar2 = (ImageView) findViewById(R.id.iv_speed_star2);
        speedstar3 = (ImageView) findViewById(R.id.iv_speed_star3);
        speedstar4 = (ImageView) findViewById(R.id.iv_speed_star4);
        speedstar5 = (ImageView) findViewById(R.id.iv_speed_star5);

        qualitystar1 = (ImageView) findViewById(R.id.iv_quality_star1);
        qualitystar2 = (ImageView) findViewById(R.id.iv_quality_star2);
        qualitystar3 = (ImageView) findViewById(R.id.iv_quality_star3);
        qualitystar4 = (ImageView) findViewById(R.id.iv_quality_star4);
        qualitystar5 = (ImageView) findViewById(R.id.iv_quality_star5);

        attutideystar1 = (ImageView) findViewById(R.id.iv_attitude_star1);
        attutideystar2 = (ImageView) findViewById(R.id.iv_attitude_star2);
        attutideystar3 = (ImageView) findViewById(R.id.iv_attitude_star3);
        attutideystar4 = (ImageView) findViewById(R.id.iv_attitude_star4);
        attutideystar5 = (ImageView) findViewById(R.id.iv_attitude_star5);

        etAppraiseContent = (EditText) findViewById(R.id.tv_appraise_content);
        btnSubmit = (Button) findViewById(R.id.btn_appraise_submit);

        //点击改变颜色
        btnSubmit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    Log.d(TAG, "onTouch: "+event.getAction());
                    btnSubmit.setBackgroundColor(Color.parseColor("#65B5FF"));
                }else if (event.getAction() ==MotionEvent.ACTION_UP){
                    Log.d(TAG, "onTouch: "+event.getAction());
                    btnSubmit.setBackgroundColor(Color.parseColor("#6699ff"));
                }
                return false;
            }
        });

    }

    //星星的点击事件实现
    private void starOnClick(){
        final List<ImageView> list = new ArrayList<>();
        list.add(star1);
        list.add(star2);
        list.add(star3);
        list.add(star4);
        list.add(star5);
        final List<ImageView> qualityList = new ArrayList<>();
        qualityList.add(qualitystar1);
        qualityList.add(qualitystar2);
        qualityList.add(qualitystar3);
        qualityList.add(qualitystar4);
        qualityList.add(qualitystar5);
        final List<ImageView> speedList = new ArrayList<>();

        speedList.add(speedstar1);
        speedList.add(speedstar2);
        speedList.add(speedstar3);
        speedList.add(speedstar4);
        speedList.add(speedstar5);
        final List<ImageView> attitudeList = new ArrayList<>();

        attitudeList.add(attutideystar1);
        attitudeList.add(attutideystar2);
        attitudeList.add(attutideystar3);
        attitudeList.add(attutideystar4);
        attitudeList.add(attutideystar5);

        all.setCount(starCount);
        quality.setCount(qualitCount);
        speed.setCount(speedCount);
        attitude.setCount(attitudeCount);

        changeStarCount(list,all);
        changeStarCount(qualityList,quality);
        changeStarCount(speedList,speed);
        changeStarCount(attitudeList,attitude);

    }

    private void changeStarCount(final List<ImageView> list , final StarCount count ) {
        for (int i = 0; i < list.size(); i++) {
            final int finalI = i;
            list.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击了 说明要改默认好评
                    count.setCount(0);
                    count.setCount(finalI+1);
                    for (int j= 0; j<5;j++){
                        list.get(j).setImageResource(R.drawable.star_white);
                    }
                    for (int i =0;i<count.getCount();i++){
                        list.get(i).setImageResource(R.drawable.star_red);
                    }
                }
            });
        }
    }

    //提交 并且成功 则跳转详情页面
    private void upApply(String json) {

                Util.submit("appraise",json,ApplyAppraise).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError: 错误返回"+e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse: 成功返回"+response.toString());
                        Toast.makeText(MyApplication.getContext(), response.toString(), Toast.LENGTH_LONG).show();
                        Intent intent= new Intent(AppraiseActivity.this,DetailsActivity.class);
                        intent.putExtra("repairId",apply.getId());
                        intent.putExtra("appraiseIntent",true);
                        startActivity(intent);

                    }
                });

            }



    //提交按钮 点击绑定数据
    private void btnSubmitOnClick(){


        //获取数据，也就是星星数量starCount和评价文字tvAppriseContent，还有id作为插入数据库的条件

        //点击提交数据
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Apply appraisApply=new Apply();
                appraisApply.setId(apply.getId());
                appraisApply.setEvaluate(String.valueOf(all.getCount()));
                appraisApply.setSpeedEval(String.valueOf(speed.getCount()));
                appraisApply.setQualityEval(String.valueOf(quality.getCount()));
                appraisApply.setAttitudeEval(String.valueOf(attitude.getCount()));
                appraisApply.setEvalText(etAppraiseContent.getText().toString());
                String json=JsonUtil.beanToJson(appraisApply);
                upApply(json);
            }
        });
    }


}

class StarCount{
    int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}