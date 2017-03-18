package fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import application.MyApplication;
import camera.CalculateImage;
import camera.FIleUtils;
import model.Apply;
import model.Area;
import model.Category;
import model.Place;

import model.ResultBean;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import repair.com.repair.MainActivity;
import repair.com.repair.R;
import repari.com.adapter.DialogAdapter;
import repari.com.adapter.DialogDetailAdapter;
import util.JsonUtil;
import util.NetworkUtils;

import static android.R.attr.bitmap;
import static android.R.attr.fillEnabled;
import static android.R.attr.foreground;
import static android.R.attr.x;
import static android.R.attr.y;
import static android.content.Context.MODE_PRIVATE;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static camera.CalculateImage.getSmallBitmap;
import static com.zhy.http.okhttp.OkHttpUtils.post;
import static repair.com.repair.MainActivity.GET_JSON;
import static repair.com.repair.MainActivity.REQUEST_IMAGE;
import static repair.com.repair.MainActivity.TAKE_PHOTO_RAW;
import static repair.com.repair.MainActivity.UP_APPLY;
import static repair.com.repair.MainActivity.list_uri;
import static util.NetworkUtils.isNetworkConnected;


public class ApplyFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ApplyFragment";


    private EditText et_name, et_tel, et_describe, et_details;

    //后面添加的电子邮箱，报修密码，报修区域，楼号，报修类型，类型详情
    private EditText etEmail, etApplyPassword, etArea, etDetailArea, etApplyType, etApplyTypeDetails;
    //记录区域ID
    int AreaId;
    //滚动
    private ScrollView svBackground;
    private boolean svState=false;

    private TextView mtv_no;
    private Button btn_apply;
    private ImageView image_camera, img_add, img_1, img_2, img_3;

    private Button btn_clear;

    //    private Spinner sp_place, sp_category;
    private List<String> list_place = new ArrayList<>();
    private List<String> list_category = new ArrayList<>();
    private List<ImageView> imageViewList = new ArrayList<>();

    private Apply apply = new Apply();

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static ResultBean res = null;

    ArrayAdapter categoryAdapter;
    ArrayAdapter placeAdapter;

    //用于存放报修区域，报修楼号，报修类型，报修详情的list,放入适配器在对话框显示
    private List<String> listArea = new ArrayList<>();
    private List<String> listDetailArea = new ArrayList<>();
    private List<String> listApplyType = new ArrayList<>();
    private List<String> listApplyDetailType = new ArrayList<>();

    //对话框
    DialogPlus dialogArea;
    DialogPlus dialogDetailArea;
    DialogPlus dialogApplyType;
    DialogPlus dialogApplyDetailType;
    DialogPlus dialogGetImage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MainFragment", "Apply_onCreateView");

