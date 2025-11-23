import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class AdminDashboard extends JFrame {
    private JPanel panel1;
    private JTable pendingCoursesTable;
    private JButton approveButton;
    private JButton rejectButton;
    private DefaultTableModel tableModel;
    private CourseLessonDB db;
    private Admin admin;
    private ArrayList<Course> courses;
    private ArrayList<Course> pendingCourses;

    public AdminDashboard(Admin admin, CourseLessonDB db) {
        this.admin = admin;
        this.db = db;
        this.courses = db.getCourses();

        panel1 = new JPanel();
        panel1.setLayout(null);
        setContentPane(panel1);
        setTitle("Admin Dashboard - " + admin.getName());
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Table
        pendingCoursesTable = new JTable();
        tableModel = new DefaultTableModel(new String[]{"Course ID", "Course Name", "Instructor ID"}, 0);
        pendingCoursesTable.setModel(tableModel);
        JScrollPane scrollPane = new JScrollPane(pendingCoursesTable);
        scrollPane.setBounds(20, 20, 740, 250);
        panel1.add(scrollPane);

        // Buttons
        approveButton = new JButton("Approve");
        approveButton.setBounds(200, 300, 150, 30);
        panel1.add(approveButton);

        rejectButton = new JButton("Reject");
        rejectButton.setBounds(400, 300, 150, 30);
        panel1.add(rejectButton);

        loadPendingCourses();

        // Approve button action
        approveButton.addActionListener(e -> {
            int selectedRow = pendingCoursesTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel1, "Select a course to approve.");
                return;
            }
            Course selected = pendingCourses.get(selectedRow);
            admin.approveCourse(selected);  // updates course in memory
            JOptionPane.showMessageDialog(panel1, "Course " + selected.getName() + " approved.");
            db.updateCourse(selected);
            loadPendingCourses();
        });

        // Reject button action
        rejectButton.addActionListener(e -> {
            int selectedRow = pendingCoursesTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel1, "Select a course to reject.");
                return;
            }
            Course selected = pendingCourses.get(selectedRow);
            admin.rejectCourse(selected);
            db.updateCourse(selected);// updates course in memory
            JOptionPane.showMessageDialog(panel1, "Course " + selected.getName() + " rejected.");
            loadPendingCourses();
        });

        setVisible(true);
    }

    // Load pending courses from the full courses list
    private void loadPendingCourses() {
        tableModel.setRowCount(0);
        pendingCourses = new ArrayList<>();
        for (Course c : courses) {
            if (c.getApprovalStatus() == Course.ApprovalStatus.PENDING) {
                pendingCourses.add(c);
            }
        }
        for (Course c : pendingCourses) {
            tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getInstructorId()});
        }
    }
}
