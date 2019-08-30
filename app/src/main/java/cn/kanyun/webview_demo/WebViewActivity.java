package cn.kanyun.webview_demo;

import android.app.DownloadManager;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.orhanobut.logger.Logger;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.kanyun.webview_demo.databinding.ActivityWebviewBinding;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    UrlViewModel urlViewModel;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.tool_bar)
    androidx.appcompat.widget.Toolbar toolbar;

    /**
     * 初始Url,即，webView.load()中的url
     */
    private static String url;

    private static boolean isSelectAll = false;

    CurrentShowUrlViewModel currentShowUrlViewModel;

    private IntentFilter intentFilter;

    ActivityWebviewBinding binding;

    /**
     * 当前网页标题
     * 这个值的更改是在MyWebViewClient中的onPageFinished方法中更改的
     */
    public static String currentPageTitle;

    DownloadReceiver downloadReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.activity_webview);

//        databing配置
        currentShowUrlViewModel = ViewModelProviders.of(this).get(CurrentShowUrlViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview);
        binding.setShowUrlViewModel(currentShowUrlViewModel);
        binding.setLifecycleOwner(this);


        ButterKnife.bind(this);

        downloadReceiver = new DownloadReceiver();
        intentFilter = new IntentFilter();
//        只有持有相同的action的接受者才能接收此广播(这里注册监听两个广播,一个是系统的下载广播,一个是自定义的广播)
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentFilter.addAction(FileDownLoadListener.DOWNLOAD_FILE_NAME_ACTION);
//        注册广播
        this.registerReceiver(downloadReceiver, intentFilter);

        setSupportActionBar(toolbar);
//        在xml中设置文字/icon并不生效
        toolbar.setNavigationIcon(R.drawable.ic_exit);
//        使用toolbar.setTitle("")无效,需要这样设置
        getSupportActionBar().setTitle("这是一个标题");
//        需要注意的是该方法需要放在setSupportActionBar()方法后执行[导航点击监听->返回上个页面],这个按钮就是ToolBar左侧的按钮
        toolbar.setNavigationOnClickListener(v -> finish());

//        从ViewModel中获取值(很明显的,ViewModel并不能成跨Activity)
//        urlViewModel = ViewModelProviders.of(this).get(UrlViewModel.class);
//        url = urlViewModel.getUrl().getValue();

//        从Application中取到保存的全局变量
        MyApplication application = (MyApplication) MyApplication.getInstance();
        url = application.getUrl();

        webView = findViewById(R.id.web_view);


//        设置这个方法就是让需要加载的网页在WebView里加载，而不是打开手机里的浏览器去加载
        webView.setWebChromeClient(new MyWebChromeClient(progressBar));
//        给WebView添加Chrome处理器，用来处理js的对话框（比如alert等）、图标的渲染等
        webView.setWebViewClient(new MyWebViewClient(currentShowUrlViewModel));

//        得到WebSetting
        WebSettings webSettings = webView.getSettings();
//        启用javascript
        webSettings.setJavaScriptEnabled(true);
//        页面进行 Js 注入
//        webView.addJavascriptInterface(new WebAppBridge(new WebAppBridge.OauthLoginImpl() {
//                    @Override
//                    public void getResult(String s) {
//                        //TODO
//                    }
//                }),
//                "oauth");


//        设置Agent
        String ua = webSettings.getUserAgentString();
        webSettings.setUserAgentString(ua);

//         支持缩放
        webSettings.setSupportZoom(true);
//        是否显示缩放按钮，默认false,若为false，则该WebView不可缩放
        webSettings.setBuiltInZoomControls(false);
//        隐藏原生的缩放控件
        webSettings.setDisplayZoomControls(false);

//        设置此属性，可任意比例缩放。大视图模式
        webSettings.setUseWideViewPort(true);
//        和setUseWideViewPort(true)一起解决网页自适应问题
        webSettings.setLoadWithOverviewMode(true);

//        设置js可以直接打开窗口，如window.open()，默认为false
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);


//         支持自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
//        解决图片不显示
        webSettings.setBlockNetworkImage(false);


        // 设置 WebView 的缓存模式
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

//        https://blog.csdn.net/a345017062/article/details/8703221

//        启用应用缓存
        webSettings.setAppCacheEnabled(true);
//         Android 私有缓存存储，如果你不调用setAppCachePath方法，WebView将不会产生这个目录
        webSettings.setAppCachePath(getCacheDir().getAbsolutePath());

//        启用或禁用DOM缓存
        webSettings.setDomStorageEnabled(true);
