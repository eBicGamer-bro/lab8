import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomeMenu extends JFrame {
    private JPanel p1;
    private JButton loginButton1;
    private JButton signupButton;
    private JButton adminButton; // Make sure Field Name in Designer is exactly "adminButton"

    public WelcomeMenu() {
        setVisible(true);
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Welcome Menu");
        setLocationRelativeTo(null);

        // IMPORTANT: load your GUI panel first
        setContentPane(p1);

        PeopleDB peopleDB = new PeopleDB();
        CourseLessonDB courseDB = new CourseLessonDB();

        // LOGIN button
        loginButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new Login(); // opens your existing Login window
            }
        });

        // SIGNUP button
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new Signup(); // opens your existing Signup window
            }
        });

        // ADMIN button
        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField adminNameField = new JTextField();
                JPasswordField adminPassField = new JPasswordField();
                Object[] message = {
                        "Admin Name:", adminNameField,
                        "Password:", adminPassField
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Admin Login", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String adminName = adminNameField.getText();
                    String adminPass = new String(adminPassField.getPassword());

                    if(adminName.equalsIgnoreCase("hamza") && adminPass.equals("1234")){
                        JOptionPane.showMessageDialog(null, "Admin Login Successful");
                        setVisible(false);
                        new AdminDashboard(); // opens your Admin dashboard
                    } else {
                        JOptionPane.showMessageDialog(null, "Incorrect Admin Credentials");
                    }
                }
            }
        });
    }
}
