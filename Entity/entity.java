package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Entity {
    // พิกัดใน world
    public int WorldX, WorldY;

    // speed
    public int speed;

    // ทิศทาง
    public String directions = " ";
    public BufferedImage up,down,left,right;

    // ชน/ไม่ชน
    public boolean collisionOn = false;

    // hitbox
    public Rectangle solidArea;

    // สถานะ
    public boolean alive = true;

    //Methods
    public void setDefaultValues() {}
    public void  update() {}
    public void draw(Graphics2D g2) {}


    
}
