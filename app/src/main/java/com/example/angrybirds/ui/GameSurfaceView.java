package com.example.angrybirds.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import com.example.angrybirds.R;
import com.example.angrybirds.bll.BasicBody;
import com.example.angrybirds.bll.ClickListener;
import com.example.angrybirds.bll.CreateListener;
import com.example.angrybirds.bll.DestroyListener;
import com.example.angrybirds.bll.ResumeListener;
import com.example.angrybirds.bll.ShotListener;
import com.example.angrybirds.music.BGM;

import java.util.LinkedList;
import java.util.List;

/**
 * 动态显示游戏内容
 * @author ZhengMinghang
 */
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback,
        UiInterface, Runnable, View.OnTouchListener {
    public static final int FPS = 30; // 画面刷新帧率

    public static final int GAME_NOT_READY = 0; // surface view 尚未创建
    public static final int GAME_READY = 1; // surface view 创建完毕
    public static final int GAME_WIN = 2; // 游戏胜利
    public static final int GAME_LOSS = 3; // 游戏失败
    private int status; // 游戏状态
    private boolean runFlag; // 刷新画面线程的结束标志

    private List<BasicBody> bodyList; // 要显示的所有物品
    private BasicBody shotBody; // 在弹弓上的物品
    private boolean focusOnShot; // 是否正在拖动弹弓上的物品

    private ShotListener shotListener; // 弹弓发射时间监听
    private ClickListener clickListener; // 点击事件监听
    private DestroyListener destroyListener; // 退出事件监听
    private ResumeListener resumeListener; // 重新开始事件监听
    private CreateListener createListener; // 游戏创建事件监听

    private Bitmap bgBmp; // 背景图
    private Canvas canvas; // 画布
    private SurfaceHolder sfh;

    private int screenW, screenH; // 屏幕尺寸
    private float slingshotW, slingshotH; // 弹弓位置
    private float groundH; // 地面位置

    public GameSurfaceView(final Context context) {
        super(context);
        sfh = this.getHolder();
        sfh.addCallback(this);
        setOnTouchListener(this);
        setFocusable(true);
        bodyList = new LinkedList<>();
        status = GAME_NOT_READY;
    }

    /**
     * 绘图
     */
    private void myDraw() {
        try {
            canvas = sfh.lockCanvas();
            if (canvas == null) return;

            // 背景图片
            canvas.drawBitmap(bgBmp, 0, 0, null);
            // 绘制物体
            for(BasicBody body: bodyList) {
                drawBody(body);
            }
            // 绘制游戏结束界面
            drawOverHint();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(canvas != null) {
                sfh.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawOverHint() {
        Paint paint = new Paint();
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.status_font_size));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(ResourcesCompat.getFont(getContext(), R.font.angrybirds));
        paint.setColor(getResources().getColor(R.color.gameOver));

        // 绘制游戏结束状态
        if(status == GAME_WIN){
            canvas.drawText("Victory", screenW * 0.5f, screenH * 0.6f, paint);
        } else if(status == GAME_LOSS) {
            canvas.drawText("Defeat", screenW * 0.5f, screenH * 0.6f, paint);
        }
    }

    private void drawBody(BasicBody body) {
        if(body == null) return;

        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.slingshot));
        paint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.slingshot_width));
        paint.setStrokeCap(Paint.Cap.ROUND);

        if(body == shotBody) {
            // 弹弓上的物品
            canvas.drawLine(screenW * 410f / 2560f, screenH * 940f / 1440f,
                    body.x - body.getWidth()/4, body.y, paint);
            body.draw(canvas, paint);
            canvas.drawLine(screenW * 338f / 2560f, screenH * 945f / 1440f,
                    body.x - body.getWidth()/4, body.y, paint);
        } else {
            // 非弹弓上的物品
            body.draw(canvas, paint);
        }
    }

    @Override
    public int getScreenW() {
        // 等待画布被创建（否则screenW为0）
        while(status == GAME_NOT_READY){
            try { Thread.sleep(100); } catch (Exception ignored) {}
        }
        return screenW;
    }

    @Override
    public int getScreenH() {
        // 等待画布被创建（否则screenH为0）
        while(status == GAME_NOT_READY){
            try { Thread.sleep(100); } catch (Exception ignored) {}
        }
        return screenH;
    }

    @Override
    public int getGroundY() {
        while(status == GAME_NOT_READY){
            try { Thread.sleep(100); } catch (Exception ignored) {}
        }
        return (int) (groundH);
    }

    @Override
    public void addBody(BasicBody body) {
        while(status == GAME_NOT_READY){
            try { Thread.sleep(100); } catch (Exception ignored) {}
        }
        bodyList.add(body);
    }

    @Override
    public void deleteBody(BasicBody body) {
        if(shotBody == body)
            shotBody = null;
        bodyList.remove(body);
    }

    @Override
    public void putOnSlingshot(BasicBody body, ShotListener listener) {
        addBody(body);
        shotBody = body;
        body.x = slingshotW;
        body.y = slingshotH;
        shotListener = listener;
    }

    @Override
    public void resume() {
        bodyList.clear();
        shotBody = null;
        if(status != GAME_NOT_READY)
            status = GAME_READY;
        if(resumeListener != null)
            resumeListener.resumePerformed();
    }

    @Override
    public void gameOver(boolean result) {
        if(result)
            status = GAME_WIN;
        else
            status = GAME_LOSS;
    }

    @Override
    public void setDestroyListener(DestroyListener listener) {
        destroyListener = listener;
    }

    @Override
    public void setResumeListener(ResumeListener listener) {
        resumeListener = listener;
    }

    @Override
    public void setCreateListener(CreateListener listener) {
        createListener = listener;
    }

    @Override
    public void setClickListener(ClickListener listener) {
        clickListener = listener;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenW = this.getWidth();
        screenH = this.getHeight();
        slingshotW = screenW * 370f/2560f;
        slingshotH = screenH * 942f/1440f;
        groundH = screenH * 1236f/1440f;

        bgBmp = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(this.getResources(), R.drawable.bg_game),
                screenW, screenH, true);

        runFlag = true;
        new Thread(this).start();

        if (status == GAME_NOT_READY)
            status = GAME_READY;

        if(createListener != null)
            createListener.createPerformed();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        runFlag = false;
        if(destroyListener != null)
            destroyListener.destroyPerformed();
    }

    @Override
    public void run() {
        while (runFlag) {
            long startTime = System.currentTimeMillis();
            myDraw();
            long endTime = System.currentTimeMillis();
            try {
                if (endTime - startTime < 1000 / FPS){
                    Thread.sleep(1000 / FPS - (endTime - startTime));
                }
            } catch (Exception ignored) {}
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX(), y = event.getY();
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            // 开始拖动小鸟
            if(shotBody != null && shotBody.pointIn(x, y)) {
                focusOnShot = true;
                BGM.playSlingshot();
            }
        }
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            if(focusOnShot && shotBody != null){
                // 限制弹弓在一定范围内拖动
                float limit = groundH - slingshotH - shotBody.getHeight() / 2;
                float length = ViewUtils.dist(x, y, slingshotW, slingshotH);
                if(length <= limit){
                    shotBody.x = x;
                    shotBody.y = y;
                } else {
                    shotBody.x = (x - slingshotW) / length * limit + slingshotW;
                    shotBody.y = (y - slingshotH) / length * limit + slingshotH;
                }
            }
        }
        if(event.getAction() == MotionEvent.ACTION_UP){
            // 发射小鸟
            if(focusOnShot && shotBody != null){
                BGM.playBirdShot();
                BGM.stopSlingshot();
                if(shotListener != null){
                    shotListener.shotPerformed(shotBody, slingshotW, slingshotH);
                }
                shotBody = null;
            } else if(clickListener != null){
                clickListener.clickPerformed(event.getX(), event.getY());
            }
            focusOnShot = false;
        }
        return true;
    }
}
