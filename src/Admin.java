import java.util.List;

public class Admin {
    private String id;
    private String name;
    private String password;

    public Admin(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPassword() { return password; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }

    // Approve a course
    public void approveCourse(Course course) {
        course.setApprovalStatus(Course.ApprovalStatus.APPROVED);
        System.out.println("Course " + course.getName() + " approved by Admin " + name);
    }

    // Reject a course
    public void rejectCourse(Course course) {
        course.setApprovalStatus(Course.ApprovalStatus.REJECTED);
        System.out.println("Course " + course.getName() + " rejected by Admin " + name);
    }

    // View all PENDING courses
    public void viewPendingCourses(List<Course> courses) {
        System.out.println("Pending courses:");
        for (Course c : courses) {
            if (c.getApprovalStatus() == Course.ApprovalStatus.PENDING) {
                System.out.println("- " + c.getName() + " (" + c.getId() + ")");
            }
        }
    }
}
