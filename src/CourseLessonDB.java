import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

public class CourseLessonDB {
    private final String filename = "courses.json";
    private ArrayList<Course> courses;
    private ArrayList<Lesson> lessons;

    public CourseLessonDB() {
        courses = new ArrayList<>();
        lessons = new ArrayList<>();
        load();
    }

    public void addCourse(Course c) {
        courses.add(c);
        save();
    }

    public void updateCourse(Course c) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getId().equals(c.getId())) {
                courses.set(i, c);
                save();
                return;
            }
        }
        courses.add(c);
        save();
    }

    public void removeCourse(String courseId) {
        courses.removeIf(course -> course.getId().equals(courseId));
        save();
    }

    public void addLesson(Lesson l) {
        lessons.add(l);
        save();
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public ArrayList<Lesson> getLessons() {
        return lessons;
    }

    public Optional<Course> findCourseById(String id) {
        for (Course c : courses) if (c.getId().equals(id)) return Optional.of(c);
        return Optional.empty();
    }

    private void save() {
        try {
            JSONObject obj = new JSONObject();
            JSONArray cArr = new JSONArray();
            for (Course c : courses) {
                JSONObject co = new JSONObject();
                co.put("id", c.getId());
                co.put("name", c.getName());
                co.put("instructorId", c.getInstructorId());
                co.put("description", c.getDescription());
                JSONArray lessonsArr = new JSONArray();
                for (Lesson l : c.getLessons()) {
                    JSONObject lo = new JSONObject();
                    lo.put("id", l.getId());
                    lo.put("title", l.getTitle());
                    lo.put("content", l.getContent());
                    JSONArray resArr = new JSONArray();
                    String[] res = l.getOptionalResources();
                    if (res != null) {
                        for (String r : res) resArr.put(r);
                    }
                    lo.put("optionalResources", resArr);
                    lessonsArr.put(lo);
                }
                co.put("lessons", lessonsArr);
                JSONArray studs = new JSONArray();
                for (Student s : c.getStudents()) studs.put(s.getId());
                co.put("students", studs);
                cArr.put(co);
            }
            JSONArray lArr = new JSONArray();
            for (Lesson l : lessons) {
                JSONObject lo = new JSONObject();
                lo.put("id", l.getId());
                lo.put("title", l.getTitle());
                lo.put("content", l.getContent());
                JSONArray resArr = new JSONArray();
                String[] res = l.getOptionalResources();
                if (res != null) {
                    for (String r : res) resArr.put(r);
                }
                lo.put("optionalResources", resArr);
                lArr.put(lo);
            }
            obj.put("courses", cArr);
            obj.put("lessons", lArr);
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
            courses.clear();
            lessons.clear();
            JSONArray cArr = obj.optJSONArray("courses");
            if (cArr != null) {
                for (int i = 0; i < cArr.length(); i++) {
                    JSONObject c = cArr.getJSONObject(i);
                    Course course = new Course(
                            c.optString("id", ""),
                            c.optString("name", ""),
                            c.optString("instructorId", ""),
                            c.optString("description", "")
                    );
                    JSONArray lArr = c.optJSONArray("lessons");
                    if (lArr != null) {
                        for (int j = 0; j < lArr.length(); j++) {
                            JSONObject lo = lArr.getJSONObject(j);
                            Lesson lesson = new Lesson(
                                    lo.optString("id", ""),
                                    lo.optString("title", ""),
                                    lo.optString("content", "")
                            );
                            JSONArray resArr = lo.optJSONArray("optionalResources");
                            if (resArr != null) {
                                String[] res = new String[resArr.length()];
                                for (int r = 0; r < resArr.length(); r++) res[r] = resArr.getString(r);
                                lesson.setOptionalResources(res);
                            } else {
                                lesson.setOptionalResources(new String[0]);
                            }
                            course.addLesson(lesson);
                        }
                    }
                    JSONArray sArr = c.optJSONArray("students");
                    if (sArr != null) {
                        for (int j = 0; j < sArr.length(); j++) {
                            String sid = sArr.getString(j);
                            Student stub = new Student(sid, sid, "", "");
                            course.addStudent(stub);
                        }
                    }
                    courses.add(course);
                }
            }
            JSONArray lArr = obj.optJSONArray("lessons");
            if (lArr != null) {
                for (int i = 0; i < lArr.length(); i++) {
                    JSONObject l = lArr.getJSONObject(i);
                    Lesson lesson = new Lesson(
                            l.optString("id", ""),
                            l.optString("title", ""),
                            l.optString("content", "")
                    );
                    JSONArray resArr = l.optJSONArray("optionalResources");
                    if (resArr != null) {
                        String[] res = new String[resArr.length()];
                        for (int r = 0; r < resArr.length(); r++) res[r] = resArr.getString(r);
                        lesson.setOptionalResources(res);
                    } else {
                        lesson.setOptionalResources(new String[0]);
                    }
                    lessons.add(lesson);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

