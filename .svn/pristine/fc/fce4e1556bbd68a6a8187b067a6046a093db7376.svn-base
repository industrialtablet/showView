package com.webview.receiver;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import com.webview.content.ViewContent;
import com.webview.http.IRequestListener;
import com.webview.http.InterfaceOp;
import com.webview.main.ShowView;
import com.webview.movie.MyFloatView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
/**
 * Jetty广播接收器
 * @author Arlen.he
 *
 */
public class Receiver extends BroadcastReceiver{

	private Handler mHandler;
	public Receiver(Activity activity,Handler mHandler){
		this.mHandler=mHandler;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(ViewContent.JETTY_SERVICE_URI)){
			if(MyFloatView.surfaceView!=null&&MyFloatView.mPlayViewStatus){
//				//关闭视频窗口
//				if (MyFloatView.mPlayViewStatus)
//				{
//					MyFloatView.mPlayViewPrepareStatus = false;
//					MyFloatView.mPlayViewStatus = false;
//					MyFloatView.onExit();
//				}

			}
			
			String url=intent.getStringExtra("main_view_url");
			Bundle bundle=new Bundle();
			bundle.putString("url", url);
			Message msg=new Message();
			msg.what=ViewContent.JETTY_TO_URL;
			msg.setData(bundle);
			mHandler.sendMessage(msg);
		}
	}

}
