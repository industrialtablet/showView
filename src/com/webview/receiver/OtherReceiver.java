package com.webview.receiver;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.webview.content.ViewContent;
import com.webview.http.IRequestListener;
import com.webview.http.InterfaceOp;
import com.webview.main.ShowView;
import com.webview.util.FileDetailsUtil;
import com.webview.util.LogUtil;
import com.webview.util.StringUtils;
import com.webview.util.UploadLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.webview.util.ZipUtil;
/**
 * 心跳信息交互广播
 * @author Administrator
 *
 */
public class OtherReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		//提交文件信息
		if(intent.getAction().equals(ViewContent.FILEDETAILS_UPLOAD)){
			String toJson=intent.getStringExtra("result");
			String result=FileDetailsUtil.getDownFileDetails(Environment.getExternalStorageDirectory().getAbsolutePath()
					+"/jetty/webapps/console/upload");
			try {
				JSONObject json=new JSONObject(toJson);
				final String organize_id=json.getString("organize_id");
				final String imei=json.getString("imei");
				final String sn=json.getString("sn");
				final String mac_ether=json.getString("mac_ether");
				final String mac_wifi=json.getString("mac_wifi");
				Map<String, String> params = new LinkedHashMap<String, String>();
				params.put("organize_id", organize_id);
				params.put("imei", imei);
				params.put("sn", sn);
				params.put("mac_ether", mac_ether);
				params.put("mac_wifi", mac_wifi);
				params.put("mac_wifi", mac_wifi);
				params.put("files", result);
				InterfaceOp.UploadFileDetails(params, new IRequestListener() {
					
					@Override
					public void onError(Exception e) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onComplete(boolean isError, String errMsg, String resString,
							JSONObject respObj) {
						//Log.i("uploadFileDetails-->", respObj.toString());
						
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		//更新天气js
		if(intent.getAction().equals(ViewContent.FILEWEATHER_UPDATE)){
			String result=intent.getStringExtra("result");
			try {
				StringUtils.writeFile(Environment.getExternalStorageDirectory().getAbsolutePath()
						+"/jetty/webapps/console/data/weather_data.js",
						result);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		//获取机器信息
		if(intent.getAction().equals(ViewContent.UPLOAD_LOG_SN)){
			String result=intent.getStringExtra("result");
			if(result!=null){
				try {
					JSONObject json=new JSONObject(result);
					final String organize_id=json.getString("organize_id");
					final String imei=json.getString("imei");
					final String sn=json.getString("sn");
					final String mac_ether=json.getString("mac_ether");
					final String mac_wifi=json.getString("mac_wifi");
					//先获取是否上传
					Map<String, String> params = new LinkedHashMap<String, String>();
					params.put("organize_id", organize_id);
					params.put("imei", imei);
					params.put("sn", sn);
					params.put("mac_ether", mac_ether);
					params.put("mac_wifi", mac_wifi);
					InterfaceOp.isUploadFileLog(params,new IRequestListener() {

						@Override
						public void onError(Exception e) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onComplete(boolean isError, String errMsg, String resString,
								JSONObject respObj) {
							//取得返回值确定是否上传,上传打包哪几个
							boolean result=false;
							try {
								result=respObj.getBoolean("result");
							} catch (JSONException e1) {
								result=false;
								e1.printStackTrace();
							}
							if(result){
								try {
									List<NameValuePair> ps=new ArrayList<NameValuePair>();
									ps.add(new BasicNameValuePair("imei", imei));
									ps.add(new BasicNameValuePair("sn", sn));
									ps.add(new BasicNameValuePair("organize_id", organize_id));
									ps.add(new BasicNameValuePair("mac_ether", mac_ether));
									ps.add(new BasicNameValuePair("mac_wifi", mac_wifi));
									//						    		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
									//						    		ps.add(new BasicNameValuePair("date", sdf.format(new Date())+""));
									JSONArray array=respObj.getJSONArray("list");
									if(array.length()>0){
										List<String> islog=new ArrayList<String>();
										for (int i = 0; i < array.length(); i++) {
											Log.i("http logs get:", array.getString(i));
											//判断文件是否存在
											if(fileIsExists(LogUtil.logPath+"/"+array.getString(i)+".log")){
												islog.add(array.getString(i)+".log");
											}
										}
										if(islog.size()>0){
											//开始压缩上传
											String logPath = LogUtil.logPath;
											try {
												ZipUtil.zipFolder(logPath,Environment.getExternalStorageDirectory().getAbsolutePath(),islog);
											} catch (Exception e) {
												Log.e("log", "压缩错误",e);
												return;
											}
											ps.add(new BasicNameValuePair("the_file",Environment.getExternalStorageDirectory().getAbsolutePath()+"/log.zip"));
											UploadLog.http_upload(ps);
										}
										
									}else{
										//										UploadLog.http_upload(ps);
									}

								} catch (JSONException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}
					});


				} catch (Exception e) {
					e.printStackTrace();
					Log.e("http", e.toString());
				}
			}
		}
	}
	//检查文件是否存在
	public boolean fileIsExists(String path){
		try{
			File f=new File(path);
			if(!f.exists()){
				return false;
			}

		}catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}

}
