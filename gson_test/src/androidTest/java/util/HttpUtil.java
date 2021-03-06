package util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

	public static void sendHttpRequest(final String address,final HttpCallbackListener listener)
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
			
				HttpURLConnection connection=null;
				try {
					connection=(HttpURLConnection) new URL(address).openConnection();
					Log.d("MainActivity"," UttpUtil建立连接: response="+address.toString());
					connection.setRequestMethod("GET");


					Log.d("MainActivity"," UttpUtil调用: Connection.setReadTimeout=");
					InputStream in =connection.getInputStream();//容易异常
					Log.d("MainActivity"," UttpUtil调用: Connection.getInputStream="+in.toString());
					BufferedReader reader =new BufferedReader(new InputStreamReader(in));
					StringBuilder response =new StringBuilder();
					String line;
					while((line=reader.readLine())!=null)
					{
						response.append(line);
						Log.d("MainActivity"," UttpUtil调用: response="+response.toString());
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