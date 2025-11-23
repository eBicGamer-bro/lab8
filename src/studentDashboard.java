import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class studentDashboard extends JFrame {
    private JButton enrollCourseButton;
    private JButton browseCourseButton;
    private JButton viewCertificatesButton; // New Button
    private JTable availableCourses;
    private JTable enrolledCourses;
    private JButton logoutButton;
    private JPanel panel1;
    private DefaultTableModel model1; // Available Courses
    private DefaultTableModel model2; // Enrolled Courses

    private Student currentStudent;
    private CourseLessonDB acourses;
    private ArrayList<Course> allCourses;
    private PeopleDB db;

    public studentDashboard(Student student, PeopleDB db, CourseLessonDB cb) {
        this.currentStudent = student;
        this.db = db;
        acourses = cb;

        // 1. Setup Main Container (Fix for Layout Error)
        // Instead of setting panel1 directly, we create a wrapper to hold panel1 and the new button.
        JPanel mainContainer = new JPanel(new BorderLayout());

        // Add the IntelliJ form panel to the center
        mainContainer.add(panel1, BorderLayout.CENTER);

        // Create a panel for the Certificates button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        viewCertificatesButton = new JButton("View Earned Certificates");
        bottomPanel.add(viewCertificatesButton);

        // Add button panel to the bottom
        mainContainer.add(bottomPanel, BorderLayout.SOUTH);

        // Set the content pane to our new wrapper
        setContentPane(mainContainer);

        setTitle("Student Dashboard: " + student.getName());
        setSize(1000, 600);
        setLocationRelativeTo(null);
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

        // ONLY approved courses for students
        allCourses = acourses.getApprovedCourses();

        // 4. Populate tables
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
                new Login().setVisible(true);
            }
        });

        enrollCourseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = availableCourses.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(mainContainer, "Please select a course from 'Available Courses'.");
                    return;
                }

                String courseName = (String) model1.getValueAt(selectedRow, 0);
                Course selectedCourse = null;

                // Find the course among approved courses
                for (Course c : allCourses) {
                    if (c.getName().equals(courseName)) {
                        selectedCourse = c;
                        break;
                    }
                }

                if (selectedCourse != null) {
                    selectedCourse.addStudent(currentStudent);
                    currentStudent.enrollCourse(selectedCourse);
                    acourses.updateCourse(selectedCourse); // Persist to courses.json

                    model1.removeRow(selectedRow);
                    model2.addRow(new Object[]{courseName});
                    JOptionPane.showMessageDialog(mainContainer, "Enrolled in " + courseName);
                }
            }
        });

        browseCourseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = enrolledCourses.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(mainContainer, "Select a course from 'Enrolled Courses' to browse.");
                    return;
                }

                String courseName = (String) model2.getValueAt(selectedRow, 0);
                Course selectedCourse = null;

                // Find the course among approved courses
                for (Course c : allCourses) {
                    if (c.getName().equals(courseName)) {
                        selectedCourse = c;
                        break;
                    }
                }

                if (selectedCourse != null) {
                    // Opens the Lesson view to access lessons
                    new LessonDashboard(selectedCourse, currentStudent, studentDashboard.this, db).setVisible(true);
                    setVisible(false);
                }
            }
        });

        // 6. View Certificates Action
        viewCertificatesButton.addActionListener(e -> {
            ArrayList<Certificate> certs = currentStudent.getCertificates();
            if (certs == null || certs.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No certificates earned yet.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("=== My Certificates ===\n\n");
            for (Certificate c : certs) {
                sb.append("Course: ").append(c.getCourseName()).append("\n");
                sb.append("Certificate ID: ").append(c.getCertificateId()).append("\n");
                sb.append("Date: ").append(new java.util.Date(c.getIssueDate())).append("\n");
                sb.append("-------------------------\n");
            }

            JTextArea ta = new JTextArea(sb.toString());
            ta.setEditable(false);
            ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JOptionPane.showMessageDialog(this, new JScrollPane(ta), "My Certificates", JOptionPane.INFORMATION_MESSAGE);
        });

        setVisible(true);
    }
}