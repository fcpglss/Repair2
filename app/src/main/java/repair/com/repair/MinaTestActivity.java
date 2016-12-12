package repair.com.repair;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by Administrator on 2016-12-6.
 */

public class MinaTestActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_start_server;
    private Button btn_send;
    private MessageBroadcast receiver =
            new MessageBroadcast();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Main","MinaTestActivity��onCreate()������");
        setContentView(R.layout.fragment3);
        Log.d("Main","MinaTestActivity��setContent������");
        btn_start_server = (Button) findViewById(R.id.start_server_view);
        btn_send = (Button) findViewById(R.id.send_view);
        initView();
        registerBroadcast();
    }


    private void initView() {
        btn_start_server.setOnClickListener(this);
        btn_send.setOnClickListener(this);
    }

    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter("minatest.mina");
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, filter);
    }

    private void unregisterBroadcast()
    {
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View view) {

        switch(view.getId())
        {
            case R.id.send_view:
                SessionManager.getmInstance().writeToServer("123");
                break;
            case R.id.start_server_view:
                Intent intent=new Intent(this, RuquestServer.class);
                startService(intent);
                break;
        }

    }
    private class MessageBroadcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
           Log.d("Main",intent.getStringExtra("message"));
        }
    }


    @Override
    protected void onDestroy() {
        unregisterBroadcast();
        stopService(new Intent(this,MinaTestActivity.class));
        super.onDestroy();
        Log.d("Main","MinaTestActivity��Destroy������");
    }
}
