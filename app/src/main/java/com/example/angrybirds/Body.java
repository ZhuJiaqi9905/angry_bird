package com.example.angrybirds;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * 一个物品，小鸟、猪、木块等
 * @author ZhengMinghang
 */
public class Body {
    public Bitmap ico;
    public float x, y, ang;

    /**
     * 产生一个物体
     * @param ico 图标
     * @param x 中心点横坐标
     * @param y 中心点纵坐标
     * @param ang 旋转角度
     */
    public Body(Bitmap ico, float x, float y, float ang) {
        this.ico = ico;
        this.x = x;
        this.y = y;
        this.ang = ang;
    }

    public void draw(Canvas canvas, Paint paint){
        Matrix mx = new Matrix();
        mx.postRotate(ang, ico.getWidth() / 2f, ico.getHeight() / 2f);
        mx.postTranslate(x - ico.getWidth() / 2f, y - ico.getHeight() / 2f);
        canvas.drawBitmap(ico, mx, paint);
    }

    /**
     * 判断(x,y)是否在物体上
     */
    public boolean pointIn(float x, float y){
        return Math.abs(x - this.x) < ico.getWidth() / 2f &&
                Math.abs(y - this.y) < ico.getHeight() / 2f;
    }

    public float getWidth() {
        return ico.getWidth();
    }

    public float getHeight() {
        return ico.getHeight();
    }
}
