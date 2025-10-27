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

    // 🆕 สถานะเริ่มเกม
    private boolean gameStarted = false;

    // ---- ระบบเลเวล ----
    public int level = 1;

    // ---- ระบบคะแนนรวม ----
    public int totalScore = 0; // คะแนนรวมเดียว

    // ---- เอนทิตีหลัก ----
    public Player player1 = new Player(this, keyH);
    public ArrayList<Enemy> enemies = new ArrayList<>();
    public ArrayList<Boss> bosses = new ArrayList<>();
    public ArrayList<Bullet> bullets = new ArrayList<>();
    public ArrayList<EnemyBullet> enemyBullets = new ArrayList<>();

    public CollisionCheck collisionChecker = new CollisionCheck(this);
    public Tilemanager tileM = new Tilemanager(this);

    // ปุ่ม
    private JButton restartBtn;
    private JButton nextLevelBtn;
    private JButton startBtn;

    // สุ่ม
    private final java.util.Random rng = new java.util.Random();

    // ---------- คอนสตรักเตอร์ ----------
    public GamePanle() {
        setPreferredSize(new Dimension(Widthscreen, Hightscreen));
        setBackground(Color.black);
        setDoubleBuffered(true);
        addKeyListener(keyH);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        setLayout(null);

        // 🆕 ปุ่ม Start (โชว์ตั้งแต่เปิดเกมครั้งแรก)
        startBtn = new JButton("Start");
        startBtn.setBounds(Widthscreen / 2 - 60, Hightscreen / 2 - 20, 120, 40);
        startBtn.setFocusable(false);
        startBtn.setVisible(true);
        startBtn.addActionListener(e -> {
            gameStarted = true;
            startBtn.setVisible(false);
            requestFocusInWindow();
            setupLevel(); // สร้างด่านเมื่อเริ่มเกม
        });
        add(startBtn);

        // ปุ่ม Restart
        restartBtn = new JButton("Restart");
        restartBtn.setBounds(Widthscreen / 2 - 60, Hightscreen / 2 + 50, 120, 40);
        restartBtn.setFocusable(false);
        restartBtn.setVisible(false);
        restartBtn.addActionListener(e -> {
            restartGame();
            requestFocusInWindow();
        });
        add(restartBtn);

        // ปุ่ม Next Level
        nextLevelBtn = new JButton("Next Level");
        nextLevelBtn.setBounds(Widthscreen / 2 - 80, Hightscreen / 2 + 10, 160, 40);
        nextLevelBtn.setFocusable(false);
        nextLevelBtn.setVisible(false);
        nextLevelBtn.addActionListener(e -> {
            nextLevel();
            requestFocusInWindow();
        });
        add(nextLevelBtn);

        // ❌ ไม่เรียก setupLevel ที่นี่ เพราะรอให้ผู้เล่นกด Start ก่อน
        // setupLevel();
    }

    // ---------- LOOP ----------
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
                if (remainingTime < 0) remainingTime = 0;
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ---------- GAME LOGIC ----------
    public void update() {
        // 🆕 ถ้ายังไม่เริ่มเกม กด TAB ไม่ทำงาน และไม่อัปเดตอะไร
        if (!gameStarted) return;

        if (keyH.tabPressed == 1) {
            MenuOpen = !MenuOpen;
            keyH.tabPressed = 0;
        }
        if (gameOver) return;

        if (!MenuOpen) {
            player1.update();

            // อัปเดตศัตรูทั่วไป
            for (Enemy e : enemies) {
                if (e.alive) e.update();
            }

            // อัปเดตบอส
            for (Boss b : bosses) {
                if (b.alive) b.update();
            }

            // ตรวจบอสที่ตาย เพิ่มคะแนน
            for (int i = bosses.size() - 1; i >= 0; i--) {
                Boss b = bosses.get(i);
                if (!b.alive) {
                    bosses.remove(i);
                    score += 1000;
                    totalScore += 1000;
                }
            }

            // ตรวจศัตรูทั่วไปที่ตาย เพิ่มคะแนน
            for (int i = enemies.size() - 1; i >= 0; i--) {
                Enemy e = enemies.get(i);
                if (!e.alive) {
                    enemies.remove(i);
                    score += 200;
                    totalScore += 200;
                }
            }

            // อัปเดตลูกกระสุน
            for (int i = 0; i < bullets.size(); i++) {
                Bullet b = bullets.get(i);
                b.update();
                if (!b.alive) { bullets.remove(i); i--; }
            }
            for (int i = 0; i < enemyBullets.size(); i++) {
                EnemyBullet eb = enemyBullets.get(i);
                eb.update();
                if (!eb.alive) { enemyBullets.remove(i); i--; }
            }

            // Game Over
            if (!player1.alive) {
                gameOver = true;
                nextLevelBtn.setVisible(false);
                restartBtn.setVisible(true);
            }

            // ผ่านด่าน
            if (!gameOver && isLevelCleared()) {
                nextLevelBtn.setVisible(true);
            } else {
                nextLevelBtn.setVisible(false);
            }
        }
    }

    // ---------- LEVEL SYSTEM ----------
    private void setupLevel() {
        enemies.clear();
        enemyBullets.clear();
        bullets.clear();
        bosses.clear();

        int enemyCount = level * 3; // ด่านสูงขึ้น ศัตรูมากขึ้น
        for (int i = 0; i < enemyCount; i++) {
            spawnEnemyRandom();
        }

        // บอส 1 ตัวต่อด่าน
        spawnBossRandom();
    }

    private void nextLevel() {
        level++;
        setupLevel();
        nextLevelBtn.setVisible(false);
    }

    private boolean isLevelCleared() {
        for (Enemy e : enemies) if (e.alive) return false;
        for (Boss b : bosses) if (b.alive) return false;
        return true;
    }

    // ---------- สุ่มเกิดศัตรู ----------
    private void spawnEnemyRandom() {
        for (int tries = 0; tries < 40; tries++) {
            int col = rng.nextInt(maxWorldCol);
            int row = rng.nextInt(maxWorldRow);
            int tileId = tileM.mapTileNum[col][row];
            boolean walkable = (tileM.tile[tileId] != null && !tileM.tile[tileId].collision);
            if (!walkable) continue;

            int x = col * titlesize;
            int y = row * titlesize;

            int dx = x - player1.WorldX;
            int dy = y - player1.WorldY;
            int minDist = titlesize * 5;
            if (dx * dx + dy * dy < minDist * minDist) continue;

            enemies.add(new Enemy(this, x, y));
            return;
        }
    }

    private void spawnBossRandom() {
        for (int tries = 0; tries < 40; tries++) {
            int col = rng.nextInt(maxWorldCol);
            int row = rng.nextInt(maxWorldRow);

            int tileId = tileM.mapTileNum[col][row];
            boolean walkable = (tileM.tile[tileId] != null && !tileM.tile[tileId].collision);
            if (!walkable) continue;

            int x = col * titlesize;
            int y = row * titlesize;

            int dx = x - player1.WorldX;
            int dy = y - player1.WorldY;
            int minDist = titlesize * 6;
            if (dx * dx + dy * dy < minDist * minDist) continue;

            bosses.add(new Boss(this, x, y));
            return;
        }
        bosses.add(new Boss(this, Widthscreen / 2, Hightscreen / 2));
    }

    private void restartGame() {
        bullets.clear();
        enemyBullets.clear();
        enemies.clear();
        bosses.clear();

        MenuOpen = false;
        gameOver = false;
        level = 1;
        score = 0;
        totalScore = 0;

        restartBtn.setVisible(false);
        nextLevelBtn.setVisible(false);

        // รีสตาร์ทแล้วถือว่าเริ่มเกมอยู่
        gameStarted = true;

        player1 = new Player(this, keyH);
        setupLevel();
    }

    // ---------- RENDER ----------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // วาดพื้น
        tileM.draw(g2);

        // วาดศัตรูทั่วไป
        for (Enemy e : enemies) if (e.alive) e.draw(g2);

        // วาดบอส
        for (Boss b : bosses) if (b.alive) b.draw(g2);

        // วาดกระสุน
        for (Bullet b : bullets) b.draw(g2);
        for (EnemyBullet eb : enemyBullets) eb.draw(g2);

        // วาดผู้เล่น
        player1.draw(g2);

        // UI ต่าง ๆ แสดงเฉพาะเมื่อเริ่มเกมแล้ว
        if (gameStarted) {
            // แถบเลือดผู้เล่น
            g2.setColor(Color.red);
            int barW = 100, barH = 10;
            int curW = (int) ((player1.health / (double) player1.maxHealth) * barW);
            g2.fillRect(20, 20, curW, barH);
            g2.setColor(Color.white);
            g2.drawRect(20, 20, barW, barH);

            // คะแนน/เลเวล
            g2.setColor(Color.WHITE);
            g2.drawString("Level: " + level, 20, 50);
            g2.drawString("Score: " + totalScore, 20, 70);
        }

        // Overlay: ยังไม่เริ่มเกม
        if (!gameStarted) {
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRect(0, 0, Widthscreen, Hightscreen);
            g2.setColor(Color.white);
            g2.setFont(g2.getFont().deriveFont(28f));
            String msg = "Click 'Start' to Play";
            java.awt.FontMetrics fm = g2.getFontMetrics();
            int msgX = (Widthscreen - fm.stringWidth(msg)) / 2;
            g2.drawString(msg, msgX, Hightscreen / 2 - 40);
        }

        // Game Over
        if (gameOver) {
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, Widthscreen, Hightscreen);
            g2.setColor(Color.white);
            g2.setFont(g2.getFont().deriveFont(36f));
            String msg = "GAME OVER";
            java.awt.FontMetrics fm = g2.getFontMetrics();
            int msgX = (Widthscreen - fm.stringWidth(msg)) / 2;
            g2.drawString(msg, msgX, Hightscreen / 2);
        }

        // ผ่านด่าน (เฉพาะเมื่อเริ่มเกมแล้ว)
        if (gameStarted && !gameOver && isLevelCleared()) {
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, Widthscreen, Hightscreen);
            g2.setColor(Color.white);
            g2.setFont(g2.getFont().deriveFont(28f));
            String msg = "Stage Cleared! Click 'Next Level'";
            java.awt.FontMetrics fm = g2.getFontMetrics();
            int msgX = (Widthscreen - fm.stringWidth(msg)) / 2;
            g2.drawString(msg, msgX, Hightscreen / 2);
        }
    }
}
