package com.example.angrybirds.bll;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.angrybirds.R;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

class Bird extends BasicBody {
    public enum Kind{
        BLUE, YELLOW, WHITE, RED
    }

    /**
     * 产生一个物体
     *
     * @param x   中心点横坐标
     * @param y   中心点纵坐标
     * @param ang 旋转角度
     */
    public Bird(float x, float y, float ang,  Kind kind, Context context) {

        super(x, y, ang);

        // 根据类型设置不同小鸟属性
        switch (kind){
            case BLUE:
                ico = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_1);
                this.characterfixdef.density = 0.009f;
                break;
            case YELLOW:
                ico = BitmapFactory.decodeResource(context.getResources(), R.drawable.yellow_1);
                this.characterfixdef.density = 0.006f;
                break;

            case WHITE:
                ico = BitmapFactory.decodeResource(context.getResources(), R.drawable.white_1);
                this.characterfixdef.density = 0.007f;
                break;
            case RED:
                ico = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_1);
                this.characterfixdef.density = 0.008f;
                break;
            default:
                break;
        }
        this.y -=  this.getHeight() / 2;

    }


    public synchronized void createBirdBody(World world, float RATE){
        float w = getWidth() ;
        float h = getHeight();
        Log.d("Bird", "height"  + this.getHeight() + "width " + this.getWidth());

        PolygonShape polShape = new PolygonShape();//形状是矩形
        polShape.setAsBox(w/2/RATE, h/2/RATE);

        characterfixdef.shape = polShape;
        characterfixdef.filter.groupIndex = -1; // 允许碰撞


        setPosition(new Vec2((x)/RATE, (y)/RATE) );

        this.body = world.createBody(characterdef); //物理世界创造物体
        body.createFixture(characterfixdef);
        body.m_userData = this; //在body中保存Bird
        body.setActive(true);
    }



    public float getDensity() {
        return this.characterfixdef.density;
    }

    /**
     * 碰撞动作
     */
    @Override
    void actionAfterCrash() {
        super.actionAfterCrash();
    }


}
