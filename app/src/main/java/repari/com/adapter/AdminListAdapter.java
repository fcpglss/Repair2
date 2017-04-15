package repari.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import java.util.ArrayList;
import java.util.List;

import application.MyApplication;


import camera.FIleUtils;
import model.Apply;
import model.Employee;
import model.Response;
import model.ResultBean;
import okhttp3.Call;
import repair.com.repair.AdminListActivity;

import repair.com.repair.R;
import util.JsonUtil;
import util.Util;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.ACTION_SEND_MULTIPLE;
import static repair.com.repair.MainActivity.UP_APPLY;
import static repair.com.repair.MainActivity.windowWitch;
import static repair.com.repair.MainActivity.windowHeigth;

/**
 * Created by hsp on 2017/4/8.
 */

public class AdminListAdapter extends BaseAdapter {

    private static final String TAG = "AdminListAdapter";
    private static boolean isLoadImages = false;

    public static String JSONEMPLOYEE = "http://192.168.31.201:8888/myserver2/AdminServerUpdate";
   // public static String JSONEMPLOYEE = "http://192.168.43.128:8888/myserver2/AdminServerUpdate";
    private Context context;
    private Response response;
    private ResultBean resultBean;
    private LayoutInflater inflater;
    private List<Apply> list = new ArrayList<>();
    private DialogPlus dialogSend;
    private DialogPlus dialogChoose;

    private boolean mCanGetBitmapFromNetWork = true;

    private Drawable mDefaultBitmapDrawable;
    DialogAdapter dialogChooseAdapter;

    List<String> listImage = new ArrayList<>();

    List<String> list1 = new ArrayList<>();

    List<Employee> employees = new ArrayList<>();

    List<File> listFile = new ArrayList<>();

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

    public AdminListAdapter(Context context, ResultBean resultBean) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        mDefaultBitmapDrawable = context.getResources().getDrawable(R.mipmap.ic_launcher);
        this.resultBean = resultBean;
        list = resultBean.getApplys();
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
        Apply apply = list.get(position);
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
            viewHolder.tvDescribe = (TextView) view.findViewById(R.id.textView8);
            viewHolder.tvTel = (TextView) view.findViewById(R.id.tv_admin_item_tel);
            viewHolder.imgView = (ImageView) view.findViewById(R.id.img_admin_pic);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tvName.setText(apply.getRepair());
        viewHolder.tvTime.setText(Util.setTime(apply.getRepairTime()));
        viewHolder.tvcategory.setText(apply.getClasss());
        viewHolder.tvAdress.setText(Util.setTitle(apply));
        viewHolder.tvTel.setText(apply.getTel());
        viewHolder.tvDescribe.setText(apply.getRepairDetails());

        ImageView imageView=viewHolder.imgView;
        final String tag = (String)imageView.getTag();
        String photoUrl = "";

        if(Util.getPhotoUrl(position, resultBean))
        {
            photoUrl=resultBean.getApplys().get(position).getA_imaes().get(0);
        }
        final String uri = photoUrl;
        if (!uri.equals(tag)) {
            imageView.setImageDrawable(mDefaultBitmapDrawable);
        }

        if (mCanGetBitmapFromNetWork&&!photoUrl.equals("")) {
            imageView.setTag(photoUrl);
//            mImageLoader.bindBitmap(photoUrl, imageView, mImageWidth, mImageHeigth);
            Picasso.with(context).load(photoUrl).into(imageView);
        }
        //设置对话框
        setDialog(viewHolder, apply);

