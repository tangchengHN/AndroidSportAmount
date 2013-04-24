package com.jsu.tangcheng.androidsportamount.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

	private static int VERSION = 1;
	private static String NAME = "tc.db";
	public static final String CREAT_TABLE_TC = "create table tc(" +
			"id integer,data text,distance real,calorie real)";

	public DBHelper(Context context) {
		super(context, NAME , null, VERSION );
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREAT_TABLE_TC);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
