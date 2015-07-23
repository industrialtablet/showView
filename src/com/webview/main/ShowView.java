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
