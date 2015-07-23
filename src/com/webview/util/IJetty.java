//========================================================================
//$Id: IJetty.java 474 2012-01-23 03:07:14Z janb.webtide $
//Copyright 2008 Mort Bay Consulting Pty. Ltd.
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package com.webview.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.webview.main.ProxyBridge;
import com.webview.main.R;

/**
 * IJetty
 * 
 * Main Jetty activity. Can start other activities: + configure + download
 * 
 * Can start/stop services: + IJettyService
 */
public class IJetty extends Activity
{

    private static final String TAG = "Jetty";
    private static IJetty instance;
//    public HTML5WebView mWebView;
//    private PropertiesUtils mPropertiesUtil;

    public static final String __START_ACTION = "org.mortbay.ijetty.start";
    public static final String __STOP_ACTION = "org.mortbay.ijetty.stop";
    public static final String __START_MOVIE_ACTION = "org.mortbay.ijetty.movie.start";
    public static final String __STOP_MOVIE_ACTION = "org.mortbay.ijetty.movie.stop";

    public static final String __PORT = "org.mortbay.ijetty.port";
    public static final String __NIO = "org.mortbay.ijetty.nio";
    public static final String __SSL = "org.mortbay.ijetty.ssl";

    public static final String __CONSOLE_PWD = "org.mortbay.ijetty.console";
    public static final String __PORT_DEFAULT = "8080";
    public static final boolean __NIO_DEFAULT = true;
    public static final boolean __SSL_DEFAULT = false;

    public static final String __CONSOLE_PWD_DEFAULT = "admin";

    public static final String __WEBAPP_DIR = "webapps";
    public static final String __ETC_DIR = "etc";
    public static final String __CONTEXTS_DIR = "contexts";
    public static final String __UPLOAD_DIR = "console/upload";

    public static final String __TMP_DIR = "tmp";
    public static final String __WORK_DIR = "work";
    public static final int __SETUP_PROGRESS_DIALOG = 0;
    public static final int __SETUP_DONE = 2;
    public static final int __SETUP_RUNNING = 1;
    public static final int __SETUP_NOTDONE = 0;

    private Button startButton;
    private Button stopButton;
    private Button configButton;
    private TextView footer;
    private TextView info;
    private TextView console;
    private ScrollView consoleScroller;
    private StringBuilder consoleBuffer = new StringBuilder();
    private Runnable scrollTask, videoTask;
    private ProgressDialog progressDialog;
    private Thread progressThread;
    private Handler handler;
    private BroadcastReceiver bcastReceiver;

    class ConsoleScrollTask implements Runnable
    {
        public void run()
        {
            consoleScroller.fullScroll(View.FOCUS_DOWN);
        }
    }

