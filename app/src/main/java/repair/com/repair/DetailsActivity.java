package repair.com.repair;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import application.MyApplication;
import imagehodler.ImageLoader;
import model.Apply;
import model.Category;
import model.Employee;
import model.Place;
import model.Response;
import model.ResultBean;
import repari.com.adapter.DialogAdapterImg;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.Util;

import static repair.com.repair.R.id.tv_details_category;
//import static repair.com.repair.R.id.tv_details_employee;
import static repair.com.repair.MainActivity.windowWitch;
import static repair.com.repair.MainActivity.windowHeigth;


/**
 * Created by hsp on 2016/12/1.
 */

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DetailsActivity";
   private static final String URL="http://192.168.31.201:8888/myserver2/servlet/action";

  //  private static final String URL="http://192.168.43.128:8888/myserver2/servlet/action";

    boolean visible = false;//员工详细页面默认不可见

    //评价星星
    private ImageView star1,star2,star3,star4,star5;
    //评价文字
    private TextView appraise;
    //大图片

    private ImageView showBigImg;
    //背景
    LinearLayout linearLayoutDetail;
    //对话框
    private DialogPlus dialogPlus;

    private TextView tvArea;

    private TextView  tvName, tv_date, tv_category, tv_status, tv_place, tv_describe,tvFinishTime,tvDetailCategory;

    private TextView tv_details_employee1;

    private TextView tv_employee_company, tv_employee_phone, tv_employee_can, tv_paigong;
    private ImageView iv_employee_arr;
    private LinearLayout ll_employee_details;

    private ImageView img_category, img_status, img_back;
    private ImageView img1, img2, img3;
    private Apply apply = null;
    private Employee employee=null;
    private String repairId;

    private ResultBean detailRes = null;
    private Category category = null;

    private List<ImageView> imageviewList = new ArrayList<ImageView>();

    private List<ImageView> star_list=new ArrayList<ImageView>();
    public ImageLoader mImageLoader = ImageLoader.build(MyApplication.getContext());
    private List<String> list_imageView = new ArrayList<>();
    private Response response;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "msg="+msg.what);
            switch (msg.what) {
                case 2:
                    Log.d(TAG, "handleMessage2:连接服务器失败,尝试从本地文件读取");
                    Toast.makeText(MyApplication.getContext(),response.getErrorMessage(),Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(DetailsActivity.this,"连接服务器成功,但是服务器返回数据错误",Toast.LENGTH_SHORT).show();
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

        repairId=getIntent().getStringExtra("repairId");

        Log.d(TAG, "onCreate: 获取repairId="+repairId);
        if(apply==null)
        {
            queryFromServer(URL,repairId);
        }
        else
        {
            if (apply.getLogisticMan() != null) {
                iv_employee_arr.setVisibility(View.VISIBLE);
            } else {
                tv_paigong.setVisibility(View.VISIBLE);
            }
            bindItem();
        }

    }
    public void queryFromServer(String url,String repairId) {

        String jsonurl = url + "?detail=" + repairId;
        Log.d(TAG, "queryFromServer: " + jsonurl);
        HttpUtil.sendHttpRequest(jsonurl, new HttpCallbackListener() {
            @Override
            public void onFinish(String responseString) {
                //请求成功后获取到json
                final String responseJson = responseString.toString();
                Log.d(TAG, "请求成功onFinish: " + responseJson);
                response = JsonUtil.jsonToResponse(responseJson);
                if (response.getErrorType() != 0) {
                    //连接成功，但读取数据失败
                    mhandler.sendEmptyMessage(4);
                }
                //连接成功，抛到主线程更新UI
                else {
                    detailRes = response.getResultBean();
                    apply=detailRes.getApplys().get(0);
                    employee=detailRes.getEmployee().get(0);
                    mhandler.sendEmptyMessage(3);
                }
            }

            @Override
            public void onError(Exception e) {
                Response rp = new Response();
                rp.setErrorType(-1);
                rp.setError(true);
                rp.setErrorMessage("网络异常，返回空值");
                response = rp;
                mhandler.sendEmptyMessage(2);
            }
        });
    }

    private void initView() {
        //员工详细信息
        tv_employee_company = (TextView) findViewById(R.id.tv_employee_company);
        tv_employee_phone = (TextView) findViewById(R.id.tv_employee_phone);
        tv_employee_can = (TextView) findViewById(R.id.tv_employee_can);
        iv_employee_arr = (ImageView) findViewById(R.id.iv_employee_arr);
        ll_employee_details = (LinearLayout) findViewById(R.id.ll_employee_details);

        //星星
        star1 = (ImageView) findViewById(R.id.iv_star1);
        star2 = (ImageView) findViewById(R.id.iv_star2);
        star3 = (ImageView) findViewById(R.id.iv_star3);
        star4 = (ImageView) findViewById(R.id.iv_star4);
        star5 = (ImageView) findViewById(R.id.iv_star5);
        star_list.add(star1);
        star_list.add(star2);
        star_list.add(star3);
        star_list.add(star4);
        star_list.add(star5);
        //评价文字
        appraise = (TextView) findViewById(R.id.tv_appraise);
        //大图片

        showBigImg = (ImageView) findViewById(R.id.iv_show_big_img);

        //背景
        linearLayoutDetail = (LinearLayout) findViewById(R.id.ll_detail_info);

        tv_paigong = (TextView) findViewById(R.id.tv_paigong);

        tv_details_employee1 = (TextView) findViewById(R.id.tv_details_employee1);
//        tv_details_employee2 = (TextView) findViewById(R.id.tv_details_employee2);
//        tv_details_employee3 = (TextView) findViewById(R.id.tv_details_employee3);
//        tv_details_employee4 = (TextView) findViewById(R.id.tv_details_employee4);



        tvName = (TextView) findViewById(R.id.tv_details_name);
//        tv_tel = (TextView) findViewById(R.id.tv_details_tel);
        tv_category = (TextView) findViewById(tv_details_category);
        tvDetailCategory = (TextView) findViewById(R.id.tv_details_details_category);
        tv_date = (TextView) findViewById(R.id.tv_details_date);
        tvFinishTime= (TextView) findViewById(R.id.tv_details_finish_date);
        tv_status = (TextView) findViewById(R.id.tv_details_details);
        tv_place = (TextView) findViewById(R.id.tv_details_place);
//        tv_email= (TextView) findViewById(R.id.tv_details_email);

        tvArea = (TextView) findViewById(R.id.tv_details_area);



        // tv_employee= (TextView) findViewById(R.id.tv_details_employee);

        tv_describe = (TextView) findViewById(R.id.tv_details_describe);
        img_category = (ImageView) findViewById(R.id.img_category);
        img_status = (ImageView) findViewById(R.id.img_status);
//        img_back = (ImageView) findViewById(R.id.img_back2);

        img1 = (ImageView) findViewById(R.id.img_pc1);
        img2 = (ImageView) findViewById(R.id.img_pc2);
        img3 = (ImageView) findViewById(R.id.img_pc3);
        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);

        imageviewList.add(img1);
        imageviewList.add(img2);
        imageviewList.add(img3);
//        img_back.setOnClickListener(this);

        //员工详细信息箭头点击事件
        iv_employee_arr.setOnClickListener(this);


    }

    private void clickPic(View v){
        ImageView iV = (ImageView) v;
        showBigImg.setImageDrawable(iV.getDrawable());
        showBigImg.setVisibility(View.VISIBLE);
        //设置背景
        linearLayoutDetail.setBackgroundColor(Color.BLACK);

        showBigImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBigImg.setVisibility(View.GONE);
                linearLayoutDetail.setBackgroundColor(Color.rgb(211,211,211));
            }
        });
    }


    private void bindItem() {
        getCategory();
        Log.d(TAG, "bindItem: "+apply.getRepair());
        tvName.setText(apply.getRepair());
        setNameXXX();
//        tv_tel.setText(apply.getTel());
//        setTelXXXX();
//        tv_email.setText(apply.getEmail());

        tv_category.setText(category.getC_name());
        tvDetailCategory.setText(apply.getDetailClass());
        tv_place.setText(util.Util.setAddress(apply));

        tvArea.setText(apply.getArea());


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");


        tv_date.setText(Util.setTime(apply.getRepairTime()));

        tvFinishTime.setText(Util.setTime(apply.getFinilTime()));

        tv_describe.setText(apply.getRepairDetails());
        img_category.setImageResource(getCategoryIcon());

        getApplyImages();

        img_status.setImageResource(getRightIcon());
        if (apply.getA_imaes().size() > 0) {
            for (int i = 0; i <= apply.getA_imaes().size() - 1; i++) {
                Log.d(TAG, "该Apply的images集合:" + apply.getA_imaes().get(i));
            }
        } else {
            Log.d(TAG, "该Apply的images集合为空");
        }

        //直接设置员工信息 但是默认被隐藏
       // setEmployeeInf(apply.getLogisticMan());
        if (employee!=null){
            tv_details_employee1.setText(employee.getName());
            tv_employee_company.setText("");
            tv_employee_phone.setText(employee.getE_tel());
            tv_employee_can.setText(employee.getJob());
        }


        Log.d(TAG, "bindItem: "+Integer.parseInt(apply.getEvaluate()));
        //设置星星
       setStarColor(Integer.parseInt(apply.getEvaluate()));
        //设置评价内容
        appraise.setText(apply.getEvalText());

    }

    private void setNameXXX()
    {
        if(tvName.getText()!=null&&!tvName.getText().equals(" "))
        {
            String name=  tvName.getText().toString();
            int len=name.length();
            name=name.replace(name.substring(1,len),"**");
            tvName.setText(name);
        }
    }
