import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LessonDashboard extends JFrame {

    private JPanel mainPanel;
    private JList<String> lessonList;
    private JTextArea contentArea;
    private JButton markCompletedButton;
    private JButton startQuizButton;
    private JButton backButton;
    private JLabel courseTitleLabel;
    private JProgressBar progressBar;
    private JScrollPane listScrollPane;
    private JScrollPane contentScrollPane;
    private JSplitPane splitPane;

    private Course course;
    private Student student;
    private JFrame parentFrame;
    private ArrayList<Lesson> lessons;

    private PeopleDB db;
    private CourseLessonDB courseDb;

    public LessonDashboard(Course course, Student student, JFrame parentFrame, PeopleDB db) {
        this.course = course;
        this.student = student;
        this.parentFrame = parentFrame;
        this.db = db;
        this.courseDb = new CourseLessonDB();

        lessons = new ArrayList<>();
        for (Lesson l : course.getLessons()) {
            if (!student.hasCompletedLesson(course.getId(), l.getId())) {
                lessons.add(l);
            }
        }

        setTitle("Course View: " + course.getName());
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ***** TOP *****
        JPanel topPanel = new JPanel(new BorderLayout());
        backButton = new JButton("<< Back");
        courseTitleLabel = new JLabel("Course: " + course.getName(), SwingConstants.CENTER);
        courseTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(courseTitleLabel, BorderLayout.CENTER);

        // ***** LESSON LIST *****
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Lesson l : lessons) listModel.addElement(l.getTitle());

        lessonList = new JList<>(listModel);
        lessonList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lessonList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        listScrollPane = new JScrollPane(lessonList);
        listScrollPane.setBorder(BorderFactory.createTitledBorder("Lessons"));

        // ***** CONTENT AREA *****
        contentArea = new JTextArea();
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBorder(BorderFactory.createTitledBorder("Lesson Content"));

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, contentScrollPane);
        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0.3);

        // ***** BOTTOM PANEL *****
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        JPanel btnPanel = new JPanel();
        startQuizButton = new JButton("Start Quiz");
        markCompletedButton = new JButton("Mark Completed");

        btnPanel.add(startQuizButton);
        btnPanel.add(markCompletedButton);

        bottomPanel.add(progressBar, BorderLayout.CENTER);
        bottomPanel.add(btnPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        updateProgressBar();

        // ***** EVENTS *****
        lessonList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = lessonList.getSelectedIndex();
                if (idx != -1) {
                    contentArea.setText(lessons.get(idx).getContent());
                    contentArea.setCaretPosition(0);
                }
            }
        });

        // ***** START QUIZ BUTTON *****
        startQuizButton.addActionListener(e -> {
            int idx = lessonList.getSelectedIndex();
            if (idx == -1) {
                JOptionPane.showMessageDialog(this, "Please select a lesson first.");
                return;
            }

            Lesson lesson = lessons.get(idx);

            // retry limit = 1
            int attempts = student.countAttemptsFor(course.getId(), lesson.getId());
            if (attempts >= 1) {
                JOptionPane.showMessageDialog(this,
                        "You already used your only attempt.\nRetry limit = 1.",
                        "No Attempts Left",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // open quiz
            new QuizFrame(courseDb, db, course, lesson, student).setVisible(true);
        });

        // ***** MARK COMPLETED BUTTON *****
        markCompletedButton.addActionListener(e -> {
            int idx = lessonList.getSelectedIndex();
            if (idx == -1) {
                JOptionPane.showMessageDialog(this, "Please select a lesson first.");
                return;
            }

            Lesson lesson = lessons.get(idx);

            student.markLessonCompleted(course.getId(), lesson.getId());
            student.updateCourseProgress(course);

            removeLessonFromList(idx);
            updateProgressBar();
            db.save();

            JOptionPane.showMessageDialog(this, "Lesson marked as completed!");
        });

        backButton.addActionListener(e -> {
            parentFrame.setVisible(true);
            dispose();
        });
    }

    private void removeLessonFromList(int index) {
        lessons.remove(index);
        ((DefaultListModel<String>) lessonList.getModel()).remove(index);
        contentArea.setText("");
    }

    private void updateProgressBar() {
        for (Student.Progress p : student.getProgresses()) {
            if (p.getCourse().getId().equals(course.getId())) {
                progressBar.setValue((int) p.getPercentage());
                return;
            }
        }
    }
}
