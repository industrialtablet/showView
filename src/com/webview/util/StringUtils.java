package com.webview.util;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

public class StringUtils{
    public static String replaceBlank(String str) 
    {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
    /**
     * 转化成json对象
     * 
     * @return
     */
    public static JSONObject toJson(){
        JSONObject jsonObject = new JSONObject();
        
        return jsonObject;
    }
    /**
     * 写文件到sd卡
     * @param fileName
     * @param message
     */
    public static void writeFile(String fileName, String message)
    {
        try
        {
            FileOutputStream overWrite = new FileOutputStream(fileName,false);
            OutputStreamWriter osw = new OutputStreamWriter(overWrite,"UTF-8");
            osw.write(message);
            osw.flush();
            overWrite.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}