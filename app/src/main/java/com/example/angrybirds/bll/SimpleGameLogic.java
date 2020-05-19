package com.example.angrybirds.bll;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.example.angrybirds.R;
import com.example.angrybirds.music.BGM;
import com.example.angrybirds.ui.UiInterface;

/**
 * 一个简单的游戏逻辑处理
 * 小鸟发射后朝一个方向移动，点击屏幕调整移动方向
 * 小鸟飞出屏幕时游戏结束
 * @author ZhengMinghang
 */
public class SimpleGameLogic implements ShotListener, ClickListener, Runnable,
        ResumeListener, DestroyListener, CreateListener{
    private static final int FPS = 30; // 帧率
    private static final int GAME_READY = 0; // 小鸟未发射
    private static final int GAME_FLYING = 1; // 小鸟正在飞翔
    private static final int GAME_OVER = 2; // 结束了
    private int status; // 状态

    private Context context;
    private UiInterface ui;
    private int level;

    private BasicBody bird, pig;
    private float dx, dy; // 小鸟飞翔的方向

    private boolean flag; // 控制线程结束

    public SimpleGameLogic(Context context, int level, UiInterface ui) {
        this.level = level;
        this.ui = ui;
        this.context = context;
        ui.setDestroyListener(this);
        ui.setResumeListener(this);
        ui.setClickListener(this);
        ui.setCreateListener(this);
        init();
    }

    private void init() {
        this.status = GAME_READY;
        bird = new BasicBody(BitmapFactory.decodeResource(context.getResources(), R.drawable.birds),
                0, 0, 0);
        pig = new BasicBody(BitmapFactory.decodeResource(context.getResources(), R.drawable.pigs),
                ui.getScreenW() / 2f, ui.getGroundY(), 0);
        pig.y -= pig.getHeight() / 2;
        ui.putOnSlingshot(bird, this);
        ui.addBody(pig);
    }

    @Override
    public void clickPerformed(float x, float y) {
        if(status == GAME_FLYING) {
            bird.ang += 90;
            dy = -dy;
        }
    }

    @Override
    public void shotPerformed(BasicBody body, float x, float y) {
        dx = x - body.x;
        dy = y - body.y;
        status = GAME_FLYING;
    }

    @Override
    public void run() {
        // 朝一个方向移动，超出屏幕时游戏结束
        while(flag) {
            long startTime = System.currentTimeMillis();

            if(status == GAME_FLYING){
                bird.x += dx * 0.2;
                bird.y += dy * 0.2;
                if(bird.x > ui.getScreenW() || bird.x < 0){
                    BGM.playVictory();
                    ui.gameOver(true);
                    status = GAME_OVER;
                }
                if(bird.y > ui.getGroundY() - bird.getHeight()/2 || bird.y < 0){
                    BGM.playDefeat();
                    ui.gameOver(false);
                    status = GAME_OVER;
                }
            }

            long endTime = System.currentTimeMillis();
            try {
                if (endTime - startTime < 1000 / FPS){
                    Thread.sleep(1000 / FPS - (endTime - startTime));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void destroyPerformed() {
        flag = false;
    }

    @Override
    public void resumePerformed() {
        init();
    }

    @Override
    public void createPerformed() {
        flag = true;
        new Thread(this).start();
    }
}
