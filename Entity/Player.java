package Entity;

import java.awt.Color;

import MainGame.GamePanle;
import MainGame.KeyEventHandler;

public class Player extends Human {
    public long lastHitTime = 0; // เวลาโดนล่าสุด
    public int invincibleTime = 1000; // 1 วินาที (หน่วย ms)

    GamePanle gp;
    KeyEventHandler keyH;
    public Weapon Current_weapon;
    public int health = 5;
    public int maxHealth = 5;
    public boolean alive = true;
    // 0=up, 1=down, 2=left, 3=right
    int facing = 0;

    public Player(GamePanle gp, KeyEventHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        setDefaultValues();
        this.Current_weapon = new Weapon("Pistol", 10, 5);
    }

    public void setDefaultValues() {
        x = 100;
        y = 100;
        speed = 4;
    }

    public void takeDamage() {
        long now = System.currentTimeMillis();
        if (now - lastHitTime < invincibleTime) {
            return; // ⬅️ ยังอยู่ในช่วงอมตะ ไม่โดนซ้ำ
        }

        lastHitTime = now;
        health--;
        System.out.println("Player HP: " + health);

        if (health <= 0) {
            alive = false;
            System.out.println("Player died!");
        }
    }

    public void update() {
        if (keyH.upPressed == 1 && (y - speed) >= 0) {
            y -= speed;
            facing = 0;
        }
        if (keyH.downPressed == 1 && (y + speed) <= gp.getHeight() - gp.titlesize) {
            y += speed;
            facing = 1;
        }
        if (keyH.leftPressed == 1 && (x - speed) >= 0) {
            x -= speed;
            facing = 2;
        }
        if (keyH.rightPressed == 1 && (x + speed) <= gp.getWidth() - gp.titlesize) {
            x += speed;
            facing = 3;
        }

        // ยิงกระสุนตามทิศที่หัน
        if (keyH.spacePressed == 1) {
            int cx = x + gp.titlesize / 2;
            int cy = y + gp.titlesize / 2;

            int bs = Current_weapon.bulletSpeed;
            int dx = 0, dy = 0;
            if (facing == 0)
                dy = -bs;
            else if (facing == 1)
                dy = bs;
            else if (facing == 2)
                dx = -bs;
            else if (facing == 3)
                dx = bs;

            gp.shootFromPlayer(cx, cy, dx, dy); // ยิงจริง

            keyH.spacePressed = 0;
        }
    }

    public void draw(java.awt.Graphics2D g2) {
        g2.setColor(Color.white);
        g2.fillRect(x, y, gp.titlesize, gp.titlesize);
    }
}