//        return inflater.inflate(R.layout.apply_frag, null);
        return inflater.inflate(R.layout.apply_fragment, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("Apply_Fragment", "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        initView();

        initData();


    }

    private void initView() {

        //后面添加的几个EditText
        etEmail = (EditText) getActivity().findViewById(R.id.et_email);
        etApplyPassword = (EditText) getActivity().findViewById(R.id.et_apply_password);
        etArea = (EditText) getActivity().findViewById(R.id.et_area);
        etDetailArea = (EditText) getActivity().findViewById(R.id.et_detail_area);
        etApplyType = (EditText) getActivity().findViewById(R.id.et_apply_type);
//        etApplyTypeDetails = (EditText) getActivity().findViewById(R.id.et_apply_type_details);
        //


        et_name = (EditText) getActivity().findViewById(R.id.et_name);

        et_tel = (EditText) getActivity().findViewById(R.id.et_tel);

        et_describe = (EditText) getActivity().findViewById(R.id.et_apply_describe);
        et_details = (EditText) getActivity().findViewById(R.id.et_apply_details);

//        image_camera = (ImageView) getActivity().findViewById(R.id.img_camera);
//        image_camera.setOnClickListener(this);
        img_add = (ImageView) getActivity().findViewById(R.id.iv_add);
        img_add.setOnClickListener(this);

        img_1 = (ImageView) getActivity().findViewById(R.id.iv_img1);
        img_2 = (ImageView) getActivity().findViewById(R.id.iv_img2);
        img_3 = (ImageView) getActivity().findViewById(R.id.iv_img3);
        img_1.setOnClickListener(this);
        img_2.setOnClickListener(this);
        img_3.setOnClickListener(this);
        imageViewList.add(img_1);
        imageViewList.add(img_2);
        imageViewList.add(img_3);
        btn_apply = (Button) getActivity().findViewById(R.id.btn_apply);
        btn_clear = (Button) getActivity().findViewById(R.id.btn_clear);
        btn_apply.setOnClickListener(this);
        btn_clear.setOnClickListener(this);

        //滚动
        svBackground = (ScrollView) getActivity().findViewById(R.id.sv_apply);
//        sp_category = (Spinner) getActivity().findViewById(R.id.sp_apply);
//        sp_place = (Spinner) getActivity().findViewById(R.id.sp_local);
//        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                category = (String) sp_category.getSelectedItem();
//                Log.d("ApplyFragmentSpinner", "onItemSelected: " + category);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                Log.d("ApplyFragmentSpinner", "onNothingSelected: " + category);
//
//            }
//        });
//        sp_place.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                place = (String) sp_place.getSelectedItem();
//                Log.d("ApplyFragmentSpinner", "onItemSelected: " + place);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                Log.d("ApplyFragmentSpinner", "onItemSelected: " + place);
//            }
//        });


    }

    private void bindView() {


        apply.setRepair(et_name.getText().toString());
        apply.setTel(et_tel.getText().toString());
        apply.setEmail(etEmail.getText().toString());
        apply.setPassword(etApplyPassword.getText().toString());
        apply.setArea(etArea.getText().toString());
        apply.setDetailArea(etDetailArea.getText().toString());


        apply.setRoom(et_details.getText().toString());
        apply.setClasss(etApplyType.getText().toString());
        apply.setRepairDetails(et_describe.getText().toString());

    }


    private void initData() {
        new AsyncTask<Void, Void, ResultBean>() {

            @Override
            protected ResultBean doInBackground(Void... voids) {
                SharedPreferences preferences = getActivity().getSharedPreferences("json_data", getActivity().MODE_PRIVATE);
                String json = preferences.getString("json", "");
                res = JsonUtil.jsonToBean(json);
                return res;
            }


            @Override
            protected void onPostExecute(ResultBean resultBean) {
//                setView(resultBean);
                Log.d(TAG, "onPostExecute: ");
                setDialogView(resultBean);
            }
        }.execute();


    }

    private void setDialogView(ResultBean resultBean) {
        if (resultBean != null) {
//            DialogAdapter areaAdapter = new DialogAdapter(getActivity(),listArea,R.layout.simple_list_item);
            DialogAdapter detailAreaAdapter = new DialogAdapter(getActivity(), listDetailArea, R.layout.simple_list_item);
            DialogAdapter areaAdapter = new DialogAdapter(getActivity(), listArea, R.layout.simple_list_item);
            DialogDetailAdapter applyTypeAdapter = new DialogDetailAdapter(getActivity(), listApplyType,resultBean.getCategory(), R.layout.dialog_detail_type);
//            DialogAdapter ApplyTypeDetails = new DialogAdapter(getActivity(),listApplyDetailType,R.layout.simple_list_item);

            setDialogArea(areaAdapter);
            setDialogDetailArea(detailAreaAdapter);
            setDialogApplyType(applyTypeAdapter);
            }


    }




    private void setDialogArea(final DialogAdapter dialogAdapter) {

        for (Area a :
                res.getAreas()) {
            listArea.add(a.getArea());
        }


        dialogArea = DialogPlus.newDialog(getActivity())
                .setAdapter(dialogAdapter)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.dialog_head1)
                .setContentWidth(800)
//                .setCancelable(true)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        Log.d("DialogPlus", "onItemClick() called with: " + "item = [" +
                                item + "], position = [" + position + "]");

                        if (position!=-1){
                            etArea.setText(listArea.get(position));
                            AreaId = position + 1;
                            Log.d(TAG, "onItemClick: 区域Id " + AreaId);

                            dialogArea.dismiss();
                        }


                    }

                })
                .setExpanded(true, 1000)  // This will enable the expand feature, (similar to android L share dialog)
                .create();

        //给EditText设置点击事件
        etArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch: ");
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    dialogAdapter.notifyDataSetChanged();
                    etDetailArea.setText("");
                    dialogArea.show();
                    Log.d(TAG, "onTouch: dialogDetailArea.show()");
                }
                return false;
            }
        });


    }

    private void setDialogDetailArea(final DialogAdapter dialogAdapter) {

        dialogDetailArea = DialogPlus.newDialog(getActivity())
                .setAdapter(dialogAdapter)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.dialog_head2)
                .setContentWidth(800)
