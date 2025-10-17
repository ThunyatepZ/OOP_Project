package tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import MainGame.GamePanle;
public class Tilemanager {
    GamePanle gp;
    public Tile[] tile;
    int mapTileNum[][];

    public Tilemanager(GamePanle gp) {
        this.gp = gp;
        tile = new Tile[10];
        mapTileNum = new int[gp.maxCol][gp.maxRow];
        getTileImage();
        loadMap();
    }
    public void getTileImage() {
        try {
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/acs/tile_map/edge.png"));
            tile[0].collision = true;
            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/acs/tile_map/floorReplace.png"));
            tile[2] = new Tile();
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("/acs/tile_map/Wall.png"));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap() {
        try{
            InputStream is = getClass().getResourceAsStream("/acs/map/map1.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int col = 0;
            int row = 0;
            while(col < gp.maxCol && row < gp.maxRow){
                String line = br.readLine();
                while(col < gp.maxCol){
                    String numbers[] = line.split(" ");
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;
                    col++;

                }
                if(col == gp.maxCol){
                    col = 0;
                    row++;
                }
                
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void draw(Graphics2D g2){
        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;

        while(col < gp.maxCol && row < gp.maxRow){
            int tileNum = mapTileNum[col][row];
            g2.drawImage(tile[tileNum].image, x, y, gp.titlesize, gp.titlesize, null);
            col++;
            x += gp.titlesize;
            if(col == gp.maxCol){
                col = 0;
                x = 0;
                row++;
                y += gp.titlesize;
            }
        }
    }
}
