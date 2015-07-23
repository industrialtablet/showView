package com.webview.movie;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;

import com.webview.main.R;
import com.webview.main.ShowView;
import com.webview.util.AppConstants;
import com.webview.util.IJetty;
import com.webview.util.PlayLocation;

public class MyFloatView implements OnCompletionListener, OnErrorListener,
		OnInfoListener, OnPreparedListener, OnSeekCompleteListener,
		OnVideoSizeChangedListener, SurfaceHolder.Callback,
		android.view.View.OnClickListener
{
	private static List<File> mMediaFiles = null;
	private static int mCurIndex = 0;

	private float mTouchStartX;
	private float mTouchStartY;
	static File __JETTY_DIR = new File(Environment.getExternalStorageDirectory(),"jetty");
	public static float x = 0;
	public static float y = 0;
	public static float mWidth = 800;
	public static float mHeight = 600;

	public static boolean mAutoPlayList = false;
	public static boolean mPlayViewPrepareStatus = false;
	public static boolean mPlayViewStatus = false;
	public static boolean mPlayViewIsFull = false;
	public static String playListJsonString = "";

	static ViewGroup mlayoutView;
	static Context context;
	Display currentDisplay;
	public static SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	Button mButton;
	static MediaPlayer  mediaPlayer;// 使用的是MediaPlayer来播放视频
	int videoWidth = 0; // 视频的宽度，初始化，后边会对其进行赋值
	int videoHeight = 0; // 同上
	boolean readyToPlayer = false;

	public final static String LOGCAT = "=>MyFloatView";

	public static final String ACTION_DESTROY_MOVIE = "org.mortbay.ijetty.send.movie.Destroy";

	public MyFloatView(ViewGroup layoutView)
	{
		// TODO Auto-generated constructor stub
		mlayoutView = layoutView;
		context = mlayoutView.getContext();
		mlayoutView.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
			{
				// TODO Auto-generated method stub
				onTouchEvent(paramMotionEvent);
				return false;
			}

		});
		initWindow();
	}

	public View getLayoutView()
	{
		return mlayoutView;
	}

	public void onResume()
	{

	}

	public void initWindow()
	{
		// 获取WindowManager
		wm = (WindowManager) context.getApplicationContext().getSystemService("window");
		// 设置LayoutParams(全局变量）相关参数
		// wmParams = ((MyApplication)getApplication()).getMywmParams();
		wmParams = new WindowManager.LayoutParams();
		/**
		 * 以下都是WindowManager.LayoutParams的相关属性 具体用途可参考SDK文档
		 */
		wmParams.type = /*LayoutParams.TYPE_SYSTEM_ALERT | */LayoutParams.TYPE_SYSTEM_OVERLAY; // 设置window type
		// 设置图片格式，效果为背景透明
		wmParams.format = PixelFormat.TRANSPARENT;
		// 设置Window flag
//		wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
//				| LayoutParams.FLAG_NOT_FOCUSABLE
//				| LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		/*
		 * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
		 * wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL |
		 * LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE;
		 */
		wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
		// 以屏幕左上角为原点，设置x、y初始值
		currentDisplay = wm.getDefaultDisplay();
//		WIDTH = currentDisplay.getWidth();
//		HEIGHT = currentDisplay.getHeight();

		//以下参数调整视频窗口的位置,大小
		wmParams.x = (int)x;
		wmParams.y = (int)y;
		wmParams.width = (int)mWidth;
		wmParams.height = (int)mHeight;
	}

	static int VIEW_WIDTH = 200, VIEW_HEIGHT = 200;

	public void bindViewListener()
	{
		initialUI();
	}

	private void initialUI()
	{
		// 关于SurfaceView和Surfaceolder可以查看文档
		//loc = PlayLocation.restoreLocation();//暂不考虑播放位置
		surfaceView = (SurfaceView) mlayoutView.findViewById(R.id.myView);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnSeekCompleteListener(this);
		mediaPlayer.setOnVideoSizeChangedListener(this);
		//listSupportedMediaFiles();
		if(!mMediaFiles.isEmpty()){
		    curFile = mMediaFiles.get(mCurIndex);
		}
//		if (loc == null) {
//			curFile = mMediaFiles.get(mCurIndex);
//		} else {
//			curFile = new File(loc.getFilename());
//			mCurIndex = mMediaFiles.indexOf(curFile);
//		}
		// 本地地址和网络地址都可以
		try
		{
			Log.w(LOGCAT + "->initialUI", curFile.getAbsolutePath());
			mediaPlayer.setDataSource(curFile.getAbsolutePath());
			mPlayViewPrepareStatus = true;
		} catch (IllegalArgumentException e)
		{
			// TODO: handle exception
			Log.v(LOGCAT, e.getMessage());
			onExit();
		} catch (IllegalStateException e)
		{
			Log.v(LOGCAT, e.getMessage());
			onExit();
		} catch (IOException e)
		{
			Log.v(LOGCAT, e.getMessage());
			onExit();
		}
		//IntentFilter filter = new IntentFilter(ACTION_DESTROY_MOVIE);
		//context.registerReceiver(sReceiver, filter);
	}

	private PlayLocation loc = null;
	private static File curFile = null;

	public static void startPlay()
	{
	    mPlayViewStatus = true;
	    if (mediaPlayer.isPlaying()) return;
	    //mediaPlayer.prepareAsync();
	}

	public boolean prepare() {
		//Log.e(LOGCAT, "prepare() video prepare 1");
//		if (mMediaFiles != null && mMediaFiles.size() < 1) {
//			//MainActivity.mImageView.setVisibility(View.VISIBLE);
//			return false;
//		} else {
//			//MainActivity.mImageView.setVisibility(View.GONE);
//		}
		//Log.e(LOGCAT, "prepare() video prepare 2");
		//loc = PlayLocation.restoreLocation();
	        if(null == mMediaFiles  || mMediaFiles.size() < 1)
	        {
	            Log.e(LOGCAT, "prepare() video prepare error!-----mMediaFiles is null or mMediaFiles.size is zero.");
	            return false;
	        }
		curFile = mMediaFiles.get(mCurIndex);
//		if (loc == null) {
//			curFile = mMediaFiles.get(mCurIndex);
//		} else {
//			curFile = new File(loc.getFilename());
//			mCurIndex = mMediaFiles.indexOf(curFile);
//		}
		
		if (null !=  mediaPlayer ) {
//			mediaPlayer.reset();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.reset();

			mediaPlayer.setOnCompletionListener(this);
			mediaPlayer.setOnErrorListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnSeekCompleteListener(this);
			mediaPlayer.setOnVideoSizeChangedListener(this);

			mediaPlayer.setOnInfoListener(new OnInfoListener() {
				@Override
				public boolean onInfo(MediaPlayer mp, int what, int extra) {
					//Log.e("gayr", "onInfo what: "+what+"  extra: "+extra);
					return false;
				}
			});
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// .STREAM_MUSIC);
			mediaPlayer.setDisplay(surfaceHolder);
			mediaPlayer.setScreenOnWhilePlaying(true);

			try
			{
				Log.v(LOGCAT, curFile.getAbsolutePath());
				mediaPlayer.setDataSource(curFile.getAbsolutePath());
				mPlayViewPrepareStatus = true;
				//记录log到本地
				//LogUtil.createLog(curFile.getAbsolutePath());
			} catch (IllegalArgumentException e)
			{
				// TODO: handle exception
				Log.v(LOGCAT, e.getMessage());
				onExit();
			} catch (IllegalStateException e)
			{
				Log.v(LOGCAT, e.getMessage());
				onExit();
			} catch (IOException e)
			{
				Log.v(LOGCAT, e.getMessage());
				onExit();
			}

			mediaPlayer.prepareAsync();
			//mPlayViewPrepareStatus = true;
			// mMediaPlayer.seekTo(pos);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
//			if (curFile != null && curFile.exists())
//				curFile.delete();
			SystemClock.sleep(10);
			//MainActivity.mImageView.setVisibility(View.GONE);
			prepareNext();
		}
		return false;
	}


	public void play() {
		if (mediaPlayer.isPlaying())
			return;
		mediaPlayer.start();
//		new Timer().schedule(new TimerTask() {
//			@Override
//			public void run() {
//				if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//					int length = mediaPlayer.getDuration();
//					//Log.e("smallstar", "play length: " + length);
//					mMediaPlayer.seekTo(length);
//				}
//			}
//		}, 2 * 1000);
//		if (loc != null) {
//			mediaPlayer.seekTo(loc.getPos());
//		}
	}


	static BroadcastReceiver sReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// TODO Auto-generated method stub
			//onExit();
		}
	};

    public static void onExit()
    {
        try
        {
        	wm.removeView(mlayoutView);
    		if (null !=  mediaPlayer ) {
//    			mediaPlayer.reset();
    			mediaPlayer.release();
    			mediaPlayer = null;
    		}
//        	mediaPlayer.pause();
//			mediaPlayer.stop();
//			mediaPlayer.reset();
//			mediaPlayer.release();
			Intent mIntent = new Intent("removeUI");
			mIntent.setClass(context, MediaPlaybackService.class);
			context.startService(mIntent);
			context.unregisterReceiver(sReceiver);
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
    }

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height)
	{
		Log.v(LOGCAT, "surfaceChanged Called.");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		Log.v(LOGCAT, "surfaceCreated called.");
		mediaPlayer.setDisplay(holder);// 若无此设置，将只有声音而无图像
		mediaPlayer.prepareAsync();
//		try
//		{
//			mediaPlayer.prepareAsync();
//		} catch (IllegalStateException e)
//		{
//			onExit();
//		} catch (IOException e)
//		{
//			onExit();
//		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		Log.v(LOGCAT, "surfaceDestroyed Called");
		//onExit();
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer arg0, int arg1, int arg2)
	{
		Log.v(LOGCAT, "onVideoSizeChanged Called");
	}

	@Override
	public void onSeekComplete(MediaPlayer mp)
	{
		Log.v(LOGCAT, "onSeekComplete Called");

	}

	public static void listAllMediaFiles(){
	    //File[] files;
	    mMediaFiles = new ArrayList<File>();
	    mCurIndex = 0;
	    if(playListJsonString.isEmpty())
	    {
	        Log.e(LOGCAT, "playListJsonString is null!");
	    }
	    else
	    {
	         JSONArray jsonar=null;
	          try {
	              jsonar = new JSONArray(playListJsonString);
	              for(int i=0; i<jsonar.length(); i++)
	              {
	                  JSONObject oj = jsonar.getJSONObject(i);
	                  Log.v("smallstar", oj.getString("file"));
	                  String filenpath = oj.getString("file");
	                  String end = filenpath.toLowerCase();
	                  if (end.endsWith(".mp4") || end.endsWith(".rmvb")
                                  || end.endsWith(".3gp") || end.endsWith(".avi")
                                  || end.endsWith(".rm") || end.endsWith(".mov")
                                  || end.endsWith(".flv") || end.endsWith(".f4v") || end.endsWith(".mkv")) {
	                      File file = new File(__JETTY_DIR+"/"+IJetty.__WEBAPP_DIR + "/console/" + filenpath);
	                      if(file.exists()){mMediaFiles.add(file);}
	                      
	                  }
	              }
	          } catch (JSONException e) {
	              e.printStackTrace();
	          }
	    }
	    if(mMediaFiles.isEmpty()){
	        //播放垫底节目
	        File file = new File(__JETTY_DIR+"/"+IJetty.__WEBAPP_DIR + "/console/" + "upload/default.mp4");
	        mMediaFiles.add(file);
	    }
	    //Log.e("gary", "listSupportedMediaFiles: " + mMediaFiles);
	}

	private static void listSupportedMediaFiles() {
		File mediaFile = new File(AppConstants.getMediaSdFolder());
		mCurIndex = 0;
		mMediaFiles = null;
		if (!mediaFile.exists())
			mediaFile.mkdirs();
		File[] files = mediaFile.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f) {
				if (f == null)
					return false;
				if (!f.exists())
					return false;
				if (f.isDirectory())
					return true;
				String end = f.getName().toLowerCase();
				if (end.endsWith(".mp4") || end.endsWith(".rmvb")
						|| end.endsWith(".3gp") || end.endsWith(".avi")
						|| end.endsWith(".rm") || end.endsWith(".mov")
						|| end.endsWith(".flv") || end.endsWith(".mkv")) {
					return true;
				}

				return false;
			}
		});
		if (files == null)
			return;
		mMediaFiles = Arrays.asList(files);
		Log.e("gary", "listSupportedMediaFiles: " + mMediaFiles);
	}


	@Override
	public void onPrepared(MediaPlayer mp)
	{
		//MediaPlayer加载准备完毕才能开始播放
		Log.v(LOGCAT, "onPrepared Called");
		videoWidth = (int)this.mWidth; //mp.getVideoWidth();
		videoHeight = (int)this.mHeight;//mp.getVideoHeight();
		/** 这一步为videod的高宽赋值，将其值控制在可控的范围之内，
		 * 在VideoView的源码中也有相关的代码
		 *  */
//		if (videoWidth > currentDisplay.getWidth()
//				|| videoHeight > currentDisplay.getHeight())
//		{
//			float heightRatio = (float) videoHeight
//					/ (float) currentDisplay.getHeight();
//			float widthRatio = (float) videoWidth
//					/ (float) currentDisplay.getWidth();
//			if (heightRatio > 1 || widthRatio > 1)
//			{
//				if (heightRatio > widthRatio)
//				{
//					videoHeight = (int) Math.ceil((float) videoHeight
//							/ (float) heightRatio);
//					videoWidth = (int) Math.ceil((float) videoWidth
//							/ (float) heightRatio);
//				} else
//				{
//					videoHeight = (int) Math.ceil((float) videoHeight
//							/ (float) widthRatio);
//					videoWidth = (int) Math.ceil((float) videoWidth
//							/ (float) widthRatio);
//				}
//			}
//		}

		//videoWidth = 800;
		//videoHeight = 600;

		// 设置悬浮窗口长宽数据
		//Log.d(LOGCAT, String.valueOf(videoWidth) + "<=>" + String.valueOf(videoHeight));
		wmParams.width = VIEW_WIDTH = videoWidth;
		wmParams.height = VIEW_HEIGHT = videoHeight;
		if(mPlayViewIsFull)
		    updateView2FullScreen();
		else
		    surfaceView.setLayoutParams(new FrameLayout.LayoutParams(videoWidth, videoHeight));
		if(mAutoPlayList)
		{
		    try {
	                        play();
	                } catch (Exception e) {
	                        Log.e(LOGCAT, "========>");
	                        e.printStackTrace();
	                        if (curFile != null && curFile.exists())
	                                curFile.delete();
	                        prepareNext();
	                }
		}	
	}

	@Override
	public boolean onInfo(MediaPlayer arg0, int whatInfo, int extra)
	{
		if (whatInfo == MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING)
		{
			Log.v(LOGCAT, "Media Info, Media Info Bad Interleaving " + extra);
		} else if (whatInfo == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE)
		{
			Log.v(LOGCAT, "Media Info, Media Info Not Seekable " + extra);
		} else if (whatInfo == MediaPlayer.MEDIA_INFO_UNKNOWN)
		{
			Log.v(LOGCAT, "Media Info, Media Info Unknown " + extra);
		} else if (whatInfo == MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING)
		{
			Log.v(LOGCAT, "MediaInfo, Media Info Video Track Lagging " + extra);
		} else if (whatInfo == MediaPlayer.MEDIA_INFO_METADATA_UPDATE)
		{
			Log.v(LOGCAT, "MediaInfo, Media Info Metadata Update " + extra);
		}
		return false;
	}


	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2)
	{
		Log.v(LOGCAT, "onError Called");
		if (arg1 == MediaPlayer.MEDIA_ERROR_SERVER_DIED)
		{
			Log.v(LOGCAT, "Media Error, Server Died " + arg2);
		} else if (arg1 == MediaPlayer.MEDIA_ERROR_UNKNOWN)
		{
			Log.v(LOGCAT, "Media Error, Error Unknown " + arg2);
		}
		onExit();//出错后关闭视频播放窗口。
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer arg0)
	{
		Log.v(LOGCAT, "onCompletion Called");
		
		if ( null != mediaPlayer ) {
			Log.e("smallstar", " onComplete: "+ arg0.getCurrentPosition());
			try {
				mediaPlayer.setScreenOnWhilePlaying(false);
				mediaPlayer.reset();
				mediaPlayer.release();
				mediaPlayer = null;
			} catch (Exception e) {
				e.printStackTrace();
				onExit();
			}
			
		}
		prepareNext();
		//onExit(); //播放完后是否关闭视频播放窗口。
	}


	private void prepareNext(){
		if (mCurIndex < 0) {
			//listSupportedMediaFiles();
		    listAllMediaFiles();
		} else {
			mCurIndex++;
			if (mCurIndex >= mMediaFiles.size()) {
				//listSupportedMediaFiles();
			    listAllMediaFiles();
			}
		}
		prepare();
	}

	public void showLayoutView()
	{
		wm.addView(mlayoutView, wmParams);
	}

