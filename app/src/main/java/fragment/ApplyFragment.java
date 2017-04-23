package fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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


import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;

import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import application.MyApplication;
import camera.FIleUtils;
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
import repair.com.repair.MainActivity;
import repair.com.repair.R;
import repari.com.adapter.DialogAdapter;
import repari.com.adapter.DialogDetailAdapter;
import util.DialogUtil;
import util.EdiTTouch;
import util.JsonUtil;
import util.Util;

import static camera.CalculateImage.getSmallBitmap;
import static repair.com.repair.MainActivity.GET_JSON;
import static repair.com.repair.MainActivity.JSON_URL;
import static repair.com.repair.MainActivity.REQUEST_IMAGE;
import static repair.com.repair.MainActivity.TAKE_PHOTO_RAW;
import static repair.com.repair.MainActivity.UP_APPLY;
import static repair.com.repair.MainActivity.list_uri;
import static util.NetworkUtils.isNetworkConnected;
import static repair.com.repair.MainActivity.windowWitch;
import static repair.com.repair.MainActivity.windowHeigth;


public class ApplyFragment extends LazyFragment2 implements View.OnClickListener, GetFragment, EdiTTouch, ResetVisable {

    private static final String TAG = "ApplyFragment";


    private boolean isFirst = true;

    private EditText et_name, et_tel, et_describe;

    //后面添加的电子邮箱，报修密码，报修区域，楼号，报修类型，类型详情
    private EditText etEmail, etApplyPassword, etArea, etDetailArea, etApplyType, etApplyTypeDetails,etAddressDetail;
    //对话框点击显示下一级
    private LinearLayout llApplyArea, llApplyDetailArea, llApplyBigFloorRoom, llApplyFloor, llApplyRoom, llApplyType, llApplyDetailType;
    //记录区域ID
    int AreaId;
    int PlaceId;//楼号ID
    int fliesId;//层号ID
    // 添加层号 房间号
    private EditText etFloor, etRoom;


    private List<Area> newAreaList = new ArrayList<>();
    private List<Place> newPlace = new ArrayList<>();
    private List<Flies> newFlies = new ArrayList<>();
    private List<Room> newRoom = new ArrayList<>();
    private List<Category> newCategory = new ArrayList<>();
    private List<DetailClass> newDetailClass = new ArrayList<>();


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

//    ArrayAdapter categoryAdapter;
//    ArrayAdapter placeAdapter;

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
    private List<Integer> listApplyDetailTypeID = new ArrayList<>();

    private List<String> listApplyType = new ArrayList<>();
    private List<String> listApplyDetailType = new ArrayList<>();

    private int areaId = 0;
    private int placeId = 0;
    private int flieId = 0;
    private int roomId = 0;
    private int categoryId = 0;
    private int detailTypeID = 0;

    private View view;


    /**
     * 用于适配器的 Stringlist
     */
    List<String> newAreaStringList = new ArrayList<>();
    List<String> newPlaceStringList = new ArrayList<>();
    List<String> newFliesStringList = new ArrayList<>();
    List<String> newRoomStringList = new ArrayList<>();
    List<String> newCategoryStringList = new ArrayList<>();
    List<String> newDetailClassStringList = new ArrayList<>();


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
                    Toast.makeText(getActivity(), response.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    break;
                case 4:
                    Log.d(TAG, "handleMessage4: 内存没有数据，尝试从本地文件读取");
                    String addressJson = Util.loadAddressFromLocal(MyApplication.getContext());
                    addressRes = JsonUtil.jsonToBean(addressJson);
                    break;
                case 5:
                    Toast.makeText(getActivity(), "请填写报修地址", Toast.LENGTH_SHORT).show();
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
        return R.layout.apply_fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            loadData();
        }
    }

    private void loadData() {
        queryFromServer("area", "0", JSON_URL);
        queryFromServer("category", "0", JSON_URL);
    }

    protected void initViews(View view) {

        etEmail = (EditText) view.findViewById(R.id.et_email);
        etApplyPassword = (EditText) view.findViewById(R.id.et_apply_password);
        etArea = (EditText) view.findViewById(R.id.et_area);
        etDetailArea = (EditText) view.findViewById(R.id.et_detail_area);
        etApplyType = (EditText) view.findViewById(R.id.et_apply_type);
        etApplyTypeDetails = (EditText) view.findViewById(R.id.et_apply_detail_type);
        et_name = (EditText) view.findViewById(R.id.et_name);
        etAddressDetail = (EditText) view.findViewById(R.id.et_apply_address_details);

        etFloor = (EditText) view.findViewById(R.id.et_floor);
        etRoom = (EditText) view.findViewById(R.id.et_room);

        et_tel = (EditText) view.findViewById(R.id.et_tel);

        et_describe = (EditText) view.findViewById(R.id.et_apply_describe);

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
        ivBigImg = (ImageView) view.findViewById(R.id.iv_big_img);

        //设置图片点击事件
        imgOnclick();

        imageViewList.add(img_1);
        imageViewList.add(img_2);
        imageViewList.add(img_3);
        btn_apply = (Button) view.findViewById(R.id.btn_apply);
        btn_clear = (Button) view.findViewById(R.id.btn_clear);
        btn_apply.setOnClickListener(this);
        btn_clear.setOnClickListener(this);

        //滚动
        svBackground = (ScrollView) view.findViewById(R.id.sv_apply);


        //设置点击颜色
        btn_apply.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d(TAG, "onTouch: " + event.getAction());
                    btn_apply.setBackgroundResource(R.drawable.button_submit2);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d(TAG, "onTouch: " + event.getAction());
                    btn_apply.setBackgroundResource(R.drawable.button_submit);
                }
                return false;
            }
        });
        btn_clear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d(TAG, "onTouch: " + event.getAction());
                    btn_clear.setBackgroundResource(R.drawable.button_submit2);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d(TAG, "onTouch: " + event.getAction());
                    btn_clear.setBackgroundResource(R.drawable.button_submit);
                }
                return false;
            }
        });

        llApplyArea = (LinearLayout) view.findViewById(R.id.ll_apply_area);
        llApplyDetailArea = (LinearLayout) view.findViewById(R.id.ll_apply_detail_area);
        llApplyBigFloorRoom = (LinearLayout) view.findViewById(R.id.ll_apply_big_floor_room);
        llApplyFloor = (LinearLayout) view.findViewById(R.id.ll_apply_floor);
        llApplyRoom = (LinearLayout) view.findViewById(R.id.ll_apply_room);
        llApplyType = (LinearLayout) view.findViewById(R.id.ll_apply_type);
        llApplyDetailType = (LinearLayout) view.findViewById(R.id.ll_apply_detail_type);


        /**
         * 初始化dialog
         */
        setDialogAdapter();
        setDialog();
        setEditTextOnTouch();
