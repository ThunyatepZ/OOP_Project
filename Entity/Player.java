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

    public int health = 10;
    public int maxHealth = 10;
    public int score = 0;
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
        WorldX = gp.titlesize * 23;
        WorldY = gp.titlesize * 35;
        speed = 10;
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


    // ยิงปืน
    private void tryShoot() {
        long now = System.currentTimeMillis();
        if (now - lastShotTime < shotCooldown) return;
        lastShotTime = now;

        int bx = WorldX + gp.titlesize/2 - 4;
        int by = WorldY + gp.titlesize/2 - 4;

        int dirX = 0, dirY = 0;
        switch (directions) {
            case "up" -> dirY = -1;
            case "down" -> dirY = 1;
            case "left" -> dirX = -1;
            case "right" -> dirX = 1;
        }
        if (dirX == 0 && dirY == 0) return;

        Bullet b = new Bullet(gp, bx, by, dirX, dirY,7);
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

    }
}
