package Entity;

import java.awt.Color;
import java.awt.Graphics2D;

import MainGame.GamePanle;

public class pistol extends Weapon {
    GamePanle gp;
    public int x, y;
    public int dx, dy;         // ความเร็วตามแกน X/Y (ทิศ)
    public boolean alive = true;

    public pistol(GamePanle gp, int x, int y, int bulletSpeed, int damage, int dx, int dy) {
        super("Pistol", damage, bulletSpeed);
        this.gp = gp;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    public void update() {
        x += dx;
        y += dy;
        // ออกนอกจอแล้วให้หายไป
        if (x < 0 || y < 0 || x > gp.getWidth() || y > gp.getHeight()) {
            alive = false;
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.yellow);
        g2.fillRect(x, y, 5, 10);
    }
}
