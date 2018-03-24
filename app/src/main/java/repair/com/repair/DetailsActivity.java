package repair.com.repair;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import application.MyApplication;
import me.codeboy.android.aligntextview.AlignTextView;
import model.Apply;
import model.Employee;
import model.Photo;
import model.Response;
import model.ResultBean;
import okhttp3.Call;
import repari.com.adapter.ImgAdapter;
import util.AESUtil;

import util.JsonUtil;


import util.Util;

import static constant.RequestUrl.ApplyDetail;



/**
 * Created by hsp on 2016/12/1.
 */

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DetailsActivity";

    //员工详细页面默认不可见
    boolean visible = false;

    //评价星星
    ImageView star1, star2, star3, star4, star5;

    ImageView speedstar1, speedstar2, speedstar3, speedstar4, speedstar5;
    ImageView qualitystar1, qualitystar2, qualitystar3, qualitystar4, qualitystar5;
    ImageView attutideystar1, attutideystar2, attutideystar3, attutideystar4, attutideystar5;

    //评价文字
    private TextView appraise;
    //大图片

    private PhotoView showBigImg;
    //背景
    LinearLayout linearLayoutDetail;

    private AlignTextView tvArea;

    private AlignTextView tv_describe;

    private TextView tvName, tv_date, tvDealTime, tvCompensation, tvNeed, tvAdmin,tvThirdMan,tvFishTime;

    private TextView tv_details_employee1;

    private TextView tv_employee_phone, tv_paigong;
    private ImageView iv_employee_arr;
    private LinearLayout ll_employee_details, llDetailDeal,llThirdMan,llFinshTime;


    private ImageView  img_status;

    private Apply apply = null;

    private List<Employee> employeeList = new ArrayList<>();
    private String repairId;

    private ResultBean detailRes = null;

    private boolean isAppraisePage = false;


    private List<ImageView> star_list = new ArrayList<ImageView>();
    private List<ImageView> qualityList = new ArrayList<ImageView>();
    private List<ImageView> speedList = new ArrayList<ImageView>();
    private List<ImageView> attitudeList = new ArrayList<ImageView>();
    private List<String> list_imageView = new ArrayList<>();
    private Response response;

    private GridView gridView ;
    private ImgAdapter imgAdapter ;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "msg=" + msg.what);
            switch (msg.what) {
                case 2:
                    Log.d(TAG, "handleMessage2:连接服务器失败,尝试从本地文件读取");
                    Toast.makeText(MyApplication.getContext(), response.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    if (apply.getLogisticMan() != null) {
                        iv_employee_arr.setVisibility(View.VISIBLE);
                    } else {
                        tv_paigong.setVisibility(View.VISIBLE);
                    }
                    Log.d(TAG, "handleMessage3 ");
                    bindItem();
                    break;
                case 4:
                    Toast.makeText(DetailsActivity.this, "连接服务器成功,但是服务器返回数据错误", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "handleMessage4 ");
                    break;

            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detailinfo);

        initView();

        repairId = getIntent().getStringExtra("repairId");
        isAppraisePage = getIntent().getBooleanExtra("appraiseIntent", false);

        if (apply == null) {
            queryFromServer(ApplyDetail, repairId);
        } else {
            if (apply.getLogisticMan() != null) {
                iv_employee_arr.setVisibility(View.VISIBLE);
            } else {
                tv_paigong.setVisibility(View.VISIBLE);
            }
            bindItem();
        }

    }

    public void queryFromServer(String url, String repairId) {

        String jsonurl = url;

        OkHttpUtils.get().
                url(jsonurl).
                addParams("detail", repairId)
                .tag(this)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Response rp = new Response();
                        rp.setErrorType(-1);
                        rp.setError(true);
                        rp.setErrorMessage("网络异常，返回空值");
                        response = rp;
                        mhandler.sendEmptyMessage(2);
                    }

                    @Override
                    public void onResponse(String responses, int id) {
                        //请求成功后获取到json
                        final String responseJson = responses.toString();
                        Log.d(TAG, "请求成功onFinish: " + responseJson);
                        response = JsonUtil.jsonToResponse(responseJson);
                        if (response.getErrorType() != 0) {
                            //连接成功，但读取数据失败
                            mhandler.sendEmptyMessage(4);
                        }
                        //连接成功，抛到主线程更新UI
                        else {
                            detailRes = response.getResultBean();
                            apply = detailRes.getApplys().get(0);
                            String phone = apply.getTel();
                            apply.setTel(AESUtil.decode(phone));

                            employeeList = detailRes.getEmployee();
                            mhandler.sendEmptyMessage(3);
                        }
                    }
                });

    }

    private void initView() {
        //员工详细信息
        tv_employee_phone = (TextView) findViewById(R.id.tv_details_employee_phone);
        llFinshTime= (LinearLayout) findViewById(R.id.ll_finish_time);
        llThirdMan = (LinearLayout) findViewById(R.id.ll_thirdMan);
        tvFishTime = (TextView) findViewById(R.id.tv_details_finish_date);
        iv_employee_arr = (ImageView) findViewById(R.id.iv_employee_arr);
        ll_employee_details = (LinearLayout) findViewById(R.id.ll_employee_details);
        tvThirdMan = (TextView) findViewById(R.id.tv_thirdMan);

        gridView = (GridView) findViewById(R.id.gv_img);
        //星星
        star1 = (ImageView) findViewById(R.id.iv_star1);
        star2 = (ImageView) findViewById(R.id.iv_star2);
        star3 = (ImageView) findViewById(R.id.iv_star3);
        star4 = (ImageView) findViewById(R.id.iv_star4);
        star5 = (ImageView) findViewById(R.id.iv_star5);

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

        star_list.add(star1);
        star_list.add(star2);
        star_list.add(star3);
        star_list.add(star4);
        star_list.add(star5);

        speedList.add(speedstar1);
        speedList.add(speedstar2);
        speedList.add(speedstar3);
        speedList.add(speedstar4);
        speedList.add(speedstar5);

        attitudeList.add(attutideystar1);
        attitudeList.add(attutideystar2);
        attitudeList.add(attutideystar3);
        attitudeList.add(attutideystar4);
        attitudeList.add(attutideystar5);

        qualityList.add(qualitystar1);
        qualityList.add(qualitystar2);
        qualityList.add(qualitystar3);
        qualityList.add(qualitystar4);
        qualityList.add(qualitystar5);


        //评价文字
        appraise = (TextView) findViewById(R.id.tv_appraise);
        //大图片

        showBigImg = (PhotoView) findViewById(R.id.iv_show_big_img);
        showBigImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        showBigImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showBigImg.setVisibility(View.GONE);
                linearLayoutDetail.setBackgroundColor(Color.WHITE);
                Log.d(TAG, "onClick: 显示");
            }
        });
        //背景
        linearLayoutDetail = (LinearLayout) findViewById(R.id.ll_detail_info);

        tv_paigong = (TextView) findViewById(R.id.tv_paigong);

        tv_details_employee1 = (TextView) findViewById(R.id.tv_details_employee1);


        tvName = (TextView) findViewById(R.id.tv_details_name);
        tv_date = (TextView) findViewById(R.id.tv_details_date);
        tvDealTime = (TextView) findViewById(R.id.tv_details_deal_date);


        tvArea = (AlignTextView) findViewById(R.id.tv_details_area);



        tv_describe = (AlignTextView) findViewById(R.id.tv_details_describe);




        tvNeed = (TextView) findViewById(R.id.tv_details_need);
        tvCompensation = (TextView) findViewById(R.id.tv_details_compensation);
        tvAdmin = (TextView) findViewById(R.id.tv_details_admin);
        llDetailDeal = (LinearLayout) findViewById(R.id.ll_detail_deal);

        img_status = (ImageView) findViewById(R.id.img_status);

        //员工详细信息箭头点击事件
        iv_employee_arr.setOnClickListener(this);

    }

    private void visibaleTime() {
        if (!tvDealTime.getText().equals("尚未处理")) {
            llDetailDeal.setVisibility(View.VISIBLE);
        }
        if(!tvFishTime.getText().equals("尚未完成")){
            llDetailDeal.setVisibility(View.VISIBLE);
        }
    }




    private void bindItem() {

        apply.setRepair(AESUtil.decode(apply.getRepair()));
        tvName.setText(Util.setNameXX(apply.getRepair()));

        //设置故障地点
        Util.formatArea(apply, tvArea);

        tv_date.setText(Util.getDealTime(apply.getRepairTime()));

        tvDealTime.setText(Util.getDealTime(apply.getDealTime()));
        tvFishTime.setText(Util.getFinshTime(apply.getFinilTime()));
        if(TextUtils.isEmpty(apply.getThirdLogisticMan())){

        }else{
            tvThirdMan.setText(apply.getThirdLogisticMan());
            llThirdMan.setVisibility(View.VISIBLE);
        }
        String Compensation = "";
        if (apply.getCompensation().equals("0")) {
            Compensation = "不收费";
        } else {
            Compensation = "收费";
        }

        tvCompensation.setText(Compensation);
        tvNeed.setText(apply.getMaterial());
        tvAdmin.setText(apply.getLogisticMan());

        //设置完成时间和处理时间的可见性。
        visibaleTime();
        //设置故障描述
        Util.formatBreakDown(apply, tv_describe);

        getApplyImages();

        img_status.setImageResource(getRightIcon());

        StringBuilder sbEmployeeName = new StringBuilder();
        StringBuilder sbEmployeePhone = new StringBuilder();
        String employeeName = "";
        String employeePhone = "";

        List<Employee> employeeTempList = new ArrayList<>();

        String[] idArrayapply = {""};
        if (apply.getServerMan() != null && !apply.getServerMan().isEmpty()) {
            idArrayapply = apply.getServerMan().split(",");
        }
        //emloyeeList 是所有人员的信息


        //遍历id  获取 维修人员信息
        for (Employee e :
                employeeList) {
            for (int i = 0; i < idArrayapply.length; i++) {
                if (e.getAccount().equals(idArrayapply[i])) {
                    employeeTempList.add(e);
                }
            }
        }


        if (employeeTempList.size() > 0) {

            for (Employee e : employeeTempList) {
                sbEmployeeName.append(e.getName()).append(",");
                sbEmployeePhone.append(e.getE_tel()).append(",");
            }
            employeeName = sbEmployeeName.toString().substring(0, sbEmployeeName.length() - 1);
            employeePhone = sbEmployeePhone.toString().substring(0, sbEmployeePhone.length() - 1);
            tv_details_employee1.setText(employeeName);
            tv_employee_phone.setText(employeePhone);
        }

        //设置星星
        setStarColor(Integer.parseInt(apply.getEvaluate()), star_list);
        setStarColor(Integer.parseInt(apply.getQualityEval()), qualityList);
        setStarColor(Integer.parseInt(apply.getSpeedEval()), speedList);
        setStarColor(Integer.parseInt(apply.getAttitudeEval()), attitudeList);
        //设置评价内容
        appraise.setText(apply.getEvalText());

    }




    private int getRightIcon() {
        int image = R.id.iv_icon;

        switch (apply.getState()) {
            case 1:
                image = R.drawable.weichuli;
                break;
            case 2:

                image = R.drawable.yipaigong;
                break;
            case 3:
                image = R.drawable.yizuofei;
                break;

            case 4:
                image = R.drawable.yiwanjie;
                break;
            case 5:
                image = R.drawable.weixiuzhong;
                break;
            default:
                image = R.drawable.weichuli;
        }
        return image;
    }

    private void getApplyImages() {
        //只加载最后三张图
        list_imageView = apply.getA_imaes();
        if (list_imageView != null && list_imageView.size() > 0) {
            if (list_imageView.size() > 3) {
                int length = list_imageView.size() - 3;
                for (int i = 0; i < length; i++) {
                    list_imageView.remove(i);
                }
            }
            imgAdapter = new ImgAdapter(this, list_imageView);
            gridView.setAdapter(imgAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    PhotoView p = (PhotoView) view;
                    Info info = p.getInfo();
                    Bitmap drawingCache = p.getDrawingCache();
                    showBigImg.setImageBitmap(drawingCache);
                    showBigImg.animaFrom(info);
                    showBigImg.setVisibility(View.VISIBLE);
                }
            });

        }


    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        OkHttpUtils.getInstance().cancelTag(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.iv_employee_arr:

                if (apply.getLogisticMan() != null) {

                    if (visible == false) {
                        ll_employee_details.setVisibility(View.VISIBLE);
                        visible = true;
                    } else {
                        ll_employee_details.setVisibility(View.GONE);
                        visible = false;
                    }
                } else {
                    visible = false;
                    tv_paigong.setVisibility(View.VISIBLE);
                    iv_employee_arr.setVisibility(View.GONE);
                }

                break;
            case R.id.iv_star1:
                break;

        }
    }


    private void setStarColor(int appraise, List<ImageView> list) {
        if (appraise > 0) {
            for (int i = 0; i < appraise; i++) {
                list.get(i).setColorFilter(getResources().getColor(R.color.starColor));
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (showBigImg.getVisibility() == View.VISIBLE) {

                showBigImg.setVisibility(View.GONE);
                linearLayoutDetail.setBackgroundColor(Color.WHITE);
            } else {
                if (isAppraisePage) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("appraise", "ok");
                    startActivity(intent);
                    Log.d(TAG, "onKeyDown: 返回了main");
                    this.finish();
                } else {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("appraise", "false");
                    startActivity(intent);
                    Log.d(TAG, "onKeyDown: 返回了main");
                    this.finish();
                }

            }
        }


//        return super.onKeyDown(keyCode,event);
        return true;
    }
}
