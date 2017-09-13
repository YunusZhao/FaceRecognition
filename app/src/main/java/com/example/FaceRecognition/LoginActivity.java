package com.example.FaceRecognition;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.FaceRecognition.Model.User;
import com.example.FaceRecognition.Util.BaseActivity;


public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    // UI 组件
    private EditText accountText;
    //记住密码
    private SharedPreferences pref;
    private CheckBox rememberAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        accountText = (EditText) findViewById(R.id.acount);
        final Button faceToLoginButton = (Button) findViewById(R.id.face_to_login_btn);
        final Button pwdToLoginButton = (Button)findViewById(R.id.pwd_to_login_btn);
        Button registerButton =(Button)findViewById(R.id.register_button);

        //记住密码功能
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        rememberAccount = (CheckBox) findViewById(R.id.remember_acount);
        boolean isRemember = pref.getBoolean("remember_account", false);
        if (isRemember) {
            //将账号密码设置到文本框
            String account = pref.getString("account", "");
            accountText.setText(account);
            rememberAccount.setChecked(true);
        }
        //获取刚注册的账号
        Intent intent = getIntent();
        if (intent != null) {
            Log.d(TAG, "onCreate:intent " + User.getAccount());
            accountText.setText(User.getAccount());
        }
        //给账户输入框加入监听事件
        accountText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(accountText.getText().toString())) {
                    faceToLoginButton.setEnabled(false);
                    pwdToLoginButton.setEnabled(false);
                    faceToLoginButton.setTextColor(ContextCompat.getColor(LoginActivity.this,R.color.btn_enable));
                    pwdToLoginButton.setTextColor(ContextCompat.getColor(LoginActivity.this,R.color.btn_enable));
                } else {
                    faceToLoginButton.setEnabled(true);
                    pwdToLoginButton.setEnabled(true);
                    faceToLoginButton.setTextColor(ContextCompat.getColor(LoginActivity.this,R.color.textcolor));
                    pwdToLoginButton.setTextColor(ContextCompat.getColor(LoginActivity.this,R.color.lightblue));
                }
            }
        });



        User.setAccount(accountText.getText().toString());
        Log.d(TAG, "onCreate: 账户是" + User.getAccount());
        if (TextUtils.isEmpty(User.getAccount())) {
            faceToLoginButton.setEnabled(false);
            pwdToLoginButton.setEnabled(false);
            faceToLoginButton.setTextColor(ContextCompat.getColor(LoginActivity.this,R.color.btn_enable));
            pwdToLoginButton.setTextColor(ContextCompat.getColor(LoginActivity.this,R.color.btn_enable));
        }

        //刷脸登录按钮事件
        faceToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.setAccount(accountText.getText().toString());
                Intent intent = new Intent(LoginActivity.this, FaceIdentify.class);
                intent.putExtra("FLAG", 1);
                startActivity(intent);
            }
        });
        //密码登录
        pwdToLoginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                User.setAccount(accountText.getText().toString());
                Intent intent = new Intent(LoginActivity.this,PwdLoginActivity.class);
                Log.d(TAG, "onClick: " + User.getAccount());
                startActivity(intent);
            }
        });

        //注册
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}

