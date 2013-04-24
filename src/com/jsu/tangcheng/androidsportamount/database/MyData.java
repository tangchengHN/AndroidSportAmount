package com.jsu.tangcheng.androidsportamount.database;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class MyData implements BaseColumns{

	
	public static final String TABLENAME = "tc";
	public static final String KEY_ID = "id";
	public static final String KEY_DATA = "data";
	public static final String KEY_DISTANCE = "distance";
	public static final String KEY_CALORIE = "calorie";
	
	public static final String[] COLUMNS = { KEY_ID, KEY_DATA, KEY_DISTANCE, KEY_CALORIE};
	public static int ids;
	public static int[] time = new int[2];
	public static float lastcalorie;
	
	//data
	private long _id;
	public MyData(){
		
	}
	
	String data;
	float distance;
	float calorie;
	public MyData(String data, float distance, float calorie) {
		super();
		this.data = data;
		this.distance = distance;
		this.calorie = calorie;
	}
	
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
	public float getCalorie() {
		return calorie;
	}
	public void setCalorie(float calorie) {
		this.calorie = calorie;
	}
	@Override
	public String toString() {
		return "MyData [data=" + data + ", distance=" + distance + ", calorie="
				+ calorie + "]";
	}
	
	/**
	 * 查询数据库得到MyData类型的集合
	 * @param db
	 * @return
	 */
	public static List<MyData> select(SQLiteDatabase db){
		Cursor cursor = db.query(TABLENAME, COLUMNS,null,null,
				null, null, null);
		List<MyData> list = new ArrayList<MyData>();
		time = new int[2];
		while (cursor.moveToNext()) {
			ids = cursor.getInt(0);
			System.out.println("ids"+ ids);
			String a = cursor.getString(1).substring(0, cursor.getString(1).indexOf("|"));
			String b = cursor.getString(1).substring(cursor.getString(1).indexOf("|")+1, cursor.getString(1).length());
			System.out.println(a+"aaaaa  " +b);
			time[0] = Integer.valueOf(a);
			time[1] = Integer.valueOf(b);
			lastcalorie = cursor.getFloat(3);
			MyData data = new MyData(cursor.getString(1), cursor.getFloat(2), cursor.getFloat(3));
			list.add(data);
		}
		return list;
	}
	
	public long get_id() {
		return _id;
	}
	public void set_id(long _id) {
		this._id = _id;
	}
}
