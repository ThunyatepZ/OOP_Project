package MainGame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import entity.Player;
import entity.obstacle;
import tile.Tilemanager;

public class GamePanle extends JPanel implements Runnable {
    // ---- หน้าจอ ----
    final int OriginalTitlesize = 16;
    final int scale = 3;
    public int titlesize = OriginalTitlesize * scale;
    public final int maxCol = 16;
    public final int maxRow = 12;
    public final int Widthscreen = titlesize * maxCol;
    public final int Hightscreen = titlesize * maxRow;

    // ---- ระบบหลัก ----
    KeyEventHandler keyH = new KeyEventHandler();
    final int FPS = 60;
    Thread gameThread;
    public Boolean MenuOpen = false;
    public boolean gameOver = false;

    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int WorldWidth = titlesize * maxWorldCol;
    public final int WorldHeight = titlesize * maxWorldRow;
    // ---- เอนทิตีหลัก ----
    public Player player1 = new Player(this, keyH);
    public obstacle Obs1 = new obstacle(this,0,500);
    public obstacle Obs2 = new obstacle(this,0,700);
    public obstacle Obs3 = new obstacle(this,0,1000);
    public CollisionCheck collisionChecker = new CollisionCheck(this);


    Tilemanager tileM = new Tilemanager(this);

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
        double drawInterval = 1_000_000_000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime /= 1_000_000.0;
                if (remainingTime < 0)
                    remainingTime = 0;
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void update() {
        // Toggle เมนู
        if (keyH.tabPressed == 1) {
            MenuOpen = !MenuOpen;
            keyH.tabPressed = 0;
        }

        if (gameOver)
            return; // ถ้า Game Over ไม่อัปเดต

        if (!MenuOpen) {
            // อัปเดตผู้เล่น/ศัตรู
            player1.update();
            Obs1.update();
            // Obs2.update();
            // Obs3.update();

            // ถ้า Player ตาย → Game Over
            if (!player1.alive) {
                gameOver = true;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Pause overlay
        if (MenuOpen) {
            g2.setColor(Color.black);
            g2.fillRect(0, 0, Widthscreen, Hightscreen);
            g2.setColor(Color.white);
            g2.drawRect(100, 100, Widthscreen - 200, Hightscreen - 200);
            g2.drawString("Paused - press Tab to resume", 260, 250);
            g2.dispose();
            return;
        }

        // วาดพื้น, ศัตรู, กระสุน, ผู้เล่น
        tileM.draw(g2);
        player1.draw(g2);
        Obs1.draw(g2);
        // Obs2.draw(g2);
        // Obs3.draw(g2);
        // ---- HUD: แถบเลือดแบบง่าย ----
        g2.setColor(Color.red);
        int barW = 100, barH = 10;
        int curW = (int) ((player1.health / (double) player1.maxHealth) * barW);
        g2.fillRect(20, 20, curW, barH);
        g2.setColor(Color.white);
        g2.drawRect(20, 20, barW, barH);

        // Game Over overlay
        if (gameOver) {
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, Widthscreen, Hightscreen);
            g2.setColor(Color.white);
            g2.setFont(g2.getFont().deriveFont(36f));
            String msg = "GAME OVER";
            int w = g2.getFontMetrics().stringWidth(msg);
            g2.drawString(msg, (Widthscreen - w) / 2, Hightscreen / 2);
        }

        g2.dispose();
    }
}
