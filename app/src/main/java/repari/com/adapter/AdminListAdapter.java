package repari.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Target;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import application.MyApplication;
import camera.FIleUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import model.Apply;
import model.Employee;
import model.Response;
import model.ResultBean;
import okhttp3.Call;
import repair.com.repair.AdminListActivity;
import repair.com.repair.ChangeActivity;
import repair.com.repair.R;
import util.FileUtils;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.Util;

import static repair.com.repair.MainActivity.JSON_URL;
import static repair.com.repair.MainActivity.UP_APPLY;
import static util.NetworkUtils.isNetworkConnected;
import static repair.com.repair.MainActivity.windowWitch;
import static repair.com.repair.MainActivity.windowHeigth;

/**
 * Created by hsp on 2017/4/8.
 */

public class AdminListAdapter extends BaseAdapter {

    private static final String TAG = "AdminListAdapter";
    public static String JSONEMPLOYEE = "http://192.168.31.201:8888/myserver2/AdminServer";
    private AdminListActivity context;
    private Response response;
    private ResultBean resultBean;
    private LayoutInflater inflater;
    private List<Apply> list = new ArrayList<>();
    private DialogPlus dialogSend;
    private DialogPlus dialogChoose;
    DialogAdapter dialogChooseAdapter;

    List<String> emailList = new ArrayList<>();

    //
    List<Uri> listU = new ArrayList<>();
    List<File> listFile = new ArrayList<>();

    //员工list
    List<Employee> employeeList = new ArrayList<>();
    //存放员工
    List<Employee> saveEmployeeList = new ArrayList<>();

    List<String> list1 = new ArrayList<>();

    List<String> employEmail = new ArrayList<>();

    List<String> employPhone = new ArrayList<>();
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    break;
                case 3:
                    Log.d(TAG, "handleMessage: list1" + list1.toString());
                    dialogChooseAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    public AdminListAdapter(AdminListActivity context, ResultBean resultBean) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.resultBean = resultBean;
        list = resultBean.getApplys();
        employeeList = resultBean.getEmployee();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;

