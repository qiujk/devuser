package com.qiujk.devuser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class MainActivity extends AppCompatActivity {
    private Button btnMediaPlayer; //调用摄像头按钮
    private Button btnAlbum; //调用相册按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //// TODO: 2018/1/4
        JSONObject jObj = JSON.parseObject("{\"username\":\"qjk\"}");
        String username = jObj.getString("username");
        initViews();
    }

    private void initViews() {
        btnMediaPlayer = (Button) findViewById(R.id.btnMediaPlayer);
        btnAlbum = (Button) findViewById(R.id.btnAlbum);
        Button btnVideoPlayer = (Button) findViewById(R.id.btnVideoPlayer);
        Button btnVitamio = (Button) findViewById(R.id.btnVitamio);
        ButtonOnClickListener onClickListener = new ButtonOnClickListener();
        btnMediaPlayer.setOnClickListener(onClickListener);
        btnAlbum.setOnClickListener(onClickListener);
        btnVideoPlayer.setOnClickListener(onClickListener);
        btnVitamio.setOnClickListener(onClickListener);
    }

    class ButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnAlbum:
                    Intent intent = new Intent(getApplicationContext(), AlbumActivity.class);
                    startActivity(intent);
                    //finish();
                    break;
                case R.id.btnMediaPlayer:
                    Intent playerIntent = new Intent(getApplicationContext(), VideoActivity.class);
                    startActivity(playerIntent);
                    //finish();
                    break;
                case R.id.btnVideoPlayer:
                    Intent videoIntent = new Intent(getApplicationContext(), VideoViewActivity.class);
                    startActivity(videoIntent);
                    break;
                case R.id.btnVitamio:
                    break;
            }
        }
    }
}
