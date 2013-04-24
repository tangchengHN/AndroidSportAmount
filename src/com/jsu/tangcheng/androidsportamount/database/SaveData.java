package com.jsu.tangcheng.androidsportamount.database;

import java.util.Calendar;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SaveData {

	public static final String PRE_NAME = "tangcheng";
	public static final String KEY_ID = "id";
	public static final String KEY_DATA = "data";
	public static final String KEY_DISTANCE = "distance";
	public static final String KEY_CALORIE = "calorie";

	private String table = "tc";
	public static int id = 1;
//	SharedPreferences preferences;
	Editor editor;

	Context context;
	SQLiteDatabase database;

	public SaveData(Context context) {
		this.context = context;

		database = new DBHelper(context).getWritableDatabase();
//		preferences = context.getSharedPreferences(PRE_NAME,
//				context.MODE_APPEND);
	}

	/*
	 * 获取系统当前时间
	 */
	public int[] judgeTime() {
		int[] time = new int[2];
		Calendar cal = Calendar.getInstance();
		time[0] = Integer.valueOf(String.valueOf(cal.get(Calendar.MONTH)+1));
		time[1] = cal.get(Calendar.DAY_OF_MONTH);
System.out.println(time[0] + "judgeTime"+ time[1]);
		return time;
	}

	/*
	 * 数据处理
	 */
	public void dealData(MyData myData, String lTime ,int newDay) {
		if (newDay == 1) {// 如果是新的一天，插入数据
			insertDB(myData);

		} else if(newDay == 2){//同一天，更新数据
			System.out.println("aaaaaaaaaaaaaaaaaaaaaaaa");
			updateDB(myData);
			
		}else if(newDay == 0){//不同月，删除上个月所有数据，插入当天数据
			deleteDB(lTime);
			insertDB(myData);

		}

	}

	/*
	 * 删除数据
	 */
	private void deleteDB(String lTime) {
		
		Log.d("hj", "删除、、、");
		String whereClause = "data = " + lTime;
		database.delete(table, whereClause , null);
	}

	/*
	 * 插入数据库
	 */
	public void insertDB(MyData myData) {
		
		
		
		Log.d("hj", "插入、、、");
		ContentValues values = new ContentValues();

		values.put(KEY_ID, id++);
		values.put(KEY_DATA, myData.getData());
		values.put(KEY_DISTANCE, myData.getDistance());
		values.put(KEY_CALORIE, myData.getCalorie());
//		System.out.println(id+"KEY_ID");
		database.insert(table, null, values);
	}

	/*
	 * 更新数据库
	 */
	public void updateDB(MyData myData) {
		ContentValues values = new ContentValues();

		values.put(KEY_ID, MyData.ids);
		values.put(KEY_DATA, myData.getData());
		values.put(KEY_DISTANCE, myData.getDistance());
		values.put(KEY_CALORIE, myData.getCalorie());
		Log.d("hj", "更新、、、");
//		database.execSQL("update " + table + " set distance = "
//				+ myData.getDistance() + ",calorie = "
//				+ myData.getCalorie() + " where data = " + myData.getData());
		
//		System.out.println("execSQL"+("update " + table + " set distance = distance + "
//				+ myData.getDistance() + ",calorie = calorie + "
//				+ myData.getCalorie() + " where data = " + myData.getData()));
		database.update(table, values, "data=?", new String[]{myData.getData()});
	}
	

}
