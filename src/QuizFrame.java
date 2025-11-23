import javax.swing.*;
import java.awt.*;
import java.util.List;

public class QuizFrame extends JFrame {

    private Student student;
    private Course course;
    private Lesson lesson;
    private long startTime;

    private int[] answers; // chosen option index for each Q
    private JPanel qPanel;
    private JButton submitBtn;
    private JLabel infoLabel;
import javax.swing.*;
import java.awt.*;
import java.util.List;

    public class QuizFrame extends JFrame {

        private Student student;
        private Course course;
        private Lesson lesson;
        private long startTime;

        private int[] answers; // chosen option index for each question
        private JPanel qPanel;
        private JButton submitBtn;
        private JLabel infoLabel;

        private CourseLessonDB db;
        private PeopleDB peopleDB;

        private final long TIME_LIMIT = 30 * 60 * 1000; // 30 minutes

        public QuizFrame(CourseLessonDB db, PeopleDB peopleDB, Course course, Lesson lesson, Student student) {
            this.db = db;
            this.peopleDB = peopleDB;
            this.course = course;
            this.lesson = lesson;
            this.student = student;

            // ======= 1. Check if lesson is completed =======
            if (!student.hasCompletedLesson(course.getId(), lesson.getId())) {
                JOptionPane.showMessageDialog(this,
                        "You must complete the lesson before taking the quiz.",
                        "Lesson Not Completed",
                        JOptionPane.WARNING_MESSAGE);
                dispose();
                return;
            }

            // ======= 2. Check if quiz exists =======
            if (lesson.getQuiz() == null) {
                JOptionPane.showMessageDialog(this, "No quiz prepared for this lesson.");
                dispose();
                return;
            }

            Quiz quiz = lesson.getQuiz();

            // ======= 3. Check retry limit =======
            int attempts = student.countAttemptsFor(course.getId(), lesson.getId());
            if (attempts >= quiz.getMaxAttempts()) {
                JOptionPane.showMessageDialog(this,
                        "You already used your allowed attempt.\nRetry limit = " + quiz.getMaxAttempts(),
                        "No Attempts Left",
                        JOptionPane.WARNING_MESSAGE);
                dispose();
                return;
            }

            // ======= 4. Check number of questions =======
            if (quiz.getQuestions().size() != Quiz.REQUIRED_QUESTIONS) {
                JOptionPane.showMessageDialog(this,
                        "Quiz must contain exactly " + Quiz.REQUIRED_QUESTIONS + " MCQ questions.");
                dispose();
                return;
            }

            // ======= Initialize answers =======
            answers = new int[Quiz.REQUIRED_QUESTIONS];
            for (int i = 0; i < Quiz.REQUIRED_QUESTIONS; i++) answers[i] = -1;

            // ======= JFrame setup =======
            setTitle("Quiz – " + lesson.getTitle());
            setSize(900, 700);
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
                p.setBorder(BorderFactory.createTitledBorder("Q" + (i + 1) + ": " + q.getText()));

                JPanel opts = new JPanel(new GridLayout(q.getOptions().size(), 1));
                ButtonGroup bg = new ButtonGroup();

                for (int j = 0; j < q.getOptions().size(); j++) {
                    JRadioButton rb = new JRadioButton(q.getOptions().get(j).getText());
                    int qi = i;
                    int oi = j;
                    rb.addActionListener(e -> answers[qi] = oi);
                    bg.add(rb);
                    opts.add(rb);
                }

                p.add(opts, BorderLayout.CENTER);
                qPanel.add(p);
            }

            submitBtn = new JButton("Submit Quiz");
            submitBtn.addActionListener(e -> onSubmit());

            infoLabel = new JLabel("Time Limit: 30 minutes   |   Attempts Allowed: " + quiz.getMaxAttempts());

            JScrollPane scroll = new JScrollPane(qPanel);

            add(infoLabel, BorderLayout.NORTH);
            add(scroll, BorderLayout.CENTER);
            add(submitBtn, BorderLayout.SOUTH);
        }

        private void onSubmit() {
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed > TIME_LIMIT) {
                JOptionPane.showMessageDialog(this,
                        "Time is over. You failed the quiz.",
                        "Timeout",
                        JOptionPane.ERROR_MESSAGE);

                recordAttempt(0, false);
                dispose();
                return;
            }

            Quiz quiz = lesson.getQuiz();
            int correct = quiz.grade(answers);
            boolean passed = correct >= 5; // 50% passing

            recordAttempt(correct, passed);

