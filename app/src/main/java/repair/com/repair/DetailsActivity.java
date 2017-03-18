package repair.com.repair;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import application.MyApplication;
import imagehodler.ImageLoader;
import model.Apply;
import model.Category;
import model.Employee;
import model.Photo;
import model.Place;
import model.ResultBean;
import repari.com.adapter.DialogAdapterImg;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static repair.com.repair.R.id.ifRoom;
import static repair.com.repair.R.id.iv_star1;
import static repair.com.repair.R.id.tv_details_category;
//import static repair.com.repair.R.id.tv_details_employee;
import static repair.com.repair.R.id.tv_employee_can;
import static repair.com.repair.R.id.tv_employee_company;

/**
 * Created by hsp on 2016/12/1.
 */

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DetailsActivity";
    boolean visable = false;//员工详细页面默认不可见

    LinearLayout l;


    //评价星星
    private ImageView star1,star2,star3,star4,star5;
    //评价文字
    private TextView appraise;
    //大图片
    private ImageView bigImg;
    private ImageView showBigImg;
    //背景
    LinearLayout linearLayoutDetail;
    //对话框
    private DialogPlus dialogPlus;

    private TextView tv_email, tvName, tv_tel, tv_date, tv_category, tv_status, tv_place, tv_describe,tvFinishTime;

    private TextView tv_details_employee1;

    private TextView tv_employee_company, tv_employee_phone, tv_employee_can, tv_paigong;
    private ImageView iv_employee_arr;
    private LinearLayout ll_employee_details;

    private ImageView img_category, img_status, img_back;
    private ImageView img1, img2, img3;
    private Apply apply = null;
    private ResultBean res = null;
    private Category category = null;
    private Place place = null;

    private List<ImageView> imageviewList = new ArrayList<ImageView>();

    private List<ImageView> star_list=new ArrayList<ImageView>();
    public ImageLoader mImageLoader = ImageLoader.build(MyApplication.getContext());
    private List<String> list_imageView = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detailinfo);


        initView();
        apply = (Apply) this.getIntent().getSerializableExtra("apply_item");

        if (apply.getLogisticMan() != null) {
            iv_employee_arr.setVisibility(View.VISIBLE);
        } else {
            tv_paigong.setVisibility(View.VISIBLE);
        }
        res = (ResultBean) this.getIntent().getSerializableExtra("res");
        bindItem();

    }

    private void initView() {
        //员工详细信息
        tv_employee_company = (TextView) findViewById(R.id.tv_employee_company);
        tv_employee_phone = (TextView) findViewById(R.id.tv_employee_phone);
        tv_employee_can = (TextView) findViewById(R.id.tv_employee_can);
        iv_employee_arr = (ImageView) findViewById(R.id.iv_employee_arr);
        ll_employee_details = (LinearLayout) findViewById(R.id.ll_employee_details);
        //

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
        bigImg = (ImageView) findViewById(R.id.detail_show_big_img);
        showBigImg = (ImageView) findViewById(R.id.iv_show_big_img);

        //背景
        linearLayoutDetail = (LinearLayout) findViewById(R.id.ll_detail_info);

        tv_paigong = (TextView) findViewById(R.id.tv_paigong);

        tv_details_employee1 = (TextView) findViewById(R.id.tv_details_employee1);
//        tv_details_employee2 = (TextView) findViewById(R.id.tv_details_employee2);
//        tv_details_employee3 = (TextView) findViewById(R.id.tv_details_employee3);
//        tv_details_employee4 = (TextView) findViewById(R.id.tv_details_employee4);



        tvName = (TextView) findViewById(R.id.tv_details_name);
        tv_tel = (TextView) findViewById(R.id.tv_details_tel);
        tv_category = (TextView) findViewById(tv_details_category);
        tv_date = (TextView) findViewById(R.id.tv_details_date);
        tvFinishTime= (TextView) findViewById(R.id.tv_details_finish_date);
        tv_status = (TextView) findViewById(R.id.tv_details_details);
        tv_place = (TextView) findViewById(R.id.tv_details_place);
        tv_email= (TextView) findViewById(R.id.tv_details_email);


        // tv_employee= (TextView) findViewById(R.id.tv_details_employee);

        tv_describe = (TextView) findViewById(R.id.tv_details_describe);
        img_category = (ImageView) findViewById(R.id.img_category);
        img_status = (ImageView) findViewById(R.id.img_status);
        img_back = (ImageView) findViewById(R.id.img_back2);

        img1 = (ImageView) findViewById(R.id.img_pc1);
        img2 = (ImageView) findViewById(R.id.img_pc2);
        img3 = (ImageView) findViewById(R.id.img_pc3);
        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);



        imageviewList.add(img1);
        imageviewList.add(img2);
        imageviewList.add(img3);

        img_back.setOnClickListener(this);


        //员工详细信息箭头点击事件
        iv_employee_arr.setOnClickListener(this);

        //员工名字点击事件

        //tv_details_employee1.setOnClickListener(this);

        //只有一个员工了 连点击事件都不用了直接显示那个员工
