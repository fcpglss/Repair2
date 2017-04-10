package util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;

import model.Apply;
import model.Area;
import model.Category;
import model.Flies;
import model.Place;
import model.Response;
import model.ResultBean;
import model.Room;


public class Util {

	private static final String TAG = "Util";
	
	public static int convertToInt(Object value,int defaultValue)
	{
		if(value!=null || "".equals(value.toString().trim()))
		{
			return defaultValue;
		}
		try {
			return Integer.valueOf(value.toString());
		} catch (Exception e) {
			try {
				return Double.valueOf(value.toString()).intValue();
				
			} catch (Exception e2) {
				return defaultValue;
			}
		}	
	}
	public static String convertToString(Object value,String defaultString)
	{
		if(value!=null || "".equals(value.toString().trim()))
		{
			return defaultString;
		}
		try {
			return value.toString();
		} catch (Exception e) {
			
			return defaultString;
		}	
		
	}
	public static int getAreaId(String areaName,List<Area> listArea)
	{
		if(areaName !=null &&!areaName.equals(""))
		{
			for(Area area:listArea)
			{
				if(area.getArea().equals(areaName))
					return area.getId();
			}
			return -2;
		}
		return -2;
	}
	/**
	 * ����¥������ȡ��¥�ŵ�ID
	 * @param detailAreaName ,¥����
	 * @param listPlace  ,��DetailArea�л�ȡ������
	 * @return
	 */
	public static int getDetailAreaId(String detailAreaName,List<Place> listPlace)
	{
		if(detailAreaName !=null &&!detailAreaName.equals(""))
		{
			for(Place place:listPlace)
			{
				
				return place.getP_id();
			}
			return -2;
		}
		return -2;
	}
	public static int getCategoryId(String className,List<Category> listCategory)
	{
		if(className !=null &&!className.equals(""))
		{
			for(Category category2:listCategory)
			{
				if(category2.getC_name().equals(className))
					return category2.getC_id();
			}
			return -2;
		}
		return -2;
	}
	/**
	 * ���ݲ����,¥��ID,��ȡ���ID
	 * @param placeId ¥�ŵ�ID
	 * @param fliesName �����
	 * @param listFlies  Flies���е�����
	 * @return
	 */
	public static int getFlies(int placeId,String fliesName,List<Flies> listFlies)
	{
		if(fliesName!=null && !fliesName.equals(""))
		{
			for(Flies flies : listFlies)
			{
				if(placeId==flies.getaFloor())
				{
					if(fliesName.equals(flies.getFlies()))
					{
						return flies.getId();
					}
				}
				
			}
			return 0;
		}
		else
		{
			return 0;
		}
		
	}
	public static int getRoomId(int fliesId,String roomName,List<Room> listRoom)
	{
		if(fliesId==0)
		{
			return 0;
		}
		if(roomName!=null && !roomName.equals(""))
		{
			for(Room romm : listRoom)
			{
				if(fliesId==romm.getFlies())
				{
					if(roomName.equals(romm.getRoomNumber()))
					{
						return romm.getId();
					}
				}
				
			}
			return 0;
		}
		else
		{
			return 0;
		}
	}
	//为放置flies层号，房间号为null设置
	public static  String setTitle(Apply apply)
	{
		String address="";
		String flies=apply.getFlies();
		String room=apply.getRoom();
		if(flies!=null&&!flies.equals("")) {
			address = apply.getArea() + " " + apply.getDetailArea() + flies;
			if (room != null && !room.equals(""))
			{
				address = apply.getArea() + " " + apply.getDetailArea() + flies+apply.getRoom();
			}
		}
		else
		{
			address=apply.getArea()+" "+apply.getDetailArea(); //没有层号的时候 可在后面加其他地址
		}
		return address;
	}
	public static  String setAddress(Apply apply)
	{
		String address="";
		String flies=apply.getFlies();
		String room=apply.getRoom();
		if(flies!=null&&!flies.equals("")) {
			address =  apply.getDetailArea() + flies;
			if (room != null && !room.equals(""))
			{
				address =  apply.getDetailArea() + flies+apply.getRoom();
			}
		}
		else
		{
			address=apply.getDetailArea()+""; //没有层号的时候 可在后面加其他地址
		}
		return address;
	}
	public static String  errorMessage(Response response)
	{
		switch (response.getErrorType())
		{
			case -1:
				return "连接服务器超时或者网络不通";
			case -2:
				return "连接成功，但是服务器返回数据为空或异常";
			default:
				return "";
		}

	}

	public  static void writeJsonToLocal(final String jsonString, final Context mContext) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String json = jsonString;
				SharedPreferences.Editor editor = mContext.getSharedPreferences("json_data", mContext.MODE_PRIVATE).edit();
				editor.putString("json", json);
				editor.commit();
			}
		}).start();

	}
	//将服务器第一次获取到的数据，写到文件json_data中，key为json
	public  static void writeJsonToLocal(final ResultBean resultBean, final Context mContext) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String json = JsonUtil.beanToResultBean(resultBean);
				SharedPreferences.Editor editor = mContext.getSharedPreferences("json_data", mContext.MODE_PRIVATE).edit();
				editor.putString("json", json);
				editor.apply();
				Log.d(TAG, "writeJsonToLocal: 成功将FirstRequest的Json写入本地json_data文件中，key:json");
			}
		}).start();

	}
	//将从服务器获取到的数据写入address_data文件中，key为address
	public  static void writeAddressToLocal(final ResultBean resultBean, final Context mContext) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String json = JsonUtil.beanToResultBean(resultBean);
				SharedPreferences.Editor editor = mContext.getSharedPreferences("address_data", mContext.MODE_PRIVATE).edit();
				editor.putString("address", json);
				editor.apply();
				Log.d(TAG, "writeAddressToLocal: 成功将address的Json写入本地address_data文件中，key:address");
			}
		}).start();
	}
	//从address_data文件中读出地点相关的字符串
	public static String loadAddressFromLocal(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences("address_data", context.MODE_PRIVATE);
		String json = preferences.getString("address", "");

		Log.d(TAG, "loadAddressFromLocal:从本地address_data文件中读出json: "+json);
		return json;
	}
	public static String loadFirstFromLocal(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences("json_data", context.MODE_PRIVATE);
		String json = preferences.getString("json", "");

		Log.d(TAG, "loadFirstFromLocal: 从本地文件json_data中读出json:"+json);
		return json;
	}
	public  static void writeMyResToLocal(final ResultBean resultBean, final Context mContext) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String json = JsonUtil.beanToResultBean(resultBean);
				SharedPreferences.Editor editor = mContext.getSharedPreferences("myrepair_data", mContext.MODE_PRIVATE).edit();
				editor.putString("myrepair", json);
				editor.apply();
				Log.d(TAG, "writeJsonToLocal: 成功将MyRepair的Json写入本地myrepair_data文件中，key:myrepair");
			}
		}).start();

	}
	public static String loadMyResFromLocal(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences("myrepair_data", context.MODE_PRIVATE);
		String json = preferences.getString("myrepair", "");
		Log.d(TAG, "loadFirstFromLocal: 从本地文件myrepair_data中读出json:"+json);
		return json;
	}

	/**
	 * 将datetime类型的数据只精确到秒
	 * @param datetime
	 * @return
	 */
	public static  String setTime(String datetime) {

		if (datetime != null && !datetime.equals(""))
		{
			return datetime.split(":")[0]+":"+datetime.split(":")[1];
		}
		return "";
	}
}
