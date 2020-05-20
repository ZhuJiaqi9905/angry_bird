package com.example.angrybirds.bll;

public interface ShotListener {
    /**
     * body 从弹弓上被发射出去
     * @param body 通过body获得发射点位置
     * @param x body初始的位置横坐标
     * @param y body初始的位置纵坐标
     */
    void shotPerformed(BasicBody body, float x, float y);
}