        return view;
    }

    private void downLoadBitmap(Apply apply) {
        listImage = apply.getA_imaes();
        listFile.clear();
        new AsyncTask<Void, Void, List<File>>() {
            @Override
            protected List<File> doInBackground(Void... voids) {
                List<File> files = new ArrayList<File>();
                File imgFile = null;
                FileOutputStream out = null;
                for (int i = 0; i < listImage.size(); i++) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = Picasso.with(context).load(listImage.get(i)).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imgFile = FIleUtils.createImageFile2(context);
                    try {
                        out = new FileOutputStream(imgFile);
                        //有图片
                        if (bitmap != null) {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            listFile.add(imgFile);
                        }
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<File> files) {
                super.onPostExecute(files);
            }
        }.execute();
    }

    class ViewHolder {
        TextView tvName, tvTime, tvAdress, tvcategory, tvServerMan, tvDescribe, tvTel, tvDetailClass;
        Button btnSend, btnChoose;
        ImageView imgView;
    }

    private void setDialog(final AdminListAdapter.ViewHolder viewHolder, final Apply apply) {

        list1.clear();
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

                                }
                                if (position == 1) {
                                    String temp = viewHolder.tvServerMan.getText().toString();
                                    List<String> emails = getEmail(temp);
                                    startEmail3(viewHolder, emails);
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

    private List<String> getEmail(String serverMan) {
        List<String> emails = new ArrayList<>();
        if (serverMan != null && !"".equals(serverMan)) {
            String temp2[] = serverMan.split(",");
            for (int i = 0; i < temp2.length; i++) {
                for (int j = 0; j < employees.size(); j++) {
                    if (temp2[i].equals(employees.get(j).getName())) {
                        emails.add(employees.get(j).getE_email());
                        break;
                    }
                }
            }
            return emails;
        } else {
            Toast.makeText(context, "请先添加维修人员", Toast.LENGTH_SHORT).show();
            return emails;
        }
    }

    private void startEmail3(ViewHolder viewHolder, List<String> emails) {
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
            setIntent(listTemp, intent, viewHolder);
            intent.putParcelableArrayListExtra(intent.EXTRA_STREAM, uris);
        } else {
            if (listFile.size() == 0) {
                intent.setType("plain/text");
                Log.d(TAG, "startEmail3: plain/text");
                setIntent(listTemp, intent, viewHolder);
            } else {
                intent.setType("*/*");
                Log.d(TAG, "startEmail3: */*");
                setIntent(listTemp, intent, viewHolder);
                intent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
            }

        }
        context.startActivity(intent);
    }

    private void setIntent(List<String> listTemp, Intent intent, ViewHolder viewHolder) {
        int size = listTemp.size();
        String[] s = listTemp.toArray(new String[size]);
        StringBuilder sb = new StringBuilder();
        String tvName = viewHolder.tvName.getText().toString();
        tvName = Util.setNameXX(tvName);
        sb.append("报修人信息：    " + tvName + "\n");
        sb.append("                            " + viewHolder.tvTel.getText() + "\n");
        sb.append("故障地点：        " + viewHolder.tvAdress.getText() + "\n");
        sb.append("报修时间：        " + viewHolder.tvTime.getText() + "\n");
        sb.append("故障类型：        " + viewHolder.tvcategory.getText() + "\n");
        sb.append("故障描述：        " + viewHolder.tvDescribe.getText() + "\n");
        intent.putExtra(Intent.EXTRA_EMAIL, s);
        intent.putExtra(Intent.EXTRA_CC, s);
        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        intent.putExtra(Intent.EXTRA_SUBJECT, viewHolder.tvAdress.getText().toString());
    }

    private void setChooseDialog(final ViewHolder viewHolder) {

        dialogChooseAdapter = new DialogAdapter(context, list1, R.layout.simple_list_item);
        dialogChoose = DialogPlus.newDialog(context)
                .setAdapter(dialogChooseAdapter)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.dialog_head9)
                .setContentWidth((int) (windowWitch / 1.5))
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                       if(position==-1)
                       {

                       }else
                       {
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
                               }
                               Log.d(TAG, "onItemClick222: 执行了");
                           } else {
                               viewHolder.tvServerMan.setText(list1.get(position).toString());
                               Log.d(TAG, "onItemClick: 执行了");
                           }
                           dialogChoose.dismiss();
                       }
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

                        Log.d(TAG, "onResponse: " + responseJson);
                        if (employees.size() > 0)
                            employees.clear();
                        setEmployee();

                        mhandler.sendEmptyMessage(3);
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

    private void setEmployee() {
        for (Employee e : resultBean.getEmployee()) {
            list1.add(e.getName());
            employees.add(e);
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
