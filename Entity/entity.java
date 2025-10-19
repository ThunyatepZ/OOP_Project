package entity;

import java.awt.Rectangle;
public class Entity {
    public int WorldX, WorldY, speed;
    public Rectangle solidArea;
    public String directions = "";
    public boolean collisionOn = false;
}
