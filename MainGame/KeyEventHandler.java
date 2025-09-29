package MainGame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
public class KeyEventHandler implements KeyListener{

    public int upPressed, downPressed, leftPressed, rightPressed,spacePressed,wep1,wep2,wep3 = 0;
    public int tabPressed, enterPressed; // ← เพิ่ม
    @Override
    public void keyTyped(java.awt.event.KeyEvent e) {

        
    }

    @Override
    public void keyPressed(java.awt.event.KeyEvent e) {

        int ASCII = e.getKeyCode();
        if(ASCII == KeyEvent.VK_W){
            upPressed = 1;
        }
        if(ASCII == KeyEvent.VK_A){
            System.out.println("A");
            leftPressed = 1;
        }
        if(ASCII == KeyEvent.VK_S){
            System.out.println("S");
            downPressed = 1;
        }
        if(ASCII == KeyEvent.VK_D){
            System.out.println("D");
            rightPressed = 1;
        }
        if(ASCII == KeyEvent.VK_SPACE){
            System.out.println("SPACE");
            spacePressed = 1;
        }

        if (ASCII == KeyEvent.VK_P)   tabPressed = 1;    // ← เพิ่ม
        if (ASCII == KeyEvent.VK_ENTER) enterPressed = 1;  // ← เพิ่ม
    }

    @Override
    public void keyReleased(java.awt.event.KeyEvent e) {
        int ASCII = e.getKeyCode();
        if(ASCII == KeyEvent.VK_W){
            upPressed = 0;
        }
        if(ASCII == KeyEvent.VK_A){
            System.out.println("A");
            leftPressed = 0;
        }
        if(ASCII == KeyEvent.VK_S){
            System.out.println("S");
            downPressed = 0;
        }
        if(ASCII == KeyEvent.VK_D){
            System.out.println("D");
            rightPressed = 0;
        }
        if(ASCII == KeyEvent.VK_SPACE){
            System.out.println("SPACE");
            spacePressed = 0;
        }
        if (ASCII == KeyEvent.VK_P)   tabPressed = 0;;     // ← เพิ่ม
        if (ASCII == KeyEvent.VK_ENTER) enterPressed = 0;   // ← เพิ่ม
        
    }
}