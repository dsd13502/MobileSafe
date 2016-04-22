package com.dsd.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.utils.ToastUtils;

public class EnterPsdActivityActivity extends Activity {

	private String packageName;
	private TextView tv_app_name;
	private ImageView iv_app_icon;
	private EditText et_psd;
	private Button bt_submit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_psd_activity);
		
		packageName = getIntent().getStringExtra("packageName");
		initUI();
		initData();
	}
	
	private void initData() {
		
		PackageManager pm = getPackageManager();
		try {
			ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
			Drawable drawable = applicationInfo.loadIcon(pm);
			iv_app_icon.setBackgroundDrawable(drawable);
			String name = applicationInfo.name;
			if(name != null)
			{
				tv_app_name.setText(name);
			}
			else
			{
				tv_app_name.setText(packageName);
				
			}
			
			bt_submit.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String psd = et_psd.getText().toString().trim();
					if(!TextUtils.isEmpty(psd))
					{
						if(psd.equals("123"))
						{
							Intent intent = new Intent("android.intent.action.SIK_APP");
							intent.putExtra("SikPackageName", packageName);
							sendBroadcast(intent);
							//解锁进入应用
							finish();
							
						}
						else
						{
							
							ToastUtils.show(getApplicationContext(), "密码不正确，请重新输入");
						}
					}
					else
					{
						ToastUtils.show(getApplicationContext(), "请输入密码");
					}
					
				}
			});
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void initUI() {
		tv_app_name = (TextView) findViewById(R.id.tv_app_name);
		iv_app_icon = (ImageView) findViewById(R.id.iv_app_icon);
		
		et_psd = (EditText) findViewById(R.id.et_psd);
		bt_submit = (Button) findViewById(R.id.bt_submit);
	}
}
