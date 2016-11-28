package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class RepairOpenHelp extends SQLiteOpenHelper {

	/**
	 * APPLYS     ???? ?????
	 * CATEGORY ?????????
	 * PLACE       ?????????
	 * EMPLOYEE  ?????????
	 */
	
	
	private static final String APPLYS="create  table  applys( "
			+ "a_id  integer primary key autoincrement,"
			+ " a_no          text,  "
			+ " a_name     text ,"
			+ " a_tel         text, "
			+ " a_category    integer,"
			+ "a_place       integer,"
			+ "a_detalis     text ,"
			+ "a_describe    text,"
			+ " a_status      text default('??????'),"
			+ " a_emplopyeeId  integer,"
			+ "a_image        text," 
			+ "a_pingjia      text)";
	
	// a_image ????
	
	
	private static final String CATEGORY="create table category("
			+ "c_id  integer primary key atuoincrement,"
			+ "c_name text  ,"
			+ "c_imageurl   BLOB,"
			+ "c_priority text )";
	
	//c_imageurl ????
	
	
	private static final String PLACE="create table place("
			+ "p_id  integer primary key autoincrement,"
			+ "p_name text  )";
	
	private static final String EMPLOYEE="create table employee( "
			+ "e_id integer primary key autoincrement,"
			+ "employeeName text  ,"
			+ "fireData  text,"
			+ "e_tel  textl,"
			+ "e_can integer)";
	
	
	
	public RepairOpenHelp(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CATEGORY);
		db.execSQL(PLACE);
		db.execSQL(EMPLOYEE);
		db.execSQL(APPLYS);		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
