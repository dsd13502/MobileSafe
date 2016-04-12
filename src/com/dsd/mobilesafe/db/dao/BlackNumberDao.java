package com.dsd.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dsd.mobilesafe.db.BlackNumberOpenHelper;
import com.dsd.mobilesafe.db.daomain.BlackNumberInfo;

/**
 * 用于操作数据库的方法，在这里我们使用单利模式
 * @author im_dsd
 *
 */
public class BlackNumberDao {

	//BlackNumberDao单例模式
	//[1]构造私有化的构造按
	private BlackNumberDao(Context context)
	{
		blackNumberOpenHelper = new BlackNumberOpenHelper(context);
	}
	//[2]声明一个当前类的对象
	
	private static BlackNumberDao blackNumberDao = null;
	private BlackNumberOpenHelper blackNumberOpenHelper;
	private SQLiteDatabase db = null;
	private ArrayList<BlackNumberInfo> blackNumberList;
	//[3]提供一个方法，如果当前类的对象为空，创建一个新的
	
	public static BlackNumberDao getInstance(Context context)
	{
		if(blackNumberDao == null)
		{
			blackNumberDao = new BlackNumberDao(context);
		}
		return blackNumberDao;
	}
	
	/**
	 * 增加一个条目
	 * @param phone 拦截的电话号码
	 * @param mode 拦截类型（1：短信， 2：电话， 3：短信和电话）
	 */
	public void insert(String phone,String mode)
	{
		//【1】开启数据库，准备做写入操作
		if(db == null)
		{
			db = blackNumberOpenHelper.getWritableDatabase();
		}
		ContentValues values = new ContentValues();
		values.put("phone", phone);
		values.put("mode", mode);
		db.insert("blacknumber", null, values);
		//【end】关闭数据哭
	}
	
	/**
	 * 从数据库中删除一条电话号码
	 * @param phone
	 */
	public void delete(String phone)
	{
		if(db == null)
		{
			db = blackNumberOpenHelper.getWritableDatabase();
		}
		
		db.delete("blacknumber", "phone = ?", new String[]{phone});
	}
	
	/**
	 * 更新指定电话号码的模式
	 * @param phone 更新拦截模式的电话号码
	 * @param mode 新的模式
	 */
	public void updata(String phone,String mode)
	{
		if(db == null)
		{
			db = blackNumberOpenHelper.getWritableDatabase();
		}
		
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		db.update("blacknumber", values, "phone = ?", new String[]{phone});
	}
	
	/**
	 * 查询所有内容
	 */
	public List<BlackNumberInfo> findAll()
	{
		if(db == null)
		{
			db = blackNumberOpenHelper.getWritableDatabase();
		}
		
		//"_id desc" 按照_id 的倒叙查找
		Cursor cursor = db.query("blacknumber", new String[]{"phone","mode"},null, 
				null, null, null, "_id desc");
		blackNumberList = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext())
		{
			 BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			 
			 blackNumberInfo.setPhone(cursor.getString(0));
			 blackNumberInfo.setMode(cursor.getString(1));
			 
			 blackNumberList.add(blackNumberInfo);
			 
		}
		
		cursor.close();
		
		return blackNumberList;
	}
}
