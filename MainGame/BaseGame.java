package MainGame;
// import java.awt.*;
import javax.swing.JFrame;
public class BaseGame {
    public static void main(String[] args){
        JFrame window = new JFrame("Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(900,600);
        GamePanle gamePanle = new GamePanle();
        gamePanle.startGameThread();
        window.add(gamePanle);
        window.setVisible(true);
        window.setLocationRelativeTo(null);
        
    }
}
