package repair.com.repair;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.jakewharton.rxbinding2.view.RxView;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import application.MyApplication;
import camera.CalculateImage;
import camera.FIleUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fragment.ResetVisable;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import model.Apply;
import model.Area;
import model.Category;
import model.DetailClass;
import model.Flies;
import model.Place;
import model.Response;
import model.ResultBean;
import model.Room;
import okhttp3.Call;
import repari.com.adapter.DialogAdapter;
import util.DialogUtil;
import util.JsonUtil;
import util.Util;

import static camera.CalculateImage.getSmallBitmap;
import static constant.RequestUrl.GET_JSON;
import static constant.RequestUrl.JSON_URL;
import static constant.RequestUrl.QUERYMYREPAIR;
import static constant.RequestUrl.UP_APPLY;
import static repair.com.repair.MainActivity.REQUEST_IMAGE;
import static repair.com.repair.MainActivity.TAKE_PHOTO_RAW;
import static repair.com.repair.MainActivity.windowHeigth;
import static repair.com.repair.MainActivity.windowWitch;

/**
 * Created by hsp on 2017/3/14.
 */

public class ChangeActivity extends AppCompatActivity implements View.OnClickListener, ResetVisable {
    private static final String TAG = "changeActivity";
    private static boolean startPic = false;
    private static boolean startCamarea = false;
    private EditText et_name, et_tel, et_describe, et_details;
    //后面添加的电子邮箱，报修密码，报修区域，楼号，报修类型，类型详情
    private EditText etEmail, etApplyPassword, etArea, etDetailArea, etApplyType, etApplyTypeDetails;
    //包含editText 的 LinearLayout
    private LinearLayout llDetailType, llContain, llFloor, llRoom, llDetailArea;
    //记录区域ID
    int AreaId;
    int PlaceId;//楼号ID
    // 添加层号 房间号
    private EditText etFloor, etRoom;

    private List<String> changeImgUrl = new ArrayList<>();
    private List<File> fileList = new ArrayList<>();

    //对话框
    SweetAlertDialog sweetAlertDialog;
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

    private List<ImageView> imageViewList = new ArrayList<>();

    private Apply apply = new Apply();

    public static ResultBean addressRes = null;

    private SVProgressHUD svProgressHUD;


    //用于存放报修区域，报修楼号，报修类型，报修详情的list,放入适配器在对话框显示
    private List<String> listArea = new ArrayList<>();


    private int areaId = 0;
    private int placeId = 0;
    private int flieId = 0;
    private int roomId = 0;
    private int categoryId = 0;
    private int detailTypeID = 0;


    List<Uri> changeUriList = new ArrayList<>();


    //图片文件路径
    List<File> imgFileList;

    private Response response;
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    closeDiag();
                    break;
                case 3:
                    //只有请求到了该记录才展示View;
                    // setInt();
                    closeDiag();
                    initView();
                    setInt();
                    setView();
                    bindView();
                    break;
                case 4:
                    queryFromServer("changeId", changeApply.getId(), QUERYMYREPAIR);
                    break;
                case 5:
                    Toast.makeText(ChangeActivity.this, "请填写报修地址", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    //修改成功
                    sweetAlertDialog.setTitleText("修改成功")
                            .setConfirmText(null)
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    Observable.timer(1,TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            Intent intent = new Intent(ChangeActivity.this, DetailsActivity.class);
                            intent.putExtra("repairId", apply.getId());
                            startActivity(intent);
                        }
                    });

                    break;
                case 7:
                    //服务器有返回 但是不成功
                    sweetAlertDialog.setTitleText("提交失败")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    break;
                case 8:
                    //直接错误了
                    sweetAlertDialog.setTitleText("网络异常")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    break;
            }
        }

    };
    //对话框
    DialogPlus dialogArea;

    DialogPlus dialogGetImage;

    //获取点击修改获得的apply
    Apply changeApply;
    Apply tempApply = new Apply();

    private void setInt() {
        Log.d(TAG, "setInt: " + tempApply.getArea());
        areaId = Integer.parseInt(tempApply.getArea());
        placeId = Integer.parseInt(tempApply.getDetailArea());
        flieId = Integer.parseInt(tempApply.getFlies());
        roomId = Integer.parseInt(tempApply.getRoom());
        categoryId = Integer.parseInt(tempApply.getClasss());
        detailTypeID = Integer.parseInt(tempApply.getDetailClass());

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change2);
        changeApply = (Apply) getIntent().getSerializableExtra("apply");
        addressRes = (ResultBean) getIntent().getSerializableExtra("address");
        svProgressHUD = new SVProgressHUD(this);
        svProgressHUD.showWithStatus("正在加载");
