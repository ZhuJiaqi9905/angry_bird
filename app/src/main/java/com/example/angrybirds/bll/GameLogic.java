package com.example.angrybirds.bll;

import android.content.Context;
import android.util.Log;

import com.example.angrybirds.music.BGM;
import com.example.angrybirds.ui.UiInterface;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 *
 *
 *
 */
public class GameLogic implements ShotListener, ClickListener, Runnable, ContactListener,
        ResumeListener, DestroyListener, CreateListener{
    private static final int GAME_READY = 0; // 小鸟未发射
    private static final int GAME_FLYING = 1; // 小鸟正在飞翔
    private static final int GAME_OVER = 2; // 结束了
    private int status; // 状态
    private boolean startSimulate = true;  //物理世界模拟

    private Context context;
    private UiInterface ui;
    private int level; //关卡

    //鸟，猪，材料的容器
    private volatile ArrayList<Bird> birdGroup;
    private volatile ArrayList<Pig> pigGroup;
    private volatile ArrayList<Material> materialGroup;

    private volatile ArrayList<Bird> curCreateBirdGroup; //蓝鸟点击屏幕会出现新的蓝鸟。用这个容器装
    private ArrayList<Body> bodyToBeDestroyed;//要被销毁的body刚体


    // 目前执行动作的小鸟及其属性
    private volatile Bird curBird;
    private volatile int curBirdIndex; // 小鸟在数组中编号

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

    //大地
    Ground ground;

    private final static float PIG_DIE_SPEED = 4f;
    private final static float MATERIAL_DIE_SPEED = 3f;
    //不同等级的布局
    LevelLayout levelLayout;

    //点击屏幕触发小鸟额外技能
    private boolean isClicked = false;

    public GameLogic(Context context, int level, UiInterface ui) {
        Log.d(TAG, "GameLogic: create");
        this.level = level;

        this.ui = ui;
        this.context = context;
        init();

        ui.setDestroyListener(this);
        ui.setResumeListener(this);
        ui.setClickListener(this);
        ui.setCreateListener(this);
    }

    private void init() {
        startSimulate = false;

        //创建物理世界的部分
        gravity = new Vec2(0, 10);
        world = new World(gravity, false);
        AABB aabb =  new AABB();
        aabb.lowerBound.set(-100, -100);
        aabb.upperBound.set(100, 100);
        world.queryAABB(null, aabb);
        world.setContactListener(this); // 设置碰撞监听

        // 大地刚体创建
        ground = new Ground(0, (ui.getScreenH() + ui.getGroundY())/2, ui.getScreenW() , ui.getScreenH()- ui.getGroundY(), context);
        ground.createGroundBody(world, RATE);

        //创建布局和世界
        birdGroup = new ArrayList<Bird>();
        pigGroup = new ArrayList<Pig>();
        materialGroup = new ArrayList<Material>();
        bodyToBeDestroyed = new ArrayList<Body>();
        curCreateBirdGroup = new ArrayList<Bird>();

        levelLayout = new LevelLayout(birdGroup,pigGroup,materialGroup,ui,context, world);
        //对第level关进行布局。建立鸟，猪，材料等
        levelLayout.createLevel(this.level);


        // 小鸟上弓, 游戏开始
        curBirdIndex = 0;
        newTurn(curBirdIndex);
        startSimulate = true;
        isClicked = false;
    }





    /**
     * 点击屏幕时候小鸟动作
     * @param x x坐标
     * @param y y坐标
     */
    @Override
    public void clickPerformed(float x, float y) {

        if(status == GAME_FLYING && isClicked == false) {
            curBird.doClickAction(curCreateBirdGroup,ui, context, world);
            isClicked = true;
        }
    }


    /**
     * 准备发射
     * body 小鸟
     * x 鼠标的位置x
     * y 鼠标的位置y
     */
    @Override
    public synchronized void shotPerformed(BasicBody body, float x, float y) {
        startSimulate = false;
        //得到发射的方向
        dx = x - body.x;
        dy = y - body.y;
        status = GAME_FLYING;

        this.curBird.x = body.x;
        this.curBird.y = body.y;
        //把bird和刚体联系起来
        curBird.createBirdBody(world, RATE);
        curBird.body.applyForce(new Vec2(dx * 5f, dy * 5f) , curBird.body.getWorldCenter());  // new Add
        startSimulate = true;
        isClicked = false;
    }

    /**
     * 游戏过程中不断进行物体位置更新，渲染等
     */
    @Override
    public void run() {

        while (flag){
            if(status != GAME_OVER && startSimulate){
                simulateWorld();//模拟物理世界
                // 猪群
                Pig pig;
                if(pigGroup != null){
                    for (int i = 0; i < pigGroup.size(); i++) {
                        pig = pigGroup.get(i);
                        if(pig != null && pig.alive){
                            pig.updatePosition(); //根据物理世界的模拟，同步猪的位置
                        }
                    }
                }

                //材料
                Material material;
                if(materialGroup != null){
                    for (int i = 0; i < materialGroup.size(); i++) {
                        material = materialGroup.get(i);
                        if(material != null && material.alive){
                            material.updatePosition();//根据物理世界的模拟，同步材料的位置
                        }
                    }
                }
                //小鸟
                if(status == GAME_FLYING && curBird != null){
                    curBird.updatePosition();//根据物理世界的模拟，同步小鸟的位置
                    //如果新生成了蓝色的鸟
                    if(curBird.getMyKind() == Bird.Kind.BLUE && isClicked == true){
                        Bird blueBird;
                        for(int i = 0; i < curCreateBirdGroup.size(); i++){
                            blueBird = curCreateBirdGroup.get(i);
                            if(blueBird != null && blueBird.alive){
                                blueBird.updatePosition();
                            }
                        }
                    }
                    //检查游戏状态
                    check();
                }
                try{
                    Thread.sleep(13);
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


    /**
     * 检查游戏状态
     * 即检查小鸟是否越界（死亡）
     */
    private void check(){

        //先判断通过点击新产生的蓝鸟中，有没有已经死掉的
        if(curCreateBirdGroup == null) return;
        for(Bird blueBird: curCreateBirdGroup){
            if(blueBird.x > ui.getScreenW() || blueBird.y < 0 || blueBird.x < 0 || blueBird.isStopped(0.05f)){
                blueBird.alive = false;
                blueBird.body.setActive(false);
                bodyToBeDestroyed.add(blueBird.body);
            }
        }
        //判断弹弓上的鸟有没有死掉
        if(curBird == null || curBird.body == null)
            return;
        if(curBird.x > ui.getScreenW() || curBird.y < 0 || curBird.x < 0 || curBird.isStopped(0.05f)){
            curBird.alive = false;
            curBird.body.setActive(false);
            bodyToBeDestroyed.add(curBird.body);

            checkGame(); // 检查游戏是否结束
        }

    }

    /**
     * 检查游戏是否结束。
     * 如果游戏结束，根据胜利或失败显示相应文字
     * 如果没结束，在弹弓上加入新的鸟
     */
    void checkGame(){
        boolean pigAlive = false, birdAlive = false;
        for(Pig curPig : pigGroup){
            if (curPig.alive) {
                pigAlive = true;
                break;
            }
        }
        if(!pigAlive) { // 游戏胜利
            ui.gameOver(true);

            status = GAME_OVER;
        }
        else{
            curBirdIndex++;
            birdAlive = curBirdIndex < birdGroup.size();
            if(!birdAlive){ // 游戏失败
                ui.gameOver(false);
                status = GAME_OVER;
            }
            else{ // 新鸟上弓箭
                //判断上一局是否有新创建的蓝鸟未被清理
                for(Bird blueBird: curCreateBirdGroup){
                    if(blueBird.alive == true){
                        blueBird.alive = false;
                        blueBird.body.setActive(false);
                        bodyToBeDestroyed.add(blueBird.body);
                    }

                }
                curCreateBirdGroup.clear();
                newTurn(curBirdIndex);
            }
        }
    }

    /**
     * 新的一轮，把新的鸟放到弹弓上
     * @param index 要放到弹弓上的鸟的编号
     */
    synchronized void newTurn(int index){

        curBird = birdGroup.get(index);
        ui.putOnSlingshot(curBird, this); // 上弓箭
        status = GAME_READY;

    }

    @Override
    public void destroyPerformed() {

        for(Body b : this.bodyToBeDestroyed){
            world.destroyBody(b);
        }
        flag = false;
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
        //先得到两个碰撞对象的刚体
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();
        boolean pigIsDie = false;
        boolean materialIsDie = false;
        Body diedPigBody = null;
        Body diedMaterialBody = null;
        //判断碰撞是否会把猪打死
        if(bodyA.m_userData instanceof Pig && !(bodyB.m_userData instanceof Pig)){
            //如果bodyA是一个猪，bodyB不是猪
            pigIsDie = judgePigDie(bodyA, bodyB);
            diedPigBody = bodyA;
        }
        else if(bodyB.m_userData instanceof Pig && !(bodyA.m_userData instanceof Pig)){
            pigIsDie = judgePigDie(bodyB, bodyA);
            diedPigBody = bodyB;
        }
        //判断鸟和材料的碰撞，是否会击穿材料
        else if(bodyA.m_userData instanceof Bird && bodyB.m_userData instanceof Material){
            materialIsDie = judgeMaterialDie(bodyA, bodyB);
            diedMaterialBody = bodyB;
        }
        else if(bodyA.m_userData instanceof Material && bodyB.m_userData instanceof Bird){
            materialIsDie = judgeMaterialDie(bodyB, bodyA);
            diedMaterialBody = bodyA;

        }
        else{
            return;
        }
        if(pigIsDie && diedPigBody != null){//如果猪死了
            BGM.playPigDie();
            Pig pig = (Pig)diedPigBody.m_userData;
            pig.alive = false;

            diedPigBody.setActive(false);
            world.destroyBody(diedPigBody);
            bodyToBeDestroyed.add(diedPigBody);
        }
        if(materialIsDie && diedMaterialBody != null){//如果材料被击穿了
            BGM.playCollision();
            Material material = (Material) diedMaterialBody.m_userData;
            material.alive = false;
            diedMaterialBody.setActive(false);
            world.destroyBody(diedMaterialBody);
            bodyToBeDestroyed.add(diedMaterialBody);
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
     * @return 猪是否死亡
     */
    private boolean judgePigDie(Body pigBody, Body otherBody){
        Vec2 pigVel = pigBody.getLinearVelocity();
        Vec2 otherVel = otherBody.getLinearVelocity();
        Vec2 relativeVel = pigVel.sub(otherVel);//计算相对速度

        //如果相对速度大于死亡速度,就死了
        return relativeVel.length() > PIG_DIE_SPEED;
    }

    /**
     * 判断猪和其它物体碰撞时是否死亡
     * @param birdBody 鸟的刚体
     * @param materialBody 材料刚体
     * @return 材料是否被击穿
     */
    private boolean judgeMaterialDie(Body birdBody, Body materialBody){
        //石头是无法被击穿的
        Material mBody = (Material) materialBody.m_userData;
        if(mBody.getMyKind() == Material.Kind.STONE){
            return false;
        }
       //木头和冰可以被击穿
        Vec2 birdVel = birdBody.getLinearVelocity();
        Vec2 materialVel = materialBody.getLinearVelocity();
        Vec2 relativeVel = birdVel.sub(materialVel);//计算相对速度
        //小鸟和木头等碰撞完，会减速
        Vec2 newVel = birdVel.mul(0.75f);
        birdBody.setLinearVelocity(newVel);

        return relativeVel.length() > MATERIAL_DIE_SPEED;
    }
}

