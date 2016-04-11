package com.dsd.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 黑名单数据库类
 * @author im_dsd
 *
 */
public class BlackNumberOpenHelper extends SQLiteOpenHelper {

	public BlackNumberOpenHelper(Context context) {
		super(context, "blacknumber.db", null, 1);
		
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建数据库中表的方法
		db.execSQL("create table blacknumber (_id integer primary key autoincrement,"
				+ "phone varchar(20),mode varchar(5));");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 数据库更新后执行的方法

	}

}
