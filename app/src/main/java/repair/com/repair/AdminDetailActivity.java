package repair.com.repair;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import application.MyApplication;
import imagehodler.ImageLoader;
import model.Apply;
import model.Category;
import model.Employee;
import model.Response;
import model.ResultBean;
import okhttp3.Call;
import repari.com.adapter.AdminListAdapter;
import repari.com.adapter.DialogAdapter;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.Util;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.ACTION_SEND_MULTIPLE;
import static repair.com.repair.MainActivity.windowHeigth;
import static repair.com.repair.MainActivity.windowWitch;

/**
 * Created by Administrator on 2017-4-15.
 */

public class AdminDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AdminDetailActivity";

    public static String JSONEMPLOYEE = "http://192.168.31.201:8888/myserver2/AdminServerUpdate";
    // public static String JSONEMPLOYEE = "http://192.168.43.128:8888/myserver2/AdminServerUpdate";
    private static final String URL="http://192.168.31.201:8888/myserver2/servlet/action";
     // private static final String URL="http://192.168.43.128:8888/myserver2/servlet/action";

    private boolean isFirst=true;
    private boolean isSend=false;
    //大图片
    private ImageView showBigImg;
    //背景
    LinearLayout linearLayoutDetail;
    //对话框
    private DialogPlus dialogChoose;
    private DialogAdapter dialogChooseAdapter;

    private TextView  tvName, tvTel,tvTime, tvEmail,tvAdress,tvCategory, tvDetailAddress,  tvDescribe,tvDetailCategory;

    private ImageView imgCategory;
    private ImageView img1, img2, img3;

    private Apply apply = null;

    private List<Employee> employees;
    private List<String> employesName= new ArrayList<>();
    List<File> listFile = new ArrayList<>();
    private String repairId;

    private Button btnChoose,btnSend,btnSubmit;

    private EditText etServerMan;
    private ResultBean adminDetailRes = null;
    private Category category = null;

    private List<ImageView> imageviewList = new ArrayList<ImageView>();

    private List<ImageView> star_list=new ArrayList<ImageView>();
    private List<String> list_imageView = new ArrayList<>();
    private Response adminDetailResponse;
    private Response adminEmployeeResponse;
    private ResultBean adminEmployeeResutlt;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "msg="+msg.what);
            switch (msg.what) {
                case 2:
                    Log.d(TAG, "handleMessage2:连接服务器失败,尝试从本地文件读取");
                    Toast.makeText(MyApplication.getContext(),adminDetailResponse.getErrorMessage(),Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Log.d(TAG, "handleMessage3 ");
                    bindItem();
                    break;
                case 4:
                    Toast.makeText(AdminDetailActivity.this,"连接服务器成功,但是服务器返回数据错误",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "handleMessage4 ");
                    break;
                case 5:
                    Log.d(TAG, "handleMessage: employeeName" + employesName.toString());
                    dialogChooseAdapter.notifyDataSetChanged();
                    setChooseDialog();
                    break;

            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_detail_infor);
        if(isFirst)
        {
            initView();
            repairId=getIntent().getStringExtra("repairId");
            Log.d(TAG, "onCreate: 获取repairId="+repairId);
            if(apply==null)
            {
                queryFromServer(URL,repairId);
            }
            else
            {
                bindItem();
            }
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
                adminDetailResponse = JsonUtil.jsonToResponse(responseJson);
                if (adminDetailResponse.getErrorType() != 0) {
                    //连接成功，但读取数据失败
                    mhandler.sendEmptyMessage(4);
                }
                //连接成功，抛到主线程更新UI
                else {
                    adminDetailRes = adminDetailResponse.getResultBean();
                    apply=adminDetailRes.getApplys().get(0);
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
        tvEmail= (TextView) findViewById(R.id.tv_admin_details_email);
        //故障信息
        tvAdress= (TextView) findViewById(R.id.tv_admin_details_address);
        tvDescribe = (TextView) findViewById(R.id.tv_admin_details_describe);
        tvCategory = (TextView) findViewById(R.id.tv_admin_details_category);
        tvDetailAddress= (TextView) findViewById(R.id.tv_admin_details_detailaddress);
        tvTime = (TextView) findViewById(R.id.tv_admin_details_repairtime);
        //button按钮
        btnChoose= (Button) findViewById(R.id.btn_admin_add);
        btnSend= (Button) findViewById(R.id.btn_admin_send);
        btnSubmit= (Button) findViewById(R.id.btn_admin_subimit);
        dialogChooseAdapter = new DialogAdapter(this, employesName, R.layout.simple_list_item);
        //editText
        etServerMan= (EditText) findViewById(R.id.et_admin_serverman);


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
        //设置报修人信息
        tvName.setText(apply.getRepair());
        tvTel.setText(apply.getTel());
        tvEmail.setText(apply.getEmail());
        //设置故障信息
        tvAdress.setText(Util.setAddress(apply));
        tvCategory.setText(apply.getClasss()+" "+(""+apply.getDetailClass()+""));
        tvDescribe.setText(apply.getRepairDetails());
        tvDetailAddress.setText(apply.getRepairDetails());
        tvTime.setText(Util.setTime(apply.getRepairTime()));
        //设置类型图标
        imgCategory.setImageResource(getCategoryIcon());
        getApplyImages();
        if (apply.getA_imaes().size() > 0) {
            for (int i = 0; i <= apply.getA_imaes().size() - 1; i++) {
                Log.d(TAG, "该Apply的images集合:" + apply.getA_imaes().get(i));
            }
        } else {
            Log.d(TAG, "该Apply的images集合为空");
        }
        setButtonOnClick();
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
                Picasso.with(this).load(list_imageView.get(i)).into(imageviewList.get(i));
                LinearLayout.LayoutParams layout = (LinearLayout.LayoutParams) imageviewList.get(i).getLayoutParams();
                layout.height = windowHeigth/4;
                layout.width = (int) (windowWitch/3.3);
                imageviewList.get(i).setLayoutParams(layout);
                Log.d(TAG, "Picasso执行一次 "+list_imageView.get(i));
            }
        } else {
            Log.d(TAG, "list_imageView为0,该Apply没有图片");
        }

    }


    private int getCategoryIcon() {
        int image = R.id.img_admin_category;
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){

            if (showBigImg.getVisibility() == View.VISIBLE){
                showBigImg.setVisibility(View.GONE);
                linearLayoutDetail.setBackgroundColor(Color.rgb(211,211,211));
            }else{
                Intent intent = new Intent(this,AdminListActivity.class);
                startActivity(intent);
                Log.d(TAG, "onKeyDown: 返回了main");
                this.finish();
            }
        }
        return true;
    }
    private void setButtonOnClick()
    {
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upApply(apply.getClasss());
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = etServerMan.getText().toString();
                List<String> emails = getEmail(temp);
                startEmail( emails);
            }
        });
    }
    private void setSubmitButton()
    {
        if(isSend)
        {
            btnSend.setBackgroundResource(R.drawable.button_submit);
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

    }
    private void setChooseDialog() {

        dialogChoose = DialogPlus.newDialog(this)
                .setAdapter(dialogChooseAdapter)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.dialog_head9)
                .setContentWidth((int) (windowWitch / 1.5))
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        if(position!=-1) {
                            String temp = etServerMan.getText().toString();
                            setServerManText(position, temp);
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

            Util.submit("categoryName",categoryName,JSONEMPLOYEE ).execute(new StringCallback() {
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
                        employees=adminEmployeeResutlt.getEmployee();
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
        for(Employee e :employees)
        {
            employesName.add(e.getName());
        }
    }
    private void startEmail( List<String> emails) {
        List<String> listTemp = emails;
        ArrayList<Uri> uris = new ArrayList<>();
        for (int i = 0; i < listFile.size(); i++) {
            Uri u = Uri.fromFile(listFile.get(i));
            uris.add(u);
        }
        boolean multple = uris.size() > 1;
        Log.d(TAG, "startEmail3: uri的size: "+ uris.size());
        Intent intent = new Intent(multple ? ACTION_SEND_MULTIPLE : ACTION_SEND);
        if (multple) {
            intent.setType("*/*");
            setIntent(listTemp, intent);
            intent.putParcelableArrayListExtra(intent.EXTRA_STREAM, uris);
        } else {
            if (listFile.size() == 0) {
                intent.setType("plain/text");
                Log.d(TAG, "startEmail3: plain/text");
                setIntent(listTemp, intent);
            } else {
                intent.setType("*/*");
                Log.d(TAG, "startEmail3: */*");
                setIntent(listTemp, intent);
                intent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
            }

        }
        startActivity(intent);
    }
    private void setIntent(List<String> listTemp, Intent intent) {
        int size = listTemp.size();
        String[] s = listTemp.toArray(new String[size]);
        Log.d(TAG, "setIntent: emaillist："+listTemp.toString());
        StringBuilder sb = new StringBuilder();

        String tvNames = Util.setNameXX(apply.getRepair());
        sb.append("报修人信息：    " + tvNames + "\n");
        sb.append("                            " + tvTel.getText().toString() + "\n");
        sb.append("故障地点：        " + tvAdress.getText().toString() + "\n");
        sb.append("具体地点：        " + tvDetailAddress.getText().toString() + "\n");
        sb.append("故障类型：        " + tvCategory.getText().toString() + "\n");
        sb.append("故障描述：        " + tvDescribe.getText().toString() + "\n");
        sb.append("报修时间：        " + tvTime.getText().toString() + "\n");
        intent.putExtra(Intent.EXTRA_EMAIL, s);
        intent.putExtra(Intent.EXTRA_CC, s);
        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        intent.putExtra(Intent.EXTRA_SUBJECT, tvAdress.getText().toString());
    }
    private List<String> getEmail(String serverMan) {
        Log.d(TAG, "getEmail: "+serverMan);
        List<String> emails = new ArrayList<>();
        if (serverMan != null && !"".equals(serverMan)) {
            String temp2[] = serverMan.split(",");
            Log.d(TAG, "getEmail: temp[]2的长度"+temp2.length);

            for (int i = 0; i < temp2.length; i++) {
                Log.d(TAG, "getEmail: temp"+i+":"+temp2[i]);
                Log.d(TAG, "getEmail: employees.size="+employees.size());
                for (int j = 0; j < employees.size(); j++) {
                    Log.d(TAG, "getEmail: employees.size="+employees.get(j));
                    if (temp2[i].equals(employees.get(j).getName())) {
                        emails.add(employees.get(j).getE_email());
                        break;
                    }
                }
            }
            return emails;
        } else {
            Toast.makeText(this, "请先添加维修人员", Toast.LENGTH_SHORT).show();
            return emails;
        }
    }
}
