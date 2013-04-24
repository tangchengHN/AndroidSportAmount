package com.jsu.tangcheng.androidsportamount.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.jsu.tangcheng.androidsportamount.R;
import com.jsu.tangcheng.androidsportamount.application.SportLocation;
import com.jsu.tangcheng.androidsportamount.service.SportAmountService;

public class MainActivity extends Activity implements OnClickListener {

    private static final int DIALOG_SET_WEIGHT = 0;
	private Intent intent;
	private LocationClient mLocClient;
	private Vibrator mVibrator01;
	private TextView mTv;
	private TextView tv_showCount;
	private AlertDialog setWeight;
	private EditText editText_setWeight;
	public static int weight;
	private Timer timer = new Timer();
	private Handler handler = new Handler(){
		

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (weight>=20) {	
					if (SportAmountService.isStart) {
						refreshData();
					}
				}
				break;
				
			default:
				break;
			}
		}
	};
	
	private TimerTask task = new TimerTask() {
		
		@Override
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
			
		}
	};

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initLocation();
        timer.schedule(task, 1000, 5000);
    }

    private void initView() {
    	tv_showCount = (TextView) findViewById(R.id.tv_showCount);
    }

	private void initLocation() {
		mLocClient = ((SportLocation)getApplication()).mLocationClient;
		((SportLocation)getApplication()).mTv = mTv;
		mVibrator01 =(Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);
		((SportLocation)getApplication()).mVibrator01 = mVibrator01;
		
		setLocationOption();
		mLocClient.requestLocation();
	}

	//设置相关参数
	private void setLocationOption(){
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);				//打开gps
		option.setCoorType("bd09ll");		//设置坐标类型
		option.setScanSpan(10000);	//设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
		option.setAddrType("all");		//设置地址信息，仅设置为“all”时有地址信息，默认无地址信息
		mLocClient.setLocOption(option);
	}

	//实时刷新数据
	public void refreshData(){
		tv_showCount.setText("量跑值\n"+(SportAmountService.sportAmount)+"\n");
	}
	public void onClick(View v){
    	switch (v.getId()) {
		case R.id.button1:
			
			break;
			
		case R.id.button2:
			
			intent = new Intent(this,SportDetailNumActivity.class);
			startActivity(intent);
			break;

		case R.id.button3:
			intent = new Intent(this,SportMapActivity.class);
			startActivity(intent);
			break;
			
		case R.id.bt_start:
			if (weight<=10) {
				Toast.makeText(getApplicationContext(), "输先输入体重以便准确测量！", Toast.LENGTH_SHORT).show();
			}else{
				if (SportAmountService.isStart) {
					Toast.makeText(getApplicationContext(), "不要重复打开服务", Toast.LENGTH_SHORT).show();
				}else{
					intent = new Intent(this, SportAmountService.class);//开启服务
					startService(intent);
					mLocClient.start();	
					Toast.makeText(getApplicationContext(), "服务开启", Toast.LENGTH_SHORT).show();
				}
			}
			break;
			
		case R.id.bt_stop:
			if (SportAmountService.isStart) {
				intent = new Intent(this, SportAmountService.class);//关闭服务
				this.stopService(intent);
				mLocClient.stop();
				Toast.makeText(getApplicationContext(), "服务关闭", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(getApplicationContext(), "不要重复关闭服务", Toast.LENGTH_SHORT).show();
			}
			
			break;
			

		case R.id.bt_set_weight:
			showDialog(DIALOG_SET_WEIGHT);
			break;
		}
    }
	
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_SET_WEIGHT:
			return createDialogSetWeight();
		}
		return super.onCreateDialog(id);
	}
	private Dialog createDialogSetWeight() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_input_add);
		builder.setTitle("请输入体重 单位（KG）");
		editText_setWeight = new EditText(this);
		editText_setWeight.setSingleLine();
		editText_setWeight.setInputType(InputType.TYPE_CLASS_NUMBER);
		builder.setView(editText_setWeight);
		builder.setPositiveButton("确定", this);
		builder.setNegativeButton("取消", null);
		setWeight = builder.show();
		return setWeight;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DIALOG_SET_WEIGHT:
			if (weight!=0) {
				editText_setWeight.setText(""+weight);
			}else{
				editText_setWeight.setText("");
			}
			editText_setWeight.setHint("输入数字（>20） 例如:100");
			break;
		}
		super.onPrepareDialog(id, dialog);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
    
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		weight = 0;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			if (!"".equals(editText_setWeight.getText().toString())) {
				weight = Integer.valueOf(editText_setWeight.getText().toString());
				if (weight<=20) {
					Toast.makeText(getApplicationContext(), "输入正确的体重", Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(getApplicationContext(), "请输入数字   单位：(KG)", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
}
