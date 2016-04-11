package com.dsd.mobilesafe.activity;

import com.dsd.mobilesafe.service.AddressService;
import com.dsd.mobilesafe.utils.ConstantValue;
import com.dsd.mobilesafe.utils.ServiceUtil;
import com.dsd.mobilesafe.utils.SpUtils;
import com.dsd.mobilesafe.view.SettingClickView;
import com.dsd.mobilesafe.view.SettingItemView;
import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {

	private SettingItemView siv = null;
	private int mToastStyle;
	private SettingClickView siv_address;
	protected String[] mToastStyleDes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seting);

		initUI();
		initUpdate();	
		initAddress();
		initToastStyle();
		initLocatin();

	}

	/**
	 * 双击居中view所在屏幕位置的处理方法
	 */
	private void initLocatin() {
		SettingClickView scv_location = (SettingClickView) findViewById(R.id.scv_location);
		scv_location.setTitle("归属地提示框的位置");
		scv_location.setDes("设置归属地提示框的位置");
		
		scv_location.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),ToastLocationActivity.class));
			}
		});
	}

	private void initToastStyle() {
		siv_address = (SettingClickView) findViewById(R.id.scv_toast_style);
		siv_address.setTitle("设置电话归属地显示风格");
		// 创建描述文字所在的String类型数组
		mToastStyleDes = new String[] { "透明", "橙色", "蓝色", "灰色", "绿色", };
		mToastStyle = SpUtils.getInt(this, ConstantValue.TOAST_STYLE, 0);
		// [3]通过索引，获取字符串数组中的文字，显示给描述内容控件
		siv_address.setDes(mToastStyleDes[mToastStyle]);

		// [4]监听点击事件，弹出对话框
		siv_address.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// [5]显示吐司样式的对话框
				showToastStyleDialog();
			}

		});
	}

	/**
	 * 创建选中显示样式的对话框
	 */
	private void showToastStyleDialog() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("请选择归属地样式");
		//选择单个条目事件监听
		/*(1：Stirng类型的数组描述颜色文字的数组，
		 * 2：弹出对话框的选中条目的随意值
		 * 3：点击某一个条目后触发的点击事件（1.记录选中的索引值，2关闭对话框，3.显示选中颜色文字）) 
		 */
		
		builder.setSingleChoiceItems(mToastStyleDes, mToastStyle, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) { //which 选中的索引值
				// 1.记录选中的索引值，2关闭对话框，3.显示选中颜色文字
				SpUtils.putInt(getApplicationContext(), ConstantValue.TOAST_STYLE, which);
				siv_address.setDes(mToastStyleDes[which]);
				dialog.dismiss();
			}
		});
		//消极按钮
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
			}
		});
		//显示对话框
		builder.show();
	}

	/**
	 * 是否显示电话归属地的方法
	 */
	private void initAddress() {

		final SettingItemView siv_address = (SettingItemView) findViewById(R.id.siv_address);

		// 判读归属的服务是否开启了
		boolean isRunning = ServiceUtil.isRunning(getApplicationContext(),
				"com.dsd.mobilesafe.service.AddressService");
		siv_address.setCheck(isRunning);

		siv_address.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 获取点击前的状态
				boolean isCheck = siv_address.isCheck();
				siv_address.setCheck(!isCheck);

				if (!isCheck) {
					startService(new Intent(getApplicationContext(),
							AddressService.class));

				}
			}
		});
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		siv = (SettingItemView) findViewById(R.id.siv_update);
	}

	/**
	 * 初始化自动更新功能
	 */
	private void initUpdate() {

		// 数据回显示
		boolean openUpdate = SpUtils.getBoolean(getApplicationContext(),
				ConstantValue.OPEN_UPDATE, true);
		siv.setCheck(openUpdate);

		siv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				boolean isCheck = siv.isCheck();
				// 将原有数据取反
				siv.setCheck(!isCheck);
				// 将取反后的状态存储到是sp中
				SpUtils.putBoolean(getApplicationContext(),
						ConstantValue.OPEN_UPDATE, !isCheck);
			}
		});
	}

}
