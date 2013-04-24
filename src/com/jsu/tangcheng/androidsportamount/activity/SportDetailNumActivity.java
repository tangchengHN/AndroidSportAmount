package com.jsu.tangcheng.androidsportamount.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jsu.tangcheng.androidsportamount.R;
import com.jsu.tangcheng.androidsportamount.application.SportLocation;
import com.jsu.tangcheng.androidsportamount.service.SportAmountService;

public class SportDetailNumActivity extends Activity{

	private TextView tv_poi;
	private TextView tv_distance;
	private TextView tv_amount;
	private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_sportdetail_num);
    	
    	initView();
    	setData();
    }
    //给界面设置值
	private void setData() {
		tv_poi.setText(SportLocation.sb);
		
		tv_distance.setText(SportLocation.distance+"");
		tv_amount.setText(SportAmountService.sportAmountCache+"");
	}
	private void initView() {
		tv_poi = (TextView) findViewById(R.id.tv_poi);
		tv_distance = (TextView) findViewById(R.id.tv_distance);
		tv_amount = (TextView) findViewById(R.id.tv_amount);
	}
	
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.button1:

			intent = new Intent(this,MainActivity.class);
			startActivity(intent);
			finish();
			break;
			
		case R.id.button2:
			
			break;

		case R.id.button3:
			intent = new Intent(this,SportMapActivity.class);
			startActivity(intent);
			finish();
			break;
			
		case R.id.bt_to_bar:
			intent = new Intent(this,SportDetailBarActivity.class);
			startActivity(intent);
			break;
			
		}
	}
}
