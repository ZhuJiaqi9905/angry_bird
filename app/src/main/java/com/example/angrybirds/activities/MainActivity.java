package com.example.angrybirds.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.ImageButton;

import com.example.angrybirds.R;
import com.example.angrybirds.ui.ViewUtils;
import com.example.angrybirds.music.BGM;

/**
 * 主界面
 * @author ZhengMinghang
 */
public class MainActivity extends AppCompatActivity {
    private ImageButton btnStart, btnExit, btnMusic;

    /**
     * 设置按键效果和音乐
     */
    private void setButtons() {
        // 开始游戏按钮
        final Intent intent = new Intent(MainActivity.this, SelectLevelActivity.class);
        btnStart = ViewUtils.setButton(this, R.id.btnStart, R.drawable.btn_start,
                R.drawable.btn_start_active, ()-> startActivity(intent));

        // 退出
        btnExit = ViewUtils.setButton(this, R.id.btnExit, R.drawable.btn_exit,
                R.drawable.btn_exit_active, this::finish);

        // 开启/关闭音乐按钮
        btnMusic = ViewUtils.setBtnMusic(this, R.id.btnMusic);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setVolumeControlStream(AudioManager.STREAM_MUSIC); // 音量控制
        try{
            BGM.init(this, R.raw.bgm); // 背景音乐
        } catch (Exception e) { e.printStackTrace(); }
        setButtons(); // 按键
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
