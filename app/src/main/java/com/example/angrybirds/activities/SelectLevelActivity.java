package com.example.angrybirds.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.angrybirds.R;
import com.example.angrybirds.ui.ViewUtils;
import com.example.angrybirds.music.BGM;

/**
 * 选择关卡界面
 * @author ZhengMinghang
 */
public class SelectLevelActivity extends Activity {
    private static final int LEVELS = 3; // 关卡总数

    private ImageButton btnMusic, btnBack;
    private ImageButton[] btnLevels;

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

        // 关卡按钮
        LinearLayout btnLayout = findViewById(R.id.levels);
        btnLevels = new ImageButton[LEVELS];
        for(int level = 1; level <= LEVELS; level++){
            ImageButton btnLevel = new ImageButton(this);
            btnLevels[level - 1] = btnLevel;
            btnLayout.addView(btnLevel);

            // 设置背景图片
            int resId = ViewUtils.getResIdByName("btn_level_"+level, R.drawable.btn_level_1);
            btnLevel.setBackgroundResource(resId);

            // 设置大小
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            int btnWidth = (int) (screenHeight * 0.25);
            int btnHeight = (int) (screenHeight * 0.25);
            ViewUtils.setBtnSize(btnLevel, btnWidth, btnHeight);

            // 设置按钮行为
            final Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("level", level);
            ViewUtils.setBtnAction(btnLevel, ()-> startActivity(intent));
        }
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
        ViewUtils.setMusicIcon(btnMusic);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BGM.pause();
    }
}
