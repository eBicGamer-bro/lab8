import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomeMenu extends JFrame {
    private JPanel p1;
    private JButton loginButton1;
    private JButton signupButton;

    public WelcomeMenu() {
        setVisible(true);
        setSize(400,400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Welcome Menu");
        setLocationRelativeTo(null);
        setContentPane(p1);
        loginButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new Login();
            }
        });
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new Signup();
            }
        });
    }
}
