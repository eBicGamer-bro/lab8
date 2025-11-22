import java.util.ArrayList;

public class Student extends User {

    private ArrayList<Course> enrolledCourses;
    private ArrayList<Progress> progresses;
    private ArrayList<QuizAttempt> quizAttempts; // NEW

    public Student(String id, String name, String email, String hashPassword) {
        super(id, name, email, hashPassword);
        enrolledCourses = new ArrayList<>();
        progresses = new ArrayList<>();
        quizAttempts = new ArrayList<>();
    }

    public ArrayList<Course> getEnrolledCourses() { return enrolledCourses; }
    public ArrayList<Progress> getProgresses() { return progresses; }
    public ArrayList<QuizAttempt> getQuizAttempts() { return quizAttempts; }

    public void setQuizAttempts(ArrayList<QuizAttempt> a) { quizAttempts = a; }

    public void addQuizAttempt(QuizAttempt a) { quizAttempts.add(a); }

    public int countAttemptsFor(String courseId, String lessonId) {
        int c = 0;
        for (QuizAttempt a : quizAttempts) {
            if (a.getCourseId().equals(courseId) && a.getLessonId().equals(lessonId))
                c++;
        }
        return c;
    }

    public void enrollCourse(Course c) {
        enrolledCourses.add(c);
        c.addStudent(this);
        progresses.add(new Progress(c, 0.0));
    }

    public void updateProgress(String cid, double val) {
        for (Progress p : progresses) {
            if (p.getCourse().getId().equals(cid)) {
                p.setPercentage(val);
                return;
            }
        }
    }

    public static class Progress {
        private Course course;
        private double percentage;

        public Progress(Course c, double p) {
            course = c;
            percentage = p;
        }

        public Course getCourse() { return course; }
        public double getPercentage() { return percentage; }
        public void setPercentage(double p) { percentage = p; }
    }

    @Override
    public void showInfo() {
        System.out.println("Student: " + name);
    }
}
