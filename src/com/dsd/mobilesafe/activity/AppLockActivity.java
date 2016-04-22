package com.dsd.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.R.drawable;
import com.dsd.mobilesafe.R.id;
import com.dsd.mobilesafe.R.layout;
import com.dsd.mobilesafe.db.dao.AppLockDao;
import com.dsd.mobilesafe.db.daomain.AppInfo;
import com.dsd.mobilesafe.engine.AppInfoProvider;
import com.dsd.mobilesafe.utils.ConstantValue;

public class AppLockActivity extends Activity {

	protected static final String tag = "AppLockActivity";
	private Button bt_unlock, bt_lock;
	private LinearLayout ll_unlock, ll_lock;
	private TextView tv_unlock, tv_lock;
	private ListView lv_unlock, lv_lock;
	private AppLockDao mDao;
	private static List<AppInfo> mAppInfoList = new ArrayList<AppInfo>();
	private static List<AppInfo> mLockList = null;
	private static List<AppInfo> mUnLockList = null;
	private MyAdapter lockAdapte;
	private MyAdapter unLockAdapter;
	private TranslateAnimation mTranslateAnimation;

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			// 6.接收到消息,填充已加锁和未加锁的数据适配器

			lockAdapte = new MyAdapter(true);
			unLockAdapter = new MyAdapter(false);
			lv_lock.setAdapter(lockAdapte);

			lv_unlock.setAdapter(unLockAdapter);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);

		initUI();
		initData();
		initAnimation();
	}

	/*
	 * 初始化平移动画
	 */
	private void initAnimation() {
		mTranslateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 1,
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0);
		
		mTranslateAnimation.setDuration(500);
		
	}

	/**
	 * 添加数据
	 */
	private void initData() {
		new Thread() {
			public void run() {
				super.run();
				mDao = AppLockDao.getInstance(getApplicationContext());

				mLockList = new ArrayList<AppInfo>();
				mUnLockList = new ArrayList<AppInfo>();

				mAppInfoList.clear();
				mAppInfoList = AppInfoProvider
						.getAppInfoList(getApplicationContext());

				List<String> lockList = mDao.findAll();

				if (mAppInfoList != null) {
					for (AppInfo appInfo : mAppInfoList) {
						if (lockList.contains(appInfo.packageName)) {
							mLockList.add(appInfo);
						} else {
							mUnLockList.add(appInfo);
						}
					}
				} else {
					Log.i(tag, "mAppInfoList 为空");
				}

				mHandler.sendEmptyMessage(0);
			};
		}.start();

	}

	/**
	 * 创建UI
	 */
	private void initUI() {
		bt_unlock = (Button) findViewById(R.id.bt_unlock);
		bt_lock = (Button) findViewById(R.id.bt_lock);

		ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
		ll_lock = (LinearLayout) findViewById(R.id.ll_lock);

		tv_unlock = (TextView) findViewById(R.id.tv_unlock);
		tv_lock = (TextView) findViewById(R.id.tv_lock);

		lv_unlock = (ListView) findViewById(R.id.lv_app_unlock);
		lv_lock = (ListView) findViewById(R.id.lv_app_lock);

		bt_lock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 加锁按钮被点击
				bt_unlock.setBackgroundResource(R.drawable.tab_left_default);
				bt_lock.setBackgroundResource(R.drawable.tab_right_pressed);
				ll_unlock.setVisibility(View.GONE);
				ll_lock.setVisibility(View.VISIBLE);

				Log.i(tag, "bt_lock");
				if (lockAdapte != null) {
					lockAdapte.notifyDataSetChanged();

				}
				mHandler.sendEmptyMessage(0);
			}
		});

		bt_unlock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 未加锁按钮被点击

				Log.i(tag, "bt_unlock");
				ll_unlock.setVisibility(View.VISIBLE);
				ll_lock.setVisibility(View.GONE);
				bt_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
				bt_lock.setBackgroundResource(R.drawable.tab_right_default);

				if (unLockAdapter != null) {
					unLockAdapter.notifyDataSetChanged();
				}
			}
		});
	}

	private class MyAdapter extends BaseAdapter {

		private boolean isLocked;

		public MyAdapter(boolean isLocked) {
			this.isLocked = isLocked;
		}

		@Override
		public int getCount() {

			if (isLocked) {
				Log.i(tag, "mLockList.size(): " + mLockList.size());
				return mLockList.size();
			} else {
				Log.i(tag, "mUnLockList.size(): " + mUnLockList.size());
				return mUnLockList.size();

			}

		}

		@Override
		public AppInfo getItem(int position) {
			if (isLocked) {
				return mLockList.get(position);
			} else {
				return mUnLockList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final int location = position;

			final View view ;
			ViewHolder holder = null;

			if (convertView != null) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				holder = new ViewHolder();
				view = View.inflate(getApplicationContext(),
						R.layout.listview_app_lock, null);
				holder.im_app_icon = (ImageView) view
						.findViewById(R.id.im_app_icon);
				holder.tv_app_name = (TextView) view
						.findViewById(R.id.tv_app_name);
				holder.im_lock_icon = (ImageView) view
						.findViewById(R.id.im_lock_icon);
				view.setTag(holder);

			}

			String name = getItem(position).name;
			if (name != null) {
				holder.tv_app_name.setText(name);
			} else {
				holder.tv_app_name.setText(getItem(position).packageName);
			}
			if (!isLocked) {
				holder.im_lock_icon.setBackgroundResource(R.drawable.unlock);
			}

			holder.im_app_icon
					.setBackgroundDrawable(getItem(position).drawable);

			holder.im_lock_icon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					view.startAnimation(mTranslateAnimation);
					mTranslateAnimation.setAnimationListener(new AnimationListener() {
						
						@Override
						public void onAnimationStart(Animation animation) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onAnimationRepeat(Animation animation) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onAnimationEnd(Animation animation) {
							// TODO Auto-generated method stub
							// [1]消失的动画
							// [2]从mUnLockList中移除
							AppInfo item = getItem(location);

							if (mLockList != null && mUnLockList != null) {
								if (isLocked) {

									// 修改数据库
									mDao.delete(item.packageName);
									// 修改mUnLockList
									mUnLockList.add(0, item);
									mLockList.remove(location);

									// 刷新
									if (lockAdapte != null) {
										lockAdapte.notifyDataSetChanged();
									}

								} else {
									// 修改mLockList
									mLockList.add(0, item);
									// 修改数据库
									mDao.insert(item.packageName);

									mUnLockList.remove(location);

									// 刷新
									if (unLockAdapter != null) {
										unLockAdapter.notifyDataSetChanged();
									}
								}
								// [3]添加数据库
								// [4]添加mLockList

							}
						}
					});
					
				
				}
			});
			return view;
		}
	}

	private static class ViewHolder {
		ImageView im_app_icon;
		ImageView im_lock_icon;
		TextView tv_app_name;

	}
}
