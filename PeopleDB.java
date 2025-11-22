import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.util.ArrayList;

public class PeopleDB {
    private final String filename = "people.json";
    private ArrayList<Student> students;
    private ArrayList<Instructor> instructors;
    private ArrayList<Course> courses;

    public PeopleDB() {
        students = new ArrayList<>();
        instructors = new ArrayList<>();
        courses = new ArrayList<>();
        load();
    }

    public void addStudent(Student s) {
        students.add(s);
        save();
    }

    public void addInstructor(Instructor i) {
        instructors.add(i);
        save();
    }

    public void addCourse(Course c) {
        courses.add(c);
        save();
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public ArrayList<Instructor> getInstructors() {
        return instructors;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }


    private void save() {
        try {
            JSONObject obj = new JSONObject();

            JSONArray studentsArr = new JSONArray();
            for (Student s : students) {
                JSONObject so = new JSONObject();
                so.put("id", s.getId());
                so.put("name", s.getName());
                so.put("email", s.getEmail());
                so.put("hashPassword", s.getHashPassword());

                // quiz attempts
                JSONArray attempts = new JSONArray();
                for (QuizAttempt a : s.getQuizAttempts()) {
                    JSONObject ao = new JSONObject();
                    ao.put("courseId", a.getCourseId());
                    ao.put("lessonId", a.getLessonId());
                    ao.put("score", a.getScore());
                    ao.put("attemptNumber", a.getAttemptNumber());
                    ao.put("passed", a.isPassed());
                    ao.put("timestamp", a.getTimestamp());
                    ao.put("studentId", a.getStudentId());
                    attempts.put(ao);
                }
                so.put("quizAttempts", attempts);

                studentsArr.put(so);
            }

            JSONArray instructorsArr = new JSONArray();
            for (Instructor ins : instructors) {
                JSONObject io = new JSONObject();
                io.put("id", ins.getId());
                io.put("name", ins.getName());
                io.put("email", ins.getEmail());
                io.put("hashPassword", ins.getHashPassword());
                instructorsArr.put(io);
            }

            obj.put("students", studentsArr);
            obj.put("instructors", instructorsArr);

            try (FileWriter writer = new FileWriter(filename)) {
                writer.write(obj.toString(4));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void load() {
        try {
            File file = new File(filename);
            if (!file.exists()) return;

            StringBuilder text = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) text.append(line);
            }
            if (text.length() == 0) return;

            JSONObject obj = new JSONObject(text.toString());

            students.clear();
            instructors.clear();

            JSONArray sArr = obj.optJSONArray("students");
            if (sArr != null) {
                for (int i = 0; i < sArr.length(); i++) {
                    JSONObject s = sArr.getJSONObject(i);

                    if (s.has("id") && s.has("name") && s.has("email") && s.has("hashPassword")) {
                        Student student = new Student(
                                s.getString("id"),
                                s.getString("name"),
                                s.getString("email"),
                                s.getString("hashPassword")
                        );

                        // quizAttempts
                        JSONArray aArr = s.optJSONArray("quizAttempts");
                        if (aArr != null) {
                            ArrayList<QuizAttempt> attempts = new ArrayList<>();
                            for (int ai = 0; ai < aArr.length(); ai++) {
                                JSONObject ao = aArr.getJSONObject(ai);
                                QuizAttempt qa = new QuizAttempt();
                                qa.setStudentId(ao.optString("studentId", student.getId()));
                                qa.setCourseId(ao.optString("courseId", ""));
                                qa.setLessonId(ao.optString("lessonId", ""));
                                qa.setScore(ao.optInt("score", 0));
                                qa.setAttemptNumber(ao.optInt("attemptNumber", 1));
                                qa.setPassed(ao.optBoolean("passed", false));
                                qa.setTimestamp(ao.optLong("timestamp", System.currentTimeMillis()));
                                attempts.add(qa);
                            }
                            student.setQuizAttempts(attempts);
                        }

                        students.add(student);
                    } else {
                        System.out.println("Error: Missing required fields for student at index " + i);
                    }
                }
            }

            JSONArray iArr = obj.optJSONArray("instructors");
            if (iArr != null) {
                for (int i = 0; i < iArr.length(); i++) {
                    JSONObject ins = iArr.getJSONObject(i);

                    if (ins.has("id") && ins.has("name") && ins.has("email") && ins.has("hashPassword")) {
                        Instructor instructor = new Instructor(
                                ins.getString("id"),
                                ins.getString("name"),
                                ins.getString("email"),
                                ins.getString("hashPassword")
                        );
                        instructors.add(instructor);
                    } else {
                        System.out.println("Error: Missing required fields for instructor at index " + i);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
