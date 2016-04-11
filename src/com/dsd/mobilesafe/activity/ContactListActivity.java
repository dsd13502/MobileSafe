package com.dsd.mobilesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dsd.mobilesafe.R;
import com.lidroid.xutils.view.annotation.event.OnItemClick;

public class ContactListActivity extends Activity {

	private ListView lv_contact_list;
	protected static final String tag = "ContactListActivity";

	private List<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();
	private MyAdapter myAdapter = null;
	/**
	 * 建立消息队列，获取子线程的消息
	 */
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			myAdapter = new MyAdapter();
			lv_contact_list.setAdapter(myAdapter);

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);

		initUI();
		initDate();
	}

	private void initUI() {
		lv_contact_list = (ListView) findViewById(R.id.lv_contact_list);

		// 设置点击事件
		lv_contact_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// [1] 获取点中条目的索引指向的集合中的对象
				if (myAdapter != null) {
					//[2]获取当前条目集合对应的电话号码
					HashMap<String,String> hashMap = myAdapter.getItem(position);
					//[3]此电话号码需要给第三个导航界面使用
					String phone = hashMap.get("phone");
					Intent intent = new Intent();
					intent.putExtra("phone", phone);
					setResult(0, intent);
					
					finish();
				}
			}
		});
	}

	/**
	 * 获得联系人数据方法
	 */
	private void initDate() {
		// 因为读取系统联系人，可能是一个耗时操作，所以要放到子线程中

		new Thread() {
			public void run() {
				// [1]获取内容解析者。
				ContentResolver contentResolver = getContentResolver();
				// 【2】做查询系统联系人数据库标的过程（记得加 读取联系人的权限）
				Cursor query = contentResolver.query(Uri
						.parse("content://com.android.contacts/raw_contacts"),
						new String[] { "contact_id" }, null, null, null);
				// 循环游标，直到没有数据为止

				// 因为contactList是全局的，所以一开始可能里面有数据，所以要清空一下，防止出现数据重复
				contactList.clear();

				while (query.moveToNext()) {
					String id = query.getString(0);
					Cursor indexCursor = contentResolver.query(
							Uri.parse("content://com.android.contacts/data"),
							new String[] { "data1", "mimetype" },
							"raw_contact_id = ?", new String[] { id }, null);
					// 循环获得每一个联系人的电话号码或者姓名，

					// 每次找到一个id就创建一个HashMap用于存储的姓名，电话号码，（一个HashMap对应一个姓名，和一个电话号码）
					HashMap<String, String> hashMap = new HashMap<String, String>();

					while (indexCursor.moveToNext()) {
						String data = indexCursor.getString(0);
						String type = indexCursor.getString(1);

						if (type.equals("vnd.android.cursor.item/name")) {
							if (!TextUtils.isEmpty(data)) {
								hashMap.put("name", data);
								
							}
						} else if (type
								.equals("vnd.android.cursor.item/phone_v2")) {
							if (!TextUtils.isEmpty(data)) {
								hashMap.put("phone", data);
								
							}
						}
					}
					indexCursor.close();

					contactList.add(hashMap);
					
				}

				query.close();

				// 使用消息机制，告诉主线程，数据已经准备完成了，可以使用Adapter了

				// 因为就一个消息，所以可以不用匹配消息类型了。
				mHandler.sendEmptyMessage(0);

			}
		}.start();

	}

	/**
	 * 联系人列表的数据适配器
	 * 
	 * @author im_dsd
	 * 
	 */
	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return contactList.size();
		}

		@Override
		public HashMap<String, String> getItem(int position) {
			// TODO Auto-generated method stub
			return contactList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder holder = null;

			if (convertView != null) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.listview_contact_item, null);
				holder = new ViewHolder();
				holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
				holder.tv_phone = (TextView) view.findViewById(R.id.tv_phone);
				holder.tv_name.setText(getItem(position).get("name"));
				holder.tv_phone.setText(getItem(position).get("phone"));
				view.setTag(holder);
			}
			return view;
		}

	}

	static class ViewHolder {
		TextView tv_name ;
		TextView tv_phone;
	}

}
