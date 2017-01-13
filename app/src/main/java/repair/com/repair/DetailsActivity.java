package repair.com.repair;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static repair.com.repair.R.id.ifRoom;
import static repair.com.repair.R.id.t;
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

    private TextView tv_no, tv_name, tv_tel, tv_date, tv_category, tv_status,  tv_place, tv_describe;

    private TextView tv_details_employee1, tv_details_employee2, tv_details_employee3, tv_details_employee4;
    private List<String> allEmployeeName = new ArrayList<>();

    private TextView tv_employee_company, tv_employee_phone, tv_employee_can,tv_paigong;
    private ImageView iv_employee_arr;
    private LinearLayout ll_employee_details;

    private ImageView img_category, img_status, img_back;
    private ImageView img1, img2, img3;
    private Apply apply = null;
    private ResultBean res = null;
    private Category category = null;
    private Place place = null;
    private List<ImageView> imageviewList = new ArrayList<ImageView>();

    public ImageLoader mImageLoader = ImageLoader.build(MyApplication.getContext());
    private List<String> list_imageView = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);




        initView();
        apply = (Apply) this.getIntent().getSerializableExtra("apply_item");

        if (apply.getEmployees()!=null){
            iv_employee_arr.setVisibility(View.VISIBLE);
        }else {
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

        tv_paigong = (TextView) findViewById(R.id.tv_paigong);

        tv_details_employee1 = (TextView) findViewById(R.id.tv_details_employee1);
        tv_details_employee2 = (TextView) findViewById(R.id.tv_details_employee2);
        tv_details_employee3 = (TextView) findViewById(R.id.tv_details_employee3);
        tv_details_employee4 = (TextView) findViewById(R.id.tv_details_employee4);


        tv_no = (TextView) findViewById(R.id.tv_details_no);
        tv_name = (TextView) findViewById(R.id.tv_details_name);
        tv_tel = (TextView) findViewById(R.id.tv_details_tel);
        tv_category = (TextView) findViewById(tv_details_category);
        tv_date = (TextView) findViewById(R.id.tv_details_date);
        tv_status = (TextView) findViewById(R.id.tv_details_details);
        tv_place = (TextView) findViewById(R.id.tv_details_place);

        // tv_employee= (TextView) findViewById(R.id.tv_details_employee);

        tv_describe = (TextView) findViewById(R.id.tv_details_describe);
        img_category = (ImageView) findViewById(R.id.img_category);
        img_status = (ImageView) findViewById(R.id.img_status);
        img_back = (ImageView) findViewById(R.id.img_back2);

        img1 = (ImageView) findViewById(R.id.img_pc1);
        img2 = (ImageView) findViewById(R.id.img_pc2);
        img3 = (ImageView) findViewById(R.id.img_pc3);


        imageviewList.add(img1);
        imageviewList.add(img2);
        imageviewList.add(img3);

        img_back.setOnClickListener(this);

        //员工详细信息箭头点击事件
        iv_employee_arr.setOnClickListener(this);
        //员工名字点击事件
        tv_details_employee1.setOnClickListener(this);
        tv_details_employee2.setOnClickListener(this);
        tv_details_employee3.setOnClickListener(this);
        tv_details_employee4.setOnClickListener(this);

    }

    private void bindItem() {
        getCategory();
        getPlace();
        tv_no.setText("" + apply.getA_no());
        tv_name.setText(apply.getA_name());
        tv_tel.setText(apply.getA_tel());
        tv_category.setText(category.getC_name());
        tv_place.setText(place.getP_name() + "—" + apply.getA_detalis());
        tv_status.setText(apply.getA_status());
        // tv_employee.setText(""+apply.getEmployees());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        tv_date.setText(apply.getA_createat());






        tv_describe.setText(apply.getA_describe());
        img_category.setImageResource(getCategoryIcon());
        img_status.setImageResource(getRightIcon());
        if (apply.getA_imaes().size() > 0) {
            for (int i = 0; i <= apply.getA_imaes().size() - 1; i++) {
                Log.d(TAG, "该Apply的images集合:" + apply.getA_imaes().get(i));
            }
        } else {
            Log.d(TAG, "该Apply的images集合为空");
        }
        getApplyImages();

        allEmployeeName = getEmployNames(apply);

        setName();

    }

    private void setName() {

        if (allEmployeeName == null) {
            Log.d(TAG, "setName: 空");
        } else {
            Log.d(TAG, "setName: " + allEmployeeName.size());
            if (allEmployeeName.size() > 0){
                tv_details_employee1.setText(allEmployeeName.get(0));
                Log.d(TAG, "setName:allEmployeeName.get(0) " + allEmployeeName.get(0));
            }

            if (allEmployeeName.size() > 1){
                Log.d(TAG, "setName:allEmployeeName.get(1) " + allEmployeeName.get(1));
                tv_details_employee2.setText(allEmployeeName.get(1));
            }

            if (allEmployeeName.size() > 2){
                Log.d(TAG, "setName:allEmployeeName.get(2) " + allEmployeeName.get(2));
                tv_details_employee2.setText(allEmployeeName.get(2));
            }

            if (allEmployeeName.size() > 3){
                Log.d(TAG, "setName:allEmployeeName.get(3) " + allEmployeeName.get(3));
                tv_details_employee2.setText(allEmployeeName.get(3));
            }

        }


    }


    private void getCategory() {

        for (Category c : res.getCategory()) {
            if (apply.getA_category() == c.getC_id()) {
                category = c;
                break;
            }
        }

    }

    private void getPlace() {

        for (Place place : res.getPlaces()) {
            if (apply.getA_place() == place.getP_id()) {
                this.place = place;
                break;
            }
        }

    }

    private int getRightIcon() {
        int image = R.id.iv_icon;

        switch (apply.getA_status()) {
            case "处理中":
                image = R.drawable.chulizhong;
                break;
            case "待处理":
                image = R.drawable.daichuli;
                break;
            case "已完成":
                image = R.drawable.finish;
                break;
            case "已失效":
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
                Log.d(TAG, "执行了一次bindBitmap"+list_imageView.get(i));
            }


        } else {
            Log.d(TAG, "list_imageView为0,该Apply没有图片");
        }

    }


    private int getCategoryIcon() {
        int image = R.id.iv_icon;

        switch (apply.getA_category()) {
            case 1:
                image = R.drawable.water;
                break;
            case 2:
                image = R.drawable.dian;
                break;
            case 3:
                image = R.drawable.door;
                break;
            case 4:
                image = R.drawable.computer;
                break;
            default:
                image = R.drawable.computer;
        }
        return image;
    }

    private void getEmployee(Apply apply) {
//        String company="";
//
//      String[] employees=apply.getEmployees().split(" ");
//        for (int i=0;i<employees.length;i++)
//        {
//            for(Employee e : res.getEmployee())
//            {
//                if(employees[i].equals(e.getEmployeeName()))
//                {
//                    company+=e.getE_company();
//                }
//            }
//        }
//        tv_employee_company.setText(company);

        if (apply.getEmployees() == null) {
            return;
        }
        String applyEmployees = apply.getEmployees();
        Log.d(TAG, "getEmployee: " + applyEmployees);
        String[] applyEmployee = applyEmployees.split(" ");
        Log.d(TAG, "getEmployee: " + applyEmployee[0]);
        String companyString = "";
        String telString = "";
        String canString = "";

        Log.d(TAG, "getEmployee: " + res.getEmployee().size());
        for (int i = 0; i < applyEmployee.length; i++) {
            for (int j = 0; j < res.getEmployee().size(); j++) {
//                Log.d(TAG, "getEmployee: res.getEmployee().get(1).getEmployeeName() "+res.getEmployee().get(1).getEmployeeName());
//                Log.d(TAG, "getEmployee: applyEmployee"+applyEmployee[0]);

                //公司
                if (res.getEmployee().get(j).getEmployeeName().equals(applyEmployee[i])) {
                    String companyString1 = companyString;
                    if (!res.getEmployee().get(j).getE_company().equals(companyString1)) {
                        companyString = companyString + res.getEmployee().get(j).getE_company();
                    }
                    String telStringTest = telString;
                    if (!res.getEmployee().get(j).getE_tel().equals(telStringTest)) {
                        telString = telString + " " + res.getEmployee().get(j).getE_tel();
                    }
                    //canString = canString+res.getEmployee().get(j).getE_can();
                }

            }
        }

        tv_employee_company.setText(companyString);
        tv_employee_phone.setText(telString);
        // tv_employee_can.setText(canString);

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

                if (apply.getEmployees()!=null){

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
            case R.id.tv_details_employee1:
                setNameOnclick(allEmployeeName.get(0));
                break;
            case R.id.tv_details_employee2:
                setNameOnclick(allEmployeeName.get(1));
                break;
            case R.id.tv_details_employee3:
                setNameOnclick(allEmployeeName.get(2));
                break;
            case R.id.tv_details_employee4:
                setNameOnclick(allEmployeeName.get(3));
                break;
        }
    }

    private void setNameOnclick(String name) {
        Log.d(TAG, "setNameOnclick: Onclick 里面"+name);
        Employee employee = getEmployeeInfo(name);
        if (employee.getE_company() != null)
            Log.d(TAG, "setNameOnclick: "+employee.getE_company());
            tv_employee_company.setText(employee.getE_company());
        if (employee.getE_tel() != null)
            tv_employee_phone.setText(employee.getE_tel());
        if (employee.getE_can() != null)
            tv_employee_can.setText(employee.getE_can());
    }


    private List<String> getEmployNames(Apply apply) {

        if (apply.getEmployees() == null) {
            return null;
        }
        List<String> a = new ArrayList<>();
        String applyEmployees = apply.getEmployees();

        String reg = "\\s+";

        String[] applyEmployee = applyEmployees.split(reg);
        Log.d(TAG, "getEmployNames: " + applyEmployees);
        for (int i = 0; i < applyEmployee.length; i++) {
            Log.d(TAG, "getEmployNames: pp" + applyEmployee[i]);
            a.add(applyEmployee[i]);
        }


        return a;
    }

    private Employee getEmployeeInfo(String name) {
        Log.d(TAG, "getEmployeeInfo: 有没有"+name);

        Log.d(TAG, "getEmployeeInfo: start");
        Employee employee = new Employee();

        for (int i = 0; i < res.getEmployee().size(); i++) {
            Log.d(TAG, "getEmployeeInfo: "+res.getEmployee().get(i).getEmployeeName());

            if (res.getEmployee().get(i) == null) return null;
            if (res.getEmployee().get(i).getEmployeeName().equals(name)) {
                Log.d(TAG, "getEmployeeInfo: "+res.getEmployee().get(i).getE_company());
                employee.setEmployeeName(name);
                employee.setE_company(res.getEmployee().get(i).getE_company());
                employee.setE_tel(res.getEmployee().get(i).getE_tel());
                employee.setE_can(res.getEmployee().get(i).getE_can());
            }
        }
        return employee;
    }
}
