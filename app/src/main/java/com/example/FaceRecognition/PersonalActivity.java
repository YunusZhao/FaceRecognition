package com.example.FaceRecognition;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.FaceRecognition.Model.User;
import com.example.FaceRecognition.Util.ActivityController;
import com.example.FaceRecognition.Util.BaseActivity;

public class PersonalActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        //UI组件
        TextView helloText = (TextView) findViewById(R.id.hello_text);
        Button logoutBtn = (Button) findViewById(R.id.logout);
        Button exitBtn = (Button) findViewById(R.id.exit_app);

        helloText.setText("你好，" + User.getAccount());

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityController.finishAll();
                Intent intent = new Intent(PersonalActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityController.finishAll();
            }
        });

    }
}
