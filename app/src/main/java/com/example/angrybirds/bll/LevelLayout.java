package com.example.angrybirds.bll;

import android.content.Context;

import com.example.angrybirds.ui.UiInterface;

import org.jbox2d.dynamics.World;

import java.security.Key;
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
    // 位置信息
    static class position{
        float curX, curY;
        position(float x, float y){
            curX = x;
            curY = y;
        }
    }
    // @para: curPosition: 光标所处的位置
    // @para: changeX : 是否移到光标的x位置
    // @para: changeY : 是否移动光标的y位置
    public void createBox(position curPosition,boolean changeX, boolean changeY, boolean hasPig, Material.Kind materKind){
        float x = curPosition.curX;
        float y = curPosition.curY;
        Material wood1 = new Material(x, y, 0, context, Material.Kind.WOOD, true);
        wood1.x += wood1.getWidth() / 2;
        x = wood1.x;
        wood1.createMaterialBody(world, RATE);
        Material stone1 = new Material(x, y-wood1.getHeight(), 0, context, materKind, false);
        Material wood2 = new Material(x + stone1.getWidth()*0.8f, y, 0, context, Material.Kind.WOOD, true);
        stone1.x = (wood1.x + wood2.x)/2;
        wood2.createMaterialBody(world, RATE);
        stone1.createMaterialBody(world, RATE);

        materialList.add(wood1);
        materialList.add(wood2);
        materialList.add(stone1);

        ui.addBody(wood1);
        ui.addBody(wood2);
        ui.addBody(stone1);

        x = (wood1.x + wood2.x)/2f;
        if(hasPig){
            Pigs pig = new Pigs(x, y, 0, 1, context); // -wood1.getHeight()-stone1.getHeight()
            pig.createPigBody(world, RATE);
            pigList.add(pig);
            ui.addBody(pig);
        }
        if(changeY) curPosition.curY -= wood1.getHeight() + stone1.getHeight();
        if(changeX) curPosition.curX += stone1.getWidth() + 1.2 * wood1.getWidth();
    }

    public void createWall(position curPosition,boolean changeX, boolean changeY, Material.Kind materKind){
        float x = curPosition.curX;
        float y = curPosition.curY;
        Material thing;
        for(int i=0;i<2;i++){
            thing = new Material(x, y, 0, context, materKind, true);
            thing.createMaterialBody(world, RATE);
            materialList.add(thing);
            ui.addBody(thing);
            x += thing.getWidth();
        }
        thing = new Material(x, y, 0, context, materKind, true);
        thing.createMaterialBody(world, RATE);
        materialList.add(thing);
        ui.addBody(thing);
        x += thing.getWidth();
        if(changeY) curPosition.curY -= thing.getHeight();
        if(changeX) curPosition.curX += 3.1 *thing.getWidth();
    }
    public void createBand(position curPosition,boolean changeX, boolean changeY, Material.Kind materKind){
        float x = curPosition.curX;
        float y = curPosition.curY;
        Material thing;
        for(int i=0;i<5;i++){
            thing = new Material(x, y, 0, context, materKind, false);
            thing.x += 0.5 * thing.getWidth();
            thing.createMaterialBody(world, RATE);
            materialList.add(thing);
            ui.addBody(thing);
            y -= thing.getHeight();
        }
        thing = new Material(x, y, 0, context, materKind, false);
        thing.x += 0.5 * thing.getWidth();
        thing.createMaterialBody(world, RATE);
        materialList.add(thing);
        ui.addBody(thing);
        x += thing.getWidth();
        if(changeY) curPosition.curY -= 6 *thing.getHeight();
        if(changeX) curPosition.curX += thing.getWidth();
    }

    public void designLevel1(){
        //创建鸟
        float x = 0;
        float gapX = 0;
        float y = ui.getGroundY();
        Bird curBird;
        //红鸟一个
        curBird = new Bird(x, y, 0, Bird.Kind.RED, context);
        gapX = curBird.getWidth() *1.1f; x += gapX;
        birdList.add(curBird);
        ui.addBody(curBird);
        //黄鸟一个
        curBird = new Bird(x, y, 0, Bird.Kind.YELLOW, context);
        gapX = curBird.getWidth() *1.1f; x += gapX;
        birdList.add(curBird);
        ui.addBody(curBird);
        position curPosition = new position(ui.getScreenW()/3, y);
        createWall(curPosition,false,true, Material.Kind.STONE);
        createWall(curPosition,true,false, Material.Kind.STONE);
        curPosition.curY = ui.getGroundY();
        createBand(curPosition,true,false, Material.Kind.STONE);
        curPosition.curY = ui.getGroundY();
        x = curPosition.curX;

        Material stone1 = new Material(x, y, 0, context, Material.Kind.STONE, false);
        stone1.x = x + stone1.getWidth() / 3;
        stone1.y = 7 * stone1.getHeight();
        stone1.createMaterialBody(world, RATE);
        materialList.add(stone1);
        ui.addBody(stone1);
        curPosition.curX = x + stone1.getWidth()*0.8f ;
        createBand(curPosition,true,false, Material.Kind.STONE);
        curPosition.curY = ui.getGroundY();
        createWall(curPosition,false,true, Material.Kind.STONE);
        createWall(curPosition,true,false, Material.Kind.STONE);
        curPosition.curY = ui.getGroundY();

        Pigs pig = new Pigs(x + stone1.getWidth() / 2, y, 0, 1, context);
        pig.createPigBody(world, RATE);
        pigList.add(pig);
        ui.addBody(pig);
    }
    public void designLevel2(){
        //创建鸟
        float x = 0;
        float gapX = 0;
        float y = ui.getGroundY();
        Bird curBird;
        Bird.Kind[]  kindList = {Bird.Kind.BLUE, Bird.Kind.RED, Bird.Kind.WHITE};
        for(int i=0;i<3;i++){
            curBird = new Bird(x, y, 0, kindList[i], context);
            gapX = curBird.getWidth() *1.1f; x += gapX;
            birdList.add(curBird);
            ui.addBody(curBird);
        }
        // 创建光标
        position curPosition = new position(ui.getScreenW()/3, y);
        createWall(curPosition,false,true, Material.Kind.STONE);
        createWall(curPosition,true,false, Material.Kind.STONE);
        curPosition.curY = ui.getGroundY();
        //创建野猪和材料。
        for(int i = 0;i < 3;i++){
            for(int j = 0;j<1;j++){
                createBox(curPosition,false,true,true, Material.Kind.WOOD);
            }
            createBox(curPosition,true,false,true, Material.Kind.STONE);
            curPosition.curY = ui.getGroundY();
        }
        // 墙
        createWall(curPosition,false,true, Material.Kind.STONE);
        createWall(curPosition,true,false, Material.Kind.STONE);
        curPosition.curY = ui.getGroundY();
    }
    public void designLevel3() {
        //创建鸟
        float x = 0;
        float gapX = 0;
        float y = ui.getGroundY();
        Bird curBird;
        Bird.Kind[]  kindList = {Bird.Kind.BLUE, Bird.Kind.WHITE, Bird.Kind.RED};

        for(int i=0;i<2;i++){
            curBird = new Bird(x, y, 0, kindList[i], context);
            gapX = curBird.getWidth() *1.1f; x += gapX;
            birdList.add(curBird);
            ui.addBody(curBird);
        }
        // 创建光标
        position curPosition = new position((float) (ui.getScreenW()/2.5), y);
        //创建野猪和材料。
        for(int i = 0;i < 3;i++){
            createBox(curPosition,false,true,true,Material.Kind.WOOD);
            createBox(curPosition,true,false,true,Material.Kind.STONE);
            curPosition.curY = ui.getScreenH() *0.3f;
            if(i < 2) {
                createWall(curPosition, false, true, Material.Kind.STONE);
                createWall(curPosition, true, false, Material.Kind.STONE);
            }
            curPosition.curY = ui.getGroundY();
        }
    }
}
