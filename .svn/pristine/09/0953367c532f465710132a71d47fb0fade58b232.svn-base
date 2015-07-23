package com.webview.http;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

public class InterfaceOp {
	/**
	 * 一级域名
	 */
	public static String URL_DEFAULT_DOMAIN = "118.123.17.98:50905";//"118.123.17.98:50905";// "www.solscloud.com";
	public static String URL_DOMAIN = URL_DEFAULT_DOMAIN;
	public static final String URL_ROOT_DIRECTORY = "/api";
	public static final String PROTO_APP_VER = "1";
	public static final String PROTO_SUBMMIT_INFO = "2";
	public static final String PROTO_AD = "3";
	public static final String PROTO_MESSAGE = "4";
	public static final String PROTO_HEARTBEAT = "5";
	public static final String PROTO_ADDRESS = "6";
	public static final String PROTO_APPS_LOCATION = "7";
	public static final String PROTO_SUBMIT_DOWNLOAD_INFO = "8";
	public static final String PROTO_SUBMIT_APPS_LIST = "10";
	public static final String PROTO_FETCH_SN = "11";
	public static final String PROTO_PLAYLISTGET = "12";
	public static final String PROTO_PLAYLISTCONFIRM = "13";
	public static final String PROTO_APKSGET = "14";
	public static final String PROTO_APKSCONFIRM = "15";
	public static final String PROTO_REFRESHCONFIRM = "16";
	public static final String PROTO_REBOOT = "17";
	public static final String COMMIT_APPS = "18";

	public static final String APP_ID_SETTING = "1";
	public static final String APP_ID_LUNCHER = "2";
	public static final String APP_ID_MEDIAPLAYER = "3";

	/*
	 * 协议1 获得版本信息
	 */
	public static void protoAppVer(String appid, String appver,
			IRequestListener listener) {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("proto", PROTO_APP_VER);
		params.put("appid", appid);
		params.put("ver", appver);
		String URL_APP_VER = "http://" + URL_DOMAIN + URL_ROOT_DIRECTORY
				+ "/appver.php";
		NetworkUtil.readDataASync(URL_APP_VER, params, false, listener);
	}

	/*
	 * 返回需要更新的URL，否则为null
	 */
	public static String processAppVer(JSONObject jsonObj) {
		if (jsonObj == null)
			return null;
		Boolean updateFlag = jsonObj.optBoolean("updateFlag", false);
		if (!updateFlag)
			return null;
		String appDownloadUrl = jsonObj.optString("downloadUrl", null);
		if (appDownloadUrl == null || !appDownloadUrl.startsWith("http://"))
			return null;
		return appDownloadUrl;
	}

	/*
	 * 协议2 提交客户端信息
	 */
	public static void protoSubmmitInfo(IRequestListener listener) {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("proto", PROTO_SUBMMIT_INFO);
		params.put("account", "");
		params.put("password", "");
		params.put("phonenum", "");
		params.put("sn", "");
		params.put("code", "");
		String URL_SUBMMIT_INFO = "http://" + URL_DOMAIN + URL_ROOT_DIRECTORY
				+ "/submmitinfo.php";
		NetworkUtil.readDataASync(URL_SUBMMIT_INFO, params, false, listener);
	}
	/**
	 * 上传下载文件详情
	 * @param listener
	 */
	public static void UploadFileDetails(Map<String, String> params,IRequestListener listener) {
		String URL_PLAYLIST = "http://" + URL_DOMAIN + URL_ROOT_DIRECTORY
				+ "/playlists/files";
		NetworkUtil.readDataASync(URL_PLAYLIST, params, false, listener);
	}
	/**
	 * 检测是否需要上传log日志
	 * @param listener
	 */
	public static void isUploadFileLog(Map<String, String> params,IRequestListener listener){
		
		String URL_SUBMMIT_INFO = "http://" + URL_DOMAIN + URL_ROOT_DIRECTORY
				+ "/logs";
		NetworkUtil.readDataASync(URL_SUBMMIT_INFO, params, false, listener);
	}
	/**
	 * 刷新确认
	 * @param params
	 * @param listener
	 */
	public static void refreshIsOk(Map<String, String> params,IRequestListener listener){
		String URL_SUBMMIT_INFO = "http://" + URL_DOMAIN + URL_ROOT_DIRECTORY
				+ "/refresh";
		NetworkUtil.readDataASync(URL_SUBMMIT_INFO, params, false, listener);
	}
	public static void protoFetchWeather(IRequestListener listener) {
		String URL_APP_VER = "http://php.weather.sina.com.cn/iframe/index/w_cl.php?code=js&day=0&city=&dfc=1&charset=utf-8&day=4";
		NetworkUtil.readDataASync(URL_APP_VER, null, false, listener);
	}
}
