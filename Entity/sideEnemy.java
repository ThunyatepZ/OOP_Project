package Entity;

import java.awt.Color;
import java.awt.Graphics2D;

import MainGame.GamePanle;

public class sideEnemy extends Human {
    GamePanle gp;
    public boolean alive = true;
    private boolean movingDown = true; // true = ลง, false = ขึ้น

    public sideEnemy(GamePanle gp , Player player, int startX, int startY) {
        this.gp = gp;
        this.x = startX;
        this.y = startY;
        this.speed = 10;
    }

    public void update() {
        if (movingDown) {
            y += speed;
            if (y >= gp.getHeight() - gp.titlesize) movingDown = false;
        } else {
            y -= speed;
            if (y <= 0) movingDown = true;
        }
    }

    public void draw(Graphics2D g2) {
        if (!alive) return;
        g2.setColor(Color.red);
        g2.fillRect(x, y, gp.titlesize, gp.titlesize);
    }
}
