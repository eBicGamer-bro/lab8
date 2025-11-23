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

    public ArrayList<Student> getStudents() { return students; }
    public ArrayList<Instructor> getInstructors() { return instructors; }
    public ArrayList<Course> getCourses() { return courses; }

    public Course findCourseById(String id) {
        for (Course c : courses) {
            if (c.getId().equals(id)) return c;
        }
        return null;
    }

    public Student findStudentById(String id) {
        for (Student s : students) {
            if (s.getId().equals(id)) return s;
        }
        return null;
    }

    public void updateStudent(Student updated) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId().equals(updated.getId())) {
                students.set(i, updated);
                save();
                return;
            }
        }
        students.add(updated);
        save();
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

    public void updateCourse(Course updated) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getId().equals(updated.getId())) {
                courses.set(i, updated);
                save();
                return;
            }
        }
        courses.add(updated);
        save();
    }

    public void save() {
        try {
            JSONObject obj = new JSONObject();

            JSONArray coursesArr = new JSONArray();
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
                    String[] ress = l.getOptionalResources();
                    JSONArray resArr = new JSONArray();
                    if (ress != null) {
                        for (String r : ress) resArr.put(r);
                    }
                    lo.put("optionalResources", resArr);
                    lessonsArr.put(lo);
                }
                co.put("lessons", lessonsArr);

                JSONArray studs = new JSONArray();
                for (Student s : c.getStudents()) studs.put(s.getId());
                co.put("students", studs);

                coursesArr.put(co);
            }

            JSONArray studentsArr = new JSONArray();
            for (Student s : students) {
                JSONObject so = new JSONObject();
                so.put("id", s.getId());
                so.put("name", s.getName());
                so.put("email", s.getEmail());
                so.put("hashPassword", s.getHashPassword());

                JSONArray progArr = new JSONArray();
                for (Student.Progress p : s.getProgresses()) {
                    JSONObject po = new JSONObject();
                    po.put("courseId", p.getCourse().getId());
                    po.put("percentage", p.getPercentage());
                    progArr.put(po);
                }
                so.put("progresses", progArr);

                JSONArray comp = new JSONArray();
                for (String key : s.getCompletedLessons()) comp.put(key);
                so.put("completedLessons", comp);

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

            obj.put("courses", coursesArr);
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
            courses.clear();

            JSONArray cArr = obj.optJSONArray("courses");
            if (cArr != null) {
                for (int i = 0; i < cArr.length(); i++) {
                    JSONObject co = cArr.getJSONObject(i);
                    Course c = new Course(
                            co.optString("id", ""),
                            co.optString("name", ""),
                            co.optString("instructorId", ""),
                            co.optString("description", "")
                    );
                    JSONArray lessonsArr = co.optJSONArray("lessons");
                    if (lessonsArr != null) {
                        for (int li = 0; li < lessonsArr.length(); li++) {
                            JSONObject lo = lessonsArr.getJSONObject(li);
                            Lesson l = new Lesson(
                                    lo.optString("id", ""),
                                    lo.optString("title", ""),
                                    lo.optString("content", "")
                            );
                            JSONArray resArr = lo.optJSONArray("optionalResources");
                            if (resArr != null) {
                                String[] res = new String[resArr.length()];
                                for (int r = 0; r < resArr.length(); r++) res[r] = resArr.getString(r);
                                l.setOptionalResources(res);
                            } else l.setOptionalResources(new String[0]);
                            c.addLesson(l);
                        }
                    }
                    JSONArray studs = co.optJSONArray("students");
                    if (studs != null) {
                        for (int si = 0; si < studs.length(); si++) {
                            String sid = studs.getString(si);
                            Student stub = new Student(sid, sid, "", "");
                            c.addStudent(stub);
                        }
                    }
                    courses.add(c);
                }
            }

            JSONArray sArr = obj.optJSONArray("students");
            if (sArr != null) {
                for (int i = 0; i < sArr.length(); i++) {
                    JSONObject s = sArr.getJSONObject(i);
                    Student st = new Student(
                            s.optString("id", ""),
                            s.optString("name", ""),
                            s.optString("email", ""),
                            s.optString("hashPassword", "")
                    );
                    JSONArray compArr = s.optJSONArray("completedLessons");
                    if (compArr != null) {
                        ArrayList<String> cl = new ArrayList<>();
                        for (int ci = 0; ci < compArr.length(); ci++) cl.add(compArr.getString(ci));
                        st.setCompletedLessons(cl);
                    }
                    JSONArray qArr = s.optJSONArray("quizAttempts");
                    if (qArr != null) {
                        ArrayList<QuizAttempt> list = new ArrayList<>();
                        for (int qi = 0; qi < qArr.length(); qi++) {
                            JSONObject ao = qArr.getJSONObject(qi);
                            QuizAttempt qa = new QuizAttempt();
                            qa.setStudentId(ao.optString("studentId", st.getId()));
                            qa.setCourseId(ao.optString("courseId", ""));
                            qa.setLessonId(ao.optString("lessonId", ""));
                            qa.setScore(ao.optInt("score", 0));
                            qa.setAttemptNumber(ao.optInt("attemptNumber", 1));
                            qa.setPassed(ao.optBoolean("passed", false));
                            qa.setTimestamp(ao.optLong("timestamp", System.currentTimeMillis()));
                            list.add(qa);
                        }
                        st.setQuizAttempts(list);
                    }
                    JSONArray pArr = s.optJSONArray("progresses");
                    if (pArr != null) {
                        ArrayList<Student.Progress> plist = new ArrayList<>();
                        for (int pi = 0; pi < pArr.length(); pi++) {
                            JSONObject po = pArr.getJSONObject(pi);
                            String cid = po.optString("courseId", "");
                            double pct = po.optDouble("percentage", 0.0);
                            Course pc = findCourseById(cid);
                            if (pc == null) {
                                pc = new Course(cid, cid);
                                courses.add(pc);
                            }
                            plist.add(new Student.Progress(pc, pct));
                        }
                        st.setProgresses(plist);
                    }
                    students.add(st);
                }
            }

            JSONArray iArr = obj.optJSONArray("instructors");
            if (iArr != null) {
                for (int i = 0; i < iArr.length(); i++) {
                    JSONObject ins = iArr.getJSONObject(i);
                    Instructor instructor = new Instructor(
                            ins.optString("id", ""),
                            ins.optString("name", ""),
                            ins.optString("email", ""),
                            ins.optString("hashPassword", "")
                    );
                    instructors.add(instructor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
