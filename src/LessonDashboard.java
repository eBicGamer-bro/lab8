import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LessonDashboard extends JFrame {

    private JPanel mainPanel;
    private JList<String> lessonList;
    private JTextArea contentArea;
    private JButton markCompletedButton;
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


    public LessonDashboard(Course course, Student student, JFrame parentFrame,PeopleDB db) {
        this.course = course;
        this.student = student;
        this.parentFrame = parentFrame;

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

        JPanel topPanel = new JPanel(new BorderLayout());
        backButton = new JButton("<< Back");
        courseTitleLabel = new JLabel("Course: " + course.getName(), SwingConstants.CENTER);
        courseTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(courseTitleLabel, BorderLayout.CENTER);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Lesson l : lessons) listModel.addElement(l.getTitle());

        lessonList = new JList<>(listModel);
        lessonList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lessonList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        listScrollPane = new JScrollPane(lessonList);
        listScrollPane.setBorder(BorderFactory.createTitledBorder("Lessons"));

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

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        markCompletedButton = new JButton("Mark Current Lesson as Completed");
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        bottomPanel.add(progressBar, BorderLayout.CENTER);
        bottomPanel.add(markCompletedButton, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        updateProgressBar();

        lessonList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = lessonList.getSelectedIndex();
                if (idx != -1) {
                    contentArea.setText(lessons.get(idx).getContent());
                    contentArea.setCaretPosition(0);
                }
            }
        });

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
            JOptionPane.showMessageDialog(this, "Lesson completed and removed!");
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
