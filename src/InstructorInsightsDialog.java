import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class InstructorInsightsDialog extends JDialog {
    private Course course;
    private PeopleDB peopleDb;
    private Analytics analytics = new Analytics();

    public InstructorInsightsDialog(Window owner, Course course, PeopleDB peopleDb) {
        super(owner, "Insights — " + course.getName(), ModalityType.APPLICATION_MODAL);
        this.course = course;
        this.peopleDb = peopleDb;
        setSize(900, 560);
        setLocationRelativeTo(owner);
        init();
    }

    private void init() {
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Student Performance", createStudentPerformancePanel());
        tabs.addTab("Quiz Averages", createQuizAveragesPanel());
        tabs.addTab("Completion %", createCompletionPanel());

        add(tabs, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        bottom.add(close);
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel createStudentPerformancePanel() {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        java.util.List<Student> students = course.getStudents();
        DefaultListModel<Student> lm = new DefaultListModel<>();
        for (Student s : students) lm.addElement(s);

        JList<Student> list = new JList<>(lm);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int idx, boolean sel, boolean foc) {
                super.getListCellRendererComponent(list, value, idx, sel, foc);
                if (value instanceof Student) setText(((Student) value).getName() + " (" + ((Student) value).getId() + ")");
                return this;
            }
        });

        JPanel left = new JPanel(new BorderLayout(6,6));
        left.add(new JLabel("Students:"), BorderLayout.NORTH);
        left.add(new JScrollPane(list), BorderLayout.CENTER);
        left.setPreferredSize(new Dimension(260, 0));

        JPanel center = new JPanel(new BorderLayout(8,8));
        JLabel hint = new JLabel("<html>Select a student and press a chart button to display per-lesson performance.<br/>Lessons without quizzes are skipped.</html>");
        center.add(hint, BorderLayout.NORTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton showLine = new JButton("Show Line Chart");
        JButton showBar = new JButton("Show Bar Chart");
        controls.add(showLine);
        controls.add(showBar);
        center.add(controls, BorderLayout.SOUTH);

        p.add(left, BorderLayout.WEST);
        p.add(center, BorderLayout.CENTER);

        showLine.addActionListener(e -> {
            Student s = list.getSelectedValue();
            if (s == null) { JOptionPane.showMessageDialog(this, "Select a student."); return; }
            showStudentPerformanceChart(s, false);
        });

        showBar.addActionListener(e -> {
            Student s = list.getSelectedValue();
            if (s == null) { JOptionPane.showMessageDialog(this, "Select a student."); return; }
            showStudentPerformanceChart(s, true);
        });

        return p;
    }

    private void showStudentPerformanceChart(Student s, boolean bar) {
        java.util.List<Lesson> lessonsWithQuiz = new ArrayList<>();
        for (Lesson l : course.getLessons()) if (l.getQuiz() != null) lessonsWithQuiz.add(l);
        if (lessonsWithQuiz.isEmpty()) { JOptionPane.showMessageDialog(this, "No lessons with quizzes."); return; }

        String[] labels = new String[lessonsWithQuiz.size()];
        double[] vals = new double[lessonsWithQuiz.size()];
        for (int i = 0; i < lessonsWithQuiz.size(); i++) {
            Lesson L = lessonsWithQuiz.get(i);
            labels[i] = L.getTitle();
            vals[i] = computeStudentScoreForLesson(s, course, L);
        }

        ChartFrame cf = new ChartFrame("Performance - " + s.getName(), labels, vals, bar);
        cf.setVisible(true);
    }

    private double computeStudentScoreForLesson(Student s, Course c, Lesson l) {
        int total = 0;
        int count = 0;
        for (QuizAttempt a : s.getQuizAttempts()) {
            if (a.getCourseId().equals(c.getId()) && a.getLessonId().equals(l.getId())) {
                total += a.getScore();
                count++;
            }
        }
        if (count == 0) return 0.0;
        return ((double) total / (double) count);
    }

    private JPanel createQuizAveragesPanel() {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        java.util.List<Lesson> lessons = course.getLessons();
        DefaultListModel<Lesson> lm = new DefaultListModel<>();
        for (Lesson l : lessons) lm.addElement(l);

        JList<Lesson> list = new JList<>(lm);
        list.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int idx, boolean sel, boolean foc) {
                super.getListCellRendererComponent(list, value, idx, sel, foc);
                if (value instanceof Lesson) {
                    Lesson L = (Lesson) value;
                    setText(L.getTitle() + (L.getQuiz() != null ? " ✓" : " ✗"));
                }
                return this;
            }
        });

        JPanel left = new JPanel(new BorderLayout(6,6));
        left.add(new JLabel("Lessons:"), BorderLayout.NORTH);
        left.add(new JScrollPane(list), BorderLayout.CENTER);
        left.setPreferredSize(new Dimension(260, 0));

        JPanel right = new JPanel(new BorderLayout(6,6));
        JButton showSelected = new JButton("Show Selected (Bar)");
        JButton showAll = new JButton("Show All Averages (Bar)");
        JPanel btns = new JPanel(new GridLayout(2,1,8,8));
        btns.add(showSelected);
        btns.add(showAll);
        right.add(btns, BorderLayout.NORTH);

        showSelected.addActionListener(e -> {
            Lesson sel = list.getSelectedValue();
            if (sel == null || sel.getQuiz() == null) { JOptionPane.showMessageDialog(this, "Select a lesson that has a quiz."); return; }
            String[] labels = { sel.getTitle() };
            double[] vals = { analytics.averageScoreLesson(course, sel) };
            ChartFrame cf = new ChartFrame("Quiz Average - " + sel.getTitle(), labels, vals, true);
            cf.setVisible(true);
        });

        showAll.addActionListener(e -> {
            java.util.List<Lesson> targets = new ArrayList<>();
            for (Lesson L : lessons) if (L.getQuiz() != null) targets.add(L);
            if (targets.isEmpty()) { JOptionPane.showMessageDialog(this, "No lessons with quizzes."); return; }
            String[] labels = new String[targets.size()];
            double[] vals = new double[targets.size()];
            for (int i = 0; i < targets.size(); i++) {
                labels[i] = targets.get(i).getTitle();
                vals[i] = analytics.averageScoreLesson(course, targets.get(i));
            }
            ChartFrame cf = new ChartFrame("Quiz Averages - " + course.getName(), labels, vals, true);
            cf.setVisible(true);
        });

        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.CENTER);
        return p;
    }

    private JPanel createCompletionPanel() {
        JPanel p = new JPanel(new BorderLayout(12,12));
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        DefaultListModel<Lesson> lm = new DefaultListModel<>();
        for (Lesson l : course.getLessons()) lm.addElement(l);
        JList<Lesson> list = new JList<>(lm);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel left = new JPanel(new BorderLayout(6,6));
        left.add(new JLabel("Lessons:"), BorderLayout.NORTH);
        left.add(new JScrollPane(list), BorderLayout.CENTER);
        left.setPreferredSize(new Dimension(260, 0));

        JPanel right = new JPanel(new GridLayout(3,1,8,8));
        JButton showCourseCompletion = new JButton("Show Course Completion (Bar)");
        JButton showLessonCompletion = new JButton("Show Selected Lesson Completion (Bar)");
        right.add(showCourseCompletion);
        right.add(showLessonCompletion);

        showCourseCompletion.addActionListener(e -> {
            double pct = analytics.courseCompletion(course);
            String[] labels = {"Course Completion %"};
            double[] vals = {pct};
            ChartFrame cf = new ChartFrame("Course Completion - " + course.getName(), labels, vals, true);
            cf.setVisible(true);
        });

        showLessonCompletion.addActionListener(e -> {
            Lesson sel = list.getSelectedValue();
            if (sel == null) { JOptionPane.showMessageDialog(this, "Select a lesson."); return; }
            double pct = analytics.lessonCompletion(course, sel);
            String[] labels = { sel.getTitle() };
            double[] vals = { pct };
            ChartFrame cf = new ChartFrame("Lesson Completion - " + sel.getTitle(), labels, vals, true);
            cf.setVisible(true);
        });

        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.CENTER);
        return p;
    }
}
