import javax.swing.*;
import java.awt.*;
import java.util.List;

public class QuizFrame extends JFrame {

    private Student student;
    private Course course;
    private Lesson lesson;
    private LessonDashboard dashboard;
    private long startTime;
    private int[] answers;
    private JPanel qPanel;
    private JButton submitBtn;
    private JLabel infoLabel;
    private final long TIME_LIMIT = 30*60*1000; // 30 minutes
    private final int MAX_ATTEMPTS = 3;

    public QuizFrame(Student student, Course course, Lesson lesson, LessonDashboard dashboard) {
        this.student = student;
        this.course = course;
        this.lesson = lesson;
        this.dashboard = dashboard;

        int attempts = student.countAttemptsFor(course.getId(), lesson.getId());
        if (attempts >= MAX_ATTEMPTS) {
            JOptionPane.showMessageDialog(this,
                    "You have already used all 3 attempts for this quiz.");
            dispose();
            return;
        }

        if (lesson.getQuiz() == null) {
            JOptionPane.showMessageDialog(this, "No quiz prepared for this lesson.");
            dispose();
            return;
        }

        Quiz quiz = lesson.getQuiz();
        if (quiz.getQuestions().size() != 10) {
            JOptionPane.showMessageDialog(this, "Quiz must have 10 questions.");
            dispose();
            return;
        }

        answers = new int[10];
        for (int i = 0; i < 10; i++) answers[i] = -1;

        setTitle("Quiz – " + lesson.getTitle());
        setSize(900,700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI(quiz);
        startTime = System.currentTimeMillis();
    }

    private void initUI(Quiz quiz) {
        qPanel = new JPanel();
        qPanel.setLayout(new BoxLayout(qPanel, BoxLayout.Y_AXIS));

        List<Question> qs = quiz.getQuestions();
        for (int i = 0; i < qs.size(); i++) {
            Question q = qs.get(i);
            JPanel p = new JPanel(new BorderLayout());
            p.setBorder(BorderFactory.createTitledBorder("Q" + (i+1) + ": " + q.getText()));

            JPanel opts = new JPanel(new GridLayout(q.getOptions().size(),1));
            ButtonGroup bg = new ButtonGroup();

            for (int j = 0; j < q.getOptions().size(); j++) {
                JRadioButton rb = new JRadioButton(q.getOptions().get(j).getText());
                int qi=i,oi=j;
                rb.addActionListener(e -> answers[qi]=oi);
                bg.add(rb);
                opts.add(rb);
            }
            p.add(opts, BorderLayout.CENTER);
            qPanel.add(p);
        }

        submitBtn = new JButton("Submit Quiz");
        submitBtn.addActionListener(e -> onSubmit());
        infoLabel = new JLabel("Time Limit: 30 minutes | Attempts Left: "
                + (MAX_ATTEMPTS - student.countAttemptsFor(course.getId(), lesson.getId())));
        JScrollPane scroll = new JScrollPane(qPanel);
        add(infoLabel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(submitBtn, BorderLayout.SOUTH);
    }

    private void onSubmit() {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > TIME_LIMIT) {
            JOptionPane.showMessageDialog(this,"Time over! You failed the quiz.");
            recordAttempt(0,false);
            dispose();
            return;
        }

        Quiz quiz = lesson.getQuiz();
        int correct = quiz.grade(answers);
        boolean passed = quiz.isPassed(correct);

        recordAttempt(correct,passed);

        JOptionPane.showMessageDialog(this,
                "You scored " + correct + "/10\n" + (passed?"Passed!":"Failed."),
                "Quiz Result",
                JOptionPane.INFORMATION_MESSAGE);

        if (passed) {
            student.updateCourseProgress(course);
        }

        dispose();
    }

    private void recordAttempt(int score, boolean passed) {
        int attemptNumber = student.countAttemptsFor(course.getId(), lesson.getId()) + 1;
        QuizAttempt qa = new QuizAttempt(student.getId(), course.getId(), lesson.getId(),
                score, attemptNumber, passed, System.currentTimeMillis());
        student.addQuizAttempt(qa);
    }
}
