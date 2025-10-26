package MainGame;

import javax.swing.JFrame;

public class BaseGame {
    static JFrame window;

    public static void main(String[] args) {
        window = new JFrame("2D Game - Stage 1");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        GamePanle gp = new GamePanle(); // ด่านแรก
        window.add(gp);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        gp.startGameThread();
    }

    // ฟังก์ชันเปลี่ยนไปด่าน 2
}
