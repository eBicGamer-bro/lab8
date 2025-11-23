import java.util.Date;
import java.util.UUID;

public class Certificate {
    private String certificateId;
    private String studentId;
    private String courseId;
    private String courseName;
    private long issueDate;

    public Certificate(String studentId, String courseId, String courseName) {
        this.certificateId = UUID.randomUUID().toString();
        this.studentId = studentId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.issueDate = System.currentTimeMillis();
    }

    // Constructor for loading from DB
    public Certificate(String certificateId, String studentId, String courseId, String courseName, long issueDate) {
        this.certificateId = certificateId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.issueDate = issueDate;
    }

    public String getCertificateId() { return certificateId; }
    public String getStudentId() { return studentId; }
    public String getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }
    public long getIssueDate() { return issueDate; }

    @Override
    public String toString() {
        return "Certificate: " + courseName + " (Issued: " + new Date(issueDate) + ")";
    }
}