//        sweetAlertDialog = new SweetAlertDialog(this,SweetAlertDialog.NORMAL_TYPE);
        queryFromServerFirst("changeId", changeApply.getId(), QUERYMYREPAIR);

    }

    private void queryFromServerFirst(String changeIdParmas, String applyIds, String urls) {
        Util.submit(changeIdParmas, applyIds, urls)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(ChangeActivity.this, "连接不到服务器,请检查网络", Toast.LENGTH_SHORT).show();
                        mhandler.sendEmptyMessage(2);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Response response1 = JsonUtil.jsonToResponse(response);
                        if (response1 == null || response1.getResultBean() == null || response1.getResultBean().getApplys() == null
                                || response1.getResultBean().getApplys().size() <= 0) {
                            //没有获取到数据
                            mhandler.sendEmptyMessage(2);
                        } else {
                            tempApply = response1.getResultBean().getApplys().get(0);
                            Log.d(TAG, "onResponse: tempApply" + tempApply.getFlies());
                            //获取到了该条数据
                            mhandler.sendEmptyMessage(3);
                        }

                    }
                });
    }

    private void closeDiag() {
        if (svProgressHUD.isShowing()) {
            svProgressHUD.dismiss();
        }
    }

    private void initView() {

        llDetailArea = (LinearLayout) findViewById(R.id.ll_change_detail_area);
        llContain = (LinearLayout) findViewById(R.id.ll_change_contain);
        llFloor = (LinearLayout) findViewById(R.id.ll_change_floor);
        llRoom = (LinearLayout) findViewById(R.id.ll_change_room);
        llDetailType = (LinearLayout) findViewById(R.id.ll_change_detail_type);


        etEmail = (EditText) findViewById(R.id.et_change_email);
        etApplyPassword = (EditText) findViewById(R.id.et_change_apply_password);
        etArea = (EditText) findViewById(R.id.et_change_area);
        etDetailArea = (EditText) findViewById(R.id.et_change_detail_area);
        etApplyType = (EditText) findViewById(R.id.et_change_apply_type);
        etApplyTypeDetails = (EditText) findViewById(R.id.et_change_apply_detail_type);
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
//        btn_apply.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        btn_apply.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn_apply.setBackgroundResource(R.drawable.button_submit2);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn_apply.setBackgroundResource(R.drawable.button_submit);
                }
                return false;
            }
        });
        sweetAlertDialog = new SweetAlertDialog(ChangeActivity.this, SweetAlertDialog.WARNING_TYPE);

        RxView.clicks(btn_apply).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Log.d(TAG, "accept: 提交了");
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog
                        .setTitleText("确认提交？")
                        .setCancelText("取消")
                        .setConfirmText("确定")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                if (check()) {
                                    if(checkValidate()) {

                                        if (Util.isPhoneNumberValid(et_tel.getText().toString())) {
                                            sweetAlertDialog
                                                    .setTitleText("正在提交")
                                                    .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                                            bindView();
                                            upApply();
                                        } else {
                                            sweetAlertDialog.setTitleText("请填写真实电话号码")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            sweetAlertDialog.dismiss();
                                                        }
                                                    });
                                        }
                                    } else{
                                        sweetAlertDialog.setTitleText("不能输入特殊字符")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                        sweetAlertDialog.dismiss();
                                                    }
                                                });
                                    }
                                } else {
                                    sweetAlertDialog.setTitleText("* 标记为必填内容")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    sweetAlertDialog.dismiss();
                                                }
                                            });
                                }
                            }
                        });
                sweetAlertDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);

                if (!ChangeActivity.this.isFinishing() && !sweetAlertDialog.isShowing()) {
                    sweetAlertDialog.show();
                }
            }
        });


        //滚动
        svBackground = (ScrollView) findViewById(R.id.sv_change_apply);

        /**
         * 初始化Dialog
         */
        setDialogAdapter();
        setDialog();
        setEditTextOnTouch();

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
        etApplyTypeDetails.setText(changeApply.getDetailClass());
        et_describe.setText(changeApply.getRepairDetails());
        et_details.setText(changeApply.getAddressDetail());

