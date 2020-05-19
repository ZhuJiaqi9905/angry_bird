package com.example.angrybirds.ui;

import com.example.angrybirds.bll.Body;
import com.example.angrybirds.bll.ClickListener;
import com.example.angrybirds.bll.CreateListener;
import com.example.angrybirds.bll.DestroyListener;
import com.example.angrybirds.bll.ResumeListener;
import com.example.angrybirds.bll.ShotListener;

public interface UiInterface {
    /**
     * @return 布画的宽度
     */
    int getScreenW();

    /**
     * @return 布画的高度
     */
    int getScreenH();

    /**
     * @return 地面所在直线的纵坐标
     */
    int getGroundY();

    /**
     * 添加一个物品
     * @param body 物品
     */
    void addBody(Body body);

    /**
     * 删除一个物品
     * @param body 物品
     */
    void deleteBody(Body body);

    /**
     * 将一个物品放在弹弓上（不需要再调用addBody）
     * 此时该物品的位置会随着玩家的拖拽而改变，直到玩家松手被发射出去
     * @param listener 当物品被发射出去时会调用listener;
     */
    void putOnSlingshot(Body body, ShotListener listener);

    /**
     * 恢复初始状态
     */
    void resume();

    /**
     * 游戏结束了
     * @param result true为赢 false为输
     */
    void gameOver(boolean result);

    /**
     * 当玩家返回或按下HOME键时调用listener
     */
    void setDestroyListener(DestroyListener listener);

    /**
     * 当玩家按下重新开始游戏按钮时会调用listener
     */
    void setResumeListener(ResumeListener listener);

    /**
     * 当玩家开始游戏或者按下HOME键又返回游戏时会调用listener
     */
    void setCreateListener(CreateListener listener);

    /**
     * 当玩家点击屏幕时会调用listener;
     */
    void setClickListener(ClickListener listener);
}
