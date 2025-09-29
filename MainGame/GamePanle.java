package MainGame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import Entity.Enemy;
import Entity.Player;
import Entity.pistol;   // <- ใช้ชื่อตามที่คุณประกาศไว้ (ตัวพิมพ์เล็ก)

public class GamePanle extends JPanel implements Runnable {
    final int OriginalTitlesize = 16;
    final int scale = 3;
    public int titlesize = OriginalTitlesize * scale;
    final int maxRow = 16;
    final int maxCol = 12;
    final int Widthscreen = titlesize * maxRow;
    final int Hightscreen = maxCol * titlesize;

    public pistol NPistol;        // กระสุน 1 ลูก (ไม่ใช้ ArrayList)
    KeyEventHandler keyH = new KeyEventHandler();
    final int FPS = 60;
    Thread gameThread;

    Player player1 = new Player(this, keyH);
    Enemy enemy1 = new Enemy(this, player1);

    public GamePanle() {
        setPreferredSize(new Dimension(Widthscreen, Hightscreen));
        setBackground(Color.black);
        setDoubleBuffered(true);
        addKeyListener(keyH);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (gameThread != null) {
            double drawInterval = 1000000000.0 / FPS;
            double nextDrawTime = System.nanoTime() + drawInterval;

            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime /= 1_000_000.0;
                if (remainingTime < 0) remainingTime = 0;
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    boolean menuState = false;
    String[] menutext = { "item1", "item2" };
    int indexitem = 0;
    public void update() {

        if (keyH.tabPressed == 1) {
            menuState = !menuState;
            keyH.tabPressed = 0; // ป้องกันการสลับเมนูหลายครั้ง
        }

        if(menuState){
            return;
        }
        player1.update();
        enemy1.update();



        // อัปเดตกระสุนเดียว
        if (NPistol != null) {
            NPistol.update();
            if (!NPistol.alive) {
                NPistol = null;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        player1.draw(g2);
        enemy1.draw(g2);

        // วาดกระสุน
        if (NPistol != null) {
            NPistol.draw(g2);
        }
        if (menuState) {
            g2.setColor(Color.WHITE);
            g2.fillRect(50, 50, 300, 200);
            for (int i = 0; i < menutext.length; i++) {
                if (i == indexitem) {
                    g2.setColor(Color.black);
                } else {
                    g2.setColor(Color.black);
                }
                g2.drawString(menutext[i], 150, 150 + i * 30);
            }
            g2.dispose();
            return; // ไม่ต้องวาดส่วนอื่นๆ เมื่ออยู่ในเมนู
        }
        g2.dispose();
    }
}
