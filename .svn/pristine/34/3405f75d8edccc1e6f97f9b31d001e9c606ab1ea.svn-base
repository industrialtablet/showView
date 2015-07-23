package com.webview.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class LogUtil {
	private static boolean mDebugFlag = true;
	private static final String DEBUG_TAG = "LogUtil";
	private static String LOG_FILE_NAME = "/mnt/sdcard/log.txt";
	public static String logPath=Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
			+"playlog";
	public static void enableDebug(boolean pDebugFlag) {
		mDebugFlag = pDebugFlag;
	}

	public static void log(String pMsg) {
		if (mDebugFlag) {
			Log.e(DEBUG_TAG, pMsg);
		}
	}

	private static void logToFile(File logFile, String text) {
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BufferedWriter buf = null;
		try {
			buf = new BufferedWriter(new FileWriter(logFile, true));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			buf.append(sdf.format(new Date()) + "\t" + text);
			buf.newLine();
			buf.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (buf != null)
				try {
					buf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

		}
	}

	public static void logToDefaultFile(String text) {
		logToFile(new File(LOG_FILE_NAME), text);
	}
	
	public static void appendLog(String text) {
		String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		File logFile = new File(Environment.getExternalStorageDirectory(),
				"cyxh-" + dateStr + ".log");
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BufferedWriter buf = null;
		try {
			buf = new BufferedWriter(new FileWriter(logFile, true));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			buf.append(sdf.format(new Date())+"\t");
			buf.append(text);
			buf.newLine();
			buf.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (buf != null)
				try {
					buf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	///////////以下为xx.log记录播放内容上传/////////
	public static void createLog(String message) {
		Date d = new Date();
		String logFilename = new SimpleDateFormat("yyyyMMdd").format(d)+".log";
		String logTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(d);
		File file = new File(logPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		File saveFile = new File(file, logFilename);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile,true)));
			writer.write(logTime+" "+message);
			writer.newLine();
			writer.flush();
		} catch(Exception e) {
			if (writer!=null) {
				try {
					writer.close();
				} catch (IOException i) {
				}
			}
		} finally {
			if (writer!=null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
