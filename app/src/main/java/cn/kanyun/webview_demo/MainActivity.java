package cn.kanyun.webview_demo;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.orhanobut.logger.Logger;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.sxu.shadowdrawable.ShadowDrawable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.kanyun.webview_demo.databinding.ActivityMainBinding;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import scut.carson_ho.searchview.ICallBack;
import scut.carson_ho.searchview.bCallBack;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_STORAGE_CODE = 0;
    Context context;
    scut.carson_ho.searchview.SearchView searchView;

    @BindView(R.id.current_url)
    TextView current_url;

    @BindView(R.id.main_tool_bar)
    androidx.appcompat.widget.Toolbar toolbar;


    RecyclerView bookRecyclerView;

    RecyclerView.Adapter adapter;

    UrlViewModel urlViewModel;

    SlidingMenu menu;

    //    这是AndroidStudio自动生成的(当在layout.xml中点击了Convert to data binding layout后
    //    会自动生成该类,这个按钮需要使用Android Studio在xml中找到根布局,然后鼠标光标停留在根布局上,
    //    会出现小灯泡,然后点击小灯泡图标,就可以将布局更改为databinding布局)
    ActivityMainBinding binding;

    /**
     * 权限
     */
    private static final String[] perms = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};

    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

//        try {
//            DBFactory.open(context, MyApplication.DB_NAME);
//        } catch (SnappydbException e) {
//            e.printStackTrace();
//        }
//        初始化ViewModel
        urlViewModel = ViewModelProviders.of(this).get(UrlViewModel.class);

//        设置了dataBinding后就不用这个setContentView了
//        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//        binding.setUrlViewModel()方法是在xml中配置的变量的名称,相当于给xml中的变量进行赋值
        binding.setUrlViewModel(urlViewModel);
        binding.setLifecycleOwner(this);


//        当值改变时,做哪些事情(当在xml中配置了data后,就不需要在在这里设置了)
//        urlViewModel.getUrl().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(String s) {
//                current_url.setText(s);
//            }
//        });


        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitleMargin(0, 1, 0, 1);
//        设置Toolbar图标和点击事件
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.toggle();
            }
        });

//         配置 SlidingMenu
        menu = new SlidingMenu(this);
//        设定模式，SlidingMenu在左边
        menu.setMode(SlidingMenu.LEFT);
//         设置触摸屏幕的模式（全屏都可以拖拽触摸）
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);


//        设置阴影的图片资源
        menu.setShadowDrawable(R.drawable.shadow);
//        设置阴影的宽度
        menu.setShadowWidthRes(R.dimen.shadow_width);

//         设置预留屏幕宽度
        menu.setBehindOffsetRes(R.dimen.sliding_menu_offset);
//        设置SlidingMenu菜单的宽度
        menu.setBehindWidth(500);

//         设置渐入渐出效果的值
        menu.setFadeDegree(0.35f);
//        附加到当前的Activity上去
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        //为侧滑菜单设置布局
        menu.setMenu(R.layout.left_menu_layout);


//        监听slidingmenu打开时，【这个方法对右边的slidingmenu是无效的】
        menu.setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                Logger.d("SlidingMenu打开了");
            }
        });

//        监听slidingmenu完全打开
        menu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                Logger.d("SlidingMenu完全打开了");
            }
        });

//        监听slidingmenu关闭时事件
        menu.setOnCloseListener(new SlidingMenu.OnCloseListener() {
            @Override
            public void onClose() {
                Logger.d("SlidingMenu关闭了");
            }
        });

    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view = super.onCreateView(parent, name, context, attrs);

        return view;
    }

    @Override
    protected void onStart() {
        super.onStart();
        bookRecyclerView = findViewById(R.id.book_recycler_view);
        adapter = new BookAdapter();
        // 一行代码搞定（默认为渐显效果）
        ((BookAdapter) adapter).openLoadAnimation();
        bookRecyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(context);
        bookRecyclerView.setLayoutManager(layoutManager);
        bookRecyclerView.setItemAnimator(new DefaultItemAnimator());
        bookRecyclerView.setNestedScrollingEnabled(false);

//        长按书签弹出删除对话框
        ((BookAdapter) adapter).setOnItemLongClickListener(new BookLongClickListener(context));

//        点击书签,开启浏览
        ((BookAdapter) adapter).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Book book = (Book) adapter.getItem(position);
                urlViewModel.modifyUrl(book.getUrl());
                open();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EasyPermissions.requestPermissions(this, "hellp",
                PERMISSION_STORAGE_CODE, perms);
    }

    @OnClick(R.id.search_layout)
    public void input() {
        Logger.d("跳转到还有历史记录的搜索布局");
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, 0, 0, 0);
        View view = View.inflate(context, R.layout.search_view_layout, null);
//        setContentView会替换掉之前添加的layout布局,而addContentView可以不继续在同一个activity里面添加多个不同的layout布局，不会出现替换的情况
        addContentView(view, layoutParams);
//        setContentView(R.layout.search_view_layout);
        searchView = findViewById(R.id.search_view);
//        设置点击键盘上的搜索按键后的操作（通过回调接口）
        searchView.setOnClickSearch(new ICallBack() {
            //            参数 = 搜索框输入的内容
            @Override
            public void SearchAciton(String string) {
                Logger.d("SearchView 接收到参数：" + string);
                urlViewModel.modifyUrl(string);
                ((ViewGroup) view.getParent()).removeView(view);
            }

        });


//        设置点击返回按键后的操作（通过回调接口）
        searchView.setOnClickBack(new bCallBack() {
            @Override
            public void BackAciton() {
//                addContentView视图后移除该视图
                ((ViewGroup) view.getParent()).removeView(view);
            }
        });
    }

    /**
     * 跳转到WebViewActivity
     */
    @OnClick(R.id.open)
    public void open() {
//        跳转Activity时,将URL的值放到Appcation中,用作全局变量
        MyApplication application = (MyApplication) MyApplication.getInstance();
        application.setUrl(urlViewModel.getUrl().getValue());

        Intent intent = new Intent();
        intent.setClass(context, WebViewActivity.class);
        startActivity(intent);
    }

    @AfterPermissionGranted(PERMISSION_STORAGE_CODE)
    public void perResult() {
        if (EasyPermissions.hasPermissions(context, perms)) {
            // 已经赋予了权限
            Logger.d("被@AfterPermissionGranted注解的方法被执行,且已经获取到权限");
        } else {
            Logger.d("被@AfterPermissionGranted注解的方法被执行,但是没有获取到权限");
            // 没有赋予权限，此时请求权限
//            第一个参数：Context对象  第二个参数：权限弹窗上的文字提示语。告诉用户，这个权限用途。 第三个参数：这次请求权限的唯一标示，code。 第四个参数 : 一些系列的权限
            EasyPermissions.requestPermissions(this, "请确认允许权限,这将打开您的相机为您制作个性皮肤",
                    PERMISSION_STORAGE_CODE, perms);
        }
    }
}
