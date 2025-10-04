package Entity;

import java.awt.Color;
import java.awt.Graphics2D;

import MainGame.GamePanle;

public class pistol extends Weapon {
    GamePanle gp;
    public int x, y;
    public int facing;   // ทิศ (0=up,1=down,2=left,3=right)
    public boolean alive = true;

    public pistol(GamePanle gp, int x, int y, int bulletSpeed, int damage, int facing) {
        super("Pistol", damage, bulletSpeed);
        this.gp = gp;
        this.x = x;
        this.y = y;
        this.facing = facing;
    }

    public void update() {
        // เคลื่อนที่ตามทิศ
        if (facing == 0) y -= bulletSpeed;
        else if (facing == 1) y += bulletSpeed;
        else if (facing == 2) x -= bulletSpeed;
        else if (facing == 3) x += bulletSpeed;

        // ออกนอกจอแล้วหายไป
        if (x < 0 || y < 0 || x > gp.getWidth() || y > gp.getHeight()) {
            alive = false;
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.yellow);
        g2.fillRect(x, y, 20, 10);
    }
}
