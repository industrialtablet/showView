/*
 * Copyright (C) 2007 The Android Open Source Project
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

package com.webview.movie;

import com.webview.main.R;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Provides "background" audio playback capabilities, allowing the user to
 * switch between activities without stopping playback.
 */
public class MediaPlaybackService extends Service {
	private final static String TAG = "-->MediaPlaybackService";
	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(TAG,"onCreate()");
	}
	
	public ViewGroup fView;
	public MyFloatView sFloatView;

	/*brief 创建浮动视频窗口
	 *param context
	 */
	private void createView(Context context, float x, float y, float w , float h) {
//		if (fView != null) {
//			return;
//		}
		if (fView != null)
			fView.removeAllViews();
		fView = (ViewGroup) View.inflate(context, R.layout.main, null);
		// 显示myFloatView图像
		sFloatView = new MyFloatView(fView);
		sFloatView.x = x;
		sFloatView.y = y;
		sFloatView.mWidth = w;
		sFloatView.mHeight = h;
		sFloatView.bindViewListener();
		sFloatView.showLayoutView();
		//sFloatView.playViewPrepareFinish();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			String action = intent.getAction();
			//String cmd = intent.getStringExtra("command");
			if ("createUI".equals(action)) {
				//Log.v(TAG,"onStartCommand()-->进来了");
				Bundle bunde = intent.getExtras();
				float x = bunde.getFloat("x");
				float y = bunde.getFloat("y");
				float w = bunde.getFloat("w");
				float h = bunde.getFloat("h");
			    createView(this, x, y, w, h);
			} 
			else if ("removeUI".equals(action)) 
			{
			    Log.v(TAG,"onStartCommand() removeUI");
			    if(fView != null){
                    fView.removeAllViews();
                    fView = null;
                    sFloatView = null;
			    }
			}
		}
		
		//return super.onStartCommand(intent, flags, startId); 
		return START_STICKY;
	}
	boolean iPlayState = false;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
