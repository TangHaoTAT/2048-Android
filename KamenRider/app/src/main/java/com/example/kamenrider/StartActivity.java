package com.example.kamenrider;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
//    private static int SPLASH_DISPLAY_LENGHT=3000; //延迟3秒
    private CountDownTimer timer;
    private TextView skip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        /*
         *方式一:通过继承Activity，使用Handler对象实现,AndroidManifest.xml中对应Activity中android:theme="@android:style/Theme.NoTitleBar"
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(StartActivity.this,MainActivity.class);
                startActivity(intent);//执行intent跳转
                StartActivity.this.finish();//关闭StartActivity，将其回收，防止按返回键会返回此界面
            }
        },SPLASH_DISPLAY_LENGHT);
        */
//        AppCompatActivity没有标题栏，只是有个ActionBar像个标题栏，对于AppCompatActivity隐藏title用下面的方式：
        getSupportActionBar().hide();
//        全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        skip=(TextView)findViewById(R.id.skip);
        if (!isFinishing()){
            timer=new CountDownTimer(1000*3,1000) {
                @Override
                public void onTick(long l) {
/*
                    int timeNumber=(int)l;
                    skip.setText(timeNumber/1000+" 跳过");
                    skip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(StartActivity.this,MainActivity.class));
                            // 回收内存
                            // 避免内存泄漏
                            if (timer != null) {
                                timer.cancel();
                                timer = null;
                            }
                            finish();
                        }
                    });*/
                }
                @Override
                public void onFinish() {
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                    // 回收内存
                    // 避免内存泄漏
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    finish();
                }
            }.start();
        }
    }
}