    /**
    

    static
    {
        __JETTY_DIR = new File(Environment.getExternalStorageDirectory(),"jetty");
        //        if(StringUtils.replaceBlank(Build.MODEL).equals("EC3MBXboard") || StringUtils.replaceBlank(Build.MODEL).equals("EC3AdBoard"))
        //        {
        //            __JETTY_DIR = new File(AmlogicExt.getExternalStorage2Directory(),"jetty");
        //        }
        //        else
        //        {
        //            __JETTY_DIR = new File(Environment.getExternalStorageDirectory(),"jetty");
        //        }
        // Ensure parsing is not validating - does not work with android
        System.setProperty("org.eclipse.jetty.xml.XmlParser.Validating","false");

        // Bridge Jetty logging to Android logging
        System.setProperty("org.eclipse.jetty.util.log.class","org.mortbay.ijetty.AndroidLog");
        org.eclipse.jetty.util.log.Log.setLog(new AndroidLog());
    }

    public static IJetty getInstance()
    {
        return instance;
    }

    public IJetty()
    {
        super();

        handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                int total = msg.getData().getInt("prog");
                progressDialog.setProgress(total);
                if (total >= 100)
                {
                    dismissDialog(__SETUP_PROGRESS_DIALOG);
                }
            }

        };
    }

    public String formatJettyInfoLine(String format, Object... args)
    {
        String ms = "";
        if (format != null)
            ms = String.format(format,args);
        return ms + "<br/>";
    }

    public void consolePrint(String format, Object... args)
    {
        String msg = String.format(format,args);
        if (msg.length() > 0)
        {
            consoleBuffer.append(msg).append("<br/>");
            console.setText(Html.fromHtml(consoleBuffer.toString()));
            Log.i(TAG,msg); // Only interested in non-empty lines being output to Log
        }
        else
        {
            consoleBuffer.append(msg).append("<br/>");
            console.setText(Html.fromHtml(consoleBuffer.toString()));
        }

        if (scrollTask == null)
        {
            scrollTask = new ConsoleScrollTask();
        }

        consoleScroller.post(scrollTask);
    }

    protected int getStoredJettyVersion()
    {
        File jettyDir = __JETTY_DIR;
        if (!jettyDir.exists())
        {
            return -1;
        }
        File versionFile = new File(jettyDir,"version.code");
        if (!versionFile.exists())
        {
            return -1;
        }
        int val = -1;
        ObjectInputStream ois = null;
        try
        {
            ois = new ObjectInputStream(new FileInputStream(versionFile));
            val = ois.readInt();
            return val;
        }
        catch (Exception e)
        {
            Log.e(TAG,"Problem reading version.code",e);
            return -1;
        }
        finally
        {
            if (ois != null)
            {
                try
                {
                    ois.close();
                }
                catch (Exception e)
                {
                    Log.d(TAG,"Error closing version.code input stream",e);
                }
            }
        }
    }

    //工作线程  

    class videoSizeThread implements Runnable
    {
        public void run()
        {
            while (!Thread.currentThread().isInterrupted())
            {
                if (AppConstants.PEOPLE_DETECT_TIMEOUT < 0 && MyFloatView.mPlayViewStatus)
                {
                    Log.e(TAG,"PEOPLE_DETECT_TIMEOUT < 0");
                    Message msg = new Message();
                    msg.what = AppConstants.MSG_UPDATE_VIDEO_WINFULLSIZE;
                    mHandler.sendMessage(msg);
                }
                else if ((AppConstants.PEOPLE_DETECT_TIMEOUT > 0) && MyFloatView.mPlayViewIsFull && MyFloatView.mPlayViewStatus)
                {
                    Log.e(TAG,"PEOPLE_DETECT_TIMEOUT > 0");
                    Message msg = new Message();
                    msg.what = AppConstants.MSG_UPDATE_VIDEO_WINSIZE;
                    mHandler.sendMessage(msg);
                }
                try
                {
                    Thread.sleep(500);
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    //////////////////////////////////////////
    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case AppConstants.MSG_INSTALL_COMPLETE:
                {
                    if (msg.arg1 == ApkUtils.SUCCEEDED)
                    {
                        // Log.e("gary", "mHandler install success");
                        //                              String packagename = msg.getData().getString("packagename");
                        //                              if (packagename.equals("com.mylayout.app"))
                        //                                      ApkUtils.startApk(packagename,
                        //                                                      ".MainActivity");
                    }
                    else
                    {
                        // Log.e("gary", "mHandler install failed");
                    }
                }
                    break;
                case AppConstants.MSG_REQUEST_DOWNLOAD:
                {
                    long vStartToNowTime = System.currentTimeMillis() - StartupReceiver.START_TIME;
                    if (vStartToNowTime > AppConstants.START_DOWNLOAD_TIME)
                    {
                        downloadMsg(msg);
                    }
                    else
                    {
                        Message vMsg = mHandler.obtainMessage();
                        mHandler.sendMessageDelayed(vMsg,AppConstants.START_DOWNLOAD_TIME - vStartToNowTime);
                    }
                }
                    break;
                case AppConstants.MSG_UPDATE_TIME:
                    //                        mTimeTv.setText(mTimeStr);
                    //                        if (mTimeStr.equals("00:00:00")) {
                    //                                setDate();
                    //                        } else if (mTimeStr.endsWith("00:00")) {
                    //                                updateAll();
                    //                        } else if (mTimeStr.endsWith("04:00")) {
                    //                                WeatherAndAddressUtil.initAddressInfo(mHandler);
                    //                        }
                    break;
                case AppConstants.MSG_SHOW_MESSAGE:
                {
                    String msgStr = msg.getData().getString("msg");
                    if (TextUtils.isEmpty(msgStr))
                        return;
                    //vMainText.setText(msgStr.trim());
                }
                    break;
                case AppConstants.MSG_RELOCATE_LOGOIMG:
                {

                    IJetty.this.finish();
                    //                      ApkUtils.startApk("com.mylayout.app",
                    //                                      "com.mylayout.app.MainActivity");
                }

                case AppConstants.MSG_SUBMIT_APPS_LIST:
                    //                      mHandler.removeMessages(AppConstants.MSG_SUBMIT_APPS_LIST);
                    submitAppsList(mHandler);
                    break;
                case AppConstants.MSG_UPDATE_VIDEO_WINFULLSIZE:
                    MyFloatView.updateView2FullScreen();
                    break;
                case AppConstants.MSG_UPDATE_VIDEO_WINSIZE:
                    MyFloatView.updateViewPosition();
                    MyFloatView.updateViewSize();
                    MyFloatView.mPlayViewIsFull = false;
                    break;
                default:
                    break;
            }
        };
    };

    private void downloadMsg(android.os.Message msg)
    {
        final String downloadUrl = msg.getData().getString("downloadUrl");
        final String savedName = msg.getData().getString("savedName");
        DownloadManager.getInstance().startDownload(downloadUrl,savedName);
    }

    public static void submitAppsList(final Handler pHandler)
    {
        InterfaceOp.protoSubmmitAppsList(new IRequestListener()
        {
            public void onError(Exception e)
            {
                // TODO Auto-generated method stub
                pHandler.sendEmptyMessageDelayed(AppConstants.MSG_SUBMIT_APPS_LIST,AppConstants.DELAYED_SUBMIT_APPS_LIST);
            }

            public void onComplete(boolean isError, String errMsg, JSONObject respObj)
            {
                // TODO Auto-generated method stub
                if (isError)
                    pHandler.sendEmptyMessageDelayed(AppConstants.MSG_SUBMIT_APPS_LIST,AppConstants.DELAYED_SUBMIT_APPS_LIST);
            }
        });
    }

    public String getDisplayScreenSize()
    {
        WindowManager w = getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try
            {
                widthPixels = (Integer)Display.class.getMethod("getRawWidth").invoke(d);
                heightPixels = (Integer)Display.class.getMethod("getRawHeight").invoke(d);
            }
            catch (Exception ignored)
            {
                Log.e("-->smallstar","getDisplayScreenSize error");
            }
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17)
            try
            {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize",Point.class).invoke(d,realSize);
                widthPixels = realSize.x;
                heightPixels = realSize.y;
            }
            catch (Exception ignored)
            {
                Log.e("-->smallstar","getDisplayScreenSize error");
            }
        return String.valueOf(widthPixels) + "*" + String.valueOf(heightPixels);
    }

    @Override
    protected void onDestroy()
    {
        if (bcastReceiver != null)
            unregisterReceiver(bcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题�?        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息�?        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        MainApplication.getInstance().setAppHandler(mHandler);

        instance = this;
        mWebView = new HTML5WebView(this);
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setHorizontalScrollBarEnabled(false);//水平不显�?        mWebView.setVerticalScrollBarEnabled(false); //垂直不显�?        mWebView.setBackgroundColor(Color.parseColor("#000000")); //ok 不会闪黑�?        mWebView.setLayerType(WebView.LAYER_TYPE_HARDWARE,null);

        //设置�?��自动运行
        Intent autoStarIntent = new Intent("com.mortbay.ijetty.IJetty");
        autoStarIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        autoStarIntent.addFlags(32);
        sendBroadcast(autoStarIntent);

        //启动人体感应
        if (AppConstants.ACTION_PEOPLE_DETECT)
        {
            Intent peopleDetectIntent = new Intent("android.intent.action.set.ec3.pir");
            peopleDetectIntent.putExtra("setting","on");
            sendBroadcast(peopleDetectIntent);
            //VideoSizeOperation();
            if (videoTask == null)
            {
                videoTask = new videoSizeThread();
                new Thread(videoTask).start();
            }
        }

        //获取屏幕分辨�?        AppConstants.RESOLUTION = getDisplayScreenSize();
        //处理配置文件
        mPropertiesUtil = new PropertiesUtils();
        File clientProps = new File(IJetty.__JETTY_DIR + "/" + IJetty.__ETC_DIR + "/properties.xml");
        try
        {
            if (clientProps.exists() && clientProps.length() != 0)
            {
                //判断版本是否比软件assets文件夹里面自带的低，如果版本过低�?��更新
                mPropertiesUtil.readPropertiesFileFromXML(IJetty.getInstance().getBaseContext().getAssets().open("properties.xml"));
                int defaultPropVersion = Integer.parseInt(mPropertiesUtil.getVersion());
                mPropertiesUtil.readPropertiesFileFromXML(clientProps.getAbsolutePath());
                int curPropVersion = Integer.parseInt(mPropertiesUtil.getVersion());
                if (defaultPropVersion > curPropVersion)
                {
                    //如果软件自带属�?版本大于当前使用版本，说明软件升级的同时配置文件�?��升级，使用软件里面自带的默认配置
                    //TODO：需要进�?��处理不能被覆盖的属�?
                    mPropertiesUtil.readPropertiesFileFromXML(IJetty.getInstance().getBaseContext().getAssets().open("properties.xml"));
                    mPropertiesUtil.writePropertiesFileToXML(clientProps.getAbsolutePath());
                }
                Log.w(TAG,"===========================================================");
                AppConstants.CLIENT_CUR_PLAYURL = mPropertiesUtil.getPlayUrl();
                Log.w(TAG,AppConstants.CLIENT_CUR_PLAYURL);
                Log.w(TAG,"===========================================================");
                PlayListUtil.playListVersion = mPropertiesUtil.getPlayListVersion();
                ApkUtils.apkPushVersion = mPropertiesUtil.getApkPushVersion();
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            Log.w("smallstar------>","!clientProps.exists() error!!!!!!");
            e.printStackTrace();
        }

        //启动服务:(心跳，网络状�?
        startService(new Intent(this,DaemonService.class));

        //js交互接口
        ProxyBridge jsBridge = new ProxyBridge(this);
        mWebView.addJavascriptInterface(jsBridge,"ia");
        mWebView.setHorizontalScrollBarEnabled(false);

        setContentView(R.layout.jetty_controller);
        //setContentView(mWebView.getLayout());
        startButton = (Button)findViewById(R.id.start);
        startButton.setVisibility(View.GONE);//隐藏
        stopButton = (Button)findViewById(R.id.stop);
        stopButton.setVisibility(View.GONE);//隐藏
        configButton = (Button)findViewById(R.id.config);
        configButton.setVisibility(View.GONE);//隐藏
        final Button downloadButton = (Button)findViewById(R.id.download);
        downloadButton.setVisibility(View.GONE);//隐藏

        IntentFilter filter = new IntentFilter();
        filter.addAction(__START_ACTION);
        filter.addAction(__STOP_ACTION);
        filter.addAction(__START_MOVIE_ACTION);
        filter.addCategory("default");

        bcastReceiver = new BroadcastReceiver()
        {
            public void onReceive(Context context, Intent intent)
            {
                if (__START_ACTION.equalsIgnoreCase(intent.getAction()))
                {
                    startButton.setEnabled(false);
                    configButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    consolePrint("<br/>Started Jetty at %s",new Date());
                    String[] connectors = intent.getExtras().getStringArray("connectors");
                    if (null != connectors)
                    {
                        for (int i = 0; i < connectors.length; i++)
                            consolePrint(connectors[i]);
                    }

                    printNetworkInterfaces();

                    if (AndroidInfo.isOnEmulator(IJetty.this))
                        consolePrint("Set up port forwarding to see i-jetty outside of the emulator.");

                    //warFile = new File("file:///android_asset/console.war");
                    File file = new File(IJetty.__JETTY_DIR + "/" + IJetty.__WEBAPP_DIR + "/" + "console/settings/basicsettings.html");
                    if (file.exists())
                    {

                        //mWebView.loadUrl("file:///android_asset/bg.html");
                        if (file.length() > 0)
                        {
                            //Not empty, do something here.
                            //i-jetty启动后再加载设置web界面
                            setContentView(mWebView.getLayout());
                            //TODO：判断网络是否已经链接，如果已经链接则不显示设置页面�?                            int onlineTimeout = 0;
                            do
                            {
                                if (onlineTimeout > 10)
                                    break;
                                SystemClock.sleep(1000);
                                onlineTimeout++;
                            }
                            while (!AppConstants.ONLINE_STATUS);
                            if (AppConstants.ONLINE_STATUS)//
                            {
                                mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                                //mWebView.getSettings().setCacheMode( WebSettings.LOAD_NO_CACHE);
                                mWebView.clearHistory();
                                mWebView.clearFormData();
                                mWebView.clearCache(true);
                                mWebView.loadUrl(AppConstants.CLIENT_CUR_PLAYURL);
                            }
                            else
                                mWebView.loadUrl("http://localhost:8080/console/settings/basicsettings.html");
                        }
                    }
                }
                else if (__STOP_ACTION.equalsIgnoreCase(intent.getAction()))
                {
                    startButton.setEnabled(true);
                    configButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    consolePrint("<br/> Jetty stopped at %s",new Date());
                }
                else if (__START_MOVIE_ACTION.equalsIgnoreCase(intent.getAction()))
                {
                    //但是如果service尚没有运行，系统会先调用onCreate()，然后调用onStartCommand().
                    Log.i(TAG,"onReceive() get Broadcast org.mortbay.ijetty.movie.start");
                    Intent mIntent = new Intent("createUI");
                    mIntent.setClass(context,MediaPlaybackService.class);
                    context.startService(mIntent);
                }
            }

        };

        registerReceiver(bcastReceiver,filter);

        // Watch for button clicks.
        startButton.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                if (isUpdateNeeded())
                    IJettyToast.showQuickToast(IJetty.this,R.string.loading);
                else
                {
                    //TODO get these values from editable UI elements
                    Intent intent = new Intent(IJetty.this,IJettyService.class);
                    intent.putExtra(__PORT,__PORT_DEFAULT);
                    intent.putExtra(__NIO,__NIO_DEFAULT);
                    intent.putExtra(__SSL,__SSL_DEFAULT);
                    intent.putExtra(__CONSOLE_PWD,__CONSOLE_PWD_DEFAULT);
                    startService(intent);
                }
            }
        });

        stopButton.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                stopService(new Intent(IJetty.this,IJettyService.class));
            }
        });

        configButton.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                IJettyEditor.show(IJetty.this);
            }
        });

        downloadButton.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                IJettyDownloader.show(IJetty.this);
            }
        });

        info = (TextView)findViewById(R.id.info);
        info.setVisibility(View.GONE);
        footer = (TextView)findViewById(R.id.footer);
        footer.setVisibility(View.GONE);
        console = (TextView)findViewById(R.id.console);
        console.setVisibility(View.GONE);
        consoleScroller = (ScrollView)findViewById(R.id.consoleScroller);
        consoleScroller.setVisibility(View.GONE);

        StringBuilder infoBuffer = new StringBuilder();
        try
        {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(),0);
            infoBuffer.append(formatJettyInfoLine("i-jetty version %s (%s)",pi.versionName,pi.versionCode));
        }
        catch (NameNotFoundException e)
        {
            infoBuffer.append(formatJettyInfoLine("i-jetty version unknown"));
        }
        infoBuffer.append(formatJettyInfoLine("On %s using Android version %s",AndroidInfo.getDeviceModel(),AndroidInfo.getOSVersion()));
        info.setText(Html.fromHtml(infoBuffer.toString()));

        StringBuilder footerBuffer = new StringBuilder();
        footerBuffer.append("<b>Project:</b> <a href=\"http://code.google.com/p/i-jetty\">http://code.google.com/p/i-jetty</a> <br/>");
        footerBuffer.append("<b>Server:</b> http://www.eclipse.org/jetty <br/>");
        footerBuffer.append("<b>Support:</b> http://www.intalio.com/jetty/services <br/>");
        footer.setText(Html.fromHtml(footerBuffer.toString()));

        //软件打开后直接启动WEB服务
        Intent intent = new Intent(IJetty.this,IJettyService.class);
        intent.putExtra(__PORT,__PORT_DEFAULT);
        intent.putExtra(__NIO,__NIO_DEFAULT);
        intent.putExtra(__SSL,__SSL_DEFAULT);
        intent.putExtra(__CONSOLE_PWD,__CONSOLE_PWD_DEFAULT);
        startService(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mWebView.stopLoading();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {

        //获得触摸的坐�? 
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction())
        {
        //触摸屏幕时刻  
            case MotionEvent.ACTION_DOWN:
                if (AppConstants.ORGANIZE_ID == "7")
                {
                    if (MyFloatView.mPlayViewPrepareStatus)
                    {
                        Log.e("smallstar","MyFloatView.mPlayViewPrepareStatus is true!");
                        MyFloatView.mPlayViewStatus = false;
                        MyFloatView.onExit();
                    }
                    if (!ApkUtils.isBackgroundRunning(IJetty.getInstance(),"com.suncco.weather"))
                    {
                        Log.e("smallstar","com.suncco.weather is not running.");
                        ApkUtils.startAppByPackageName("com.suncco.weather");
                    }
                    else
                    {
                        Log.e("smallstar","com.suncco.weather is running.");
                        ApkUtils.startAppByPackageName("com.suncco.weather");
                    }
                }
                break;
            //触摸并移动时�? 
            case MotionEvent.ACTION_MOVE:

                break;
            //终止触摸时刻  
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        Log.w(TAG,"onKeyDown");
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
        {

            Log.w(TAG,"onKeyDown, KEYCODE_BACK");
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0)
            {
                //响应事件的具体代�? 
                if (MyFloatView.mPlayViewPrepareStatus)
                {
                    Log.e("smallstar","MyFloatView.mPlayViewPrepareStatus is true!");
                    MyFloatView.mPlayViewStatus = false;
                    MyFloatView.onExit();
                }

                if (mWebView.getOriginalUrl().equals("http://localhost:8080/console/settings/basicsettings.html"))
                {
                    stopService(new Intent(this,DaemonService.class));
                    stopService(new Intent(this,IJettyService.class));
                    finish();
                }
                else
                {
                    //mWebView.loadUrl("http://localhost:8080/console/settings/index.html");
                    mWebView.loadUrl("http://localhost:8080/console/settings/basicsettings.html");
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

    public static void show(Context context)
    {
        final Intent intent = new Intent(context,IJetty.class);
        context.startActivity(intent);
    }

    @Override
    protected void onResume()
    {
        if (!SdCardUnavailableActivity.isExternalStorageAvailable())
        {
            SdCardUnavailableActivity.show(this);
        }
        else
        {
            //work out if we need to do the installation finish step
            //or not. We do it iff:
            // - there is no previous jetty version on disk
            // - the previous version does not match the current version
            // - we're not already doing the update

            if (isUpdateNeeded())
            {
                setupJetty();
            }
        }

        if (IJettyService.isRunning())
        {
            startButton.setEnabled(false);
            configButton.setEnabled(false);
            stopButton.setEnabled(true);
            //mWebView.loadUrl("http://localhost:8080/console/settings/basicsettings.html");
        }
        else
        {
            startButton.setEnabled(true);
            configButton.setEnabled(true);
            stopButton.setEnabled(false);
        }
        try
        {
            ApkUtils.startService("com.mytime","com.mytime.RequestSetNetworktimeService");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        super.onResume();
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case __SETUP_PROGRESS_DIALOG:
            {
                progressDialog = new ProgressDialog(IJetty.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMessage("Finishing initial install ...");

                return progressDialog;
            }
            default:
                return null;
        }
    }

    private void printNetworkInterfaces()
    {
        try
        {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface ni : Collections.list(nis))
            {
                Enumeration<InetAddress> iis = ni.getInetAddresses();
                for (InetAddress ia : Collections.list(iis))
                {
                    consoleBuffer.append(formatJettyInfoLine("Network interface: %s: %s",ni.getDisplayName(),ia.getHostAddress()));
                }
            }
        }
        catch (SocketException e)
        {
            Log.w(TAG,e);
        }
    }

    protected void setStoredJettyVersion(int version)
    {
        File jettyDir = __JETTY_DIR;
        if (!jettyDir.exists())
        {
            return;
        }
        File versionFile = new File(jettyDir,"version.code");
        ObjectOutputStream oos = null;
        try
        {
            FileOutputStream fos = new FileOutputStream(versionFile);
            oos = new ObjectOutputStream(fos);
            oos.writeInt(version);
            oos.flush();
        }
        catch (Exception e)
        {
            Log.e(TAG,"Problem writing jetty version",e);
        }
        finally
        {
            if (oos != null)
            {
                try
                {
                    oos.close();
                }
                catch (Exception e)
                {
                    Log.d(TAG,"Error closing version.code output stream",e);
                }
            }
        }
    }

    /**
     * We need to an update iff we don't know the current jetty version or it is different to the last version that was
     * installed.
     * 
     * @return
     */
    public boolean isUpdateNeeded()
    {
        //if no previous version file, assume update is required

        try
        {
            //if different previous version, update is required
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(),0);
            if (pi == null)
                return true;

            //if /sdcard/jetty/.update file exists, then update is required
        }
        catch (Exception e)
        {
            //if any of these tests go wrong, best to assume update is true?
            return true;
        }

        return false;
    }
}
