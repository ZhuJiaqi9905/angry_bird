package com.example.angrybirds.bll;

import android.content.Context;

import com.example.angrybirds.ui.UiInterface;

import org.jbox2d.dynamics.World;

import java.util.ArrayList;

public class LevelLayout {
    /**
     * 进行每个关卡的布局。
     *
     */
    private volatile ArrayList<Bird> birdList;
    private volatile ArrayList<Pigs> pigList;
    private volatile ArrayList<Material> materialList;
    private UiInterface ui;
    private Context context;
    private World world;
    private final static float RATE = 30; //物理世界和像素转换比例
    public LevelLayout(ArrayList<Bird> birdGroup, ArrayList<Pigs> pigGroup,
                       ArrayList<Material> materialGroup, UiInterface ui,
                       Context context, World world){
        this.birdList = birdGroup;
        this.pigList = pigGroup;
        this.materialList = materialGroup;
        this.ui = ui;
        this.context = context;
        this.world = world;
    }

    public void createLevel(int level){
        switch(level){
            case 1:
                designLevel1();
                break;
            case 2:
                designLevel2();
                break;
            case 3:
                designLevel3();
                break;
            default:

        }
        return;
    }
    // new Add
    // 位置信息
    static class position{
        float curX, curY;
        position(float x, float y){
            curX = x;
            curY = y;
        }
    }

    // new Add
    // @para: curPosition: 光标所处的位置
    // @para: changeX : 是否移到光标的x位置
    // @para: changeY : 是否移动光标的y位置
    public void createBox(position curPosition,boolean changeX, boolean changeY){
        float x = curPosition.curX;
        float y = curPosition.curY;
        Material wood1 = new Material(x, y, 0, context, Material.Kind.WOOD, true);
        //Material wood3 = new Material(x+wood1.getWidth(), y, 0, context, Material.Kind.WOOD, true);
        wood1.createMaterialBody(world, RATE);
        // wood3.createMaterialBody(world, RATE);

        Material stone1 = new Material(x, y-wood1.getHeight(), 0, context, Material.Kind.STONE, false);

        Material wood2 = new Material(x + stone1.getWidth()*0.8f, y, 0, context, Material.Kind.WOOD, true);
        //Material wood4 = new Material(x + stone1.getWidth()*0.9f - wood2.getWidth(), y, 0, context, Material.Kind.WOOD, true);
        stone1.x = (wood1.x + wood2.x)/2;


        wood2.createMaterialBody(world, RATE);
        // wood4.createMaterialBody(world,RATE);
        stone1.createMaterialBody(world, RATE);

        materialList.add(wood1);
        materialList.add(wood2);
        //materialList.add(wood3);
        //materialList.add(wood4);
        materialList.add(stone1);

        ui.addBody(wood1);
        ui.addBody(wood2);
        //ui.addBody(wood3);
        //ui.addBody(wood4);
        ui.addBody(stone1);

        x = (wood1.x + wood2.x)/2f;
        Pigs pig = new Pigs(x, y-wood1.getHeight()-stone1.getHeight(), 0, 1, context);
        pig.createPigBody(world, RATE);
        pigList.add(pig);
        ui.addBody(pig);
        if(changeY) curPosition.curY -= wood1.getHeight() + stone1.getHeight();
        if(changeX) curPosition.curX += stone1.getWidth() + 1.2 * wood1.getWidth();
    }

    public void designLevel1(){
        //创建鸟
        float x = 0;
        float gapX = 0;
        float y = ui.getGroundY();
        //红鸟一个
        Bird curBird = new Bird(x, y, 0, Bird.Kind.RED, context);
        gapX = curBird.getWidth() *1.1f; x += gapX;
        birdList.add(curBird);
        ui.addBody(curBird);
        //蓝鸟一个
        curBird = new Bird(x, y, 0, Bird.Kind.BLUE, context);
        gapX = curBird.getWidth() *1.1f; x += gapX;
        birdList.add(curBird);
        ui.addBody(curBird);
        //黄鸟一个
        curBird = new Bird(x, y, 0, Bird.Kind.YELLOW, context);
        gapX = curBird.getWidth() *1.1f; x += gapX;
        birdList.add(curBird);
        ui.addBody(curBird);
        //白鸟一个
        curBird = new Bird(x, y, 0, Bird.Kind.WHITE, context);
        gapX = curBird.getWidth() *1.1f; x += gapX;
        birdList.add(curBird);
        ui.addBody(curBird);
        //创建野猪和材料。
        //材料的初始角度都是0。布尔变量vertical来判断要创建竖直的还是水平的
        x = ui.getScreenW()/2;
        Material wood1 = new Material(x, y, 0, context, Material.Kind.WOOD, true);
        wood1.createMaterialBody(world, RATE);

        Material stone1 = new Material(x, y-wood1.getHeight(), 0, context, Material.Kind.STONE, false);
        //Material stone2 = new Material(x, y-wood1.getHeight()-stone1.getHeight(), 0, context, Material.Kind.STONE, false);
        Material wood2 = new Material(x + stone1.getWidth()*0.7f, y, 0, context, Material.Kind.WOOD, true);
        stone1.x = (wood1.x + wood2.x)/2;
        //stone2.x = stone1.x;

        wood2.createMaterialBody(world, RATE);
        stone1.createMaterialBody(world, RATE);
        materialList.add(wood1);
        materialList.add(wood2);
        materialList.add(stone1);
        //materialList.add(stone2);
        ui.addBody(wood1);
        ui.addBody(wood2);
        ui.addBody(stone1);
        //ui.addBody(stone2);
        x = (wood1.x + wood2.x)/2f;
        Pigs pig = new Pigs(x, y, 0, 1, context);
        pig.createPigBody(world, RATE);
        pigList.add(pig);
        ui.addBody(pig);
    }
    // new Add
    public void designLevel2(){
        //创建鸟
        float x = 0;
        float gapX = 0;
        float y = ui.getGroundY();
        Bird curBird;
        for(int i=0;i<4;i++){
            curBird = Bird.createRandomBird(x, y, 0, context);
            gapX = curBird.getWidth() *1.1f; x += gapX;
            birdList.add(curBird);
            ui.addBody(curBird);
        }
        // 创建光标
        position curPosition = new position(ui.getScreenW()/3, y);
        //创建野猪和材料。
        //材料的初始角度都是0。布尔变量vertical来判断要创建竖直的还是水平的
        for(int i = 0;i < 3;i++){
            for(int j = 0;j<1;j++){
                createBox(curPosition,false,true);
            }
            createBox(curPosition,true,false);
            curPosition.curY = y;
        }
    }

    // new Add
    public void designLevel3() {
        //创建鸟
        float x = 0;
        float gapX = 0;
        float y = ui.getGroundY();
        Bird curBird;
        for(int i=0;i<4;i++){
            curBird = Bird.createRandomBird(x, y, 0, context);
            gapX = curBird.getWidth() *1.1f; x += gapX;
            birdList.add(curBird);
            ui.addBody(curBird);
        }
        // 创建光标
        position curPosition = new position((float) (ui.getScreenW()/2.5), y);
        //创建野猪和材料。
        //材料的初始角度都是0。布尔变量vertical来判断要创建竖直的还是水平的
        for(int i = 0;i < 3;i++){
            for(int j = 0;j<i + 1;j++){
                createBox(curPosition,false,true);
            }
            createBox(curPosition,true,false);
            curPosition.curY = y;
        }
    }
}
