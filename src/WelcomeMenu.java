import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomeMenu extends JFrame {
    private JPanel p1;
    private JButton loginButton1;
    private JButton signupButton;
    private JButton adminButton; // Must match field name in Designer

    public WelcomeMenu() {
        setVisible(true);
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Welcome Menu");
        setLocationRelativeTo(null);
        setContentPane(p1);

        PeopleDB peopleDB = new PeopleDB();
        CourseLessonDB courseDB = new CourseLessonDB();

        // LOGIN button
        loginButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new Login();
            }
        });

        // SIGNUP button
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new Signup();
            }
        });

        // ADMIN button
        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField adminIDField = new JTextField();
                JPasswordField adminPassField = new JPasswordField();
                Object[] message = {
                        "Admin ID:", adminIDField,
                        "Password:", adminPassField
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Admin Login", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String adminID = adminIDField.getText().trim().toUpperCase();
                    String adminPass = new String(adminPassField.getPassword()).trim();

                    if(adminID.isEmpty() || adminPass.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Enter ID and Password");
                        return;
                    }

                    Admin admin = peopleDB.loginAdmin(adminID, adminPass);
                    if(admin != null){
                        JOptionPane.showMessageDialog(null, "Admin Login Successful\nWelcome " + admin.getName());
                        setVisible(false);
                        new AdminDashboard(admin, peopleDB.getCourses());
                    } else {
                        JOptionPane.showMessageDialog(null, "Incorrect Admin ID or Password");
                    }
                }
            }
        });
    }
}
