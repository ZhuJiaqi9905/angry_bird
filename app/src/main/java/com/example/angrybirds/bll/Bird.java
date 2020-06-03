package com.example.angrybirds.bll;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.provider.Contacts;
import android.util.Log;

import com.example.angrybirds.R;
import com.example.angrybirds.ui.UiInterface;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class Bird extends BasicBody {
    public enum Kind{
        BLUE, YELLOW, WHITE, RED;
    }
    private Kind myKind;


    static final float factor = 3;
    private final static float RATE = 30; //物理世界和像素转换比例
    /**
     * 产生一个小鸟
     *
     * @param x   中心点横坐标
     * @param y   中心点纵坐标
     * @param ang 旋转角度
     * @param kind 鸟的种类
     * @param context 环境
     */
    Bird(float x, float y, float ang, Kind kind, Context context) {

        super(x, y, ang);
        myKind = kind;
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
        this.characterfixdef.density *= factor;  // 整体调整质量的因子，优化游戏体验 new Add
        this.y -=  this.getHeight() / 2;

    }


    /**
     * 创建小鸟刚体
     * @param world 物理世界
     * @param RATE 物理世界与像素的转换比例
     */
    synchronized void createBirdBody(World world, float RATE){
        float w = getWidth() ;
        float h = getHeight();
        PolygonShape polShape = new PolygonShape();//形状是矩形
        polShape.setAsBox(w/2/RATE, h/2/RATE);
        characterfixdef.shape = polShape;
        characterfixdef.filter.groupIndex = -1; // 允许碰撞
        setPosition(new Vec2((x)/RATE, (y)/RATE) );
        while (this.body == null) {
            this.body = world.createBody(characterdef); //物理世界创造物体
        }
        body.createFixture(characterfixdef);
        body.m_userData = this; //在body中保存Bird
        body.setActive(true);
    }





    /**
     * 碰撞动作
     */
    @Override
    void actionAfterCrash() {
        super.actionAfterCrash();
    }


    /**
     *点击屏幕时，执行不同鸟的动作
     * @param birdGroup 存放新产生蓝鸟的容器
     * @param ui ui界面
     * @param context 环境
     * @param world 物理世界
     */
    void doClickAction(ArrayList<Bird> birdGroup, UiInterface ui, Context context, World world){
        switch(myKind){
            case RED:
                actByRedBird();
                break;
            case WHITE:
                actByWhiteBird();
                break;
            case YELLOW:
                actByYellowBird();
                break;
            case BLUE:
                actByBlueBird(birdGroup, ui, context, world);
                break;
        }


    }

    /**
     * 红鸟点击屏幕的动作。实际上什么都不做
     */
    private void actByRedBird(){
    };

    /** 黄鸟点击屏幕的动作。在原方向上速度加倍，进行冲击。
    *
     */
    private void actByYellowBird(){
        Vec2 v = this.body.getLinearVelocity();
        Vec2 new_v =new Vec2(2* v.x, 2* v.y);
        this.body.setLinearVelocity(new_v);
    }

    /**
     *蓝鸟点击屏幕时，会分身新的蓝鸟
     * @param birdGroup 存放新生成的鸟的容器
     * @param ui ui界面
     * @param context 环境
     * @param world 物理世界
     */
    private void actByBlueBird(ArrayList<Bird> birdGroup, UiInterface ui, Context context, World world){
        Vec2 curVel = this.body.getLinearVelocity();
        Vec2 upVel = new Vec2(0.866f*curVel.x-0.5f*curVel.y, 0.5f*curVel.x+0.866f*curVel.y);
        Vec2 downVel = new Vec2(0.866f*curVel.x+0.5f*curVel.y, -0.5f*curVel.x+0.866f*curVel.y);
        //创建两个新的鸟
        Bird upBird = new Bird(this.x, this.y, 0, Kind.BLUE, context);
        ui.addBody(upBird);
        Bird downBird = new Bird(this.x, this.y, 0, Kind.BLUE, context);
        ui.addBody(downBird);

        upBird.createBirdBody(world, RATE);
        downBird.createBirdBody(world, RATE);
        upBird.body.setLinearVelocity(upVel);
        downBird.body.setLinearVelocity(downVel);
        birdGroup.add(upBird);
        birdGroup.add(downBird);
    }

    /**
     * 白鸟点击屏幕时，会垂直下落
     */
    private void actByWhiteBird(){
        Vec2 v = this.body.getLinearVelocity();
        Vec2 new_v =new Vec2(0, 3 * v.length());
        this.body.setLinearVelocity(new_v);
    }
    public Kind getMyKind(){
        return myKind;
    }
}
