package fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
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
import model.Flies;
import model.Place;
import model.Response;
import model.ResultBean;
import model.Room;
import okhttp3.Call;
import repair.com.repair.MainActivity;
import repair.com.repair.R;
import repari.com.adapter.DialogAdapter;
import repari.com.adapter.DialogDetailAdapter;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.Util;

import static android.content.Context.MODE_PRIVATE;
import static camera.CalculateImage.getSmallBitmap;
import static repair.com.repair.MainActivity.GET_JSON;
import static repair.com.repair.MainActivity.JSON_URL;
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
    int PlaceId;//楼号ID
    int fliesId;//层号ID
    // 添加层号 房间号
    private EditText etFloor, etRoom;


    //滚动
    private ScrollView svBackground;
    private Button btn_apply;
    private ImageView image_camera, img_add, img_1, img_2, img_3;

    private RelativeLayout rl1, rl2, rl3;
    //打叉图片
    private ImageView ivX1, ivX2, ivX3;
    //显示大图
    private LinearLayout llBigImg;
    private ImageView ivBigImg;

    private Button btn_clear;

    private List<String> list_place = new ArrayList<>();
    private List<String> list_category = new ArrayList<>();
    private List<ImageView> imageViewList = new ArrayList<>();

    private Apply apply = new Apply();

    public static ResultBean addressRes = null;

    ArrayAdapter categoryAdapter;
    ArrayAdapter placeAdapter;

    //用于存放报修区域，报修楼号，报修类型，报修详情的list,放入适配器在对话框显示
    private List<String> listArea = new ArrayList<>();
    private List<String> listDetailArea = new ArrayList<>();
    private List<String> listFloor = new ArrayList<>();
    private List<String> listRoom = new ArrayList<>();
    //用于存放Id的list
    private List<Integer> listAreaID = new ArrayList<>();
    private List<Integer> listDetailAreaID = new ArrayList<>();
    private List<Integer> listFloorID = new ArrayList<>();
    private List<Integer> listRoomID = new ArrayList<>();
    private List<Integer> listApplyTypeID = new ArrayList<>();

    private List<String> listApplyType = new ArrayList<>();
    private List<String> listApplyDetailType = new ArrayList<>();

    private int areaId = 0;
    private int placeId = 0;
    private int flieId = 0;
    private int roomId = 0;
    private int categoryId = 0;

    //用来比较的list
    List<Uri> list = new ArrayList<>();

    Uri[] arrayUri = new Uri[3];

    private Response response;
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    Log.d(TAG, "handleMessage2:连接服务器失败,尝试从本地文件读取");
                    String addressJson2 = Util.loadAddressFromLocal(MyApplication.getContext());
                    addressRes = JsonUtil.jsonToBean(addressJson2);
                    Log.d(TAG, "handleMessage:2 本地数据addressRes" + addressJson2);
                    setDialogView(addressRes);
                    break;
                case 3:
                    setDialogView(addressRes);
                    break;
                case 4:
                    Log.d(TAG, "handleMessage4: 内存没有数据，尝试从本地文件读取");
                    String addressJson = Util.loadAddressFromLocal(MyApplication.getContext());
                    addressRes = JsonUtil.jsonToBean(addressJson);
                    setDialogView(addressRes);
                    break;
                case 5:
                    Toast.makeText(getActivity(), "请填写报修地址", Toast.LENGTH_SHORT).show();
                    ;
                    break;
            }
        }
    };
    //对话框
    DialogPlus dialogArea;
    DialogPlus dialogDetailArea;
    DialogPlus dialogFloor;
    DialogPlus dialogRoom;

    DialogPlus dialogApplyType;
    DialogPlus dialogGetImage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "Apply_onCreateView");

        return inflater.inflate(R.layout.apply_fragment, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        initView();
        queryFromServer(JSON_URL);
    }

    private void initView() {

        etEmail = (EditText) getActivity().findViewById(R.id.et_email);
        etApplyPassword = (EditText) getActivity().findViewById(R.id.et_apply_password);
        etArea = (EditText) getActivity().findViewById(R.id.et_area);
        etDetailArea = (EditText) getActivity().findViewById(R.id.et_detail_area);
        etApplyType = (EditText) getActivity().findViewById(R.id.et_apply_type);
        et_name = (EditText) getActivity().findViewById(R.id.et_name);

        etFloor = (EditText) getActivity().findViewById(R.id.et_floor);
        etRoom = (EditText) getActivity().findViewById(R.id.et_room);

        et_tel = (EditText) getActivity().findViewById(R.id.et_tel);

        et_describe = (EditText) getActivity().findViewById(R.id.et_apply_describe);
        et_details = (EditText) getActivity().findViewById(R.id.et_apply_details);

        img_add = (ImageView) getActivity().findViewById(R.id.iv_add);
        img_add.setOnClickListener(this);

        img_1 = (ImageView) getActivity().findViewById(R.id.iv_img1);
        img_2 = (ImageView) getActivity().findViewById(R.id.iv_img2);
        img_3 = (ImageView) getActivity().findViewById(R.id.iv_img3);

        //包裹三张图片的RelativeLayout
        rl1 = (RelativeLayout) getActivity().findViewById(R.id.rl_img1);
        rl2 = (RelativeLayout) getActivity().findViewById(R.id.rl_img2);
        rl3 = (RelativeLayout) getActivity().findViewById(R.id.rl_img3);
        //打叉图片
        ivX1 = (ImageView) getActivity().findViewById(R.id.iv_img_x1);
        ivX2 = (ImageView) getActivity().findViewById(R.id.iv_img_x2);
        ivX3 = (ImageView) getActivity().findViewById(R.id.iv_img_x3);
        //设置打叉点击事件
        XOnclick();
        //大图
        llBigImg = (LinearLayout) getActivity().findViewById(R.id.ll_big_img);
        ivBigImg = (ImageView) getActivity().findViewById(R.id.iv_big_img);

        //设置图片点击事件
        imgOnclick();

        imageViewList.add(img_1);
        imageViewList.add(img_2);
        imageViewList.add(img_3);
        btn_apply = (Button) getActivity().findViewById(R.id.btn_apply);
        btn_clear = (Button) getActivity().findViewById(R.id.btn_clear);
        btn_apply.setOnClickListener(this);
        btn_clear.setOnClickListener(this);

        //滚动
        svBackground = (ScrollView) getActivity().findViewById(R.id.sv_apply);


        //设置点击颜色
        btn_apply.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    Log.d(TAG, "onTouch: "+event.getAction());
                    btn_apply.setBackgroundColor(Color.parseColor("#65B5FF"));
                }else if (event.getAction() ==MotionEvent.ACTION_UP){
                    Log.d(TAG, "onTouch: "+event.getAction());
                    btn_apply.setBackgroundColor(Color.parseColor("#6699ff"));
                }
                return false;
            }
        });
        btn_clear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    Log.d(TAG, "onTouch: "+event.getAction());
                    btn_clear.setBackgroundColor(Color.parseColor("#65B5FF"));
                }else if (event.getAction() ==MotionEvent.ACTION_UP){
                    Log.d(TAG, "onTouch: "+event.getAction());
                    btn_clear.setBackgroundColor(Color.parseColor("#6699ff"));
                }
                return false;
            }
        });

    }

    private void imgOnclick() {
        img_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBigImg(v);
            }
        });
        img_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBigImg(v);
            }
        });
        img_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBigImg(v);
            }
        });
    }

    private void setBigImg(View v) {
        ImageView iv = (ImageView) v;
        llBigImg.setVisibility(View.VISIBLE);
        ivBigImg.setImageDrawable(iv.getDrawable());
        ivBigImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llBigImg.setVisibility(View.GONE);
                ivBigImg.setImageDrawable(null);
            }
        });
    }

    private void XOnclick() {

        ivX1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img_1.setImageDrawable(null);
                rl1.setVisibility(View.GONE);
                rl2.setVisibility(View.GONE);
                rl3.setVisibility(View.GONE);
                list_uri.remove(0);
                switchImage();

            }
        });
        ivX2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img_2.setImageDrawable(null);
                rl1.setVisibility(View.GONE);
                rl2.setVisibility(View.GONE);
                rl3.setVisibility(View.GONE);
                list_uri.remove(1);
                switchImage();
            }
        });
        ivX3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_3.setImageDrawable(null);
                rl1.setVisibility(View.GONE);
                rl2.setVisibility(View.GONE);
                rl3.setVisibility(View.GONE);
                list_uri.remove(2);
                switchImage();
            }
        });
    }

    private void bindView() {

        apply.setRepair(et_name.getText().toString());
        apply.setTel(et_tel.getText().toString());
        apply.setEmail(etEmail.getText().toString());
        apply.setPassword(etApplyPassword.getText().toString());
//        apply.setArea(etArea.getText().toString());
//        apply.setDetailArea(etDetailArea.getText().toString());
//        apply.setFlies(etFloor.getText().toString());
//        apply.setRoom(etRoom.getText().toString());
//        apply.setClasss(etApplyType.getText().toString());
        setApply();
        apply.setRepairDetails(et_describe.getText().toString());
    }


    public void queryFromServer(String url) {

        String jsonurl = url + "?applyfragment";
        Log.d(TAG, "queryFromServer: " + jsonurl);
        HttpUtil.sendHttpRequest(jsonurl, new HttpCallbackListener() {
            @Override
            public void onFinish(String responseString) {
                //请求成功后获取到json
                final String responseJson = responseString.toString();
                Log.d(TAG, "请求成功onFinish: " + responseJson);
                //解析json获取到Response;
                response = JsonUtil.jsonToResponse(responseJson);
                if (response.getErrorType() != 0) {
                    //response出现错误，尝试从内存中获取数据
                    if (addressRes != null) {
                        mhandler.sendEmptyMessage(4);
                    }
                    //内存没有，尝试从本地获取数据
                    else {
                        mhandler.sendEmptyMessage(2);
                    }
                }
                //连接成功，抛到主线程更新UI
                else {
                    addressRes = response.getResultBean();
                    mhandler.sendEmptyMessage(3);
                    Util.writeAddressToLocal(addressRes, MyApplication.getContext());
                }
            }

            @Override
            public void onError(Exception e) {
                Response rp = new Response();
                rp.setErrorType(-1);
                rp.setError(true);
                rp.setErrorMessage("网络异常，返回空值");
                response = rp;
                Log.d(TAG, "onError: " + e.getMessage() + ",response错误信息:" + rp.getErrorMessage());
                mhandler.sendEmptyMessage(2);
            }
        });
    }


    private void setDialogView(ResultBean resultBean) {
        if (resultBean != null) {


            DialogAdapter areaAdapter = new DialogAdapter(getActivity(), listArea, R.layout.simple_list_item);
            DialogAdapter detailAreaAdapter = new DialogAdapter(getActivity(), listDetailArea, R.layout.simple_list_item);
            DialogAdapter floorAdapter = new DialogAdapter(getActivity(), listFloor, R.layout.simple_list_item);
            DialogAdapter roomAdapter = new DialogAdapter(getActivity(), listRoom, R.layout.simple_list_item);
            DialogDetailAdapter applyTypeAdapter = new DialogDetailAdapter(getActivity(), listApplyType, resultBean.getCategory(), R.layout.dialog_detail_type);


            //选择区域对话框
            setDialogArea(areaAdapter);
            //选择楼号对话框
            setDialogDetailArea(detailAreaAdapter);
            //选择层数对话框
            setDialogFloor(floorAdapter);
            //选择房间对话框
            setDialogRoom(roomAdapter);
            //选择类型对话框
            setDialogApplyType(applyTypeAdapter);
        }


    }

    private void setDialogArea(final DialogAdapter dialogAdapter) {

        for (Area a :
                addressRes.getAreas()) {
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
                        if (position != -1) {
                            etArea.setText(listArea.get(position));
                            areaId = getAreaID(listArea.get(position));
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
                    //点击区域清空 楼号 层号 房间
                    etDetailArea.setText("");
                    etFloor.setText("");
                    etRoom.setText("");
                    //清空存放好的Id
                    placeId = 0;
                    flieId = 0;
                    roomId = 0;
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
                        if (position != -1) {
                            etDetailArea.setText(listDetailArea.get(position));
                            placeId = getDetailId(listDetailArea.get(position));
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
                    //点击区域清空 层号 房间
                    etFloor.setText("");
                    etRoom.setText("");
                    //清空存放好的Id
                    flieId = 0;
                    roomId = 0;

                    listDetailArea.clear();

                    for (Place p :
                            addressRes.getPlaces()) {
                        if (etArea.getText().toString() != null && !etArea.equals("")) {
                            areaId = getAreaID(etArea.getText().toString());
                            if (areaId == p.getAreaID())
                                listDetailArea.add(p.getP_name());
                        }
                    }
                    dialogAdapter.notifyDataSetChanged();

                    // 提示先选择区域
                    Log.d(TAG, "onTouch: etArea数据 " + etArea.getText());
                    if (!etArea.getText().toString().equals("")) {
                        dialogDetailArea.show();
                        Log.d(TAG, "onTouch:show ");
                    } else {
                        List<String> list = new ArrayList<>();
                        list.add("请先选择报修区域");
                        DialogAdapter dialogAdapter = new DialogAdapter(getActivity(), list, R.layout.simple_list_item);
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

    private void setDialogFloor(final DialogAdapter dialogAdapter) {

        dialogFloor = DialogPlus.newDialog(getActivity())
                .setAdapter(dialogAdapter)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.dialog_head6)
                .setContentWidth(800)
//                .setCancelable(true)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        Log.d("DialogPlus", "onItemClick() called with: " + "item = [" +
                                item + "], position = [" + position + "]");
                        if (position != -1) {
                            etFloor.setText(listFloor.get(position));
//                            flieId = getFloor(listFloor.get(position));
                            flieId = listFloorID.get(position);
                            dialogFloor.dismiss();
                        }

                    }

                })
                .setExpanded(true, 1000)  // This will enable the expand feature, (similar to android L share dialog)
                .create();

        //给EditText设置点击事件
        etFloor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch: ");
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    //点击区域清空  房间
                    etRoom.setText("");
                    //清空存放好的Id
                    roomId = 0;
                    listFloor.clear();
                    listFloorID.clear();
                    Log.d(TAG, "onTouch: for之前");

                    //// FIXME: 2017/3/22 添加相应数据
                    for (Flies f : addressRes.getFlies()) {
                        Log.d(TAG, "onTouch: f：" + f.getFlies());
                        if (etDetailArea.getText().toString() != null && !etDetailArea.equals("")) {
                            int id = getDetailId(etDetailArea.getText().toString());
                            Log.d(TAG, "onTouch: id " + id);
                            Log.d(TAG, "onTouch: f.getaFloor:" + f.getaFloor());
                            Log.d(TAG, "onTouch: f.getid " + f.getId());
                            if (id == f.getaFloor()) {
                                listFloor.add(f.getFlies());
                                Log.d(TAG, "onTouch: 层号 ：" + f.getFlies());
                                listFloorID.add(f.getId());
                            }
                        }
                    }


                    dialogAdapter.notifyDataSetChanged();


                    if (!etDetailArea.getText().toString().equals("")) {
                        dialogFloor.show();
                        Log.d(TAG, "onTouch:show ");
                    } else {
                        List<String> list = new ArrayList<>();
                        list.add("请先选择楼号");
                        DialogAdapter dialogAdapter = new DialogAdapter(getActivity(), list, R.layout.simple_list_item);
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

    private void setDialogRoom(final DialogAdapter dialogAdapter) {

        dialogRoom = DialogPlus.newDialog(getActivity())
                .setAdapter(dialogAdapter)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.dialog_head7)
                .setContentWidth(800)
//                .setCancelable(true)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        Log.d("DialogPlus", "onItemClick() called with: " + "item = [" +
                                item + "], position = [" + position + "]");
                        if (position != -1) {
                            etRoom.setText(listRoom.get(position));
//                            roomId = getRoom(listRoom.get(position));
                            roomId = listRoomID.get(position);
                            Log.d(TAG, "onItemClick: " + listRoom.get(position));
                            Log.d(TAG, "onItemClick: " + roomId);
                            dialogRoom.dismiss();
                        }
                    }

                })
                .setExpanded(true, 1000)  // This will enable the expand feature, (similar to android L share dialog)
                .create();

        //给EditText设置点击事件
        etRoom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch: ");
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    listRoom.clear();
                    listRoomID.clear();

                    // FIXME: 2017/3/22 添加相应数据
                    for (Room r : addressRes.getRooms()) {
                        if (etFloor.getText().toString() != null && !etFloor.equals("")) {
                            int Id = getFloor(etFloor.getText().toString());
                            if (Id == r.getFlies()) {
                                listRoom.add(r.getRoomNumber());
                                listRoomID.add(r.getId());
                            }
                        }
                    }

                    dialogAdapter.notifyDataSetChanged();

                    // 提示先选择区域
                    if (!etFloor.getText().toString().equals("")) {
                        dialogRoom.show();
                        Log.d(TAG, "onTouch:show ");
                    } else {
                        List<String> list = new ArrayList<>();
                        list.add("请先选择层号");
                        DialogAdapter dialogAdapter = new DialogAdapter(getActivity(), list, R.layout.simple_list_item);
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
                        Log.d(TAG, "onTouch: warnning show");
                        dialogGetImage.show();
                    }

                }
                return false;
            }
        });

    }

    private void setDialogApplyType(DialogDetailAdapter dialogAdapter) {

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
                        if (position != -1) {
                            etApplyType.setText(listApplyType.get(position));
//                            categoryId = getCategroy(listApplyType.get(position));
                            categoryId = listApplyTypeID.get(position);
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

                    listApplyType.clear();
                    listApplyTypeID.clear();
                    for (Category c : addressRes.getCategory()) {
                        listApplyType.add(c.getC_name());
                        listApplyTypeID.add(c.getC_id());
                    }
                    dialogApplyType.show();
                    Log.d(TAG, "onTouch: dialogDetailArea.show()");
                }
                return false;
            }
        });
    }

    private void setDialogApplyTypeDetails(DialogAdapter dialogAdapter) {

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
            for (Uri u : list_uri) {
                int i = 0;
                arrayUri[i] = u;
                Log.d(TAG, "onResume: " + u.toString());
                i++;
            }

            //判断和赋值
            switchImage();


        } else {

            Log.d("Apply_Fragment", "ApplyFragment没有获取到uri");

        }
        Log.d("MainFragment", "Apply_onResume");
    }

    private void switchImage() {

        switch (list_uri.size() - 1) {
            case 0:
                rl1.setVisibility(View.VISIBLE);
                img_1.setImageBitmap(getSmallBitmap(getRealPathFromURI(list_uri.get(0)), 180, 180));
                break;
            case 1:
                rl1.setVisibility(View.VISIBLE);
                rl2.setVisibility(View.VISIBLE);
                img_1.setImageBitmap(getSmallBitmap(getRealPathFromURI(list_uri.get(0)), 180, 180));
                img_2.setImageBitmap(getSmallBitmap(getRealPathFromURI(list_uri.get(1)), 180, 180));
                break;
            case 2:

                rl1.setVisibility(View.VISIBLE);
                rl2.setVisibility(View.VISIBLE);
                rl3.setVisibility(View.VISIBLE);
                img_1.setImageBitmap(getSmallBitmap(getRealPathFromURI(list_uri.get(0)), 180, 180));
                img_2.setImageBitmap(getSmallBitmap(getRealPathFromURI(list_uri.get(1)), 180, 180));
                img_3.setImageBitmap(getSmallBitmap(getRealPathFromURI(list_uri.get(2)), 180, 180));
                break;


        }
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
        DialogAdapter dialogAdapter = new DialogAdapter(getActivity(), list, R.layout.simple_list_item);
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
                        if (position == 0) {
                            Log.d(TAG, "onItemClick: positon = " + position);
                            startCamera();
                        }
                        if (position == 1) {
                            Log.d(TAG, "onItemClick: positon = " + position);
                            startGallery();
                        }
                        dialogGetImage.dismiss();
                    }

                })
                .setExpanded(true, 1000)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialogGetImage.show();

    }

    public static File fileUri;

    private void startCamera() {
        fileUri = FIleUtils.createImageFile();
//        ContentValues values =new ContentValues();
//        values.put(MediaStore.Images.Media.TITLE,file.getAbsolutePath());
//        photoUri=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

//        MainActivity.list_uri.add(Uri.fromFile(file));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileUri));
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


    private int getAreaID(String areaName) {
        int areaID;
        List<Area> areas = addressRes.getAreas();
        for (Area area : areas) {
            if (area.getArea().equals(areaName)) {
                areaID = area.getId();
                return areaID;
            }
        }
        return -1;
    }

    private int getDetailId(String detailName) {
        int id;
        List<Place> list = addressRes.getPlaces();
        for (Place p : list) {
            if (p.getP_name().equals(detailName)) {
                PlaceId = p.getP_id();
                return PlaceId;
            }
        }
        return -1;
    }

    private int getFloor(String floorName) {
        int id = 0;
        List<Flies> list = addressRes.getFlies();
        for (Flies f : list) {
            if (f.getFlies().equals(floorName)) {
                id = f.getId();
            }
            return id;
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

                if (apply.getArea().equals("0") || apply.getDetailArea().equals("0")) {
                    mhandler.sendEmptyMessage(5);
                    Log.d(TAG, "setApply: AreaId" + apply.getArea());
                    Log.d(TAG, "setApply: DetailAreaId" + apply.getDetailArea());
                    Log.d(TAG, "setApply: fliesId" + apply.getFlies());
                    Log.d(TAG, "setApply: roomId" + apply.getRoom());
                    Log.d(TAG, "setApply: categoryId" + apply.getClasss());
                    return;
                }
                Log.d(TAG, "setApply: AreaId" + apply.getArea());
                Log.d(TAG, "setApply: DetailAreaId" + apply.getDetailArea());
                Log.d(TAG, "setApply: fliesId" + apply.getFlies());
                Log.d(TAG, "setApply: roomId" + apply.getRoom());
                Log.d(TAG, "setApply: categoryId" + apply.getClasss());
                String json = JsonUtil.beanToJson(apply);
                Log.d(TAG, "upApply: json " + json);
                for (Uri u :
                        list_uri) {
                    Log.d(TAG, "upApply: " + u.toString());
                }
                List<File> files = getFiles(list_uri);

                submit(json, files).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(MyApplication.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //clearAll();
                        Toast.makeText(MyApplication.getContext(), response.toString(), Toast.LENGTH_LONG).show();
                        if ("申请成功等待处理".equals(response.toString())){
                            writePhoneToLocal(apply, MyApplication.getContext());
                        }
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


    private void setApply() {
        apply.setArea(String.valueOf(areaId));
        apply.setDetailArea(String.valueOf(placeId));
        apply.setFlies(String.valueOf(flieId));
        apply.setRoom(String.valueOf(roomId));
        apply.setClasss(String.valueOf(categoryId));

    }


    private List<File> getFiles(List<Uri> list_uri) {

        String[] paths = new String[3];
        List<File> files = new ArrayList<>();
        if (list_uri.size() > 0 && list_uri != null) {
            for (int i = 0; i < list_uri.size(); i++) {


                if (list_uri.get(i).toString().split(":")[0].equals("file")) {
                    String s = list_uri.get(i).toString().split("//")[1];
                    paths[i] = s;
                } else {
                    paths[i] = getPath(list_uri.get(i));
                }

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
        etFloor.setText("");
        etRoom.setText("");

//        sp_category.setSelection(0);
//        sp_place.setSelection(0);

        rl1.setVisibility(View.GONE);
        rl2.setVisibility(View.GONE);
        rl3.setVisibility(View.GONE);
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
