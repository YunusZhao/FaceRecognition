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
import android.widget.Toast;

import com.example.bangbangmail.Util.BaseAcctivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Pattern;

public class ResetPassword extends BaseAcctivity implements NavigationView.OnClickListener{
    private static final String TAG = "ResetPassword";
    private ResetPwdTask mAuthTask = null;
    private EditText newPwdText;
    private EditText confirmPwdText;
    private View mProgressView;
    private View mRestPwdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("重置密码");
        setSupportActionBar(toolbar);
        newPwdText = (EditText) findViewById(R.id.new_password);
        confirmPwdText = (EditText) findViewById(R.id.affirm_password);
        mProgressView = findViewById(R.id.resetpwd_progress);
        mRestPwdView = findViewById(R.id.resetpwd_form);
        Button resetPwdBtn = (Button) findViewById(R.id.reset_password_button);
        resetPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptResetPwd();
            }
        });
    }


    public void attemptResetPwd()  {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        newPwdText.setError(null);
        confirmPwdText.setError(null);

        // Store values at the time of the login attempt.
        String newPassword = newPwdText.getText().toString();
        String rePassword = confirmPwdText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid mail, if the user entered one.
        if (TextUtils.isEmpty(newPassword)) {
            newPwdText.setError("设置新密码");
            focusView = newPwdText;
            cancel = true;
        }
        if (TextUtils.isEmpty(rePassword)) {
            confirmPwdText.setError("请再次输入密码");
            focusView = confirmPwdText;
            cancel = true;
        }

        if (!rePassword.equals(newPassword)) {
            confirmPwdText.setError("前后密码不一致");
            focusView = confirmPwdText;
            cancel = true;
        }

        // Check for a valid
        if (!TextUtils.isEmpty(newPassword)&&!isPwdValid(newPassword)) {
            newPwdText.setError("密码只能由数字字母下划线组成");
            focusView = newPwdText;
            cancel = true;
        }

        if (!isPwdLenthLegal(newPassword)) {
            newPwdText.setError("密码长度应为 6-16 位");
            focusView = newPwdText;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            String mail = getIntent().getStringExtra("mail");
            Log.d(TAG, "attemptResetPwd: mail" + mail);
            mAuthTask = new ResetPwdTask(mail, newPassword);
            mAuthTask.execute((Void) null);
        }
    }

    //验证密码长度是否在 6 - 16位
    private boolean isPwdLenthLegal(String pwd) {
        //TODO: Replace this with your own logic
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

    @Override
    public void onClick(View v) {
        ResetPassword.this.finish();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class ResetPwdTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        ProgressDialog resetPwdProgress = new ProgressDialog(ResetPassword.this);

        ResetPwdTask(String mail, String password) {
            mEmail = mail;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            resetPwdProgress.setMessage("正在重置密码...");
            resetPwdProgress.setCancelable(true);
            resetPwdProgress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            boolean matchResult = false;
            try {
                // Simulate network access.
                matchResult = resetPassword(mEmail, mPassword);
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
            resetPwdProgress.dismiss();
            if (success) {
                Intent intent = new Intent(ResetPassword.this,LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(ResetPassword.this, "重置密码失败", Toast.LENGTH_SHORT).show();
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
    private boolean resetPassword(String email, String pwd){
        boolean signal = false;
        try{
            Log.d("resetPassword", "重置密码...");
            Log.d(TAG, "mailMatchTel: " + email + "-" + pwd);
            URL url = new URL("http://120.77.168.57:9090/FishMail/UFindAndChangeServlet");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            //传递数据
            String data = "mail=" + URLEncoder.encode(email,"UTF-8")
                    + "&password=" + URLEncoder.encode(pwd,"UTF-8");
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
                Log.d(TAG, "resetPassword: 状态码：" + urlConnection.getResponseCode());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.d(TAG, "resetPassword: signal:" + signal);
        return signal;

    }



}
