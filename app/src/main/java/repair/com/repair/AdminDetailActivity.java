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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import application.MyApplication;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import model.Admin;
import model.Apply;
import model.Employee;
import model.Response;
import model.ResultBean;
import okhttp3.Call;
import repari.com.adapter.DialogAdapter;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.UIUtil;
import util.Util;

import static constant.RequestUrl.JSONEMPLOYEE;
import static constant.RequestUrl.TEXT_EMAIL_URL;
import static constant.RequestUrl.URL;
import static repair.com.repair.MainActivity.windowHeigth;
import static repair.com.repair.MainActivity.windowWitch;

//import static constant.RequestUrl.AdMINUPDATE;

/**
 * Created by Administrator on 2017-4-15.
 */

public class AdminDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AdminDetailActivity";


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (sDialog != null) {
            sDialog.dismiss();
        }
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private int index = -1;


    //大图片
    private ImageView showBigImg;
    //背景
    LinearLayout linearLayoutDetail;
    //对话框
    private DialogPlus dialogChoose;
    private List<String> serverIDString = new ArrayList<>();
    private Set<String> serverTempList = new LinkedHashSet<>();
    private DialogAdapter dialogChooseAdapter;

    private TextView tvName, tvTel, tvTime, tvEmail, tvAdress, tvCategory;

    private ImageView imgCategory;
    private ImageView img1, img2, img3;

    private Apply apply = null;

    private String updateJson;

    private List<Employee> employees;
    private SweetAlertDialog sDialog;
    private List<String> employesName = new ArrayList<>();

    private String repairId;
    private int isIntenet;
    private Button btnChoose, btnSend, btnSubmit;

    private EditText etServerMan, etMatrial;
    private ResultBean adminDetailRes = null;


    private List<ImageView> imageviewList = new ArrayList<ImageView>();


    private Admin adminInf;

    private List<String> list_imageView = new ArrayList<>();
    private Response adminDetailResponse;
    private Response adminEmployeeResponse;
    private ResultBean adminEmployeeResutlt;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "msg=" + msg.what);
            switch (msg.what) {
                case 2:
                    Log.d(TAG, "handleMessage2:连接服务器失败,尝试从本地文件读取");
                    Toast.makeText(MyApplication.getContext(), adminDetailResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Log.d(TAG, "handleMessage3 ");
                    bindItem();
                    break;
                case 4:
                    Toast.makeText(AdminDetailActivity.this, "连接服务器成功,但是服务器返回数据错误", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "handleMessage4 ");
                    break;
                case 5:
                    Log.d(TAG, "handleMessage: 根据CategoryId获取相应的维修人员: employeList=" + employees.toString());
                    dialogChooseAdapter.notifyDataSetChanged();
                    setChooseDialog();
                    break;
                case 6:
                    sDialog.setTitleText("修改成功")
                            .setConfirmText("")
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    Intent intent = new Intent(AdminDetailActivity.this, AdminListActivity.class);
                    startActivity(intent);
                    break;
                case 7:
                    sDialog.setTitleText("修改失败")
                            .setContentText("")
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                case 8:
                    sDialog.setTitleText("网络错误")
                            .setContentText("")
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    break;
                case 9:
                    sDialog.setTitleText("已成功派工")
                            .setContentText("")
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    Observable.timer(1, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            AdminListActivity.onResumeValue = "OK";
                            Intent intentToList = new Intent(AdminDetailActivity.this, AdminListActivity.class);
                            startActivity(intentToList);
                        }
                    });
                    break;
