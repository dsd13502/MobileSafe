package com.dsd.mobilesafe.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建App加锁数据库
 * 
 * @author im_dsd
 * 
 */
public class AppLockOpenHelder extends SQLiteOpenHelper {

	//创建数据库
	public AppLockOpenHelder(Context context) {
		super(context, "appLock.db", null, 1);

	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table applock " +
				"(_id integer primary key autoincrement , packagename varchar(50));");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
