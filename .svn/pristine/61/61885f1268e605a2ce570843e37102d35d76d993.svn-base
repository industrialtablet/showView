/*
 * Copyright (c) 2015,AndroidCloud Open Source Project,彭易星.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * FileName:     ProxyBridge.java
 * @Description: 

 * Company     深圳市恒耀源科技有限公司
 * WebSite	   www.best-cloud.cn
 * @author:    彭易星(QQ:15119143;email:pengyixing@gmail.com)
 * @version    V1.0 
 * Createdate: 2015年7月24日
 *
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------
 * 2015年7月24日 彭易星          1.0           1.0
 * 
 * Why & What is modified: <修改原因描述>
 * 
 */
package com.webview.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.webview.content.ViewContent;
import com.webview.movie.MovieUtils;
import com.webview.movie.MyFloatView;
import com.webview.util.AppConstants;
import com.webview.util.FileDigest;
import com.webview.util.LogUtil;
import com.webview.util.MainApplication;
import com.webview.util.PreferenceUtils;
import com.webview.util.StringUtils;

public class ProxyBridge {
	private static Context mContext;

	public ProxyBridge(Context context){
		this.mContext=context;
	}
	/**
	 * 通知生成函数接口
	 */
	@JavascriptInterface
	public void getCreateView(String json){
		Intent mIntent = new Intent();
		mIntent.setAction(ViewContent.RECEIVER_URI);
		mIntent.putExtra("tojson", json); 
		mContext.sendBroadcast(mIntent);
	}
	
