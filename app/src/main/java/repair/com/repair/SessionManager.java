package repair.com.repair;


import android.util.Log;

import org.apache.mina.core.session.IoSession;

/**
 * Created by Administrator on 2016-12-6.
 */

public class SessionManager {
    private static SessionManager mInstance =null;
    /**
     * �����������ͨ�ŵĶ���
     */
    private IoSession mSession;

    public static SessionManager getmInstance()
    {
        if(mInstance==null)
        {
            synchronized (SessionManager.class){
                if(mInstance==null){
                    mInstance=new SessionManager();
                }
            }
        }
        return mInstance;
    }
    private SessionManager()
    {

    }
    public void setSession(IoSession session)
    {
        this.mSession=session;
    }

    /**
     * ������д����������
     * @param msg
     */
    public boolean writeToServer(Object msg)
    {
        if(mSession!=null)
        {
            mSession.write(msg);
            return true;
        }
        else
        {
            return false;

        }
    }
    public void closeSession()
    {
        if(mSession!=null)
        {
          mSession.close();
        }
    }
    public void removeSession()
    {
        this.mSession=null;
    }
}
