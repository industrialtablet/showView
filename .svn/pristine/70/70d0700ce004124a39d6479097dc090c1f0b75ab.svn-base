package com.webview.movie;

import com.webview.main.ShowView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class MovieUtils {
	
	private static boolean movieViewStatus = false;
	
	public static void movieViewStarPlay(Context context, final float x, final float y, final float width, final float height, String jsonString)
	{
    	if((x+y+width+height == 0) || (jsonString=="")){Log.i("movieViewStarPlay:", "params error.");return;}
    	movieViewClose(context);
    	moviewViewSetPlayListJsonString(jsonString);
    	movieViewPrepare(context, x, y, width, height);
    	movieViewStarPlay(true);
	}
	
    /*
     * 设置播放清单json格式字符串
     * movieViewPrepare调用之前需要设置
     * */
    public static void moviewViewSetPlayListJsonString(String jsonString)
    {
    	Log.e("MovieUtils", "moviewViewSetPlayListJsonString");
        if (jsonString.isEmpty())
        {
            return;
        }
        MyFloatView.playListJsonString = jsonString;
    }
    
    /*
     * 准备打开视频前的第一步设置
     * @param x，y:视频窗口位置坐标
     * @param width, height:视频窗口长和宽
     *
     * */
    public static void movieViewPrepare(Context context, final float x, final float y, final float width, final float height)
    {
    	Log.e("MovieUtils", "movieViewPrepare");
    	Log.i("movie:", x+"/"+y+"/"+width+"/"+height);
        MyFloatView.x = x;
        MyFloatView.y = y;
        MyFloatView.mWidth = width;
        MyFloatView.mHeight = height;
        //打开视频窗口
        Runnable runnable = new Runnable()
        {
            public void run()
            {
                MyFloatView.listAllMediaFiles();
                Intent intent = new Intent(ShowView.getInstance(),MediaPlaybackService.class);
                intent.setAction("createUI");
                Bundle bundle=new Bundle();
                bundle.putFloat("x", x);
                bundle.putFloat("y", y);
                bundle.putFloat("w", width);
                bundle.putFloat("h", height);
                intent.putExtras(bundle);
                ShowView.getInstance().startService(intent);
            }
        };
        Activity a = (Activity)context;
        a.runOnUiThread(runnable);
    }
    
    /*
     * 获取视频窗口准备状态
     * 准备好了才播放：true为准备好了
     * */
    private boolean getMovieViewPrepareStatus()
    {
    	Log.e("MovieUtils", "getMovieViewPrepareStatus");
        return MyFloatView.mPlayViewPrepareStatus;
    }

    /*
     * 启动开始播放视频
     * 
     * @param autoPlayList:是否自动轮流播放播单里面的视频，如果为false，当前视频播放完毕后将停止播放
     * */
    public static void movieViewStarPlay(boolean autoPlay)
    {
    	if(!movieViewStatus)
    	{
    		movieViewStatus=true;
	        if (autoPlay)
	        {
	            MyFloatView.mAutoPlayList = true;
	        }
	        else
	        {
	            MyFloatView.mAutoPlayList = false;
	        }
    	}
    }
    
    /*
     * 关闭视频窗口
     * */
    public static void movieViewClose(Context context)
    {
    	Log.e("MovieUtils", "movieViewClose");
    	if(movieViewStatus)
    	{
    		movieViewStatus=false;
	        Runnable runnable = new Runnable()
	        {
	            public void run()
	            {
	            	MyFloatView.onExit();
	            }
	        };
	        Activity a = (Activity)context;
	        a.runOnUiThread(runnable);
	        
    	}
    }
}
