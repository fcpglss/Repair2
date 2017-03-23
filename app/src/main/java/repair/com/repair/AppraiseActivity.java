package repair.com.repair;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import model.Apply;

/**
 * Created by hsp on 2017/3/14.
 */

public class AppraiseActivity extends AppCompatActivity {

    //报修人姓名，电话，报修地址（从区域到房间），其他信息，维修人员
    TextView tvAppraiseName,tvAppraisePhone,tvAppraiseAddress,tvAppraiseOther,tvAppraiseServerName;
    //星星
    ImageView star1,star2,star3,star4,star5;
    //数星星
    int starCount=0;
    boolean starState=true;

    //评价内容
    EditText etAppraiseContent;
    //提交
    Button btnSubmit;
    Apply apply=null;

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
        Bundle b = new Bundle();
        b = intent.getExtras();
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
                    if(starState){
                        starCount = finalI;
                        //改变星星颜色
                        for (int j = 0; j < finalI; j++) {
                            // FIXME: 2017/3/14 改变前i个颜色
                            list.get(j).setColorFilter(getResources().getColor(R.color.starColor));
                        }
                        starState = false;
                    }else{
                        for (ImageView imageView:list){
                            imageView.setColorFilter(getResources().getColor(R.color.et_downline));
                        }
                    }

                }
            });
        }
    }


    //提交按钮点击时间实现
    private void btnSubmitOnClick(){

        //获取数据，也就是星星数量starCount和评价文字tvAppriseContent，还有id作为插入数据库的条件
        String content = etAppraiseContent.getText().toString();
        String id = apply.getId();

        //点击提交数据
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }



}
