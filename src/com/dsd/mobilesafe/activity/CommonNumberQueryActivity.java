package com.dsd.mobilesafe.activity;

import java.util.List;

import com.dsd.mobilesafe.R;
import com.dsd.mobilesafe.R.id;
import com.dsd.mobilesafe.R.layout;
import com.dsd.mobilesafe.engine.CommonnumDao;
import com.dsd.mobilesafe.engine.CommonnumDao.Child;
import com.dsd.mobilesafe.engine.CommonnumDao.Group;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class CommonNumberQueryActivity extends Activity {

	private ExpandableListView elv_common_number;
	private List<Group> mCommonNumberList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number_query);
		
		initUI();
		initData();
	
	}
	
	private void initData() {
		mCommonNumberList = new CommonnumDao().getCommonNumber(getApplicationContext());
		
		MyAdapter myAdapter = new MyAdapter();
		elv_common_number.setAdapter(myAdapter);
		
		elv_common_number.setOnChildClickListener(new OnChildClickListener() {	
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
				// 拨打电话
				String number = mCommonNumberList.get(groupPosition).childList.get(childPosition).number;
				startCall(number);
				return false;
			}

		
		});
		
		
	}
	/**
	 * 拨打电话
	 */
	private void startCall(String number) {
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:"+ number));
		
		
	}
	private void initUI() {
		elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);
	}
	
	private class MyAdapter extends BaseExpandableListAdapter
	{

		@Override
		public int getGroupCount() {
			return mCommonNumberList.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mCommonNumberList.get(groupPosition).childList.size();
		}

		@Override
		public Group getGroup(int groupPosition) {
			
			return mCommonNumberList.get(groupPosition);
		}

		@Override
		public Child getChild(int groupPosition, int childPosition) {
			return mCommonNumberList.get(groupPosition).childList.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = new TextView(getApplicationContext());
			textView.setText("			"+getGroup(groupPosition).name);
			textView.setTextColor(Color.RED);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			return textView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			
			View view = View.inflate(getApplicationContext(), R.layout.elv_child_item, null);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_number = (TextView) view.findViewById(R.id.tv_number);
			
			tv_name.setText(getChild(groupPosition, childPosition).name);
			tv_number.setText(getChild(groupPosition, childPosition).number);
			
			return view;
			
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
	}
}
