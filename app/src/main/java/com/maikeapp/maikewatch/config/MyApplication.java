package com.maikeapp.maikewatch.config;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import com.gzgamut.sdk.model.Maike;

/**
 * Created by SunnyJiang on 2016/5/19.
 */
public class MyApplication extends Application{
    private static MyApplication myApplication;
    private static Maike device;

    /**
     * 获取maike对象的单例对象
     * @return
     */
    public static Maike newMaikeInstance(){
        if (device == null){
            device = new Maike(getAppContext());
            Log.d(CommonConstants.LOGCAT_TAG_NAME+"_new_device","new_device_maike");
        }
        return device;
    }


    /**
     * Access to current global Context of the application, pay attention to use
     * this method must be in the AndroidManifest. XML configuration
     * MyApplication
     *
     * @Title: getAppContext
     * @return Context
     */

    public static Context getAppContext() {
        if (null == myApplication) {
            throw new RuntimeException("Please AndroidManifest. XML configuration MyApplication");
        }
        return myApplication;
    }

    /**
     * Access to current global Application of the Application, pay attention to
     * use this method must be in the AndroidManifest. XML configuration
     * MyApplication
     *
     * @Title: getAppContext
     * @return Application
     */
    public static Application getApp() {
        if (null == myApplication) {
            throw new RuntimeException("Please AndroidManifest. XML configuration MyApplication");
        }
        return myApplication;
    }

    /**
     * Called when the application is starting, before any other application
     * objects have been created. Implementations should be as quick as possible
     * (for example using lazy initialization of state) since the time spent in
     * this function directly impacts the performance of starting the first
     * activity, service, or receiver in a process. If you override this method,
     * be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
//        //jpush
//        JPushInterface.setDebugMode(true);
//        JPushInterface.init(this);
    }

    /**
     * Called when the application is stopping. There are no more application
     * objects running and the process will exit. <em>Note: never depend on
     * this method being called; in many cases an unneeded application process
     * will simply be killed by the kernel without executing any application
     * code.</em> If you override this method, be sure to call
     * super.onTerminate().
     */
    @Override
    public void onTerminate() {

        super.onTerminate();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onLowMemory() {
        super.onLowMemory();
    }
}
