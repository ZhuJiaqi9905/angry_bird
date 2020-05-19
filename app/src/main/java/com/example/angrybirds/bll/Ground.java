package com.example.angrybirds.bll;

import android.content.Context;
import android.graphics.BitmapFactory;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class Ground extends BasicBody {

    private float width,height;
    Ground(float x, float y, float width, float height, Context context) {
        super(x, y, 0); // 大地的位置
        this.width = width * 2;
        this.height = height;
        characterfixdef.friction = 0.7f;
        characterfixdef.density = 0f;

        characterfixdef.filter.groupIndex = 1;
    }

    public Body createGroundBody(World world, float RATE) {
        PolygonShape gshape = new PolygonShape();
        gshape.setAsBox(width / RATE, height / 2 /RATE);
        characterfixdef.shape = gshape;

        setPosition(new Vec2((x)/RATE, (y)/RATE) );
        characterfixdef.density = 0f;
        characterdef.type = BodyType.STATIC;
        Body ground = world.createBody(characterdef);
        ground.m_userData = this;
        ground.createFixture(characterfixdef);
        return ground;
    }
}
