import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class App extends JFrame{
    public App() {
        add(new MainPanel(), BorderLayout.CENTER);
        setTitle("UNO");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }
    public static void main(String[] args) {
        new App();
    }
}