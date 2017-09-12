package com.example.bangbangmail;

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

import com.example.bangbangmail.Util.BaseAcctivity;

public class LoginActivity extends BaseAcctivity {
    private static final String TAG = "LoginActivity";

    // UI 组件
    private EditText acountText;
    private View mLoginFormView;
    //记住密码
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox rememberAcount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        acountText = (EditText) findViewById(R.id.acount);
        final Button faceToLoginButton = (Button) findViewById(R.id.face_to_login_btn);
        final Button pwdToLoginButton = (Button)findViewById(R.id.pwd_to_login_btn);
        Button registerButton =(Button)findViewById(R.id.register_button);

        //记住密码功能
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        rememberAcount = (CheckBox) findViewById(R.id.remember_acount);
        boolean isRemember = pref.getBoolean("remember_acount",false);
        if (isRemember) {
            //将账号密码设置到文本框
            String acount = pref.getString("acount", "");
            acountText.setText(acount);
            rememberAcount.setChecked(true);
        }
//        //获取刚注册的账号
//        Intent intent = getIntent();
//        if (intent != null) {
//            String acount = intent.getStringExtra("ACOUNT");
//            Log.d(TAG, "onCreate:intent " + acount);
//            acountText.setText(acount);
//        }
        //给账户输入框加入监听事件
        acountText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(acountText.getText().toString())) {
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



        final String acount = acountText.getText().toString();
        Log.d(TAG, "onCreate: 账户是" + acount);
        if (TextUtils.isEmpty(acount)) {
            faceToLoginButton.setEnabled(false);
            pwdToLoginButton.setEnabled(false);
            faceToLoginButton.setTextColor(ContextCompat.getColor(LoginActivity.this,R.color.btn_enable));
            pwdToLoginButton.setTextColor(ContextCompat.getColor(LoginActivity.this,R.color.btn_enable));
        }

        //刷脸登录按钮事件
        faceToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String acount = acountText.getText().toString();
                Intent intent = new Intent(LoginActivity.this, FaceIdentify.class);
                intent.putExtra("FLAG", 1);
                intent.putExtra("ACOUNT", acount);
                startActivity(intent);
            }
        });
        //密码登录
        pwdToLoginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String acount = acountText.getText().toString();
                Intent intent = new Intent(LoginActivity.this,PwdLoginActivity.class);
                Log.d(TAG, "onClick: " + acount);
                intent.putExtra("ACOUNT", acount);
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

