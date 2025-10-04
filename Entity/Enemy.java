package Entity;

import java.awt.Color;
import java.awt.Graphics2D;

import MainGame.GamePanle;

public class Enemy extends Human {
    GamePanle gp;
    Player target;
    public boolean alive = true;

    public Enemy(GamePanle gp, Player target) {
        this.gp = gp;
        this.target = target;
        setDefaultValues();
    }

    public void setDefaultValues() {
        x = 500;
        y = 500;
        speed = 1;
    }
    public void update() {
        int r = gp.titlesize;

        if (target.x > x + r) {
            x += speed;
        } 
        else if (target.x + r < x) {
            x -= speed;
        }

        if (target.y > y + r) {
            y += speed;
        } 
        else if (target.y + r < y) {
            y -= speed;
        }

        // กันไม่ให้ออกนอกจอ
        x = Math.max(0, Math.min(x, gp.getWidth() - r));
        y = Math.max(0, Math.min(y, gp.getHeight() - r));
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.red);
        g2.fillRect(x, y, gp.titlesize, gp.titlesize);
    }
}
