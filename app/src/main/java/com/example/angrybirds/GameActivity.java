package com.example.angrybirds;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * 处理游戏开始的UI和Logic
 * @author ZhengMinghang
 */
public class GameActivity extends Activity {
    private ImageButton btnBack, btnMusic, btnResume;
    private GameSurfaceView gameView; // ui
    private int level;

    /**
     * 设置按键效果和音乐
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setButtons() {
        // 返回
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v("Back", event.toString());
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btnBack.setBackground(getResources().getDrawable(R.drawable.btn_back_active));
                    BGM.playTouchDown();
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    btnBack.setBackground(getResources().getDrawable(R.drawable.btn_back));
                    BGM.playTouchUp();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btnBack.setBackground(getResources().getDrawable(R.drawable.btn_back));
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
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (BGM.getStatus() == BGM.PLAYER_PLAY)
                        btnMusic.setBackground(getResources().getDrawable(R.drawable.btn_music_active));
                    else
                        btnMusic.setBackground(getResources().getDrawable(R.drawable.btn_nomusic_active));
                    BGM.playTouchDown();
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    if (BGM.getStatus() == BGM.PLAYER_PLAY)
                        btnMusic.setBackground(getResources().getDrawable(R.drawable.btn_music));
                    else
                        btnMusic.setBackground(getResources().getDrawable(R.drawable.btn_nomusic));
                    BGM.playTouchUp();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    BGM.playTouchUp();
                    if (BGM.toggle() == BGM.PLAYER_PLAY)
                        btnMusic.setBackground(getResources().getDrawable(R.drawable.btn_music));
                    else
                        btnMusic.setBackground(getResources().getDrawable(R.drawable.btn_nomusic));
                }
                return true;
            }
        });

        // 重新开始
        btnResume = (ImageButton) findViewById(R.id.btnResume);
        btnResume.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v("Resume", event.toString());
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btnResume.setBackground(getResources().getDrawable(R.drawable.btn_resume_active));
                    BGM.playTouchDown();
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    btnResume.setBackground(getResources().getDrawable(R.drawable.btn_resume));
                    BGM.playTouchUp();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btnResume.setBackground(getResources().getDrawable(R.drawable.btn_resume));
                    BGM.playTouchUp();
                    gameView.resume();
                }
                return true;
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 创建布局并放入按键、标题和gameSurfaceView
        FrameLayout layout = new FrameLayout(this);
        setContentView(layout);
        gameView = new GameSurfaceView(this);
        layout.addView(gameView);
        layout.addView(LayoutInflater.from(this).inflate(R.layout.activity_game, null));
        setButtons();

        // 获得当前等级
        Intent intent = getIntent();
        level = intent.getIntExtra("level", 0);
        ((TextView) findViewById(R.id.level)).setText("Level-"+level);

        // 创建线程处理游戏逻辑
        new Thread(new Runnable() {
            @Override
            public void run() {
                new GameLogic(GameActivity.this, level, gameView);
            }
        }).start();
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