//                    SharedPreferences preferences = AdminDetailActivity.this.getSharedPreferences("admin_data", MODE_PRIVATE);
//                    String json = preferences.getString("admin", "");
//                    Log.d(TAG, "handleMessage: account " + json);
//                    Admin adminFromJson = JsonUtil.jsonToAdmin(json);
//
//
//                    Apply applyTemp = new Apply();
//                    applyTemp.setId(apply.getId());
////                    维修工
//
//                    //获取维修工id字符串
//                    StringBuilder s = new StringBuilder();
//                    for (String str :
//                            serverTempList) {
//
//                        s.append(str).append(",");
//                    }
//                    String tempString = s.toString().substring(0, s.length() - 1);
//
////                    Log.d(TAG, "handleMessage: "+tempString);
//
//                    applyTemp.setServerMan(tempString);
//                    //后台人员
////                    Log.d(TAG, "handleMessage: account " + AdminListActivity.account);
//                    applyTemp.setLogisticMan(AdminListActivity.account);
////                    Log.d(TAG, "handleMessage: account " + adminFromJson.getAccount());
//                    //状态 改为 2 已派工
//                    applyTemp.setState(2);
//                    //物料
//                    applyTemp.setMaterial(etMatrial.getText().toString());
//
//                    String applyJson = JsonUtil.beanToJson(applyTemp);
//                    Util.submit("deal", applyJson, AdMINUPDATE).execute(new StringCallback() {
//                        @Override
//                        public void onError(Call call, Exception e, int id) {
//
//                        }
//
//                        @Override
//                        public void onResponse(String response, int id) {
//                            if (response.equals("OK")) {
//                                mhandler.sendEmptyMessage(12);
//                                Log.d(TAG, "onResponse:  handler 12 " + response);
//                            } else {
//                                mhandler.sendEmptyMessage(13);
//                            }
//                        }
//                    });
                case 10:
                    sDialog.setTitleText("服务器维护,邮件派工失败。")
                            .setContentText("")
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    break;
                case 11:
                    if (sDialog != null) {
                        sDialog.setTitleText("网络异常,请检查网络")
                                .setContentText("")
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    }
                    break;
                case 12:
                    sDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    Observable.timer(1, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            //AdminListActivity.onResumeValue = index;
                            Intent intentToList = new Intent(AdminDetailActivity.this, AdminListActivity.class);
                            startActivity(intentToList);
                        }
                    });

                    break;
                case 13:
                    sDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    break;

            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.admin_detail_infor);
        adminInf = Util.loadWriteAdmin(this);
        //   index=getIntent().getIntExtra("index",-2);
        Log.d(TAG, "onCreate: index -> " + index);
        Log.d(TAG, "onCreate: " + adminInf.getEmailPassword());
        initView();

        repairId = getIntent().getStringExtra("repairId");
        isIntenet = getIntent().getIntExtra("isIntent", 0);
        Log.d(TAG, "onCreate: 获取isIntent的值：" + isIntenet);
        Log.d(TAG, "onCreate: 获取repairId=" + repairId);
        queryFromServer(URL, repairId);
        Log.d(TAG, "onCreate: apply");


    }

    private Admin getAdmin() {
        Admin admin = new Admin();
        admin = (Admin) getIntent().getSerializableExtra("adminDetail");
        return admin;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        super.onNewIntent(intent);
    }


    public void queryFromServer(String url, String repairId) {

        String jsonurl = url + "?detail=" + repairId;
        Log.d(TAG, "queryFromServer: " + jsonurl);
        HttpUtil.sendHttpRequest(jsonurl, new HttpCallbackListener() {
            @Override
            public void onFinish(String responseString) {
                //请求成功后获取到json
                final String responseJson = responseString.toString();
                Log.d(TAG, "请求成功onFinish: " + responseJson);
                adminDetailResponse = JsonUtil.jsonToResponse(responseJson);
                if (adminDetailResponse.getErrorType() != 0) {
                    //连接成功，但读取数据失败
                    mhandler.sendEmptyMessage(4);
                }
                //连接成功，抛到主线程更新UI
                else {
                    adminDetailRes = adminDetailResponse.getResultBean();
                    apply = adminDetailRes.getApplys().get(0);

                    mhandler.sendEmptyMessage(3);
                }
            }

            @Override
            public void onError(Exception e) {
                Response rp = new Response();
                rp.setErrorType(-1);
                rp.setError(true);
                rp.setErrorMessage("网络异常，返回空值");
                adminDetailResponse = rp;
                mhandler.sendEmptyMessage(2);
            }
        });
    }

    private void initView() {

        showBigImg = (ImageView) findViewById(R.id.iv_admin_show_big_img);

        //背景
        linearLayoutDetail = (LinearLayout) findViewById(R.id.ll_admin_info);


        //申报人信息
        tvName = (TextView) findViewById(R.id.tv_admin_details_name);
        tvTel = (TextView) findViewById(R.id.tv_admin_details_tel);
        tvEmail = (TextView) findViewById(R.id.tv_admin_details_email);
        //故障信息
        tvAdress = (TextView) findViewById(R.id.tv_admin_details_address);

        tvCategory = (TextView) findViewById(R.id.tv_admin_details_category);

        etMatrial = (EditText) findViewById(R.id.et_admin_detial_matrial);
        tvTime = (TextView) findViewById(R.id.tv_admin_details_repairtime);
        //button按钮
        btnChoose = (Button) findViewById(R.id.btn_admin_add);
        btnSend = (Button) findViewById(R.id.btn_admin_send);
        btnSubmit = (Button) findViewById(R.id.btn_admin_subimit);
        //设置颜色点击改变
        Util.setOnClickBackgroundColor(btnChoose);
        Util.setOnClickBackgroundColor(btnSend);

        Log.d(TAG, "1 initView: btn设置setClickable为false");
        Log.d(TAG, "2 执行initSubmitOnClick");
        initSubmitOnClick();
        dialogChooseAdapter = new DialogAdapter(this, employesName, R.layout.simple_list_item);
        //editText
        etServerMan = (EditText) findViewById(R.id.et_admin_serverman);


        imgCategory = (ImageView) findViewById(R.id.img_admin_category);

        img1 = (ImageView) findViewById(R.id.img_admin_pc1);
        img2 = (ImageView) findViewById(R.id.img_admin_pc2);
        img3 = (ImageView) findViewById(R.id.img_admin_pc3);

        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);

        imageviewList.add(img1);
        imageviewList.add(img2);
        imageviewList.add(img3);
    }

    private void clickPic(View v) {
        final ImageView iV = (ImageView) v;
//        showBigImg.setImageDrawable(iV.getDrawable());
        showBigImg.setBackground(iV.getDrawable());
        showBigImg.setVisibility(View.VISIBLE);
        //设置背景
        linearLayoutDetail.setBackgroundColor(Color.BLACK);

        showBigImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBigImg.setBackground(null);
                showBigImg.setVisibility(View.GONE);
                linearLayoutDetail.setBackgroundColor(Color.rgb(211, 211, 211));
            }
        });
    }


    private void bindItem() {
        //设置报修人信息
        tvName.setText(apply.getRepair());
        tvTel.setText(apply.getTel());
        tvEmail.setText(apply.getEmail());
        //设置故障信息

        tvAdress.setText(Util.setAddress(apply, 16, false));

        tvCategory.setText(Util.setClass(apply, 16, false));

        tvTime.setText(Util.getDealTime(apply.getRepairTime()));
        //设置类型图标
        imgCategory.setImageResource(UIUtil.getCategoryIcon(apply.getClasss()));
        getApplyImages(AdminListActivity.hasPic);
        if (apply.getA_imaes().size() > 0) {
            for (int i = 0; i <= apply.getA_imaes().size() - 1; i++) {
                Log.d(TAG, "该Apply的images集合:" + apply.getA_imaes().get(i));
            }
        } else {
            Log.d(TAG, "该Apply的images集合为空");
        }
        setButtonOnClick();
    }


    private void getApplyImages(boolean hasPic) {
        if (hasPic) {
            list_imageView = apply.getA_imaes();
            if (list_imageView != null && list_imageView.size() > 0) {
                if (list_imageView.size() > 3) {
                    int length = list_imageView.size() - 3;
                    for (int i = 0; i < length; i++) {
                        list_imageView.remove(i);
                    }
                }
                for (int i = 0; i < list_imageView.size(); i++) {
                    Picasso.with(this).load(list_imageView.get(i)).into(imageviewList.get(i));
                    LinearLayout.LayoutParams layout = (LinearLayout.LayoutParams) imageviewList.get(i).getLayoutParams();
                    layout.height = windowHeigth / 4;
                    layout.width = (int) (windowWitch / 3.3);
                    imageviewList.get(i).setLayoutParams(layout);
                    Log.d(TAG, "Picasso执行一次 " + list_imageView.get(i));
                }
            } else {
                Log.d(TAG, "list_imageView为0,该Apply没有图片");
            }
        } else {
            list_imageView = apply.getA_imaes();
        }


    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (sDialog != null) {
            sDialog.dismiss();
            sDialog = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
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


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (showBigImg.getVisibility() == View.VISIBLE) {
                showBigImg.setBackground(null);
                showBigImg.setVisibility(View.GONE);
                linearLayoutDetail.setBackgroundColor(Color.rgb(211, 211, 211));
            } else {
                Intent intent = new Intent(this, AdminListActivity.class);
                startActivity(intent);
                Log.d(TAG, "onKeyDown: 返回了main");
                this.finish();
            }
        }
        return true;
    }

    private void setButtonOnClick() {

        RxView.clicks(btnChoose).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (serverTempList.size() > 4) {
                            new SweetAlertDialog(AdminDetailActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("最多五个人员")
                                    .setContentText("请清空后重新添加")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                        }
                                    })
                                    .show();
                        } else {
                            upApply(apply.getClasss());
                        }
                    }
                });

        RxView.clicks(btnSend).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {

                        etServerMan.setText("");
                        serverTempList.clear();
                        setMatrial();
                    }
                });

        changeColor(etMatrial, btnSend);
        changeColor(etServerMan, btnSend);


    }


    private void changeColor(EditText editText, final Button btn) {

        RxTextView.textChanges(editText).subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence charSequence) throws Exception {
                if (charSequence.toString().equals("")) {
                    btn.setBackgroundResource(R.drawable.button_submit2);
                } else {
                    btn.setBackgroundResource(R.drawable.button_submit);
                }
            }
        });
    }


    /**
     * 在判断 etServerMan中是不是选中值了,如为空值则 设置按钮颜色和不可点击
     * 应在对话框dissMiss调用,和有关serverMan赋值的地方调用
     */
    private void setMatrial() {
        //进入方法都设置能输入；
        if (etServerMan.getText().toString().equals("")) {
            Log.d(TAG, "setMatrial: ");
            etMatrial.setText("");
            btnSubmit.setBackgroundResource(R.drawable.button_submit2);
            //设置不能输入
            etServerMan.setEnabled(false);
        } else {

            btnSubmit.setBackgroundResource(R.drawable.button_submit);

        }
    }


    private void setChooseDialog() {

        Log.d(TAG, "获取employeeName成功setChooseDialog: ");
        dialogChoose = DialogPlus.newDialog(this)
                .setAdapter(dialogChooseAdapter)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.dialog_head9)
                .setContentWidth((int) (windowWitch / 1.5))
                .setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogPlus dialog) {
                        setMatrial();
                    }
                })
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        if (position != -1) {
                            String temp = etServerMan.getText().toString();
                            setServerManText(position, temp);
                            serverTempList.add(serverIDString.get(position));
                            dialogChoose.dismiss();
                        }
                    }
                })
                .setExpanded(true, (int) (windowHeigth / 1.5))  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialogChoose.show();
    }

    private void setServerManText(int position, String temp) {
        boolean flag = false;
        if (temp != null && !"".equals(temp)) {
            String temp2[] = temp.split(",");
            for (int i = 0; i < temp2.length; i++) {
                if (employesName.get(position).equals(temp2[i])) {
                    flag = true;
                }
            }
            if (!flag) {
                etServerMan.setText(temp + "," + employesName.get(position));
            }
        } else {
            etServerMan.setText(employesName.get(position).toString());
        }

    }

    private void upApply(String categoryName) {

        if (true) {

            Util.submit("categoryName", categoryName, JSONEMPLOYEE).execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Toast.makeText(MyApplication.getContext(), "网络连接超时,请检查网络", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(String responses, int id) {
                    final String responseJson = responses.toString();
                    Log.d(TAG, "onResponse: " + responseJson);
                    adminEmployeeResponse = JsonUtil.jsonToResponse(responseJson);
                    if (adminEmployeeResponse != null) {
                        adminEmployeeResutlt = adminEmployeeResponse.getResultBean();
                    }
                    if (adminEmployeeResutlt != null) {
                        employees = adminEmployeeResutlt.getEmployee();
                        setEmployeName();
                        mhandler.sendEmptyMessage(5);
                    } else {
                        adminEmployeeResponse.setErrorType(-2);
                        adminEmployeeResponse.setError(false);
                        adminEmployeeResponse.setErrorMessage("连接服务器成功，但返回的数据为空或是异常");
                        Log.d(TAG, "queryFromServer请求成功：但adminRes没有值，抛到到主线程尝试从本地加载res更新UI,messages=4");
                        mhandler.sendEmptyMessage(2);
                    }
                }
            });

        } else {
            Toast.makeText(MyApplication.getContext(), "未知错误", Toast.LENGTH_SHORT).show();
        }
    }

    private void setEmployeName() {
        if (employesName.size() > 0)
            employesName.clear();
        for (Employee e : employees) {
            employesName.add(e.getName());
            serverIDString.add(e.getAccount());
        }
    }


    private String getCOntentString() {

        StringBuilder sb = new StringBuilder();

        String tvNames = Util.setNameXX(apply.getRepair());
        String guzhang = tvCategory.getText().toString().trim();
        String address = tvAdress.getText().toString().trim();
        String cailiao = etMatrial.getText().toString().trim();

        sb.append("报修人:" + tvNames + "\r\n");
        sb.append("联系电话:" + tvTel.getText().toString() + "\r\n");
        sb.append("联系邮箱:" + tvEmail.getText().toString() + "\r\n");
        sb.append("故障:" + guzhang + "\r\n");
        sb.append("位置：" + address + "\r\n");
        sb.append("材料:" + cailiao + "\r\n");

        return sb.toString();
    }


    private List<String> getAccount(String serverMan) {
        Log.d(TAG, "getEmail: " + serverMan);
        List<String> accounts = new ArrayList<>();
        if (serverMan != null && !"".equals(serverMan)) {
            String temp2[] = serverMan.split(",");
            Log.d(TAG, "getEmail: temp[]2的长度" + temp2.length);

            for (int i = 0; i < temp2.length; i++) {
                Log.d(TAG, "getEmail: temp" + i + ":" + temp2[i]);
                Log.d(TAG, "getEmail: employees.size=" + employees.size());
                for (int j = 0; j < employees.size(); j++) {
                    Log.d(TAG, "getEmail: employees.size=" + employees.get(j));
                    if (temp2[i].equals(employees.get(j).getName())) {
                        accounts.add(employees.get(j).getAccount());
                        break;
                    }
                }
            }
            return accounts;
        } else {
            Toast.makeText(this, "请先添加维修人员", Toast.LENGTH_SHORT).show();
            return accounts;
        }
    }

    private void initSubmitOnClick() {

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SharedPreferences preferences = MyApplication.getContext().getSharedPreferences("adminEmail", Context.MODE_PRIVATE);
                //String email = preferences.getString("email", "");
                String email = adminInf.getE_email();
                String password = adminInf.getEmailPassword();
                String account = adminInf.getAccount();
                Log.d(TAG, "onClick: " + email);
                Log.d(TAG, "onClick: " + password);
                //password=Util.encryptStr("LBY681love","R9k1d0?j");

                if (password == null || "".equals(password)) {
                    SweetAlertDialog sweetDialog = new SweetAlertDialog(AdminDetailActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("请到电脑端设置邮箱密码")
                            .setConfirmText("确定")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            });
                    sweetDialog.show();
                    return;
                }


                if (etServerMan.getText().toString().equals("")) {
                    Toast.makeText(AdminDetailActivity.this, "请添加维修人员", Toast.LENGTH_SHORT).show();
                } else {
                    btnSubmit.setBackgroundResource(R.drawable.button_submit);
                    Util.setOnClickBackgroundColor(btnSubmit);
                    String temp = etServerMan.getText().toString();
                    List<String> servManAccount = getAccount(temp);
                    if (servManAccount.size() > 0) {
                        String serverAccounts = getServerAccount(servManAccount);
//                        final String contentString = getCOntentString();
                        String applyId = apply.getId();
                        String imgPath = getImgName();
                        Log.d(TAG, "onClick: URL ->" + TEXT_EMAIL_URL);
                        Log.d(TAG, "onClick: FLAGW ->" + email);
                        Log.d(TAG, "onClick: ID ->" + applyId);
                        Log.d(TAG, "onClick: serverMan ->" + serverAccounts);
                        Log.d(TAG, "onClick: AD ->" + account);
                        //Log.d(TAG, "onClick: imgPath ->" + imgPath);

                        //应该放在Util。submit下方

                        sDialog = new SweetAlertDialog(AdminDetailActivity.this);
                        sDialog.setTitleText("等待服务器返回结果")
                                .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        sDialog.show();
                        //提交维修单
//                        Util.submit("adminEmail", email, "password", password, "content", contentString,
//                                "serverEmail", serverEmail, "ID", apply.getId(), "imgPath", imgPath, TEXT_EMAIL_URL)
                        Util.submits("FLAGW", "sendEmail", "ID", applyId, "serverMan", serverAccounts, "AD", account, TEXT_EMAIL_URL)
                                .readTimeOut(100000L)
                                .writeTimeOut(100000L)
                                .connTimeOut(100000L)
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        Log.d(TAG, "onError: ->联网失败");
                                        Log.d(TAG, "onError: " + e.toString());
                                        Log.d(TAG, "onError: " + id);
                                        mhandler.sendEmptyMessage(11);
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        Log.d(TAG, "onResponse:  ->" + response);
                                        if ("发送成功".equals(response)) {
                                            Log.d(TAG, "onResponse: 获取 " + response);
                                            //发送邮件成功
                                            mhandler.sendEmptyMessage(9);
                                        } else {
                                            //发送邮件失败
                                            mhandler.sendEmptyMessage(10);
                                        }
                                    }
                                });

                    }
                }


            }
        });
    }


    private String getServerAccount(List<String> accounts) {
        String serverAcount = "";
        for (String s : accounts) {
            if (serverAcount.equals("")) {
                serverAcount = s;
                continue;
            }
            serverAcount = serverAcount + "," + s;
        }
        Log.d(TAG, "getServerAccount: " + serverAcount);
        return serverAcount;
    }

    private String getImgName() {
        String imgName = "";
        for (String s : list_imageView) {
            File flies = new File(s);
            Log.d(TAG, "getImgName: " + flies.getName());
            if (imgName.equals("")) {
                imgName = flies.getName();
                continue;
            }
            imgName = imgName + "," + flies.getName();
        }
        return imgName;
    }


}
