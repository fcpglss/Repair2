package repair.com.repair;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;

/**
 * Created by Administrator on 2016-12-6.
 */

public class ConnectionManager {
    private static final String BROADCAST_ACTION ="minatest.mina";
    private static final String MESSAGE="message";
    private ConnectionConfig mConfig;
    private WeakReference<Context> mContext;
    private NioSocketConnector mConnection;
    private IoSession mSession;
    private InetSocketAddress mAddress;

    private ConnectFuture future=null;

    public ConnectionManager(ConnectionConfig config)
    {
        this.mConfig=config;
        this.mContext =new WeakReference<Context>(config.getContext());
        init();
    }

    private void init() {
        mAddress=new InetSocketAddress(mConfig.getIp(),mConfig.getPort());
        mConnection =new NioSocketConnector();
        mConnection.getSessionConfig().setReadBufferSize(mConfig.getReadBufferSize());
        mConnection.setConnectTimeoutMillis(mConfig.getConnectionTimeout());
        mConnection.getFilterChain().addLast("logging",new LoggingFilter());
        mConnection.getFilterChain().addLast("codec",new ProtocolCodecFilter
                (new ObjectSerializationCodecFactory()));
        mConnection.setHandler(new DefaultHandler(mContext.get()));
    }

   public boolean connect()
   {
       try
       {
           //��������
           future= mConnection.connect(mAddress);
           //һֱ�ȵ�������Ϊֹ
           future.awaitUninterruptibly();
           mSession =future.getSession();
           Log.d("Main","ConnectionMAnager���connect��msession="+mSession.toString());
       }catch (Exception e)
       {
           Log.d("Main","ConnectionManager��connect()"+e.getMessage().toString());
           return false;
       }
       return mSession == null ? false : true;
   }
    public void disConnection()
    {
        mConnection.dispose();//�ر�;
        mConnection=null;
        mSession=null;
        mAddress=null;
        mContext=null;
    }

    private static class DefaultHandler extends IoHandlerAdapter
    {

        private Context mContext;
        DefaultHandler(Context context)
        {
            this.mContext=context;
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            //��Session���浽���ǵ�SessionManager���Ӷ����Է�����Ϣ����������
            SessionManager.getmInstance().setSession(session);
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {

            if(mContext!=null)
            {
                Intent intent =new Intent(BROADCAST_ACTION);
                intent.putExtra(MESSAGE,message.toString());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
        }
    }
}
