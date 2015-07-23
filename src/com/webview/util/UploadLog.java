package com.webview.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import com.webview.content.ViewContent;
import com.webview.http.InterfaceOp;
import com.webview.main.ShowView;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * @author Arlen
 * 定时上传log日志
 *
 */
public class UploadLog {
	//长传地址
	private static String UPLOAD_LOG_URL="http://"+InterfaceOp.URL_DOMAIN+"/api/logs/submit";
	//上传时间间隔为1小时
	private static int TIME_UPLOAD=60*60*1000;
	//最近7天时间
	private static List<String> senven_day=new ArrayList<String>();;
	//文件路径
	private static String UPLOAD_ADDR="";
	/**
	 * 是否开启计时器
	 */
	public static boolean isGoing=true;
	public static boolean isStop=false;
	public static Timer timer=null;
	public static TimerTask task=null;


	/**
	 * 开始
	 */
	public static void start(){
		//
		if(timer!=null){
			timer.cancel();
			timer=null;
		}
		if (task != null)
		{
			task.cancel();
			task = null;
		}	
		//
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
					//Log.i("timer re", "is ok  log");
					//一周外的日志删除
					try {
						senven_day.clear();
						senven_day_time();
						deleteFolderFile(LogUtil.logPath,false);
					} catch (Exception e) {
						e.printStackTrace();
					}
					//Log.i("timer re", "is ok delete log");
					//发送广播来获取机器信息,仅仅是获取机器心跳里面使用的相关信息
					Intent intent=new Intent();
					intent.setAction("com.mytime.action.upload.log");
					ShowView.getInstance().sendBroadcast(intent);
					timer.cancel();
					reStart();
				}
			};
		}
		timer.schedule(task, TIME_UPLOAD);
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

	public static void is_upload_filelog(){

	}

	public static void http_upload(final List<NameValuePair> ps){
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpResponse httpResponse=post(UPLOAD_LOG_URL,ps,"the_file");
					//若状态码为200 ok   
					if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){  
						//取出回应字串  
						String strResult=EntityUtils.toString(httpResponse.getEntity());  
						Log.e("http result", strResult);
					}else{  
						Log.e("http result-->", httpResponse.getStatusLine().getStatusCode()+"");
					}  
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 带参上传
	 * @param url
	 * @param nameValuePairs
	 * @param fileTarget
	 * @return
	 */
	public static HttpResponse post(String url, List<NameValuePair> nameValuePairs,
			String fileTarget) {
		HttpResponse response = null;
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		Log.e("url", url);
		// url = "http://im.x431.com:8888/dev/?action=user_service.setface";
		HttpPost httpPost = new HttpPost(url);

		try {
			MultipartEntity entity = new MultipartEntity();
			for (int index = 0; index < nameValuePairs.size(); index++) {
				if (nameValuePairs.get(index).getName()
						.equalsIgnoreCase(fileTarget)) {
					// If the key equals to "image", we use FileBody to transfer
					// the data
					entity.addPart(nameValuePairs.get(index).getName(),
							new FileBody(new File(nameValuePairs.get(index)
									.getValue())));
				} else {
					//Normal string data
					entity.addPart(
							nameValuePairs.get(index).getName(),
							new StringBody(nameValuePairs.get(index).getValue()));
				}
			}
			// entity.addPart("user_id", new StringBody("32209"));
			// entity.addPart("type", new StringBody("1"));
			// File file = new File(nameValuePairs.get(1).getValue());
			// ContentBody cbFile = new FileBody(file);
			// entity.addPart("pic", cbFile);

			httpPost.setEntity(entity);
			//			httpPost.setHeader("Content-Type", "multipart/form-data");
			response = httpClient.execute(httpPost);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
	 /**   
     * 删除指定目录下文件及目录    
     * @param deleteThisPath   
     * @param filepath   
     * @return    
     */     
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {     
        if (!TextUtils.isEmpty(filePath)) {     
            try {  
                File file = new File(filePath);     
                if (file.isDirectory()) {// 处理目录     
                    File files[] = file.listFiles();     
                    for (int i = 0; i < files.length; i++) {     
                        deleteFolderFile(files[i].getAbsolutePath(), true);     
                    }      
                }     
                if (deleteThisPath) {     
                    if (!file.isDirectory()) {// 如果是文件，删除   
                    	boolean isDel=false;
                    	for (int i = 0; i < senven_day.size(); i++) {
							if(file.getName().equalsIgnoreCase(senven_day.get(i))){
								isDel=true;
							}
						}
                    	if(!isDel){
                    		file.delete(); 
                    	}
                    } else {// 目录     
                   if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除目录     
//                            file.delete();     
                        }     
                    }     
                }  
            } catch (Exception e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }     
        }     
    }    

	public static void senven_day_time(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); // 格式化日期  
		senven_day.add(sdf.format(new Date())+".log");
		for (int i = 0; i < 7; i++) {
			senven_day.add(sdf.format(getDateBefore(new Date(), i+1))+".log");
		}
	}
	 /** 
     * 得到几天前的时间 
     */  
    public static Date getDateBefore(Date d, int day) {  
        Calendar now = Calendar.getInstance();  
        now.setTime(d);  
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);  
        return now.getTime();  
    }  
  
    /** 
     * 得到几天后的时间 
     */  
    public static Date getDateAfter(Date d, int day) {  
        Calendar now = Calendar.getInstance();  
        now.setTime(d);  
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);  
        return now.getTime();  
    }  
}
