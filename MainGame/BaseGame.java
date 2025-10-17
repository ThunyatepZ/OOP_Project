package MainGame;
// import java.awt.*;
import javax.swing.JFrame;
public class BaseGame {

    public static void main(String[] args){
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("2D Game");
        GamePanle gamePanle = new GamePanle();
        window.add(gamePanle);
        window.pack();
        window.setLocationRelativeTo(null);

        gamePanle.startGameThread();
        window.setVisible(true);
    }
}
