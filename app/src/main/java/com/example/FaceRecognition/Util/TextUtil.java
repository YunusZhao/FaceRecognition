package com.example.FaceRecognition.Util;

import java.util.regex.Pattern;

/**
 * Created by yun on 2017/9/14.
 */

public class TextUtil {
    /**
     * 用户名只能包含数字，英文，下划线
     * @param username
     * @return
     */
    public static boolean isUsernameValid(String username) {
        String reg = "\\w+([-+.]\\w+)*";
        // 创建 Pattern 对象
        Pattern p = Pattern.compile(reg);
        return p.matcher(username).matches();
    }

    //验证密码长度是否在 6 - 16位
    public static boolean isPwdLengthLegal(String pwd) {
        return (pwd.length() > 5) && (pwd.length() < 17);
    }

    /**
     * 密码只能包含数字，英文，下划线
     * @param password
     * @return
     */
    public static boolean isPwdValid(String password) {
        String reg = "\\w+([_]\\w+)*";
        // 创建 Pattern 对象
        Pattern p = Pattern.compile(reg);
        return p.matcher(password).matches();
    }
}