//                .setCancelable(true)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        Log.d("DialogPlus", "onItemClick() called with: " + "item = [" +
                                item + "], position = [" + position + "]");
                        if (position!=-1){
                            etDetailArea.setText(listDetailArea.get(position));

                            Log.d(TAG, "onItemClick: 区域Id " + AreaId);
                            dialogDetailArea.dismiss();
                        }

                    }

                })
                .setExpanded(true, 1000)  // This will enable the expand feature, (similar to android L share dialog)
                .create();

        //给EditText设置点击事件
        etDetailArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch: ");
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    listDetailArea.clear();

                    for (Place p :
                            res.getPlaces()) {
                        if (etArea.getText().toString() != null && !etArea.equals(""))
                        {
                            int areaId=getAreaID(etArea.getText().toString());
                            if(areaId==p.getAreaID())
                                listDetailArea.add(p.getP_name());
                        }
                    }
                    dialogAdapter.notifyDataSetChanged();

                    // FIXME: 2017/3/11
                    Log.d(TAG, "onTouch: etArea数据 "+etArea.getText());
                    if (!etArea.getText().toString().equals("")){
                        dialogDetailArea.show();
                        Log.d(TAG, "onTouch:show ");
                    }else {
                        List<String> list = new ArrayList<>();
                        list.add("请先选择报修区域");
                        DialogAdapter dialogAdapter = new DialogAdapter(getActivity(),list,R.layout.simple_list_item);
                        dialogGetImage = DialogPlus.newDialog(getActivity())
                                .setAdapter(dialogAdapter)
                                .setGravity(Gravity.CENTER)
                                .setContentWidth(800)
                                .setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                        dialogGetImage.dismiss();
                                    }

                                })
                                .create();
                        dialogGetImage.show();
                    }

                }
                return false;
            }
        });


    }


    private void setDialogApplyType(DialogDetailAdapter dialogAdapter) {

        for (Category c :
                res.getCategory()) {
            listApplyType.add(c.getC_name());
        }


        dialogApplyType = DialogPlus.newDialog(getActivity())
                .setAdapter(dialogAdapter)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.dialog_head3)
                .setContentWidth(800)
