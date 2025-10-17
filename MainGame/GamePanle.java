package MainGame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;

import Entity.Player;
import Entity.pistol;
import Entity.sideEnemy;
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

    // ---- เอนทิตีหลัก ----
    public Player player1 = new Player(this, keyH);
    public sideEnemy sideenemy1 = new sideEnemy(this, player1, 200, 0);
    public sideEnemy sideenemy2 = new sideEnemy(this, player1, 500, 100);
    public sideEnemy sideenemy3 = new sideEnemy(this, player1, 300, 200);

    public pistol currentBullet; // กระสุนปัจจุบัน

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

    // ยิงกระสุนจากผู้เล่น
    public void shootFromPlayer(int x, int y, int dx, int dy) {
        if (currentBullet != null)
            return;

        int facing;
        int bulletSpeed;
        if (Math.abs(dx) >= Math.abs(dy)) {
            facing = (dx >= 0) ? 3 : 2; // ขวา : ซ้าย
            bulletSpeed = Math.abs(dx);
        } else {
            facing = (dy >= 0) ? 1 : 0; // ลง : ขึ้น
            bulletSpeed = Math.abs(dy);
        }

        currentBullet = new pistol(this, x, y, bulletSpeed, 1, facing);
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
            if (sideenemy1.alive)
                sideenemy1.update();
            if (sideenemy2.alive)
                sideenemy2.update();
            if (sideenemy3.alive)
                sideenemy3.update();

            // อัปเดตกระสุน + ชนกับศัตรู
            if (currentBullet != null) {
                currentBullet.update();
                Rectangle b = new Rectangle(currentBullet.x, currentBullet.y, 5, 10);

                if (sideenemy1.alive && b.intersects(new Rectangle(sideenemy1.x, sideenemy1.y, titlesize, titlesize))) {
                    sideenemy1.alive = false;
                    currentBullet = null;
                } else if (sideenemy2.alive
                        && b.intersects(new Rectangle(sideenemy2.x, sideenemy2.y, titlesize, titlesize))) {
                    sideenemy2.alive = false;
                    currentBullet = null;
                } else if (sideenemy3.alive
                        && b.intersects(new Rectangle(sideenemy3.x, sideenemy3.y, titlesize, titlesize))) {
                    sideenemy3.alive = false;
                    currentBullet = null;
                }

                if (currentBullet != null && !currentBullet.alive)
                    currentBullet = null;
            }

            // ---- ชนผู้เล่นกับศัตรู → ให้ Player จัดการเลือดเอง ----
            Rectangle p = new Rectangle(player1.x, player1.y, titlesize, titlesize);
            if ((sideenemy1.alive && p.intersects(new Rectangle(sideenemy1.x, sideenemy1.y, titlesize, titlesize))) ||
                    (sideenemy2.alive && p.intersects(new Rectangle(sideenemy2.x, sideenemy2.y, titlesize, titlesize)))
                    ||
                    (sideenemy3.alive
                            && p.intersects(new Rectangle(sideenemy3.x, sideenemy3.y, titlesize, titlesize)))) {
                player1.takeDamage(); // ลดเลือดใน Player
            }

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
        if (sideenemy1.alive)
            sideenemy1.draw(g2);
        if (sideenemy2.alive)
            sideenemy2.draw(g2);
        if (sideenemy3.alive)
            sideenemy3.draw(g2);
        if (currentBullet != null)
            currentBullet.draw(g2);
        player1.draw(g2);

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
