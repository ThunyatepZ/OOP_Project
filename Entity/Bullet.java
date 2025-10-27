package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import MainGame.GamePanle;
public class Bullet {
    public GamePanle gp;

    public int WorldX, WorldY;   // ตำแหน่ง world
    public int speed = 20;        // ความเร็ว px/เฟรม
    public int dx = 0, dy = 0;   // ทิศทางหน่วย (-1,0,1)
    public int size = 20;         // ขนาดกระสุน
    public boolean alive = true;
    public int damage = 0; // ยังอยู่ไหม
    private Player player;
    public Bullet(GamePanle gp, int startX, int startY, int dx, int dy,int damage) {
        this.gp = gp;
        this.WorldX = startX;
        this.WorldY = startY;
        this.dx = dx;
        this.dy = dy;
        this.damage = damage;
    }

    public void update() {
        if (!alive) return;

        // เคลื่อนที่
        WorldX += dx * speed;
        WorldY += dy * speed;

        // ชน tile โดยดูที่จุดกลางกระสุน
        int cx = WorldX + size / 2;
        int cy = WorldY + size / 2;
        int col = cx / gp.titlesize;
        int row = cy / gp.titlesize;

        // หลุดโลก = ตาย
        if (col < 0 || row < 0 || col >= gp.maxWorldCol || row >= gp.maxWorldRow) {
            alive = false;
            return;
        }

        int tileNum = gp.tileM.mapTileNum[col][row];
        if (gp.tileM.tile[tileNum].collision) {
            alive = false;
            return;
        }

        // ชนศัตรูตัวใดตัวหนึ่ง
        Rectangle bulletRect = new Rectangle(WorldX, WorldY, size, size);
        for (Enemy e : gp.enemies) {
            if (!e.alive) continue;
            Rectangle enemyRect = new Rectangle(e.WorldX, e.WorldY, e.size, e.size);
            if (bulletRect.intersects(enemyRect)) {
                e.alive = false; // โดนแล้วตายทันที (ปรับเป็นลด HP ได้)
                alive = false;
                break;
            }
        }
    }

    public void draw(Graphics2D g2) {
        if (!alive) return;

        int screenX = WorldX - gp.player1.WorldX + gp.player1.screenX;
        int screenY = WorldY - gp.player1.WorldY + gp.player1.screenY;

        g2.setColor(Color.RED);
        g2.fillOval(screenX, screenY, size, size);
    }
}
