package com.webview.util;

import java.util.List;


import android.os.Build;
import android.os.Environment;
import android.util.Log;

public class AppConstants {
	
        //客户端组织ID，用来区别客户，可理解为客户代码
        public static String ORGANIZE_ID = "11";//For 试用
        
	/*
	 * 新版广告机客户端UI相关
	 * */
        //Test URL
	//http://58.216.50.44/js/dzxsp/sbjgxsh.aspx?pjsdid=AA3F5755-C5E5-4541-B928-D784A21208F2
	//http://develowebs.com/testcode/IdeaRep/configuration/index.php
	//http://dev-bnr.mxs.ca/display10.html;
        public static String CLIENT_SETTINGS_PLAYURL = "http://localhost:8080/console/settings/basicsettings.html";
        public static String CLIENT_DEFAULT2_PLAYURL = "http://localhost:8080/console/index.html";
        public static String CLIENT_CUR_PLAYURL = CLIENT_DEFAULT2_PLAYURL;
	public static String GROUP_ID = "";
	
	public static String RESOLUTION = "";
	public static String URLPLAYLIST = "";
	
	/**
	 * 网络相关
	 */
	public static final int TIMEOUT_FETCH_CONNECTION = 5 * 1000;
	public static final int TIMEOUT_ESTABLISH_CONNECTION = 10 * 1000;
	public static final int TIMEOUT_REQUEST = 20 * 1000;

	/**
	 * 六边形真实的长宽�?
	 */
	public static int w = 0;
	public static int h = 0;
	public static double a = 0;
	/**
	 * LogoView相关
	 */
	public static final int LOGOVIEW_TEXT_SIZE = 20;
	public static final int LOGOVIEW_NAME_MAX_LEN = 20;
	public static Object LOCK_OBJ = new Object();
	/**
	 * 第一�?
	 */
	public static int LOGOVIEW_FIRST_COL = 0;


	/**
	 * 省份信息
	 */
//	public static List<Province> PROVINCES_INFO = null;
	public static final String PROVINCES_FILE_NAME = "city.config";
	public static final String URL_DOMAIN_PREFIX = "province";
	/**
	 * Handler 消息
	 */
	public static final int MSG_SHOW_WEATHER = 1;
	public static final int MSG_INSTALL_COMPLETE = 2;
	public static final int MSG_REQUEST_DOWNLOAD = 3;
	public static final int MSG_UPDATE_TIME = 4;
	public static final int MSG_SHOW_MESSAGE = 5;
	public static final int MSG_RELOCATE_LOGOIMG = 6;
	public static final int MSG_SUBMIT_APPS_LIST = 7;
	public static final int MSG_CREATE_FLOW_WINDOW = 8;
	public static final int MSG_DESTROY_FLOW_WINDOW = 9;
	public static final int MSG_FOAT_FULL = 10;
	public static final int MSG_PAGE_TIMEOUT = 11;
	public static final int MSG_UPDATE_VIDEO_WINFULLSIZE = 12;
	public static final int MSG_UPDATE_VIDEO_WINSIZE = 13;
	
	/**
	 * 下载完成广播
	 */
	public static final String ACTION_DOWN_FINISH = "org.mortbay.ijetty.action.download.finish";
	public static final String ACTION_APK_DOWN_FINISH = "org.mortbay.ijetty.action.download.apk.finish";
	public static final String ACTION_MEDIA_DOWN_FINISH = "org.mortbay.ijetty.action.download.media.finish";
	/**
	 * 定位完成
	 */
	public static final String ACTION_LOCATION_COMPLETE = "com.mylayout.app.action.location.complete";
	/**
	 * 软件更新广播
	 */
	public static final String ACTION_APP_UPDATE = "com.mylayout.app.action.app.update";
	/**
	 * 广告数据发�?间隔
	 */
	public static final String ACTION_AD_INTERVAL = "com.mylayout.app.action.ad.interval";
	
	/**
	 * 人体感应广播
	 * */
	public static final boolean ACTION_PEOPLE_DETECT = false;//TRUE则启动人体感�?
	public static final String ACTION_PEOPLE_COME = "android.intent.action.people.coming";
	public static final String ACTION_PEOPLE_LEAVE = "android.intent.action.people.leaving";
	
	// /**
	// * UI程序崩溃
	// */
	// public static final String ACTION_UI_CRASH =
	// "com.mylayout.app.action.ui.crash";
	public static final String DOWNLOADING_FILE_PREFFIX = ".tmp";
	
	
	/**
	 * 提交应用列表的延时时�?
	 */
	public static long DELAYED_SUBMIT_APPS_LIST = 10* 60 * 1000;

