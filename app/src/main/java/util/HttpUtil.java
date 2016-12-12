package util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import application.MyApplication;

public class HttpUtil {

	public static void sendHttpRequest(final String address,final HttpCallbackListener listener)
	{

		new Thread(new Runnable() {

			@Override
			public void run() {

				HttpURLConnection connection=null;
				try {
					connection=(HttpURLConnection) new URL(address).openConnection();
					connection.setRequestProperty("Charset", "utf-8");
					connection.setRequestProperty("Accept-Charset", "utf-8");
					Log.d("MainFragment_Http"," UttpUtil建立连接: response="+address.toString());
					connection.setRequestMethod("GET");
					Log.d("MainFragment_Http"," UttpUtil调用: Connection.setReadTimeout=");
					InputStream in =connection.getInputStream();//容易异常
					Log.d("MainFragment_Http"," UttpUtil调用: Connection.getInputStream="+in.toString());
					BufferedReader reader =new BufferedReader(new InputStreamReader(in,"UTF-8"));
					StringBuilder response =new StringBuilder();
					String line;
				while((line=reader.readLine())!=null)
					{
						response.append(line);
						//Log.d("MainFragment_Http"," UttpUtil调用: response="+response.toString());
					}

					if(listener!=null)
					{
						listener.onFinish(response.toString());
					}

				} catch (Exception e) {

					if(listener!=null)
					{
						listener.onError(e);
					}
				}finally
				{
					if(connection!=null)
					{
						connection.disconnect();
					}
				}
			}
		}).start();
	}


}