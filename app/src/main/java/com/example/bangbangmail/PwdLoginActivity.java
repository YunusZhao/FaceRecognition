package com.example.bangbangmail;

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

import com.example.bangbangmail.Util.BaseAcctivity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
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

public class PwdLoginActivity extends BaseAcctivity {
    private static final String TAG = "LoginActivity";
    private UserLoginTask mAuthTask = null;

    // UI 组件
    private EditText acountText;
    private EditText passwordText;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd_login);

        // Set up the login form.
        acountText = (EditText) findViewById(R.id.acount);
        passwordText = (EditText) findViewById(R.id.password);

        Intent intent = getIntent();
        final String acount = intent.getStringExtra("ACOUNT");
        Log.d(TAG, "onCreate: " + acount);
        acountText.setText(acount);

        acountText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                acountText.setFocusableInTouchMode(true);
                return false;
            }
        });

        //登录按钮事件
        Button loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pwd = passwordText.getText().toString();
                try {
                    if (loginNetwork(acount, pwd)) {
                        Toast.makeText(PwdLoginActivity.this, acount + "，登陆成功！", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(PwdLoginActivity.this, PersonalActivity.class);
                        i.putExtra("ACOUNT", acount);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(PwdLoginActivity.this, acount + "，登陆失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (FileNotFoundException e) {
                    Toast.makeText(PwdLoginActivity.this, acount + "，登陆失败！文件未找到", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                Intent i = new Intent(PwdLoginActivity.this, LoginActivity.class);
                startActivity(i);
                finish();


//                Intent intent = new Intent(PwdLoginActivity.this, PersonalActivity.class);
//                intent.putExtra("ACOUNT", acount);
//                startActivity(intent);
//                attemptLogin();
            }
        });
//       Button forget_password_button = (Button)findViewById(R.id.forget_password);
//                forget_password_button.setOnClickListener(new OnClickListener(){
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(LoginActivity.this,ForgetPasswordActivity.class);
//                        startActivity(intent);
//                    }
//                });
//                Button register_button =(Button)findViewById(R.id.register_button);
//                register_button.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
//                        startActivity(intent);
//            }
//        });
    }

    public static boolean loginNetwork(String usr,String pwd) throws FileNotFoundException {
        // String filepath = "C:\\Users\\lenovo idea\\Desktop\\liuwenwu.JPG";
        String urlStr = "http://59.110.235.173:8080/app/lodinByPwd ";
        Map<String, String> textMap = new HashMap<>();
        textMap.put("name", usr);
        textMap.put("password", pwd);
        String ret = formUpload(urlStr, textMap, null);
        System.out.println(ret);
        if(ret.equals("success"))
            return true;
        else
            return false;
    }

    public static String formUpload(String urlStr, Map<String, String> textMap, Map<String, InputStream> fileMap) {
        String res = "";
        HttpURLConnection conn = null;
        String BOUNDARY = "---------------------------123821742118716";
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            //
            if (textMap != null) {
                StringBuffer strBuf = new StringBuffer();
                Iterator<Map.Entry<String, String>> iter = textMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, String> entry = iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
                    strBuf.append(inputValue);
                }
                out.write(strBuf.toString().getBytes());
                System.out.println(strBuf);
            }
            // file

            if (fileMap != null) {
                Iterator<Map.Entry<String, InputStream>> iter = fileMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, InputStream> entry = iter.next();
                    String inputName = (String) entry.getKey();
                    FileInputStream inputValue =   (FileInputStream) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    String filename = System.currentTimeMillis()+".jpg";
                    String contentType = "image/png";
                    StringBuffer strBuf = new StringBuffer();
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename + "\"\r\n");
                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
                    out.write(strBuf.toString().getBytes());
                    DataInputStream in = new DataInputStream(inputValue);
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }
                    in.close();
                }
            }
            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();
            // 接收数据
            StringBuffer strBuf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuf.append(line);
            }
            res = strBuf.toString();
            reader.close();
            reader = null;
        } catch (Exception e) {
            System.out.println("error" + urlStr);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
        return res;
    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
     /*
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("请输入密码");
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("请输入邮箱");
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError("输入正确的邮箱");
            focusView = mEmailView;
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
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    } */
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@")&&email.contains(".");
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        ProgressDialog loginProgress = new ProgressDialog(PwdLoginActivity.this);

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
//            loginProgress.setTitle("你好，" + mEmail);
            loginProgress.setMessage("登录中...");
            loginProgress.setCancelable(true);
            loginProgress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            boolean loginres = false;
            try {
                // Simulate network access.
                loginres = loginByPost(mEmail, mPassword);
                Log.d(TAG, "doInBackground:loginres: " + loginres);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                loginres = false;
            }
            return loginres;
        }

        /*
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
//            showProgress(false);
            if (success) {
                editor = pref.edit();
                if (rememberPwd.isChecked()) {
                    editor.putBoolean("remember_password", true);
                    editor.putString("mail", mEmail);
                    editor.putString("password", mPassword);
                } else {
                    editor.clear();
                }
                editor.apply();
                Log.d(TAG, "当前用户: " + mEmail);
                FishMailApplication.setMail(mEmail);
                FishMailApplication.setPwd(mPassword);
                Log.d(TAG, "当前用户: " + FishMailApplication.getMail());
                Intent intent = new Intent(PwdLoginActivity.this,PwdLoginActivity.class);
                startActivity(intent);
                finish();
                loginProgress.dismiss();
            } else {
                loginProgress.dismiss();
                Toast.makeText(PwdLoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            loginProgress.dismiss();
//            showProgress(false);
        }
    }
    */

        /*
        *   发送http请求验证用户名、密码
        *   @return boolean
        */
        private boolean loginByPost(String mail, String passwd) {
            boolean signal = false;
            try {
                Log.d("loginByPost", "try to login");
                Log.d(TAG, "loginByPost: " + mail + "-" + passwd);
                URL url = new URL("http://120.77.168.57:9090/FishMail/UserLoginServlet");
//            URL url = new URL("http://101.201.69.120:8080/HealthGuardian/Validate.do");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setReadTimeout(5000);
                urlConnection.setConnectTimeout(5000);
                //传递数据
                String data = "mail=" + URLEncoder.encode(mail, "UTF-8")
                        + "&password=" + URLEncoder.encode(passwd, "UTF-8");
                Log.d(TAG, "loginByPost: date:" + data);
                urlConnection.setRequestProperty("Connection", "keep-alive");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                //获取输出流
                OutputStream os = urlConnection.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                //接收报文
                if (urlConnection.getResponseCode() == 200) {
                    InputStream is = urlConnection.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int len = 0;
                    byte buffer[] = new byte[1024];
                    while ((len = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, len);
                    }
                    is.close();
                    baos.close();
                    final String res = new String(baos.toByteArray());
                    if (res.equals("true")) {
                        signal = true;
                    } else {
                        signal = false;
                    }
                } else {
                    Log.d(TAG, "loginByPost: 状态码：" + urlConnection.getResponseCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "loginByPost: signal:" + signal);
            return signal;
        }
    }
}

