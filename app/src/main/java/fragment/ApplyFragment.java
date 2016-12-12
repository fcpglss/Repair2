package fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import model.Test2;
import repair.com.repair.R;

/**
 * Created by hsp on 2016/11/27.
 */

public class ApplyFragment extends Fragment implements View.OnClickListener {

    private EditText et_name;
    private EditText et_category;
    private EditText et_place;
    private EditText et_tel;

    private Button btn_apply;

    private DataOutputStream out=null;
    private DataInputStream in =null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Log.d("MainFragment","Apply_onCreateView");
        return inflater.inflate(R.layout.apply_frag, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    private void initView() {
        et_name= (EditText) getActivity().findViewById(R.id.et_name);
        et_category= (EditText) getActivity().findViewById(R.id.et_category);
        et_place= (EditText) getActivity().findViewById(R.id.et_apply_place);
        et_tel= (EditText) getActivity().findViewById(R.id.et_tel);
        btn_apply= (Button) getActivity().findViewById(R.id.btn_apply);
        btn_apply.setOnClickListener(this);
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
        Log.d("MainFragment", "Apply_onDestroy");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("MainFragment", "Apply_onResume");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("MainFragment", "Apply_onStart");
    }

    @Override
    public void onClick(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Test2 test2 =new Test2();
                test2.setA_name(et_name.getText().toString());
                test2.setA_category(Integer.parseInt(et_category.getText().toString()));
                test2.setA_tel(et_tel.getText().toString());
                test2.setA_place(Integer.parseInt(et_place.getText().toString()));
                Gson gson =new Gson();
                String jsonString = gson.toJson(test2);
                try {
                    byte data[] =jsonString.getBytes();
                  InetAddress address=InetAddress.getLocalHost();
                    DatagramPacket data_pack=new DatagramPacket(data,data.length,address,666);
                    DatagramSocket mail_data=new DatagramSocket();
                    mail_data.send(data_pack);
                } catch (IOException e) {
                    Log.d("Apply","错误："+e.getMessage().toString());
                }
                Log.d("Apply","提交json="+jsonString);
            }
        }).start();

    }
}
