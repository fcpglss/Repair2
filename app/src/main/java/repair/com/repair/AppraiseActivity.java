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
import util.JsonUtil;
import util.Util;

import static constant.RequestUrl.UPDATESERVER;

/**
 * Created by hsp on 2017/3/14.
 */

public class AppraiseActivity extends AppCompatActivity {
    private static final String TAG = "AppraiseActivity";


    //报修人姓名，电话，报修地址（从区域到房间），其他信息，维修人员
    TextView tvAppraiseName,tvAppraisePhone,tvAppraiseAddress,tvAppraiseOther,tvAppraiseServerName;
    //星星
    ImageView star1,star2,star3,star4,star5;
    //数星星 默认好评
    int starCount=5;
    boolean starState=true;

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
        tvAppraiseName.setText(apply.getRepair());
        tvAppraisePhone.setText(apply.getTel());
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

        for (int i = 0; i < list.size(); i++) {
            final int finalI = i;
            list.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击了 说明要改默认好评
                    starCount = 0;
                    starCount = finalI+1;
                    Log.d(TAG, "onClick: "+starCount);
                    for (int j= 0; j<5;j++){
                        list.get(j).setImageResource(R.drawable.star_white);
                        Log.d(TAG, "starOnClick: 赋值白色");
                    }
                    for (int i =0;i<starCount;i++){
                        list.get(i).setImageResource(R.drawable.star_red);
                        Log.d(TAG, "starOnClick: 赋值红色");
                    }
                }
            });
        }


    }
    //提交 并且成功 则跳转详情页面
    private void upApply(String json) {

                Util.submit("appraise",json,UPDATESERVER).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError: 错误返回"+e.toString());
                        Toast.makeText(MyApplication.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
                appraisApply.setEvaluate(String.valueOf(starCount));
                appraisApply.setEvalText(etAppraiseContent.getText().toString());
                String json=JsonUtil.beanToJson(appraisApply);
                upApply(json);
            }
        });
    }


}