//	public void playViewPrepareFinish()
//	{
//	    mPlayViewPrepareStatus = true;
//	}

	public static int WIDTH = 1024, HEIGHT = 552;
	public static final int left = -VIEW_WIDTH / 2, top = 0, right = WIDTH
			- VIEW_WIDTH / 2, bottom = HEIGHT - VIEW_HEIGHT / 2;
	private static WindowManager wm = null;
	private static WindowManager.LayoutParams wmParams = null;
	private float mLastTouchY = 0;
	int rangeOut_H = -1, rangeOut_V = -1;

	public boolean onTouchEvent(MotionEvent event)
	{
		// 获取相对屏幕的坐标，即以屏幕左上角为原点
		x = event.getRawX();
		y = event.getRawY(); // 25是系统状态栏的高度
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			// 获取相对View的坐标，即以此View左上角为原点
			mTouchStartX = event.getX();
			mTouchStartY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			if (mLastTouchY > 110)
			{
				mTouchStartX = event.getX();
				mTouchStartY = event.getY();
				mLastTouchY = 0;
			}
			int positionX = (int) (x - mTouchStartX);
			int positionY = (int) (y - mTouchStartY);

			if (positionX < left || positionX > right)
			{// check borad just
				// reset
				rangeOut_H = positionX < left ? left : right;
			}
			if (positionY < top || positionY > bottom)
			{// check borad just
				// reset
				rangeOut_V = positionY < top ? top : bottom;
			}

			updateViewPosition();
			break;

		case MotionEvent.ACTION_UP:

			if (rangeOut_H != -1)
			{
				x = rangeOut_H + mTouchStartX;
			}
			if (rangeOut_V != -1)
			{
				y = rangeOut_V + mTouchStartY;
			}

			updateViewPosition();
			mTouchStartX = mTouchStartY = 0;
			rangeOut_H = rangeOut_V = -1;
			break;
		default:
			mTouchStartX = mTouchStartY = 0;
			rangeOut_H = rangeOut_V = -1;
			break;
		}
		return true;
	}


    public static void updateViewSize()
    {
        Log.e("==========>" + LOGCAT,"updateViewSize");
        if (mWidth < 0)
            mWidth = 0;
        if (mHeight < 0)
            mHeight = 0;
        surfaceView.setLayoutParams(new FrameLayout.LayoutParams((int)mWidth,(int)mHeight));
    }

    public static void updateViewSize(float width, float height )
    {
        Log.e("==========>" + LOGCAT,"updateViewSize");
        if (width < 0)
            width = 0;
        if (height < 0)
            height = 0;
        surfaceView.setLayoutParams(new FrameLayout.LayoutParams((int)width,(int)height));
    }
	//放大
	public static void zoomIn(){
	    wmParams.width = wmParams.width + 20;
	    wmParams.height = wmParams.height + 20;
	    //wm.updateViewLayout(mlayoutView, wmParams);
	    surfaceView.setLayoutParams(new FrameLayout.LayoutParams(wmParams.width, wmParams.height));
	}
	//缩小
	public static void zoomOut(){
	    wmParams.width = wmParams.width - 20;
	    wmParams.height = wmParams.height - 20;
	    wm.updateViewLayout(mlayoutView, wmParams);
	}

        public static void updateViewPosition()
        {
            //更新浮动窗口位置参数
            Log.e("==========>"+LOGCAT, "updateViewPosition");
            if(x<0) x=0;
            if(y<0) y=0;
            wmParams.x = (int)x;
            wmParams.y = (int)y;
            wm.updateViewLayout(mlayoutView, wmParams);
        }
        
        public static void updateView2FullScreen()
        {
            String widthPixels = AppConstants.RESOLUTION.substring(0, AppConstants.RESOLUTION.indexOf("*"));
            String heightPixels = AppConstants.RESOLUTION.substring(AppConstants.RESOLUTION.indexOf("*") + 1);
            Log.e("==========>"+LOGCAT, widthPixels);
            Log.e("==========>"+LOGCAT, heightPixels);
            MyFloatView.updateViewPosition(0,0);
            //16:9=widthPixels:x
            if(Integer.valueOf(widthPixels) > Integer.valueOf(heightPixels))
            {
                MyFloatView.updateViewSize(Integer.valueOf(widthPixels), Integer.valueOf(heightPixels));
            }
            else
            {
                MyFloatView.updateViewSize(Integer.valueOf(widthPixels), Integer.valueOf(widthPixels)*9/16);
            }
            //MyFloatView.updateViewSize(1366,768);
            MyFloatView.mPlayViewIsFull = true;
        }
        
        public static void updateViewPosition(float xx, float yy)
        {
            //更新浮动窗口位置参数
            Log.e("==========>"+LOGCAT, "updateViewPosition");
            if(xx<0) xx=0;
            if(yy<0) yy=0;
            wmParams.x = (int)xx;
            wmParams.y = (int)yy;
            wm.updateViewLayout(mlayoutView, wmParams);
        }
        
	public static void leftMoveView()
	{
	    Log.v("smallstar", "================moveleft===================");
	    wmParams.x = (int) (wmParams.x - 5);
	    //wmParams.y = (int) (y - mTouchStartY);
	    wm.updateViewLayout(mlayoutView, wmParams);
	}

	public static void rightMoveView()
	{
	    Log.v("smallstar", "================right===================");
	       wmParams.x = (int) (wmParams.x + 5);
	       //wmParams.y = (int) (y - mTouchStartY);
	       wm.updateViewLayout(mlayoutView, wmParams);
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
	}

}