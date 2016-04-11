package com.dsd.mobilesafe.test;

import java.util.List;

import com.dsd.mobilesafe.db.dao.BlackNumberDao;
import com.dsd.mobilesafe.db.daomain.BlackNumberInfo;

import android.test.AndroidTestCase;

public class Test extends AndroidTestCase {

	public void insertTest()
	{
		//getContext() 仅仅由做测试的上下文
		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		dao.insert("110", "1");
		
	}
	
	public void deleteTest()
	{
		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		dao.delete("110");
	}
	
	public void updateTest()
	{
		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		dao.updata("110", "2");
	}
	
	public void findAllTest()
	{
		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		List<BlackNumberInfo> findAll = dao.findAll();
		
	}
}
