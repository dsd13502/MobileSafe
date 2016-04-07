package com.dsd.mobilesafe.engine;

import java.io.File;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AddressDao {
	private static final String tag = "AddressDao";
	// [1]设定访问数据库路径
	public static String path = "data/data/com.dsd.mobilesafe/files/address.db";

	/**
	 * 传递电话号码，开启数据库链接，进行访问，返回一个归属地
	 * 
	 * @param phone
	 *            需要查询的电话号码
	 */
	public static void getAdress(String phone) {
		File file = new File(path);
		if (file.exists()) {
			Log.i(tag, "address.db存在");
			phone = phone.substring(0, 7);
			// [2]传递电话号码，开启数据库链接，进行访问，返回一个归属地
			// 【2.1】开始数据库链接，（只读的形式打开）
			SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.OPEN_READONLY);
			// [3]查询数据库
			Cursor cursor = db.query("data1", new String[] { "outkey" },
					"id = ?", new String[] { phone }, null, null, null);
			// [4]查到即可
			while (cursor.moveToNext()) {
				String outKey = cursor.getString(0);
				Log.i(tag, "outKey : " + outKey);

				// [5]使用outkey作为键查询data2表

				Cursor indexCursof = db.query("data2", new String[] { "location" },
						"id = ?", new String[] { outKey }, null, null, null);
				
				while(indexCursof.moveToNext())
				{
					//[6]获取电话归属地
					String address = indexCursof.getString(0);
					Log.i(tag, "address : " + address);
				}

			}
		}

	}

}
