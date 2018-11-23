package com.liuhai.jiugeh5;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.liuhai.jiugeh5.Mycordova;
import com.liuhai.jiugeh5.R;
import com.liuhai.jiugeh5.view.MoveImage;


import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.PluginEntry;
import org.apache.cordova.Whitelist;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewClient;
import org.apache.cordova.engine.SystemWebViewEngine;

import java.net.CookieStore;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressLint({"SetJavaScriptEnabled", "NewApi"})
public class XYXActivity extends Mycordova {
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private SystemWebView webView;
    private int activityRequestCode;
    private CordovaPlugin activityResultCallBack;
    private CordovaPreferences prefs = new CordovaPreferences();
    private Whitelist internalWhitelist = new Whitelist();
    private Whitelist externalWhitelist = new Whitelist();
    private ArrayList<PluginEntry> pluginEntries;
    private Context context;
    private String weburl;
  //  private FloatView mFloatView;
    private ProgressDialog progressDialog;
    private String name;
    private String icon;
    private LinearLayout right;
    private LinearLayout left;
    private SystemWebViewEngine engine;
    private ProgressBar bar;
    private static final String APP_CACAHE_DIRNAME = "/data/data/com.liuhai.jiugeh5/cache/webviewCache";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadConfig();
        if(!preferences.getBoolean("ShowTitle", false))
        {
            getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        if(preferences.getBoolean("SetFullscreen", false))
        {
            preferences.set("Fullscreen", true);
        }
        if(preferences.getBoolean("Fullscreen", false))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                immersiveMode = true;
            }
            else
            {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }

        super.onCreate(savedInstanceState);

        cordovaInterface = makeCordovaInterface();
        if(savedInstanceState != null)
        {
            cordovaInterface.restoreInstanceState(savedInstanceState);
        }
    //    DialogUtil.getInstance().OpenFloat(this);
        setContentView(R.layout.activity_cordova_xyx);
        bar = (ProgressBar) findViewById(R.id.progress);
        init();
        context = this;
        setFullScreen(true);
        weburl = "http://www.563wan.cn";
      // weburl = "http://h5.7youxi.com/";
     //  name = "九哥H5游戏盒";
     //   icon = getIntent().getExtras().getString("icon");
        initView();
   //     initWebView();
    //    mFloatView = new FloatView(this, webView, this, weburl, name, icon);
    //    mFloatView.show();
    //    showProgress("努力加载资源中....");

    }


    @Override
    protected void init() {
        appView = makeWebView();
        createViews();
        if (!appView.isInitialized()) {
            appView.init(cordovaInterface, pluginEntries, preferences);
        }
        cordovaInterface.onCordovaInit(appView.getPluginManager());

        // Wire the hardware volume controls to control media if desired.
        String volumePref = preferences.getString("DefaultVolumeStream", "");
        if ("media".equals(volumePref.toLowerCase(Locale.ENGLISH))) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void loadConfig() {
        ConfigXmlParser parser = new ConfigXmlParser();
        parser.parse(this);
        preferences = parser.getPreferences();
        preferences.setPreferencesBundle(getIntent().getExtras());
        launchUrl = parser.getLaunchUrl();
        pluginEntries = parser.getPluginEntries();
        /*****************changed begin*******************/
        // 内置白名单插件
        PluginEntry object = new PluginEntry("whiltelist", new WhitelistPlugin(this));
        pluginEntries.add(object);
        // 这个已经过时 不再使用 大胆的删掉
//        Config.parser = parser;
        /*****************changed end*******************/
    }



    @Override
    protected CordovaWebView makeWebView() {
        webView = (SystemWebView) findViewById(R.id.game_WV);
//        ConfigXmlParser parser = new ConfigXmlParser();
//        parser.parse(this);//这里会解析res/xml/config.xml配置文件
        engine = new SystemWebViewEngine(webView);
        SystemWebViewClient cordovaWebViewClient = new SystemWebViewClient(
                engine) {
            @Override
            public void onPageFinished(WebView arg0, String arg1) {
                // TODO Auto-generated method stub
                super.onPageFinished(arg0, arg1);
                bar.setVisibility(View.GONE);
           //     cancleProgress();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();// 接受所有网站的证书
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.startsWith("weixin://wap/pay?")) {
               // if (url.contains("wx.tenpay")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                } else {
                    if (url.equals("http://h5.m.gamedog.cn/open/50/")) {
                        XYXActivity.this.finish();
                    } else {
                        webView.loadUrl(url);
                        return true;
                    }

                }
                return true;
            }

        };

        webView.setWebViewClient(cordovaWebViewClient);
        CordovaWebView cordovaWebView = new CordovaWebViewImpl(engine);
        return cordovaWebView;
    }
    @Override
    protected void createViews() {
        //因为要使用自定义布局，此处setContentView需要注掉
//      appView.getView().setId(100);
//      appView.getView().setLayoutParams(new FrameLayout.LayoutParams(
//              ViewGroup.LayoutParams.MATCH_PARENT,
//              ViewGroup.LayoutParams.MATCH_PARENT));
//      setContentView(appView.getView());

        if (preferences.contains("BackgroundColor")) {
            int backgroundColor = preferences.getInteger("BackgroundColor", Color.BLACK);
            // Background of activity:
            appView.getView().setBackgroundColor(backgroundColor);
        }
        appView.getView().requestFocusFromTouch();
    }


    private void initView() {
     //   webView = (SystemWebView) findViewById(R.id.game_WV);
        right = (LinearLayout) findViewById(R.id.ll_menu);
        left = (LinearLayout) findViewById(R.id.ll_menu_left);
        final MoveImage moveImage= (MoveImage) findViewById(R.id.gd_float_view);
        moveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                moveImage.setVisibility(View.INVISIBLE);

                left.setVisibility(View.GONE);
                right.setVisibility(View.GONE);
                if(moveImage.isleft()){

                    left.setVisibility(View.VISIBLE);

                }else{
                    right.setVisibility(View.VISIBLE);

                }


            }
        });


        findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUserCenter();
                left.setVisibility(View.GONE);
                right.setVisibility(View.GONE);
                moveImage.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.tv_back_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUserCenter();
                left.setVisibility(View.GONE);
                right.setVisibility(View.GONE);
                moveImage.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.tv_gift).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opengiftcenter();
                left.setVisibility(View.GONE);
                right.setVisibility(View.GONE);
                moveImage.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.tv_gift_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opengiftcenter();
                left.setVisibility(View.GONE);
                right.setVisibility(View.GONE);
                moveImage.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.tv_active).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActive();
                left.setVisibility(View.GONE);
                right.setVisibility(View.GONE);
                moveImage.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.tv_active_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActive();
                left.setVisibility(View.GONE);
                right.setVisibility(View.GONE);
                moveImage.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.tv_gl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGlcenter();
                left.setVisibility(View.GONE);
                right.setVisibility(View.GONE);
                moveImage.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.tv_gl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openGlcenter();
                left.setVisibility(View.GONE);
                right.setVisibility(View.GONE);
                moveImage.setVisibility(View.VISIBLE);
            }
        });

