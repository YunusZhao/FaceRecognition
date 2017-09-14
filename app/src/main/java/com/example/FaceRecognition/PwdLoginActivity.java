package com.example.FaceRecognition;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.FaceRecognition.Model.User;
import com.example.FaceRecognition.Util.BaseActivity;
import com.example.FaceRecognition.Util.NetUtil;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PwdLoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    private UserLoginTask mAuthTask = null;

    // UI 组件
    private EditText accountText;
    private EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd_login);

        // Set up the login form.
        accountText = (EditText) findViewById(R.id.acount);
        passwordText = (EditText) findViewById(R.id.password);

        Log.d(TAG, "onCreate: " + User.getAccount());
        accountText.setText(User.getAccount());

        accountText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                accountText.setFocusableInTouchMode(true);
                return false;
            }
        });

        //登录按钮事件
        Button loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User.setPassword(passwordText.getText().toString());
                mAuthTask = new UserLoginTask(User.getAccount(), User.getPassword());
                mAuthTask.execute();
            }
        });
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String account;
        private final String password;
        ProgressDialog loginProgress = new ProgressDialog(PwdLoginActivity.this);

        UserLoginTask(String account, String password) {
            this.account = account;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            loginProgress.setTitle("你好，" + account);
            loginProgress.setMessage("登录中...");
            loginProgress.setCancelable(true);
            loginProgress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean loginFlag = false;
            try {
                // Simulate network access.
                loginFlag = NetUtil.loginNetwork(account, password);
                Log.d(TAG, "doInBackground:login: " + loginFlag);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                loginFlag = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return loginFlag;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (success) {
                Toast.makeText(PwdLoginActivity.this, account + "，登陆成功！", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(PwdLoginActivity.this, PersonalActivity.class);
                startActivity(i);
                loginProgress.dismiss();
                finish();
            } else {
                loginProgress.dismiss();
                Toast.makeText(PwdLoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                Intent i = new Intent(PwdLoginActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            loginProgress.dismiss();
        }
    }
}

