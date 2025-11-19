import javax.swing.*;
import java.awt.*;

public class InstructorDashboardFrame extends JFrame {
    private CourseLessonDB db;
    private PeopleDB peopleDb;
    private String instructorId;

    private DefaultListModel<Course> courseModel = new DefaultListModel<>();
    private JList<Course> courseList = new JList<>(courseModel);

    private JButton btnAdd = new JButton("Add Course");
    private JButton btnEdit = new JButton("Edit Course");
    private JButton btnDelete = new JButton("Delete Course");
    private JButton btnLessons = new JButton("Manage Lessons");
    private JButton btnStudents = new JButton("View Enrolled Students");
    //private JButton btnRefresh = new JButton("Refresh");
    private JButton btnLogout = new JButton("Logout");

    public InstructorDashboardFrame(CourseLessonDB db, PeopleDB peopleDb, String instructorId) {
        this.db = db;
        this.peopleDb = peopleDb;
        this.instructorId = instructorId;
        setTitle("Instructor Dashboard - " + instructorId);
        setSize(800, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        init();
        loadCourses();
        setVisible(true);
    }

    private void init() {
        JPanel left = new JPanel(new BorderLayout());
        left.setBorder(BorderFactory.createTitledBorder("Your Courses"));
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        left.add(new JScrollPane(courseList), BorderLayout.CENTER);

        JPanel leftButtons = new JPanel(new GridLayout(8, 1, 8, 8));
        leftButtons.add(btnAdd);
        leftButtons.add(btnEdit);
        leftButtons.add(btnDelete);
        leftButtons.add(btnLessons);
        leftButtons.add(btnStudents);
        //leftButtons.add(btnRefresh);
        leftButtons.add(btnLogout);

        add(left, BorderLayout.CENTER);
        add(leftButtons, BorderLayout.EAST);

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnLessons.addActionListener(e -> onLessons());
        btnStudents.addActionListener(e -> onViewStudents());
       // btnRefresh.addActionListener(e -> loadCourses());
        btnLogout.addActionListener(e -> onLogout());
    }

    private void loadCourses() {
        courseModel.clear();
        for (Course c : db.getCourses()) {
            if (c.getInstructorId().equals(instructorId)) courseModel.addElement(c);
        }
    }

    private void onAdd() {
        CourseFormDialog dlg = new CourseFormDialog(this, "Add Course", null);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            Course c = new Course(dlg.getCourseId(), dlg.getTitle(), instructorId, dlg.getDescription());
            db.addCourse(c);
            loadCourses();
        }
    }

    private void onEdit() {
        Course c = courseList.getSelectedValue();
        if (c == null) { JOptionPane.showMessageDialog(this, "Select a course."); return; }
        CourseFormDialog dlg = new CourseFormDialog(this, "Edit Course", c);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            c.setName(dlg.getTitle());
            c.setDescription(dlg.getDescription());
            c.setInstructorId(instructorId);
            db.updateCourse(c);
            loadCourses();
        }
    }

    private void onDelete() {
        Course c = courseList.getSelectedValue();
        if (c == null) { JOptionPane.showMessageDialog(this, "Select a course."); return; }
        int ok = JOptionPane.showConfirmDialog(this, "Delete course " + c.getName() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            db.removeCourse(c.getId());
            loadCourses();
        }
    }

    private void onLessons() {
        Course c = courseList.getSelectedValue();
        if (c == null) { JOptionPane.showMessageDialog(this, "Select a course."); return; }
        LessonManagerDialog dlg = new LessonManagerDialog(this, db, instructorId, c);
        dlg.setVisible(true);
        loadCourses();
    }

    private void onViewStudents() {
        Course c = courseList.getSelectedValue();
        if (c == null) { JOptionPane.showMessageDialog(this, "Select a course."); return; }
        EnrolledStudentsDialog dlg = new EnrolledStudentsDialog(this, peopleDb, c);
        dlg.setVisible(true);
    }

    private void onLogout() {
        int ok = JOptionPane.showConfirmDialog(this, "Logout?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            dispose();
            new Login().setVisible(true);
        }
    }
}