package MainGame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyEventHandler implements KeyListener {
    public int upPressed = 0, downPressed = 0, leftPressed = 0, rightPressed = 0;
    public int spacePressed = 0;
    public int tabPressed = 0;
    public int shootPressed = 0; // ปุ่มยิง (F)

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP)
            upPressed = 1;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN)
            downPressed = 1;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT)
            leftPressed = 1;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT)
            rightPressed = 1;

        if (code == KeyEvent.VK_SPACE)
            spacePressed = 1;
        if (code == KeyEvent.VK_TAB)
            tabPressed = 1;

        if (code == KeyEvent.VK_F)
            shootPressed = 1; // ยิง
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP)
            upPressed = 0;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN)
            downPressed = 0;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT)
            leftPressed = 0;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT)
            rightPressed = 0;

        if (code == KeyEvent.VK_SPACE)
            spacePressed = 0;
        if (code == KeyEvent.VK_TAB)
            tabPressed = 0;

        if (code == KeyEvent.VK_F)
            shootPressed = 0; // ปล่อยปุ่มยิง
    }
}
