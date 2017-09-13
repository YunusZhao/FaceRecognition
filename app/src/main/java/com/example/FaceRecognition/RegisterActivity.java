package com.example.FaceRecognition;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.FaceRecognition.Model.User;
import com.example.FaceRecognition.Util.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Pattern;

public class RegisterActivity extends BaseActivity {
    private static final String TAG = "RegisterActivity";
    // UI 组件
    private EditText usernameText;
    private EditText mPasswordView;
    private EditText rePasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("注册");
        setSupportActionBar(toolbar);
        //表单项
        usernameText = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.reg_pwd);
        rePasswordText = (EditText) findViewById(R.id.re_pwd);
        //注册按钮事件
        Button nextBtn = (Button) findViewById(R.id.next_step);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
                Intent intent = new Intent(RegisterActivity.this, FaceIdentify.class);
                User.setAccount(usernameText.getText().toString());
                User.setPassword(mPasswordView.getText().toString());
                intent.putExtra("FLAG", 0);
                startActivity(intent);
            }
        });
    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        // Reset errors.
        usernameText.setError(null);
        mPasswordView.setError(null);
        rePasswordText.setError(null);

        // Store values at the time of the login attempt.
        String username = usernameText.getText().toString();
        String password = mPasswordView.getText().toString();
        String rePassword = rePasswordText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(username)) {
            usernameText.setError("用户名不能为空");
            focusView = usernameText;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("密码不能为空");
            focusView = mPasswordView;
            cancel = true;
        }
        if (!rePassword.equals(password)) {
            rePasswordText.setError("前后密码不一致");
            focusView = rePasswordText;
            cancel = true;
        }

        // Check for a valid
        if (!TextUtils.isEmpty(username)&&!isUsernameValid(username)) {
            usernameText.setError("用户名只能由数字字母下划线组成");
            focusView = usernameText;
            cancel = true;
        }

        if (!TextUtils.isEmpty(password)&&!isPwdValid(password)) {
            mPasswordView.setError("密码只能由数字字母下划线组成");
            focusView = mPasswordView;
            cancel = true;
        }

        if (!isPwdLengthLegal(password)) {
            mPasswordView.setError("密码长度应为 6-16 位");
            focusView = mPasswordView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            showProgress(true);
//            mAuthTask = new RegisterTask(username, tel, password);
//            mAuthTask.execute((Void) null);
        }
    }

    /**
     * 用户名只能包含数字，英文，下划线
     * @param username
     * @return
     */
    private boolean isUsernameValid(String username) {
        String reg = "\\w+([-+.]\\w+)*";
        // 创建 Pattern 对象
        Pattern p = Pattern.compile(reg);
        return p.matcher(username).matches();
    }

    //验证密码长度是否在 6 - 16位
    private boolean isPwdLengthLegal(String pwd) {
        return (pwd.length() > 5) && (pwd.length() < 17);
    }

    /**
     * 密码只能包含数字，英文，下划线
     * @param password
     * @return
     */
    private boolean isPwdValid(String password) {
        String reg = "\\w+([_]\\w+)*";
        // 创建 Pattern 对象
        Pattern p = Pattern.compile(reg);
        return p.matcher(password).matches();
    }
}