//        CordovaInterface cordovaInterface = (CordovaInterface) context;



        initWebView();
    }






    /**
     * 返回
     */
    private void openUserCenter() {
        if (webView != null) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                Toast.makeText(getApplication(), "当前已经是最前页面了",1).show();
            }
        }



    }

    /**
     * 关闭
     */
    private void opengiftcenter() {
        finish();


    }

    /**
     * 刷新
     */
    private void openGlcenter() {
   //     DialogUtil.getInstance().ShowShareDialog(this, this, weburl, name, icon);

    }

    /**
     * 分享
     */
    private void openActive() {
        if (webView != null) {
            webView.reload();
        }


    }


    private void initWebView() {
//        internalWhitelist.addWhiteListEntry("*", false);
//        externalWhitelist.addWhiteListEntry("tel:*", false);
//        externalWhitelist.addWhiteListEntry("sms:*", false);
      //  prefs.set("loglevel", "DEBUG");

        //webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setAppCachePath(APP_CACAHE_DIRNAME);
        webView.getSettings().setDatabasePath(APP_CACAHE_DIRNAME);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
     //   webView.getSettings().setBlockNetworkImage(true);
        //如果访问的页面中有Javascript，则webview必须设置支持Javascript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE,null);//开启硬件加速

        webView.getSettings().setUserAgentString(webView.getSettings().getUserAgentString()+"www563wancn");
//        webView.setOnTouchListener ( new View.OnTouchListener () {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // TODO Auto-generated method stub
//                switch (event.getAction ()) {
//                    case MotionEvent.ACTION_DOWN :
//                    case MotionEvent.ACTION_UP :
//                        if (!v.hasFocus ()) {
//                            v.requestFocus ();
//                        }
//                        break ;
//                }
//                return false ;
//            }
//        });
//        webView.init(this, makeWebViewClient(webView),
//                makeChromeClient(webView), pluginEntries, internalWhitelist,
//                externalWhitelist, prefs);


        if(weburl.startsWith("http://sdk.h5.gamedog.cn/game/")){
            webView.loadUrl(weburl+"1/");
        }else{
            webView.loadUrl(weburl);
        }




    }












    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    private void setFullScreen(Boolean isFullScreen) {
        if (isFullScreen) {// 全屏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {// 退出全屏
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

    }


    @Override
    public void onBackPressed() {
    //    super.onBackPressed();



    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
////            if (webView.canGoBack()) {
////                webView.goBack();
////            } else {
//       ///     if (mFloatView != null) {
//        //        mFloatView.destroy();
//       //     }
//         //  finish();
//            DialogUtil.getInstance().DestoryDialog(this);
//
//            //  }
//        }
//        return super.onKeyDown(keyCode, event);
//
//    }






    @Override
    public void loadlisten() {

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }


    }

    @Override
    public void setStatusBar() {


    }




}
