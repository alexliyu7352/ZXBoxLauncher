package com.zx.zxboxlauncher;


import android.app.Activity;
import android.app.Application;

import com.open.androidtvwidget.utils.OPENLOG;
import com.zx.zxboxlauncher.activity.BaseActivity;

import java.util.ArrayList;


/**
 * 功能描述：用于存放全局变量和公用的资源等
 *
 * @author wxt
 */
public class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";
    private static BaseApplication instance;

    /**
     * Activity集合
     */
    private static ArrayList<BaseActivity> activitys = new ArrayList<BaseActivity>();

    @Override
    public void onCreate() {
        super.onCreate();

        OPENLOG.initTag("ZX", true);
    }

    /**
     * 添加Activity到ArrayList<Activity>管理集合
     *
     * @param activity
     */
    public void addActivity(BaseActivity activity) {
        String className = activity.getClass().getName();
        for (Activity at : activitys) {
            if (className.equals(at.getClass().getName())) {
                activitys.remove(at);
                break;
            }
        }
        activitys.add(activity);
    }

    /**
     * 退出应用程序的时候，手动调用
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        for (BaseActivity activity : activitys) {
            activity.defaultFinish();
        }
    }

    public static BaseApplication getInstance() {
        if (null == instance) {
            instance = new BaseApplication();
        }
        return instance;
    }
}