//







    private void setEmployeeInf(String logisticAccount)
    {
        if(getEmploye(logisticAccount)!=null)
        {
            Employee e=getEmploye(logisticAccount);
            tv_details_employee1.setText(e.getName());
            tv_employee_phone.setText(" "+e.getE_tel());
        }
        else
        {
            tv_details_employee1.setText(" ");
            tv_employee_phone.setText(" ");
            tv_employee_can.setText(" ");
            tv_employee_company.setText(" ");
        }
    }


    //获取到apply中LogisticMan字段，（员工姓名)的对象
    private Employee getEmploye(String employeeName) {

        Employee employee=new Employee();
        if(employeeName!=null && !employeeName.equals(""))
        {
            List<Employee> list_employee = detailRes.getEmployee();
            for (Employee e : list_employee) {
                if (e.getName().equals(employeeName)) {
                    return e;
                }
            }
        }

        return employee;
    }




    private String getState()
    {
       switch (apply.getState())
       {
           case 1 :
               return "正在处理";
           case 2:
               return "派员中";
           case 3:
               return "已完工";
           case 4:
               return "已失效";
       }
       return "正在审核";
    }




    private void getCategory() {

        for (Category c : detailRes.getCategory()) {
            if (apply.getClasss().equals(c.getC_name())) {
                category = c;
                break;
            }
        }

    }

    private int getRightIcon() {
        int image = R.id.iv_icon;

        switch (apply.getState()) {
            case 1:
                image = R.drawable.daichuli;
                break;
            case 2:
                image = R.drawable.chulizhong1;
                break;
            case 3:
                image = R.drawable.yishixiao;
                break;
            case 4:
                image = R.drawable.finish;
                break;
            default:
                image = R.drawable.daichuli;
        }
        return image;
    }

    private void getApplyImages() {
        list_imageView = apply.getA_imaes();
        if (list_imageView!=null&&list_imageView.size() > 0) {
            if (list_imageView.size() > 3) {
                int length = list_imageView.size() - 3;
                for (int i = 0; i < length; i++) {
                    list_imageView.remove(i);
                }
            }
            for (int i = 0; i < list_imageView.size(); i++) {
//                mImageLoader.bindBitmap(list_imageView.get(i), imageviewList.get(i), 150, 150);
                Picasso.with(this).load(list_imageView.get(i)).into(imageviewList.get(i));

                LinearLayout.LayoutParams layout = (LinearLayout.LayoutParams) imageviewList.get(i).getLayoutParams();
                layout.height = windowHeigth/4;
                layout.width = (int) (windowWitch/3.3);
                imageviewList.get(i).setLayoutParams(layout);
                Log.d(TAG, "执行了一次bindBitmap "+list_imageView.get(i));
            }

        } else {
            Log.d(TAG, "list_imageView为0,该Apply没有图片");
        }

    }


    private int getCategoryIcon() {
        int image = R.id.iv_icon;

        switch (apply.getClasss()) {
            case "水":
                image = R.drawable.water;
                break;
            case "电":
                image = R.drawable.dian;
                break;
            case "土建":
                image = R.drawable.door;
                break;
            case "设备":
                image = R.drawable.computer;
                break;
            default:
                image = R.drawable.computer;
        }
        return image;
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {


        switch (view.getId()) {
//            case R.id.img_back2:
//                Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
//                startActivity(intent);
//                break;
            case R.id.iv_employee_arr:

                if (apply.getLogisticMan()!=null){

                    if (visible == false) {
                        ll_employee_details.setVisibility(View.VISIBLE);
                        visible = true;
                    } else {
                        ll_employee_details.setVisibility(View.GONE);
                        visible = false;
                    }
                }
                else {
                    visible = false;
                    tv_paigong.setVisibility(View.VISIBLE);
                    iv_employee_arr.setVisibility(View.GONE);
                }

                break;
            case R.id.iv_star1:
                break;
            case R.id.img_pc1:
                clickPic(view);
                break;
            case R.id.img_pc2:
                clickPic(view);
                break;
            case R.id.img_pc3:
                clickPic(view);
                break;
        }
    }


    private void setStarColor(int appraise)
    {
        if(appraise>0)
        {
            for(int i=0;i<appraise;i++)
            {
                star_list.get(i).setColorFilter(getResources().getColor(R.color.starColor));
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){

            if (showBigImg.getVisibility() == View.VISIBLE){
                showBigImg.setVisibility(View.GONE);
                linearLayoutDetail.setBackgroundColor(Color.rgb(211,211,211));
            }else{
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                Log.d(TAG, "onKeyDown: 返回了main");
                this.finish();
            }
        }


//        return super.onKeyDown(keyCode,event);
        return true;
    }
}
