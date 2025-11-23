import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;

public class Login extends JFrame {
    private JPanel p1;
    private JTextField textFieldID;
    private JTextField textFieldPassword;
    private JButton loginButton;
    private JButton backButton;
    private ValidationAndHashing v = new ValidationAndHashing();

    public Login() {
        setVisible(true);
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Login Menu");
        setLocationRelativeTo(null);
        setContentPane(p1);

        PeopleDB peopleDB = new PeopleDB();
        CourseLessonDB courseDB = new CourseLessonDB();

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
                String id = textFieldID.getText().trim();
                String password = textFieldPassword.getText().trim();

                if(id.isEmpty()){
                    JOptionPane.showMessageDialog(null,"Please Enter Your ID");
                    return;
                }
                if(password.isEmpty()){
                    JOptionPane.showMessageDialog(null,"Please Enter Your Password");
                    return;
                }

                char type = Character.toUpperCase(id.charAt(0));

                try {
                    if(type == 'S'){ // Student
                        if(!v.idExist(id)) {
                            JOptionPane.showMessageDialog(null, "ID Doesn't Exist");
                            return;
                        }
                        String name = v.passwordCheck(password,id);
                        if(name == null){
                            JOptionPane.showMessageDialog(null,"Incorrect Password");
                            return;
                        }
                        Student student = null;
                        for(Student s : peopleDB.getStudents()){
                            if(s.getId().equalsIgnoreCase(id)){
                                student = s;
                                break;
                            }
                        }
                        if(student != null){
                            setVisible(false);
                            new studentDashboard(student, peopleDB, courseDB);
                        }

                    } else if(type == 'I'){ // Instructor
                        if(!v.idExist(id)) {
                            JOptionPane.showMessageDialog(null, "ID Doesn't Exist");
                            return;
                        }
                        String name = v.passwordCheck(password,id);
                        if(name == null){
                            JOptionPane.showMessageDialog(null,"Incorrect Password");
                            return;
                        }
                        setVisible(false);
                        new InstructorDashboardFrame(courseDB, peopleDB, id);

                    } else if(type == 'A'){ // Admin
                        // Hash the password before checking
                        String hashedPassword = v.passwordHashing(password);
                        Admin admin = null;
                        for(Admin a : peopleDB.getAdmins()){
                            if(a.getId().equalsIgnoreCase(id) && a.getPassword().equals(hashedPassword)){
                                admin = a;
                                break;
                            }
                        }
                        if(admin == null){
                            JOptionPane.showMessageDialog(null,"Incorrect Admin ID or Password");
                            return;
                        }
                        setVisible(false);
                        new AdminDashboard(admin, peopleDB.getCourses());

                    } else {
                        JOptionPane.showMessageDialog(null,"Unknown user type!");
                    }

                } catch (NoSuchAlgorithmException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
