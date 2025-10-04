package MainGame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle; // ✅ ใช้เช็กชน

import javax.swing.JPanel;

import Entity.Player;
import Entity.pistol;
import Entity.sideEnemy;
import tile.Tilemanager;

public class GamePanle extends JPanel implements Runnable {
    final int OriginalTitlesize = 16;
    final int scale = 3;
    public int titlesize = OriginalTitlesize * scale;
    final int maxRow = 16;
    final int maxCol = 12;
    final int Widthscreen = titlesize * maxRow;
    final int Hightscreen = maxCol * titlesize;

    KeyEventHandler keyH = new KeyEventHandler();
    final int FPS = 60;
    Thread gameThread;

    public Player player1 = new Player(this, keyH);
    public sideEnemy sideenemy1 = new sideEnemy(this, player1, 200, 0);
    public sideEnemy sideenemy2 = new sideEnemy(this, player1, 500, 100);
    public sideEnemy sideenemy3 = new sideEnemy(this, player1, 300, 300);

    Tilemanager tileM = new Tilemanager(this);

    // กระสุนตัวเดียว
    private pistol currentBullet = null;

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
            double drawInterval = 1_000_000_000.0 / FPS;
            double nextDrawTime = System.nanoTime() + drawInterval;

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
        player1.update();

        // อัปเดตศัตรูเฉพาะที่ยังอยู่
        if (sideenemy1.alive){
            sideenemy1.update();
        }
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

            // ลบกระสุนถ้าหลุดจอ
            if (currentBullet != null && !currentBullet.alive)
                currentBullet = null;
        }
    }

    // ยิงกระสุนจาก Player (รับ dx, dy)
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        tileM.draw(g2);

        // วาดตัวละคร/ศัตรู/กระสุน
        player1.draw(g2);
        if (sideenemy1.alive)
            sideenemy1.draw(g2);
        if (sideenemy2.alive)
            sideenemy2.draw(g2);
        if (sideenemy3.alive)
            sideenemy3.draw(g2);

        if (currentBullet != null)
            currentBullet.draw(g2);

        g2.dispose();
    }
}
