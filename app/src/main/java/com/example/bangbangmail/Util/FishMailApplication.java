package com.example.bangbangmail.Util;

import android.app.Application;
import android.content.Context;

/**
 * Created by Kenshin on 2017/5/29.
 */

public class FishMailApplication extends Application{
    private static Context mContext;
    private static String mMail;
    private static String mPwd;

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        super.onCreate();
    }

    public static void setMail(String mail) {
        mMail = mail;
    }

    public static void setPwd(String pwd) {
        mPwd = pwd;
    }

    public static String getMail() {
        return mMail;
    }

    public static String getPwd() {
        return mPwd;
    }

    public static Context getmContext() {
        return mContext;
    }
}
