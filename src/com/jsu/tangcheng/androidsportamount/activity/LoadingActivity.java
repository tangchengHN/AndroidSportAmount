package com.jsu.tangcheng.androidsportamount.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.jsu.tangcheng.androidsportamount.R;

public class LoadingActivity<ProgreeDialog> extends Activity{	
	private ProgreeDialog progreeDialog = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		
		setloading();
	}

	private void setloading() {
		
		progreeDialog = (ProgreeDialog) ProgressDialog.show(this,
				"please wait...", "量跑isLoading...", true);
		new Thread() {
			public void run() {
				try {
					sleep(5000);
					Intent intent = new Intent();
					intent.setClass(LoadingActivity.this, MainActivity.class);
					LoadingActivity.this.startActivity(intent);
					LoadingActivity.this.finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
				((Dialog) progreeDialog).dismiss();
			}
		}.start();
	}

}
