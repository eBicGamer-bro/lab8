import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;

public class Signup extends JFrame{
    private JPanel p1;
    private JTextField textFieldID;
    private JTextField textFieldName;
    private JTextField textFieldGmail;
    private JTextField textFieldPassword;
    private JButton signupButton;
    private JComboBox comboBoxType;
    private JButton backButton;
    private ValidationAndHashing v = new ValidationAndHashing();
    private PeopleDB list = new PeopleDB();

    public Signup() {
        setVisible(true);
        setSize(400,400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Signup Menu");
        setLocationRelativeTo(null);
        setContentPane(p1);
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object typee = comboBoxType.getSelectedItem();
                if (typee == null) {
                    JOptionPane.showMessageDialog(null, "Select a Gender!");
                    return;
                }
                String type = comboBoxType.getSelectedItem().toString();
                String id = textFieldID.getText();
                if(v.validID(id,type)) {
                    if(v.idExist(id)){
                        JOptionPane.showMessageDialog(null, "ID Already Exist");
                        return;
                    }}
                else
                {
                    JOptionPane.showMessageDialog(null, "Enter a Valid ID Format");
                    return;
                }
                String name = textFieldName.getText();
                if (!v.nameValidation(name)) {
                    JOptionPane.showMessageDialog(null, "Enter A Valid Name!");
                    return;
                }
                String gmail = textFieldGmail.getText();
                if(!v.gmailValidation(gmail)){
                    JOptionPane.showMessageDialog(null, "Enter A Valid Email Format!");
                    return;
                }
                String password = textFieldPassword.getText();
                if(password == null || password.trim().isEmpty()){
                    JOptionPane.showMessageDialog(null, "Enter A Password!");
                    return;
                }
                try {
                    password = v.passwordHashing(password);
                } catch (NoSuchAlgorithmException ex) {
                    throw new RuntimeException(ex);
                }
                id = id.toUpperCase();
                if(id.charAt(0)=='S'){
                    Student student = new Student(id,name,gmail,password);
                    list.addStudent(student);
                    JOptionPane.showMessageDialog(null,"Student User Created Successfully");
                } else if (id.charAt(0)=='I') {
                    Instructor instructor = new Instructor(id,name,gmail,password);
                    list.addInstructor(instructor);
                    JOptionPane.showMessageDialog(null,"Instructor User Created Successfully");
                }
                setVisible(false);
                new WelcomeMenu();

            }


        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new WelcomeMenu();
            }
        });
    }
}
