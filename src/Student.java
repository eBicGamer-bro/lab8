import java.util.ArrayList;

public class Student extends User {

    private ArrayList<Course> enrolledCourses;
    private ArrayList<Progress> progresses;
    private ArrayList<QuizAttempt> quizAttempts;
    private ArrayList<String> completedLessons;
    // NEW: Certificates list
    private ArrayList<Certificate> certificates;

    public Student(String id, String name, String email, String hashPassword) {
        super(id, name, email, hashPassword);
        enrolledCourses = new ArrayList<>();
        progresses = new ArrayList<>();
        quizAttempts = new ArrayList<>();
        completedLessons = new ArrayList<>();
        certificates = new ArrayList<>();
    }

    public ArrayList<Course> getEnrolledCourses() { return enrolledCourses; }
    public ArrayList<Progress> getProgresses() { return progresses; }
    public ArrayList<QuizAttempt> getQuizAttempts() { return quizAttempts; }
    public ArrayList<String> getCompletedLessons() { return completedLessons; }
    // NEW: Getter for certificates
    public ArrayList<Certificate> getCertificates() { return certificates; }

    public void setProgresses(ArrayList<Progress> progresses) {this.progresses = progresses;}
    public void setQuizAttempts(ArrayList<QuizAttempt> a) { quizAttempts = a; }
    public void setCompletedLessons(ArrayList<String> a) { completedLessons = a; }
    // NEW: Setter for certificates
    public void setCertificates(ArrayList<Certificate> c) { this.certificates = c; }

    public void addQuizAttempt(QuizAttempt a) { quizAttempts.add(a); }

    // NEW: Add a certificate
    public void addCertificate(Certificate c) {
        certificates.add(c);
    }

    // NEW: Check if student already has a certificate for this course
    public boolean hasCertificateForCourse(String courseId) {
        for (Certificate c : certificates) {
            if (c.getCourseId().equals(courseId)) return true;
        }
        return false;
    }

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

    public void markLessonCompleted(String courseId, String lessonId) {
        String key = courseId + ":" + lessonId;
        if (!completedLessons.contains(key))
            completedLessons.add(key);
    }

    public boolean hasCompletedLesson(String courseId, String lessonId) {
        return completedLessons.contains(courseId + ":" + lessonId);
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

    public void updateCourseProgress(Course course) {
        int total = course.getLessons().size();
        int completed = 0;

        for (Lesson l : course.getLessons()) {
            if (hasCompletedLesson(course.getId(), l.getId())) {
                completed++;
            }
        }

        double percentage = (total == 0) ? 0 : ((double) completed / (double) total) * 100.0;
        updateProgress(course.getId(), percentage);
    }
}