//                .setCancelable(true)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        Log.d("DialogPlus", "onItemClick() called with: " + "item = [" +
                                item + "], position = [" + position + "]");
                        if (position!=-1){
                            etApplyType.setText(listApplyType.get(position));
                            dialogApplyType.dismiss();
                        }
                    }

                })
                .setExpanded(true, 1000)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        etApplyType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch: ");
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    dialogApplyType.show();
                    Log.d(TAG, "onTouch: dialogDetailArea.show()");
                }
                return false;
            }
        });
    }

    private void setDialogApplyTypeDetails(DialogAdapter dialogAdapter) {

    }


    private void setView(ResultBean resultbean) {
        if (resultbean != null) {
            categoryAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list_category);
            placeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list_place);
            if (list_place.size() == resultbean.getPlaces().size()) {
                categoryAdapter.notifyDataSetChanged();
                placeAdapter.notifyDataSetChanged();

//                sp_category.setAdapter(categoryAdapter);
//                sp_place.setAdapter(placeAdapter);

            } else {
                for (Category c : resultbean.getCategory()) {
                    list_category.add(c.getC_name());
                }
                for (Place place : resultbean.getPlaces()) {
                    list_place.add(place.getP_name());
                }
                categoryAdapter.notifyDataSetChanged();
                placeAdapter.notifyDataSetChanged();
//                sp_category.setAdapter(categoryAdapter);
//                sp_place.setAdapter(placeAdapter);
            }

        } else {
            Log.d("Apply_Fragment", "res为null");
            initData();
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("MainFragment", "Apply_onAttach");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("MainFragment", "Apply_onPause");
    }

    @Override
    public void onStop() {

        super.onStop();
        Log.d("MainFragment", "Apply_onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        list_uri.clear();
        Log.d("MainFragment", "Apply_onDestroy");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MainFragment", "Apply_onStart");
        if (MainActivity.list_uri.size() > 0) {
            Log.d("Apply_Fragment", "ApplyFragment已经获得了uri");
            if (list_uri.size() > 3) {
                int length = list_uri.size() - 3;
                for (int i = 0; i < length; i++) {
                    list_uri.remove(i);
                }
            }
            switch (list_uri.size() - 1) {


                case 0:
                    img_1.getLayoutParams().height = 200;
                    img_1.getLayoutParams().width = 200;
                    img_1.setImageBitmap(getSmallBitmap(getRealPathFromURI(list_uri.get(0)), 180, 180));
                    img_2.setImageBitmap(null);
                    img_3.setImageBitmap(null);
                    break;
                case 1:


                    img_1.getLayoutParams().height = 200;
                    img_1.getLayoutParams().width = 200;
                    img_2.getLayoutParams().height = 200;
                    img_2.getLayoutParams().width = 200;

                    img_1.setImageBitmap(getSmallBitmap(getRealPathFromURI(list_uri.get(0)), 180, 180));
                    img_2.setImageBitmap(getSmallBitmap(getRealPathFromURI(list_uri.get(1)), 180, 180));
                    img_3.setImageBitmap(null);
                    break;
                case 2:


                    img_1.getLayoutParams().height = 200;
                    img_1.getLayoutParams().width = 200;
                    img_2.getLayoutParams().height = 200;
                    img_2.getLayoutParams().width = 200;
                    img_3.getLayoutParams().height = 200;
                    img_3.getLayoutParams().width = 200;

                    img_1.setImageBitmap(getSmallBitmap(getRealPathFromURI(list_uri.get(0)), 180, 180));
                    img_2.setImageBitmap(getSmallBitmap(getRealPathFromURI(list_uri.get(1)), 180, 180));
                    img_3.setImageBitmap(getSmallBitmap(getRealPathFromURI(list_uri.get(2)), 180, 180));
                    break;


            }


        } else {

            Log.d("Apply_Fragment", "ApplyFragment没有获取到uri");

        }
        Log.d("MainFragment", "Apply_onResume");
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_apply:

                bindView();
                upApply();
                break;

            case R.id.btn_clear:

                clearAll();

                break;

//            case R.id.img_camera:
//                startCamera();
//                break;

            case R.id.iv_add:
//                startGallery();

                addPic();

                break;
        }

    }

    private void addPic() {
        List<String> list = new ArrayList<>();
        list.add("打开相机");
        list.add("选择本地图片");
        DialogAdapter dialogAdapter = new DialogAdapter(getActivity(),list,R.layout.simple_list_item);
        dialogGetImage = DialogPlus.newDialog(getActivity())
                .setAdapter(dialogAdapter)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.dialog_head4)
                .setContentWidth(800)
//                .setCancelable(true)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        Log.d("DialogPlus", "onItemClick() called with: " + "item = [" +
                                item + "], position = [" + position + "]");
                        if (position==0){
                            Log.d(TAG, "onItemClick: positon = "+position);
                            startCamera();
                        }
                        if (position ==1){
                            Log.d(TAG, "onItemClick: positon = "+position);
                            startGallery();
                        }
                        dialogGetImage.dismiss();
                    }

                })
                .setExpanded(true, 1000)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialogGetImage.show();

    }

    private void startCamera() {
        File file = FIleUtils.createImageFile();
        MainActivity.list_uri.add(Uri.fromFile(file));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            getActivity().startActivityForResult(intent, TAKE_PHOTO_RAW);

        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        Log.d("MainActivity", "getRealPathFromURI: " + result);
        return result;

    }


