package com.example.FaceRecognition.Util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 该类用于管理活动，保证随时随地退出程序
 * Created by Kenshin on 2017/5/31.
 */

public class ActivityController {
    public static List<Activity> activityList = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activityList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