//        setAreaDialog();

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
        String MD5  = Util.getMD5(etApplyPassword.getText().toString());
        Log.d(TAG, "bindView: apply MD5: "+MD5);
        apply.setPassword(MD5);
        setApply();
        apply.setRepairDetails(et_describe.getText().toString());
        apply.setAddressDetail(etAddressDetail.getText().toString());
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
    private void showId(){
        Log.d(TAG, "showId: 区域id: "+areaId);
        Log.d(TAG, "showId: 楼号id： "+placeId);
        Log.d(TAG, "showId: 楼层id： "+flieId);
        Log.d(TAG, "showId: 房间id： "+roomId);
        Log.d(TAG, "showId: 类型id:  "+categoryId );
        Log.d(TAG, "showId: 类详id： "+detailTypeID);
    }

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
        placeDialog = DialogUtil.getDialogBuilder(getActivity(), placeAdapter, R.layout.dialog_head2, this)
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
        fliesDialog = DialogUtil.getDialogBuilder(getActivity(),fliesAdapter,R.layout.dialog_head6,this)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        flieId = newFlies.get(position).getId();
                        showId();
                        etFloor.setText(newFlies.get(position).getFlies());
                        dialog.dismiss();
                    }
                })
                .create();
        /**
         * 房间
         */
        roomDialog = DialogUtil.getDialogBuilder(getActivity(),roomAdapter,R.layout.dialog_head7,this)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        roomId = newRoom.get(position).getId();
                        showId();
                        etRoom.setText(newRoom.get(position).getRoomNumber());
                        dialog.dismiss();
                    }
                })
                .create();
        /**
         * 类型
         */
        categoryDialog = DialogUtil.getDialogBuilder(getActivity(),categoryAdapter,R.layout.dialog_head3,this)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        categoryId = newCategory.get(position).getC_id();
                        showId();
                        etApplyType.setText(newCategory.get(position).getC_name());
                        dialog.dismiss();
                    }
                })
                .create();
        /**
         * 详细类型
         */
        detailClassDialog = DialogUtil.getDialogBuilder(getActivity(),detailClassAdapter,R.layout.dialog_head10 ,this)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        detailTypeID = newDetailClass.get(position).getId();
                        showId();
                        etApplyTypeDetails.setText(newDetailClass.get(position).getClassDetail());
                        dialog.dismiss();
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
                    queryFromServer("place",String.valueOf(areaId),JSON_URL);
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
                    queryFromServer("flies",String.valueOf(placeId),JSON_URL);
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
                    queryFromServer("room",String.valueOf(flieId),JSON_URL);
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
                if (event.getAction() == MotionEvent.ACTION_UP){
                    setNextVisible(v,event);
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
                if (event.getAction() == MotionEvent.ACTION_UP){
                    queryFromServer("detailClass",String.valueOf(categoryId),JSON_URL);
                    setNextVisible(v,event);
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
                llApplyDetailArea.setVisibility(View.INVISIBLE);
                llApplyBigFloorRoom.setVisibility(View.GONE);
                llApplyFloor.setVisibility(View.INVISIBLE);
                llApplyRoom.setVisibility(View.INVISIBLE);
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
                llApplyBigFloorRoom.setVisibility(View.GONE);
                llApplyFloor.setVisibility(View.INVISIBLE);
                llApplyRoom.setVisibility(View.INVISIBLE);
                //清空存放好的Id
                flieId = 0;
                roomId = 0;
                break;
            case R.id.et_floor:
                //点击区域清空   房间
                etRoom.setText("");
                //清空下一级可见
                llApplyRoom.setVisibility(View.INVISIBLE);
                //清空存放好的Id
                roomId = 0;
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
                Collections.sort(newPlace,c);
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
                Collections.sort(newFlies,flies);

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
                Room.ComparatorRoom ro=new Room.ComparatorRoom();
                Collections.sort(newRoom,ro);
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

                Category.ComparatorCategory ca=new Category.ComparatorCategory();
                Collections.sort(newCategory,ca);

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

                DetailClass.ComparatorDetail deta=new DetailClass.ComparatorDetail();
                Collections.sort(newDetailClass,deta);
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
        Log.d(TAG, "onDestroy: ");
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
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
                Log.d(TAG, "onResume: " + u.toString());
                i++;
            }

            //判断和赋值
            switchImage();

        } else {

            Log.d(TAG, "onResume: " + "ApplyFragment没有获取到uri");
        }

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


            case R.id.iv_add:
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

    public static File fileUri;

    private void startCamera() {
        fileUri = FIleUtils.createImageFile();
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

    private int getFloor(int placeId, String floorName) {

        Log.d(TAG, "getFloor: floor  " + floorName);
        int id = 0;
        List<Flies> list = addressRes.getFlies();
        for (Flies f : list) {
            if (f.getaFloor() == placeId) {
                if (f.getFlies().equals(floorName)) {
                    id = f.getId();
                    return id;
                }
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

                Util.submit("apply", json, GET_JSON, UP_APPLY, files).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(MyApplication.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //clearAll();
                        Toast.makeText(MyApplication.getContext(), response.toString(), Toast.LENGTH_LONG).show();
                        if ("申请成功等待处理".equals(response.toString())) {
                            Util.writePhoneToLocal(apply, MyApplication.getContext());
                        }
                    }
                });

            } else {
                Toast.makeText(MyApplication.getContext(), "请填写完整信息...", Toast.LENGTH_SHORT).show();
            }


        }

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

                Log.d(TAG, "getFiles: " + paths[i]);
                String newPath = Util.compressImage(getActivity(), paths[i]);
                // Log.d(TAG, "getFiles: "+newPath);
                files.add(new File(newPath));
            }
        }
        return files;

    }


    private void clearAll() {
        et_name.setText("");
        et_tel.setText("");
        et_describe.setText("");

        etArea.setText("");
        etDetailArea.setText("");
        etEmail.setText("");
        etApplyType.setText("");

        etApplyPassword.setText("");
        etFloor.setText("");
        etRoom.setText("");

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
    public void resetVisible() {

        //进来就全部不可见
        llApplyDetailArea.setVisibility(View.INVISIBLE);
        llApplyBigFloorRoom.setVisibility(View.GONE);
        llApplyFloor.setVisibility(View.INVISIBLE);
        llApplyRoom.setVisibility(View.INVISIBLE);
        llApplyDetailType.setVisibility(View.INVISIBLE);


        //如果区域不为其他 楼号可见
        if (!etArea.getText().toString().equals("其它") && !etArea.getText().toString().equals("")) {
            Log.d(TAG, "resetVisible: " + etArea.getText());
            llApplyDetailArea.setVisibility(View.VISIBLE);
        }
        if (!etDetailArea.getText().toString().equals("其它") && !etDetailArea.getText().toString().equals("")) {
            Log.d(TAG, "resetVisible: " + etDetailArea.getText());
            llApplyBigFloorRoom.setVisibility(View.VISIBLE);
            llApplyFloor.setVisibility(View.VISIBLE);
        }
        if (!etFloor.getText().toString().equals("其它") && !etFloor.getText().toString().equals("")) {
            Log.d(TAG, "resetVisible: " + etFloor.getText());
            llApplyRoom.setVisibility(View.VISIBLE);
        }
        if (!etApplyType.getText().toString().equals("其它") && !etApplyType.getText().toString().equals("")) {
            Log.d(TAG, "resetVisible: " + etApplyType.getText());
            llApplyDetailType.setVisibility(View.VISIBLE);
        }
    }

    private void resetVisableAlways() {
        //进来就全部不可见
        llApplyDetailArea.setVisibility(View.INVISIBLE);
        llApplyBigFloorRoom.setVisibility(View.GONE);
        llApplyFloor.setVisibility(View.INVISIBLE);
        llApplyRoom.setVisibility(View.INVISIBLE);
        llApplyDetailType.setVisibility(View.INVISIBLE);
    }


    @Override
    public void setVisable() {

    }
}
