package com.example.angrybirds.bll;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;

/**
 * 一个物品，小鸟、猪、木块等
 *
 */
public class BasicBody {
    Bitmap ico;
    public float x, y, ang, size;
    boolean alive = true;
    BodyDef characterdef = new BodyDef();
    FixtureDef characterfixdef = new FixtureDef();
    public Shape charactershape;
    volatile Body body;//一个物体所对应的jbox2d中的刚体
    private final static float RATE = 30; //物理世界和像素转换比例
    float timeStopped;

    /**
     * 产生一个物体
     * @param ico 图标
     * @param x 中心点横坐标
     * @param y 中心点纵坐标
     * @param ang 旋转角度
     */
    BasicBody(Bitmap ico, float x, float y, float ang) {
        this.ico = ico;
        this.x = x;
        this.y = y;
        this.ang = ang;
        this.size = 1;
        initBodySetting();
    }

    BasicBody(float x, float y, float ang){
        this.x = x;
        this.y = y;
        this.ang = ang;
        this.size = 1;
        initBodySetting();
    }

    BasicBody(float x, float y, float ang, float size) {
        this.ico = ico;
        this.x = x;
        this.y = y;
        this.ang = ang;
        this.size = size;
        initBodySetting();
    }

    void initBodySetting(){

        characterdef.type = characterdef.type.DYNAMIC;
        characterdef.allowSleep = false;

        characterfixdef.friction = 0.5f;
        characterfixdef.density = 0.8f;
        characterfixdef.restitution = 0.3f;
    }

    void setIco(Bitmap ico1){
        this.ico = ico1;
    }

    public void draw(Canvas canvas, Paint paint){
        if(alive) { // 存活时候显示
            Matrix mx = new Matrix();
            mx.postRotate(ang, getWidth() / 2f, getHeight() / 2f);
            mx.postTranslate(x - getWidth() / 2f, y - getHeight() / 2f);

            canvas.drawBitmap(ico, mx, paint);
        }
    }

    public Boolean isStopped(float delta) {
        boolean isMoving = (
                Math.abs(body.getLinearVelocity().x) >= 0.25f || Math.abs(body.getLinearVelocity().y) >= 0.25f);
        if(isMoving) {
            timeStopped = 0f;
            return false;
        } else {
            timeStopped += delta;
            return timeStopped >= 0.3f;
        }
    }

    /**
     *  碰撞动作
     */
    void actionAfterCrash(){

    }

    /**
     * 判断(x,y)是否在物体上
     */
    public boolean pointIn(float x, float y){
        return Math.abs(x - this.x) < getWidth() / 2f &&
                Math.abs(y - this.y) < getHeight() / 2f;
    }


    void setPosition(Vec2 worldpos) {
        characterdef.position.set(worldpos);
    }

    public void setCharactershape(Shape charactershape) {
        this.characterfixdef.shape=charactershape;
    }
    public void setCharacterdef(BodyDef characterdef) {
        this.characterdef=characterdef;
    }
    public void setCharacterfixturedef(FixtureDef characterfix) {
        this.characterfixdef=characterfix;

    }

    BodyDef getCharacterdef() {
        return characterdef;
    }

    FixtureDef getCharacterfixdef() {
        return characterfixdef;
    }

    public float getWidth() {
        return ico.getWidth() * size;
    }

    public float getHeight() {
        return ico.getHeight() * size;
    }
    public void updatePosition(){
        if(body == null){
            Log.v("BasicBody", "in updatePosition , body is null");
            return;
        }

        this.x = body.getPosition().x * RATE;
        this.y = body.getPosition().y * RATE;
        this.ang = (float)(body.getAngle() / Math.PI * 180);

    }

}