	@JavascriptInterface
	public void getTest(String name){
		new AlertDialog.Builder(mContext).setMessage("来自服务器的消息:"+name).show();
	}
	@JavascriptInterface
	public void getWebViewXY(String message){
		try {
			CreateLayout.getXY=new JSONArray(message);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 调用应用接口
	 * @param packageName
	 */
	@JavascriptInterface
	public static void getOpenApp(String packageName,String classNamexx){
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
	}
	/**
	 * 写网页日志接口
	 * @param time
	 * @param message
	 */
	@JavascriptInterface
	public void createWebLog(String message){
		try {
			LogUtil.createLog(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@JavascriptInterface
	public void createScenes(String message){
		String data="var scenes=";
		if (!message.isEmpty()) {
			data+=message;
		}else{
			data+="{}";
		}
		try {
			StringUtils.writeFile(Environment.getExternalStorageDirectory().getAbsolutePath()
					+"/jetty/webapps/console/data/scenes.js",
					data);
			//写完之后保存时间和MD5值
			String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			String md5Program="";
			File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()
					+"/jetty/webapps/console/data/program.js");
			if(file.exists()){
				md5Program=FileDigest.getFileMD5(file);
			}
			String md5Scenes="";
			file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
					+"/jetty/webapps/console/data/scenes.js");
			if(file.exists()){
				md5Scenes=FileDigest.getFileMD5(file);
			}
			PreferenceUtils.setPrefString(mContext, "date", date);
			PreferenceUtils.setPrefString(mContext, "md5Program", md5Program);
			PreferenceUtils.setPrefString(mContext, "md5Scenes", md5Scenes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@JavascriptInterface
	public String checkScenes(){
		String date=PreferenceUtils.getPrefString(mContext, "date", "");
		String md5Program=PreferenceUtils.getPrefString(mContext, "md5Program", "");
		String md5Scenes=PreferenceUtils.getPrefString(mContext, "md5Scenes", "");
		
		String new_program_md5="";
		String new_scenes_md5="";
		try {
			File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/jetty/webapps/console/data/program.js");
			if(file.exists()) new_program_md5=FileDigest.getFileMD5(file);
			else new_program_md5="";
			
			file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/jetty/webapps/console/data/scenes.js");
			if(file.exists()) new_scenes_md5=FileDigest.getFileMD5(file);
			else new_scenes_md5="";
			}
		catch (Exception e) {
		}
		JSONObject jsonObj=new JSONObject();
		try {
			jsonObj.put("date", date);
			jsonObj.put("program_md5", md5Program);
			jsonObj.put("scenes_md5", md5Scenes);
			jsonObj.put("new_program_md5", new_program_md5);
			jsonObj.put("new_scenes_md5", new_scenes_md5);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObj.toString().trim();
	}
	
	public boolean checkBrowser(String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			ApplicationInfo info = mContext.getPackageManager().getApplicationInfo(
					packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
	 private static boolean execWithRoot(String... args)
	    {
	        Process proc = null;
	        try
	        {
	            proc = Runtime.getRuntime().exec("/system/bin/sh",null,new File("/system/bin"));
	        }
	        catch (IOException e)
	        {
	            e.printStackTrace();
	        }
	        if (proc != null)
	        {
	            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
	            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())),true);
	            for (String arg : args)
	            {
	                out.println(arg);
	            }
	            try
	            {
	                String line;
	                while ((line = in.readLine()) != null)
	                {
	                    LogUtil.log(line);
	                    if (line.contains("Success"))
	                        return true;
	                    else if (line.contains("Error"))
	                        return false;
	                    else
	                        return false;
	                }
	            }
	            catch (Exception e)
	            {
	                e.printStackTrace();
	                /**
	                 * Root安装失败，尝试非ROOT安装
	                 */
	            }
	            finally
	            {

	                try
	                {
	                    if (in != null)
	                        in.close();
	                }
	                catch (IOException e)
	                {
	                    e.printStackTrace();
	                }
	                out.close();
	                proc.destroy();
	            }
	        }
	        return false;
	    }

	    @JavascriptInterface
	    private static boolean movieViewStretch()
	    {
	    	
	        boolean isSuccess = false;
	        String cmd = "echo 1 > /sys/class/video/screen_mode" + "\n";
	        try
	        {
	            execWithRoot(cmd);
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        return isSuccess;
	    }

	    /*
	     * 判断文件是否存在
	     * @param filePath : 文件路径，可以是文件或者文件夹
	     * */
	    @JavascriptInterface
	    public boolean fileExist(String filePath)
	    {
	        boolean isFileExist = false;
	        File file = new File(AppConstants.getSdFolder() + "jetty/webapps/console/" + filePath);	        //Log.w("===smallstar===", AppConstants.getSdFolder() + "jetty/webapps/console/demo/" + filePath);
	        if (file.exists())
	        {
	            isFileExist = true;
	        }
	        return isFileExist;
	    }

	    /*
	     * 系统设置
	     * */
	    @JavascriptInterface
	    public void Settings()
	    {
	        Runnable runnable = new Runnable()
	        {
	            public void run()
	            {
	                Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS); //系统设置
	                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	                ShowView.getInstance().startActivity(intent);
	            }
	        };
	        Activity a = (Activity)mContext;
	        a.runOnUiThread(runnable);
	    }

	    @JavascriptInterface
	    public void wifiSet()
	    {
	        Runnable runnable = new Runnable()
	        {
	            public void run()
	            {
	                Intent intentActivity = new Intent();// = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
	                if (android.os.Build.VERSION.SDK_INT > 10)
	                {
	                    intentActivity.setComponent(new ComponentName("com.android.settings","com.android.settings.wifi.WifiPickerActivity"));
	                    intentActivity.putExtra("extra_prefs_show_button_bar",true);
	                }
	                else
	                {
	                    intentActivity = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
	                }
	                intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	                MainApplication.getInstance().startActivity(intentActivity);
	            }
	        };
	        Activity a = (Activity)mContext;
	        a.runOnUiThread(runnable);
	    }

	    /*
	     * 播放网页助手函数
	     * @param urlPlayListJson:网页播放清单字符串
	     * */
	    @JavascriptInterface
	    public void playUrlAssist(String urlPlayListJson)
	    {
	        if(urlPlayListJson.isEmpty())
	        {
	            return;
	        }
	        AppConstants.URLPLAYLIST = urlPlayListJson;
	    }
	    
	    @JavascriptInterface
	    public void settingTimeOut()
	    {
	        AppConstants.isSettingsTimeOut = true;
	    }

	    @JavascriptInterface
	    public String getNetworkType()
	    {
	        if(AppConstants.NETWORK_TYPE == 0)
	            return "WIFI";
	        else if(AppConstants.NETWORK_TYPE == 1)
	            return "ETHERNET";
	        return "unconnected";
	    }

	    @JavascriptInterface
	    public String getIp()
	    {
	        String ret = "";
	        if (AppConstants.NETWORK_TYPE == 0)
	        {
	            ret = AppConstants.WIFI_IP;
	        }
	        else if (AppConstants.NETWORK_TYPE == 1)
	        {
	            ret = AppConstants.ETH_IP;
	        }
	        else
	        {
	            ret = "unkown network type!";
	        }
	        return ret;
	    }

	    @JavascriptInterface
	    public String getMac()
	    {
	        String ret = "";
	        if (AppConstants.NETWORK_TYPE == 0)
	        {
	            ret = AppConstants.WIFI_MAC;
	        }
	        else if (AppConstants.NETWORK_TYPE == 1)
	        {
	            ret = AppConstants.ETH_MAC;
	        }
	        else
	        {
	            ret = "unkown network type!";
	        }
	        return ret;
	    }

	    @JavascriptInterface
	    public void ethSet()
	    {
	        if (StringUtils.replaceBlank(AppConstants.BOARD_MODEL).equals("A06"))
	        {
	            return;
	        }
	        Runnable runnable = new Runnable()
	        {
	            public void run()
	            {
	                Intent intentActivity = new Intent();// = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
	                if (android.os.Build.VERSION.SDK_INT > 10)
	                {
	                    intentActivity.setComponent(new ComponentName("com.android.settings","com.android.settings.ethernet.EthernetPickerActivity"));
	                    intentActivity.putExtra("extra_prefs_show_button_bar",true);
	                }
	                else
	                {
	                    intentActivity = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
	                }
	                intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	                MainApplication.getInstance().startActivity(intentActivity);
	            }
	        };
	        Activity a = (Activity)mContext;
	        a.runOnUiThread(runnable);
	    }

	    @JavascriptInterface
	    public void movieViewStarPlayByJs(final float x, final float y, final float width, final float height, String jsonString)
	    {
	    	Log.d("movieViewStarPlayByJs", jsonString);
	    	MovieUtils.movieViewStarPlay(mContext, x, y, width, height, jsonString);
	    	//Log.i("movieViewStarPlayByJs:", x+"/"+y+"/"+width+"/"+height);
//	    	if((x+y+width+height == 0) || (jsonString=="")){Log.i("movieViewStarPlayByJs:", "params error from JS.");return;}
//	    	MovieUtils.movieViewClose(mContext);
//	    	MovieUtils.moviewViewSetPlayListJsonString(jsonString);
//	    	MovieUtils.movieViewPrepare(mContext, x, y, width, height);
//	    	MovieUtils.movieViewStarPlay(true);
	    }

	    /*
	     * 关闭视频窗口
	     * */
	    @JavascriptInterface
	    public void movieViewClose()
	    {
	    	Log.e("ProxyBridge", "movieViewClose");
	    	MovieUtils.movieViewClose(mContext);
	    }


	    /*
	     * 更新播放窗口的大小
	     * @param width, height:播放窗口的长和宽
	     * */
	    @JavascriptInterface
	    public void updateViewSize(float width, float height)
	    {
	    	Log.i("movieddd:", width+"/"+height);
	        MyFloatView.mWidth = width;
	        MyFloatView.mHeight = height;
	        MyFloatView.updateViewSize();	    
	    }

	    /*
	     * 更新播放窗口的位置
	     * 
	     * param x,y:播放窗口的位置坐标
	     * */
	    @JavascriptInterface
	    public void updateViewPosition(float x, float y)
	    {
	        Log.v("smallstar x",String.valueOf(x));
	        Log.v("smallstar y",String.valueOf(y));
	        MyFloatView.x = x;
	        MyFloatView.y = y;
	        MyFloatView.updateViewPosition();
	        //            Log.v("smallstar", "-------------------updateViewPosition--------------------------");
	        //            Runnable runnable = new Runnable() {
	        //                public void run() {
	        //                    
	        //                }
	        //            };
	        //            Activity a = (Activity) mContext;
	        //            a.runOnUiThread(runnable);	    
	    }

	    @JavascriptInterface
	    public void moveLeft()
	    {
	        MyFloatView.x = 100;
	        MyFloatView.y = 100;
	        Log.v("smallstar","-------------------moveleft--------------------------");
	        Runnable runnable = new Runnable()
	        {
	            public void run()
	            {
	                MyFloatView.updateViewPosition();
	            }
	        };
	        Activity a = (Activity)mContext;
	        a.runOnUiThread(runnable);
	    }

	    @JavascriptInterface
	    public void moveright()
	    {
	        Log.v("smallstar","-------------------moveright--------------------------");
	        Runnable runnable = new Runnable()
	        {
	            public void run()
	            {
	                MyFloatView.zoomIn();
	            }
	        };
	        Activity a = (Activity)mContext;
	        a.runOnUiThread(runnable);
	    }
	    /*
	    @JavascriptInterface
	    public void timeZoneSet() {
	        //Toast.makeText(getApplicationContext(),"xdtianyu",Toast.LENGTH_LONG).show();
	    	Log.v("smallstar", "ethSet()");
	    	Runnable runnable = new Runnable() {
	    	    public void run() {
	    	    	Intent intentActivity = new Intent();// = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
	    	    	if(android.os.Build.VERSION.SDK_INT > 10 ){  
	    	    		intentActivity.setComponent(new ComponentName("com.android.settings",
	    	    				"com.android.settings.ZonePickerActivity"));
	    	    		intentActivity.putExtra("extra_prefs_show_button_bar", true);
	    	    	}else {  
	    	    		intentActivity = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
	    	    	}
	    	    	intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	    	MainApplication.getInstance().startActivity(intentActivity);
	    	    }
	    	};
	    	Activity a = (Activity) mContext;
	    	a.runOnUiThread(runnable);
	    }
	    */
}