	public static int SCROLL_MAX_MOVE = 0; // 滑动等于这个数时移动�?��六边�?
	/**
	 * 配置文件�?
	 */
	public static final String CONFIG_FILENAME = "config";
	public static final int HEARTBEAT_TIME_DEFAULT = 16;// s
	public static int NETSTATUSMONITOR = 5;
	/**
	 * 心跳间隔时间(�?
	 */
	public static int HEARTBEAT_TIME = HEARTBEAT_TIME_DEFAULT;
	
	/**
	 * fetchSN提交时间
	 */
	public static final int SN_NORMAL_INTERVAL = 2 * 60 * 60 * 1000;
	public static final int SN_ERROR_INTERVAL = 15 * 1000;

	/**
	 * 广告数据发�?间隔时间(�?
	 */
	public static final int AD_INTERVAL_TIME_DEFAULT = 3;// 3小时
	public static int AD_INTERVAL_TIME = AD_INTERVAL_TIME_DEFAULT;
	/**
	 * 多媒体相�?
	 */
	public static final String MEDIA_PACKAGE_NAME = "com.mylayout.app.media";
	public static final String MEDIA_ACTIVITY_NAME = ".MainActivity";
	/**
	 * 待机�?��时间
	 */
	public static final long INTERVAL_TIME = 2 * 60 * 1000; // 120s

	/**
	 * 设置APPID
	 */
	public static final String APP_ID_SETTING = "1";
	/**
	 * Luncher的APPID
	 */
	public static final String APP_ID_LUNCHER = "2";
	/**
	 * 多媒体播放器的APPID
	 */
	public static final String APP_ID_MEDIAPLAYER = "3";

	/**
	 * 数据发�?间隔
	 */
	public static final int AD_SUBMMIT_INTERVAL_DEFAULT = 3;// 小时
	public static int AD_SUBMMIT_INTERVAL = AD_SUBMMIT_INTERVAL_DEFAULT;
	
	/**
	 * 目录
	 */
	public static final String BOARD_MODEL = Build.MODEL;
	public static  String MEDIA_SD_FOLDER;// = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
	public static final String MEDIA_FOLDER = "Media2";	
	public static boolean IS_MEDIA_MOUNTED = false;
	public static final long START_DOWNLOAD_TIME = 60 * 1000;   //�?��后过多长时间�?��下载
	public static long PEOPLE_DETECT_TIMEOUT = 60*1000;

        public static String getSdFolder()
        {
            MEDIA_SD_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
//              if(StringUtils.replaceBlank(BOARD_MODEL).equals("EC3MBXboard") || 
//                 StringUtils.replaceBlank(BOARD_MODEL).equals("EC3AdBoard")  ||
//                 StringUtils.replaceBlank(BOARD_MODEL).equals("IN101SA")    ||
//                 StringUtils.replaceBlank(BOARD_MODEL).equals("IN156SA"))
//              {
//                      MEDIA_SD_FOLDER = AmlogicExt.getExternalStorage2Directory().getPath() + "/";
//              }
//              else
//              {
//                      MEDIA_SD_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
//              }
              //MEDIA_PATH = MEDIA_SD_FOLDER + MEDIA_FOLDER;
                return MEDIA_SD_FOLDER;
        }	
	
	public static String getMediaSdFolder()
	{
	    MEDIA_SD_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
//		if(StringUtils.replaceBlank(BOARD_MODEL).equals("EC3MBXboard") || 
//		   StringUtils.replaceBlank(BOARD_MODEL).equals("EC3AdBoard")  ||
//		   StringUtils.replaceBlank(BOARD_MODEL).equals("IN101SA")    ||
//		   StringUtils.replaceBlank(BOARD_MODEL).equals("IN156SA"))
//		{
//			MEDIA_SD_FOLDER = AmlogicExt.getExternalStorage2Directory().getPath() + "/";
//		}
//		else
//		{
//			MEDIA_SD_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
//		}
//		//MEDIA_PATH = MEDIA_SD_FOLDER + MEDIA_FOLDER;
		//return MEDIA_SD_FOLDER + MEDIA_FOLDER;
	    return MEDIA_SD_FOLDER + "/" + "hyy";
	}
	
	/*
	 * 网络状�?
	 * */
	public static boolean NETWORK_STATUS = false;  //connected=true; disconnected=false.
	public static boolean ONLINE_STATUS = false;   //判断外网是否链接
	public static int NETWORK_TYPE = -1;//-1:unconnected,0:"WIFI",1:"ethernet";//WIFI or ethernet
	public static int WIFI_STATUS = 1;
	public static String WIFI_MAC = "";
	public static String WIFI_IP = "";
	public static int ETH_STATUS = 0;
	public static String ETH_MAC = "";
	public static String ETH_IP = "";
	
	/*
	 * �?���?0秒未操作设置界面自动跳转到内容界�?
	 * */
	public static boolean isSettingsTimeOut = false;
}
