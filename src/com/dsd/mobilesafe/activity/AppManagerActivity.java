package com.dsd.mobilesafe.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.R.id;
import com.dsd.mobilesafe.R.layout;
import com.dsd.mobilesafe.db.daomain.AppInfo;
import com.dsd.mobilesafe.engine.AppInfoProvider;
import com.dsd.mobilesafe.utils.ToastUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

@SuppressLint("NewApi")
public class AppManagerActivity extends Activity  implements OnClickListener{

	private String availSpace;
	private String sdAvailSpace;
	private TextView tv_memory;
	private TextView tv_sd_memory;
	private ListView lv_list_app;
	private List<AppInfo> mAppInfoList = null;
	private List<AppInfo> mSystemAppList = null;
	private List<AppInfo> mCustomerAppList = null;
	private AppInfo appInfo;
	private MyAdapter myAdapter = null;
	private Handler mHander = new Handler() {
		

		public void handleMessage(android.os.Message msg) {
			if(myAdapter != null)
			{
				myAdapter.notifyDataSetChanged();
			}else
			{
				
				myAdapter = new MyAdapter();
			}
			lv_list_app.setAdapter(myAdapter);
		};
		
	};
	private TextView tv_app_title;
	private String tag = "AppManagerActivity";
	private PopupWindow popupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);

		intUI();
		initTitle();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(tag, "onResume");
		// 初始化数据，和重新获取数据。
		initAppData();
		
		
	}
	private void initAppData() {
		new Thread() {
			@Override
			public void run() {
				if(mAppInfoList != null)
				{
					mAppInfoList.clear();
				}
				mAppInfoList = AppInfoProvider
						.getAppInfoList(getApplicationContext());
				mSystemAppList = new ArrayList<AppInfo>();
				mCustomerAppList = new ArrayList<AppInfo>();
				for (AppInfo info : mAppInfoList) {
					if (info.isSystem) {
						mSystemAppList.add(info);
					} else {
						mCustomerAppList.add(info);
					}

				}
				mHander.sendEmptyMessage(0);
			}
		}.start();
	}

	private void intUI() {

		tv_memory = (TextView) findViewById(R.id.tv_memory);
		tv_sd_memory = (TextView) findViewById(R.id.tv_sd_memory);
		lv_list_app = (ListView) findViewById(R.id.lv_list_app);
		tv_app_title = (TextView) findViewById(R.id.tv_app_title);
		
		lv_list_app.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				//AbsListView就是
				
			if(mSystemAppList != null & mCustomerAppList != null)
			{
				if(firstVisibleItem >= mCustomerAppList.size() + 1)
				{
					tv_app_title.setText("系统应用("+mSystemAppList.size()+")");
				}
				else
				{
					tv_app_title.setText("用户应用("+mCustomerAppList.size()+")");
				}
			}
			}
		});
		
		
		lv_list_app.setOnItemClickListener(new OnItemClickListener() {

			

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if (position == 0 || position == mCustomerAppList.size() + 1) {
					// 返回灰色条目的状态码
					return ;
				} else {
					if (position < mCustomerAppList.size() + 1) {
						appInfo = mCustomerAppList.get(position - 1);
					} else {
						// 使用系统应用的集合（索引 - 用户应用的条目数 - 灰色条目数）
						appInfo = mSystemAppList.get(position
								- mCustomerAppList.size() - 2);
					}
					showPopupWindow(view);
				}
				
				
			}
		});
		
	}

	/**
	 * 
	 */
	private void showPopupWindow(View view) {

		
		AlphaAnimation alpha = new AlphaAnimation(0, 1);
		alpha.setDuration(1000);
		//保留动画最终的位置
		alpha.setFillAfter(true);
		
		ScaleAnimation scale = new ScaleAnimation(
				0, 1,
				0, 1,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(alpha);
		animationSet.addAnimation(scale);
		
		
		View popupView = View.inflate(getApplicationContext(), R.layout.popupwindow_layout, null);
		TextView tv_uninstall = (TextView) popupView.findViewById(R.id.tv_uninstall);
		TextView tv_share = (TextView) popupView.findViewById(R.id.tv_share);
		TextView tv_start = (TextView) popupView.findViewById(R.id.tv_start);
		
		tv_uninstall.setOnClickListener(this);
		tv_share.setOnClickListener(this);
		tv_start.setOnClickListener(this);
		
		popupWindow = new PopupWindow(popupView ,
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT,true);
		//设置一个透明窗体
		popupWindow.setBackgroundDrawable(new ColorDrawable());
		//指定窗体位置
		popupWindow.showAsDropDown(view, view.getWidth()/3, - view.getHeight() );
		
		//只要开启view的动画就可以了，不用加在popupWindow上
		popupView.startAnimation(animationSet);
		
		
	}

	/**
	 * 初始化手机剩余空间
	 */
	private void initTitle() {
		// 【1】获取手机磁盘（内存）可用大小，磁盘的路径
		String path = Environment.getDataDirectory().getAbsolutePath();
		// 【2】获取sdk卡的可用大小，sd卡路径
		String sdPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		Log.i(tag, sdPath);
		availSpace = getAvailSpace(path);
		sdAvailSpace = getAvailSpace(sdPath);

		tv_memory.setText("内存可用空间：" + availSpace);
		tv_sd_memory.setText("sd卡可用空间：" + sdAvailSpace);
	}

	/**
	 * 获取可用空间的大小
	 * 
	 * @param path
	 *            文件的路径
	 * @return
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private String getAvailSpace(String path) {
		// 使用API
		StatFs statFs = new StatFs(path);

		// 【2】获取可用区块的方法
		long availableBlocks = statFs.getAvailableBlocksLong();
		// 【3】获取区块的大小
		long blockSize = statFs.getBlockSizeLong();
		// 可用区块个数×区块大小 = 可用空间大小
		long availSpace = availableBlocks * blockSize;
		// 将进制转换
		String formatFileSize = Formatter.formatFileSize(this, availSpace);

		return formatFileSize;

	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mAppInfoList.size() + 2;
		}

		// 指定list内容的类型数目
		@Override
		public int getViewTypeCount() {
			return 2;
		}

		// 给出类型的标记
		@Override
		public int getItemViewType(int position) {
			if (position == 0 || position == mCustomerAppList.size() + 1) {
				// 返回灰色条目的状态码
				return 0;
			} else {
				// 返回应用列表
				return 1;
			}

		}

		@Override
		public AppInfo getItem(int position) {
			int mode = getItemViewType(position);
			// 灰色条目，不从集合中取数据
			if (mode == 0) {
				return null;
			} else {
				if (position < mCustomerAppList.size() + 1) {
					// 使用用户应用的集合
					return mCustomerAppList.get(position - 1);
				} else {
					// 使用系统应用的集合（索引 - 用户应用的条目数 - 灰色条目数）
					return mSystemAppList.get(position
							- mCustomerAppList.size() - 2);
				}
			}
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int mode = getItemViewType(position);
			View view = null;
			ViewHolder holder = null;
			if (mode == 0) {
				if (convertView != null) {
					view = convertView;
					holder = (ViewHolder) view.getTag();
				} else {
					view = View.inflate(getApplicationContext(),
							R.layout.listview_app_des_item, null);
					holder = new ViewHolder();
					holder.tv_app_des = (TextView) view
							.findViewById(R.id.tv_app_des);
					view.setTag(holder);
				}

				if (position == 0) {
					holder.tv_app_des.setText("用户应用（" + mCustomerAppList.size()
							+ ")");
				} else {
					holder.tv_app_des.setText("系统应用（" + mSystemAppList.size()
							+ ")");
				}
			} else {

				if (convertView != null) {
					view = convertView;
					holder = (ViewHolder) view.getTag();
				} else {
					view = View.inflate(getApplicationContext(),
							R.layout.listview_appinfo_item, null);
					holder = new ViewHolder();
					holder.iv_icon = (ImageView) view
							.findViewById(R.id.iv_icon);
					holder.tv_name = (TextView) view
							.findViewById(R.id.tv_app_name);
					holder.tv_packgeName = (TextView) view
							.findViewById(R.id.tv_app_location);

					view.setTag(holder);
				}

				holder.iv_icon
						.setBackgroundDrawable(getItem(position).drawable);
				holder.tv_name.setText(getItem(position).name);
				if (getItem(position).isSdApp) {
					holder.tv_packgeName.setText("sd卡应用");
				} else {
					holder.tv_packgeName.setText("手机应用");
				}
			}

			return view;
		}

	}

	private static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_packgeName;
		TextView tv_app_des;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		//卸载应用
		case R.id.tv_uninstall:
			if(appInfo.isSystem)
			{
				ToastUtils.show(getApplicationContext(), "此应用不能被卸载");
			}
			else
			{
				Intent intent = new Intent("android.intent.action.DELETE");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:"+appInfo.packageName));
				startActivity(intent);
				
			}
			break;
			//打开应用
		case R.id.tv_start:
			PackageManager pm = getPackageManager();
			//通过Launch开启指定包名的意图，开启意图
			Intent intent = pm.getLaunchIntentForPackage(appInfo.packageName);
			if(intent != null)
			{
				startActivity(intent);				
			}
			else
			{
				ToastUtils.show(getApplicationContext(), "此应用不能被开启");
			}
			break;
		case R.id.tv_share:
			
			break;

		
		}
		
		if(popupWindow != null)
		{
			popupWindow.dismiss();
		}
		
	}
}
