package com.example.angrybirds.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
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
    /**
     * 关卡总数
     */
    private int levels;
    private ImageButton btnMusic, btnBack;
    private ImageButton[] btnLevel;

    /**
     * 设置按键效果和音乐
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setButtons() {
        // 返回
        btnBack = ViewUtils.setButton(this, R.id.btnBack, R.drawable.btn_back,
                R.drawable.btn_back_active, this::finish);

        // 开启/关闭音乐按钮
        btnMusic = ViewUtils.setBtnMusic(this, R.id.btnMusic);

        // 关卡按钮
        LinearLayout btnLayout = findViewById(R.id.levels);
        btnLevel = new ImageButton[levels];
        for(int i = 0; i < levels; i++){
            btnLevel[i] = ViewUtils.setBtnLevel(this, i + 1);
            btnLayout.addView(btnLevel[i]);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        levels = getResources().getInteger(R.integer.levels);
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
