package com.webview.main;

import java.io.File;
import java.io.IOException;

import org.json.JSONObject;

import tw.com.prolific.driver.pl2303.PL2303Driver;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.webview.content.ViewContent;
import com.webview.http.IRequestListener;
import com.webview.http.InterfaceOp;
import com.webview.movie.MediaPlaybackService;
import com.webview.movie.MovieUtils;
import com.webview.movie.MyFloatView;
import com.webview.receiver.Receiver;
import com.webview.util.AppConstants;
import com.webview.util.FileUtil;
import com.webview.util.StringUtils;
import com.webview.util.UploadLog;
/**
 * 主界面
 * @author Arlen.he
 *
 */
public class MainActivity extends Activity {
	private String TAG = "ShowView MainActivity Log";
	//private static final boolean SHOW_DEBUG = true;
	
	/*
	PL2303Driver mSerial;
	//BaudRate.B4800, DataBits.D8, StopBits.S1, Parity.NONE, FlowControl.RTSCTS
	private static String mOldCar = "";
	private PL2303Driver.BaudRate mBaudrate = PL2303Driver.BaudRate.B115200;
	private PL2303Driver.DataBits mDataBits = PL2303Driver.DataBits.D8;
	private PL2303Driver.Parity mParity = PL2303Driver.Parity.NONE;
	private PL2303Driver.StopBits mStopBits = PL2303Driver.StopBits.S1;
	private PL2303Driver.FlowControl mFlowControl = PL2303Driver.FlowControl.OFF;
	private static final String ACTION_USB_PERMISSION = "com.prolific.pl2303hxdsimpletest.USB_PERMISSION";
	*/
	
	TextView message_show;
	private CreateLayout layout;
	
	/*
	private Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ViewContent.JETTY_TO_URL:
				Log.i("main", "刷新");
				if(layout!=null){
					layout.removeAllViews();
				}
				layout=new CreateLayout(MainActivity.this);
				setContentView(layout);
				break;
			default: break;
			}
		}
	};
	*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		AppConstants.RESOLUTION = this.getDisplayScreenSize();
		message_show=(TextView) findViewById(R.id.message_show);
		message_show.setText("节目正在加载中...");
		
		//webView窗口在onResume()函数中创建
		
		/*
		// get service
		mSerial = new PL2303Driver((UsbManager) getSystemService(Context.USB_SERVICE),this, ACTION_USB_PERMISSION);
		// check USB host function.
		if (!mSerial.PL2303USBFeatureSupported()) {
			Toast.makeText(this, "No Support USB host API", Toast.LENGTH_SHORT).show();
			Log.d("ShowView", "No Support USB host API");
			mSerial = null;
		}
		*/
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    // 如果是返回键,直接返回到桌面
	        if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME){
	    		if(MyFloatView.surfaceView!=null&&MyFloatView.mPlayViewStatus){
	    			//关闭视频窗口
	    			MovieUtils.movieViewClose(this);
	    		}
	        }
	        return super.onKeyDown(keyCode, event);
	    }
	//屏蔽返回键的代码:
