package cn.kanyun.webview_demo;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappyDB;

public class MyApplication extends Application {

    private static MyApplication instance;

    public static final String DB_NAME = "books";

    /**
     * 利用Application 来保存全局变量,供各个Activity使用
     */
    private String url;

    private static DB snappyDB;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static Application getInstance() {
        return instance;
    }

    /**
     * 其他需要使用snappyDB的地方,调用此方法
     *
     * @return
     */
    public static DB getDb() {
        return snappyDB;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//        初始化Logger
        Logger.addLogAdapter(new AndroidLogAdapter());

        try {
//            构建SnappyDB
            snappyDB = new SnappyDB.Builder(this)
//                    .directory(Environment.getExternalStorageDirectory().getAbsolutePath()) //可选的(当我设置这项的时候,在虚拟机上会报错,保持默认好了)
                    .name(DB_NAME)//可选的
                    .build();
//            打开SnappyDB
            snappyDB = DBFactory.open(this, DB_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