        if (null == view) {
            view = inflater.inflate(R.layout.admin_item_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.btnChoose = (Button) view.findViewById(R.id.btn_choose);
            viewHolder.btnSend = (Button) view.findViewById(R.id.btn_send);
            viewHolder.tvAdress = (TextView) view.findViewById(R.id.tv_admin_item_address);
            viewHolder.tvcategory = (TextView) view.findViewById(R.id.tv_admin_item_category);
            viewHolder.tvName = (TextView) view.findViewById(R.id.tv_admin_item_name);
            viewHolder.tvServerMan = (TextView) view.findViewById(R.id.tv_admin_item_server_man);
            viewHolder.tvTime = (TextView) view.findViewById(R.id.tv_admin_item_time);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Apply apply = list.get(position);
        viewHolder.tvName.setText(apply.getRepair());
        viewHolder.tvTime.setText(Util.setTime(apply.getRepairTime()));
        viewHolder.tvcategory.setText(apply.getClasss());
        viewHolder.tvAdress.setText(Util.setTitle(apply));

        downLoadBitmap(apply);


        //设置对话框
        setDialog(viewHolder);

        return view;
    }


    private void downLoadBitmap(Apply apply) {
        List<String> listImage = apply.getA_imaes();

        new AsyncTask<List<String>, Void, List<Uri>>() {
            @Override
            protected void onPostExecute(List<Uri> uris) {
                super.onPostExecute(uris);
                listU = uris;
            }

            @Override
            protected List<Uri> doInBackground(List<String>... params) {
                List<Uri> listUri = new ArrayList<>();
                for (String s :
                        params[0]) {
                    try {
                        Bitmap bitmap = null;
                        bitmap = Picasso.with(context).load(s).get();
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
                        String uri = FileUtils.saveBitMapToFile(context, timeStamp, bitmap, true, "Android/data/repair.com.repair/cache");
                        Log.d(TAG, "subscribe: " + uri);
                        listUri.add(Uri.parse(uri));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return listUri;
                    }

                }
                return listUri;
            }

        }.execute(listImage);
    }

    class ViewHolder {
        TextView tvName, tvTime, tvAdress, tvcategory, tvServerMan;
        Button btnSend, btnChoose;
    }

    private void setDialog(final AdminListAdapter.ViewHolder viewHolder) {

        Button btnSend = viewHolder.btnSend;
        Button btnChoose = viewHolder.btnChoose;

        final List<String> list = new ArrayList<>();
        list.add("发送邮件");
        list.add("发送短信");
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAdapter dialogAdapter = new DialogAdapter(context, list, R.layout.simple_list_item);
                dialogSend = DialogPlus.newDialog(context)
                        .setAdapter(dialogAdapter)
                        .setGravity(Gravity.CENTER)
                        .setHeader(R.layout.dialog_head8)
                        .setContentWidth((int) (windowWitch / 1.5))
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                Log.d("DialogPlus", "onItemClick() called with: " + "item = [" +
                                        item + "], position = [" + position + "]");
                                if (position == 0) {
                                    Log.d(TAG, "onItemClick: positon = " + position);

                                }
                                if (position == 1) {
                                    Log.d(TAG, "onItemClick: positon = " + position);
                                    startEmail2(viewHolder);

                                }
                                dialogSend.dismiss();
                            }

                        })
                        .setExpanded(true, (int) (windowHeigth / 1.5))  // This will enable the expand feature, (similar to android L share dialog)
                        .create();
                if (!viewHolder.tvServerMan.getText().toString().isEmpty()) {
                    dialogSend.show();
                }

            }
        });


        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = viewHolder.tvcategory.getText().toString();
                upApply(s);
                setChooseDialog(viewHolder);
            }
        });


    }

    private void startEmail(ViewHolder viewHolder) {

        List<String> listTemp = new ArrayList(emailList);
        emailList.clear();


        if (listTemp.size() <= 0) {
            return;
        } else {

            String contentDetails = "";
            String contentBrief = "";
            String shareUrl = "";
            Intent it = new Intent(Intent.ACTION_SEND);
            it.setType("text/plain");
            Intent targeted = null;
            List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(it, 0);
            if (!resInfo.isEmpty()) {
                List<Intent> targetedShareIntents = new ArrayList<Intent>();
                for (ResolveInfo info : resInfo) {
                    targeted = new Intent(Intent.ACTION_SEND);
                    targeted.setType("text/plain");
                    ActivityInfo activityInfo = info.activityInfo;

                    // judgments : activityInfo.packageName, activityInfo.name, etc.
                    if (!activityInfo.name.contains("mail")) {
                        continue;
                    }
                    int size = listTemp.size();
                    String[] s = listTemp.toArray(new String[size]);
                    StringBuilder sb = new StringBuilder();
                    sb.append("报修人：  " + viewHolder.tvName.getText() + "\n");
                    sb.append("报修地点：" + viewHolder.tvAdress.getText() + "\n");
                    sb.append("报修时间：" + viewHolder.tvTime.getText() + "\n");
                    sb.append("报修类型：" + viewHolder.tvcategory.getText() + "\n");

                    Log.d(TAG, "startEmail: " + s[0]);
                    targeted.putExtra(Intent.EXTRA_EMAIL, new String[]{"3487394@163.com", "45435@qq.com"});
                    Log.d(TAG, "startEmail: " + viewHolder.tvAdress.getText().toString());
                    targeted.putExtra(Intent.EXTRA_SUBJECT, viewHolder.tvAdress.getText().toString()); // 主题
                    Log.d(TAG, "startEmail: " + sb.toString());
                    targeted.putExtra(Intent.EXTRA_TEXT, sb.toString()); // 正文
                    targeted.setPackage(activityInfo.packageName);


                }

                if (targeted != null) {
                    try {
                        context.startActivity(targeted);
                    } catch (Exception e) {
                        throw new RuntimeException("不能启动");
                    }
                }

            }
        }
    }

    private void startEmail1(ViewHolder viewHolder) {

        List<String> listTemp = new ArrayList(emailList);
        emailList.clear();


        boolean found = false;
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");

        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(share, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains("mail")
                        || info.activityInfo.name.toLowerCase().contains("mail")) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("报修人： " + viewHolder.tvName.getText() + "\n");
                    sb.append("报修地点： " + viewHolder.tvAdress.getText() + "\n");
                    sb.append("报修时间： " + viewHolder.tvTime.getText() + "\n");
                    sb.append("报修类型：" + viewHolder.tvcategory.getText() + "\n");
                    share.putExtra(Intent.EXTRA_EMAIL, new String[]{"1422189551@qq.com", "1422189552@qq.com"});
                    share.putExtra(Intent.EXTRA_SUBJECT, viewHolder.tvAdress.getText().toString()); // 主题
                    share.putExtra(Intent.EXTRA_TEXT, sb.toString()); // 正文
                }
            }
            if (!found) {
                return;
            }
            context.startActivity(Intent.createChooser(share, "选择"));
        }
    }

    private void startEmail2(ViewHolder viewHolder) {
        List<String> listTemp = new ArrayList(emailList);
        emailList.clear();

        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("image/*");
        intent.setType("application/octet-stream");
        intent.setType("message/rfc882");
        Intent.createChooser(intent, "Choose Email Client");

        String[] tos = {"way.ping.li@gmail.com"};
        String[] ccs = {"way.ping.li@gmail.com"};
        String[] bccs = {"way.ping.li@gmail.com"};


        int size = listTemp.size();
        String[] s = listTemp.toArray(new String[size]);
        StringBuilder sb = new StringBuilder();
        sb.append("报修人：  " + viewHolder.tvName.getText() + "\n");
        sb.append("报修地点：" + viewHolder.tvAdress.getText() + "\n");
        sb.append("报修时间：" + viewHolder.tvTime.getText() + "\n");
        sb.append("报修类型：" + viewHolder.tvcategory.getText() + "\n");


        intent.putExtra(Intent.EXTRA_EMAIL, s);
        intent.putExtra(Intent.EXTRA_CC, s);
//        intent.putExtra(Intent.EXTRA_BCC, bccs);
        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        intent.putExtra(Intent.EXTRA_SUBJECT, viewHolder.tvAdress.getText().toString());

//        Log.d(TAG, "startEmail2: "+listU.get(0));

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/yingyongbao_7022130.apk";
//        for(int i=0;i<listU.size();i++)
//        {
//            File file =new File(listU.get(i).toString());
//            Log.d(TAG, "startEmail2: "+file.getPath().toString());
//            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//        }
//        String path1 = context.getExternalCacheDir()
        String path1 = listU.get(0).toString();
        Log.d(TAG, "startEmail2: " + path1);

        File file = new File(path1);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        context.startActivity(intent);


    }

    private void setChooseDialog(final ViewHolder viewHolder) {

        list1.clear();
//        emailList.clear();
        dialogChooseAdapter = new DialogAdapter(context, list1, R.layout.simple_list_item);
        dialogChoose = DialogPlus.newDialog(context)
                .setAdapter(dialogChooseAdapter)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.dialog_head9)
                .setContentWidth((int) (windowWitch / 1.5))
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        String temp = viewHolder.tvServerMan.getText().toString();
                        boolean flag = false;
                        if (temp != null && !"".equals(temp)) {
                            String temp2[] = temp.split(",");

                            for (int i = 0; i < temp2.length; i++) {
                                if (list1.get(position).equals(temp2[i])) {
                                    flag = true;
                                }
                            }
                            if (!flag) {
                                viewHolder.tvServerMan.setText(temp + "," + list1.get(position));
                                emailList.add(employEmail.get(position).toString());
                            }
                            Log.d(TAG, "onItemClick222: 执行了");
                        } else {
                            viewHolder.tvServerMan.setText(list1.get(position).toString());
                            emailList.add(employEmail.get(position).toString());
                            Log.d(TAG, "onItemClick: 执行了");

                        }
                        dialogChoose.dismiss();
                    }

                })
                .setExpanded(true, (int) (windowHeigth / 1.5))  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialogChoose.show();
    }


    private void upApply(String categoryName) {

        if (true) {

            List<File> files = new ArrayList<>();

            submit(categoryName, files).execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    //   Toast.makeText(MyApplication.getContext(), "我的报修页面请求失败", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onResponse(String responses, int id) {
                    final String responseJson = responses.toString();
                    //解析json获取到Response;
                    response = JsonUtil.jsonToResponse(responseJson);
                    if (response != null) {
                        resultBean = response.getResultBean();
                    }
                    if (resultBean != null) {
                        Log.d(TAG, "queryFromServer请求成功：res有值，抛到到主线程更新UI,messages=3");
                        Log.d(TAG, "onFinish: " + responseJson);
                        for (Employee e : resultBean.getEmployee()) {
                            list1.add(e.getName());
                            employEmail.add(e.getE_email());
                        }
                        mhandler.sendEmptyMessage(3);

                        //   Util.writeJsonToLocal(adminRes, MyApplication.getContext());//注意刷新和冲突
                    } else {
                        response.setErrorType(-2);
                        response.setError(false);
                        response.setErrorMessage("连接服务器成功，但返回的数据为空或是异常");
                        Log.d(TAG, "queryFromServer请求成功：但res没有值，抛到到主线程尝试从本地加载res更新UI,messages=4");
                        mhandler.sendEmptyMessage(2);
                    }
                }
            });

        } else {
            Toast.makeText(MyApplication.getContext(), "未知错误", Toast.LENGTH_SHORT).show();
        }
    }


    private RequestCall submit(String phone, List<File> files) {
        PostFormBuilder postFormBuilder = OkHttpUtils.post();
        for (int i = 0; i < files.size(); i++) {
            postFormBuilder.addFile("file", "file" + i + ".jpg", files.get(i));
            Log.d(TAG, "submit: " + files.get(i).getPath());
        }


        postFormBuilder.addParams("categoryName", phone);
        if (files.size() > 0) {
            postFormBuilder.url(UP_APPLY);
        } else {
            postFormBuilder.url(JSONEMPLOYEE);

        }

        return postFormBuilder.build();
    }


}