//	@Override
//	public boolean onKeyDown(int keyCode,KeyEvent event)
//	{
//		switch(keyCode)
//		{
//			case KeyEvent.KEYCODE_HOME:return true;
//			case KeyEvent.KEYCODE_BACK:return true;
//			case KeyEvent.KEYCODE_CALL:return true;
//			case KeyEvent.KEYCODE_SYM: return true;
//			case KeyEvent.KEYCODE_VOLUME_DOWN: return true;
//			case KeyEvent.KEYCODE_VOLUME_UP: return true;
//			case KeyEvent.KEYCODE_STAR: return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
	
	//	@Override
	//	protected void finalize() throws Throwable {
	//		// TODO Auto-generated method stub
	//		super.finalize();
	//	}

	//	private void getReceiver(){
	//		Receiver receiver=new Receiver(MainActivity.this, mHandler);
	//		IntentFilter filter=new IntentFilter();
	//		filter.addAction(ViewContent.JETTY_SERVICE_URI);
	//		this.registerReceiver(receiver, filter);
	//	}

	@Override
	protected void onPause() {
		if(MyFloatView.surfaceView!=null&&MyFloatView.mPlayViewStatus){
			//关闭视频窗口
			if (MyFloatView.mPlayViewStatus)
			{
				MyFloatView.onExit();
		        Intent mIntent = new Intent("removeUI");
		        mIntent.setClass(this,MediaPlaybackService.class);
		        startService(mIntent);
			}
		}
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//String action =  getIntent().getAction();
		layout=new CreateLayout(MainActivity.this);
		setContentView(layout);
		Intent mIntent = new Intent();
		mIntent.setAction(ViewContent.JETTY_SERVICE_URI);
		sendBroadcast(mIntent);
		UploadLog.start();
		new Thread(new WeatherThread()).start();
		new Thread(new readVideoFilesFromExtMem()).start();
		
		/*
		if(prepareUsbSerial())
		{
			openUsbSerial();
			new Thread(new readDataFromSerialThread()).start();
		}
		*/
	}
	
	public void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(CreateLayout.webView!=null&&CreateLayout.webView.length>0){
			for (int i = 0; i < CreateLayout.webView.length; i++) {
				CreateLayout.webView[i].removeAllViews();
				CreateLayout.webView[i].destroy();
			}
		}
		CreateLayout.bottomWebView.removeAllViews();
		CreateLayout.bottomWebView.destroy();
		super.onDestroy();
	}

	/*
	private boolean prepareUsbSerial()
	{
		//if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action))        
		if(!mSerial.isConnected()) {
			if (SHOW_DEBUG) {
				Log.d(TAG, "New instance : " + mSerial);
			}

			if( !mSerial.enumerate() ) {

				Toast.makeText(this, "no more devices found", Toast.LENGTH_SHORT).show();     
				return false;
			} else {
				Log.d(TAG, "onResume:enumerate succeeded!");
			}    		 
		}//if isConnected  
		Toast.makeText(this, "attached", Toast.LENGTH_SHORT).show();
		
		int res = 0;
		try {
			res = mSerial.setup(mBaudrate, mDataBits, mStopBits, mParity, mFlowControl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if( res<0 ) {
			Log.d("ShowView", "fail to setup");
			return false;
		}
		return true;
	}
	*/
	
	/*
	private void openUsbSerial() {
		Log.d("openUsbSerial", "Enter  openUsbSerial");
		if(null==mSerial)
		{
			Log.d("openUsbSerial", "mSerial is null.");
			return;   	 
		}
		if (mSerial.isConnected()) {
			String str = "115200";//PL2303HXD_BaudRate_spinner.getSelectedItem().toString();
			int baudRate= Integer.parseInt(str);
			switch (baudRate) {
			case 9600:
				mBaudrate = PL2303Driver.BaudRate.B9600;
				break;
			case 19200:
				mBaudrate =PL2303Driver.BaudRate.B19200;
				break;
			case 115200:
				mBaudrate =PL2303Driver.BaudRate.B115200;
				break;
			default:
				mBaudrate =PL2303Driver.BaudRate.B9600;
				break;
			}   		            
			Log.d("openUsbSerial", "baudRate:"+baudRate);
			// if (!mSerial.InitByBaudRate(mBaudrate)) {
			if (!mSerial.InitByBaudRate(mBaudrate,700)) {
				if(!mSerial.PL2303Device_IsHasPermission()) {
					Toast.makeText(this, "cannot open, maybe no permission", Toast.LENGTH_SHORT).show();		
				}

				if(mSerial.PL2303Device_IsHasPermission() && (!mSerial.PL2303Device_IsSupportChip())) {
					Toast.makeText(this, "cannot open, maybe this chip has no support, please use PL2303HXD / RA / EA chip.", Toast.LENGTH_SHORT).show();
				}
			} else {        	      
				Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();        	   
			}
		}//isConnected

		Log.d("openUsbSerial", "Leave openUsbSerial");
	}//openUsbSerial
	*/
	
	public class readVideoFilesFromExtMem implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				try {
					//readDataFromSerial();
					runOnUiThread(new Runnable() {
					     @Override
					     public void run() {
					      //Toast.makeText(MainActivity.this, "UI操作...", 1000).show(); 
					    	 //获取指定目录文件清单
					    	 FileUtil.getSrcList();
					    	 //判断文件清单是否有变化
					    	 //
					     }
					    });
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
	public class readDataFromSerialThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				try {
					readDataFromSerial();
					runOnUiThread(new Runnable() {
					     @Override
					     public void run() {
					      Toast.makeText(RunOnUIThreadActivity.this, "UI操作...", 1000).show(); 
					     }
					    });
					Thread.sleep(1000);//
					//Toast.makeText(RunOnUIThreadActivity.this, "UI操作...", 1000).show(); 
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	*/

	/*
	private void readDataFromSerial() {
		StringBuffer sbOldCar = new StringBuffer();
		//String oldCar = "";
		int len;
		// byte[] rbuf = new byte[4096];
		byte[] rbuf = new byte[1024];
		StringBuffer sbHex=new StringBuffer();
		Log.d("readDataFromSerial", "Enter readDataFromSerial");
		if(null==mSerial)
			return;        
		//Log.d("readDataFromSerial 1 ", "Enter readDataFromSerial");
		if(!mSerial.isConnected()) 
			return;
		len = mSerial.read(rbuf);
		if(len<0) {
			Log.d("readDataFromSerial:", "Get nothing....");
			return;
		}
		if (len > 0) {
			//Log.d("readDataFromSerial:", "Get some Cars.");
			for(int j=0; j<len; j++)
			{
				////////////////////
				String temp=Integer.toHexString(rbuf[j]&0x000000FF);
				String temp1=Integer.toHexString(rbuf[j+1]&0x000000FF);
				String temp2=Integer.toHexString(rbuf[j+2]&0x000000FF);
				if(temp.equals("ff") && temp1.equals("50") && temp2.equals("52"))
				{
					//Log.d("readDataFromSerial:", "Get pack start...");
					String temp3=Integer.toHexString(rbuf[j+3]&0x000000FF);
					String temp4=Integer.toHexString(rbuf[j+4]&0x000000FF);
					String temp5=Integer.toHexString(rbuf[j+5]&0x000000FF);
					String temp6=Integer.toHexString(rbuf[j+6]&0x000000FF);
					if(temp3.equals("1"))
					{
						if(temp4.equals("d4") && temp5.equals("c1") && temp6.equals("42"))
						{
							//Log.d("readDataFromSerial:", "Get one Cars start with 粤B.");
							String temp7=Integer.toHexString(rbuf[j+7]&0x000000FF);
							if(temp7.equals("41") && !mOldCar.equals("41"))
							{
								//Log.d("readDataFromSerial:", "Get new Cars start with 粤B A.");
								//Toast.makeText(this, "粤B*A7U64", Toast.LENGTH_LONG).show();
								mOldCar = "41";
								MovieUtils.movieViewStarPlay(this, 0, 0, 1366,768,"[{\"file\":\"upload/AudiAqcSportback.mp4\"}]");
								//MovieUtils.movieViewStarPlay(this, 0, 0, 1366,768, "[{\"file\":\"upload/o3/e9dddfdfcbf5ebc8620de0c22a5a23b81420556409.mp4\"}]");
							}
							else if(temp7.equals("53") && !mOldCar.equals("53"))
							{
								//Toast.makeText(this, "粤B*S572A", Toast.LENGTH_LONG).show();
								//Log.d("readDataFromSerial:", "Get new Cars start with 粤B S.");
								mOldCar = "53";
								MovieUtils.movieViewStarPlay(this, 0, 0, 1366,768,"[{\"file\":\"upload/MercedesBenzAClassbcParking.mp4\"}]");
								//MovieUtils.movieViewStarPlay(this, 0, 0, 1366,768,"[{\"file\":\"upload/o11/big_note5.mp4\"}]");
							}
							else
							{
								//MovieUtils.movieViewStarPlay(this, 0, 0, 1366,768,"[{\"file\":\"upload/o11/cms3.mp4\"}]");
							}
								
						}
					}
					else
					{
						Log.d("readDataFromSerial:", "too many Cars....");
					}
				}
			}
		}
		else {     	
			if (SHOW_DEBUG) {
				//Log.d("readDataFromSerial", "read len : 0 ");
			}
			//etRead.setText("empty");
			return;
		}
		//Log.d("readDataFromSerial", "Leave readDataFromSerial");	
	}//readDataFromSerial
	*/
	
	public class WeatherThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				try {
					// protoFetchWeather
					InterfaceOp.protoFetchWeather(new IRequestListener() {
						public void onError(Exception e) {
						}
						public void onComplete(boolean isError, String errMsg,
								String resString, JSONObject respObj) {
							if (!resString.isEmpty()) {
								String weatherData = resString.substring(
										resString.indexOf("var"),
										resString.indexOf("window"));
								weatherData = weatherData.replaceFirst("w",
										"weather_data");
								weatherData = weatherData.replaceFirst(";w",
										";weather_data");
								weatherData = weatherData.replaceFirst("add",
										"weather_time");
								try {
									String weatherDataFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/jetty/webapps/console/data/weather_data.js";
									File weatherDataFile = new File(weatherDataFilePath);
									if(weatherDataFile.exists())
										StringUtils.writeFile(weatherDataFilePath,weatherData);
									else
										Log.d("WeatherThread", "weatherDataFile not exists!");
								} catch (Exception e) {
									e.printStackTrace();
								}							}
						}
					});
					Thread.sleep(1000 * 60 * 30);// 30分钟刷新一次
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getDisplayScreenSize()
    {
        WindowManager w = getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try
            {
                widthPixels = (Integer)Display.class.getMethod("getRawWidth").invoke(d);
                heightPixels = (Integer)Display.class.getMethod("getRawHeight").invoke(d);
            }
            catch (Exception ignored)
            {
                Log.e("-->smallstar","getDisplayScreenSize error");
            }
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17)
            try
            {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize",Point.class).invoke(d,realSize);
                widthPixels = realSize.x;
                heightPixels = realSize.y;
            }
            catch (Exception ignored)
            {
                Log.e("-->smallstar","getDisplayScreenSize error");
            }
        return String.valueOf(widthPixels) + "*" + String.valueOf(heightPixels);
    }
}
