package com.example.FaceRecognition.Model;

/**
 * Created by Kenshin on 2017/5/20.
 */

public class User {

    public static final String Path = "///sdcard/photo.jpg";

    private static String account;
    private static String password;

    public static void setAccount(String account) {
        User.account = account;
    }
    public static void setPassword(String password) {
        User.password = password;
    }
    public static String getAccount() {
        if (account == null) {
            return "";
        }
        return account;
    }
    public static String getPassword() {
        if (password == null) {
            return "";
        }
        return password;
    }
}
