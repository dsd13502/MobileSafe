package com.dsd.mobilesafe.engine;

import java.io.File;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AddressDao {
	private static final String tag = "AddressDao";
	// [1]设定访问数据库路径
	public static String path = "data/data/com.dsd.mobilesafe/files/address.db";
	private static String mAddress = "未知电话";

	/**
	 * 传递电话号码，开启数据库链接，进行访问，返回一个归属地
	 * 
	 * @param phone
	 *            需要查询的电话号码
	 */
	public static String getAdress(String phone) {

		// 正则标的式，匹配手机号码
		// 手机的正则的表达式
		String regularExpression = "^1[3-8]\\d{9}";
		if (phone.matches(regularExpression)) {
			File file = new File(path);
			if (file.exists()) {
				
				phone = phone.substring(0, 7);
				// [2]传递电话号码，开启数据库链接，进行访问，返回一个归属地
				// 【2.1】开始数据库链接，（只读的形式打开）
				SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
						SQLiteDatabase.OPEN_READONLY);
				// [3]查询数据库
				Cursor cursor = db.query("data1", new String[] { "outkey" },
						"id = ?", new String[] { phone }, null, null, null);
				// [4]查到即可
				if (cursor.moveToNext()) {
					String outKey = cursor.getString(0);
					Log.i(tag, "outKey : " + outKey);

					// [5]使用outkey作为键查询data2表

					Cursor indexCursof = db.query("data2",
							new String[] { "location" }, "id = ?",
							new String[] { outKey }, null, null, null);

					if (indexCursof.moveToNext()) {
						mAddress = indexCursof.getString(0);
						Log.i(tag, "address : " + mAddress);
					}

				}
				else
				{
					int length = phone.length();
					switch (length) {
					case 3: //110 119
						mAddress = "报警电话";
						break;
					case 4: //110 119
						mAddress = "模拟器";
						break;
					default:
						break;
					}
				}
			}
			else
			{
				Log.i(tag, "address.db不存在");
			}
		}
		else
		{
			int length = phone.length();
			switch (length) {
			case 3:
				mAddress="报警电话";
				
				break;
			case 4:
				mAddress="模拟器";
				
				break;
			}
		}

		return mAddress;
	}

}
