package entity;

import java.awt.Color;
import java.awt.Rectangle;

import MainGame.GamePanle;
import MainGame.KeyEventHandler;

public class Player extends Entity {
    public long lastHitTime = 0; // เวลาโดนล่าสุด
    public int invincibleTime = 1000; // 1 วินาที (หน่วย ms)

    GamePanle gp;
    KeyEventHandler keyH;
    public int health = 5;
    public int maxHealth = 5;
    public boolean alive = true;
    public final int screenX;
    public final int screenY;
    int facing = 0;

    public boolean stapping = false;

    public Player(GamePanle gp, KeyEventHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        screenX = gp.Widthscreen / 2 - (gp.titlesize / 2);
        screenY = gp.Hightscreen / 2 - (gp.titlesize / 2);

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = 32;
        solidArea.height = 32;
        setDefaultValues();
    }

    public void setDefaultValues() {
        WorldX = 100;
        WorldY = 100;
        speed = 5;
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
        boolean moving = false;
        if (keyH.upPressed == 1) {
            directions = "up";
            moving = true;
        } else if (keyH.downPressed == 1) {
            directions = "down";
            moving = true;
        } else if (keyH.leftPressed == 1) {
            directions = "left";
            moving = true;
        } else if (keyH.rightPressed == 1) {
            directions = "right";
            moving = true;
        }

        if (keyH.spacePressed == 1) {
            stapping = true;
        }

        if (moving) {
            collisionOn = false;
            gp.collisionChecker.checkTile(this); // ใช้ directions ที่ไม่ null แล้ว
            if (!collisionOn) {
                switch (directions) {
                    case "up" -> WorldY -= speed;
                    case "down" -> WorldY += speed;
                    case "left" -> WorldX -= speed;
                    case "right" -> WorldX += speed;
                }
            }
        }

    }

    public void draw(java.awt.Graphics2D g2) {
        g2.setColor(Color.white);
        g2.fillRect(screenX, screenY, gp.titlesize, gp.titlesize);
        if (stapping) {
            g2.setColor(Color.red);
            int len = gp.titlesize, thick = gp.titlesize / 4;

            switch (directions) {
                case "up" -> g2.fillRect(screenX + gp.titlesize / 2 - thick / 2,
                        screenY - len,
                        thick, len);
                case "down" -> g2.fillRect(screenX + gp.titlesize / 2 - thick / 2,
                        screenY + gp.titlesize,
                        thick, len);
                case "left" -> g2.fillRect(screenX - len,
                        screenY + gp.titlesize / 2 - thick / 2,
                        len, thick);
                case "right" -> g2.fillRect(screenX + gp.titlesize,
                        screenY + gp.titlesize / 2 - thick / 2,
                        len, thick);
            }
            stapping = false;
        }

    }
}