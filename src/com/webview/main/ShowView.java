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
 * FileName:     ShowView.java
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

import com.webview.exception.CrashHandler;

import android.app.Application;

public class ShowView extends Application{
	private static ShowView instance;
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		CrashHandler.getInstance().init(this);
	}
	public static ShowView getInstance() {
		return instance;
	}
}
