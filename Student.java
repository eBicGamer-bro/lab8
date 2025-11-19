import java.util.ArrayList;

public class Student extends User {
    private ArrayList<Course> enrolledCourses;
    private ArrayList<Progress> progresses; // list of course progress

    public Student(String id, String name, String email, String hashPassword) {
        super(id, name, email, hashPassword);
        enrolledCourses = new ArrayList<>();
        progresses = new ArrayList<>();
    }

    public void enrollCourse(Course course) {
        enrolledCourses.add(course);
        course.addStudent(this);
        progresses.add(new Progress(course, 0.0)); // start with 0% progress
    }

    public void updateProgress(String courseId, double value) {
        for (Progress p : progresses) {
            if (p.getCourse().getId().equals(courseId)) {
                p.setPercentage(value);
                return;
            }
        }
        System.out.println("Not enrolled in this course.");
    }

    public void viewProgress() {
        System.out.println("Progress for " + name + ":");
        for (Progress p : progresses) {
            System.out.println("- " + p.getCourse().getName() + ": " + p.getPercentage() + "%");
        }
    }

    @Override
    public void showInfo() {
        System.out.println("Student ID: " + id + ", Name: " + name + ", Email: " + email);
    }

    // Inner class for course progress
    public static class Progress {
        private Course course;
        private double percentage;

        public Progress(Course course, double percentage) {
            this.course = course;
            this.percentage = percentage;
        }

        public Course getCourse() { return course; }
        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }
    }

    public ArrayList<Course> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void setEnrolledCourses(ArrayList<Course> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }

    public ArrayList<Progress> getProgresses() {
        return progresses;
    }

    public void setProgresses(ArrayList<Progress> progresses) {
        this.progresses = progresses;
    }
}
