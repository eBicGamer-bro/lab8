import java.util.ArrayList;

public class Instructor extends User {
    private ArrayList<Course> createdCourses;

    public Instructor(String id, String name, String email, String hashPassword) {
        super(id, name, email, hashPassword);
        createdCourses = new ArrayList<>();
    }

    // Course management
    public void createCourse(Course course) {
        // NEW: mark new course as PENDING
        course.setApprovalStatus(Course.ApprovalStatus.PENDING);
        createdCourses.add(course);
    }

    public void removeCourse(Course course) {
        createdCourses.remove(course);
    }

    public void viewCreatedCourses() {
        System.out.println("Courses created by " + name + ":");
        for (Course c : createdCourses) {
            System.out.println("- " + c.getName() + " [" + c.getApprovalStatus() + "]");
        }
    }

    // Add or remove lessons to a course
    public void addLessonToCourse(Course course, Lesson lesson) {
        if (createdCourses.contains(course)) {
            course.addLesson(lesson);
        } else {
            System.out.println("You did not create this course.");
        }
    }

    public void removeLessonFromCourse(Course course, Lesson lesson) {
        if (createdCourses.contains(course)) {
            course.removeLesson(lesson);
        } else {
            System.out.println("You did not create this course.");
        }
    }

    public ArrayList<Course> getCreatedCourses() {
        return createdCourses;
    }

    public void setCreatedCourses(ArrayList<Course> createdCourses) {
        this.createdCourses = createdCourses;
    }

    @Override
    public void showInfo() {
        System.out.println("Instructor ID: " + id + ", Name: " + name + ", Email: " + email);
    }
}
