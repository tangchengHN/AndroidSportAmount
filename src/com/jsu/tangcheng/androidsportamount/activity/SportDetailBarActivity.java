package com.jsu.tangcheng.androidsportamount.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;

import com.jsu.tangcheng.androidsportamount.R;
import com.jsu.tangcheng.androidsportamount.database.DBHelper;
import com.jsu.tangcheng.androidsportamount.database.MyData;
import com.jsu.tangcheng.androidsportamount.database.SaveData;

public class SportDetailBarActivity extends Activity{

	private GraphicalView graphicalView;
	private XYMultipleSeriesDataset dataset;
	private XYMultipleSeriesRenderer renderer;
	private CategorySeries series;
	private SimpleSeriesRenderer r;	
	private SQLiteDatabase db;
	private String[] titles;
	
	private String month;
	private List<MyData> list;
	private ArrayList<ArrayList<Float>> values;
	
	private final Timer timer = new Timer();
	private TimerTask task = new TimerTask() {
		@Override
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	};
//	private int i;
	Handler handler = new Handler() {


		@Override
		public void handleMessage(Message msg) {
			renderer.removeSeriesRenderer(r);
			dataset.removeSeries(series.toXYSeries());
			series.toXYSeries().clear();
			values.clear();
			initData();
			graphicalView = null;
			initSeries();
			graphicalView.invalidate();
			System.out.println(values.toString());
		}
	};
private ArrayList<Float> calorieList;
//private float distanceMax;
private float calorieMax;
private SaveData saveData;
private int ids;
private Intent intent;
//private float Max;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sportdetail_bar);
		db = new DBHelper(this).getWritableDatabase();
		
		String date;
		date = String.valueOf(new Date());
		Calendar cal = Calendar.getInstance();
		date = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
		month = String.valueOf(cal.get(Calendar.MONTH)+1);
		date = month+"|"+date;
		System.out.println(date);
		
		list = new ArrayList<MyData>();
		list = MyData.select(db);// 得到本月MyData对象集合。
		ids = MyData.ids;
		if (list.size() != 0) {
			
		}else {
			saveData = new SaveData(this);
			saveData.judgeTime();
			for (int i = 1; i < saveData.judgeTime()[1]; i++) {
				int[] time = new int[2];
				time[0] = Integer.valueOf(month);
				time[1] = i;
				MyData myData = new MyData(time[0]+"|"+time[1], (float)0, (float)0);
				saveData.insertDB(myData);
			}
		}
		
		list = MyData.select(db);// 得到本月MyData对象集合。
		db.close();
		initData();
		initSeries();
		timer.schedule(task, 5000, 5000);
	}
	
	private void initData() {

		values = new ArrayList<ArrayList<Float>>(); // 给柱子赋值
		calorieList = new ArrayList<Float>();
		if (list.size() != 0) {
			System.out.println();
			calorieMax = list.get(0).getCalorie();
			System.out.println(list.size()+"saveData list.size()");
			for (int i = list.size() - 1; i >= 1; i--) {
				calorieMax = (list.get(i).getCalorie() >= calorieMax ? list
						.get(i).getCalorie() : calorieMax);
			}
			for (int i = 0; i < list.size(); i++) {
				System.out.println(i+"saveData i");
				calorieList.add(list.get(i).getCalorie());
			}
		}else{
		}
		values.add(calorieList);
	}
	
	private void initSeries() {
		// 每天显示两个柱子
		titles = new String[] { "calorie" };
		Calendar cal = Calendar.getInstance();
		int max = cal.getMaximum(Calendar.DAY_OF_MONTH);// 得到月的最大数
		int[] colors = new int[] { Color.BLUE };

		renderer = buildBarRenderer(colors);// 调用AbstractDemoChart中的方法构建renderer.
		setChartSettings(renderer, "量跑统计（当月）", cal.get(Calendar.MONTH)+1+"月", "量跑值", 0.0, max, 0.0,
				calorieMax * 1.1, Color.CYAN, Color.BLUE);// 调用AbstractDemoChart中的方法设置
		renderer.getSeriesRendererAt(0).setDisplayChartValues(true);// 设置柱子上是否显示数量值
//		renderer.getSeriesRendererAt(1).setDisplayChartValues(true);// 设置柱子上是否显示数量值
		renderer.setXLabels(max);// X轴的格子数
		renderer.setYLabels(10);// Y轴的格子数
		renderer.setXLabelsAlign(Align.CENTER);// 刻度线与X轴坐标文字左侧对齐
		renderer.setYLabelsAlign(Align.RIGHT);// Y轴与Y轴坐标文字左对齐
		renderer.setPanEnabled(false, false);// 允许左右拖动,但不允许上下拖动.
		renderer.setMargins(new int[] { 50, 100, 55, 0 });
		// renderer.setZoomRate(1.1f);// 放大的倍率
		renderer.setZoomEnabled(true);
		renderer.setBarSpacing(0.6f);// 柱子间宽度
		dataset = buildBarDataset(titles, values);
		if (graphicalView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.ll_bar_chart);
			layout.removeAllViews();
			graphicalView = ChartFactory.getBarChartView(this, dataset,
					renderer, Type.DEFAULT);
			layout.addView(graphicalView);
		}
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
		}
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		timer.cancel();
		db.close();
	}
	
	protected XYMultipleSeriesDataset buildBarDataset(String[] titles,
			List<ArrayList<Float>> values) {
		dataset = new XYMultipleSeriesDataset();
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			series = new CategorySeries(titles[i]);
			ArrayList<Float> arrayList = values.get(i);
			// double[] v = values.get(i);
			int seriesLength = arrayList.size();
			for (int k = 0; k < seriesLength; k++) {
				series.add(arrayList.get(k));
			}
			dataset.addSeries(series.toXYSeries());
		}
		return dataset;
	}

	protected void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
	}

	protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
		renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(32);
		renderer.setChartTitleTextSize(40);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(30);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			r = new SimpleSeriesRenderer();
			r.setColor(colors[i]);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}
}
