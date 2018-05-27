package fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bm.library.PhotoView;
import com.jakewharton.rxbinding2.view.RxView;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.zhangym.customview.VerificationCodeView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import application.MyApplication;
import camera.FIleUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;
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
import network.Api;
import okhttp3.Call;
import repair.com.repair.MainActivity;
import repair.com.repair.R;
import repari.com.adapter.DialogAdapter;
import util.AESUtil;
import util.DialogUtil;
import util.EdiTTouch;
import util.JsonUtil;
import util.PermissionUtil;
import util.RxBindingUtil;
import util.Util;

import static camera.CalculateImage.getSmallBitmap;

import static constant.RequestUrl.AdressAreaList;
import static constant.RequestUrl.AdressFliesList;
import static constant.RequestUrl.AdressPlaceList;
import static constant.RequestUrl.AdressroomList;



import static constant.RequestUrl.TypeCategoryList;
import static constant.RequestUrl.TypeDetailClassList;

import static repair.com.repair.MainActivity.REQUEST_IMAGE;
import static repair.com.repair.MainActivity.TAKE_PHOTO_RAW;
import static repair.com.repair.MainActivity.list_uri;
import static repair.com.repair.MainActivity.windowHeigth;
import static repair.com.repair.MainActivity.windowWitch;


public class ApplyFragment extends LazyFragment2 implements View.OnClickListener, GetFragment, EdiTTouch, ResetVisable {

    private static final String TAG = "ApplyFragment";


    private EditText et_name, et_tel, et_describe, etCode;

    //后面添加的电子邮箱，报修密码，报修区域，楼号，报修类型，类型详情
    private EditText etEmail, etApplyPassword, etArea, etDetailArea, etApplyType, etApplyTypeDetails, etAddressDetail;
    //对话框点击显示下一级
    private LinearLayout llApplyDetailArea, llApplyFloor, llApplyRoom, llApplyDetailType;

    private LinearLayout llPhoneLine, llNameLine, llPasswordLine, llEmailLine, llAreaLine, llPlaceLine, llFliesLine;

    private LinearLayout llRoonLine, llAddressDetaiLine, llClassLine, llDetailClassLine, llDesctribeLine;


    private ImageView imgPhone, imgName, imgPassword, imgEmail, imgAddressDetail;


    private EditText etFloor, etRoom;

    public SweetAlertDialog sweetAlertDialog;


    private List<Area> newAreaList = new ArrayList<>();
    private List<Place> newPlace = new ArrayList<>();
    private List<Flies> newFlies = new ArrayList<>();
    private List<Room> newRoom = new ArrayList<>();
    private List<Category> newCategory = new ArrayList<>();
    private List<DetailClass> newDetailClass = new ArrayList<>();


    private Button btn_apply;
    private ImageView img_add, img_1, img_2, img_3;

    private RelativeLayout rl1, rl2, rl3;

    private VerificationCodeView verificationCodeView;

    //打叉图片
    private ImageView ivX1, ivX2, ivX3;
    //显示大图
    private LinearLayout llBigImg;
    private PhotoView ivBigImg;


    private List<ImageView> imageViewList = new ArrayList<>();

    private Apply apply = new Apply();

    public static ResultBean addressRes = null;

    private int areaId = 0;
    private int placeId = 0;
    private int flieId = 0;
    private int roomId = 0;
    private int categoryId = 0;
    private int detailTypeID = 0;


    /**
     * 用于适配器的 Stringlist
     */
    List<String> newAreaStringList = new ArrayList<>();
    List<String> newPlaceStringList = new ArrayList<>();
    List<String> newFliesStringList = new ArrayList<>();
    List<String> newRoomStringList = new ArrayList<>();
    List<String> newCategoryStringList = new ArrayList<>();
    List<String> newDetailClassStringList = new ArrayList<>();


    Uri[] arrayUri = new Uri[3];

    private Response response;
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case 4:

