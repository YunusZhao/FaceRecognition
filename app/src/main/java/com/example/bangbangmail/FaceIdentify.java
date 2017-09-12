package com.example.bangbangmail;

import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FaceIdentify extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "FaceIdentify";
    private CircleSufaceView mCircleSufaceView;
    //标志位，0代表注册，1代表登录
    private int flag;
    private int picFlag;
    private String acount;      //账户
    private String password;    //密码
    private ImageButton takePicBtn;
    private SurfaceHolder surfaceHolder;
    private Camera mCamera;
    private String path = "///sdcard/photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_identify);
        mCircleSufaceView = (CircleSufaceView) findViewById(R.id.sufaceview);
        takePicBtn = (ImageButton) findViewById(R.id.take_pic_btn);
        takePicBtn.setOnClickListener(this);
        Intent intent = getIntent();
        flag = intent.getIntExtra("FLAG", 1);
        Log.d(TAG, "onCreate: Falg = " + flag);
        
        if (flag == 0) { //注册
            acount = intent.getStringExtra("ACOUNT");
            password = intent.getStringExtra("PASSWORD");
        } else {  //登录
            acount = intent.getStringExtra("ACOUNT");
        }
        surfaceHolder = mCircleSufaceView.getHolder();
        initListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_pic_btn:
                picFlag = 0;

                // 获取当前相机参数
                Camera.Parameters parameters = mCamera.getParameters();
                // 设置相片格式
                parameters.setPictureFormat(ImageFormat.JPEG);
                // 设置预览大小
                parameters.setPreviewSize(800, 480);
//                // 设置对焦方式，这里设置自动对焦
//                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//                mCamera.autoFocus(new Camera.AutoFocusCallback() {
//
//                    @Override
//                    public void onAutoFocus(boolean success, Camera camera) {
//                        // 判断是否对焦成功
//                        if (success) {
//                            // 拍照 第三个参数为拍照回调
//                            mCamera.takePicture(null, null, pc);
//                        }
//                    }
//                });
//
                Log.i(TAG, "BBBBBBBBBBBBBBBBBBBBBB");
                mCamera.takePicture(null, null, pc);

                SystemClock.sleep(2000);
                final Handler mHandler=new Handler();

                new Thread(new Runnable() {
                    int myFlag;
                    @Override
                    public void run() {
                        if (flag == 0) { //注册
                            try {
                                boolean reFlag = registerNetwork(acount, password, path);
                                if (reFlag) {
                                    System.out.println("AAAA" + reFlag);
                                    myFlag = 1;
                                } else {
                                    myFlag = 2;
                                }
                            } catch (FileNotFoundException e) {
                                myFlag = 3;
                                e.printStackTrace();
                            }
                        } else { //登录
                            try {
                                if (loginNetwork(acount, path)) {
                                    myFlag = 4;
                                } else {
                                    myFlag = 5;

                                }
                            } catch (FileNotFoundException e) {
                                myFlag = 6;
                                e.printStackTrace();
                            }
                        }

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    switch (myFlag) {
                                        case 1 :
                                            Toast.makeText(FaceIdentify.this, acount + "，注册成功！", Toast.LENGTH_SHORT).show();
                                            break;
                                        case 2 :
                                            Toast.makeText(FaceIdentify.this, acount + "，注册失败！", Toast.LENGTH_SHORT).show();
                                            break;
                                        case 3 :
                                            Toast.makeText(FaceIdentify.this, acount + "，注册失败！文件未找到", Toast.LENGTH_SHORT).show();
                                            break;
                                        case 4 :
                                            Toast.makeText(FaceIdentify.this, acount + "，登陆成功！", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(FaceIdentify.this, PersonalActivity.class);
                                            i.putExtra("ACOUNT", acount);
                                            startActivity(i);
                                            finish();
                                            break;
                                        case 5 :
                                            Toast.makeText(FaceIdentify.this, acount + "，登陆失败！", Toast.LENGTH_SHORT).show();
                                            break;
                                        case 6 :
                                            Toast.makeText(FaceIdentify.this, acount + "，登陆失败！文件未找到", Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                }
                            });
                            Intent i = new Intent(FaceIdentify.this, LoginActivity.class);
                            i.putExtra("ACOUNT", acount);
                            startActivity(i);
                            finish();

                    }
                }).start();


                break;
        }
    }

    public static boolean registerNetwork(String usr,String pwd,String filepath) throws FileNotFoundException{
        // String filepath = "C:\\Users\\lenovo idea\\Desktop\\liuwenwu.JPG";
        String urlStr = "http://59.110.235.173:8080/app/register ";
        Map<String, String> textMap = new HashMap<>();
        textMap.put("name", usr);
        textMap.put("password", pwd);
        Map<String, InputStream> fileMap = new HashMap<>();
        fileMap.put("userfile", new FileInputStream(new File(filepath)) );
        String ret = formUpload(urlStr, textMap, fileMap);
        System.out.println(ret);
        if(ret.equals("success"))
            return true;
        else
            return false;
    }

    public static boolean loginNetwork(String usr,String filepath) throws FileNotFoundException{
        // String filepath = "C:\\Users\\lenovo idea\\Desktop\\liuwenwu.JPG";
        String urlStr = "http://59.110.235.173:8080/app/lodinByPic ";
        Map<String, String> textMap = new HashMap<>();
        textMap.put("name", usr);
        Map<String, InputStream> fileMap = new HashMap<>();
        fileMap.put("userfile", new FileInputStream(new File(filepath)) );
        String ret = formUpload(urlStr, textMap, fileMap);
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



    private Camera.PictureCallback pc = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // data为完整数据
            File file = new File(path);
            // 使用流进行读写
            try {
                FileOutputStream fos = new FileOutputStream(file);
                try {
                    fos.write(data);
                    picFlag = 1;
                    Log.i(TAG, "AAAAAAAAAAAAAAAAAAAAAAAAA " + data);
                    // 关闭流
                    fos.close();
                    if (mCamera != null) {
                        mCamera.release();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
    };

    private void initListener() {
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.setFixedSize(300,400);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d(TAG, "surfaceCreated: ");
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                try {
                    mCamera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                android.hardware.Camera.Size bestSize = null;
                Camera.Parameters parameters = mCamera.getParameters();
                List<Camera.Size> sizeList = mCamera.getParameters().getSupportedPreviewSizes();
                bestSize = sizeList.get(0);

                for(int i = 1; i < sizeList.size(); i++){
                    if((sizeList.get(i).width * sizeList.get(i).height) >
                            (bestSize.width * bestSize.height)){
                        bestSize = sizeList.get(i);//获取最佳尺寸
                    }
                }
                parameters.setPreviewSize(bestSize.width, bestSize.height);
                mCamera.setParameters(parameters);
                mCamera.setDisplayOrientation(90);//解决图像旋转90度问题
                mCamera.setParameters(parameters);
                mCamera.startPreview();

                mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        Log.i(TAG, "onPreviewFrame: " + data);
                        Log.i(TAG, "onPreviewFrame: " + camera);
//                        new TakePictureCallback().onPictureTaken(date, camera);
                    }
                });
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d(TAG, "surfaceChanged: ");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d(TAG, "surfaceDestroyed: ");

            }
        });
    }
}
