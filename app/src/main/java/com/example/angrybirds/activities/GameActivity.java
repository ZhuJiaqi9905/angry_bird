package com.example.angrybirds.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.angrybirds.ui.GameSurfaceView;
import com.example.angrybirds.R;
import com.example.angrybirds.ui.ViewUtils;
import com.example.angrybirds.bll.GameLogic;
import com.example.angrybirds.music.BGM;

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
        btnBack = ViewUtils.setButton(this, R.id.btnBack, R.drawable.btn_back,
                R.drawable.btn_back_active, this::finish);

        // 开启/关闭音乐
        btnMusic = ViewUtils.setBtnMusic(this, R.id.btnMusic);

        // 重新开始
        btnResume = ViewUtils.setButton(this, R.id.btnResume, R.drawable.btn_resume,
                R.drawable.btn_resume_active, ()-> gameView.resume());
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
        level = intent.getIntExtra("level", 1);
        ((TextView) findViewById(R.id.level)).setText(
                String.format(getString(R.string.Level), level));

        // 创建线程处理游戏逻辑
        new Thread(() -> new GameLogic(GameActivity.this, level, gameView)).start();
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
