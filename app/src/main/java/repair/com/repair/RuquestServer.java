package repair.com.repair;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Administrator on 2016-12-7.
 */

public class RuquestServer extends Service {
    private ConnectionThread thread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.disConnection();
        thread=null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        thread=new ConnectionThread("mina",getApplicationContext());
        thread.start();
    }

    class ConnectionThread extends HandlerThread {
        private Context context;
        boolean isConnection;
        ConnectionManager mManager;

        ConnectionThread(String name, Context context) {
            super(name);
            this.context = context;
            ConnectionConfig config = new ConnectionConfig.Builder(context)
                    .setIp("192.168.31.201")
                    .setPort(9000)
                    .setReadBufferSize(10240)
                    .setConnectionTimeout(10000).builder();
            mManager = new ConnectionManager(config);
        }

        /**
         * ��ʼ�������ǵķ�����
         */
        @Override
        protected void onLooperPrepared() {
            for (; ; ) {
                isConnection = mManager.connect();//����������������
                if (isConnection) {
                    Log.d("Main", "���ӷ������ɹ�");
                    break;
                }
                try {
                    Thread.sleep(3000);
                    Log.d("Main", "��������");
                } catch (Exception e) {
                    Log.d("Main", "MainService��onLooperPerpared()" + e.getMessage().toString());
                }
            }
        }
        public void disConnection()
        {
            mManager.disConnection();
        }
    }

}