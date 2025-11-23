public class QuizAttempt {
    private String studentId;
    private String courseId;
    private String lessonId;
    private int score;
    private int attemptNumber;
    private boolean passed;
    private long timestamp;

    public QuizAttempt() {}

    public QuizAttempt(String studentId, String courseId, String lessonId,
                       int score, int attemptNumber, boolean passed, long ts) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.lessonId = lessonId;
        this.score = score;
        this.attemptNumber = attemptNumber;
        this.passed = passed;
        this.timestamp = ts;
    }

    public String getStudentId() { return studentId; }
    public String getCourseId() { return courseId; }
    public String getLessonId() { return lessonId; }
    public int getScore() { return score; }
    public int getAttemptNumber() { return attemptNumber; }
    public boolean isPassed() { return passed; }
    public long getTimestamp() { return timestamp; }

    public void setStudentId(String id) { this.studentId = id; }
    public void setCourseId(String id) { this.courseId = id; }
    public void setLessonId(String id) { this.lessonId = id; }
    public void setScore(int s) { this.score = s; }
    public void setAttemptNumber(int n) { this.attemptNumber = n; }
    public void setPassed(boolean p) { this.passed = p; }
    public void setTimestamp(long t) { this.timestamp = t; }
}
