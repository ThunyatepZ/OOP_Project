package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Entity {
    // พิกัดใน world
    public int WorldX, WorldY;

    // ความเร็วการเคลื่อนที่
    public int speed;

    // ทิศทาง (ใช้ร่วมกับ CollisionCheck และ animation)
    public String directions = " ";
    public BufferedImage up,down,left,right;

    // ชน/ไม่ชน
    public boolean collisionOn = false;

    // hitbox สำหรับชน tile/วัตถุ
    public Rectangle solidArea;

    // สถานะมีชีวิต (ใช้กับ enemy/npс)
    public boolean alive = true;

    // --- เมธอดฐาน: ให้คลาสลูก override ได้ ---
    public void setDefaultValues() {}
    public void update() {}
    public void draw(Graphics2D g2) {}


    
}
