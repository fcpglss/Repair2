package util;

import android.util.Log;

import com.google.gson.Gson;

import model.Apply;
import model.ResultBean;


public class JsonUtil {

//	private static final int APPLY=1;
//	private static final int CATEGORY=2;
//	private static final int PLACE=3;
//	private static final int ANNONCEMENT=4;
//
//
//	private static List<Place> list_place=new ArrayList<>();
//	private static List<Category> list_category=new ArrayList<>();
//	private static List<Apply> list_apply=new ArrayList<>();
//	private static List<Announcement> list_anouncement=new ArrayList<>();
//	private static List<Employee> list_employee=new ArrayList<>();

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
			System.out.println("传入json不能解析出ResultBean:\n"+e.getMessage().toString());
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
			Log.e("Apply_Fragment", "beanToJson()解析有误:" + e.getMessage().toString());
		}

		return json;
	}
}
