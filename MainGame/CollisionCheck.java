package MainGame;

import entity.Entity;

public class CollisionCheck {
    GamePanle gp;

    public CollisionCheck(GamePanle gp) {
        this.gp = gp;
    }

    public void checkTile(Entity et) {

        int entityLeftWorldX   = et.WorldX + et.solidArea.x;
        int entityRightWorldX  = et.WorldX + et.solidArea.x + et.solidArea.width;
        int entityTopWorldY    = et.WorldY + et.solidArea.y;
        int entityBottomWorldY = et.WorldY + et.solidArea.y + et.solidArea.height;

        int entityLeftCol   = entityLeftWorldX / gp.titlesize;
        int entityRightCol  = entityRightWorldX / gp.titlesize;
        int entityTopRow    = entityTopWorldY / gp.titlesize;
        int entityBottomRow = entityBottomWorldY / gp.titlesize;

        int tileNum1, tileNum2;

        switch (et.directions) {
            case "up" -> {
                entityTopRow = (entityTopWorldY - et.speed) / gp.titlesize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision)
                    et.collisionOn = true;
            }
            case "down" -> {
                entityBottomRow = (entityBottomWorldY + et.speed) / gp.titlesize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision)
                    et.collisionOn = true;
            }
            case "left" -> {
                entityLeftCol = (entityLeftWorldX - et.speed) / gp.titlesize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision)
                    et.collisionOn = true;
            }
            case "right" -> {
                entityRightCol = (entityRightWorldX + et.speed) / gp.titlesize;
                tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision)
                    et.collisionOn = true;
            }
        }
    }
}
