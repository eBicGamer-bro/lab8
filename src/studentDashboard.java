import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class studentDashboard extends JFrame {
    private JButton enrollCourseButton;
    private JButton browseCourseButton;
    private JTable availableCourses;
    private JTable enrolledCourses;
    private JButton logoutButton;
    private JPanel panel1;
    private DefaultTableModel model1; // Available
    private DefaultTableModel model2; // Enrolled

    // Fields
    private Student currentStudent;
    private CourseLessonDB acourses;
    private ArrayList<Course> allCourses;

    public studentDashboard(Student student) {
        // 1. Setup basic window properties
        this.currentStudent = student;

        setContentPane(panel1);
        setTitle("Student Dashboard: " + student.getName());
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 2. Initialize Tables
        String[] columnName1 = {"Available Courses"};
        String[] columnName2 = {"My Enrolled Courses"};
        model1 = new DefaultTableModel(columnName1, 0);
        model2 = new DefaultTableModel(columnName2, 0);
        availableCourses.setModel(model1);
        enrolledCourses.setModel(model2);

        availableCourses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        enrolledCourses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 3. Load Data from Database
        acourses = new CourseLessonDB();
        allCourses = acourses.getCourses();

        // 4. SORT Data into tables
        String myId = currentStudent.getId();

        for (Course c : allCourses) {
            List<String> enrolledIds = c.getStudentIds();

            if (enrolledIds.contains(myId)) {
                model2.addRow(new Object[]{c.getName()});
            } else {
                model1.addRow(new Object[]{c.getName()});
            }
        }

        // 5. Button Actions
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                // Assuming Login class exists as per user workflow
                new Login().setVisible(true);
            }
        });

        enrollCourseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = availableCourses.getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(panel1, "Please select a course from 'Available Courses'.");
                    return;
                }

                String courseName = (String) model1.getValueAt(selectedRow, 0);

                Course selectedCourse = null;
                for(Course c : allCourses) {
                    if(c.getName().equals(courseName)) {
                        selectedCourse = c;
                        break;
                    }
                }

                if (selectedCourse != null) {
                    selectedCourse.addStudent(currentStudent);
                    currentStudent.enrollCourse(selectedCourse);
                    acourses.updateCourse(selectedCourse); // Save to courses.json [cite: 89]

                    model1.removeRow(selectedRow);
                    model2.addRow(new Object[]{courseName});

                    JOptionPane.showMessageDialog(panel1, "Enrolled in " + courseName);
                }
            }
        });
        browseCourseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = enrolledCourses.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(panel1, "Select a course from 'Enrolled Courses' to browse.");
                    return;
                }
                String courseName = (String) model2.getValueAt(selectedRow, 0);

                Course selectedCourse = null;
                for(Course c : allCourses) {
                    if(c.getName().equals(courseName)) {
                        selectedCourse = c;
                        break;
                    }
                }

                if (selectedCourse != null) {
                    // Opens the Lesson view to "access lessons" [cite: 83]
                    new LessonDashboard(selectedCourse, currentStudent, studentDashboard.this).setVisible(true);
                    setVisible(false);
                }
            }
        });
    }
}