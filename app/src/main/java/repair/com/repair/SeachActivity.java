//package repair.com.repair;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Pattern;
//
//import application.MyApplication;
//import model.Apply;
//import model.Category;
//import model.Place;
//import model.ResultBean;
//import repari.com.adapter.ApplysAdapter;
//import util.JsonUtil;
//import util.WaterListViewListener;
//
//import static android.content.Context.MODE_PRIVATE;
//import static repair.com.repair.R.drawable.apply;
//import static repair.com.repair.R.drawable.seach022;
//
///**
// * Created by hsp on 2016/11/27.
// */
//
//
//public class SeachActivity extends AppCompatActivity {
//
//    private static final String TAG = "SeachActivity";
//
//    private Button btnSearch;
//    private EditText edSearch;
//    private ListView listSearch;
//    private ApplysAdapter applysAdapter;
//    public ResultBean res;
//    String edText = "";
//    int matchFlag = 0;
//    ResultBean resultBean;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        // TODO Auto-generated method stub
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.seach_activity);
//
//
//        initView();
//        initData();
//        edSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//                edText = editable.toString();
//
//            }
//        });
//
//
//        btnSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                resultBean = getRes(res, edText);
//                applysAdapter = new ApplysAdapter(resultBean, SeachActivity.this);
//                if (matchFlag == 0) {
//                    Toast.makeText(SeachActivity.this, "没有符合的记录", Toast.LENGTH_SHORT).show();
//                } else {
//                    listSearch.setAdapter(applysAdapter);
//                    listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                            Apply applys = resultBean.getApplys().get(i);
//
//                            Intent intent = new Intent(MyApplication.getContext(), DetailsActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                            Bundle bundle = new Bundle();
//
//                            bundle.putSerializable("res", resultBean);
//
//                            bundle.putSerializable("apply_item", applys);
//
//                            intent.putExtras(bundle);
//
//                            startActivity(intent);
//                        }
//                    });
//                }
//
//            }
//        });
//
//
//    }
//
//    private void initData() {
//        new AsyncTask<Void, Void, ResultBean>() {
//
//            @Override
//            protected ResultBean doInBackground(Void... voids) {
//                SharedPreferences preferences = getSharedPreferences("json_data", MODE_PRIVATE);
//                String json = preferences.getString("json", "");
//                res = JsonUtil.jsonToBean(json);
//                return res;
//            }
//
//            @Override
//            protected void onPostExecute(ResultBean resultBean) {
//
//
//            }
//        }.execute();
//    }
//
//
//    private void initView() {
//        btnSearch = (Button) findViewById(R.id.btn_search);
//        edSearch = (EditText) findViewById(R.id.et_search);
//        listSearch = (ListView) findViewById(R.id.lv_search);
//    }
//
//    private ResultBean getRes(ResultBean res, String ed_string) {
//
//
//        String nameMatch;
//        String describeMatch;
//        String detalisMatch;
//        String statusMatch;
//
//        String A_place;
//
//
//        String pattern = "";
//
//
//        pattern = ".*" + ed_string + ".*";
//
//
//        Log.d(TAG, "getRes: s2" + pattern);
//        boolean Matches;
//        ResultBean resultBean = new ResultBean();
//        String regex = ed_string;
//        List<Apply> applies = res.getApplys();
//        List<Apply> newApply = new ArrayList<>();
//        for (int i = 0; i < applies.size(); i++) {
//
//            nameMatch = applies.get(i).getA_name();
//            describeMatch = applies.get(i).getA_describe();
//            detalisMatch = applies.get(i).getA_detalis();
//            statusMatch = applies.get(i).getA_status();
//
//            A_place = getPlaceId(i, res);
//
//
//            Matches = Pattern.matches(pattern, nameMatch)
//                    || Pattern.matches(pattern, detalisMatch)
//                    || Pattern.matches(pattern, describeMatch)
//                    || Pattern.matches(pattern, statusMatch)
//                    || Pattern.matches(pattern, A_place);
//
//            if (Matches) {
//                if (applies.get(i).getA_name() != null) {
//                    newApply.add(applies.get(i));
//                    //Log.d(TAG, "getRes:dfdfdf " + newApply.get(i).getA_name());
//                    matchFlag++;
//                }
//            } else {
//                Log.d(TAG, "getRes: 没有数据");
//                Log.d(TAG, "getRes: describe" + applies.get(i).getA_describe());
//            }
//
//
//        }
//        resultBean.setApplys(newApply);
//        resultBean.setAnnouncements(res.getAnnouncements());
//        resultBean.setCategory(res.getCategory());
//        resultBean.setEmployee(res.getEmployee());
//        resultBean.setPhotos(res.getPhotos());
//        resultBean.setPlaces(res.getPlaces());
//
//
//        return resultBean;
//
//
//    }
//
//    private String getPlaceId(int position, ResultBean rs) {
//        int appyly_pid = rs.getApplys().get(position).getA_place();
//
//        String p_name = "";
//
//        for (Place place : rs.getPlaces()) {
//            if (appyly_pid == place.getP_id()) {
//                p_name = place.getP_name();
//                break;
//            }
//        }
//        return p_name;
//    }
//
//    private String getCategoryId(int position, ResultBean rs) {
//        String c_name = rs.getCategory().get(position).getC_name();
//
//
//        return c_name;
//    }
//
//}
