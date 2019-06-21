package com.example.zzq.qmusic;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.security.acl.Permission;

public class WelcomeActivity extends AppCompatActivity  {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        startService(new Intent(this,MusicService.class));
        /* 启动画面，停留3s之后跳转到主菜单页面*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this,MainMenuActivity.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        },3000);
    }


}
