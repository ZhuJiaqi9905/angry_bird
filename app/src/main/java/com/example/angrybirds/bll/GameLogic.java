package com.example.angrybirds.bll;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.angrybirds.music.BGM;
import com.example.angrybirds.ui.UiInterface;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * 一个简单的游戏逻辑处理
 * 小鸟发射后朝一个方向移动，点击屏幕调整移动方向
 * 小鸟飞出屏幕时游戏结束
 * @author ZhengMinghang
 */
public class GameLogic implements ShotListener, ClickListener, Runnable, ContactListener,
        ResumeListener, DestroyListener, CreateListener{
    private static final int GAME_READY = 0; // 小鸟未发射
    private static final int GAME_FLYING = 1; // 小鸟正在飞翔
    private static final int GAME_OVER = 2; // 结束了
    private int status; // 状态

    private Context context;
    private UiInterface ui;
    private int level;

    //鸟，猪，材料的容器
    private ArrayList<Bird> birdGroup;
    private ArrayList<Pigs> pigGroup;
    private ArrayList<Material> materialGroup;

    private ArrayList<Body> bodyToBeDestroyed;//要被销毁的body刚体


    // 目前执行动作的小鸟及其属性
    private Bird curBird;
    private int curBirdIndex; // 小鸟在数组中编号

    private float dx, dy; // 小鸟飞翔的方向

    private boolean flag; // 控制线程结束

    // 物理世界
    private World world;//添加一个物理世界
    private final static float RATE = 30; //物理世界和像素转换比例
    private AABB aabb; //物理世界的范围对象
    private Vec2 gravity;//重力向量
    private float timeStep = 1f/60f;//物理世界模拟频率
    private int velocityIteration = 10; //物理世界刷新的迭代值
    private int positionIteration = 10;


    private Body gBody;
    //大地
    Ground ground;

    private final static float DIEDSPEED = 5;

    //不同等级的布局
    LevelLayout levelLayout;

    public GameLogic(Context context, int level, UiInterface ui) {
        Log.d(TAG, "GameLogic: create");
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
        //创建物理世界的部分
        gravity = new Vec2(0, 10);
        world = new World(gravity, false);
        AABB aabb =  new AABB();
        aabb.lowerBound.set(-100, -100);
        aabb.upperBound.set(100, 100);
        world.queryAABB(null, aabb);
        world.setContactListener(this); // 设置碰撞监听
        Log.v("screenH", "" + ui.getScreenH());
        Log.v("screenW", ""+ui.getScreenW());
        Log.v("groundY", ""+ui.getGroundY());

        // 大地刚体创建
        ground = new Ground(0, (ui.getScreenH() + ui.getGroundY())/2, ui.getScreenW() , ui.getScreenH()- ui.getGroundY(), context);
        gBody = ground.createGroundBody(world, RATE);

        //创建布局和世界
        birdGroup = new ArrayList<Bird>(5);
        pigGroup = new ArrayList<Pigs>(5);
        materialGroup = new ArrayList<Material>(10);
        bodyToBeDestroyed = new ArrayList<Body>(10);

        levelLayout = new LevelLayout(birdGroup,pigGroup,materialGroup,ui,context, world);
        //对第level关进行布局。建立鸟，猪，材料等
        levelLayout.createLevel(this.level);


        // 小鸟上弓, 游戏开始
        curBirdIndex = 0;
        newTurn(curBirdIndex);
    }



    // 点击屏幕时候小鸟动作
    @Override
    public void clickPerformed(float x, float y) {
        if(status == GAME_FLYING) {
            Vec2 v = curBird.body.getLinearVelocity();
            Vec2 new_v =new Vec2(2* v.x, -2* v.y);
            System.out.println("Velocity: " + v);
            curBird.ang += 90;
            curBird.body.setLinearVelocity(new_v);
        }
    }

    @Override
    public void shotPerformed(BasicBody body, float x, float y) {
        dx = x - body.x;
        dy = y - body.y;
        status = GAME_FLYING;

        this.curBird.x = body.x;
        this.curBird.y = body.y;
        //把bird和刚体联系起来
        curBird.createBirdBody(world, RATE);
        curBird.body.applyForce(new Vec2(dx, dy), curBird.body.getWorldCenter());

    }

    @Override
    public void run() {
        while (flag){
            if(status != GAME_OVER){
                if(curBird == null){
                    Log.v("null ", "curBird is null");
                }
                if(curBird != null && curBird.body != null){

                    simulateWorld();
                    // 猪群
                    Pigs pig;
                    for (int i = 0; i < pigGroup.size(); i++) {
                        pig = pigGroup.get(i);
                        if(pig != null && pig.alive){
                           pig.updatePosition(); //根据物理世界的模拟，同步猪的位置
                        }
                    }
                    //材料
                    Material material;
                    for (int i = 0; i < materialGroup.size(); i++) {
                        material = materialGroup.get(i);
                        if(material != null && material.alive){
                            material.updatePosition();//根据物理世界的模拟，同步材料的位置
                        }
                    }

                    //小鸟
                    if(status == GAME_FLYING){
                        if(curBird != null){
                            curBird.updatePosition();//根据物理世界的模拟，同步小鸟的位置
                            check();
                        }

                    }
                }
                try{
                    Thread.sleep(10);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
            else{
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    private void check(){
        // 越界检查
        Vec2 v = curBird.body.getLinearVelocity();
        if(curBird.x > ui.getScreenW() || curBird.y < 0 || curBird.x < 0 ||
                v.x * v.x + v.y * v.y < 1){
            curBird.alive = false;
            curBird.body.setActive(false);
            bodyToBeDestroyed.add(curBird.body);

            checkGame(); // 检查游戏是否结束
        }

    }

    void checkGame(){
        boolean pigAlive = false, birdAlive = false;
        for(Pigs curPig : pigGroup){
            if (curPig.alive) {
                pigAlive = true;
                break;
            }
        }
        if(!pigAlive) { // 游戏胜利
            ui.gameOver(true);
            BGM.playVictory();
            Log.d("game win", "in game win");
            status = GAME_OVER;
        }
        else{
            curBirdIndex++;
            birdAlive = curBirdIndex < birdGroup.size();
            if(!birdAlive){ // 游戏失败
                ui.gameOver(false);
                BGM.playDefeat();
                status = GAME_OVER;
            }
            else{ // 新鸟上弓箭
                newTurn(curBirdIndex);
            }
        }
    }

    // 新的小鸟上弓
    void newTurn(int index){

        curBird = birdGroup.get(index);
        ui.putOnSlingshot(curBird, this); // 上弓箭
        status = GAME_READY;
    }

    @Override
    public void destroyPerformed() {
        flag = false;

        for(Body b : this.bodyToBeDestroyed){
            world.destroyBody(b);
        }
    }

    @Override
    public void resumePerformed() {
        status = GAME_OVER; // 结束上局游戏
        init();
    }

    @Override
    public void createPerformed() {
        flag = true;
        new Thread(this).start();
    }

    /**
     * 让jbox物理世界进行模拟
     */
    private void simulateWorld(){

        try{
            world.step(timeStep, velocityIteration, positionIteration);
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    // 碰撞监听接口函数
    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();
        boolean pigIsDie = false;
        Body bodyPig = null;
        if(bodyA.m_userData instanceof Pigs && !(bodyB.m_userData instanceof Pigs)){
            //如果bodyA是一个猪，bodyB不是猪
            pigIsDie = judgePigDie(bodyA, bodyB);
            bodyPig = bodyA;
        }
        else if(bodyB.m_userData instanceof Pigs && !(bodyA.m_userData instanceof Pigs)){
            pigIsDie = judgePigDie(bodyB, bodyA);
            bodyPig = bodyB;
        }
        else{
            return;
        }
        if(pigIsDie && bodyPig != null){
            Pigs pig = (Pigs)bodyPig.m_userData;
            pig.alive = false;

            bodyPig.setActive(false);
            world.destroyBody(bodyPig);
            bodyToBeDestroyed.add(bodyPig);

        }

    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    /**
     * 判断猪和其它物体碰撞时是否死亡
     * @param pigBody 猪的刚体
     * @param otherBody 其它刚体
     * @return
     */
    private boolean judgePigDie(Body pigBody, Body otherBody){
        Vec2 pigVel = pigBody.getLinearVelocity();
        Vec2 otherVel = otherBody.getLinearVelocity();
        Vec2 relativeVel = pigVel.sub(otherVel);//计算相对速度
        Log.v("rel speed", " " + relativeVel.length());
        //如果相对速度大于死亡速度,就死了
        return relativeVel.length() > DIEDSPEED;
    }

}