//        启用或禁用数据库缓存
        webSettings.setDatabaseEnabled(true);


        // 允许加载本地 html 文件/false
        webSettings.setAllowFileAccess(true);
        // 允许通过 file url 加载的 Javascript 读取其他的本地文件,Android 4.1 之前默认是true，在 Android 4.1 及以后默认是false,也就是禁止
        webSettings.setAllowFileAccessFromFileURLs(false);
        // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源，
        // Android 4.1 之前默认是true，在 Android 4.1 及以后默认是false,也就是禁止
        // 如果此设置是允许，则 setAllowFileAccessFromFileURLs 不起做用
        webSettings.setAllowUniversalAccessFromFileURLs(false);

        //判断是否联网
        if (NetworkUtils.isConnected()) {
            //默认的缓存使用模式
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            //不从网络加载数据，只从缓存加载数据。
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        }
//        自己写的 WebView 下是无法直接下载文件，需要自己监听下载事件并对下载的动作进行处理
        webView.setDownloadListener(new FileDownLoadListener());

//        Android5.0上 WebView中Http和Https混合问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            MIXED_CONTENT_ALWAYS_ALLOW：允许从任何来源加载内容，即使起源是不安全的
//            MIXED_CONTENT_NEVER_ALLOW：不允许Https加载Http的内容，即不允许从安全的起源去加载一个不安全的资源
//            MIXED_CONTENT_COMPATIBILITY_MODE：当涉及到混合式内容时，WebView 会尝试去兼容最新Web浏览器的风格
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

//        加载url，也可以执行js函数,当执行js时 参数为 "javascript: xxx.js"
//        webView.loadUrl("javascript:adduplistener()");
        if (url.isEmpty()) {
//            url = "http://www.baidu.com";
//            在android模拟器中,如果要访问宿主电脑上的服务,需要使用的ip是10.0.2.2
//            url = "http://10.0.2.2:8000";
            url = "http://hao.uc.cn";
        }
        Logger.d("首次Load地址：" + url);
        webView.loadUrl(url);
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//            }
//        }, 3000);
    }

    /**
     * 点击WebView的地址栏
     *
     * @param editText
     */
    @OnClick(R.id.current_show_url)
    public void click(EditText editText) {
        if (!isSelectAll) {
//            全选
            editText.selectAll();
        } else {
//            设置光标位置
            editText.setSelection(0);
        }
        isSelectAll = !isSelectAll;
    }

    /**
     * 点击返回按键
     */
    @Override
    public void onBackPressed() {
//        判断 WebView 当前是否可以返回上一页
        if (webView.canGoBack()) {
//          回退到前一页
            webView.goBack();
        } else {
//            需要注意的是super.onBackPressed()会自动调用finish()方法,所以应该放在else分之中进行
            super.onBackPressed();

        }

    }


    /**
     * 如何避免 WebView 的内存泄露问题
     * 可以将 Webview 的 Activity 新起一个进程，结束的时候直接System.exit(0);退出当前进程；
     * 不在xml中定义 WebView，而是在代码中创建，使用 getApplicationgContext() 作为传递的 Conetext
     * 在 Activity 销毁的时候，将 WebView 置空
     */
    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
//        取消注册广播
        this.unregisterReceiver(downloadReceiver);
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        菜单加载器
        MenuInflater menuInflater = getMenuInflater();
//        加载菜单文件(menu布局文件要在menu文件夹地下创建才行)
        menuInflater.inflate(R.menu.menus, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Menu中各个子菜单的事件监听
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_refresh:
//                刷新
                webView.reload();
                break;
            case R.id.option_mark:
//                保存成书签
                markUrl();
                break;
            case R.id.option_save:
//                保存成离线网页
                Logger.d("保存成离线网页");
                saveUrl();
                break;
            default:
                Logger.d("分享当前网页");
                ToastUtils.showLong("暂不支持分享");

        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 保存成书签弹窗
     */
    private void markUrl() {
//        构建bundle对象
        Bundle bundle = new Bundle();
        bundle.putString("url", currentShowUrlViewModel.getShowUrl().getValue());
        bundle.putString("title", currentPageTitle);

//        获取DialogFragment对象
        AddBookDialogFragment addBookDialogFragment = AddBookDialogFragment.getInstance(bundle);


//        官方推荐Fragment之间传递bundle使用setArguments()方法,但是在DialogFragment中传递参数需要用onCreateDialog()方法
//        试了几种方法都不行,要么是获取getActivity()为null,要么无法获取Bundle,最终找到解决方案就是,对于Fragment的传参,放到Fragment的静态方法中,然后需要用到的地方使用getArguments()去取
//        addBookDialogFragment.setArguments(bundle);
//        addBookDialogFragment.onCreateDialog(bundle);
//        addBookDialogFragment.onStart();
        addBookDialogFragment.show(getSupportFragmentManager(), "AddBookDialog");

    }


    /**
     * 保存成离线网页
     */
    private void saveUrl() {
        String name = currentPageTitle + ".mht";
        webView.saveWebArchive(Environment.getExternalStorageDirectory()
                + File.separator + name);
        ToastUtils.showLong("已保存网页到：" + Environment.getExternalStorageDirectory()
                + File.separator + name);
    }

    /**
     * 加载离线网页
     *
     * @param filePath
     */
    private void loadArchive(String filePath) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.loadUrl("file:///" + filePath);
        } else {
            webView.loadDataWithBaseURL(null, filePath, "application/x-webarchive-xml", "UTF-8", null);
        }
    }
}