                    String addressJson = Util.loadAddressFromLocal(MyApplication.getContext());
                    addressRes = JsonUtil.jsonToBean(addressJson);
                    break;

            }
        }
    };
    DialogPlus dialogGetImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayout() {
        return R.layout.weixin_apply;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {

        if (isVisible) {
            loadData();
        }
    }

    private void loadData() {
        queryFromServer("area", "0", AdressAreaList);
        queryFromServer("category", "0", TypeCategoryList);
        Api.changeCode(verificationCodeView);
    }

    protected void initViews(View view) {
        et_name = (EditText) view.findViewById(R.id.et_name);
        et_tel = (EditText) view.findViewById(R.id.et_tel);
        etEmail = (EditText) view.findViewById(R.id.et_email);

        et_name.setText(Util.getSharedPData(getActivity(), "apply", "name"));
        et_tel.setText(Util.getSharedPData(getActivity(), "apply", "phone"));
        etEmail.setText(Util.getSharedPData(getActivity(), "apply", "email"));

        etApplyPassword = (EditText) view.findViewById(R.id.et_apply_password);
        etArea = (EditText) view.findViewById(R.id.et_area);
        etDetailArea = (EditText) view.findViewById(R.id.et_detail_area);
        etApplyType = (EditText) view.findViewById(R.id.et_apply_type);
        etApplyTypeDetails = (EditText) view.findViewById(R.id.et_apply_detail_type);


        etAddressDetail = (EditText) view.findViewById(R.id.et_apply_address_details);
        verificationCodeView = (VerificationCodeView) view.findViewById(R.id.verificationCodeView);
        verificationCodeView.setVerificationCodeBackground(Color.rgb(18, 150, 219));
        verificationCodeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Api.changeCode(verificationCodeView);
            }
        });
        etFloor = (EditText) view.findViewById(R.id.et_floor);
        etRoom = (EditText) view.findViewById(R.id.et_room);


        et_describe = (EditText) view.findViewById(R.id.et_apply_describe);
        etCode = (EditText) view.findViewById(R.id.et_code);
        img_add = (ImageView) view.findViewById(R.id.iv_add);
        img_add.setOnClickListener(this);

        img_1 = (ImageView) view.findViewById(R.id.iv_img1);
        img_2 = (ImageView) view.findViewById(R.id.iv_img2);
        img_3 = (ImageView) view.findViewById(R.id.iv_img3);

        //包裹三张图片的RelativeLayout
        rl1 = (RelativeLayout) view.findViewById(R.id.rl_img1);
        rl2 = (RelativeLayout) view.findViewById(R.id.rl_img2);
        rl3 = (RelativeLayout) view.findViewById(R.id.rl_img3);
        //打叉图片
        ivX1 = (ImageView) view.findViewById(R.id.iv_img_x1);
        ivX2 = (ImageView) view.findViewById(R.id.iv_img_x2);
        ivX3 = (ImageView) view.findViewById(R.id.iv_img_x3);
        //设置打叉点击事件
        XOnclick();
        //大图
        llBigImg = (LinearLayout) view.findViewById(R.id.ll_big_img);
        ivBigImg = (PhotoView) view.findViewById(R.id.iv_big_img);

        //设置图片点击事件
        imgOnclick();


        imageViewList.add(img_1);
        imageViewList.add(img_2);
        imageViewList.add(img_3);
        btn_apply = (Button) view.findViewById(R.id.btn_apply);


        sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);

        //设置点击颜色
        btn_apply.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn_apply.setBackgroundResource(R.drawable.button_submit2);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn_apply.setBackgroundResource(R.drawable.button_submit);
                }
                return false;
            }
        });
        RxView.clicks(btn_apply).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Log.d(TAG, "accept: 提交了");
                Map<String, String> map = new HashMap<>();
                map.put("name", et_name.getText().toString());
                map.put("phone", et_tel.getText().toString());
                map.put("email", etEmail.getText().toString());
                Util.saveInputedData(getActivity(), "apply", map);
                //禁止返回键
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog
                        .setConfirmClickListener(null)
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

                                    if (checkValidate()) {

                                        if (Util.isPhoneNumberValid(et_tel.getText().toString())) {
                                            bindView();
                                            sweetAlertDialog.setTitleText("正在提交请等待");

                                            sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                                            sweetAlertDialog.setCancelable(false);
                                            sweetAlertDialog.showCancelButton(false);
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
                                    } else {
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
                sweetAlertDialog.show();

            }
        });

        llApplyDetailArea = (LinearLayout) view.findViewById(R.id.ll_apply_detail_area);

        llApplyFloor = (LinearLayout) view.findViewById(R.id.ll_apply_floor);
        llApplyRoom = (LinearLayout) view.findViewById(R.id.ll_apply_room);

        llApplyDetailType = (LinearLayout) view.findViewById(R.id.ll_apply_detail_type);

        //初始化Line
        llNameLine = (LinearLayout) view.findViewById(R.id.ll_apply_name_frg);
        llPhoneLine = (LinearLayout) view.findViewById(R.id.ll_apply_phonbe_frg);
        llPasswordLine = (LinearLayout) view.findViewById(R.id.ll_apply_password_frg);
        llEmailLine = (LinearLayout) view.findViewById(R.id.ll_apply_email_frg);
        llAreaLine = (LinearLayout) view.findViewById(R.id.ll_apply_area_frg);
        llPlaceLine = (LinearLayout) view.findViewById(R.id.ll_apply_place_frg);
        llFliesLine = (LinearLayout) view.findViewById(R.id.ll_apply_flies_frg);
        llRoonLine = (LinearLayout) view.findViewById(R.id.ll_apply_room_frg);
        llClassLine = (LinearLayout) view.findViewById(R.id.ll_apply_class_frg);

        llAddressDetaiLine = (LinearLayout) view.findViewById(R.id.ll_apply_addressdeta_name);

        llDetailClassLine = (LinearLayout) view.findViewById(R.id.ll_apply_detailclass_name);
        llDesctribeLine = (LinearLayout) view.findViewById(R.id.ll_apply_addressdeta_name);

        imgName = (ImageView) view.findViewById(R.id.img_apply_name_frag);
        imgPhone = (ImageView) view.findViewById(R.id.img_apply_phone_frag);
        imgEmail = (ImageView) view.findViewById(R.id.img_apply_email_frag);
        imgPassword = (ImageView) view.findViewById(R.id.img_apply_password_frag);
        imgAddressDetail = (ImageView) view.findViewById(R.id.img_apply_addressdet_frg);


        /**
         * 初始化dialog
         */
        setDialogAdapter();
        setDialog();
        setClearEditText();
        setEditTextBackground();
        setEditTextOnTouch();
        resetVisible();
    }

    //检查必填项
    private boolean check() {

        boolean notNull = getContent(et_tel)
                && getContent(et_name)
                && getContent(etApplyPassword)
                && getContent(etArea)
                && getContent(etApplyType)
                && getContent(etCode);
        return notNull;
    }

    //检查非法字符
    private boolean checkValidate() {
        boolean ok = Util.validateString(et_name.getText().toString()) &&
                Util.validateString(etAddressDetail.getText().toString()) &&
                Util.validateString(et_describe.getText().toString());
        return ok;
    }


    private boolean getContent(EditText e) {

        return !e.getText().toString().equals("");
    }


    private void setEditTextBackground() {
        RxBindingUtil.changColorAndVisable(et_name, llNameLine, imgName);
        RxBindingUtil.changColorAndVisable(et_tel, llPhoneLine, imgPhone);
        RxBindingUtil.changColorAndVisable(etEmail, llEmailLine, imgEmail);
        RxBindingUtil.changColorAndVisable(etApplyPassword, llPasswordLine, imgPassword);
        RxBindingUtil.changColorAndVisable(etAddressDetail, llAddressDetaiLine, imgAddressDetail);

        RxBindingUtil.changColorAndVisable(etArea, llAreaLine);
        RxBindingUtil.changColorAndVisable(etDetailArea, llPlaceLine);
        RxBindingUtil.changColorAndVisable(etFloor, llFliesLine);
        RxBindingUtil.changColorAndVisable(etRoom, llRoonLine);


        RxBindingUtil.changColorAndVisable(etApplyType, llClassLine);
        RxBindingUtil.changColorAndVisable(etApplyTypeDetails, llDetailClassLine);

        RxBindingUtil.changColorAndVisable(et_describe, llDesctribeLine);

    }


    private void setClearEditText() {
        RxBindingUtil.setClearText(imgName, et_name);
        RxBindingUtil.setClearText(imgPhone, et_tel);
        RxBindingUtil.setClearText(imgPassword, etApplyPassword);
        RxBindingUtil.setClearText(imgEmail, etEmail);
        RxBindingUtil.setClearText(imgAddressDetail, etAddressDetail);
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

        ivBigImg.setBackground(iv.getDrawable());
        ivBigImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llBigImg.setVisibility(View.GONE);
                ivBigImg.setBackground(null);
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

        apply.setRepair(AESUtil.encode(et_name.getText().toString()));
        apply.setTel(AESUtil.encode(et_tel.getText().toString()));
        apply.setEmail(AESUtil.encode(etEmail.getText().toString()));
        String MD5 = Util.getMD5(etApplyPassword.getText().toString());
        apply.setPassword(MD5);

        setApply();
        apply.setRepairDetails(et_describe.getText().toString());
        String etDescribe = et_describe.getText().toString().replaceAll("\r|\n", "");
        apply.setRepairDetails(etDescribe);
        String addresDetail = etAddressDetail.getText().toString().replaceAll("\r|\n", "");
        apply.setAddressDetail(addresDetail);
    }


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
     * 设置适配器
     * 需要的list是全局的stringlist
     */
    private void setDialogAdapter() {
        areaAdapter = new DialogAdapter(getActivity(), newAreaStringList, R.layout.simple_list_item);
        placeAdapter = new DialogAdapter(getActivity(), newPlaceStringList, R.layout.simple_list_item);
        fliesAdapter = new DialogAdapter(getActivity(), newFliesStringList, R.layout.simple_list_item);
        roomAdapter = new DialogAdapter(getActivity(), newRoomStringList, R.layout.simple_list_item);
        categoryAdapter = new DialogAdapter(getActivity(), newCategoryStringList, R.layout.simple_list_item);
        detailClassAdapter = new DialogAdapter(getActivity(), newDetailClassStringList, R.layout.simple_list_item);
    }

    DialogPlus areaDialog;
    DialogPlus placeDialog;
    DialogPlus fliesDialog;
    DialogPlus roomDialog;
    DialogPlus categoryDialog;
    DialogPlus detailClassDialog;


    private void setDialog() {
        /**
         * 设置区域Dialog
         */
        areaDialog = DialogUtil.getDialogBuilder(getActivity(), areaAdapter, R.layout.dialog_head1, this)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        if (position != -1) {
                            areaId = newAreaList.get(position).getId();
                            etArea.setText(newAreaList.get(position).getArea());
                            dialog.dismiss();
                        }
                    }
                })
                .create();
        /**
         * 楼号
         */
        placeDialog = DialogUtil.getDialogBuilder(getActivity(), placeAdapter, R.layout.dialog_head2, this)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        if (position != -1) {
                            placeId = newPlace.get(position).getP_id();

                            etDetailArea.setText(newPlace.get(position).getP_name());
                            dialog.dismiss();
                        }
                    }
                })
                .create();
        /**
         * 楼层
         */
        fliesDialog = DialogUtil.getDialogBuilder(getActivity(), fliesAdapter, R.layout.dialog_head6, this)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        if (position != -1) {
                            flieId = newFlies.get(position).getId();

                            etFloor.setText(newFlies.get(position).getFlies());
                            dialog.dismiss();
                        }
                    }
                })
                .create();
        /**
         * 房间
         */
        roomDialog = DialogUtil.getDialogBuilder(getActivity(), roomAdapter, R.layout.dialog_head7, this)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        if (position != -1) {
                            roomId = newRoom.get(position).getId();

                            etRoom.setText(newRoom.get(position).getRoomNumber());
                            dialog.dismiss();
                        }
                    }
                })
                .create();
        /**
         * 类型
         */
        categoryDialog = DialogUtil.getDialogBuilder(getActivity(), categoryAdapter, R.layout.dialog_head3, this)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        if (position != -1) {
                            categoryId = newCategory.get(position).getC_id();

                            etApplyType.setText(newCategory.get(position).getC_name());

                            dialog.dismiss();
                        }
                    }
                })
                .create();
        /**
         * 详细类型
         */
        detailClassDialog = DialogUtil.getDialogBuilder(getActivity(), detailClassAdapter, R.layout.dialog_head10, this)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        if (position != -1) {
                            detailTypeID = newDetailClass.get(position).getId();

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
                    queryFromServer("place", String.valueOf(areaId), AdressPlaceList);
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
                    queryFromServer("flies", String.valueOf(placeId), AdressFliesList);
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
                    queryFromServer("room", String.valueOf(flieId), AdressroomList);
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

                    queryFromServer("detailClass", String.valueOf(categoryId), TypeDetailClassList);
                    setNextVisible(v, event);
                    detailClassDialog.show();
                }
                return false;
            }
        });


    }

    private void setNextVisible(View view, MotionEvent event) {
        switch (view.getId()) {
            case R.id.et_area:
                //点击区域清空 楼号 层号 房间
                etDetailArea.setText("");
                etFloor.setText("");
                etRoom.setText("");
                //清空下一级可见
                llApplyDetailArea.setVisibility(View.VISIBLE);
//                llApplyBigFloorRoom.setVisibility(View.GONE);
                llApplyFloor.setVisibility(View.GONE);
                llApplyRoom.setVisibility(View.GONE);
                //清空存放好的Id
                placeId = 0;
                flieId = 0;
                roomId = 0;
                break;
            case R.id.et_detail_area:
                //点击区域清空  层号 房间
                etFloor.setText("");
                etRoom.setText("");
                //清空下一级可见
//                llApplyBigFloorRoom.setVisibility(View.GONE);
                llApplyFloor.setVisibility(View.VISIBLE);
                llApplyRoom.setVisibility(View.GONE);
                //清空存放好的Id
                flieId = 0;
                roomId = 0;
                break;
            case R.id.et_floor:
                //点击区域清空   房间
                etRoom.setText("");
                //清空下一级可见
                llApplyRoom.setVisibility(View.VISIBLE);
                //清空存放好的Id
                roomId = 0;
                break;
            case R.id.et_apply_type:
                //清空详细类型
                etApplyTypeDetails.setText("");
                llApplyDetailType.setVisibility(View.VISIBLE);
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

        switch (parmsName) {
            case "area": {
                newAreaList.clear();
                newAreaStringList.clear();
                List<Area> areas = response.getResultBean().getAreas();
                for (Area area : areas) {
                }
                newAreaList.addAll(areas);
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
//                roomDialog.show();
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
    public void onAttach(Context context) {
        super.onAttach(context);

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
        list_uri.clear();

    }


    @Override
    public void onResume() {
        super.onResume();

        if (MainActivity.list_uri != null && MainActivity.list_uri.size() > 0) {
            if (list_uri.size() > 3) {
                int length = list_uri.size() - 3;
                for (int i = 0; i < length; i++) {
                    list_uri.remove(i);
                }
            }
            for (Uri u : list_uri) {
                int i = 0;
                arrayUri[i] = u;
                i++;
            }
            //判断和赋值
            switchImage();
        }

    }

    int photoWidth = 240;
    int photoHeight = 240;

    private void switchImage() {

        switch (list_uri.size() - 1) {
            case 0:
                rl1.setVisibility(View.VISIBLE);
                img_1.setImageBitmap(getSmallBitmap(getRealPathFromURI(list_uri.get(0)), photoWidth, photoHeight));
                break;
            case 1:
                rl1.setVisibility(View.VISIBLE);
                rl2.setVisibility(View.VISIBLE);
                img_1.setImageBitmap(getSmallBitmap(getRealPathFromURI(list_uri.get(0)), photoHeight, photoHeight));
                img_2.setImageBitmap(getSmallBitmap(getRealPathFromURI(list_uri.get(1)), photoHeight, photoHeight));
                break;
            case 2:

                rl1.setVisibility(View.VISIBLE);
                rl2.setVisibility(View.VISIBLE);
                rl3.setVisibility(View.VISIBLE);
                img_1.setImageBitmap(getSmallBitmap(getRealPathFromURI(list_uri.get(0)), photoHeight, photoHeight));
                img_2.setImageBitmap(getSmallBitmap(getRealPathFromURI(list_uri.get(1)), photoHeight, photoHeight));
                img_3.setImageBitmap(getSmallBitmap(getRealPathFromURI(list_uri.get(2)), photoHeight, photoHeight));
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

                break;


            case R.id.iv_add:
                addPic();
                break;
        }

    }


    private void addPic() {

        Util.getPermission(getActivity());
        List<String> list = new ArrayList<>();
        list.add("打开相机");
        list.add("选择本地图片");
        DialogAdapter dialogAdapter = new DialogAdapter(getActivity(), list, R.layout.simple_list_item);
        dialogGetImage = DialogPlus.newDialog(getActivity())
                .setAdapter(dialogAdapter)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.dialog_head4)
                .setContentWidth((int) (windowWitch / 1.5))
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        if (position == 0) {
                            startCamera();
                        }
                        if (position == 1) {
                            startGallery();
                        }
                        dialogGetImage.dismiss();
                    }

                })
                .setExpanded(true, (int) (windowHeigth / 1.5))  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialogGetImage.show();

    }

    public static File fileUri;

    private void startCamera() {

        int currentVersion = android.os.Build.VERSION.SDK_INT;

        fileUri = FIleUtils.createImageFile();


        //低于24为6.0以下
        if (currentVersion < 24) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileUri));

            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {

                getActivity().startActivityForResult(intent, TAKE_PHOTO_RAW);

            }

        } else {
            Uri photoUri = FileProvider.getUriForFile(
                    getActivity(),
                    getActivity().getPackageName(),
                    fileUri);
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            getActivity().startActivityForResult(takePhotoIntent, TAKE_PHOTO_RAW);
        }


    }


    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {

            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;

    }


    private void startGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        getActivity().startActivityForResult(intent, REQUEST_IMAGE);
    }


    private void upApply() {


        String json = JsonUtil.beanToJson(apply);

        List<File> files = getFiles(list_uri);

        Api.submit(json, etCode.getText().toString(), files)
                .connTimeOut(60000)
                .readTimeOut(60000)
                .writeTimeOut(60000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError: " + e.getMessage().toString());
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        sweetAlertDialog.setTitleText("网络异常")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse: " + response);
                        if ("申请成功等待处理".equals(response)) {
                            Util.writePhoneToLocal(ApplyFragment.this.apply, MyApplication.getContext());
                            sweetAlertDialog.setConfirmText("确定")
                                    .setTitleText("报修成功")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            clearAll();
                                            sweetAlertDialog.dismiss();
                                        }
                                    }).changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        } else {
                            sweetAlertDialog.setTitleText(response)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                        }
                                    })
                                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        }
                    }
                });
    }


    private void setApply() {
        apply.setArea(String.valueOf(areaId));
        apply.setDetailArea(String.valueOf(placeId));
        apply.setFlies(String.valueOf(flieId));
        apply.setRoom(String.valueOf(roomId));
        apply.setClasss(String.valueOf(categoryId));
        apply.setDetailClass(String.valueOf(detailTypeID));

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
                    paths[i] = Util.getPath(getActivity(), list_uri.get(i));
                }

                String newPath = Util.compressImage(getActivity(), paths[i]);

                files.add(new File(newPath));
            }
        }
        return files;

    }


    private void clearAll() {
        et_name.setText("");
        et_tel.setText("");
        et_describe.setText("");
        etCode.setText("");
        etArea.setText("");
        etDetailArea.setText("");
        etEmail.setText("");
        etApplyType.setText("");

        etApplyPassword.setText("");
        etFloor.setText("");
        etRoom.setText("");
        etAddressDetail.setText("");


        rl1.setVisibility(View.GONE);
        rl2.setVisibility(View.GONE);
        rl3.setVisibility(View.GONE);
        list_uri.clear();

        resetVisableAlways();
    }


    public LinearLayout RlIsVisable() {
        if (llBigImg != null && llBigImg.getVisibility() == View.VISIBLE) {
            return llBigImg;
        }
        return null;
    }

    @Override
    public ImageView bigImageView() {
        return ivBigImg;
    }

    @Override
    public void resetVisible() {

        //进来就全部不可见
        llApplyDetailArea.setVisibility(View.GONE);
        llApplyFloor.setVisibility(View.GONE);
        llApplyRoom.setVisibility(View.GONE);
        llApplyDetailType.setVisibility(View.GONE);


        //如果区域不为其他 楼号可见
        if (!etArea.getText().toString().equals("其它") && !etArea.getText().toString().equals("")) {

            llApplyDetailArea.setVisibility(View.VISIBLE);
        }
        if (!etDetailArea.getText().toString().equals("其它") && !etDetailArea.getText().toString().equals("")) {
            llApplyFloor.setVisibility(View.VISIBLE);
        }
        if (!etFloor.getText().toString().equals("其它") && !etFloor.getText().toString().equals("")) {

            llApplyRoom.setVisibility(View.VISIBLE);
        }
        if (!etApplyType.getText().toString().equals("其它") && !etApplyType.getText().toString().equals("")) {
            llApplyDetailType.setVisibility(View.VISIBLE);
        }
    }

    private void resetVisableAlways() {
        //进来就全部不可见
        llApplyDetailArea.setVisibility(View.GONE);
        llApplyFloor.setVisibility(View.GONE);
        llApplyRoom.setVisibility(View.GONE);
        llApplyDetailType.setVisibility(View.GONE);
    }


    @Override
    public void setVisable() {

    }

}
