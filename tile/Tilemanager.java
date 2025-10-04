package tile;

import java.awt.Graphics2D;
import java.io.IOException;

import javax.imageio.ImageIO;

import MainGame.GamePanle;
public class Tilemanager {
    GamePanle gp;
    public Tile[] tile;
    
    public Tilemanager(GamePanle gp) {
        this.gp = gp;
        tile = new Tile[10];
        getTileImage();
    }
    public void getTileImage() {
        try {
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/acs/edge.png"));
            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/acs/floor.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
    int tileSize = gp.titlesize;

    for (int y = 0; y < gp.getHeight(); y += tileSize) {
        for (int x = 0; x < gp.getWidth(); x += tileSize) {
            if (y == 0 || y >= gp.getHeight() - tileSize) {
                g2.drawImage(tile[0].image, x, y, tileSize, tileSize, null);
            }
            else {
                g2.drawImage(tile[1].image, x, y, tileSize, tileSize, null);
            }
        }
    }
    }
}