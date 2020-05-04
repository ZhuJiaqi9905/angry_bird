package com.example.angrybirds;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

/**
 * 主界面
 * @author ZhengMinghang
 */
public class MainActivity extends AppCompatActivity {
    private ImageButton btnStart, btnExit, btnMusic;

    /**
     * 设置按键效果和音乐
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setButtons() {
        // 开始游戏
        btnStart = (ImageButton) findViewById(R.id.btnStart);
        btnStart.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v("Start", event.toString());
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    btnStart.setBackground(getResources().getDrawable(R.drawable.btn_start_active));
                    BGM.playTouchDown();
                }
                else if(event.getAction() == MotionEvent.ACTION_CANCEL){
                    btnStart.setBackground(getResources().getDrawable(R.drawable.btn_start));
                    BGM.playTouchUp();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP) {
                    btnStart.setBackground(getResources().getDrawable(R.drawable.btn_start));
                    BGM.playTouchUp();
                    Intent intent = new Intent(MainActivity.this, SelectLevelActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        // 退出
        btnExit = (ImageButton) findViewById(R.id.btnExit);
        btnExit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v("Exit", event.toString());
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    btnExit.setBackground(getResources().getDrawable(R.drawable.btn_exit_active));
                    BGM.playTouchDown();
                }
                else if(event.getAction() == MotionEvent.ACTION_CANCEL){
                    btnExit.setBackground(getResources().getDrawable(R.drawable.btn_exit));
                    BGM.playTouchUp();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP) {
                    btnExit.setBackground(getResources().getDrawable(R.drawable.btn_exit));
                    BGM.playTouchUp();
                    finish();
                }
                return true;
            }
        });

        // 开启/关闭音乐
        btnMusic = (ImageButton) findViewById(R.id.btnMusic);
        btnMusic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v("Music", event.toString());
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(BGM.getStatus() == BGM.PLAYER_PLAY)
                        btnMusic.setBackground(getResources().getDrawable(R.drawable.btn_music_active));
                    else
                        btnMusic.setBackground(getResources().getDrawable(R.drawable.btn_nomusic_active));
                    BGM.playTouchDown();
                }
                else if(event.getAction() == MotionEvent.ACTION_CANCEL){
                    if(BGM.getStatus() == BGM.PLAYER_PLAY)
                        btnMusic.setBackground(getResources().getDrawable(R.drawable.btn_music));
                    else
                        btnMusic.setBackground(getResources().getDrawable(R.drawable.btn_nomusic));
                    BGM.playTouchUp();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP) {
                    BGM.playTouchUp();
                    if(BGM.toggle() == BGM.PLAYER_PLAY)
                        btnMusic.setBackground(getResources().getDrawable(R.drawable.btn_music));
                    else
                        btnMusic.setBackground(getResources().getDrawable(R.drawable.btn_nomusic));
                }
                return true;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        try{
            BGM.init(this, R.raw.bgm);
        } catch (Exception e) { e.printStackTrace(); }
        setButtons();
    }

    @Override
    protected void onStart() {
        super.onStart();
        BGM.start();
        if(BGM.getStatus() == BGM.PLAYER_PLAY)
            btnMusic.setBackground(getResources().getDrawable(R.drawable.btn_music));
        else
            btnMusic.setBackground(getResources().getDrawable(R.drawable.btn_nomusic));
    }

    @Override
    protected void onPause() {
        super.onPause();
        BGM.pause();
    }
}
