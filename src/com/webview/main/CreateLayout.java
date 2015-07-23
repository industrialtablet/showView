package com.webview.main;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.webview.content.ViewContent;
import com.webview.http.IRequestListener;
import com.webview.http.InterfaceOp;
import com.webview.movie.MyFloatView;
import com.webview.view.FastWebView;

/**
 * 添加布局
 * @author Arlen.he
 *
 */
public class CreateLayout extends RelativeLayout{
	public static JSONArray getXY;
	/**
	 * 显示多少个webView
	 */
	private static int count=0;
	/**
	 * 定义webView数组
	 */
	public static FastWebView[] webView = null; 
	/**
	 * url集合
	 */
	private static List<String> wUrl=null;
	/**
	 * 布局长宽高方位设定
	 */
	private static RelativeLayout.LayoutParams[] rl=null;
	/**
	 * 定义webView长
	 */
	private static List<Integer> wList=null;
	/**
	 * 定义webView宽
	 */
	private static List<Integer> hList=null;;
	/**
	 * x坐标
	 */
	private static List<Integer> x=null;
	/**
	 * Y坐标
	 */
	private static List<Integer> y=null;
	/**
	 * 底层
	 */
	public static WebView bottomWebView;
	
	/**
	 * 主View
	 */
	private RelativeLayout.LayoutParams layoutParams=new LayoutParams(
			RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
	private RelativeLayout rLayout;
private static SurfaceView surfaceView;
	private static final int REMOVE_WEBVIEW=0x11221;
	private Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ViewContent.SERVER_TO_JSON:
				try {
					clearCacheFolder(mContext.getCacheDir(), System.currentTimeMillis());
					//先删除以前的
//					removeView();
					getJson(msg.getData().getString("tojson"));
					createWebView();
					for (int i = 0; i < count; i++) {
						setSettingWebView(webView[i]);
					}
					setWH();
					setWebViewUrl(wUrl);
//					rLayout=new RelativeLayout(mContext);
					for (int i = 0; i < count; i++) {
//						rLayout.addView(webView[i],rl[i]);
						addView(webView[i],rl[i]);
					}
//					addView(rLayout);
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			case REMOVE_WEBVIEW:
				removeView();
				for (int i = 0; i < webView.length; i++) {
//					rLayout.removeViewAt(i);
				}
				break;
			case ViewContent.MAIN_REFRESH:
//				if(MyFloatView.surfaceView!=null&&MyFloatView.mPlayViewStatus){
				//关闭视频窗口
//				if (MyFloatView.mPlayViewStatus)
//				{
//					MyFloatView.mPlayViewPrepareStatus = false;
//					MyFloatView.mPlayViewStatus = false;
//					MyFloatView.onExit();
//				}
				
//				mContext.deleteDatabase("webview.db");  
//				mContext.deleteDatabase("webviewCache.db");
				bottomWebView.clearCache(true);
				clearCacheFolder(mContext.getCacheDir(), System.currentTimeMillis());
				bottomWebView.loadUrl(ViewContent.JETTY_DEMO_URL);
				bottomWebView.reload();
				break;
			}
		}
	};
	
	// clear the cache before time numDays
	public static int clearCacheFolder(File dir, long numDays) {      
		int deletedFiles = 0;     
		if (dir!= null && dir.isDirectory()) {         
			try {            
				for (File child:dir.listFiles()) {
					if (child.isDirectory()) {          
						deletedFiles += clearCacheFolder(child, numDays);      
					}
					if (child.lastModified() < numDays) { 
						if (child.delete()) {               
							deletedFiles++;       
						}
					}
				}         
			} catch(Exception e) {   
				e.printStackTrace();
			} 
		}   
		return deletedFiles; 
	}
	/**
	 * 是否开启计时器
	 */
	public static boolean isGoing=false;
	public static boolean isStop=false;
	public static Timer timer=null;
	public static TimerTask task=null;
	
	
	/**
	 * 开始
	 */
	public static void start(){
		if(!isGoing){
			return;
		}
		if(timer==null){
			timer = new Timer();
		}
		if(task==null){
			task=new TimerTask() { 
				@Override
				public void run() {
					((Activity) mContext).runOnUiThread(new Runnable() {
			            public void run() {
							bottomWebView.clearCache(true);
							clearCacheFolder(mContext.getCacheDir(), System.currentTimeMillis());
							bottomWebView.loadUrl(ViewContent.JETTY_DEMO_URL);
							if(getXY!=null){
								getXY=null;
							}
							timer.cancel();
			            }
			        }); 

				}
			};
		}
		timer.schedule(task, 5000);
	}
	
	/**
	 * 重新开始
	 */
	public static void reStart(){
		if (timer != null)
		{
			timer.cancel();
			timer = null;
		}
		if (task != null)
		{
			task.cancel();
			task = null;
		}
		isStop=false;
		//		isGoing = false;
		start();
	}
	
	/**
	 * 暂停
	 */
	private void stop(){
		isStop = !isStop;
	}
	
	private static Context mContext;
	/**
	 * 构造
	 * @param context
	 * @param json
	 */
	public CreateLayout(Context context) {
		super(context);
		this.mContext=context;
		//注册广播
		getReceiver();
		clearCacheFolder(context.getCacheDir(), System.currentTimeMillis());
		//创建主webView
		createMainWebView();
		addView(bottomWebView,layoutParams);
	}
	
	/**
	 * 主web
	 */
	private  void createMainWebView(){
		bottomWebView=new MainView(mContext);
		bottomWebView.setBackgroundResource(R.drawable.background_2);
		setSettingWebView(bottomWebView);
		bottomWebView.loadUrl(ViewContent.JETTY_DEMO_URL);
		bottomWebView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.i("url", url);
				view.loadUrl(url);
				return true;
			}
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)    
			{ 
				view.stopLoading();
				view.clearView();
				view.loadUrl("file:///android_asset/index.html");
				//				view.reload();刷新
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				//加载完成
				try {
					if(!ViewContent.JETTY_DEMO_URL.equals(url)){
						//开启定时器
						isGoing=true;
						if(timer!=null){
							timer.cancel();
							timer=null;
						}
						if (task != null)
						{
							task.cancel();
							task = null;
						}							
						start();
						if(webView!=null){
							if(webView.length>0){
							//关闭webView
							Intent intent=new Intent();
							intent.setAction(ViewContent.REMOVE_WEBVIEW);
							mContext.sendBroadcast(intent);
							}
						}

						if(MyFloatView.surfaceView!=null&&MyFloatView.mPlayViewStatus){
							//关闭视频窗口
//							if (MyFloatView.mPlayViewStatus)
//							{
//								MyFloatView.mPlayViewPrepareStatus = false;
//								MyFloatView.mPlayViewStatus = false;
//								MyFloatView.onExit();
//							}

						}
					}else{
						isGoing=false;
						isStop=false;
					}
				} catch (Exception e) {
					if(timer!=null){
						timer.cancel();
						timer=null;
					}
					if (task != null)
					{
						task.cancel();
						task = null;
					}	
					e.printStackTrace();
				}

			}

		});

		//js交互接口
		ProxyBridge jsBridge = new ProxyBridge(mContext);
		bottomWebView.addJavascriptInterface(jsBridge,"ia");
	}

	/**
	 * 创建WebView+SurfaceView
	 */
	private static Boolean createWebView(){
		try {
			for (int i = 0; i < count; i++) {
				webView[i]=new FastWebView(mContext);
				//设置ID
//				webView[i].setId(i+1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * 设置所有webView属性
	 */
	private static void setSettingWebView(WebView webView){
		//支持js
		webView.getSettings().setJavaScriptEnabled(true);
//		优先使用缓存
//					webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		//不允许缩放
		webView.getSettings().setSupportZoom(false);
		//不使用缓存
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		//放大缩小
		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setSavePassword(true);
		webView.getSettings().setSaveFormData(true);
		 //webView.getSettings().setPluginsEnabled(true);//可以使用插件
		webView.getSettings().setPluginState(PluginState.ON);
		webView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK);
          //mWebView.getSettings().setCacheMode( WebSettings.LOAD_NO_CACHE);
		webView.clearHistory();
		webView.clearFormData();
		webView.clearCache(true);
		webView.reload();

	}

	/**
	 * 布局方位以及长宽属性设定
	 */
	private static void setWH(){
		rl=new RelativeLayout.LayoutParams[count];
		for (int i = 0; i < count; i++) {
			//定义长宽
			rl[i]=new RelativeLayout.LayoutParams(wList.get(i),hList.get(i));
			//定义偏移距离
			rl[i].leftMargin=x.get(i);
			rl[i].topMargin=y.get(i);
		}
	}

	/**
	 * 定义url
	 * @param url
	 */
	private static void setWebViewUrl(List<String> url){
		for (int i = 0; i < count; i++) {
			ProxyBridge jsBridge = new ProxyBridge(mContext);
			webView[i].addJavascriptInterface(jsBridge,"ia");
			webView[i].loadUrl(url.get(i));
			webView[i].setWebViewClient(new WebViewClient(){
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.clearCache(true);
					view.loadUrl(url);
					return true;
				}
				public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)    
				{ 
					view.stopLoading();
					view.clearView();
					view.loadUrl("file:///android_asset/index.html");
				}
			});
		}
	}
	/**
	 * json解析
	 * @param json
	 */
	private static void getJson(String json){
		wList=new ArrayList<Integer>();
		hList=new ArrayList<Integer>();
		x=new ArrayList<Integer>();
		y=new ArrayList<Integer>();
		wUrl=new ArrayList<String>();
		if(json!=null&&!"".equals(json)){
			JSONArray jList;
			try {
				jList = new JSONArray(json);
				count=jList.length();
				webView = new FastWebView[count];
				for (int i = 0; i < jList.length(); i++) {
					JSONObject obj=jList.getJSONObject(i);
					wList.add(obj.getInt("width"));hList.add(obj.getInt("height"));
					x.add(obj.getInt("left"));y.add(obj.getInt("top"));
					wUrl.add(obj.getString("url"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
				count=0;
			}
		}
	}
	/**
	 * 删除View
	 */
	private void removeView(){
		if(webView!=null){
			for (int i = 0; i < webView.length; i++) {
				removeView(webView[i]);
			}
		}
	}
	/**
	 * 注册广播
	 */
	public  void getReceiver(){
		WebReceiver receiver=new WebReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction(ViewContent.RECEIVER_URI);
		mContext.registerReceiver(receiver, filter);
		IntentFilter filter2=new IntentFilter();
		filter2.addAction(ViewContent.JETTY_SERVICE_URI);
		mContext.registerReceiver(receiver, filter2);
	}

	class WebReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(ViewContent.RECEIVER_URI)){
				String toJson=intent.getStringExtra("tojson");
				Bundle bundle=new Bundle();
				bundle.putString("tojson", toJson);
				Message msg=new Message();
				msg.what=ViewContent.SERVER_TO_JSON;
				msg.setData(bundle);
				mHandler.sendMessage(msg);
			}
			if(intent.getAction().equals(ViewContent.REMOVE_WEBVIEW)){
				Message msg=new Message();
				msg.what=REMOVE_WEBVIEW;
				mHandler.sendMessage(msg);
			}
			if(intent.getAction().equals(ViewContent.JETTY_SERVICE_URI)){
				
				Log.i("广播", "刷新");
				String name=intent.getStringExtra("name");
				if(name!=null){
					if("systemTime".equalsIgnoreCase(name)){
						//获取机器信息
						String result_json=intent.getStringExtra("result_json");
						try {
							JSONObject json=new JSONObject(result_json);
							Map<String, String> params = new LinkedHashMap<String, String>();
							params.put("organize_id", json.getString("organize_id"));
							params.put("imei", json.getString("imei"));
							params.put("sn", json.getString("sn"));
							params.put("mac_ether", json.getString("mac_ether"));
							params.put("mac_wifi", json.getString("mac_wifi"));
							InterfaceOp.refreshIsOk(params, new IRequestListener() {
								
								@Override
								public void onError(Exception e) {
									// TODO Auto-generated method stub
									
								}
								
								@Override
								public void onComplete(boolean isError, String errMsg, String resString,
										JSONObject respObj) {
									try {
										if(respObj.getString("result").equals("true")){
											ViewContent.IS_REFRESH=false;
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
						}
						if(!ViewContent.IS_REFRESH){
							Message msg=new Message();
							msg.what=ViewContent.MAIN_REFRESH;
							mHandler.sendMessage(msg);
							ViewContent.IS_REFRESH=true;
						}
						
					}else{
						Message msg=new Message();
						msg.what=ViewContent.MAIN_REFRESH;
						mHandler.sendMessage(msg);
					}
				}else{
					Message msg=new Message();
					msg.what=ViewContent.MAIN_REFRESH;
					mHandler.sendMessage(msg);
				}
				
			}
		}
	}
	/**
	 * main
	 * @author Administrator
	 *
	 */
	class MainView extends WebView{
		public MainView(Context context) {
			super(context);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if(timer!=null){
				timer.cancel();
				timer=null;
			}
			if (task != null)
			{
				task.cancel();
				task = null;
			}	
			if(isGoing){
				reStart();
			}
			return super.onTouchEvent(event);
		}
		private boolean is_gone = false;
		public MainView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected void onWindowVisibilityChanged(int visibility) {
			super.onWindowVisibilityChanged(visibility);
			if (visibility == View.GONE) {
				try {
					WebView.class.getMethod("onPause").invoke(this);// stop flash
				} catch (Exception e) {
				}
				this.pauseTimers();
				this.is_gone = true;
			} else if (visibility == View.VISIBLE) {
				try {
					WebView.class.getMethod("onResume").invoke(this);// resume flash
				} catch (Exception e) {
				}
				this.resumeTimers();
				this.is_gone = false;
			}
		}

		@Override
		protected void onDetachedFromWindow(){
			if (this.is_gone) {
				try {
					this.destroy();
				} catch (Exception e){
					
				}
			}
		}

		@Override
		public boolean onFilterTouchEventForSecurity(MotionEvent event) {
			// TODO Auto-generated method stub
			int x=(int) event.getX();
			int y=(int) event.getY();
			
			if(getXY!=null){
				for (int i = 0; i < getXY.length(); i++) {
					try {
						JSONObject json=(JSONObject) getXY.get(i);
						JSONArray isx=json.getJSONArray("x");
						JSONArray isy=json.getJSONArray("y");
						if(x>isx.getInt(0)&&x<isx.getInt(1)&&y>isy.getInt(0)&&y<isy.getInt(1)){
							if(json.get("type").equals("pkg")){
								String packageName=json.getString("jump");
								CreateLayout.clearCacheFolder(mContext.getCacheDir(), System.currentTimeMillis());
								Toast.makeText(mContext, packageName, Toast.LENGTH_SHORT).show();
								PackageInfo pi = null;
								try {
									pi = ShowView.getInstance().getPackageManager()
											.getPackageInfo(packageName, 0);
								} catch (NameNotFoundException e) {
									e.printStackTrace();
									Toast.makeText(mContext, "应用不存在", Toast.LENGTH_SHORT).show();
								}

								Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
								// Intent resolveIntent = new Intent();
								resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
								resolveIntent.setPackage(pi.packageName);

								List<ResolveInfo> apps = ShowView.getInstance()
										.getPackageManager().queryIntentActivities(resolveIntent, 0);
								ResolveInfo ri = apps.iterator().next();
								if (ri != null) {
									if(getXY!=null){
										getXY=null;
									}
									String packageName1 = ri.activityInfo.packageName;
									String className = ri.activityInfo.name;

									Intent intent = new Intent(Intent.ACTION_MAIN);
									// Intent intent = new Intent();
									intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									intent.addCategory(Intent.CATEGORY_LAUNCHER);
									ComponentName cn = new ComponentName(packageName1, className);
									intent.setComponent(cn);
									ShowView.getInstance().startActivity(intent);
								}else{
									Toast.makeText(mContext, "应用不存在", Toast.LENGTH_SHORT).show();
								}
								break;
							}
							if(json.get("type").equals("link")){
								String url=json.getString("jump");
								bottomWebView.clearCache(true);
								clearCacheFolder(mContext.getCacheDir(), System.currentTimeMillis());
								bottomWebView.loadUrl(url);
								if(getXY!=null){
									getXY=null;
								}
								break;
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			return super.onFilterTouchEventForSecurity(event);
		}
	}
}