package com.webview.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

public class NetworkUtil {
	private static String imei = "";
	/**
	 * 网络相关
	 */
	public static final int TIMEOUT_FETCH_CONNECTION = 5 * 1000;
	public static final int TIMEOUT_ESTABLISH_CONNECTION = 10 * 1000;
	public static final int TIMEOUT_REQUEST = 20 * 1000;
	private static HttpUriRequest getHttpConn(String url,
			Map<String, String> params) throws MalformedURLException,
			IOException {
		if (TextUtils.isEmpty(url)){Log.e("->NetworkUtil","url is null.");return null;}
		if (params != null) {
			Set<String> keys = params.keySet();
			String currElem = "";
			HttpPost post = new HttpPost(url);
			List<NameValuePair> ps = new ArrayList<NameValuePair>();
			for (Iterator<String> it = keys.iterator(); it.hasNext();) {
				currElem = (String) it.next();
				//LogUtil.log(currElem + "=" + params.get(currElem));
				ps.add(new BasicNameValuePair(currElem, params.get(currElem)));
//				ps.add(new BasicNameValuePair(currElem, URLEncoder.encode(params.get(currElem))));
			}
			post.setEntity(new UrlEncodedFormEntity(ps, HTTP.UTF_8));
			return post;
		} else {
			return new HttpGet(url);
		}
	}
	
	
	/**
	 * 同步请求
	 * 
	 * @param url
	 *            地址
	 * @param params
	 *            参数
	 * @param isGB2312
	 *            返回数据是否GB2312编码
	 * @return 服务器响应的字符串
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws Exception
	 */
	public static String readDataSync(String url, Map<String, String> params,
			boolean isGB2312) throws MalformedURLException, IOException,
			Exception {
		//Log.i("shwoView=>readDataSync", "=========>url:" + url + "    params: " + params);
		HttpUriRequest request = getHttpConn(url, params);
		HttpClient client = new DefaultHttpClient();
		final HttpParams ps = client.getParams();

		HttpConnectionParams.setConnectionTimeout(ps,
				TIMEOUT_ESTABLISH_CONNECTION);

		HttpConnectionParams.setSoTimeout(ps, TIMEOUT_REQUEST);
		ConnManagerParams.setTimeout(ps, TIMEOUT_FETCH_CONNECTION);
//		ps.setParameter(CoreProtocolPNames.USER_AGENT, getUserAgent());
		HttpResponse response = null;
		HttpEntity httpEntity = null;
		String respStr = null;
		try {
			response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				httpEntity = response.getEntity();
				if (isGB2312)
					respStr = EntityUtils.toString(httpEntity, "GB2312");
				else
					respStr = EntityUtils.toString(httpEntity, "UTF-8");
				
				if (TextUtils.isEmpty(respStr))
					return null;
			}
		} catch (UnknownHostException e) {
			throw e;
		} catch (MalformedURLException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} catch (OutOfMemoryError e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			if (httpEntity != null)
				httpEntity.consumeContent();
			if (client != null)
				client.getConnectionManager().shutdown();
		}
		return respStr;
	}

	/**
	 * 异步请求
	 * 
	 * @param url
	 *            地址
	 * @param params
	 *            参数
	 * @param listener
	 *            请求回调
	 */
	public static void readDataASync(final String url,
			final Map<String, String> params, final boolean isGB2312,
			final IRequestListener listener) {

		final Runnable runnable = new Runnable() {
			public void run() {
				JSONObject jsonObj = null;
				try {
					String respStr = readDataSync(url, params, isGB2312);
					String resErr = "Error!";
					Boolean successFlag = false;
					if(params != null)
					{
						jsonObj = new JSONObject(respStr);
						successFlag = jsonObj.optBoolean("result",	false);
						resErr = jsonObj.optString("errmsg", "Response contains no error message");
					}
					if (listener != null)
						listener.onComplete(!successFlag,
								resErr,
								respStr,
								jsonObj);
				} catch (UnknownHostException e) {
					e.printStackTrace();
					if (listener != null)
						listener.onError(new IOException("UnkownHost error"));
				} catch (MalformedURLException e) {
					e.printStackTrace();
					if (listener != null)
						listener.onError(new IOException("URL error"));
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					if (listener != null)
						listener.onError(new IOException("network timeout"));
				} catch (IOException e) {
					e.printStackTrace();
					if (listener != null)
						listener.onError(new IOException("network error"));
				} catch (JSONException e) {
					e.printStackTrace();
					if (listener != null)
						listener.onError(new IOException(
								"Response is not json string"));

				} catch (OutOfMemoryError e) {
					e.printStackTrace();
					if (listener != null)
						listener.onError(new IOException("Memorry overflow"));
					System.gc();
				} catch (Exception e) {
					e.printStackTrace();
					if (listener != null)
						listener.onError(new IOException("Unkown error"));
				}
			}
		};
		performOnBackgroundThread(runnable);
	}

	public static void performOnBackgroundThread(final Runnable runnable) {
		final Thread t = new Thread() {
			@Override
			public void run() {
				try {
					runnable.run();
				} catch (OutOfMemoryError err) {
					err.printStackTrace();
					System.gc();
				} finally {
				}
			}
		};
		t.setDaemon(true);
		t.start();
	}

}
