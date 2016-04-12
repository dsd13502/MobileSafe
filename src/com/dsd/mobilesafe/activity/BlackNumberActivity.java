package com.dsd.mobilesafe.activity;

import java.util.List;

import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.R.layout;
import com.dsd.mobilesafe.db.dao.BlackNumberDao;
import com.dsd.mobilesafe.db.daomain.BlackNumberInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class BlackNumberActivity extends Activity {

	private Button bt_add;
	private ListView lv_blacknumer;
	private List<BlackNumberInfo> mBlackNumberList;
	private static final String tag = "BlackNumberActivity";
	private int mode = 1;
	private BlackNumberDao mDao;
	private MyAdapter myAdapter;
	
	private Handler mHandler = new Handler() {
		

		public void handleMessage(android.os.Message msg) {
			myAdapter = new MyAdapter();
			Log.i(tag, "phone :" + mBlackNumberList.get(0).getPhone() + "mode : "
					+ mBlackNumberList.get(0).getMode());
			lv_blacknumer.setAdapter(myAdapter);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_number);

		initUI();
		initData();
	}

	private void initData() {

		// 获取数据中的所有电话号码
		new Thread() {

			

			public void run() {
				mDao = BlackNumberDao
						.getInstance(getApplicationContext());
				mBlackNumberList = mDao.findAll();

				mHandler.sendEmptyMessage(0);
			};

		}.start();

	}

	private void initUI() {
		bt_add = (Button) findViewById(R.id.bt_add);
		lv_blacknumer = (ListView) findViewById(R.id.lv_black);

		bt_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showDialog();
			}

		});

	}

	/**
	 * 显示自定义对话框
	 */
	private void showDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		
		View view = View.inflate(getApplicationContext(), R.layout.dialog_add_blacknumber, null);
		
		RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
		final EditText et_phone = (EditText) view.findViewById(R.id.et_phone_number);
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		
		//监听RadioGroup的选中状态
		rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_sms:
					//拦截短信
					mode = 1;
					break;
				case R.id.rb_phone:
					//拦截电话
					mode = 2;
					break;
				case R.id.rb_all:
					//拦截全部
					mode = 3;
					break;

					
				default:
					break;
				}
			}
		});
		
		//确定按钮的监听 
		bt_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//[1]获取电话号码
				String phone = et_phone.getText().toString().trim();
				
				if(!TextUtils.isEmpty(phone))
				{
					//[2]插入数据
					mDao.insert(phone, mode+"");
					//****************☆☆☆☆注意list与数据库的同步数据************
					//[3]跟新数据库和集合同步。（方法：1.从数据库中重新读取一遍数据，2，手动向集合中插入）
					BlackNumberInfo info = new BlackNumberInfo();
					info.setPhone(phone);
					info.setMode(mode+"");
					
					//[4]将对象插入集合的最顶部
					mBlackNumberList.add(0,info);
					//[5]发送消息让Adapter刷新
					if(myAdapter != null)
					{
						myAdapter.notifyDataSetChanged();
					}
					
					//[6]隐藏对话框
					dialog.dismiss();
				}else
				{
					Toast.makeText(getApplicationContext(), "请输入拦截号码", 0).show();
				}
				
			}
		});
		
		//取消按钮的监听
		bt_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setView(view,0,0,0,0);
		dialog.show();
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mBlackNumberList.size();
		}

		@Override
		public BlackNumberInfo getItem(int position) {
			return mBlackNumberList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder holder = null;

			if (convertView != null) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				holder = new ViewHolder();
				view = View.inflate(getApplicationContext(),
						R.layout.listview_blacknumber_item, null);
				holder.tv_phone = (TextView) view.findViewById(R.id.tv_phone);
				holder.tv_mode = (TextView) view.findViewById(R.id.tv_mode);
				holder.iv_delete = (ImageView) view
						.findViewById(R.id.iv_delete);
				view.setTag(holder);
			}

			holder.tv_phone.setText(getItem(position).getPhone().toString()
					.trim());

			int mode = Integer.parseInt(getItem(position).getMode().toString()
					.trim());
			String str = null;
			switch (mode) {
			case 1:
				str = "短信";
				break;
			case 2:
				str = "电话";
				break;
			case 3:
				str = "所有";
				break;
			}
			holder.tv_mode.setText(str);

			return view;
		}

	}

	private static class ViewHolder {
		TextView tv_phone = null;
		TextView tv_mode = null;
		ImageView iv_delete = null;
	}
}
