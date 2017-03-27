package repair.com.repair;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import application.MyApplication;
import camera.CalculateImage;
import camera.FIleUtils;
import imagehodler.ImageLoader;
import model.Apply;
import model.Area;
import model.Category;
import model.Flies;
import model.Place;
import model.Response;
import model.ResultBean;
import model.Room;
import okhttp3.Call;
import repari.com.adapter.DialogAdapter;
import repari.com.adapter.DialogDetailAdapter;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.JsonUtil;
import util.Util;

import static camera.CalculateImage.getSmallBitmap;
import static repair.com.repair.MainActivity.GET_JSON;
import static repair.com.repair.MainActivity.JSON_URL;
import static repair.com.repair.MainActivity.REQUEST_IMAGE;
import static repair.com.repair.MainActivity.TAKE_PHOTO_RAW;
import static repair.com.repair.MainActivity.UP_APPLY;
import static util.NetworkUtils.isNetworkConnected;

/**
 * Created by hsp on 2017/3/14.
 */

public class ChangeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "changeActivity";

    private EditText et_name, et_tel, et_describe, et_details;
    //后面添加的电子邮箱，报修密码，报修区域，楼号，报修类型，类型详情
    private EditText etEmail, etApplyPassword, etArea, etDetailArea, etApplyType, etApplyTypeDetails;
    //记录区域ID
    int AreaId;
    int PlaceId;//楼号ID
    int fliesId;//层号ID
    // 添加层号 房间号
    private EditText etFloor, etRoom;

    private List<String> changeImgUrl = new ArrayList<>();

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


    List<Uri> changeUriList = new ArrayList<>();

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
                    Toast.makeText(ChangeActivity.this, "请填写报修地址", Toast.LENGTH_SHORT).show();
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

    //获取点击修改获得的apply
    Apply changeApply;

    ImageLoader imageLoader;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change2);
        changeApply = (Apply) getIntent().getSerializableExtra("apply");
        addressRes = (ResultBean) getIntent().getSerializableExtra("address");
        if (addressRes == null) {
            String addressJson = Util.loadAddressFromLocal(ChangeActivity.this);
            addressRes = JsonUtil.jsonToBean(addressJson);
        }

        imageLoader = ImageLoader.build(this);


        initView();
        setView();
        bindView();
        setDialogView(addressRes);
    }

    private void initView() {

        etEmail = (EditText) findViewById(R.id.et_change_email);
        etApplyPassword = (EditText) findViewById(R.id.et_change_apply_password);
        etArea = (EditText) findViewById(R.id.et_change_area);
        etDetailArea = (EditText) findViewById(R.id.et_change_detail_area);
        etApplyType = (EditText) findViewById(R.id.et_change_apply_type);
        et_name = (EditText) findViewById(R.id.et_change_name);

        etFloor = (EditText) findViewById(R.id.et_change_floor);
        etRoom = (EditText) findViewById(R.id.et_change_room);

        et_tel = (EditText) findViewById(R.id.et_change_tel);

        et_describe = (EditText) findViewById(R.id.et_change_apply_describe);
        et_details = (EditText) findViewById(R.id.et_change_apply_details);

        img_add = (ImageView) findViewById(R.id.iv_change_add);
        img_add.setOnClickListener(this);

        img_1 = (ImageView) findViewById(R.id.iv_change_img1);
        img_2 = (ImageView) findViewById(R.id.iv_change_img2);
        img_3 = (ImageView) findViewById(R.id.iv_change_img3);

        //包裹三张图片的RelativeLayout
        rl1 = (RelativeLayout) findViewById(R.id.rl_change_img1);
        rl2 = (RelativeLayout) findViewById(R.id.rl_change_img2);
        rl3 = (RelativeLayout) findViewById(R.id.rl_change_img3);
        //打叉图片
        ivX1 = (ImageView) findViewById(R.id.iv_change_img_x1);
        ivX2 = (ImageView) findViewById(R.id.iv_change_img_x2);
        ivX3 = (ImageView) findViewById(R.id.iv_change_img_x3);
        //设置打叉点击事件
        XOnclick();
        //大图
        llBigImg = (LinearLayout) findViewById(R.id.ll_change_big_img);
        ivBigImg = (ImageView) findViewById(R.id.iv_change_big_img);

        //设置图片点击事件
        imgOnclick();

        imageViewList.add(img_1);
        imageViewList.add(img_2);
        imageViewList.add(img_3);
        btn_apply = (Button) findViewById(R.id.btn_change_apply);
        btn_clear = (Button) findViewById(R.id.btn_change_clear);
        btn_apply.setOnClickListener(this);
        btn_clear.setOnClickListener(this);

        //滚动
        svBackground = (ScrollView) findViewById(R.id.sv_change_apply);

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
                changeUriList.remove(0);
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
                changeUriList.remove(1);
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
                changeUriList.remove(2);
                switchImage();
            }
        });
    }

    private void setView() {
        et_name.setText(changeApply.getRepair());
        et_tel.setText(changeApply.getTel());
        etEmail.setText(changeApply.getEmail());
        etApplyPassword.setText(changeApply.getPassword());
        etArea.setText(changeApply.getArea());
        etDetailArea.setText(changeApply.getDetailArea());
        etFloor.setText(changeApply.getFlies());
        etRoom.setText(changeApply.getRoom());
        etApplyType.setText(changeApply.getClasss());
        et_describe.setText(changeApply.getRepairDetails());
        areaId = getAreaID(etArea.getText().toString());
        placeId = getDetailId(etDetailArea.getText().toString());
        categoryId = getCategoryID(etApplyType.getText().toString());
        changeImgUrl = changeApply.getA_imaes();

        for (String s :
                changeImgUrl) {
            Log.d(TAG, "setView: " + s.toString());
        }

        //  把传来的uri变为本地的uri存进list_uri
        new AsyncTask<Void, Void, List<File>>() {
            @Override
            protected List<File> doInBackground(Void... params) {
                List<File>  imgFileList = new ArrayList<File>();
                File imgFile = null;
                FileOutputStream out = null;
                if (changeImgUrl != null) {
                    for (int i = 0; i < changeImgUrl.size(); i++) {
                        Bitmap bitmap = imageLoader.loadBitmap(changeImgUrl.get(i), 0, 0);
                        imgFile = FIleUtils.createImageFile();
                        try {
//                            Log.d(TAG, "doInBackground: 文件 " + imgFile.toString());
                            out = new FileOutputStream(imgFile);
                            //有图片
                            if(bitmap!=null){
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                imgFileList.add(imgFile);
                            }
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
                return imgFileList;
            }

            @Override
            protected void onPostExecute(List<File> imgFileList) {

                for (File f : imgFileList) {
                    changeUriList.add(Uri.fromFile(f));
                }
                rl1.setVisibility(View.GONE);
                rl2.setVisibility(View.GONE);
                rl3.setVisibility(View.GONE);
                switchImage();

            }
        }.execute();
//        copyBitmapToUrl();

    }


    private void copyBitmapToUrl() {
        if (changeImgUrl != null) {
            for (int i = 0; i < changeImgUrl.size(); i++) {
                Bitmap bitmap = imageLoader.loadBitmap(changeImgUrl.get(i), 0, 0);
                File imgFile = FIleUtils.createImageFile();
                try {
                    FileOutputStream out = new FileOutputStream(imgFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    changeUriList.add(Uri.fromFile(imgFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void bindView() {

        apply.setRepair(et_name.getText().toString());
        apply.setTel(et_tel.getText().toString());
        apply.setEmail(etEmail.getText().toString());
        apply.setPassword(etApplyPassword.getText().toString());

        setApply();
        apply.setRepairDetails(et_describe.getText().toString());
    }

    private void setDialogView(ResultBean resultBean) {
        if (resultBean != null) {


            DialogAdapter areaAdapter = new DialogAdapter(this, listArea, R.layout.simple_list_item);
            DialogAdapter detailAreaAdapter = new DialogAdapter(this, listDetailArea, R.layout.simple_list_item);
            DialogAdapter floorAdapter = new DialogAdapter(this, listFloor, R.layout.simple_list_item);
            DialogAdapter roomAdapter = new DialogAdapter(this, listRoom, R.layout.simple_list_item);
            DialogDetailAdapter applyTypeAdapter = new DialogDetailAdapter(this, listApplyType, resultBean.getCategory(), R.layout.dialog_detail_type);


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
        dialogArea = DialogPlus.newDialog(this)
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

        dialogDetailArea = DialogPlus.newDialog(this)
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
                        DialogAdapter dialogAdapter = new DialogAdapter(ChangeActivity.this, list, R.layout.simple_list_item);
                        dialogGetImage = DialogPlus.newDialog(ChangeActivity.this)
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

        dialogFloor = DialogPlus.newDialog(this)
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
                        DialogAdapter dialogAdapter = new DialogAdapter(ChangeActivity.this, list, R.layout.simple_list_item);
                        dialogGetImage = DialogPlus.newDialog(ChangeActivity.this)
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

        dialogRoom = DialogPlus.newDialog(this)
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
                        DialogAdapter dialogAdapter = new DialogAdapter(ChangeActivity.this, list, R.layout.simple_list_item);
                        dialogGetImage = DialogPlus.newDialog(ChangeActivity.this)
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

        dialogApplyType = DialogPlus.newDialog(ChangeActivity.this)
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

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        changeUriList.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (changeUriList.size() > 0) {
            Log.d("Apply_Fragment", "ApplyFragment已经获得了uri");
            if (changeUriList.size() > 3) {
                int length = changeUriList.size() - 3;
                for (int i = 0; i < length; i++) {
                    changeUriList.remove(i);
                }
            }

            //判断和赋值
            switchImage();


        } else {
            Log.d("Apply_Fragment", "ApplyFragment没有获取到uri");
        }
        Log.d("MainFragment", "Apply_onResume");
    }

    private void switchImage() {

        switch (changeUriList.size() - 1) {
            case 0:
                rl1.setVisibility(View.VISIBLE);
                img_1.setImageBitmap(getSmallBitmap(getRealPathFromURI(changeUriList.get(0)), 180, 180));
                break;
            case 1:
                rl1.setVisibility(View.VISIBLE);
                rl2.setVisibility(View.VISIBLE);
                img_1.setImageBitmap(getSmallBitmap(getRealPathFromURI(changeUriList.get(0)), 180, 180));
                img_2.setImageBitmap(getSmallBitmap(getRealPathFromURI(changeUriList.get(1)), 180, 180));
                break;
            case 2:

                rl1.setVisibility(View.VISIBLE);
                rl2.setVisibility(View.VISIBLE);
                rl3.setVisibility(View.VISIBLE);
                img_1.setImageBitmap(getSmallBitmap(getRealPathFromURI(changeUriList.get(0)), 180, 180));
                img_2.setImageBitmap(getSmallBitmap(getRealPathFromURI(changeUriList.get(1)), 180, 180));
                img_3.setImageBitmap(getSmallBitmap(getRealPathFromURI(changeUriList.get(2)), 180, 180));
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
            case R.id.btn_change_apply:

                bindView();
                upApply();
                break;

            case R.id.btn_change_clear:

                clearAll();

                break;

//            case R.id.img_camera:
//                startCamera();
//                break;

            case R.id.iv_change_add:
//                startGallery();
                addPic();
                Log.d(TAG, "onClick: 点击了加号");
                break;
        }

    }

    private void addPic() {
        List<String> list = new ArrayList<>();
        list.add("打开相机");
        list.add("选择本地图片");
        DialogAdapter dialogAdapter = new DialogAdapter(this, list, R.layout.simple_list_item);
        dialogGetImage = DialogPlus.newDialog(this)
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

    public static File cameraFile;

    private void startCamera() {
        cameraFile = FIleUtils.createImageFile();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            this.startActivityForResult(intent, TAKE_PHOTO_RAW);
        }

    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = this.getContentResolver().query(contentURI, null, null, null, null);
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

    private int getCategoryID(String categoryName) {
        int categoryID;
        List<Category> categoryRes = addressRes.getCategory();
        for (Category c : categoryRes) {
            if (c.getC_name().equals(categoryName)) {
                categoryID = c.getC_id();
                return categoryID;
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

        this.startActivityForResult(intent, REQUEST_IMAGE);
    }


    private void upApply() {


        if (!isNetworkConnected(this)) {
            Toast.makeText(this, "请连接网络", Toast.LENGTH_SHORT).show();

        } else {

            if (true) {

//                if (etArea.getText().toString().equals("") || etArea.getText().toString().equals("")) {
//                    mhandler.sendEmptyMessage(5);
//                    Log.d(TAG, "setApply: AreaId" + apply.getArea());
//                    Log.d(TAG, "setApply: DetailAreaId" + apply.getDetailArea());
//                    Log.d(TAG, "setApply: fliesId" + apply.getFlies());
//                    Log.d(TAG, "setApply: roomId" + apply.getRoom());
//                    Log.d(TAG, "setApply: categoryId" + apply.getClasss());
//                    return;
//                }
                Log.d(TAG, "setApply: AreaId" + apply.getArea());
                Log.d(TAG, "setApply: DetailAreaId" + apply.getDetailArea());
                Log.d(TAG, "setApply: fliesId" + apply.getFlies());
                Log.d(TAG, "setApply: roomId" + apply.getRoom());
                Log.d(TAG, "setApply: categoryId" + apply.getClasss());
                String json = JsonUtil.beanToJson(apply);
                Log.d(TAG, "upApply: json " + json);
                for (Uri u :
                        changeUriList) {
                    Log.d(TAG, "upApply: " + u.toString());
                }
                List<File> files = getFiles(changeUriList);

                submit(json, files).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(MyApplication.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //clearAll();
                        Log.d(TAG, "onResponse: "+response.toString());
                        Toast.makeText(MyApplication.getContext(), response.toString(), Toast.LENGTH_LONG).show();
                        writePhoneToLocal(apply, MyApplication.getContext());

                        Intent intent= new Intent(ChangeActivity.this,DetailsActivity.class);
                        intent.putExtra("repairId",apply.getId());
                        Log.d(TAG, "onResponse: "+apply.getId());
                        startActivity(intent);
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
        apply.setId(changeApply.getId());

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
        Cursor cursor = this.managedQuery(uri, proj, null, null, null);
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

        String getNewPath = this.getExternalCacheDir()
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
        changeUriList.clear();
    }

    private RequestCall submit(String json, List<File> files) {
        PostFormBuilder postFormBuilder = OkHttpUtils.post();
        for (int i = 0; i < files.size(); i++) {
            postFormBuilder.addFile("file", "file" + i + ".jpg", files.get(i));
            Log.d(TAG, "submit: " + files.get(i).getPath());
        }

        Log.d(TAG, "submit: json添加参数");
        postFormBuilder.addParams("update", json);
        if (files.size() > 0) {
            postFormBuilder.url(UP_APPLY);
        } else {
            postFormBuilder.url(GET_JSON);
        }

        return postFormBuilder.build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Apply_Activity", " resultCode=" + RESULT_OK + "  requestCode=" + requestCode);
        if (resultCode == RESULT_OK && requestCode == TAKE_PHOTO_RAW) {
            Log.d(TAG, "onActivityResult: " + resultCode);
            Log.d(TAG, "onActivityResult: " + cameraFile);
            changeUriList.add(Uri.fromFile(cameraFile));
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE) {
            changeUriList.add(data.getData());
            Log.d(TAG, "addItem");
            Log.i("Apply_Activity", "GalleryUri:    " + data.getData().getPath());
        }
    }

}
