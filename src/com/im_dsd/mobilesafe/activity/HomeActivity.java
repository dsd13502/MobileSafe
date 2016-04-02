package com.im_dsd.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobilesafe.R;

public class HomeActivity extends Activity {

	private String[] mTitleStrs = null;
	private int[] mDrawableIds = null;
	private GridView gv_home;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		// 初始化控件
		initUI();
		// 初始化数据
		initDate();
		
		
	}

	/**
	 * 初始化控件
	 */
	private void initUI() {
		gv_home = (GridView) findViewById(R.id.gv_home);
				
	}

	/**
	 * 初始化数据
	 */
	private void initDate() {
		// 准备数据(文字(9组),图片(9张))
		mTitleStrs = new String[] { "手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计",
				"手机杀毒", "缓存清理", "高级工具", "设置中心" };

		mDrawableIds = new int[] { R.drawable.home_safe,
				R.drawable.home_callmsgsafe, R.drawable.home_apps,
				R.drawable.home_taskmanager, R.drawable.home_netmanager,
				R.drawable.home_trojan, R.drawable.home_sysoptimize,
				R.drawable.home_tools, R.drawable.home_settings };
		
		//设置数据适配
		gv_home.setAdapter(new MyAdapter());

		//设置点击事件
		gv_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO 添加点中条目的索引
				switch (position) {
				case 0:
					
					break;
				case 1:
					
					break;
				case 2:
					
					break;
				case 3:
					
					break;
				case 4:
					
					break;
				case 5:
					
					break;
				case 6:
					
					break;
				case 7:
					
					break;
			
				//点击设置中心，跳转至SettigActivity
				case 8:
					Intent intent = new Intent(HomeActivity.this,SettingActivity.class);
					startActivity(intent);
					break;
				}
				
			}
		});
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mTitleStrs.length;
		}

		@Override
		public Object getItem(int position) {
			return mTitleStrs[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder holder = null;

			if (convertView != null) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			else
			{
				holder = new ViewHolder();
				view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
				holder.tv = (TextView) view.findViewById(R.id.tv_title);
				holder.iv = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tv.setText(mTitleStrs[position]);
				holder.iv.setBackgroundResource(mDrawableIds[position]);
				view.setTag(holder);
				
			}
			return view;
		}

	}

	private static class ViewHolder {
		TextView tv;
		ImageView iv;
	}
}
