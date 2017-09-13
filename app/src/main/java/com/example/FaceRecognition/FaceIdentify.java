package com.example.FaceRecognition;

import android.app.Dialog;
import android.app.ProgressDialog;
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

import com.example.FaceRecognition.Model.User;
import com.example.FaceRecognition.Util.CompressJPG;
import com.example.FaceRecognition.Util.Constant;
import com.example.FaceRecognition.Util.NetUtil;
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
    private CircleSurfaceView mCircleSurfaceView;
    //标志位，0代表注册，1代表登录
    private int flag;
    private ImageButton takePicBtn;
    private SurfaceHolder surfaceHolder;
    private Camera mCamera;
    private Dialog m_Dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_identify);
        mCircleSurfaceView = (CircleSurfaceView) findViewById(R.id.sufaceview);
        takePicBtn = (ImageButton) findViewById(R.id.take_pic_btn);
        takePicBtn.setOnClickListener(this);
        Intent intent = getIntent();
        flag = intent.getIntExtra("FLAG", 1);
        Log.d(TAG, "onCreate: Flag = " + flag);
        surfaceHolder = mCircleSurfaceView.getHolder();
        initListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_pic_btn:
                m_Dialog= ProgressDialog.show(FaceIdentify.this, "提示...", "请稍后...",true);
                mCamera.takePicture(null, null, pc);
                SystemClock.sleep(2000);
                final Handler mHandler=new Handler();

                new Thread(new Runnable() {
                    int myFlag;
                    @Override
                    public void run() {

                    try {
                        CompressJPG.Compress(Constant.Path);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                        if (flag == 0) { //注册
                            try {
                                boolean reFlag = NetUtil.network(User.getAccount(), User.getPassword(), Constant.Path, "register");
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
                                if (NetUtil.network(User.getAccount(), "", Constant.Path, "loginByPic")) {
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
                                            Toast.makeText(FaceIdentify.this, "，注册成功！", Toast.LENGTH_SHORT).show();
                                            break;
                                        case 2 :
                                            Toast.makeText(FaceIdentify.this, "，注册失败！", Toast.LENGTH_SHORT).show();
                                            break;
                                        case 3 :
                                            Toast.makeText(FaceIdentify.this, "，注册失败！文件未找到", Toast.LENGTH_SHORT).show();
                                            break;
                                        case 4 :
                                            Toast.makeText(FaceIdentify.this, "，登陆成功！", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(FaceIdentify.this, PersonalActivity.class);
                                            startActivity(i);
                                            finish();
                                            break;
                                        case 5 :
                                            Toast.makeText(FaceIdentify.this, "，登陆失败！", Toast.LENGTH_SHORT).show();
                                            break;
                                        case 6 :
                                            Toast.makeText(FaceIdentify.this, "，登陆失败！文件未找到", Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                }
                            });
                            Intent i = new Intent(FaceIdentify.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        m_Dialog.dismiss();
                    }
                }).start();


                break;
        }
    }

    private Camera.PictureCallback pc = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            Bitmap bMap;
            // 使用流进行读写
            try {
                bMap = BitmapFactory.decodeByteArray(data, 0, data.length);
                try {
                    Bitmap bMapRotate;
                    bMapRotate = Rotate.rotatePicture(Constant.Path, bMap);
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(Constant.Path));
                    bMap = bMapRotate;
                    bMap.compress(Bitmap.CompressFormat.JPEG, 50, bos);//将图片压缩到流中
                    bos.write(data);
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
