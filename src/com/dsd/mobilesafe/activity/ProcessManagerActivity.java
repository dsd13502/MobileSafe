package com.dsd.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.db.daomain.ProcessInfo;
import com.dsd.mobilesafe.engine.ProcessInfoProvider;

public class ProcessManagerActivity extends Activity implements OnClickListener {
	private String tag = "ProcessManagerActivity";
	private TextView tv_process_count;
	private TextView tv_memory_info;
	private TextView tv_des;
	private ListView lv_process_list;
	private Button bt_select_all;
	private Button bt_select_reverse;
	private Button bt_clear;
	private Button bt_setting;
	private int mProcessCount;
	private ProcessInfo mProcessInfo;
	private long mMemoryAvailSize;
	private long mMemoryTotalSize;
	private List<ProcessInfo> mProcessInfoList;
	private List<ProcessInfo> mCustomerProcessList;
	private List<ProcessInfo> mSystemProcessInfo;
	private MyAdapter myAdapter;
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			myAdapter = new MyAdapter();
			lv_process_list.setAdapter(myAdapter);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_manager);

		initUI();
		initTitleData();
		getProcessData();

	}

	private class MyAdapter extends BaseAdapter {
		// 获取数据适配器中条目类型的总数,修改成两种(纯文本,图片+文字)
		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount() + 1;
		}

		// 指定索引指向的条目类型,条目类型状态码指定(0(复用系统),1)
		@Override
		public int getItemViewType(int position) {
			if (position == 0 || position == mCustomerProcessList.size() + 1) {
				// 返回0,代表纯文本条目的状态码
				return 0;
			} else {
				// 返回1,代表图片+文本条目状态码
				return 1;
			}
		}

		// listView中添加两个描述条目
		@Override
		public int getCount() {
			return mProcessInfoList.size() + 2;
		}

		@Override
		public ProcessInfo getItem(int position) {
			if (position == 0 || position == mCustomerProcessList.size() + 1) {
				return null;
			} else {
				if (position < mCustomerProcessList.size() + 1) {
					return mCustomerProcessList.get(position - 1);
				} else {
					// 返回系统进程对应条目的对象
					return mSystemProcessInfo.get(position
							- mCustomerProcessList.size() - 2);
				}
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);

			if (type == 0) {
				// 展示灰色纯文本条目
				ViewTitleHolder holder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.listview_app_des_item, null);
					holder = new ViewTitleHolder();
					holder.tv_app_des = (TextView) convertView
							.findViewById(R.id.tv_app_des);
					convertView.setTag(holder);
				} else {
					holder = (ViewTitleHolder) convertView.getTag();
				}
				if (position == 0) {

					holder.tv_app_des.setText("用户进程("
							+ mCustomerProcessList.size() + ")");
				} else {

					holder.tv_app_des.setText("系统进程("
							+ mSystemProcessInfo.size() + ")");
				}
				return convertView;
			} else {
				// 展示图片+文字条目
				ViewHolder holder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.listview_process_item, null);
					holder = new ViewHolder();
					holder.iv_icon = (ImageView) convertView
							.findViewById(R.id.iv_icon);
					holder.tv_name = (TextView) convertView
							.findViewById(R.id.tv_name);
					holder.tv_memory_info = (TextView) convertView
							.findViewById(R.id.tv_memory_info);
					holder.cb_box = (CheckBox) convertView
							.findViewById(R.id.cb_box);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				holder.iv_icon.setBackgroundDrawable(getItem(position)
						.getIcon());
				holder.tv_name.setText(getItem(position).getName());