//    @Override
//    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//        switch (view.getId())
//        {
//            case R.id.sp_apply:
//
//                category = categoryAdapter.getItem(i).toString();
//                Log.d("ApplyFragmentSpinner", "onItemSelected: "+category);
//
////                category= (String) sp_category.getSelectedItem();
//                break;
//            case R.id.sp_local:
//
//                place = placeAdapter.getItem(i).toString();
//                Log.d("ApplyFragmentSpinner", "onItemSelected: "+place);
//
////                place= (String) sp_place.getSelectedItem();
//                break;
//        }
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> adapterView) {
//
//        category= (String) sp_category.getSelectedItem();
//        place= (String) sp_place.getSelectedItem();
//    }

    private int getPlaceId(String placeName) {
        int placeId;
        List<Place> places = res.getPlaces();
        for (Place p : places) {
            if (p.getP_name().equals(placeName)) {
                placeId = p.getP_id();
                return placeId;
            }
        }
        return -1;
    }

    private int getCategoryId(String categoryName) {
        int categoryId;
        List<Category> categories = res.getCategory();
        for (Category c : categories) {
            if (c.getC_name().equals(categoryName)) {
                categoryId = c.getC_id();
                return categoryId;
            }
        }
        return -1;
    }

    private int getAreaID(String areaName) {
        int areaID;
        List<Area> categories = res.getAreas();
        for (Area area : categories) {
            if (area.getArea().equals(areaName)) {
                areaID = area.getId();
                return areaID;
            }
        }
        return -1;
    }


    private void startGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        getActivity().startActivityForResult(intent, REQUEST_IMAGE);
    }


    private void upApply() {


        if (!isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), "请连接网络", Toast.LENGTH_SHORT).show();

        } else {

            if (true) {

                String json = JsonUtil.beanToJson(apply);
                Log.d(TAG, "upApply: json " + json);

                List<File> files = getFiles(list_uri);

                submit(json, files).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(MyApplication.getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //clearAll();
                        Toast.makeText(MyApplication.getContext(), response.toString(), Toast.LENGTH_LONG).show();
                        writePhoneToLocal(apply, MyApplication.getContext());

                    }
                });

            } else {
                Toast.makeText(MyApplication.getContext(), "请填写完整信息...", Toast.LENGTH_SHORT).show();
            }


        }

    }

    //将手机号写入本地文件。
    private void writePhoneToLocal(Apply apply, Context mcontext) {
        String phone = apply.getTel();
        SharedPreferences.Editor editor = mcontext.getSharedPreferences("phoneData", MODE_PRIVATE).edit();
        editor.putString("phone", apply.getTel());
        editor.apply();
    }


    private List<File> getFiles(List<Uri> list_uri) {

        String[] paths = new String[3];
        List<File> files = new ArrayList<>();
        if (list_uri.size() > 0 && list_uri != null) {
            for (int i = 0; i < list_uri.size(); i++) {

                paths[i] = getPath(list_uri.get(i));

                Log.d(TAG, "getFiles: " + paths[i]);
                String newPath = compressImage(paths[i]);
                // Log.d(TAG, "getFiles: "+newPath);
                files.add(new File(newPath));
            }
        }
        return files;

    }


    private String getPath(Uri uri) {

        String[] proj = {MediaStore.Images.Media.DATA};

        //好像是Android多媒体数据库的封装接口，具体的看Android文档
        Cursor cursor = getActivity().managedQuery(uri, proj, null, null, null);
        //按我个人理解 这个是获得用户选择的图片的索引值
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        //将光标移至开头 ，这个很重要，不小心很容易引起越界
        cursor.moveToFirst();
        //最后根据索引值获取图片路径
        String path = cursor.getString(column_index);

        Log.d("Apply_Fragment", "getPath: " + path);

        return path;
    }

    private String compressImage(String path) {

        String getNewPath = getActivity().getExternalCacheDir()
                + new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());

        String nowPath = path;

        Bitmap b = CalculateImage.getSmallBitmap(path, 200, 200);

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(getNewPath));
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "compressImage: " + getNewPath);

        return getNewPath;
    }


    private void clearAll() {
        et_name.setText("");
        et_tel.setText("");
        et_describe.setText("");
        et_details.setText("");

        etArea.setText("");
        etDetailArea.setText("");
        etEmail.setText("");
        etApplyType.setText("");
//        etApplyTypeDetails.setText("");
        etApplyPassword.setText("");

//        sp_category.setSelection(0);
//        sp_place.setSelection(0);

        img_1.setImageBitmap(null);
        img_2.setImageBitmap(null);
        img_3.setImageBitmap(null);

        img_1.getLayoutParams().height = 0;
        img_1.getLayoutParams().width = 0;
        img_2.getLayoutParams().height = 0;
        img_2.getLayoutParams().width = 0;
        img_3.getLayoutParams().height = 0;
        img_3.getLayoutParams().width = 0;
        list_uri.clear();
    }

    private RequestCall submit(String json, List<File> files) {
        PostFormBuilder postFormBuilder = OkHttpUtils.post();
        for (int i = 0; i < files.size(); i++) {
            postFormBuilder.addFile("file", "file" + i + ".jpg", files.get(i));
            Log.d(TAG, "submit: " + files.get(i).getPath());
        }

        Log.d(TAG, "submit: json添加参数");
        postFormBuilder.addParams("apply", json);
        if (files.size() > 0) {
            postFormBuilder.url(UP_APPLY);
        } else {
            postFormBuilder.url(GET_JSON);
        }

        return postFormBuilder.build();
    }

}
