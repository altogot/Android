package com.example.myapplication;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BaseApplication extends Application {

    //activity集合
    private static Map<String, Activity> destroyMap = new HashMap<>();

    //全局Context
    private static Context context;


    /**
     * 添加到销毁队列
     * @param activity
     * @param activityName
     */
    public static void addDestroyActivity(Activity activity, String activityName) {
        destroyMap.put(activityName, activity);
    }

    /**
     * 销毁指定的activity
     * @param activityName
     */
    public static void destroyActivity(String activityName) {
        Set<String> keySet = destroyMap.keySet();
        for(String key: keySet) {
            if(activityName.equals(key)) {
                destroyMap.get(key).finish();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getGlobalContext() {
        return context;
    }
}
