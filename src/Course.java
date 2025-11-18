import java.util.ArrayList;
import java.util.List;

public class Course {
    private String id;
    private String name;
    private String instructorId;
    private String description;
    private ArrayList<Lesson> lessons;
    private ArrayList<Student> students;

    public Course(String id, String name, String instructorId, String description) {
        this.id = id;
        this.name = name;
        this.instructorId = instructorId;
        this.description = description;
        this.lessons = new ArrayList<>();
        this.students = new ArrayList<>();
    }

    public Course(String id, String name) {
        this(id, name, "", "");
    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
    }

    public void removeLesson(Lesson lesson) {
        lessons.remove(lesson);
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void addStudent(Student student) {
        if (!students.contains(student)) students.add(student);
    }

    public void removeStudent(Student student) {
        students.remove(student);
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<String> getStudentIds() {
        List<String> ids = new ArrayList<>();
        for (Student s : students) ids.add(s.getId());
        return ids;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCourseId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTitle() { return name; }
    public void setTitle(String title) { this.name = title; }

    public String getInstructorId() { return instructorId; }
    public void setInstructorId(String instructorId) { this.instructorId = instructorId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}
