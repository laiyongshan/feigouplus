package com.example.youhe.youhecheguanjiaplus.ui.base;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;
import com.lkl.cloudpos.aidl.AidlDeviceService;

import gen.com.dc.pos.aidl.AidlDcService;


public abstract class BaseTestActivity extends Activity {
	private final static String TAG = "P92Pay";

	public static final int SHOW_MSG = 0;

	public static final String LKL_SERVICE_ACTION = "lkl_cloudpos_device_service";
	public static final String DC_SERVICE_ACTION = "dc_pos_device_service";

	private int showLineNum = 0;

	private LinearLayout linearLayout;
	private ScrollView scrollView;
	private TextView textView1;
	private TextView textView2;

	private static double dMoney = 0.01;

	public LinearLayout rightButArea = null;

	//设别服务连接桥
	private ServiceConnection conn = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
			Log.i(TAG, "aidlService服务连接成功");
			if(serviceBinder != null){	//绑定成功
				AidlDeviceService serviceManager = AidlDeviceService.Stub.asInterface(serviceBinder);
				onDeviceConnected(serviceManager);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "AidlService服务断开了");
		}
	};

	//绑定服务
	public void bindService(){
		Intent intent = new Intent();
		intent.setAction(LKL_SERVICE_ACTION);
		boolean flag = bindService(intent, conn, Context.BIND_AUTO_CREATE);
		if(flag){
			Log.i(TAG, "服务绑定成功");
		}else{
			Log.i(TAG, "服务绑定失败");
		}
	}

	//设别服务连接桥
	private ServiceConnection dcconn = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
			Log.i(TAG, "dcaidlService服务连接成功");
			if(serviceBinder != null){	//绑定成功
				AidlDcService dcserviceManager = AidlDcService.Stub.asInterface(serviceBinder);
				onDcConnected(dcserviceManager);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "dcAidlService服务断开了");
		}
	};

	//绑定服务
	public void bindDcService(){
		Intent intent = new Intent();
		intent.setAction(DC_SERVICE_ACTION);
		boolean flag = bindService(intent, dcconn, Context.BIND_AUTO_CREATE);
		if(flag){
			Log.i(TAG, "DC服务绑定成功");
		}else{
			Log.i(TAG, "DC服务绑定失败");
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle bundle = msg.getData();
			String msg1 = bundle.getString("msg1");
			String msg2 = bundle.getString("msg2");
			int color = bundle.getInt("color");
			updateView(msg1, msg2, color);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// super.setContentView(R.layout.base_activity);
		linearLayout = (LinearLayout) this.findViewById(R.id.tipLinearLayout);
		scrollView = (ScrollView) this.findViewById(R.id.tipScrollView);
		//rightButArea = (LinearLayout) this.findViewById(R.id.main_linearlayout);
	}


	@Override
	protected void onResume() {
		bindService();
		bindDcService();
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(conn != null){
			this.unbindService(conn);
		}
		if(dcconn != null){
			this.unbindService(dcconn);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 显示信息
	 *
	 * @param
	 * @param color
	 * @createtor：Administrator
	 * @date:2014-9-15 下午9:45:18
	 */
	public void updateView(final String msg1, final String msg2, final int color) {
		if (showLineNum % 300 == 0) { // 显示够20行的时候重新开始
			linearLayout.removeAllViews();
			showLineNum = 0;
		}
		showLineNum++;
		LayoutInflater inflater = getLayoutInflater();
		View v = inflater.inflate(R.layout.show_item, null);
		textView1 = (TextView) v.findViewById(R.id.tip1);
		textView2 = (TextView) v.findViewById(R.id.tip2);
		textView1.setText(msg1);
		textView2.setText(msg2);
		textView1.setTextColor(Color.BLACK);
		textView2.setTextColor(color);
		textView1.setTextSize(20);
		textView2.setTextSize(20);
		linearLayout.addView(v);
		scrollView.post(new Runnable() {
			public void run() {
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});

	}

	/**
	 * 更新UI
	 *
	 * @param msg1
	 * @param msg2
	 * @param color
	 * @createtor：Administrator
	 * @date:2014-11-29 下午7:01:16
	 */
	public void showMessage(final String msg1, final String msg2, final int color) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("msg1", msg1);
		bundle.putString("msg2", msg2);
		bundle.putInt("color", color);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	// 显示单条信息
	public void showMessage(final String msg1, final int color) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("msg1", msg1);
		bundle.putString("msg2", "");
		bundle.putInt("color", color);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	public void showMessage(String str) {
		this.showMessage(str, Color.BLACK);
	}

	public void clearMessage() {
		if(linearLayout !=null){
			int count = linearLayout.getChildCount();
			for(int i=0;i<count;i++){
				View v = linearLayout.getChildAt(i);
				if(v != null){
					linearLayout.removeView(v);
				}
			}
		}
	}

	public void setMoney(double money) {
		dMoney = money;
	}

	public double getMoney() {
		return dMoney;
	}

	/**
	 * 断开服务
	 */
	public  final void stopAidl(Activity activity){
		activity.unbindService(conn);
		Log.i("WU","解绑服务");
	}
	/**
	 * 服务连接成功时回调
	 * @param serviceManager
	 * @createtor：Administrator
	 * @date:2015-8-4 上午7:38:08
	 */
	public abstract void onDeviceConnected(AidlDeviceService serviceManager);

	/**
	 * 服务连接成功时回调
	 * @param serviceManager
	 * @createtor：Administrator
	 * @date:2015-8-4 上午7:38:08
	 */
	public abstract void onDcConnected(AidlDcService serviceManager);
}