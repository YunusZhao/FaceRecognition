package com.example.FaceRecognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.example.FaceRecognition.Util.CompressJPG;
import com.example.FaceRecognition.Util.Rotate;

import java.io.BufferedOutputStream;
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
    private CircleSurfaceView mCircleSufaceView;
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
        mCircleSufaceView = (CircleSurfaceView) findViewById(R.id.sufaceview);
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
//            // data为完整数据
//            File file = new File(path);
//            // 使用流进行读写
//            try {
//                FileOutputStream fos = new FileOutputStream(file);
//                try {
//                    fos.write(data);
//                    picFlag = 1;
//                    // 关闭流
//                    fos.close();
//                    if (mCamera != null) {
//                        mCamera.release();
//
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }catch (FileNotFoundException e){
//                e.printStackTrace();
//            }


            Bitmap bMap;
            // 使用流进行读写
            try {
                bMap = BitmapFactory.decodeByteArray(data, 0, data.length);
                try {
                    Bitmap bMapRotate;
                    bMapRotate = Rotate.rotatePicture(path, bMap);
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
                    bMap = bMapRotate;
                    bMap.compress(Bitmap.CompressFormat.JPEG, 50, bos);//将图片压缩到流中
                    bos.write(data);

//                    try {
//                        CompressJPG.Compress(path);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }


                    picFlag = 1;
                    // 关闭流
                    bos.close();
                    if (mCamera != null) {
                        mCamera.release();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }


//            Bitmap bMap;
//            try
//            {// 获得图片
//
//
//                bMap = BitmapFactory.decodeByteArray(data, 0, data.length);
//
//                Bitmap bMapRotate;
//
//                Matrix matrix = new Matrix();
////                matrix.reset();
//
//                matrix.setRotate(90,(float) bMap.getWidth() / 2, (float) bMap.getHeight() / 2);
////                final Bitmap bm = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), matrix, true);
////
////                matrix.postRotate(90);
//                bMapRotate = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), matrix, true);
//                bMap = bMapRotate;
//
//                // Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
//                File file = new File(path);
//                BufferedOutputStream bos =
//                        new BufferedOutputStream(new FileOutputStream(file));
//                bMap.compress(Bitmap.CompressFormat.JPEG, 50, bos);//将图片压缩到流中
//                bos.flush();//输出
//                bos.close();//关闭
//            }catch(Exception e)
//            {
//                e.printStackTrace();
//            }
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
