package com.jsu.tangcheng.androidsportamount.application;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.jsu.tangcheng.androidsportamount.activity.MainActivity;

public class SportLocation extends Application {

	private static SportLocation mInstance = null;
    public boolean m_bKeyRight = true;
    public BMapManager mBMapManager = null;

    public static final String strKey = "ADFA41C930C38A5AB18E7B28B14B5DF3D117A4E0";
    
    @Override
	//建议在您app的退出之前调用mapadpi的destroy()函数，避免重复初始化带来的时间消耗
	public void onTerminate() {
		// TODO Auto-generated method stub
	    if (mBMapManager != null) {
            mBMapManager.destroy();
            mBMapManager = null;
        }
	    geoPoints = new ArrayList<GeoPoint>();
	    distance = 0;
	    MainActivity.weight = 0;
		super.onTerminate();
	}

	public static SportLocation getInstance() {
		return mInstance;
	}
	public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(strKey,new MyGeneralListener())) {
            Toast.makeText(SportLocation.getInstance().getApplicationContext(), 
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
	}
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
    public static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(SportLocation.getInstance().getApplicationContext(), "您的网络出错啦！",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(SportLocation.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //授权Key错误：
                Toast.makeText(SportLocation.getInstance().getApplicationContext(), 
                        "请在 DemoApplication.java文件输入正确的授权Key！", Toast.LENGTH_LONG).show();
                SportLocation.getInstance().m_bKeyRight = false;
            }
        }
    }
    
    
	
	//**************************************//
	
	public LocationClient mLocationClient = null;
	private String mData;  
	public MyLocationListenner myListener = new MyLocationListenner();
	public TextView mTv;
	public static List<GeoPoint> geoPoints;
	public NotifyLister mNotifyer=null;
	public Vibrator mVibrator01;
	public static double distance;
	public static String sb;
	
	@Override
	public void onCreate() {
		mLocationClient = new LocationClient( getApplicationContext() );
		mLocationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开gps
        option.setCoorType("bd09ll");     //设置坐标类型
        option.setScanSpan(10000);
        option.setAddrType("all");  // 设置是否要返回地址信息，默认为无地址信息 
        mLocationClient.setLocOption(option);
		geoPoints = new ArrayList<GeoPoint>();
		super.onCreate(); 
		mInstance = this;
		initEngineManager(this);
	}
	
	

	/**
	 * 显示字符串
	 * @param str
	 */
	public void logMsg(String str) {
		try {
			mData = str;
			if ( mTv != null )
				mTv.setText(mData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override 
		public void onReceiveLocation(BDLocation location) {
			System.out.println("aonCreate4");
			if (location == null)
				return;
			System.out.println(" getLatitude"+ (int)(location.getLatitude()* 1e6)+"getLongitude "+(int)(location.getLongitude() *  1e6));
			if (geoPoints.size()>0) {
				double newDistance = DistanceUtil.getDistance(
						new GeoPoint((int)(location.getLatitude()* 1e6), (int)(location.getLongitude() *  1e6)), 
						geoPoints.get(geoPoints.size()-1));
				
					if(newDistance>=1000){		
						distance += newDistance;
						geoPoints.add(new GeoPoint((int)(location.getLatitude()* 1e6), (int)(location.getLongitude() *  1e6)));
					}

			}else {
				geoPoints.add(new GeoPoint((int)(location.getLatitude()* 1e6), (int)(location.getLongitude() *  1e6)));
			}
			if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb = "\n省："+location.getProvince()+"\n市："+location.getCity()+"\n区/县："+location.getDistrict()+"\n街道 : "+location.getAddrStr();
			}
		}
		
		public void onReceivePoi(BDLocation poiLocation) {
			System.out.println("aonCreate3");
			if (poiLocation == null){
				return ;
			}
		}
	}
	
	
	/**
	 * 位置提醒回调函数
	 */
	public class NotifyLister extends BDNotifyListener{
		public void onNotify(BDLocation mlocation, float distance){
			mVibrator01.vibrate(1000);
		}
	}
}