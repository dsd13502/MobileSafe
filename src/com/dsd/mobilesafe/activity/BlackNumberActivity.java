package com.dsd.mobilesafe.activity;

import java.util.List;

import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.R.layout;
import com.dsd.mobilesafe.db.dao.BlackNumberDao;
import com.dsd.mobilesafe.db.daomain.BlackNumberInfo;
import com.lidroid.xutils.view.annotation.event.OnScroll;

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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
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
	private boolean mIsLoad = false;
	private int mCount;
	
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if(myAdapter == null)
			{
				myAdapter = new MyAdapter();
				lv_blacknumer.setAdapter(myAdapter);				
			}
			else
			{
				myAdapter.notifyDataSetChanged();
			}
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
				mDao = BlackNumberDao.getInstance(getApplicationContext());
				// mBlackNumberList = mDao.findAll();

				// 我们改为分页查询数据
				mBlackNumberList = mDao.findIndex(0);
				// 获取数据总条目
				mCount = mDao.getCount();
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

		// 监听lv_blacknumber监听的滚动事件
		lv_blacknumer.setOnScrollListener(new OnScrollListener() {

			// 滚动过程中，状态发生改变调用的方法
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// OnScrollListener.SCROLL_STATE_FLING //飞速滚动
				// OnScrollListener.SCROLL_STATE_IDLE //空闲状态
				// OnScrollListener.SCROLL_STATE_TOUCH_SCROLL //拿手触摸者去滚动
				
				Log.i(tag, "list view 开始滑动");
				if (mBlackNumberList != null) {
					// 条件一： 滚动到停止的状态
					// 条件二： 最后一个条目可见（最后一个条目的索引值 >= 数据适配器中集合的总数条目个数 -1

					/*
					 * !mIsLoad是防止重复加载的变量 如果当前正在加载mISLoad就会为true
					 * 本次加载完毕后，在将mIsLoad改为false
					 * 如果洗一次加载需要去做执行的时候，会判读上述mISLoad变量，是否为false如果为true
					 * ；就需要等待上一次加载完成 将其值改为false后再去加载
					 */
					Log.i(tag, "mIsLoad : "+mIsLoad );
					Log.i(tag, "mCount : "+ mCount);
					Log.i(tag, "scrollState : "+ scrollState);
					Log.i(tag, "lv_blacknumer.getLastVisiblePosition() : "+ lv_blacknumer.getLastVisiblePosition());
					Log.i(tag, "mBlackNumberList.size() - 1 ： "+ (mBlackNumberList.size() - 1)+"");
					if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
							&& lv_blacknumer.getLastVisiblePosition() >= mBlackNumberList
									.size() - 1 && !mIsLoad) {
						
						
						
						if(mCount > mBlackNumberList.size())
						{
							new Thread() {
								public void run() {
									//正在加载中
									mIsLoad = true;
									
									mDao = BlackNumberDao
											.getInstance(getApplicationContext());
									// mBlackNumberList = mDao.findAll();

									// 2 查询数据
									List<BlackNumberInfo> moreData = mDao
											.findIndex(mBlackNumberList.size());
									// 3 添加下一页数据的过程
									mBlackNumberList.addAll(moreData);

									// 4.通知数据适配器刷新
									mHandler.sendEmptyMessage(0);
									
									mIsLoad = false;
								};
								
							}.start();	
						}

					}
				}

			}

			// 滚动过程中调用的方法
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
	}

	/**
	 * 显示自定义对话框
	 */
	private void showDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();

		View view = View.inflate(getApplicationContext(),
				R.layout.dialog_add_blacknumber, null);

		RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
		final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

		// 监听RadioGroup的选中状态
		rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_sms:
					// 拦截短信
					mode = 1;
					break;
				case R.id.rb_phone:
					// 拦截电话
					mode = 2;
					break;
				case R.id.rb_all:
					// 拦截全部
					mode = 3;
					break;

				default:
					break;
				}
			}
		});

		// 确定按钮的监听
		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// [1]获取电话号码
				String phone = et_phone.getText().toString().trim();

				if (!TextUtils.isEmpty(phone)) {
					// [2]插入数据
					mDao.insert(phone, mode + "");
					// ****************☆☆☆☆注意list与数据库的同步数据************
					// [3]跟新数据库和集合同步。（方法：1.从数据库中重新读取一遍数据，2，手动向集合中插入）
					BlackNumberInfo info = new BlackNumberInfo();
					info.setPhone(phone);
					info.setMode(mode + "");

					// [4]将对象插入集合的最顶部
					mBlackNumberList.add(0, info);
					// [5]发送消息让Adapter刷新
					if (myAdapter != null) {
						myAdapter.notifyDataSetChanged();
					}

					// [6]隐藏对话框
					dialog.dismiss();
				} else {
					Toast.makeText(getApplicationContext(), "请输入拦截号码", 0)
							.show();
				}

			}
		});

		// 取消按钮的监听
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setView(view, 0, 0, 0, 0);
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
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

			final String phone = getItem(position).getPhone().toString().trim();

			holder.iv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// [1]数据库的删除
					mDao.delete(phone);
					// [2]list删除
					mBlackNumberList.remove(position);
					// 【3】刷新Adapter
					if (myAdapter != null) {
						myAdapter.notifyDataSetChanged();
					}
				}
			});

			holder.tv_phone.setText(phone);

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
