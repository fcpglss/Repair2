package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import repair.com.repair.MainActivity;

public class HttpUtil {

	private static final String TAG = "HttpUtil";
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener)
	{

		new Thread(new Runnable() {

			@Override
			public void run() {

				HttpURLConnection connection=null;
				InputStream in=null;
				BufferedReader reader=null;
				try {
					String data= "count="+MainActivity.count;
					connection=(HttpURLConnection) new URL(address).openConnection();
					connection.setRequestProperty("Charset", "utf-8");
					connection.setRequestProperty("Accept-Charset", "utf-8");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					connection.setDoInput(true);
					connection.setRequestMethod("GET");
					if(connection.getResponseCode()==200) {
						 in= connection.getInputStream();//容易异常
						 reader= new BufferedReader(new InputStreamReader(in, "UTF-8"));
						StringBuilder response = new StringBuilder();
						String line;
						while ((line = reader.readLine()) != null) {
							response.append(line);
						}
						if (listener != null) {
							listener.onFinish(response.toString());
						}
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
					if(in!=null)
					{
						try {
							in.close();
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}

}