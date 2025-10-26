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
            up    = ImageIO.read(getClass().getResourceAsStream("/acs/Character/Ghost.png"));
            down  = ImageIO.read(getClass().getResourceAsStream("/acs/Character/GhostRight.png"));
            left  = ImageIO.read(getClass().getResourceAsStream("/acs/Character/GhostRight.png"));
            right = ImageIO.read(getClass().getResourceAsStream("/acs/Character/GhostRight.png"));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        if (!alive) return;

        // ======= เช็กว่า "ศัตรู" อยู่บนจอไหม =======
        // คำนวณตำแหน่งศัตรูบนหน้าจอ (อิงผู้เล่นเป็นกล้อง)
        int screenX = WorldX - gp.player1.WorldX + gp.player1.screenX;
        int screenY = WorldY - gp.player1.WorldY + gp.player1.screenY;

        // ขอบเผื่อ (ศัตรูจะเริ่มทำงานก่อนเข้าเฟรมจริงนิดหน่อย) — ปรับได้
        int margin = 0; // เช่น 64 ถ้าอยากให้เริ่มทำงานก่อนเข้าเฟรม

        boolean onScreen =
            (screenX + size >= -margin) &&
            (screenX <= gp.Widthscreen + margin) &&
            (screenY + size >= -margin) &&
            (screenY <= gp.Hightscreen + margin);

        if (!onScreen) {
            // อยู่นอกจอ → ไม่อัปเดตการเคลื่อนที่/ไล่ตาม
            return;
        }
        // ======= จบเช็กบนจอ =======

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
            g2.setColor(Color.RED);
            g2.fillRect(screenX, screenY, size, size);
        }
    }
}
