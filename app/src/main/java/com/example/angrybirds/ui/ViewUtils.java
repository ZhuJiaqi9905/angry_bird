package com.example.angrybirds.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.angrybirds.R;
import com.example.angrybirds.activities.GameActivity;
import com.example.angrybirds.music.BGM;

import java.lang.reflect.Field;

/**
 * 一些界面相关的工具函数
 * @author ZhengMinghang
 */
@SuppressLint("ClickableViewAccessibility")
public class ViewUtils {

    /**
     * 设置View margin 单位 px
     */
    public static void setMargins(View view, int left, int top, int right, int bottom){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(left, top, right, bottom);
        view.setLayoutParams(lp);
    }

    /**
     * 设置按钮
     * @param activity 按键所在的activity
     * @param id 按钮组件resource id
     * @param imgId 按钮图片 resource id
     * @param activeImgId 按钮按下图片 resource id
     * @param fun 点击后的要做的事
     * @return 设置完成的 btn
     */
    public static ImageButton setButton(final Activity activity, final int id, final int imgId,
                                 final int activeImgId, final Runnable fun){
        final ImageButton btn = activity.findViewById(id);
        btn.setBackgroundResource(imgId);
        btn.setOnTouchListener((v, event) -> {
            // 按下
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                btn.setBackgroundResource(activeImgId);
                BGM.playTouchDown();
            }
            // 抬起
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                btn.setBackgroundResource(imgId);
                BGM.playTouchUp();
                fun.run();
            }
            return true;
        });
        return btn;
    }

    /**
     * 开启/关闭音乐按钮
     * 涉及图标的切换，单独处理
     * @param activity 所在activity
     * @param id 按钮组件id
     */
    public static ImageButton setBtnMusic(final Activity activity, final int id) {
        final ImageButton btnMusic = activity.findViewById(id);
        btnMusic.setBackgroundResource(R.drawable.btn_music);

        btnMusic.setOnTouchListener((v, event) -> {
            // 按下
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                if(BGM.getStatus() == BGM.PLAYER_PLAY)
                    btnMusic.setBackgroundResource(R.drawable.btn_music_active);
                else
                    btnMusic.setBackgroundResource(R.drawable.btn_nomusic_active);
                BGM.playTouchDown();
            }
            // 抬起
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                BGM.playTouchUp();
                if(BGM.toggle() == BGM.PLAYER_PLAY)
                    btnMusic.setBackgroundResource(R.drawable.btn_music);
                else
                    btnMusic.setBackgroundResource(R.drawable.btn_nomusic);
            }
            return true;
        });
        return btnMusic;
    }

    /**
     * 设置选择关卡按钮
     * @param activity 所处的activity
     * @param level 关卡等级
     */
    public static ImageButton setBtnLevel(final Activity activity, final int level) {
        ImageButton btnLevel = new ImageButton(activity);
        int resid = R.drawable.level_1;
        try {
            Field field = R.drawable.class.getField("level_"+ level);
            resid = field.getInt(field.getName());
        } catch (Exception e) {}

        btnLevel.setBackgroundResource(resid);

        final int margin = activity.getResources().getDimensionPixelSize(R.dimen.levels_margin);
        final int active_margin = activity.getResources().getDimensionPixelSize(R.dimen.levels_top_active);
        setMargins(btnLevel, margin, margin, margin, margin);

        btnLevel.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                setMargins(v, margin, active_margin, margin, margin);
                BGM.playTouchDown();
            }
            else if(event.getAction() == MotionEvent.ACTION_CANCEL) {
                setMargins(v, margin, margin, margin, margin);
                BGM.playTouchUp();
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                setMargins(v, margin, margin, margin, margin);
                BGM.playTouchUp();
                Intent intent = new Intent(activity, GameActivity.class);
                intent.putExtra("level", level);
                activity.startActivity(intent);
            }
            return true;
        });
        return btnLevel;
    }

    /**
     * 计算距离
     */
    public static float dist(float x1, float y1, float x2, float y2){
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
}
