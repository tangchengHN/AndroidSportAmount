package com.jsu.tangcheng.androidsportamount.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.jsu.tangcheng.androidsportamount.R;
import com.jsu.tangcheng.androidsportamount.application.SportLocation;
public class SportMapActivity extends Activity {
	
	static MapView mMapView = null;
	
	private MapController mMapController = null;

	public MKMapViewListener mMapListener = null;
	FrameLayout mMapViewContainer = null;
	
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
    public NotifyLister mNotifyer=null;
	
	Button testUpdateButton = null;
	
	EditText indexText = null;
	MyLocationOverlay myLocationOverlay = null;
	int index =0;
	LocationData locData = null;
	
	Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Toast.makeText(SportMapActivity.this, "msg:" +msg.what, Toast.LENGTH_SHORT).show();
        };
    };

	private GraphicsOverlay graphicsOverlay = null;

	private Intent intent;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SportLocation app = (SportLocation)this.getApplication(); 
        System.out.println(SportLocation.geoPoints.toString()+" geoPoints0"); 
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            app.mBMapManager.init(SportLocation.strKey,new SportLocation.MyGeneralListener());
            System.out.println(SportLocation.geoPoints.toString()+" geoPoints1");
            if (mLocClient != null && mLocClient.isStarted()){
				setLocationOption();
				mLocClient.requestLocation();	 
				System.out.println(SportLocation.geoPoints.toString()+" geoPoints2");
			}	
        }
        
        setContentView(R.layout.activity_sportmap);
        mMapView = (MapView)findViewById(R.id.bmapView);
        mMapController = mMapView.getController();
        
        initMapView();
        
        mLocClient = new LocationClient( this );
        mLocClient.registerLocationListener( myListener );
        
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开gps
        option.setCoorType("bd09ll");     //设置坐标类型
        option.setScanSpan(5000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        mMapView.getController().setZoom(14);
        mMapView.getController().enableClick(true);
        
        mMapView.setBuiltInZoomControls(true);
        mMapListener = new MKMapViewListener() {
			
			@Override
			public void onMapMoveFinish() {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				// TODO Auto-generated method stub
				String title = "";
				if (mapPoiInfo != null){
					title = mapPoiInfo.strText;
					Toast.makeText(SportMapActivity.this,title,Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onGetCurrentMap(Bitmap b) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onMapAnimationFinish() {
				// TODO Auto-generated method stub
				
			}
		};
		mMapView.regMapViewListener(SportLocation.getInstance().mBMapManager, mMapListener);
		myLocationOverlay = new MyLocationOverlay(mMapView);
		locData = new LocationData();
	    myLocationOverlay.setData(locData);

        graphicsOverlay = new GraphicsOverlay(mMapView);
		mMapView.getOverlays().add(myLocationOverlay);
		mMapView.getOverlays().add(graphicsOverlay);
		myLocationOverlay.enableCompass();
		mMapView.refresh();
		
		testUpdateButton = (Button)findViewById(R.id.button1);
		OnClickListener clickListener = new OnClickListener(){
				public void onClick(View v) {
					testUpdateClick();
				}
	        };
	    testUpdateButton.setOnClickListener(clickListener);
	    if (SportLocation.geoPoints.size()>0) {
		    drawLine();
		}
    }
    
    private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);				//打开gps
		option.setCoorType("bd09ll");		//设置坐标类型
		option.setScanSpan(5000);	//设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
		mLocClient.setLocOption(option);
	}

	@Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }
    
    
    @Override
    protected void onDestroy() {
        if (mLocClient != null)
            mLocClient.stop();
        mMapView.destroy();
        SportLocation app = (SportLocation)this.getApplication();
        if (app.mBMapManager != null) {
            app.mBMapManager.destroy();
            app.mBMapManager = null;
        }
        super.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	mMapView.onSaveInstanceState(outState);
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	mMapView.onRestoreInstanceState(savedInstanceState);
    }
    
    public void testUpdateClick(){
        mLocClient.requestLocation();
    }
    private void initMapView() {
        mMapView.setLongClickable(true);
        //mMapController.setMapClickEnable(true);
        //mMapView.setSatellite(false);
    }
   

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	
	/**
     * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;
            
            locData.latitude = location.getLatitude();
            locData.longitude = location.getLongitude();
            locData.accuracy = location.getRadius();
            locData.direction = location.getDerect();
            myLocationOverlay.setData(locData);
            mMapView.refresh();
            mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)), mHandler.obtainMessage(1));
        }
        
        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null){
                return ;
            }
        }
    }
    
    public class NotifyLister extends BDNotifyListener{
        public void onNotify(BDLocation mlocation, float distance) {
        }
    }
    
    public void drawLine(){
    	   
  		Geometry lineGeometry = new Geometry();
  		GeoPoint[] linePoints = new GeoPoint[SportLocation.geoPoints.size()];
//  		int lat = (int) (SportLocation.geoPoints.get(SportLocation.geoPoints.size()-1).getLatitudeE6());
//	   	int lon = (int) (SportLocation.geoPoints.get(SportLocation.geoPoints.size()-1).getLongitudeE6());   	
//	   	GeoPoint pt1 = new GeoPoint(lat, lon);
//	   	lat = (int) (lat+1000);
//	   	lon = (int) (lon+200);
//	   	GeoPoint pt2 = new GeoPoint(lat, lon);
//		lat = (int) (lat+1500);
//	   	lon = (int) (lon+1620);
//	    GeoPoint pt3 = new GeoPoint(lat, lon);
//		lat = (int) (lat+1500);
//	   	lon = (int) (lon+2300);
//	    GeoPoint pt4 = new GeoPoint(lat, lon);
//	    
//  		GeoPoint[] linePoints = new GeoPoint[4];
//  		linePoints[0] = SportLocation.geoPoints.get(SportLocation.geoPoints.size()-1);
//  		linePoints[1] = pt2;
//  		linePoints[2] = pt3;
//  		linePoints[3] = pt4;
//  		linePoints[0] = SportLocation.geoPoints.get(SportLocation.geoPoints.size()-1);
  		for (int i = 0; i < linePoints.length; i++) {
			linePoints[i] = SportLocation.geoPoints.get(i);
		}
  		lineGeometry.setPolyLine(linePoints);
  		
  		Symbol lineSymbol = new Symbol();
  		Symbol.Color lineColor = lineSymbol.new Color();
  		lineColor.red = 255;
  		lineColor.green = 0;
  		lineColor.blue = 0;
  		lineColor.alpha = 126;
  		lineSymbol.setLineSymbol(lineColor, 10);
  		
  		Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);
  		
  		graphicsOverlay .setData(lineGraphic);
  		mMapView.refresh();
    	
    }
    
    public void onClick(View v){
    	switch (v.getId()) {
		case R.id.button1:

			intent = new Intent(this,MainActivity.class);
			startActivity(intent);
			finish();
			break;
			
		case R.id.button2:
			intent = new Intent(this,SportDetailNumActivity.class);
			startActivity(intent);
			finish();
			break;

		case R.id.button3:
			break;
		}
    }

}


