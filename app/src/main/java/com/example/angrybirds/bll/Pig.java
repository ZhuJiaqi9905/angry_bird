package com.example.angrybirds.bll;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.angrybirds.R;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

class Pig extends BasicBody {
    /**
     * 产生一个猪
     *
     * @param x   中心点横坐标
     * @param y   中心点纵坐标
     * @param ang 旋转角度
     * @param size 尺寸比例
     * @param context 环境
     */
    Pig(float x, float y, float ang, float size, Context context) {
        super(x, y, ang, size);
        ico = BitmapFactory.decodeResource(context.getResources(), R.drawable.pigs);
        this.y -=  this.getHeight() / 2;

    }


    /**
     * 创建猪的刚体
     * @param world 物理世界
     * @param RATE 物理世界与手机屏幕的比例
     */
    public synchronized void createPigBody(World world, float RATE){
        float w = getWidth() ;
        float h = getHeight();
        Log.d("pig", "height"  + this.getHeight() + "width " + this.getWidth());

        PolygonShape polShape = new PolygonShape();//形状是矩形
        polShape.setAsBox(w/2/RATE, h/2/RATE);

        characterfixdef.shape = polShape;
        characterfixdef.density = 0.5f;
        characterfixdef.filter.groupIndex = 1; // 设置分组

        setPosition(new Vec2((x)/RATE, (y)/RATE) );
        while(this.body == null){
            this.body = world.createBody(characterdef); //物理世界创造物体
        }
        body.createFixture(characterfixdef);
        body.m_userData = this; //在body中保存刚体
        return;

    }

    /**
     * 碰撞动作定义
     */
    @Override
    void actionAfterCrash() {
        super.actionAfterCrash();
    }
}
