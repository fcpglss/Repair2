package fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Category;
import model.Place;
import model.ResultBean;
import repair.com.repair.R;
import util.JsonUtil;

/**
 * Created by hsp on 2016/11/27.
 */

public class StatisticsFragment extends Fragment {

    private static final String TAG = "StatisticsFragment";
    private TextView tv_day, tv_week, tv_month, tv_sum;
    private TextView tv_water, tv_dian, tv_tujian, tv_shebei;
    private TextView tv_louhao;

    private Spinner sp_louhao;
    int monthCount = 0;
    int dayCount = 0;
    int weekCount = 0;
    ResultBean res = null;
    ArrayAdapter spLouhao;
    private List<String> list_louhao = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Log.d("MainFragment", "Statistic_onCreateView");
        return inflater.inflate(R.layout.fragment3, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        initData();

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
                initView(resultBean);
            }

        }.execute();


    }

    private void initView(ResultBean resultbean) {
        tv_sum.setText("" + res.getApplys().size());
        getMonthAndDay();//获取这个月每月报修和今日报修
        tv_day.setText("" + dayCount);
        tv_month.setText("" + monthCount);
        tv_week.setText("" + getNowWeek());
        tv_water.setText("" + getCategory()[0]);
        tv_dian.setText("" + getCategory()[1]);
        tv_tujian.setText("" + getCategory()[2]);
        tv_shebei.setText("" + getCategory()[3]);


        if (resultbean != null) {
            spLouhao = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item, list_louhao);
            if (list_louhao.size() == resultbean.getPlaces().size()) {
                spLouhao.notifyDataSetChanged();
                sp_louhao.setAdapter(spLouhao);

            } else {
                for (Place c : resultbean.getPlaces()) {
                    list_louhao.add(c.getP_name());
                }
                spLouhao.notifyDataSetChanged();
                sp_louhao.setAdapter(spLouhao);
            }
        } else {
            Log.d(TAG, "initView: res 为null");
            initData();
        }

        sp_louhao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                getSelectedCount(i);
                tv_louhao.setText(""+getSelectedCount(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });
    }


    private int  getSelectedCount(int i) {

        Log.d(TAG, "getSelectedCount:  点击了" +i);
        int P_place = res.getPlaces().get(i).getP_id();
        int A_place;

        Log.d(TAG, "getSelectedCount: P_place  "+P_place);

        int placeCount = 0;

        for (int j = 0; j < res.getApplys().size(); j++) {
            A_place = res.getApplys().get(j).getA_place();
            Log.d(TAG, "getSelectedCount: Aplace   "+ A_place);
            if (A_place == P_place) {
                placeCount++;
            }
        }

        return placeCount;

    }


    private void init() {

        //报修记录TextView
        tv_day = (TextView) getActivity().findViewById(R.id.tv_day);
        tv_week = (TextView) getActivity().findViewById(R.id.tv_week);
        tv_month = (TextView) getActivity().findViewById(R.id.tv_month);
        tv_sum = (TextView) getActivity().findViewById(R.id.tv_sum);

        //维修类型
        tv_water = (TextView) getActivity().findViewById(R.id.tv_water);
        tv_dian = (TextView) getActivity().findViewById(R.id.tv_dian);
        tv_shebei = (TextView) getActivity().findViewById(R.id.tv_shebei);
        tv_tujian = (TextView) getActivity().findViewById(R.id.tv_tujian);


        sp_louhao = (Spinner) getActivity().findViewById(R.id.sp_louhao);
        tv_louhao = (TextView) getActivity().findViewById(R.id.tv_louhao);


    }


    private void getMonthAndDay() {

        monthCount = 0;
        dayCount = 0;
        //获取当前年月
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
//        String date = "2016-12-16";
        Log.d(TAG, "getMonth: " + date);
        String[] nyr;

        String resDate;

        String reg = "\\D";
        String[] nowDate = date.split(reg);


        for (int i = 0; i < res.getApplys().size(); i++) {

            resDate = res.getApplys().get(i).getA_createat();
            Log.d(TAG, "getMonth: resDate" + resDate);
            Log.d(TAG, "getMonth: i  " + i);
            nyr = resDate.split(reg);


            for (String s :
                    nyr) {
                Log.d(TAG, "getMonth: " + s);
            }

            if (nyr[0].equals(nowDate[0]) && nyr[1].equals(nowDate[1])) {
                monthCount++;
            }

            if (nyr[0].equals(nowDate[0]) && nyr[1].equals(nowDate[1]) && nyr[2].equals(nowDate[2])) {
                dayCount++;
            }
        }


        Log.d(TAG, "getMonth: monthCount " + monthCount + "  dayCount  " + dayCount);


    }

    private int getNowWeek() {

        weekCount = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        int nowWeek = getWeek(date); //本周
        for (int i = 0; i < res.getApplys().size(); i++) {
            String[] strings = res.getApplys().get(i).getA_createat().split(" ");
            int resWeek = getWeek(strings[0]);
            if (nowWeek == resWeek) {
                weekCount++;
            }
        }

        Log.d(TAG, "getNowWeek: " + weekCount);
        return weekCount;

    }

    private int getWeek(String d) {

        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        Log.d(TAG, "getWeek: " + c.get(java.util.Calendar.WEEK_OF_YEAR));
        return c.get(java.util.Calendar.WEEK_OF_YEAR);
    }


    private int[] getCategory() {

        int[] count = new int[4];

        for (int i = 0; i < res.getApplys().size(); i++) {
            if (res.getApplys().get(i).getA_category() == 1) {
                count[0]++;
            }
            if (res.getApplys().get(i).getA_category() == 2) {
                count[1]++;
            }
            if (res.getApplys().get(i).getA_category() == 3) {
                count[2]++;
            }
            if (res.getApplys().get(i).getA_category() == 4) {
                count[3]++;
            }
        }

        return count;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("MainFragment", "Statisc_onAttach");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("MainFragment", "Statisc_onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("MainFragment", "Statisc_onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MainFragment", "Statisc_onDestroy");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MainFragment", "Statistics_onResume");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("MainFragment", "Statistics_onStart");
    }


}
