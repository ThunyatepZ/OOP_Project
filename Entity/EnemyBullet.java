package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import MainGame.GamePanle;

public class EnemyBullet {
    public int WorldX, WorldY;     // ตำแหน่งในโลก
    private double vx, vy;         // ทิศทาง (หน่วยพิกัดต่อเฟรม)
    private int size = 8;          // ขนาดฮิตบ็อกซ์/วาด
    public boolean alive = true;

    private final GamePanle gp;

    public EnemyBullet(GamePanle gp, int startX, int startY, double vx, double vy) {
        this.gp = gp;
        this.WorldX = startX;
        this.WorldY = startY;
        this.vx = vx;
        this.vy = vy;
    }

    public void update() {
        if (!alive) return;

        // เดินทางตามเวกเตอร์
        WorldX += vx;
        WorldY += vy;

        // ออกจากขอบโลกแบบง่าย ๆ ก็หาย
        if (WorldX < 0 || WorldY < 0 || WorldX > gp.WorldWidth || WorldY > gp.WorldHeight) {
            alive = false;
            return;
        }

        // ชนผู้เล่น → ทำดาเมจ + หาย
        Rectangle bulletBox = new Rectangle(WorldX, WorldY, size, size);
        Rectangle playerBox = new Rectangle(gp.player1.WorldX, gp.player1.WorldY, gp.titlesize, gp.titlesize);
        if (bulletBox.intersects(playerBox)) {
            gp.player1.takeDamage();
            alive = false;
        }
    }

    public void draw(Graphics2D g2) {
        if (!alive) return;
        int screenX = WorldX - gp.player1.WorldX + gp.player1.screenX;
        int screenY = WorldY - gp.player1.WorldY + gp.player1.screenY;

        // วาดเป็นวงกลมสีเหลืองให้ต่างจากกระสุนผู้เล่น
        g2.setColor(Color.YELLOW);
        g2.fillOval(screenX - size/2, screenY - size/2, size, size);
    }
}
