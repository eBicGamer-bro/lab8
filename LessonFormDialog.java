import javax.swing.*;
import java.awt.*;

public class LessonFormDialog extends JDialog {
    private JTextField tfLessonId = new JTextField();
    private JTextField tfTitle = new JTextField();
    private JTextArea taContent = new JTextArea(6, 25);
    private JTextArea taResources = new JTextArea(4, 25);
    private JButton btnSave = new JButton("Save");
    private JButton btnCancel = new JButton("Cancel");
    private boolean saved = false;
    private ValidationAndHashing v = new ValidationAndHashing();

    public LessonFormDialog(Window owner, String title, Lesson lesson) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setSize(420, 420);
        setLocationRelativeTo(owner);
        init(lesson);
    }

    private void init(Lesson lesson) {
        JPanel p = new JPanel(new BorderLayout(6,6));

        JPanel fields = new JPanel(new GridLayout(2,1,4,4));
        fields.add(labeled("Lesson ID:", tfLessonId));
        fields.add(labeled("Title:", tfTitle));
        p.add(fields, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(6,6));

        JPanel contentP = new JPanel(new BorderLayout());
        contentP.add(new JLabel("Content:"), BorderLayout.NORTH);
        contentP.add(new JScrollPane(taContent), BorderLayout.CENTER);
        center.add(contentP, BorderLayout.CENTER);

        JPanel resP = new JPanel(new BorderLayout());
        resP.add(new JLabel("Optional Resources (one per line):"), BorderLayout.NORTH);
        resP.add(new JScrollPane(taResources), BorderLayout.CENTER);
        center.add(resP, BorderLayout.SOUTH);

        p.add(center, BorderLayout.CENTER);

        JPanel btns = new JPanel();
        btns.add(btnSave);
        btns.add(btnCancel);
        p.add(btns, BorderLayout.SOUTH);

        add(p);

        if (lesson != null) {
            tfLessonId.setText(lesson.getId());
            tfLessonId.setEnabled(false);
            tfTitle.setText(lesson.getTitle());
            taContent.setText(lesson.getContent());
            String[] r = lesson.getOptionalResources();
            if (r != null && r.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < r.length; i++) {
                    sb.append(r[i]);
                    if (i < r.length - 1) sb.append("\n");
                }
                taResources.setText(sb.toString());
            }
        }

        btnSave.addActionListener(e -> {
            String id = tfLessonId.getText().trim();
            String title = tfTitle.getText().trim();

            if (id.isEmpty() || title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID and Title required.");
                return;
            }

            if (!v.validID(id, "Lesson")) {
                JOptionPane.showMessageDialog(this, "Invalid Lesson ID.\nMust start with L followed by digits.");
                return;
            }

            if (tfLessonId.isEnabled() && v.lessonIdExist(id)) {
                JOptionPane.showMessageDialog(this, "This Lesson ID already exists for this course.");
                return;
            }

            saved = true;
            setVisible(false);
        });

        btnCancel.addActionListener(e -> setVisible(false));
    }

    private JPanel labeled(String lbl, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(lbl), BorderLayout.WEST);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    public boolean isSaved() { return saved; }
    public String getLessonId() { return tfLessonId.getText().trim(); }
    public String getTitle() { return tfTitle.getText().trim(); }
    public String getContent() { return taContent.getText().trim(); }

    public String[] getOptionalResources() {
        String text = taResources.getText().trim();
        if (text.isEmpty()) return new String[0];
        String[] lines = text.split("\\r?\\n");
        java.util.ArrayList<String> list = new java.util.ArrayList<>();
        for (String s : lines) {
            s = s.trim();
            if (!s.isEmpty()) list.add(s);
        }
        return list.toArray(new String[0]);
    }
}

