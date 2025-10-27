package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import MainGame.GamePanle;

public class Enemy extends Entity {
    GamePanle gp;
    public int size;

    BufferedImage up, down, left, right;

    public Enemy(GamePanle gp, int startX, int startY) {
        this.gp = gp;
        this.size = gp.titlesize;

        this.WorldX = startX;
        this.WorldY = startY;

        this.solidArea = new Rectangle(8, 16, 32, 32);
        this.speed = 2;
        this.directions = "down";
        this.alive = true;

        getEnemyImage();
    }

    public void getEnemyImage() {
        try {
            up    = ImageIO.read(getClass().getResourceAsStream("/acs/Character/Ene.png"));
            down  = ImageIO.read(getClass().getResourceAsStream("/acs/Character/Ene.png"));
            left  = ImageIO.read(getClass().getResourceAsStream("/acs/Character/Ene.png"));
            right = ImageIO.read(getClass().getResourceAsStream("/acs/Character/Ene.png"));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        if (!alive) return;

        int px = gp.player1.WorldX;
        int py = gp.player1.WorldY;

        // เดินไล่ผู้เล่นทีละแกน + เช็คชน tile
        if (WorldX < px) {
            directions = "right";
            collisionOn = false;
            gp.collisionChecker.checkTile(this);
            if (!collisionOn) WorldX += speed;
        } else if (WorldX > px) {
            directions = "left";
            collisionOn = false;
            gp.collisionChecker.checkTile(this);
            if (!collisionOn) WorldX -= speed;
        }

        if (WorldY < py) {
            directions = "down";
            collisionOn = false;
            gp.collisionChecker.checkTile(this);
            if (!collisionOn) WorldY += speed;
        } else if (WorldY > py) {
            directions = "up";
            collisionOn = false;
            gp.collisionChecker.checkTile(this);
            if (!collisionOn) WorldY -= speed;
        }

        // ชนผู้เล่น → ทำดาเมจ
        Rectangle enemyRect  = new Rectangle(WorldX, WorldY, size, size);
        Rectangle playerRect = new Rectangle(px, py, gp.titlesize, gp.titlesize);
        if (enemyRect.intersects(playerRect)) {
            gp.player1.takeDamage();
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!alive) return;

        int screenX = WorldX - gp.player1.WorldX + gp.player1.screenX;
        int screenY = WorldY - gp.player1.WorldY + gp.player1.screenY;

        BufferedImage image = switch (directions) {
            case "up"    -> up;
            case "down"  -> down;
            case "left"  -> left;
            default      -> right;
        };

        if (image != null) {
            g2.drawImage(image, screenX, screenY, gp.titlesize, gp.titlesize, null);
        } else {
            g2.setColor(Color.BLACK);
            g2.fillRect(screenX, screenY, size, size);
        }
    }
}
