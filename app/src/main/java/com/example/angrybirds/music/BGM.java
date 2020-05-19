package com.example.angrybirds.music;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import com.example.angrybirds.R;

/**
 * 播放背景音乐和音效
 * @author ZhengMinghang
 */
public class BGM {
    public static final int PLAYER_PAUSE = 0; // 关闭音乐
    public static final int PLAYER_PLAY = 1; // 开启音乐
    private static int status; // 是否开启音乐

    private static MediaPlayer player;
    private static SoundPool soundPool;

    private static int touchDownSound; // 按键按下音效
    private static int touchUpSound; // 按键抬起音效
    private static int slingshotSound; // 拉动弹弓音效
    private static int shotSound; // 发射音效
    private static int defeatSound; // 失败音效
    private static int victorySound; // 胜利音效

    private static int slingshotStream;

    /**
     * 初始化
     * @param context context
     * @param resId 背景音乐 resource id
     */
    public static void init(Context context, int resId) {
        // 播放背景音乐
        player = MediaPlayer.create(context, resId);
        player.setLooping(true);
        player.start();

        // 加载音效资源
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        touchDownSound= soundPool.load(context, R.raw.touch_down, 1);
        touchUpSound = soundPool.load(context, R.raw.touch_up, 1);
        slingshotSound = soundPool.load(context, R.raw.slingshot, 1);
        shotSound = soundPool.load(context, R.raw.shot, 1);
        defeatSound = soundPool.load(context, R.raw.defeat, 1);
        victorySound = soundPool.load(context, R.raw.victory, 1);

        status = PLAYER_PLAY;
    }

    /**
     * 切换状态（开启声音、关闭声音）
     */
    public static void toggle(){
        if(status == PLAYER_PAUSE) {
            player.start();
            status = PLAYER_PLAY;
        } else {
            player.pause();
            status = PLAYER_PAUSE;
        }
    }

    /**
     * 获得当前播放状态
     * @return status
     */
    public static int getStatus(){
        return status;
    }

    /**
     * 播放按键按下音效
     */
    public static void playTouchDown() {
        if(status == PLAYER_PLAY) {
            Log.v("soundPool", "touch down");
            soundPool.play(touchDownSound, 1, 1, 1, 0, 1);
        }
    }

    /**
     * 播放按键松开音效
     */
    public static void playTouchUp() {
        if(status == PLAYER_PLAY) {
            Log.v("soundPool", "touch up");
            soundPool.play(touchUpSound, 1, 1, 1, 0, 1);
        }
    }

    /**
     * 播放拉弹弓音效
     */
    public static void playSlingshot(){
        if(status == PLAYER_PLAY) {
            Log.v("soundPool", "slingshot");
            slingshotStream = soundPool.play(slingshotSound, 1, 1, 1, -1, 1);
        }
    }

    /**
     * 停止播放拉弹弓音效
     */
    public static void stopSlingshot(){
        if(slingshotStream > 0){
            Log.v("soundPool", "slingshot stop");
            soundPool.stop(slingshotStream);
        }
    }

    /**
     * 播放小鸟发射的声音
     */
    public static void playBirdShot(){
        if(status == PLAYER_PLAY) {
            soundPool.play(shotSound, 1, 1, 1, 0, 1);
        }
    }

    /**
     * 播放失败音效
     */
    public static void playDefeat(){
        if(status == PLAYER_PLAY) {
            soundPool.play(defeatSound, 1, 1, 1, 0, 1);
        }
    }

    /**
     * 播放胜利音效
     */
    public static void playVictory(){
        if(status == PLAYER_PLAY) {
            soundPool.play(victorySound, 1, 1, 1, 0, 1);
        }
    }

   /**
     * 开始播放音乐（要求status为PLAYER_PLAY）
     */
    public static void start(){
        if(status == PLAYER_PLAY && !player.isPlaying()){
            player.start();
        }
    }

    /**
     * 停止播放音乐
     */
    public static void pause(){
        if(player.isPlaying()){
            player.pause();
        }
    }
}
