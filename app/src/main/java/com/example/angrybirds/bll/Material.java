package com.example.angrybirds.bll;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.angrybirds.R;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

class Material extends BasicBody {
    public enum Kind{
        ICE, WOOD, STONE;
    }
    private Kind myKind;

    Material(float x, float y, float ang, Context context, Kind kind, boolean vertical) {
        super(x, y, ang);
        myKind = kind;
        if (vertical){
            switch(kind){
                case ICE:
                    ico = BitmapFactory.decodeResource(context.getResources(), R.drawable.ice_t);
                    break;
                case WOOD:
                    ico = BitmapFactory.decodeResource(context.getResources(),R.drawable.wood_t1);
                    break;
                case STONE:
                    ico = BitmapFactory.decodeResource(context.getResources(), R.drawable.stone_t);

            }
        }
        else{
            switch (kind){
                case ICE:
                    ico = BitmapFactory.decodeResource(context.getResources(), R.drawable.ice1);
                    break;
                case WOOD:
                    ico = BitmapFactory.decodeResource(context.getResources(), R.drawable.wood);
                    break;
                case STONE:
                    ico = BitmapFactory.decodeResource(context.getResources(), R.drawable.stone1);
                    break;
                default:
                    break;
            }
        }

        switch (kind){
            case ICE:
                characterfixdef.density = 0.01f;
                characterfixdef.friction = 0.7f;
                break;
            case WOOD:
                characterfixdef.density = 0.02f;
                characterfixdef.friction = 0.7f;
                break;
            case STONE:
                characterfixdef.density = 0.07f;
                characterfixdef.friction = 0.7f;
                break;
            default:
                break;
        }


        this.y -= this.getHeight()/2;

    }


    /**
     * 创建材料刚体
     * @param world 物理世界
     * @param RATE 比例
     */
    public synchronized void createMaterialBody(World world, float RATE){
        float w = getWidth();
        float h = getHeight();


        //Log.d("material", "height"  + h + "width " + w );

        PolygonShape polShape = new PolygonShape();//形状是矩形
        polShape.setAsBox(w/2/RATE, h/2/RATE);

        characterfixdef.shape = polShape;
        characterfixdef.restitution = 0.01f;
        characterfixdef.filter.groupIndex = 1;//设置分组
        setPosition(new Vec2((x)/RATE, (y)/RATE) );

        while(this.body == null){
            this.body = world.createBody(characterdef); //物理世界创造物体
        }
        body.createFixture(characterfixdef);
        body.m_userData = this; //在body中保存材料
        return;
    }
    public Kind getMyKind(){
        return myKind;
    }
    /**
     * 碰撞动作
     */
    @Override
    void actionAfterCrash() {
        super.actionAfterCrash();
    }
}
