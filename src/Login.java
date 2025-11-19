import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Login extends JFrame {
    private JPanel p1;
    private JTextField textFieldID;
    private JTextField textFieldPassword;
    private JButton loginButton;
    private JButton backButton;
    private ValidationAndHashing v = new ValidationAndHashing();

    public Login() {
        setVisible(true);
        setSize(400,400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Login Menu");
        setLocationRelativeTo(null);
        setContentPane(p1);

        PeopleDB p = new PeopleDB();

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new WelcomeMenu();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = textFieldID.getText();
                if(!v.idExist(id)) {
                    JOptionPane.showMessageDialog(null, "ID Doesnt Exist");
                    return;
                }

                String password = textFieldPassword.getText();
                if(password == null || password.trim().isEmpty()){
                    JOptionPane.showMessageDialog(null,"Please Enter Your Password");
                    return;
                }

                String name = null;
                try {
                    name = v.passwordCheck(password,id);
                } catch (NoSuchAlgorithmException ex) {
                    throw new RuntimeException(ex);
                }

                if(name == null){
                    JOptionPane.showMessageDialog(null,"Incorrect Password");
                    return;
                }

                if(id.toUpperCase().charAt(0)=='S'){
                    JOptionPane.showMessageDialog(null,"Login Successful\nWelcome " + name);
                    ArrayList<Student> students = p.getStudents();
                    Student s = null;
                    for(Student student:students){
                        if (student.getId().equalsIgnoreCase(id)){
                            s = student;
                            break;
                        }
                    }
                    setVisible(false);
                    new studentDashboard(s);
                }
                else {
                    JOptionPane.showMessageDialog(null,"Login Successful\nWelcome " + name);
                    setVisible(false);
                    PeopleDB peopleDB = new PeopleDB();
                    CourseLessonDB courseLessonDB = new CourseLessonDB();
                    new InstructorDashboardFrame(courseLessonDB,peopleDB,id);
                }
            }
        });
    }
}
