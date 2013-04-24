package com.jsu.tangcheng.androidsportamount.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.jsu.tangcheng.androidsportamount.activity.MainActivity;
import com.jsu.tangcheng.androidsportamount.application.SportLocation;
import com.jsu.tangcheng.androidsportamount.database.DBHelper;
import com.jsu.tangcheng.androidsportamount.database.MyData;
import com.jsu.tangcheng.androidsportamount.database.SaveData;
import com.jsu.tangcheng.androidsportamount.util.RotateUtil;

public class SportAmountService extends Service implements SensorEventListener{


	private SQLiteDatabase db;
	
	SaveData saveData;
	int newDay;
	int[] lastTime = new int[2];
	int[] currentTime = new int[2];
	
	protected static final double K = 4.924;
	private Timer timer = new Timer();
	public static double sportAmount;
	private double amount;
	private float[] accelerometerValues = new float[3];  
	private float[] magneticFieldValues = new float[3];
	private float[] accelerometerValues2;

	private long lastUpdateTime;
	private long timeInterval;
	public static double sportAmountCache;  
	//传感器
	private SensorManager sensorManager;
	private Sensor sensorAccelerometer;
	
	protected double height;
	public static final double G = 9.80;

	static double[] g0={0.0,0.0,-G};
	protected double oz;
	protected double ox;
	protected double oy;
	
	//重力分量
	protected double[] g1;
	protected double[] g2;
	protected double[] g3;
	private List<double[]> g;
	//加速度 三分量
	private List<Float> Ax;
	private List<Float> Ay;
	private List<Float> Az;
	private Sensor mSensor;

	public static boolean isStart;
	private boolean isNextTime = false;
	private Handler handler = new Handler(){
		

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				sportAmount += (amount*K)*MainActivity.weight;
				amount = 0;
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
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		isStart = true;
        //获得手机方位
		initData();
		registerSensor();
		timer.schedule(task, 5000, 600);
		
	}

	private void initData() {
		g = new ArrayList<double[]>();
		Ax = new ArrayList<Float>();
		Ay = new ArrayList<Float>();
		Az = new ArrayList<Float>();
		g.add(g0);
		g.add(g0);
		Ax.add((float) 0.0);
		Ay.add((float) 0.0);
		Az.add((float) 0.0);
		Ax.add((float) 0.0);
		Ay.add((float) 0.0);
		Az.add((float) 0.0);
		db = new DBHelper(getApplicationContext()).getWritableDatabase();
		saveData = new SaveData(getApplicationContext());
		MyData.select(db);
	}

	private void registerSensor() {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);  
		sensorManager.registerListener(this, mSensor,SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, sensorAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
	}
	

	
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor == mSensor){
			magneticFieldValues = event.values;
			if (isStart) {
				calculateOrientation(accelerometerValues,magneticFieldValues);
			}
		}
		
		if (event.sensor == sensorAccelerometer) {
			accelerometerValues = event.values;
			accelerometerValues2 = event.values;
		}
	}

	/**
	 * 获得手机实时加速度
	 * @param accelerometerValues
	 * @param magneticFieldValues
	 */
	private void calculateOrientation(float[] accelerometerValues,float[] magneticFieldValues) {
		
		// 现在检测时间
        long currentUpdateTime = System.currentTimeMillis();
        if (lastUpdateTime!=0) {
            // 两次检测的时间间隔
            timeInterval = currentUpdateTime - lastUpdateTime;
		}else{
			timeInterval = 1;
		}
        // 现在的时间变成last时间
        lastUpdateTime = currentUpdateTime;
		float[] values = new float[3];  
        float[] R = new float[9];          
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(R, values);
        // 要经过一次数据格式的转换，转换为度  
        System.out.println("r"+ R);
        ox = (float) Math.toDegrees(values[0]);  
        oy = (float) Math.toDegrees(values[1]);  
        oz = (float) Math.toDegrees(values[2]); 
		System.out.println("ax: "+ox+" ay: "+oy+" az: "+oz);//不变
		g3 = RotateUtil.YRotate(oy, RotateUtil.XRotate(ox, RotateUtil.ZRotate(oz, new double[]{0.0,0.0,-G})));
		if (!isNextTime) {
			g.set(0, g3);
			Ax.set(0, accelerometerValues2[0]);
			Ay.set(0, accelerometerValues2[1]);
			Az.set(0, accelerometerValues2[2]);
		}else{
			g.set(1, g3);
			Ax.set(1, accelerometerValues2[0]);
			Ay.set(1, accelerometerValues2[1]);
			Az.set(1, accelerometerValues2[2]);
		}
		isNextTime = !isNextTime;
		getCalorie(g,Ax,Ay,Az);
      }  

	/**
	 * 通过手机实时加速度计算得到实时耗氧量
	 * @param g
	 * @param ax
	 * @param ay
	 * @param az
	 */
	protected void getCalorie(List<double[]> g, List<Float> ax, List<Float> ay, List<Float> az) {
			//单位质量每分钟的耗氧量L
		double newAmount = (3.7408*(Math.abs(ax.get(0)-g.get(0)[0])+
				Math.abs(ay.get(0)-g.get(0)[1])+
				Math.abs(az.get(0)-g.get(0)[2])+
				Math.abs(ax.get(1)-g.get(1)[0])+
				Math.abs(ay.get(1)-g.get(1)[1])+
				Math.abs(az.get(1)-g.get(1)[2]))*2000-2.4918)*((timeInterval*Math.pow(10, -11))/60);
		if (Math.abs(newAmount-amount)>=1.20e-5) {
			amount +=newAmount;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		saveDB();
		isStart = false;
		sportAmountCache = sportAmount;
		sportAmount = 0;
		amount = 0;
		SportLocation.distance = 0;
		timer.cancel();
		this.stopSelf();
		Intent service = new Intent(this, this.getClass());
		this.getApplicationContext().stopService(service );
		unregisterSensor();
	}

	private void unregisterSensor() {
		sensorManager.unregisterListener(this, sensorAccelerometer);
		sensorManager.unregisterListener(this, mSensor);
	}

	private void saveDB() {
		currentTime = saveData.judgeTime();
		lastTime = MyData.time;
		System.out.println(MyData.time[0] + " time " + MyData.time[1]);
		System.out.println(MyData.time + " MyData.time");
		if (lastTime[0] != 0) {
			if (lastTime[0] != currentTime[0]) {// 月份不等
				System.out.println("月份不等");
				newDay = 0;
			} else {
				if (lastTime[1] != currentTime[1]) {// 日期不等
					System.out.println("日期不等");
					newDay = 1;
				} else {// 日期相同
					System.out.println("日期相同");
					newDay = 2;
				}
			}

			String cTime = currentTime[0] + "|" + currentTime[1];
			String lTime = lastTime[0] + "|" + lastTime[1];
			SaveData.id = MyData.ids;
			System.out.println(SportAmountService.sportAmount+" sportAmount\n"+MyData.lastcalorie+" lastcalorie\n "+(float) (SportAmountService.sportAmount + MyData.lastcalorie)+" float");
			saveData.dealData(
					new MyData(cTime,(float) 0,((float) SportAmountService.sportAmount + MyData.lastcalorie)),lTime, newDay);
			lastTime = currentTime;

		}

	}
}
