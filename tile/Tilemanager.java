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
    public int mapTileNum[][];

    public Tilemanager(GamePanle gp) {
        this.gp = gp;
        tile = new Tile[10];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        getTileImage();
        loadMap("/acs/map/worldmap.txt");
    }
    public void getTileImage() {
        try {
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/acs/tile_map/cp.png"));
            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/acs/tile_map/floorReplace.png"));
            tile[2] = new Tile();
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("/acs/tile_map/Wall.png"));
            tile[2].collision = true;
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap(String Pathmap) {
        try{
            InputStream is = getClass().getResourceAsStream(Pathmap);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int col = 0;
            int row = 0;
            while(col < gp.maxWorldCol && row < gp.maxWorldRow){
                String line = br.readLine();
                while(col < gp.maxWorldCol){
                    String numbers[] = line.split(" ");
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;
                    col++;

                }
                if(col == gp.maxWorldCol){
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
        int worldcol = 0;
        int worldrow = 0;

        while(worldcol < gp.maxWorldCol && worldrow < gp.maxWorldRow){
            int tileNum = mapTileNum[worldcol][worldrow];
            int worldx = worldcol * gp.titlesize;
            int worldy = worldrow * gp.titlesize;
            int screensx = worldx - gp.player1.WorldX + gp.player1.screenX;
            int screensy = worldy - gp.player1.WorldY + gp.player1.screenY;
            if(worldx + gp.titlesize > gp.player1.WorldX - gp.player1.screenX &&
                worldx - gp.titlesize < gp.player1.WorldX + gp.player1.screenX &&
                worldy + gp.titlesize > gp.player1.WorldY - gp.player1.screenY &&
                worldy - gp.titlesize < gp.player1.WorldY + gp.player1.screenY){
            g2.drawImage(tile[tileNum].image, screensx, screensy, gp.titlesize, gp.titlesize, null);

            }
            worldcol++;

            if(worldcol == gp.maxWorldCol){
                worldcol = 0;
                worldrow++;
        
            }
        }
    }
}
