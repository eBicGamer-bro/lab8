import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) {
        try {
            ValidationAndHashing vh = new ValidationAndHashing();

            // 1. PeopleDB setup
            PeopleDB peopleDB = new PeopleDB();
            Student s1 = new Student("S001", "Ali", "ali@gmail.com", vh.passwordHashing("123"));
            Student s2 = new Student("S002", "Sara", "sara@gmail.com", vh.passwordHashing("123"));
            Instructor i1 = new Instructor("I001", "Omar", "omar@gmail.com", vh.passwordHashing("999"));

            peopleDB.addStudent(s1);
            peopleDB.addStudent(s2);
            peopleDB.addInstructor(i1);

            // 2. CourseLessonDB setup
            CourseLessonDB courseDB = new CourseLessonDB();
            Course c3 = new Course("C003", "J5va Basfffics", "I001", "Intro tfffa");
            Course c2 = new Course("C002", "Pytrrrron Intro", "I001", "hamza is tired ");

            // Only approved courses will appear for students
            c3.setApprovalStatus(Course.ApprovalStatus.APPROVED);
            c2.setApprovalStatus(Course.ApprovalStatus.PENDING);

            Lesson l1 = new Lesson("L001", "Intro Lesson", "Java basics content");
            Lesson l2 = new Lesson("L002", "Advanced Lesson", "Python advanced content");

            c3.addLesson(l1);
            c2.addLesson(l2);

            courseDB.addCourse(c3);
            courseDB.addCourse(c2);
            courseDB.addLesson(l1);
            courseDB.addLesson(l2);

            // 3. Launch GUI
            new WelcomeMenu();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
