package entity;

import java.awt.Color;
import java.awt.Graphics2D;

import MainGame.GamePanle;

public class obstacle extends Entity {
    GamePanle gp;
    int startX;
    boolean movingRight = true;

    public obstacle(GamePanle gp,int setworldx,int setworldy) {
        this.gp = gp;
        setdefValue(setworldx,setworldy);
        startX = WorldX; // จำจุดเริ่ม
    }

    public void setdefValue(int setworldx,int setworldy) {
        WorldX = setworldx;
        WorldY = setworldy;
        speed = 5;
    }

    public void update() {
        if (movingRight) {
            WorldX += speed;
            if (WorldX > startX + gp.titlesize * 7) movingRight = false;
        } else {
            WorldX -= speed;
            if (WorldX < startX) movingRight = true;
        }
    }

    public void draw(Graphics2D g2) {
        int sx = WorldX - gp.player1.WorldX + gp.player1.screenX;
        int sy = WorldY - gp.player1.WorldY + gp.player1.screenY;
        g2.setColor(Color.RED);
        g2.fillRect(sx, sy, gp.titlesize, gp.titlesize /3);
    }
}
