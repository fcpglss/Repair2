package util;

import android.util.Log;

import com.google.gson.Gson;


import model.Apply;
import model.Response;

import model.ResultBean;


public class JsonUtil {


	private static final String TAG = "JsonUtil";




	public static ResultBean jsonToBean(String jsonString)
	{

		try 
		{
			if(jsonString!=null&&jsonString.length()>0)
			{
				Gson gson =new Gson();
				ResultBean bean =gson.fromJson(jsonString, ResultBean.class);

				return bean;
			}
		} 
		catch (Exception e) {

			Log.d(TAG, "jsonToBean: "+e.getMessage().toString());
		}
		return null;
	}
	public static String beanToJson(Apply apply)
	{
		String json = "";
		Gson gson = new Gson();
		try {
			json = gson.toJson(apply, Apply.class);
		} catch (Exception e) {
			Log.d(TAG, "beanToJson: "+e.getMessage().toString());
		}

		return json;
	}
	public static String beanToResultBean(ResultBean resultbean)
	{
		String json = "";
		Gson gson = new Gson();
		try {
			json = gson.toJson(resultbean, ResultBean.class);
		} catch (Exception e) {
			Log.d(TAG, "beanToResultBean: "+e.getMessage().toString());
		}

		return json;
	}


	public static Response jsonToResponse(String json)
	{

		try
		{
			if(json!=null&&json.length()>0)
			{
				Gson gson =new Gson();
				Response response =gson.fromJson(json, Response.class);

				return response;
			}
		}
		catch (Exception e) {

			Log.d(TAG, "jsonToResponse: "+e.getMessage().toString());
		}
		return null;
	}

}