//        tv_details_employee2.setOnClickListener(this);
//        tv_details_employee3.setOnClickListener(this);
//        tv_details_employee4.setOnClickListener(this);

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
    private void imgOnClick(View v) {
        DialogAdapterImg dialogAdapterImg = new DialogAdapterImg(this, (ImageView) v,R.layout.dialog_show_img);
        dialogPlus = DialogPlus.newDialog(this)
                .setAdapter(dialogAdapterImg)
                .setGravity(Gravity.CENTER)
                .setContentWidth(800)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        Log.d("DialogPlus", "onItemClick() called with: " + "item = [" +
                                item + "], position = [" + position + "]");
                        dialogPlus.dismiss();
                    }

                })
                .setExpanded(true, 1000)
                .create();
        dialogPlus.show();
    }

    private void bindItem() {
        getCategory();
        getPlace();
        //详细信息不显示单号
//        tv_no.setText("" + apply.getA_no());
        tvName.setText(apply.getRepair());
        setNameXXX();
        tv_tel.setText(apply.getTel());
        setTelXXXX();
        tv_email.setText(apply.getEmail());

        tv_category.setText(category.getC_name());
        tv_place.setText(place.getP_name()+"_"+apply.getRoom());


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Log.d(TAG, "bindItem: 1" + apply.getDealTime());
        tv_date.setText(setTime(apply.getRepairTime()));
        Log.d(TAG, "bindItem: 2"+apply.getFinilTime());

        tvFinishTime.setText(setTime(apply.getFinilTime()));

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
        setEmployeeInf(apply.getLogisticMan());
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
    private void setTelXXXX()
    {
        if(tv_tel.getText()!=null&&!tv_tel.getText().equals(" "))
        {
            String tel=tv_tel.getText().toString();
            String temp=tel.substring(3,7);
            tv_tel.setText(tel.replace(temp,"****"));
        }
    }
    private String setTime(String datetime) {

        if (datetime != null && !datetime.equals(""))
        {
           return datetime.split(":")[0]+":"+datetime.split(":")[1];
        }
        return "";
    }






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
    private Employee getEmploye(String employeeAccount) {

        Employee employee=new Employee();
        if(employeeAccount!=null && !employeeAccount.equals(""))
        {
            List<Employee> list_employee = res.getEmployee();
            for (Employee e : list_employee) {
                if (e.getAccount().equals(employeeAccount)) {
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

        for (Category c : res.getCategory()) {
            if (apply.getClasss().equals(c.getC_name())) {
                category = c;
                break;
            }
        }

    }

    private void getPlace() {

        for (Place place : res.getPlaces()) {
            if (apply.getDetailArea().equals(place.getP_name())) {
                this.place = place;
                break;
            }
        }

    }

    private int getRightIcon() {
        int image = R.id.iv_icon;

        switch (apply.getState()) {
            case 1:
                image = R.drawable.chulizhong;
                break;
            case 2:
                image = R.drawable.daichuli;
                break;
            case 3:
                image = R.drawable.finish;
                break;
            case 4:
                image = R.drawable.yishixiao;
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
                mImageLoader.bindBitmap(list_imageView.get(i), imageviewList.get(i), 150, 150);
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
            case R.id.img_back2:
                Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_employee_arr:

                if (apply.getLogisticMan()!=null){

                    if (visable == false) {
                        ll_employee_details.setVisibility(View.VISIBLE);
                        visable = true;
                    } else {
                        ll_employee_details.setVisibility(View.GONE);
                        visable = false;
                    }
                }
                else {
                    visable = false;
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

}
