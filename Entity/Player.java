package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import MainGame.GamePanle;
import MainGame.KeyEventHandler;

public class Player extends Entity {
    public long lastHitTime = 0;
    public int invincibleTime = 1000;

    GamePanle gp;
    KeyEventHandler keyH;

    public int health = 5;
    public int maxHealth = 5;

    public final int screenX;
    public final int screenY;

    public boolean stapping = false;

    // ยิงกระสุน
    private long lastShotTime = 0;
    private int shotCooldown = 300; // ms

    // สปไรท์
    BufferedImage up, down, left, right;

    public Player(GamePanle gp, KeyEventHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        screenX = gp.Widthscreen / 2 - (gp.titlesize / 2);
        screenY = gp.Hightscreen / 2 - (gp.titlesize / 2);

        solidArea = new Rectangle(8, 16, 32, 32);
        directions = "up";

        setDefaultValues();
        getPlayerImage();
    }

    public void getPlayerImage(){
        try{
            up    = ImageIO.read(getClass().getResourceAsStream("/acs/Character/PlayerDown.png"));
            down  = ImageIO.read(getClass().getResourceAsStream("/acs/Character/PlayerUp.png"));
            left  = ImageIO.read(getClass().getResourceAsStream("/acs/Character/PlayerLeft.png"));
            right = ImageIO.read(getClass().getResourceAsStream("/acs/Character/PlayerRight.png"));
        }catch(IOException | NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void setDefaultValues() {
        WorldX = 100;
        WorldY = 100;
        speed = 5;
        directions = "down";
        collisionOn = false;
        alive = true;
    }

    public void takeDamage() {
        long now = System.currentTimeMillis();
        if (now - lastHitTime < invincibleTime || !alive) return;

        lastHitTime = now;
        health--;
        if (health <= 0) {
            health = 0;
            alive = false;
        }
    }

    @Override
    public void update() {
        boolean moving = false;

        if (keyH.upPressed == 1)         { directions = "up";    moving = true; }
        else if (keyH.downPressed == 1)  { directions = "down";  moving = true; }
        else if (keyH.leftPressed == 1)  { directions = "left";  moving = true; }
        else if (keyH.rightPressed == 1) { directions = "right"; moving = true; }

        // แทงมีด
        if (keyH.spacePressed == 1) {
            stapping = true;
            doStab();
            keyH.spacePressed = 0;
        }

        // ยิงปืน (ปุ่ม F)
        if (keyH.shootPressed == 1) {
            tryShoot();
            // ถ้าอยากกดยิงทีละครั้งให้ set = 0
            // keyH.shootPressed = 0;
        }

        // เดิน + ชนกำแพงด้วย CollisionCheck เดิม
        if (moving) {
            collisionOn = false;
            gp.collisionChecker.checkTile(this);
            if (!collisionOn) {
                switch (directions) {
                    case "up"    -> WorldY -= speed;
                    case "down"  -> WorldY += speed;
                    case "left"  -> WorldX -= speed;
                    case "right" -> WorldX += speed;
                }
            }
        }
    }

    // แทงมีด
    private void doStab() {
        Rectangle stab = getStabHitbox();

        for (Enemy e : gp.enemies) {
            if (!e.alive) continue;
            Rectangle enemyRect = new Rectangle(e.WorldX, e.WorldY, e.size, e.size);
            if (stab.intersects(enemyRect)) {
                e.alive = false;
            }
        }
    }

    private Rectangle getStabHitbox() {
        int len = gp.titlesize / 2;
        int thick = gp.titlesize / 4;
        int x, y, w, h;

        switch (directions) {
            case "up" -> {
                x = WorldX + gp.titlesize/2 - thick/2;
                y = WorldY - len;
                w = thick; h = len;
            }
            case "down" -> {
                x = WorldX + gp.titlesize/2 - thick/2;
                y = WorldY + gp.titlesize;
                w = thick; h = len;
            }
            case "left" -> {
                x = WorldX - len;
                y = WorldY + gp.titlesize/2 - thick/2;
                w = len; h = thick;
            }
            default /* right */ -> {
                x = WorldX + gp.titlesize;
                y = WorldY + gp.titlesize/2 - thick/2;
                w = len; h = thick;
            }
        }
        return new Rectangle(x, y, w, h);
    }

    // ยิงปืน
    private void tryShoot() {
        long now = System.currentTimeMillis();
        if (now - lastShotTime < shotCooldown) return;
        lastShotTime = now;

        int bx = WorldX + gp.titlesize/2 - 4; // -4 = ครึ่งของ size bullet 8
        int by = WorldY + gp.titlesize/2 - 4;

        int dirX = 0, dirY = 0;
        switch (directions) {
            case "up" -> dirY = -1;
            case "down" -> dirY = 1;
            case "left" -> dirX = -1;
            case "right" -> dirX = 1;
        }
        if (dirX == 0 && dirY == 0) return;

        Bullet b = new Bullet(gp, bx, by, dirX, dirY);
        gp.bullets.add(b);
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage image = switch (directions) {
            case "up" -> up;
            case "down" -> down;
            case "left" -> left;
            default -> right;
        };
        if (image != null) {
            g2.drawImage(image, screenX, screenY, gp.titlesize, gp.titlesize, null);
        } else {
            g2.setColor(Color.WHITE);
            g2.fillRect(screenX, screenY, gp.titlesize, gp.titlesize);
        }

        // วาดมีดแค่ 1 เฟรม
        if (stapping) {
            g2.setColor(Color.GRAY);
            int len = gp.titlesize/2, thick = gp.titlesize / 4;
            switch (directions) {
                case "up"    -> g2.fillRect(screenX + gp.titlesize/2 - thick/2, screenY - len, thick, len);
                case "down"  -> g2.fillRect(screenX + gp.titlesize/2 - thick/2, screenY + gp.titlesize, thick, len);
                case "left"  -> g2.fillRect(screenX - len, screenY + gp.titlesize/2 - thick/2, len, thick);
                case "right" -> g2.fillRect(screenX + gp.titlesize, screenY + gp.titlesize/2 - thick/2, len, thick);
            }
            stapping = false;
        }
    }
}
