package com.laiyifen.capital;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lyfloginlibrary.CaculateManager;
import com.example.lyfloginlibrary.sync.SyncManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends AppCompatActivity implements SyncManager.DownloadSyncDelegate {

    private WebView         webView;
    private String          appKey          = "f72953b68fb1489f926293beb8b5f39e";
    private String          appSecret       = "111111";
    private CaculateManager caculateManager = new CaculateManager();
    private String          mTitle;
    private String          mUrl;

    @BindView(R.id.tv_title)
    TextView       tvTitle;
    @BindView(R.id.iv_return)
    ImageView      ivReturn;
    @BindView(R.id.rl_topview)
    RelativeLayout rlTopView;
    @BindView(R.id.iv_select)
    ImageView      ivMore;

    public static String serviceAddress = BuildConfig.SERVER_URL;
    private String downUserMimeType;
    private String downContentDisposition;
    private String downLoadUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.wb_view);
        webView.setBackgroundColor(0);
        webView.setBackgroundResource(R.drawable.icon_background);
        ButterKnife.bind(this);
        caculateManager.bindService(this, this, appKey, appSecret);
        initWebView();
        //initDialog();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mUrl = url;
                if (url != null && TextUtils.equals(url, "lyf://mipAuthLogin")) {
                    if (caculateManager.IsToken()) {
                        caculateManager.VerifyToken();
                    }
                    caculateManager.lyfLogin();
                    return true;
                }
                if (url != null && url.contains("email://")) {
                    Log.v("myTag", "email");
                    String a = url;
                    String email = url.substring(9, a.length());
                    showEmalDilog(email);
                    return true;
                }
                if (url != null && url.contains("tel:/")) {
                    Log.v("myTag", "phone");
                    String a = url;
                    String phone = url.substring(6, a.length());
                    showPhoneDilog(phone);
                    return true;
                }
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });
        String url = serviceAddress + "menus/index";
        webView.loadUrl(serviceAddress + "menus/index");
    }


    private void initWebView() {
//        webView.clearCache(true);
//        clearCash();
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        webView.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        //设置WebView缓存模式 默认断网情况下不缓存
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        //断网情况下加载本地缓存
        // webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        //让WebView支持DOM storage API
        webView.getSettings().setDomStorageEnabled(true);

        //让WebView支持缩放
        webView.getSettings().setSupportZoom(true);

        //启用WebView内置缩放功能
        webView.getSettings().setBuiltInZoomControls(true);

        //让WebView支持可任意比例缩放
        webView.getSettings().setUseWideViewPort(true);


        //让WebView支持播放插件
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);

        //设置WebView使用内置缩放机制时，是否展现在屏幕缩放控件上
        webView.getSettings().setDisplayZoomControls(false);

        //设置在WebView内部是否允许访问文件
        webView.getSettings().setAllowFileAccess(true);

        //设置WebView的访问UserAgent

        //设置脚本是否允许自动打开弹窗
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        // 加快HTML网页加载完成速度
        if (Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            webView.getSettings().setLoadsImagesAutomatically(false);
        }

        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        // 开启Application H5 Caches 功能
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setBlockNetworkImage(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // 设置编码格式
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                // H5中包含下载链接的话让外部浏览器去处理
                //                Intent intent = new Intent(Intent.ACTION_VIEW);
                //                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                //                intent.setData(Uri.parse(url));
                //                startActivity(intent);
                downLoadUrl = url;
                downUserMimeType = mimeType;
                downContentDisposition = contentDisposition;
                getPermission();
            }
        });
        webView.setWebChromeClient(webChromeClient);
    }

    private void initWebViewQuit() {
        //        webView.clearCache(true);
               clearCash();
        webView.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        //设置WebView缓存模式 默认断网情况下不缓存
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);


        webView.getSettings().setLoadWithOverviewMode(true);
        //断网情况下加载本地缓存
        // webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        //让WebView支持DOM storage API
        webView.getSettings().setDomStorageEnabled(true);

        //让WebView支持缩放
        webView.getSettings().setSupportZoom(true);

        //启用WebView内置缩放功能
        webView.getSettings().setBuiltInZoomControls(true);

        //让WebView支持可任意比例缩放
        webView.getSettings().setUseWideViewPort(true);

        //让WebView支持播放插件
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);

        //设置WebView使用内置缩放机制时，是否展现在屏幕缩放控件上
        webView.getSettings().setDisplayZoomControls(false);

        //设置在WebView内部是否允许访问文件
        webView.getSettings().setAllowFileAccess(true);

        //设置WebView的访问UserAgent

        //设置脚本是否允许自动打开弹窗
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        // 加快HTML网页加载完成速度
        if (Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            webView.getSettings().setLoadsImagesAutomatically(false);
        }


        // 开启Application H5 Caches 功能
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setBlockNetworkImage(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // 设置编码格式
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                // H5中包含下载链接的话让外部浏览器去处理
                //                Intent intent = new Intent(Intent.ACTION_VIEW);
                //                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                //                intent.setData(Uri.parse(url));
                //                startActivity(intent);
                downLoadUrl = url;
                downUserMimeType = mimeType;
                downContentDisposition = contentDisposition;
                getPermission();
            }
        });
        webView.setWebChromeClient(webChromeClient);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @OnClick({R.id.iv_return, R.id.iv_select})
    public void myClick(View view) {
        switch (view.getId()) {
            case R.id.iv_return:
                if (webView.canGoBack()) {
                    webView.goBack();
                }
                if (webView.getTitle().equals("首页")) {
                    rlTopView.setVisibility(View.GONE);
                }
                break;
            case R.id.iv_select:
                com.laiyifen.capital.SetPopView setPopView = new com.laiyifen.capital.SetPopView(this, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //刷新网页
                        if (view.getId() == R.id.ll_reflash) {
                            webView.reload();
                        }
                        //分享
                        if (view.getId() == R.id.ll_share) {
                            //分享文字
                            Intent textIntent = new Intent(Intent.ACTION_SEND);
                            textIntent.setType("text/plain");
                            textIntent.putExtra(Intent.EXTRA_TEXT, "【"+mTitle+"】\r"+mUrl);
                            textIntent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
                            textIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(Intent.createChooser(textIntent, "分享"));


                            //分享图片
                            //                             PicturlUtils.saveMyBitmap(MainActivity.this);
                            //                             String path =  PicturlUtils.getResourcesUri(MainActivity.this,R.mipmap.ic_launcher);
                            //
                            //                            Intent imageIntent = new Intent(Intent.ACTION_SEND);
                            //                            imageIntent.setType("image/*");
                            //                            imageIntent.putExtra(Intent.EXTRA_TEXT, "【"+mTitle+"】\r"+mUrl);
                            //                            imageIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("/storage/emulated/0/mypicture/aiwuicon.jpg"));
                            //                            startActivity(Intent.createChooser(imageIntent, "分享"));

                        }
                        //退出
                        if (view.getId() == R.id.ll_logout) {
                            rlTopView.setVisibility(View.GONE);
                            initWebViewQuit();
                            // http://10.0.14.13:9080/investbpm/
                            //"https://invest.laiyifen.com:8001/investbpm/
                            LodaNewClient(serviceAddress + "menus/index", "");
                        }
                    }
                });
                setPopView.show(ivMore, 0);
                break;
            default:
                break;
        }
    }


    private void clearCash() {
        CookieSyncManager.createInstance(getApplicationContext());  //Create a singleton CookieSyncManager within a context
        CookieManager cookieManager = CookieManager.getInstance(); // the singleton CookieManager instance
        cookieManager.removeAllCookie();// Removes all cookies.
        CookieSyncManager.getInstance().sync(); // forces sync manager to sync now
        webView.setWebChromeClient(null);
        webView.setWebViewClient(null);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.clearCache(true);
    }

    @Override
    protected void onDestroy() {
       // clearCash();
        super.onDestroy();
        caculateManager.unbindService();
    }

    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private WebChromeClient webChromeClient = new WebChromeClient() {


        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            //注意:
            //必须要这一句代码:result.confirm()表示:
            //处理结果为确定状态同时唤醒WebCore线程
            //否则不能继续点击按钮
            result.confirm();
            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

        }

        //获取网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            mTitle = title;
            if (title != null && "投资管理系统".equals(title)) {
            } else {
                rlTopView.setVisibility(View.VISIBLE);
                if (title != null) {
                    tvTitle.setText(title);
                }
                if (webView.canGoBack()) {
                    ivReturn.setVisibility(View.VISIBLE);
                } else {
                    ivReturn.setVisibility(View.INVISIBLE);
                }
            }
            Log.i("myTag", "网页标题:" + title);
        }

        //加载进度回调


    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("ansen", "是否有上一个页面:" + webView.canGoBack());
        if (webView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {//点击返回按钮的时候判断有没有上一页
            webView.goBack(); // goBack()表示返回webView的上一页面
            //是首页的情况隐藏顶部标题栏
            if (webView.getTitle().equals("首页")) {
                rlTopView.setVisibility(View.GONE);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDownloadSyncStart() {

    }

    @Override
    public void onDownloadSyncProgressChanged(int progress, int all) {

    }

    @Override
    public void onDownloadSyncSuccess(String Msg) {
        Toast.makeText(this, "msg=" + Msg, Toast.LENGTH_LONG);
        try {
            JSONObject object = new JSONObject(Msg);
            JSONObject object1 = new JSONObject(object.getString("data"));
            String userId = object1.getString("user_id");
            Log.v("myTag", "userId=" + userId);
            LodaNewClient(serviceAddress + "portal", userId);
        } catch (Exception e) {

        }
        Log.v("MainActivity", Msg);
        Log.v("myTag", "msg=" + Msg);
    }

    @Override
    public void onDownloadSyncFail(String Exception) {
        Log.v("MainActivity", Exception);
    }

    private void LodaNewClient(String url, String userId) {
        Map<String, String> map = new HashMap();
        map.put("iv-user", userId);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url != null && TextUtils.equals(url, "lyf://mipAuthLogin")) {
                    if (caculateManager.IsToken()) {
                        caculateManager.VerifyToken();
                    }
                    caculateManager.lyfLogin();
                    return true;
                }
                if (url != null && url.contains("email://")) {
                    Log.v("myTag", "email");
                    String a = url;
                    String email = url.substring(9, a.length());
                    showEmalDilog(email);
                    return true;
                }
                if (url != null && url.contains("tel://")) {
                    Log.v("myTag", "phone");
                    String a = url;
                    String phone = url.substring(6, a.length());
                    showPhoneDilog(phone);
                    return true;
                }
                view.loadUrl(url);
                return true;

            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //  hideProgress();

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });
        //http://10.0.14.13:9080/investbpm/portal
        webView.loadUrl(url, map);
    }

    private void showEmalDilog(String address) {
        com.laiyifen.capital.BottomDialog emailDilog = new com.laiyifen.capital.BottomDialog(this, R.layout.email_dilog_layout, new int[]{R.id.tv_copy_email, R.id.tv_makedefalt_email, R.id.tv_cancle});
        emailDilog.setOnBottomItemClickListener(new com.laiyifen.capital.BottomDialog.OnBottomItemClickListener() {
            @Override
            public void onBottomItemClick(com.laiyifen.capital.BottomDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.tv_copy_email:
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 创建普通字符型ClipData
                        ClipData mClipData = ClipData.newPlainText("Label", address);
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(mClipData);
                        Toast.makeText(MainActivity.this, "邮箱已复制", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.tv_makedefalt_email:
                        Uri uri = Uri.parse("mailto:" + address);
                        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                        //intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
                        // intent.putExtra(Intent.EXTRA_SUBJECT, "这是邮件的主题部分"); // 主题
                        // intent.putExtra(Intent.EXTRA_TEXT, "这是邮件的正文部分"); // 正文
                        MainActivity.this.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
                        break;
                    case R.id.tv_cancle:
                        emailDilog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });


        emailDilog.show();
        TextView tvPhoneName = emailDilog.findViewById(R.id.tv_email_name);
        tvPhoneName.setText("向" + address + "发送邮件");
    }

    private void showPhoneDilog(String phone) {
        com.laiyifen.capital.BottomDialog phoneDilog = new com.laiyifen.capital.BottomDialog(this, R.layout.phone_dilog_layout, new int[]{R.id.tv_call_phone, R.id.tv_copy_phone, R.id.tv_cancle});
        phoneDilog.setOnBottomItemClickListener(new com.laiyifen.capital.BottomDialog.OnBottomItemClickListener() {
            @Override
            public void onBottomItemClick(com.laiyifen.capital.BottomDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.tv_call_phone:
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                        startActivity(dialIntent);
                        break;
                    case R.id.tv_copy_phone:
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 创建普通字符型ClipData
                        ClipData mClipData = ClipData.newPlainText("Label", phone);
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(mClipData);
                        Toast.makeText(MainActivity.this, "号码已复制", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.tv_cancle:
                        phoneDilog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
        phoneDilog.show();
        TextView tvPhoneName = phoneDilog.findViewById(R.id.tv_phone_name);
        tvPhoneName.setText(phone + "可能是一个电话号码，你可以");
    }

    private void getPermission() {
        MainActivityPermissionsDispatcher.getMultiWithPermissionCheck(this);
    }
    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void getMulti() {
        Log.v("myTag","getMulti");
        com.laiyifen.capital.DoloadUtils.downloadBySystem(MainActivity.this,downLoadUrl,downContentDisposition,downUserMimeType);
    }
    @OnPermissionDenied({Manifest.permission.READ_PHONE_STATE})//一旦用户拒绝了
    public void multiDenied() {
    }
    @OnNeverAskAgain({Manifest.permission.WRITE_EXTERNAL_STORAGE})//用户选择的不再询问
    public void multiNeverAsk() {
        Toast.makeText(this, "已拒绝一个或以上权限，并不再询问", Toast.LENGTH_SHORT).show();
    }
   

}
