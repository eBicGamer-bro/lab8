import javax.swing.*;
import java.awt.*;

public class LessonManagerDialog extends JDialog {
    private CourseLessonDB db;
    private String instructorId;
    private Course course;
    private DefaultListModel<Lesson> lessonModel = new DefaultListModel<>();
    private JList<Lesson> lessonJList = new JList<>(lessonModel);
    private JButton btnAdd = new JButton("Add Lesson");
    private JButton btnEdit = new JButton("Edit Lesson");
    private JButton btnDelete = new JButton("Delete Lesson");
    private JButton btnClose = new JButton("Close");
    private JButton btnCreateQuiz = new JButton("Create Quiz");

    public LessonManagerDialog(Window owner, CourseLessonDB db, String instructorId, Course course) {
        super(owner, "Manage Lessons - " + course.getName(), ModalityType.APPLICATION_MODAL);
        this.db = db;
        this.instructorId = instructorId;
        this.course = course;
        setSize(540, 420);
        setLocationRelativeTo(owner);
        init();
        loadLessons();
    }

    private void init() {
        setLayout(new BorderLayout());
        add(new JScrollPane(lessonJList), BorderLayout.CENTER);

        JPanel btns = new JPanel(new GridLayout(5, 1, 6, 6));
        btns.add(btnAdd);
        btns.add(btnEdit);
        btns.add(btnDelete);
        btns.add(btnCreateQuiz);
        btns.add(btnClose);
        add(btns, BorderLayout.EAST);

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnCreateQuiz.addActionListener(e -> onCreateQuiz());
        btnClose.addActionListener(e -> {
            db.updateCourse(course);
            setVisible(false);
        });
    }

    private void loadLessons() {
        lessonModel.clear();
        Course fresh = db.findCourseById(course.getId()).orElse(course);
        for (Lesson l : fresh.getLessons()) lessonModel.addElement(l);
    }

    private void onAdd() {
        LessonFormDialog dlg = new LessonFormDialog(this, "Add Lesson", null,course);
        dlg.setVisible(true);

        if (dlg.isSaved()) {
            Lesson l = new Lesson(dlg.getLessonId(), dlg.getTitle(), dlg.getContent());
            l.setOptionalResources(dlg.getOptionalResources());

            Course fresh = db.findCourseById(course.getId()).orElse(course);
            fresh.addLesson(l);

            db.updateCourse(fresh);
            course = fresh;
            loadLessons();
        }
    }
    private void onCreateQuiz() {
        Lesson selected = lessonJList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a lesson first!");
            return;
        }
        else if(selected.getQuiz() != null){
            JOptionPane.showMessageDialog(this, "This Lesson Already has a Quiz!");
        return;
    }
        CreateQuizDialog dlg = new CreateQuizDialog(this, course, selected);
        dlg.setVisible(true);

        if (dlg.isSaved()) {
            db.updateCourse(course);
            JOptionPane.showMessageDialog(this, "Quiz created for lesson: " + selected.getTitle());
        }
    }

    private void onEdit() {
        Lesson selected = lessonJList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a lesson.");
            return;
        }

        LessonFormDialog dlg = new LessonFormDialog(this, "Edit Lesson", selected,course);
        dlg.setVisible(true);

        if (dlg.isSaved()) {
            Course fresh = db.findCourseById(course.getId()).orElse(course);

            for (Lesson ls : fresh.getLessons()) {
                if (ls.getId().equals(selected.getId())) {
                    ls.setTitle(dlg.getTitle());
                    ls.setContent(dlg.getContent());
                    ls.setOptionalResources(dlg.getOptionalResources());
                }
            }

            db.updateCourse(fresh);
            course = fresh;
            loadLessons();
        }
    }

    private void onDelete() {
        Lesson selected = lessonJList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a lesson.");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this,
                "Delete lesson " + selected.getTitle() + "?",
                "Confirm", JOptionPane.YES_NO_OPTION);

        if (ok == JOptionPane.YES_OPTION) {
            Course fresh = db.findCourseById(course.getId()).orElse(course);
            fresh.getLessons().removeIf(l -> l.getId().equals(selected.getId()));
            db.updateCourse(fresh);
            course = fresh;
            loadLessons();
        }

    }
}
