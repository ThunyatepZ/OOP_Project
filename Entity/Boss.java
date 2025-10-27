package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import MainGame.GamePanle;

public class Boss extends Entity {
    GamePanle gp;
    public int size;

    BufferedImage up, down, left, right;

    public int hp = 30;
    public int speed = 2;
    public boolean alive = true;

    // ===== เพิ่ม: ระบบยิง =====
    private int shootCooldown = 0;   // นับถอยหลังเฟรม
    private int shootInterval = 60;  // ยิงทุก 60 เฟรม (1 วินาทีถ้า FPS=60)
    private int bulletSpeed = 6;     // ความเร็วกระสุน

    public Boss(GamePanle gp, int startX, int startY) {
        this.gp = gp;
        this.size = gp.titlesize;
        this.WorldX = startX;
        this.WorldY = startY;
        this.solidArea = new Rectangle(8, 16, 32, 32);
        this.directions = "down";
        getEnemyImage();
    }

    public void getEnemyImage() {
        try {
            up    = ImageIO.read(getClass().getResourceAsStream("/acs/Character/Boss.png"));
            down  = ImageIO.read(getClass().getResourceAsStream("/acs/Character/Boss.png"));
            left  = ImageIO.read(getClass().getResourceAsStream("/acs/Character/Boss.png"));
            right = ImageIO.read(getClass().getResourceAsStream("/acs/Character/Boss.png"));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        if (!alive) return;

        // คำนวณตำแหน่งบอสบนจอ
        int screenX = WorldX - gp.player1.WorldX + gp.player1.screenX;
        int screenY = WorldY - gp.player1.WorldY + gp.player1.screenY;

        // นอกจอ → ไม่เดิน ไม่ยิง
        if (screenX < -size || screenX > gp.Widthscreen || screenY < -size || screenY > gp.Hightscreen)
            return;

        // เดินเข้าใกล้ผู้เล่นแบบง่าย
        int playerX = gp.player1.WorldX;
        int playerY = gp.player1.WorldY;

        if (WorldX < playerX) { WorldX += speed; directions = "right"; }
        else if (WorldX > playerX) { WorldX -= speed; directions = "left"; }

        if (WorldY < playerY) { WorldY += speed; directions = "down"; }
        else if (WorldY > playerY) { WorldY -= speed; directions = "up"; }

        // ชนผู้เล่น
        Rectangle bossBox = new Rectangle(WorldX, WorldY, size, size);
        Rectangle playerBox = new Rectangle(playerX, playerY, gp.titlesize, gp.titlesize);
        if (bossBox.intersects(playerBox)) {
            gp.player1.takeDamage();
        }

        // ===== ยิงปืนใส่ผู้เล่น =====
        if (shootCooldown > 0) shootCooldown--;
        if (shootCooldown <= 0) {
            shootAtPlayer();
            shootCooldown = shootInterval; // รีเซ็ตคูลดาวน์
        }


        checkHitByPlayerBullet(bossBox);
    }

    private void shootAtPlayer() {
        // จุดยิงจากกลางตัวบอส
        int startX = WorldX + size / 2;
        int startY = WorldY + size / 2;

        // เวกเตอร์จากบอส → ผู้เล่น
        double dx = (gp.player1.WorldX + gp.titlesize / 2) - startX;
        double dy = (gp.player1.WorldY + gp.titlesize / 2) - startY;

        // ทำให้เป็นเวกเตอร์ความยาว = bulletSpeed
        double len = Math.sqrt(dx*dx + dy*dy);
        if (len == 0) return; // ทับกันพอดีไม่ต้องยิง
        double vx = (dx / len) * bulletSpeed;
        double vy = (dy / len) * bulletSpeed;

        // สร้างกระสุนฝั่งบอสแล้วโยนเข้าลิสต์ของ GamePanle
        gp.enemyBullets.add(new EnemyBullet(gp, startX, startY, vx, vy));
    }

    private void checkHitByPlayerBullet(Rectangle bossBox) {
        if (gp.bullets == null || gp.bullets.isEmpty()) return;

        for (int i = gp.bullets.size() - 1; i >= 0; i--) {
            Bullet b = gp.bullets.get(i);
            Rectangle bBox = new Rectangle(b.WorldX, b.WorldY, 8, 8);
            if (bossBox.intersects(bBox)) {
                gp.bullets.remove(i);
                hp -= b.damage;
                if (hp <= 0) {
                    alive = false;
                    hp = 0;
                    return;
                }
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!alive) return;
        int screenX = WorldX - gp.player1.WorldX + gp.player1.screenX;
        int screenY = WorldY - gp.player1.WorldY + gp.player1.screenY;

        // HP bar
        int barW = gp.titlesize, barH = 6, x = screenX, y = screenY - barH - 4;
        g2.setColor(Color.RED);
        g2.fillRect(x, y, barW, barH);
        g2.setColor(Color.GREEN);
        g2.fillRect(x, y, (int)(barW * (hp / 30f)), barH);

        BufferedImage img = switch (directions) {
            case "up" -> up; case "left" -> left; case "right" -> right; default -> down;
        };
        if (img != null) g2.drawImage(img, screenX, screenY, gp.titlesize, gp.titlesize, null);
        else { g2.setColor(Color.MAGENTA); g2.fillRect(screenX, screenY, size, size); }
    }
}
