package com.example.angrybirds;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.lang.reflect.Field;

/**
 * 选择关卡界面
 * @author ZhengMinghang
 */
public class SelectLevelActivity extends Activity {
    /**
     * 关卡总数
     */
    private int levels = 10;
    private ImageButton btnMusic, btnBack;
    private ImageButton[] btnLevel;

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
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    btnBack.setBackground(getResources().getDrawable(R.drawable.btn_back_active));
                    BGM.playTouchDown();
                }
                else if(event.getAction() == MotionEvent.ACTION_CANCEL){
                    btnBack.setBackground(getResources().getDrawable(R.drawable.btn_back));
                    BGM.playTouchUp();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP) {
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

        // 关卡按钮
        LinearLayout btnLayout = findViewById(R.id.levels);
        btnLevel = new ImageButton[levels];
        for(int i = 0; i < levels; i++){
            btnLevel[i] = new ImageButton(this);
            final int level = i + 1;
            int resid = R.drawable.level_1;
            try {
                Field field = R.drawable.class.getField("level_"+ level);
                resid = field.getInt(field.getName());
            } catch (Exception e) {}
            btnLevel[i].setBackgroundResource(resid);
            btnLayout.addView(btnLevel[i]);
            setMargins(btnLevel[i], 10, 10, 10, 10);
            btnLevel[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        setMargins(v, 10, 20, 10, 10);
                        BGM.playTouchDown();
                    }
                    else if(event.getAction() == MotionEvent.ACTION_CANCEL) {
                        setMargins(v, 10, 10, 10, 10);
                        BGM.playTouchUp();
                    }
                    else if(event.getAction() == MotionEvent.ACTION_UP) {
                        setMargins(v, 10, 10, 10, 10);
                        BGM.playTouchUp();
                        Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
                        intent.putExtra("level", level);
                        startActivity(intent);
                    }
                    return true;
                }
            });
        }
    }

    /**
     * 设置margin 单位 dip
     */
    private void setMargins(View view, int left, int top, int right, int bottom){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(ViewUtils.dip2px(this, left), ViewUtils.dip2px(this, top),
                ViewUtils.dip2px(this, right), ViewUtils.dip2px(this, bottom));
        view.setLayoutParams(lp);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_level);
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
