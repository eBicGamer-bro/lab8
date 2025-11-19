public class StudentInstructorDB {

    public JsonDatabase<Student> students;
    public JsonDatabase<Instructor> instructors;

    public StudentInstructorDB() {
        students = new JsonDatabase<>("students.json", Student.class);
        instructors = new JsonDatabase<>("instructors.json", Instructor.class);
    }
}
