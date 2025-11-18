import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EnrolledStudentsDialog extends JDialog {
    private PeopleDB db;
    private Course course;

    public EnrolledStudentsDialog(Window owner, PeopleDB db, Course course) {
        super(owner, "Enrolled Students - " + course.getName(), ModalityType.APPLICATION_MODAL);
        this.db = db;
        this.course = course;
        setSize(520, 320);
        setLocationRelativeTo(owner);
        init();
    }

    private void init() {
        String[] cols = {"Student ID", "Name", "Email"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        for (Student s : course.getStudents()) {
            model.addRow(new Object[]{s.getId(), s.getName(), s.getEmail()});
        }
        add(new JScrollPane(table), BorderLayout.CENTER);
        JButton close = new JButton("Close");
        close.addActionListener(e -> setVisible(false));
        JPanel bottom = new JPanel();
        bottom.add(close);
        add(bottom, BorderLayout.SOUTH);
    }
}
