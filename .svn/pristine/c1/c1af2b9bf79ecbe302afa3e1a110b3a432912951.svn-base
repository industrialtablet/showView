package com.webview.util;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * 获取下载文件详情
 * @author Arlen
 *
 */
public class FileDetailsUtil {
	private static List<String> fileNames;
	private static List<HashMap<String, String>> names;
	private static Map<String, String> sizes;

	public static String getDownFileDetails(String filePath){
		fileNames=new ArrayList<String>();
		names=new ArrayList<HashMap<String, String>>();
		sizes=new HashMap<String, String>();
		File file=new File(filePath);
		if (!file.exists()&& !file.isDirectory()){
			return "{}";
		}
		try {
			tree(file);
			return getLog();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "{}";
		}
	}
	static int i=0;
	//显示目录的方�?
	private static void tree(File f) throws Exception{
		//判断传入对象是否为一个文件夹对象
		if(!f.isDirectory()){
		}
		else{
			File[] t = f.listFiles();
			for(int i=0;i<t.length;i++){
				//判断文件列表中的对象是否为文件夹对象，如果是则执行tree递归，直到把此文件夹中所有文件输出为�?
				if(t[i].isDirectory()){
					if("data".equals(t[i].getName())||"temp".equals(t[i].getName())){
						continue;
					}
					tree(t[i].getAbsoluteFile());
				}
				else{
					HashMap<String,String> isMap=new HashMap<String,String>();
					File fileName=t[i].getAbsoluteFile();
					String s[] =t[i].getParent().toString().split("/");
					String sName=s[s.length-1];
					//当前文件所在文件夹
					if(fileNames.size()==0){
						fileNames.add(sName);
					}else{
						boolean isNames=true;
						for (int j = 0; j < fileNames.size()-1; j++) {
							if(sName.equalsIgnoreCase(fileNames.get(j))){
								isNames=false;
							}
						}
						if(isNames){
							fileNames.add(sName);
						}
						isNames=true;
					}
					isMap.put(sName, t[i].getName());
					names.add(isMap);
					sizes.put(t[i].getName(), FormetFileSize(getFileSizes(fileName)));
				}
			}
		}
	}
	/**
	 * 转换文件大小
	 * @param fileS
	 * @return
	 */
	private static String FormetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}
	//取得文件大小
	private static long getFileSizes(File f) throws Exception{
		long s=0;
		if (f.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			s= fis.available();
		}
		return s;
	}
	// 递归 取得文件夹大�?
	private long getFileSize(File f)throws Exception
	{
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++)
		{
			if (flist[i].isDirectory())
			{
				size = size + getFileSize(flist[i]);
			} else
			{
				size = size + flist[i].length();
			}
		}
		return size;
	}
	/**
	 * 递归求取目录文件个数
	 * @param f
	 * @return
	 */
	private long getlist(File f){
		long size = 0;
		File flist[] = f.listFiles();
		size=flist.length;
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getlist(flist[i]);
				size--;
			}
		}
		return size;
	}

	@SuppressLint("NewApi")
	private static String getLog() throws JSONException{
		JSONObject json=new JSONObject();
		for (int i = 0; i < fileNames.size(); i++) {
			JSONArray array=new JSONArray();
			for (int j = 0; j < names.size(); j++) {
				JSONObject name=new JSONObject();
				if(names.get(j).get(fileNames.get(i))!=null){
					name.put("name", names.get(j).get(fileNames.get(i)));
					name.put("size", sizes.get(names.get(j).get(fileNames.get(i))));
					array.put(name);
				}

			}
			json.put(fileNames.get(i), array);
		}
		return json.toString();
		//		for (int i = 0; i < fileNames.size(); i++) {
		//			System.out.println("文件夹名--》"+fileNames.get(i));
		//			System.out.println("文件--》"+names.get(fileNames.get(i)));
		//			System.out.println("size--》"+sizes.get(names.get(fileNames.get(i))));
		//		}
	}

}
