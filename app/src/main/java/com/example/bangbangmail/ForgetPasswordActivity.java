package com.example.bangbangmail;

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

import com.example.bangbangmail.Util.BaseAcctivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ForgetPasswordActivity extends BaseAcctivity implements NavigationView.OnClickListener {
    private static final String TAG = "ForgetPasswordActivity";
    private FindPwdTask mAuthTask = null;
    private EditText mailText;
    private EditText bindTelText;
    private View mProgressView;
    private View mFindPwdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("找回密码");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(this);
        //变单项
        mailText = (EditText) findViewById(R.id.mail);
        bindTelText = (EditText) findViewById(R.id.bind_phone);
        Button find_password = (Button)findViewById(R.id.find_password_button);
        find_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptForgetPwd();
            }
        });
        mProgressView = findViewById(R.id.findpwd_progress);
        mFindPwdView = findViewById(R.id.findpwd_form);
    }

    public void attemptForgetPwd()  {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mailText.setError(null);
        bindTelText.setError(null);

        // Store values at the time of the login attempt.
        String mail = mailText.getText().toString();
        String tel = bindTelText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid mail, if the user entered one.
        if (TextUtils.isEmpty(mail)) {
            mailText.setError("请输入邮箱");
            focusView = mailText;
            cancel = true;
        }
        if (TextUtils.isEmpty(tel)) {
            bindTelText.setError("请输入注册时绑定的手机号");
            focusView = bindTelText;
            cancel = true;
        }

        // Check for a valid
        if (!isEmailValid(mail)) {
            mailText.setError("输入正确的邮箱");
            focusView = mailText;
            cancel = true;
        }

        if (!isTelLenthLegal(tel)) {
            bindTelText.setError("输入正确的手机号");
            focusView = bindTelText;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = new FindPwdTask(mail, tel);
            mAuthTask.execute((Void) null);
        }
    }

    //验证手机号是否11位
    private boolean isTelLenthLegal(String tel) {
        //TODO: Replace this with your own logic
        return tel.length() == 11;
    }
    //是否是邮箱
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@")&&email.contains(".");
    }



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class FindPwdTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mTel;
        ProgressDialog verifytelProgress = new ProgressDialog(ForgetPasswordActivity.this);

        FindPwdTask(String mail, String tel) {
            mEmail = mail;
            mTel = tel;
        }

        @Override
        protected void onPreExecute() {
            verifytelProgress.setMessage("验证手机号中...");
            verifytelProgress.setCancelable(true);
            verifytelProgress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            boolean matchResult = false;
            try {
                // Simulate network access.
                matchResult = mailMatchTel(mEmail, mTel);
                Log.d(TAG, "doInBackground:matchResult: " + matchResult);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                matchResult = false;
            }
            return matchResult;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            verifytelProgress.dismiss();
            if (success) {
                Intent intent = new Intent(ForgetPasswordActivity.this,ResetPassword.class);
                intent.putExtra("mail",mEmail);
                startActivity(intent);
                finish();
            } else {
                bindTelText.setError("手机号与邮箱不匹配！");
                bindTelText.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    /*
    *   发送http请求验证用户名、密码
    *   @return boolean
    */
    private boolean mailMatchTel(String email, String tel){
        boolean signal = false;
        try{
            Log.d("mailMatchTel", "匹配绑定的手机号...");
            Log.d(TAG, "mailMatchTel: " + email + "-" + tel);
            URL url = new URL("http://120.77.168.57:9090/FishMail/UFindPassword");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            //传递数据
            String data = "mail=" + URLEncoder.encode(email,"UTF-8")
                    + "&phone=" + URLEncoder.encode(tel,"UTF-8");
            Log.d(TAG, "RegisterByPost: date:" + data);
            urlConnection.setRequestProperty("Connection","keep-alive");
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Content-Length",String.valueOf(data.getBytes().length));
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            //获取输出流
            OutputStream os =urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            //接收报文
            if(urlConnection.getResponseCode()==200){
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while((len=is.read(buffer)) != -1){
                    baos.write(buffer,0,len);
                }
                is.close();
                baos.close();
                final String res = new String(baos.toByteArray());
                if(res.equals("true")){
                    signal = true;
                }
                else {
                    signal = false;
                }
            } else {
                Log.d(TAG, "mailMatchTel: 状态码：" + urlConnection.getResponseCode());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.d(TAG, "mailMatchTel: signal:" + signal);
        return signal;

    }


    //返回按钮事件
    @Override
    public void onClick(View v) {
                ForgetPasswordActivity.this.finish();
    }
}
