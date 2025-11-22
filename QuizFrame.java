import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class QuizFrame extends JFrame {
    private Quiz quiz;
    private Student student;
    private Course course;
    private Lesson lesson;
    private long startTime;
    private int[] answers; // chosen option indexes
    private JPanel qPanel;
    private JButton submitBtn;
    private JLabel infoLabel;
    private CourseLessonDB db;
    private PeopleDB peopleDB;

    public QuizFrame(CourseLessonDB db, PeopleDB peopleDB, Course course, Lesson lesson, Student student) {
        this.db = db;
        this.peopleDB = peopleDB;
        this.course = course;
        this.lesson = lesson;
        this.student = student;
        this.quiz = lesson.getQuiz();

        setTitle("Quiz: " + lesson.getTitle());
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        if (quiz == null) {
            JOptionPane.showMessageDialog(this, "No quiz for this lesson.");
            dispose();
            return;
        }

        if (quiz.getQuestions() == null || quiz.getQuestions().size() < Quiz.REQUIRED_QUESTIONS) {
            JOptionPane.showMessageDialog(this, "Quiz is not properly configured (needs " + Quiz.REQUIRED_QUESTIONS + " questions).");
            dispose();
            return;
        }

        answers = new int[quiz.getQuestions().size()];
        for (int i = 0; i < answers.length; i++) answers[i] = -1;

        initUI();
        startTime = System.currentTimeMillis();
    }

    private void initUI() {
        qPanel = new JPanel();
        qPanel.setLayout(new BoxLayout(qPanel, BoxLayout.Y_AXIS));

        List<Question> qs = quiz.getQuestions();
        for (int i = 0; i < qs.size(); i++) {
            Question q = qs.get(i);
            JPanel p = new JPanel(new BorderLayout());
            p.setBorder(BorderFactory.createTitledBorder("Q" + (i+1) + ": " + q.getText()));
            JPanel opts = new JPanel(new GridLayout(q.getOptions().size(), 1));
            ButtonGroup bg = new ButtonGroup();
            for (int j = 0; j < q.getOptions().size(); j++) {
                JRadioButton rb = new JRadioButton(q.getOptions().get(j).getText());
                final int qi = i;
                final int oi = j;
                rb.addActionListener(e -> answers[qi] = oi);
                bg.add(rb);
                opts.add(rb);
            }
            p.add(opts, BorderLayout.CENTER);
            qPanel.add(p);
        }

        submitBtn = new JButton("Submit Quiz");
        submitBtn.addActionListener(e -> onSubmit());

        infoLabel = new JLabel("You have " + quiz.getTimeLimitMillis()/60000 + " minutes to finish. Allowed attempts: " + quiz.getMaxAttempts());


        JScrollPane scroll = new JScrollPane(qPanel);
        add(infoLabel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(submitBtn, BorderLayout.SOUTH);
    }

    private void onSubmit() {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > quiz.getTimeLimitMillis()) {
            JOptionPane.showMessageDialog(this, "Time limit exceeded. Attempt recorded as failed.");
            recordAttempt(0, false);
            dispose();
            return;
        }


        int prevAttempts = student.countAttemptsFor(course.getId(), lesson.getId());
        if (prevAttempts >= quiz.getMaxAttempts()) {
            JOptionPane.showMessageDialog(this, "No attempts left for this quiz.");
            return;
        }

        int correct = quiz.grade(answers);
        boolean passed = quiz.isPassed(correct);

        int attemptNum = prevAttempts + 1;
        recordAttempt(correct, passed, attemptNum);

        String msg = "You scored " + correct + " out of " + quiz.getQuestions().size() + ".\n";
        msg += passed ? "Passed!" : "Failed.";
        JOptionPane.showMessageDialog(this, msg);


        if (passed) {
            // update student progress for course
            double increment = 100.0 / course.getLessons().size();
            double currentP = 0;
            for (Student.Progress p : student.getProgresses()) {
                if (p.getCourse().getId().equals(course.getId())) {
                    currentP = p.getPercentage();
                    break;
                }
            }
            double newProgress = Math.min(100.0, currentP + increment);
            student.updateProgress(course.getId(), newProgress);
        }


        peopleDB.save();
        db.updateCourse(course);

        dispose();
    }


    private void recordAttempt(int score, boolean passed) {
        recordAttempt(score, passed, student.countAttemptsFor(course.getId(), lesson.getId()) + 1);
    }

    private void recordAttempt(int score, boolean passed, int attemptNumber) {
        QuizAttempt attempt = new QuizAttempt(student.getId(), course.getId(), lesson.getId(),
                score, attemptNumber, passed, System.currentTimeMillis());
        student.addQuizAttempt(attempt);

        peopleDB.save();
    }
}