            JOptionPane.showMessageDialog(this,
                    "You scored " + correct + "/10\n" +
                            (passed ? "Congratulations! You Passed!" : "You Failed."),
                    "Quiz Result",
                    JOptionPane.INFORMATION_MESSAGE);

            // Update course progress *only if student passed*
            if (passed) {
                student.updateCourseProgress(course);
            }

            peopleDB.save();
            db.updateCourse(course);

            dispose();
        }

        private void recordAttempt(int score, boolean passed) {
            int attemptNumber = student.countAttemptsFor(course.getId(), lesson.getId()) + 1;

            QuizAttempt qa = new QuizAttempt(
                    student.getId(),
                    course.getId(),
                    lesson.getId(),
                    score,
                    attemptNumber,
                    passed,
                    System.currentTimeMillis()
            );

            student.addQuizAttempt(qa);
            peopleDB.save();
        }
    }

    private CourseLessonDB db;
    private PeopleDB peopleDB;

    // ======= ثابت: الطالب له 30 دقيقة ========
    private final long TIME_LIMIT = 30 * 60 * 1000; // 30 min

    public QuizFrame(CourseLessonDB db, PeopleDB peopleDB, Course course, Lesson lesson, Student student) {
        this.db = db;
        this.peopleDB = peopleDB;
        this.course = course;
        this.lesson = lesson;
        this.student = student;

        // ======= Check إذا مفيش Quiz للـ Lesson =======
        if (lesson.getQuiz() == null) {
            JOptionPane.showMessageDialog(this, "No quiz prepared for this lesson.");
            dispose();
            return;
        }

        // ======= Check Retry Limit =======
        int attempts = student.countAttemptsFor(course.getId(), lesson.getId());
        if (attempts >= 1) {
            JOptionPane.showMessageDialog(this,
                    "You already used your allowed attempt.\nRetry limit = 1 only.",
                    "No Attempts Left",
                    JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        // ======= Load Quiz Questions =======
        Quiz quiz = lesson.getQuiz();
        if (quiz.getQuestions().size() != 10) {
            JOptionPane.showMessageDialog(this,
                    "Quiz must contain exactly 10 MCQ questions.");
            dispose();
            return;
        }

        answers = new int[10];
        for (int i = 0; i < 10; i++) answers[i] = -1;

        setTitle("Quiz – " + lesson.getTitle());
        setSize(900, 700);
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
            p.setBorder(BorderFactory.createTitledBorder("Q" + (i + 1) + ": " + q.getText()));

            JPanel opts = new JPanel(new GridLayout(q.getOptions().size(), 1));
            ButtonGroup bg = new ButtonGroup();

            for (int j = 0; j < q.getOptions().size(); j++) {
                JRadioButton rb = new JRadioButton(q.getOptions().get(j).getText());
                int qi = i;
                int oi = j;
                rb.addActionListener(e -> answers[qi] = oi);
                bg.add(rb);
                opts.add(rb);
            }

            p.add(opts, BorderLayout.CENTER);
            qPanel.add(p);
        }

        submitBtn = new JButton("Submit Quiz");
        submitBtn.addActionListener(e -> onSubmit());

        infoLabel = new JLabel("Time Limit: 30 minutes   |   Attempts Allowed: 1");

        JScrollPane scroll = new JScrollPane(qPanel);

        add(infoLabel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(submitBtn, BorderLayout.SOUTH);
    }

    private void onSubmit() {

        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > TIME_LIMIT) {
            JOptionPane.showMessageDialog(this,
                    "Time is over. You failed the quiz.",
                    "Timeout",
                    JOptionPane.ERROR_MESSAGE);

            recordAttempt(0, false);
            dispose();
            return;
        }

        Quiz quiz = lesson.getQuiz();
        int correct = quiz.grade(answers);
        boolean passed = correct >= 5; // ====== 50% passing =======

        recordAttempt(correct, passed);

        JOptionPane.showMessageDialog(this,
                "You scored " + correct + "/10\n" +
                        (passed ? "Congratulations! You Passed!" : "You Failed."),
                "Quiz Result",
                JOptionPane.INFORMATION_MESSAGE);

        // Update course progress *only if student passed*
        if (passed) {
            student.updateCourseProgress(course);
        }

        peopleDB.save();
        db.updateCourse(course);

        dispose();
    }

    private void recordAttempt(int score, boolean passed) {

        int attemptNumber = student.countAttemptsFor(course.getId(), lesson.getId()) + 1;

        QuizAttempt qa = new QuizAttempt(
                student.getId(),
                course.getId(),
                lesson.getId(),
                score,
                attemptNumber,
                passed,
                System.currentTimeMillis()
        );

        student.addQuizAttempt(qa);
        peopleDB.save();
    }
}
