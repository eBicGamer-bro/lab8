import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class AdminDashboard extends JFrame {
    private JPanel panel1;
    private JTable pendingCoursesTable;
    private JButton approveButton;
    private JButton rejectButton;

    private CourseLessonDB courseDB;
    private ArrayList<Course> pendingCourses;
    private DefaultTableModel tableModel;

    public AdminDashboard() {
        // GUI setup
        panel1 = new JPanel();
        panel1.setLayout(null);
        setContentPane(panel1);
        setTitle("Admin Dashboard");
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

        // Load courses from DB
        courseDB = new CourseLessonDB();
        pendingCourses = new ArrayList<>();
        loadPendingCourses();

        // Approve Action
        approveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = pendingCoursesTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(panel1, "Select a course to approve.");
                    return;
                }
                Course selected = pendingCourses.get(selectedRow);
                selected.setApprovalStatus(Course.ApprovalStatus.APPROVED);
                courseDB.updateCourse(selected); // Persist instantly
                JOptionPane.showMessageDialog(panel1, "Course " + selected.getName() + " approved.");
                loadPendingCourses();
            }
        });

        // Reject Action
        rejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = pendingCoursesTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(panel1, "Select a course to reject.");
                    return;
                }
                Course selected = pendingCourses.get(selectedRow);
                selected.setApprovalStatus(Course.ApprovalStatus.REJECTED);
                courseDB.updateCourse(selected); // Persist instantly
                JOptionPane.showMessageDialog(panel1, "Course " + selected.getName() + " rejected.");
                loadPendingCourses();
            }
        });

        setVisible(true);
    }

    private void loadPendingCourses() {
        tableModel.setRowCount(0);
        pendingCourses.clear();
        for (Course c : courseDB.getCourses()) {
            if (c.getApprovalStatus() == Course.ApprovalStatus.PENDING) {
                pendingCourses.add(c);
                tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getInstructorId()});
            }
        }
    }
}
