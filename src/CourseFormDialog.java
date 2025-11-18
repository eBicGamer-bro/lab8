import javax.swing.*;
import java.awt.*;

public class CourseFormDialog extends JDialog {

    private JTextField tfCourseId = new JTextField();
    private JTextField tfTitle = new JTextField();
    private JTextArea taDescription = new JTextArea(6, 25);
    private ValidationAndHashing v = new ValidationAndHashing();
    private JButton btnSave = new JButton("Save");
    private JButton btnCancel = new JButton("Cancel");

    private boolean saved = false;

    public CourseFormDialog(Window owner, String title, Course course) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setSize(420, 320);
        setLocationRelativeTo(owner);
        init(course);
    }

    private void init(Course course) {
        JPanel p = new JPanel(new BorderLayout(6,6));
        JPanel fields = new JPanel(new GridLayout(2,1,4,4));
        fields.add(labeled("Course ID:", tfCourseId));
        fields.add(labeled("Title:", tfTitle));
        p.add(fields, BorderLayout.NORTH);
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
        descriptionPanel.add(new JScrollPane(taDescription), BorderLayout.CENTER);
        p.add(descriptionPanel, BorderLayout.CENTER);
        JPanel buttons = new JPanel();
        buttons.add(btnSave);
        buttons.add(btnCancel);
        p.add(buttons, BorderLayout.SOUTH);
        add(p);
        if (course != null) {
            tfCourseId.setText(course.getCourseId());
            tfCourseId.setEnabled(false);
            tfTitle.setText(course.getTitle());
            taDescription.setText(course.getDescription());
        }
        btnSave.addActionListener(e -> {
            String cid = tfCourseId.getText().trim();
            String title = tfTitle.getText().trim();

            if (cid.isEmpty() || title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Course ID and Title are required.");
                return;
            }

            if (!v.validID(cid, "Course")) {
                JOptionPane.showMessageDialog(this, "Invalid Course ID. Must start with C followed by digits.");
                return;
            }

            if (tfCourseId.isEnabled() && v.courseIdExist(cid)) {
                JOptionPane.showMessageDialog(this, "This Course ID already exists.");
                return;
            }
            if (tfTitle.isEnabled() && v.courseNameExist(title)) {
                JOptionPane.showMessageDialog(this, "This Course Name already exists.");
                return;
            }

            saved = true;
            setVisible(false);
        });
        btnCancel.addActionListener(e -> setVisible(false));
    }

    private JPanel labeled(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(label), BorderLayout.WEST);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    public boolean isSaved() {
        return saved;
    }

    public String getCourseId() {
        return tfCourseId.getText().trim();
    }

    public String getTitle() {
        return tfTitle.getText().trim();
    }

    public String getDescription() {
        return taDescription.getText().trim();
    }
}
