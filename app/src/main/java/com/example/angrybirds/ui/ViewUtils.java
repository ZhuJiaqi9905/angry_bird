package com.example.angrybirds.ui;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.example.angrybirds.R;
import com.example.angrybirds.music.BGM;

import java.lang.reflect.Field;

/**
 * 一些界面相关的工具函数
 * @author ZhengMinghang
 */
@SuppressLint("ClickableViewAccessibility")
public class ViewUtils {
    /**
     * 按钮激活时的缩小比例
     */
    private static final float ACTIVE_SCALE = 0.85f;

    /**
     * 设置按钮大小 单位 px
     */
    public static void setBtnSize(View btn, int width, int height){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
        btn.setLayoutParams(lp);
    }

    /**
     * 依据当前BGM设置，设置Music按钮图标
     * @param btn 音乐按钮
     */
    public static void setMusicIcon(View btn) {
        if(BGM.getStatus() == BGM.PLAYER_PLAY){
            btn.setBackgroundResource(R.drawable.btn_music);
        } else {
            btn.setBackgroundResource(R.drawable.btn_nomusic);
        }
    }

    /**
     * 设置按钮行为
     * @param btn 要设置的按钮
     * @param fun 点击后的要做的事
     */
    public static void setBtnAction(final View btn, final Runnable fun){
        if (btn == null) return;

        btn.setOnTouchListener((v, event) -> {
            // 按下
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                v.setScaleX(ACTIVE_SCALE);
                v.setScaleY(ACTIVE_SCALE);
                BGM.playTouchDown();
            }
            // 取消
            else if(event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.setScaleX(1f);
                v.setScaleY(1f);
                BGM.playTouchUp();
            }
            // 抬起
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                v.setScaleX(1f);
                v.setScaleY(1f);
                BGM.playTouchUp();
                if(fun != null) {
                    fun.run();
                }
            }
            return true;
        });
    }

    /**
     * 通过名称获得drawable中的资源id
     * @param name 资源名
     * @param defaut 资源不存在时的默认返回值
     * @return 资源id
     */
    public static int getResIdByName(String name, int defaut) {
        int resId = defaut;
        try {
            Field field = R.drawable.class.getField(name);
            resId = field.getInt(field.getName());
        } catch (Exception ignored) {}
        return resId;
    }

    /**
     * 计算距离
     */
    public static float dist(float x1, float y1, float x2, float y2){
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
}
