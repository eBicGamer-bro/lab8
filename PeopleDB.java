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
            obj.put("students", new JSONArray(students));
            obj.put("instructors", new JSONArray(instructors));
            obj.put("courses", new JSONArray(courses));

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

