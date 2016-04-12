package com.dsd.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsd.mobilesafe.utils.ConstantValue;
import com.dsd.mobilesafe.utils.Md5Util;
import com.dsd.mobilesafe.utils.SpUtils;
import com.dsd.mobilesafe.utils.ToastUtils;
import com.dsd.mobilesafe.R;

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
					
					showDialog();
					break;
				//开启黑名单功能
				case 1:
					startActivity(new Intent(getApplicationContext(),BlackNumberActivity.class));
					
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
					//跳转到高级工具共功能列表界面
					startActivity(new Intent(getApplicationContext(),AToolActivity.class));
					
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
	
	
	/**
	 * 显示手机防盗功能的密码dailog
	 */
	protected void showDialog() {
		// 判读本地时候存有密码
		String psd = SpUtils.getString(this, ConstantValue.MOBILE_SAFE_PSD, "");
		if(TextUtils.isEmpty(psd))
		{
			//【1】初始设置密码对话框
			showSetPsdDialog();
		}
		else
		{
			//【2】确认密码对话框
			showConfirmPsdDialog();
		}
		
		
		
	}


	/**
	 *确认密码对话框
	 */
	private void showConfirmPsdDialog() {
		//因为需要自己定义对话框的展示样式，所以需要调用dialog.setView(view)
				Builder builder = new AlertDialog.Builder(this);
				final AlertDialog dialog = builder.create();
				final View view = View.inflate(this,R.layout.dialog_confirm_psd, null);
				
				Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
				Button bt_cancel = (Button) view.findViewById(R.id.bt_cancle);
				
				
				bt_submit.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//设置确认的点击事件，保存密码
						EditText et_confirm_psd =(EditText)view. findViewById(R.id.et_confirm_psd);
						
						String confirmPsd = et_confirm_psd.getText().toString().trim();
						
						if(!TextUtils.isEmpty(confirmPsd))
						{
							String psd = SpUtils.getString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, "");
							if(psd.equals(Md5Util.encoder(confirmPsd)))
							{
								//进入应用手机防盗模块，开启一个新的activity
								Intent intent = new Intent(HomeActivity.this,SetupOverActivity.class);
								startActivity(intent);
								
								//跳转到新的界面以后需要隐藏（关闭）对话框
								dialog.dismiss();
								
							}
							else
							{
								//密码不匹配
								ToastUtils.show(getApplicationContext(), "密码错误");
							}
						}
						else
						{
							//提示用户不能为空
							ToastUtils.show(getApplicationContext(), "请输入密码");
						}
						
					}
				});
				
				bt_cancel.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//点击取消按钮，取消显示对话框
						dialog.dismiss();
						
					}
				});
				//让对话框显示一个自己定义的对话框界面效果
				//dialog.setView(view);
				
				//兼容低版本写法，去除内边距离
				dialog.setView(view, 0, 0, 0, 0);
				
				dialog.show();
	}
	/**
	 * 设置密码对话框
	 */
	private void showSetPsdDialog() {
		
		//因为需要自己定义对话框的展示样式，所以需要调用dialog.setView(view)
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		final View view = View.inflate(this,R.layout.dialog_set_psd, null);
		
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancle);
		
		
		bt_submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//设置确认的点击事件，保存密码
				EditText et_set_psd = (EditText) view.findViewById(R.id.et_set_psd);
				EditText et_confirm_psd =(EditText)view. findViewById(R.id.et_confirm_psd);
				
				String psd = et_set_psd.getText().toString().trim();
				String confirmPsd = et_confirm_psd.getText().toString().trim();
				
				if(!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(confirmPsd))
				{
					if(psd.equals(confirmPsd))
					{
						//进入应用手机防盗模块，开启一个新的activity
						Intent intent = new Intent(HomeActivity.this,SetupOverActivity.class);
						startActivity(intent);
						
						//跳转到新的界面以后需要隐藏（关闭）对话框
						dialog.dismiss();
						
						//完成后存储密码
						 SpUtils.putString(getApplicationContext(), 
								 ConstantValue.MOBILE_SAFE_PSD, Md5Util.encoder(psd));
						
					}
					else
					{
						//密码不匹配
						ToastUtils.show(getApplicationContext(), "确认密码错误");
					}
				}
				else
				{
					//提示用户不能为空
					ToastUtils.show(getApplicationContext(), "请输入密码");
				}
				
			}
		});
		
		bt_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//点击取消按钮，取消显示对话框
				dialog.dismiss();
				
			}
		});
		//让对话框显示一个自己定义的对话框界面效果
		//dialog.setView(view);
		//兼容低版本写法，去除内边距离
		dialog.setView(view, 0, 0, 0, 0);
		
		dialog.show();
		
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