//        areaId = getAreaID(etArea.getText().toString());
//        placeId = getDetailId(etDetailArea.getText().toString());
//        categoryId = getCategoryID(etApplyType.getText().toString());
//        detailTypeID=getDetailTypeID(etApplyType.getText().toString(),etApplyTypeDetails.getText().toString());

        if(changeApply.getA_imaes()!=null){
            for (String s : changeApply.getA_imaes()) {
                Log.d(TAG, "changApply ImgList  :" +s);
            }
        }
        changeImgUrl = changeApply.getA_imaes();

        //赋值之后 设定可见性
        resetVisible();

        /**
         * 请求area 和 category
         */
        queryFromServer("area", "0", JSON_URL);
        queryFromServer("category", "0", JSON_URL);

        //  把传来的uri变为本地的uri存进list_uri
        new AsyncTask<Void, Void, List<File>>() {
            @Override
            protected List<File> doInBackground(Void... params) {
                imgFileList = new ArrayList<File>();
                File imgFile = null;
                FileOutputStream out = null;
                Log.d(TAG, "doInBackground: changeImageUrl " + changeImgUrl.size());
                if (changeImgUrl != null && changeImgUrl.size() > 0) {
                    for (int i = 0; i < changeImgUrl.size(); i++) {
//                        Bitmap bitmap = imageLoader.loadBitmap(changeImgUrl.get(i), 0, 0);
                        Bitmap bitmap = null;
                        try {
                            bitmap = Picasso.with(ChangeActivity.this).load(changeImgUrl.get(i)).get();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        imgFile = FIleUtils.createImageFile(ChangeActivity.this);
                        try {
//                            Log.d(TAG, "doInBackground: 文件 " + imgFile.toString());
                            out = new FileOutputStream(imgFile);
                            //有图片
                            if (bitmap != null) {
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                imgFileList.add(imgFile);
                            }
                            out.flush();
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
                    Log.d(TAG, "onPostExecute: " + f.getAbsolutePath());
                    fileList.add(f);
                    changeUriList.add(Uri.fromFile(f));
                    Log.d(TAG, "onPostExecute:  -> changeUriList:" + changeUriList.toString());
                }
                rl1.setVisibility(View.GONE);
                rl2.setVisibility(View.GONE);
                rl3.setVisibility(View.GONE);
                switchImage();

            }
        }.execute();

    }


    private void bindView() {

        apply.setRepair(et_name.getText().toString());
        apply.setTel(et_tel.getText().toString());
        apply.setEmail(etEmail.getText().toString());
        apply.setPassword(etApplyPassword.getText().toString());
        setApply();
        apply.setRepairDetails(et_describe.getText().toString());
        String etDescribe = et_describe.getText().toString().replaceAll("\r|\n", "");
        apply.setRepairDetails(etDescribe);
        String addresDetail = et_details.getText().toString().replaceAll("\r|\n", "");
        apply.setAddressDetail(addresDetail);
    }


    /**
     * 存放网络请求的对象list
     */
    private List<Area> newAreaList = new ArrayList<>();
    private List<Place> newPlace = new ArrayList<>();
    private List<Flies> newFlies = new ArrayList<>();
    private List<Room> newRoom = new ArrayList<>();
    private List<Category> newCategory = new ArrayList<>();
    private List<DetailClass> newDetailClass = new ArrayList<>();


    /**
     * 用于适配器的 Stringlist
     */
    List<String> newAreaStringList = new ArrayList<>();
    List<String> newPlaceStringList = new ArrayList<>();
    List<String> newFliesStringList = new ArrayList<>();
    List<String> newRoomStringList = new ArrayList<>();
    List<String> newCategoryStringList = new ArrayList<>();
    List<String> newDetailClassStringList = new ArrayList<>();


    /**
     * 用于对话框的适配器
     */
    DialogAdapter areaAdapter;
    DialogAdapter placeAdapter;
    DialogAdapter fliesAdapter;
    DialogAdapter roomAdapter;
    DialogAdapter categoryAdapter;
    DialogAdapter detailClassAdapter;
    /**
     * 对话框
     */
    DialogPlus areaDialog;
    DialogPlus placeDialog;
    DialogPlus fliesDialog;
    DialogPlus roomDialog;
    DialogPlus categoryDialog;
    DialogPlus detailClassDialog;

    /**
     * logID测试
     */
    private void showId() {
        Log.d(TAG, "showId: 区域id: " + areaId);
        Log.d(TAG, "showId: 楼号id： " + placeId);
        Log.d(TAG, "showId: 楼层id： " + flieId);
        Log.d(TAG, "showId: 房间id： " + roomId);
        Log.d(TAG, "showId: 类型id:  " + categoryId);
        Log.d(TAG, "showId: 类详id： " + detailTypeID);
    }


    /**
     * 设置适配器
     * 需要的list是全局的stringlist
     */
    private void setDialogAdapter() {
        areaAdapter = new DialogAdapter(this, newAreaStringList, R.layout.simple_list_item);
        placeAdapter = new DialogAdapter(this, newPlaceStringList, R.layout.simple_list_item);
        fliesAdapter = new DialogAdapter(this, newFliesStringList, R.layout.simple_list_item);
        roomAdapter = new DialogAdapter(this, newRoomStringList, R.layout.simple_list_item);
        categoryAdapter = new DialogAdapter(this, newCategoryStringList, R.layout.simple_list_item);
        detailClassAdapter = new DialogAdapter(this, newDetailClassStringList, R.layout.simple_list_item);
    }


    private void setDialog() {
        /**
         * 设置区域Dialog
         */
        areaDialog = DialogUtil.getDialogBuilder(this, areaAdapter, R.layout.dialog_head1, this)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        if (position != -1) {
                            areaId = newAreaList.get(position).getId();
                            showId();
                            etArea.setText(newAreaList.get(position).getArea());
                            dialog.dismiss();
                        }
                    }
                })
                .create();
        /**
         * 楼号
         */
        placeDialog = DialogUtil.getDialogBuilder(this, placeAdapter, R.layout.dialog_head2, this)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        if (position != -1) {
                            placeId = newPlace.get(position).getP_id();
                            showId();
                            etDetailArea.setText(newPlace.get(position).getP_name());
                            dialog.dismiss();
                        }
                    }
                })
                .create();
        /**
         * 楼层
         */
        fliesDialog = DialogUtil.getDialogBuilder(this, fliesAdapter, R.layout.dialog_head6, this)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        if (position != -1) {
                            flieId = newFlies.get(position).getId();
                            showId();
                            etFloor.setText(newFlies.get(position).getFlies());
                            dialog.dismiss();
                        }
                    }
                })
                .create();
        /**
         * 房间
         */
        roomDialog = DialogUtil.getDialogBuilder(this, roomAdapter, R.layout.dialog_head7, this)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        if (position != -1) {
                            roomId = newRoom.get(position).getId();
                            showId();
                            etRoom.setText(newRoom.get(position).getRoomNumber());
                            dialog.dismiss();
                        }
                    }
                })
                .create();
        /**
         * 类型
         */
        categoryDialog = DialogUtil.getDialogBuilder(this, categoryAdapter, R.layout.dialog_head3, this)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        if (position != -1) {
                            categoryId = newCategory.get(position).getC_id();
                            showId();
                            etApplyType.setText(newCategory.get(position).getC_name());
                            dialog.dismiss();
                        }
                    }
                })
                .create();
        /**
         * 详细类型
         */
        detailClassDialog = DialogUtil.getDialogBuilder(this, detailClassAdapter, R.layout.dialog_head10, this)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        if (position != -1) {
                            detailTypeID = newDetailClass.get(position).getId();
                            showId();
                            etApplyTypeDetails.setText(newDetailClass.get(position).getClassDetail());
                            dialog.dismiss();
                        }
                    }
                })
                .create();

    }

    private void setEditTextOnTouch() {

        /**
         * 区域edittext touch事件 点击清空下一级可见性
         */
        etArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    setNextVisible(v, event);
                    areaDialog.show();
                }
                return false;
            }
        });

        /**
         * 楼号
         */
        etDetailArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d(TAG, "onTouch: plac" + areaId);
                    queryFromServer("place", String.valueOf(areaId), JSON_URL);
                    Log.d(TAG, "onTouch: " + String.valueOf(areaId));
                    setNextVisible(v, event);
                    placeDialog.show();
                }
                return false;
            }
        });
        /**
         * 层号
         */
        etFloor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    queryFromServer("flies", String.valueOf(placeId), JSON_URL);
                    Log.d(TAG, "onTouch: " + String.valueOf(placeId));
                    setNextVisible(v, event);
                    fliesDialog.show();
                }
                return false;
            }
        });
        /**
         * 房间
         */
        etRoom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    queryFromServer("room", String.valueOf(flieId), JSON_URL);
                    Log.d(TAG, "onTouch: " + String.valueOf(flieId));
                    setNextVisible(v, event);
                    roomDialog.show();
                }
                return false;
            }
        });
        /**
         * 类型
         * 不用请求网络
         */
        etApplyType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    setNextVisible(v, event);
                    categoryDialog.show();
                }
                return false;
            }
        });
        /**
         * 详细类型
         */
        etApplyTypeDetails.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    queryFromServer("detailClass", String.valueOf(categoryId), JSON_URL);
                    setNextVisible(v, event);
                    detailClassDialog.show();
                }
                return false;
            }
        });


    }

    private void setNextVisible(View view, MotionEvent event) {
        switch (view.getId()) {
            case R.id.et_change_area:
                //点击区域清空 楼号 层号 房间
                etDetailArea.setText("");
                etFloor.setText("");
                etRoom.setText("");
                //清空下一级可见
                llDetailArea.setVisibility(View.INVISIBLE);
                llContain.setVisibility(View.GONE);
                llFloor.setVisibility(View.INVISIBLE);
                llRoom.setVisibility(View.INVISIBLE);
                //清空存放好的Id
                placeId = 0;
                flieId = 0;
                roomId = 0;
                break;
            case R.id.et_change_detail_area:
                //点击区域清空  层号 房间
                etFloor.setText("");
                etRoom.setText("");
                //清空下一级可见
                llContain.setVisibility(View.GONE);
                llFloor.setVisibility(View.INVISIBLE);
                llRoom.setVisibility(View.INVISIBLE);
                //清空存放好的Id
                flieId = 0;
                roomId = 0;
                break;
            case R.id.et_change_floor:
                //点击区域清空   房间
                etRoom.setText("");
                //清空下一级可见
                llRoom.setVisibility(View.INVISIBLE);
                //清空存放好的Id
                roomId = 0;
                break;
            case R.id.et_apply_type:
                //清空详细类型
                etApplyTypeDetails.setText("");
                llDetailArea.setVisibility(View.INVISIBLE);
                detailTypeID = 0;
                break;
        }
    }


    /**
     * 获取stringlist 在edittext.ontouch事件里面 或者 dialog.itemonclick里面调用
     * 改变全局的stringlist 并且更新适配器
     *
     * @param parmsName
     */
    private void changeStringList(String parmsName) {

        Log.d(TAG, "changeStringList: 参数名 ： " + parmsName);
        switch (parmsName) {
            case "area": {
                newAreaList.clear();
                newAreaStringList.clear();
                newAreaList.addAll(response.getResultBean().getAreas());
                Area.Comparator1 c = new Area.Comparator1();
                Collections.sort(newAreaList, c);
                for (Area a : newAreaList) {
                    newAreaStringList.add(a.getArea());
                }
                areaAdapter.notifyDataSetChanged();
                break;
            }

            case "place": {
                newPlace.clear();
                newPlaceStringList.clear();
                newPlace.addAll(response.getResultBean().getPlaces());
                Place.ComparatorPlace c = new Place.ComparatorPlace();
                Collections.sort(newPlace, c);
                for (Place p : newPlace) {
                    newPlaceStringList.add(p.getP_name());
                }

                placeAdapter.notifyDataSetChanged();
                break;
            }
            case "flies": {
                newFlies.clear();
                newFliesStringList.clear();
                newFlies.addAll(response.getResultBean().getFlies());
                Flies.ComparatorFlies flies = new Flies.ComparatorFlies();
                Collections.sort(newFlies, flies);

                for (Flies f : newFlies) {
                    newFliesStringList.add(f.getFlies());
                }
                fliesAdapter.notifyDataSetChanged();
                break;
            }
            case "room": {
                newRoom.clear();
                newRoomStringList.clear();
                newRoom.addAll(response.getResultBean().getRooms());
                Room.ComparatorRoom ro = new Room.ComparatorRoom();
                Collections.sort(newRoom, ro);
                for (Room r : newRoom) {
                    newRoomStringList.add(r.getRoomNumber());
                }
                roomAdapter.notifyDataSetChanged();
                break;
            }
            case "category": {
                newCategory.clear();
                newCategoryStringList.clear();
                newCategory.addAll(response.getResultBean().getCategory());

                Category.ComparatorCategory ca = new Category.ComparatorCategory();
                Collections.sort(newCategory, ca);

                for (Category c :
                        newCategory) {
                    newCategoryStringList.add(c.getC_name());
                }
                categoryAdapter.notifyDataSetChanged();
                break;
            }
            case "detailClass": {
                newDetailClass.clear();
                newDetailClassStringList.clear();
                newDetailClass.addAll(response.getResultBean().getDetailClasses());

                DetailClass.ComparatorDetail deta = new DetailClass.ComparatorDetail();
                Collections.sort(newDetailClass, deta);
                for (DetailClass d : newDetailClass) {
                    newDetailClassStringList.add(d.getClassDetail());
                }
                detailClassAdapter.notifyDataSetChanged();
                break;
            }
        }

    }


    private Response queryFromServer(final String parmsName, String parmsVules, String url) {
        Log.d(TAG, "queryFromServer: ->调用了");
        Util.submit(parmsName, parmsVules, url)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        response = new Response();
                        response.setEnd(true);
                        response.setErrorMessage("连接服务器失败,请检查网络");
                        response.setErrorType(-2);
                    }

                    @Override
                    public void onResponse(String responses, int id) {
                        final String responseJson = responses.toString();
                        Log.d(TAG, "onResponse -> queryArea: " + responseJson);
                        ;
                        response = JsonUtil.jsonToResponse(responseJson);
                        if (response.getErrorType() == -1) {
                            //服务器读取数据错误
                            response.setErrorMessage("服务器维护");
                        } else {
                            changeStringList(parmsName);
                        }
                    }

                });
        return response;
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        if (startCamarea || startPic) {

        } else {
            if (imgFileList != null && imgFileList.size() > 0) {
                for (File file : imgFileList) {
                    String tempFilePath = file.getAbsolutePath();
                    Util.deleteImage((MyApplication.getContext()), tempFilePath);
                }
            }

        }


        super.onStop();

    }

    @Override
    public void onDestroy() {
        if(sweetAlertDialog!=null){
            sweetAlertDialog.dismiss();
        }
        changeUriList.clear();

        super.onDestroy();


    }

    @Override
    public void onResume() {
        super.onResume();

        startPic = false;
        startCamarea = false;

        if (changeUriList.size() > 0) {
            Log.d(TAG, "onResume -> changeUriList=" + changeUriList.size());
            if (changeUriList.size() > 3) {
                int length = changeUriList.size() - 3;
                for (int i = 0; i < length; i++) {
                    changeUriList.remove(i);
                }
            }

            //判断和赋值
            switchImage();

        } else {
            Log.d(TAG, "onResume -> 没有获取到uri ,changeUriList的size=" + changeUriList.size());
        }

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
            case R.id.iv_change_add:

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
                .setContentWidth((int) (windowWitch / 1.5))
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
                .setExpanded(true, (int) (windowHeigth / 1.5))  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialogGetImage.show();

    }

    public static File cameraFile;

    private void startCamera() {
        startCamarea = true;
        cameraFile = FIleUtils.createImageFile(this);

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Uri photoUri = FileProvider.getUriForFile(
                this,
                getPackageName(),
                cameraFile);
      //  takePhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(takePhotoIntent, TAKE_PHOTO_RAW);
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
        Log.d(TAG, "getRealPathFromURI: " + result);
        return result;

    }


    private void startGallery() {
        startPic = true;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        this.startActivityForResult(intent, REQUEST_IMAGE);
    }


    private void upApply() {


        String json = JsonUtil.beanToJson(apply);
        Log.d(TAG, "upApply: json " + json);
        for (Uri u :
                changeUriList) {
            Log.d(TAG, "upApply: " + u.toString());
        }
        List<File> files = getFiles(changeUriList);

        Util.submit("update", json, GET_JSON, UP_APPLY, files,this)
                .connTimeOut(20000)
                .readTimeOut(20000)
                .writeTimeOut(20000)
                .execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mhandler.sendEmptyMessage(8);
            }

            @Override
            public void onResponse(String response, int id) {
                //clearAll();
                Log.d(TAG, "onResponse: " + response);
                if ("UpdateOK".equals(response)) {
                    mhandler.sendEmptyMessage(6);
                } else {
                    mhandler.sendEmptyMessage(7);
                }

            }
        });
    }

    private void setApply() {
        apply.setArea(String.valueOf(areaId));
        Log.d(TAG, "setApply: areaID" + areaId);
        apply.setDetailArea(String.valueOf(placeId));
        apply.setFlies(String.valueOf(flieId));
        apply.setRoom(String.valueOf(roomId));
        apply.setClasss(String.valueOf(categoryId));
        apply.setDetailClass(String.valueOf(detailTypeID));
        apply.setId(changeApply.getId());

    }


    private List<File> getFiles(List<Uri> list_uri) {

        String[] paths = new String[3];
        List<File> files = new ArrayList<>();
        if (list_uri.size() > 0 && list_uri != null) {
            for (int i = 0; i < list_uri.size(); i++) {
                Log.d(TAG, "getFiles: list_uri:" + list_uri.get(i).toString());
                if (list_uri.get(i).toString().split(":")[0].equals("file")) {
                    String s = list_uri.get(i).toString().split("//")[1];
                    Log.d(TAG, "getFiles: 修改后" + s);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Apply_Activity", " resultCode=" + RESULT_OK + "  requestCode=" + requestCode);
        if (resultCode == RESULT_OK && requestCode == TAKE_PHOTO_RAW) {
            Log.d(TAG, "onActivityResult: " + resultCode);
            Log.d(TAG, "onActivityResult: " + cameraFile);
            changeUriList.add(Uri.fromFile(cameraFile));

            Log.d(TAG, "onActivityResult: changeUriList的长度:" + changeUriList.toString());


        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE) {

            changeUriList.add(data.getData());

            Log.d(TAG, "onActivityResult: changeUriList的长度:" + changeUriList.toString());

            Log.d(TAG, "从相册返回");
            Log.i(TAG, "GalleryUri:    " + data.getData().getPath());
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if(sweetAlertDialog!=null){
                sweetAlertDialog.dismiss();
            }

            if (llBigImg.getVisibility() == View.VISIBLE) {
                llBigImg.setVisibility(View.GONE);
            } else {
                this.finish();
            }
        }


//        return super.onKeyDown(keyCode,event);
        return true;
    }


    @Override
    public void resetVisible() {

        //进来就全部不可见
        llDetailArea.setVisibility(View.INVISIBLE);
        llContain.setVisibility(View.GONE);
        llFloor.setVisibility(View.INVISIBLE);
        llRoom.setVisibility(View.INVISIBLE);
        llDetailType.setVisibility(View.INVISIBLE);


        //如果区域不为其他 楼号可见
        if (!etArea.getText().toString().equals("其它") && !etArea.getText().toString().equals("")) {
            Log.d(TAG, "resetVisible: " + etArea.getText());
            llDetailArea.setVisibility(View.VISIBLE);
        }
        if (!etDetailArea.getText().toString().equals("其它") && !etDetailArea.getText().toString().equals("")) {
            Log.d(TAG, "resetVisible: " + etDetailArea.getText());
            llContain.setVisibility(View.VISIBLE);
            llFloor.setVisibility(View.VISIBLE);
        }
        if (!etFloor.getText().toString().equals("其它") && !etFloor.getText().toString().equals("")) {
            Log.d(TAG, "resetVisible: " + etFloor.getText());
            llRoom.setVisibility(View.VISIBLE);
        }
        if (!etApplyType.getText().toString().equals("其它") && !etApplyType.getText().toString().equals("")) {
            Log.d(TAG, "resetVisible: " + etApplyType.getText());
            llDetailType.setVisibility(View.VISIBLE);
        }
    }


    private boolean check() {
        Log.d(TAG, "check: " + (getContent(et_tel)
                && getContent(et_name)
                && getContent(etApplyPassword)
                && getContent(etArea)
                && getContent(etApplyType)));

        return getContent(et_tel)
                && getContent(et_name)
                && getContent(etApplyPassword)
                && getContent(etArea)
                && getContent(etApplyType);
    }




    private boolean checkValidate(){
        boolean ok=Util.validateString(et_name.getText().toString())&&
                Util.validateString(et_details.getText().toString())&&
                Util.validateString(et_describe.getText().toString());
        return ok;
    }

    private boolean getContent(EditText e) {
        Log.d(TAG, "check getContent: " + e.getText().toString().equals(""));
        return !e.getText().toString().equals("");
    }


}
