package MainGame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

import entity.Boss;
import entity.Bullet;
import entity.Enemy;
import entity.EnemyBullet;
import entity.Player;
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

    // โลก
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int WorldWidth = titlesize * maxWorldCol;
    public final int WorldHeight = titlesize * maxWorldRow;

    // ---- ระบบหลัก ----
    KeyEventHandler keyH = new KeyEventHandler();
    final int FPS = 60;
    Thread gameThread;
    public boolean MenuOpen = false;
    public boolean gameOver = false;
    public int score = 0;

    // ---- เอนทิตีหลัก ----
    public Player player1 = new Player(this, keyH);
    public ArrayList<Enemy> enemies = new ArrayList<>();
    public ArrayList<Bullet> bullets = new ArrayList<>();
    public ArrayList<EnemyBullet> enemyBullets = new ArrayList<>();

    public CollisionCheck collisionChecker = new CollisionCheck(this);
    public Tilemanager tileM = new Tilemanager(this);

    // === ปุ่มรีสตาร์ท ===
    private JButton restartBtn;

    // === บอส ===
    private Boss boss;

    // ==== ระบบสุ่มเกิดศัตรูแบบเพิ่มขึ้นเรื่อย ๆ ====
    private final java.util.Random rng = new java.util.Random();
    private int spawnCooldown = 0; // นับถอยหลังเฟรม
    private int spawnInterval = 45; // เกิดทุก ~0.75 วิ (ถ้า FPS=60) ปรับได้
    private boolean bossSpawned = false; // true เมื่อถึง 3000 แล้วสลับเป็นบอส

    public GamePanle() {
        setPreferredSize(new Dimension(Widthscreen, Hightscreen));
        setBackground(Color.black);
        setDoubleBuffered(true);
        addKeyListener(keyH);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        setLayout(null); // ใช้ absolute positioning วางปุ่มเองได้

        // ศัตรูเริ่มต้น (มีนิดหน่อยก่อน)
        enemies.add(new Enemy(this, titlesize * 5, titlesize * 23));
        enemies.add(new Enemy(this, titlesize * 5, titlesize * 25));
        enemies.add(new Enemy(this, titlesize * 40, titlesize * 27));

        // สร้างบอสไว้เลย แต่จะให้เริ่มทำงาน/วาดเมื่อแต้มถึง 3000 เท่านั้น
        boss = new Boss(this, titlesize * 23, titlesize * 25);

        // ปุ่ม Restart
        restartBtn = new JButton("Restart");
        restartBtn.setBounds(Widthscreen / 2 - 60, Hightscreen / 2 + 50, 120, 40);
        restartBtn.setFocusable(false);
        restartBtn.setVisible(false); // ซ่อนไว้ก่อน
        restartBtn.addActionListener(e -> {
            restartGame();
            requestFocusInWindow(); // โฟกัสกลับให้กดคีย์ได้
        });
        add(restartBtn);
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
            return;

        if (!MenuOpen) {
            player1.update();

            // อัปเดตศัตรูที่ยังไม่ตาย
            for (Enemy e : enemies) {
                if (e.alive)
                    e.update();
            }

            // เก็บกวาดศัตรูที่ตาย + เพิ่มคะแนน + (สำคัญ) ใช้การตายเพื่อ
            // "ปลดล็อกให้เกิดเพิ่ม"
            int killedThisFrame = 0;
            for (int i = enemies.size() - 1; i >= 0; i--) {
                Enemy e = enemies.get(i);
                if (!e.alive) {
                    enemies.remove(i);
                    score += 1000; // +100 ต่อศัตรูที่ฆ่าได้
                    killedThisFrame++; // นับจำนวนที่ตายในเฟรมนี้
                }
            }

            // ===== เงื่อนไขเปลี่ยนเป็นบอส =====
            if (!bossSpawned && score >= 3000) {
                // ถึง 3000: ลบศัตรูทั้งหมด แล้วเปิดโหมดบอส
                enemies.clear();
                enemyBullets.clear(); // ล้างกระสุนฝั่งศัตรูด้วย (กันโดนแบบค้าง)
                bossSpawned = true;
            }

            // ===== ถ้ายังไม่ถึง 3000: สุ่มเกิดศัตรูเพิ่ม "เรื่อย ๆ เมื่อฆ่าตาย" =====
            if (!bossSpawned) {
                // จำนวนศัตรูที่ "ควรมี" ตามคะแนน: เริ่ม 1 ตัว + เพิ่มขึ้นเรื่อย ๆ
                // ตามจำนวนที่ฆ่า (score/100)
                int desiredAlive = 1 + (score / 100); // ฆ่ามากขึ้น → อนุญาตจำนวนมากขึ้น
                // กันไม่ให้บานปลายเกินไป (ตั้งเพดาน—ปรับได้)
                if (desiredAlive > 8)
                    desiredAlive = 8;

                int aliveNow = countAliveEnemies();

                // ใช้คูลดาวน์กันเกิดถี่เกินไป
                if (spawnCooldown > 0)
                    spawnCooldown--;
                while (aliveNow < desiredAlive && spawnCooldown <= 0) {
                    spawnEnemyRandom(); // เกิด 1 ตัว
                    spawnCooldown = spawnInterval;
                    aliveNow = countAliveEnemies();
                }
            }

            // ===== อัปเดตบอส (เฉพาะหลังแต้มถึง 3000) =====
            if (bossSpawned && boss != null && boss.alive) {
                boss.update();
            }

            // อัปเดตกระสุน + เก็บกวาด
            for (int i = 0; i < bullets.size(); i++) {
                Bullet b = bullets.get(i);
                b.update();
                if (!b.alive) {
                    bullets.remove(i);
                    i--;
                }
            }
            for (int i = 0; i < enemyBullets.size(); i++) {
                EnemyBullet eb = enemyBullets.get(i);
                eb.update();
                if (!eb.alive) {
                    enemyBullets.remove(i);
                    i--;
                }
            }

            // ตรวจ Game Over
            if (!player1.alive) {
                gameOver = true;
                restartBtn.setVisible(true); // แสดงปุ่มตอน Game Over
            }
        }
    }

    private int countAliveEnemies() {
        int c = 0;
        for (Enemy e : enemies)
            if (e.alive)
                c++;
        return c;
    }

    private void spawnEnemyRandom() {
        // ลองสุ่มหลายครั้งเพื่อหาไทล์ "เดินได้" และไม่ชิดผู้เล่นเกินไป
        for (int tries = 0; tries < 20; tries++) {
            int col = rng.nextInt(maxWorldCol); // 0..49
            int row = rng.nextInt(maxWorldRow); // 0..49

            int tileId = tileM.mapTileNum[col][row];
            boolean walkable = (tileM.tile[tileId] != null && !tileM.tile[tileId].collision);
            if (!walkable)
                continue;

            int x = col * titlesize;
            int y = row * titlesize;

            // กัน spawn ติดผู้เล่นเกินไป (อย่างน้อย 6 ช่อง)
            int dx = x - player1.WorldX;
            int dy = y - player1.WorldY;
            int minDist = titlesize * 6;
            if (dx * dx + dy * dy < minDist * minDist)
                continue;

            enemies.add(new Enemy(this, x, y));
            return; // สำเร็จ สร้างได้แล้ว ออกเลย
        }
        // ไม่เจอที่เกิดในรอบนี้ ก็ข้ามไป
    }

    private void restartGame() {
        // รีเซ็ตสถานะใหม่ทั้งหมด
        bullets.clear();
        enemyBullets.clear();
        enemies.clear();
        MenuOpen = false;
        gameOver = false;
        score = 0;
        bossSpawned = false;
        spawnCooldown = 0;

        restartBtn.setVisible(false);

        // สร้างผู้เล่นใหม่
        player1 = new Player(this, keyH);

        // เติมศัตรูเริ่มต้นนิดหน่อย
        enemies.add(new Enemy(this, titlesize * 5, titlesize * 24));
        enemies.add(new Enemy(this, titlesize * 5, titlesize * 25));
        enemies.add(new Enemy(this, titlesize * 5, titlesize * 26));

        boss = new Boss(this, titlesize * 23, titlesize * 25);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (MenuOpen) {
            g2.setColor(Color.black);
            g2.fillRect(0, 0, Widthscreen, Hightscreen);
            g2.setColor(Color.white);
            g2.drawRect(100, 100, Widthscreen - 200, Hightscreen - 200);
            g2.drawString("Paused - press Tab to resume", 260, 250);
            return;
        }

        // วาดพื้น
        tileM.draw(g2);

        // วาดศัตรู (เฉพาะก่อนถึง 3000)
        if (!bossSpawned) {
            for (Enemy e : enemies) {
                if (e.alive)
                    e.draw(g2);
            }
        }

        // วาดบอส (หลังถึง 3000)
        if (bossSpawned && boss != null && boss.alive) {
            boss.draw(g2);
        }

        // วาดกระสุน
        for (Bullet b : bullets) {
            b.draw(g2);
        }
        for (EnemyBullet eb : enemyBullets) {
            eb.draw(g2);
        }

        // วาดผู้เล่น
        player1.draw(g2);

        // แถบเลือดผู้เล่น + คะแนน
        g2.setColor(Color.red);
        int barW = 100, barH = 10;
        int curW = (int) ((player1.health / (double) player1.maxHealth) * barW);
        g2.fillRect(20, 20, curW, barH);
        g2.setColor(Color.white);
        g2.drawRect(20, 20, barW, barH);
        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + score, 20, 40);

        // ชนะเมื่อบอสตาย (หลังเข้าสู่โหมดบอสแล้ว)
        if (bossSpawned && boss != null && !boss.alive) {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, Widthscreen, Hightscreen);

            g2.setColor(Color.white);
            g2.setFont(g2.getFont().deriveFont(36f));

            String msg = "YOU WIN!";
            String yScore = "Final Score: " + score;

            java.awt.FontMetrics fm = g2.getFontMetrics();

            int msgX = (Widthscreen - fm.stringWidth(msg)) / 2;
            int msgY = Hightscreen / 2; // กึ่งกลางแนวตั้งพอดี
            g2.drawString(msg, msgX, msgY);

            int scoreX = (Widthscreen - fm.stringWidth(yScore)) / 2;
            int scoreY = msgY + fm.getHeight(); // อยู่ใต้ลงมา 1 บรรทัด
            g2.drawString(yScore, scoreX, scoreY);
            return;
        }

        if (gameOver) {
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, Widthscreen, Hightscreen);

            g2.setColor(Color.white);
            g2.setFont(g2.getFont().deriveFont(36f));

            String msg = "GAME OVER";
            String yScore = "Final Score: " + score;

            java.awt.FontMetrics fm = g2.getFontMetrics();

            int msgX = (Widthscreen - fm.stringWidth(msg)) / 2;
            int msgY = Hightscreen / 2;
            g2.drawString(msg, msgX, msgY);

            int scoreX = (Widthscreen - fm.stringWidth(yScore)) / 2;
            int scoreY = msgY + fm.getHeight();
            g2.drawString(yScore, scoreX, scoreY);
        }

    }
}
