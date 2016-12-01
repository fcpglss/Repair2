package db;

import java.util.ArrayList;
import java.util.List;

import model.Applyss;
import model.Category;
import model.Employee;
import model.Place;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RepairDB {
	
	public static final String DB_NAME="cz_db";
	
	private static final String APPLYSS="applys";
	
	private static final String CATEGORY="category";
	
	private static final String PLACE="place";
	
	private static final String EMPLOY="employee";
	
	
	
	public static final int  VERSION=1;
	
	private static RepairDB repairDB;
	
	private SQLiteDatabase sqLiteDatabase;
	
	/**
	 * 单例模式
	 */
	private RepairDB(Context context)
	{
		RepairOpenHelp repairOpenHelp =new RepairOpenHelp(context, DB_NAME, null, VERSION);
		sqLiteDatabase=repairOpenHelp.getWritableDatabase();
	}
	
	/**
	 * 获取RepairDB对象的方法
	 */
	public synchronized static RepairDB getInstance(Context context)
	{
		if(repairDB==null)
		{
			repairDB =new RepairDB(context);
		}
		
		return repairDB;
	}

	/**
	 * 将Applyss对象存进数据库中
	 * @param applyss
	 */
	
	public void saveApplyss(Applyss applyss)
	{
		if(applyss!=null)
		{
			ContentValues values = new ContentValues();
			values.put("a_no", applyss.getA_no());
			values.put("a_name", applyss.getA_name());
			values.put("a_tel", applyss.getA_tel());
			values.put("a_category", applyss.getA_category());
			values.put("a_place", applyss.getA_place());
			values.put("a_detalis", applyss.getA_detalis());
			values.put("a_describe", applyss.getA_describe());
			values.put("a_status", applyss.getA_status());
			values.put("a_employeeId", applyss.getA_emplopyeeId());
			values.put("a_image", applyss.getA_image());
			values.put("a_pingjia", applyss.getA_pingjia());
			
		sqLiteDatabase.insert(APPLYSS, null , values);
		}
		
	}
	
	/**
	 * 从数据库中读取所有的 Applyss的信息
	 */
	public List<Applyss> loadApplysses()
	{
		List<Applyss> list = new ArrayList<Applyss>();
		
		Cursor cursor=sqLiteDatabase.query(APPLYSS, null, null, null, null, null, null);
		if(cursor.moveToFirst())
		{
		do {
			
			Applyss applyss =new Applyss();
			applyss.setA_id(cursor.getInt(cursor.getColumnIndex("a_id")));
			
			applyss.setA_no(cursor.getString(cursor.getColumnIndex("a_no")));
			
			applyss.setA_name(cursor.getString(cursor.getColumnIndex("a_name")));
			
			applyss.setA_tel(cursor.getString(cursor.getColumnIndex("a_tel")));
			
			applyss.setA_category(cursor.getInt(cursor.getColumnIndex("a_category")));
			
			applyss.setA_describe(cursor.getString(cursor.getColumnIndex("a_describe")));
			
			applyss.setA_place(cursor.getInt(cursor.getColumnIndex("a_place")));
			
			applyss.setA_detalis(cursor.getString(cursor.getColumnIndex("a_detalis")));
			
			applyss.setA_image(cursor.getString(cursor.getColumnIndex("a_image")));
			
			applyss.setA_emplopyeeId(cursor.getInt(cursor.getColumnIndex("a_employeeId")));
			
			applyss.setA_pingjia(cursor.getString(cursor.getColumnIndex("a_pingjia")));
			
			applyss.setA_status(cursor.getString(cursor.getColumnIndex("a_status")));
			
			list.add(applyss);
		} while (cursor.moveToNext());
		
		}
		return list;
	}
	
	/**
	 * 将Category对象存入数据库
	 */
	public void saveCategory(Category category)
	{
		if(category !=null)
		{
			ContentValues values = new ContentValues();
			values.put("c_name", category.getC_name());
			values.put("c_priority", category.getC_priority());
			
			sqLiteDatabase.insert(CATEGORY, null, values);	
		}
	}
	
	/**
	 * 从数据库读取Category的信息
	 */
	public List<Category> loadCategories()
	{
		List<Category> list =new ArrayList<Category>();
		
		Cursor cursor =sqLiteDatabase.query (CATEGORY, null, null, null, null, null ,null);
		if(cursor.moveToFirst())
		{
			do {
				
				Category category =new Category();
				category.setC_id(cursor.getInt(cursor.getColumnIndex("c_id")));
				category.setC_name(cursor.getString(cursor.getColumnIndex("c_name")));
				category.setC_priority(cursor.getString(cursor.getColumnIndex("c_priority")));
				
				list.add(category);
				
			} while (cursor.moveToNext());
		}
		
		return list;
	}
	
	public void savePlace(Place place)
	{
		if(place!=null)
		{
		ContentValues values =new ContentValues();
	    values.put("p_name", place.getP_name());
	
	   sqLiteDatabase.insert(PLACE, null, values);
		}
	}
	
	public List<Place> loadPlaces()
	{
		List<Place> list =new ArrayList<Place>();
		Cursor cursor =sqLiteDatabase.query(PLACE, null, null, null, null, null, null);
		if(cursor.moveToFirst())
		{
			do {
				Place place =new Place();
				
				place.setP_id(cursor.getInt(cursor.getColumnIndex("p_id")));
				
				place.setP_name(cursor.getString(cursor.getColumnIndex("p_name")));
				
				list.add(place);
			} while (cursor.moveToNext());
		}	
		return list;
		
	}
	
	public void saveEmployee(Employee employee)
	{
		if(employee!=null)
		{
			ContentValues values =new ContentValues();
			values.put("employeeName", employee.getEmployeeName());
			values.put("fireData", employee.getFireData());
			values.put("e_tel", employee.getE_tel());
			values.put("e_can", employee.getE_can());
			sqLiteDatabase.insert(EMPLOY, null, values);
		}
	}
	public List<Employee> loadEmployess()
	{
		List<Employee> list =new ArrayList<Employee>();
		
		Cursor cursor =sqLiteDatabase.query(EMPLOY, null, null, null, null, null, null);
		if(cursor.moveToFirst())
		{
			do {
				Employee employee  =new Employee();
				employee.setE_id(cursor.getInt(cursor.getColumnIndex("e_id")));
				employee.setEmployeeName(cursor.getString(cursor.getColumnIndex("employeeName")));
				employee.setE_tel(cursor.getString(cursor.getColumnIndex("e_tel")));
				employee.setE_can(cursor.getInt(cursor.getColumnIndex("e_can")));
				list.add(employee);
				
			} while (cursor.moveToNext());
		}
		
		
		return list;
	}
	

}
