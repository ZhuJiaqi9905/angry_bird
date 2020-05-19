package com.example.angrybirds.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
    private GameSurfaceView gameView; // 控制界面的刷新
    private int level;

    /**
     * 设置按键效果和音乐
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setButtons() {
        // 返回按钮
        btnBack = findViewById(R.id.btnBack);
        ViewUtils.setBtnAction(btnBack, this::finish);

        // 开启/关闭音乐按钮
        btnMusic = findViewById(R.id.btnMusic);
        ViewUtils.setBtnAction(btnMusic, ()-> {
            BGM.toggle();
            ViewUtils.setMusicIcon(btnMusic);
        });

        // 重新开始
        btnResume = findViewById(R.id.btnResume);
        ViewUtils.setBtnAction(btnResume, gameView::resume);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 创建布局
        FrameLayout layout = new FrameLayout(this);
        setContentView(layout);

        // 放入游戏界面
        gameView = new GameSurfaceView(this);
        layout.addView(gameView);

        // 放入按钮
        layout.addView(LayoutInflater.from(this).inflate(R.layout.activity_game, null));
        setButtons();

        // 获得当前等级
        Intent intent = getIntent();
        level = intent.getIntExtra("level", 1);

        // 设置标题图片
        int resId = ViewUtils.getResIdByName("title_level_"+ level, R.drawable.title_level_1);
        findViewById(R.id.level).setBackgroundResource(resId);

        // 创建线程处理游戏逻辑
        new Thread(() -> new GameLogic(GameActivity.this, level, gameView)).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        BGM.start();
        ViewUtils.setMusicIcon(btnMusic);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BGM.pause();
    }
}