//				Log.i(tag, "getItem(position).getName() = "
//						+ getItem(position).getName());
				String strSize = Formatter
						.formatFileSize(getApplicationContext(),
								getItem(position).getMemSize());
				holder.tv_memory_info.setText(strSize);

				// 本进程不能被选中,所以先将checkbox隐藏掉
				if (getItem(position).getPackageName().equals(getPackageName())) {
					holder.cb_box.setVisibility(View.GONE);
				} else {
					holder.cb_box.setVisibility(View.VISIBLE);
				}

				holder.cb_box.setChecked(getItem(position).isCheck());

				return convertView;
			}
		}
	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_memory_info;
		CheckBox cb_box;
	}

	static class ViewTitleHolder {
		TextView tv_app_des;
	}

	/**
	 * 获取进程信息
	 */
	private void getProcessData() {

		new Thread() {

			@Override
			public void run() {
				if (mProcessInfoList != null) {
					mProcessInfoList.clear();
				}
				mProcessInfoList = ProcessInfoProvider
						.getProcessInfo(getApplicationContext());

				mCustomerProcessList = new ArrayList<ProcessInfo>();
				mSystemProcessInfo = new ArrayList<ProcessInfo>();

				for (ProcessInfo processInfo : mProcessInfoList) {
					if (processInfo.isSystem()) {
						mSystemProcessInfo.add(processInfo);
					} else {
						mCustomerProcessList.add(processInfo);
					}

				}
				//
				// Log.i(tag,"mProcessInfoList.size() = "+mProcessInfoList.size());
				// Log.i(tag,"mCustomerProcessList.size() = "+mCustomerProcessList.size());
				// Log.i(tag,"mSystemProcessInfo.size() = "+mSystemProcessInfo.size());
				super.run();
				mHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	/**
	 * 
	 */
	private void initTitleData() {

		// 获取进程总数
		mProcessCount = ProcessInfoProvider.getProcessCount(this);
		tv_process_count.setText("进程总数：" + mProcessCount);

		// 获取可用内存大小
		mMemoryAvailSize = ProcessInfoProvider.getMemoryAvailSize(this);
		String strAvailSpace = Formatter.formatFileSize(this, mMemoryAvailSize);
		// 获取全部可用空间大小
		mMemoryTotalSize = ProcessInfoProvider.getMemoryTotalSize(this);
		String strTotalSpace = Formatter.formatFileSize(this, mMemoryTotalSize);
		tv_memory_info.setText("剩余内存/总共内存：" + strAvailSpace + "/"
				+ strTotalSpace);

	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_memory_info = (TextView) findViewById(R.id.tv_memory_info);

		tv_des = (TextView) findViewById(R.id.tv_process_title);

		lv_process_list = (ListView) findViewById(R.id.lv_process_list);

		bt_select_all = (Button) findViewById(R.id.bt_select_all);
		bt_select_reverse = (Button) findViewById(R.id.bt_select_reverse);
		bt_clear = (Button) findViewById(R.id.bt_clear);
		bt_setting = (Button) findViewById(R.id.bt_setting);

		bt_select_all.setOnClickListener(this);
		bt_select_reverse.setOnClickListener(this);
		bt_clear.setOnClickListener(this);
		bt_setting.setOnClickListener(this);

		lv_process_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if(position == 0 || position == mCustomerProcessList.size()+1){
					return;
				}else{
					if(position<mCustomerProcessList.size()+1){
						mProcessInfo = mCustomerProcessList.get(position-1);
					}else{
						//返回系统应用对应条目的对象
						mProcessInfo = mSystemProcessInfo.get(position - mCustomerProcessList.size()-2);
					}
					if(mProcessInfo!=null){
						if(!mProcessInfo.getPackageName().equals(getPackageName())){
							//选中条目指向的对象和本应用的包名不一致,才需要去状态取反和设置单选框状态
							//状态取反
							mProcessInfo.setCheck(!mProcessInfo.isCheck());
							//checkbox显示状态切换
							//通过选中条目的view对象,findViewById找到此条目指向的cb_box,然后切换其状态
							CheckBox cb_box = (CheckBox) view.findViewById(R.id.cb_box);
							cb_box.setChecked(mProcessInfo.isCheck());
						}
					}
				}
			
			}
		});
		lv_process_list.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// 滚动过程中调用方法
				// AbsListView中view就是listView对象
				// firstVisibleItem第一个可见条目索引值
				// visibleItemCount当前一个屏幕的可见条目数
				// 总共条目总数
				if (mCustomerProcessList != null && mSystemProcessInfo != null) {
					if (firstVisibleItem >= mCustomerProcessList.size() + 1) {
						// 滚动到了系统条目
						tv_des.setText("系统进程(" + mSystemProcessInfo.size()
								+ ")");
					} else {
						// 滚动到了用户应用条目
						tv_des.setText("用户进程(" + mCustomerProcessList.size()
								+ ")");
					}
				}
			}
		});

		

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_select_all:
			selectAll();
			break;

		case R.id.bt_select_reverse:
			reverse();
			break;
		case R.id.bt_clear:
			clearAll();
			break;

		case R.id.bt_setting:
			break;
		}

	}

	/**
	 * 一键清理
	 */
	private void clearAll() {
		
		List<ProcessInfo> killProcessList = new ArrayList<ProcessInfo>();
		
		for (ProcessInfo processInfo : mCustomerProcessList) {
			if(processInfo.isCheck())
			{
				//不能在集合循环过程中去移除自身的内容，会报不安全的错误
				killProcessList.add(processInfo);
			}
		}

		for (ProcessInfo processInfo : mSystemProcessInfo) {
			if(processInfo.isCheck())
			{
				killProcessList.add(processInfo);
			}
		}

		long toatalReleaseSpace = 0;
		for (ProcessInfo processInfo : killProcessList) {
			if(mCustomerProcessList.contains(processInfo))
			{
				mCustomerProcessList.remove(processInfo);
			}else if(mSystemProcessInfo.contains(processInfo))
			{
				mSystemProcessInfo.remove(processInfo);
			}
			
			//杀死进程
			ProcessInfoProvider.killProcess(getApplicationContext(), processInfo);
			//更新UI
			//[1]获得释放的内存空间
			toatalReleaseSpace += processInfo.getMemSize();
			//[2]更新进程总数
			mProcessCount -= killProcessList.size();
			//更新
			tv_process_count.setText("总进程数："+mProcessCount);
			mMemoryAvailSize += toatalReleaseSpace;
			String releaseSpace = Formatter.formatShortFileSize(getApplicationContext(), mMemoryAvailSize);
			tv_memory_info.setText("剩余内存/总共内存"+releaseSpace+"+"+mMemoryTotalSize);
			
			Toast.makeText(getApplicationContext(), "杀死了"+killProcessList.size()
					+"个进程，释放了"+releaseSpace+"内存", 0).show();
			
		}
		
		
		if(myAdapter !=null)
			myAdapter.notifyDataSetChanged();
		
		
	}

	private void reverse() {
		for (ProcessInfo processInfo : mCustomerProcessList) {
			if (processInfo.getPackageName().equals(getPackageName())) {
				continue;
			}
			processInfo.setCheck(!processInfo.isCheck());
		}

		for (ProcessInfo processInfo : mSystemProcessInfo) {
			processInfo.setCheck(!processInfo.isCheck());
		}
		if(myAdapter !=null)
			myAdapter.notifyDataSetChanged();
	}

	/**
	 * 全选
	 */
	private void selectAll() {
		//Log.i(tag, "selectAll 启动了");
		for (ProcessInfo processInfo : mCustomerProcessList) {
			if (processInfo.getPackageName().equals(getPackageName())) {
				continue;
			}
			processInfo.setCheck(true);
		}

		for (ProcessInfo processInfo : mSystemProcessInfo) {
			processInfo.setCheck(true);
		}
		if(myAdapter !=null)
			myAdapter.notifyDataSetChanged();
	}
}
