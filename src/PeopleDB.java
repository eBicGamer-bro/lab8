import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.util.ArrayList;

public class PeopleDB {
    private final String filename = "people.json";
    private ArrayList<Student> students;
    private ArrayList<Instructor> instructors;
    private ArrayList<Admin> admins;

    public PeopleDB() {
        students = new ArrayList<>();
        instructors = new ArrayList<>();
        admins = new ArrayList<>();
        load();
    }

    public ArrayList<Student> getStudents() { return students; }
    public ArrayList<Instructor> getInstructors() { return instructors; }
    public ArrayList<Admin> getAdmins() { return admins; }

    public Student findStudentById(String id) {
        for (Student s : students) if (s.getId().equals(id)) return s;
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

    public boolean addAdmin(Admin a) {
        for (Admin adm : admins) {
            if (adm.getId().equalsIgnoreCase(a.getId())) return false;
        }
        admins.add(a);
        save();
        return true;
    }

    public Admin loginAdmin(String id, String password) {
        for (Admin adm : admins) {
            if (adm.getId().equalsIgnoreCase(id) &&
                    adm.getPassword().equals(password))
                return adm;
        }
        return null;
    }

    public void save() {
        try {
            JSONObject obj = new JSONObject();

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

                // NEW: Save Certificates
                JSONArray certs = new JSONArray();
                for (Certificate c : s.getCertificates()) {
                    JSONObject co = new JSONObject();
                    co.put("certificateId", c.getCertificateId());
                    co.put("studentId", c.getStudentId());
                    co.put("courseId", c.getCourseId());
                    co.put("courseName", c.getCourseName());
                    co.put("issueDate", c.getIssueDate());
                    certs.put(co);
                }
                so.put("certificates", certs);

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

            JSONArray adminsArr = new JSONArray();
            for (Admin a : admins) {
                JSONObject ao = new JSONObject();
                ao.put("id", a.getId());
                ao.put("name", a.getName());
                ao.put("password", a.getPassword());
                adminsArr.put(ao);
            }

            obj.put("students", studentsArr);
            obj.put("instructors", instructorsArr);
            obj.put("admins", adminsArr);

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
            admins.clear();

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
                        for (int ci = 0; ci < compArr.length(); ci++)
                            cl.add(compArr.getString(ci));
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

                    // NEW: Load Certificates
                    JSONArray certArr = s.optJSONArray("certificates");
                    if (certArr != null) {
                        ArrayList<Certificate> cList = new ArrayList<>();
                        for (int ci = 0; ci < certArr.length(); ci++) {
                            JSONObject co = certArr.getJSONObject(ci);
                            Certificate cert = new Certificate(
                                    co.optString("certificateId"),
                                    co.optString("studentId"),
                                    co.optString("courseId"),
                                    co.optString("courseName"),
                                    co.optLong("issueDate")
                            );
                            cList.add(cert);
                        }
                        st.setCertificates(cList);
                    }

                    JSONArray pArr = s.optJSONArray("progresses");
                    if (pArr != null) {
                        ArrayList<Student.Progress> plist = new ArrayList<>();
                        for (int pi = 0; pi < pArr.length(); pi++) {
                            JSONObject po = pArr.getJSONObject(pi);
                            String cid = po.optString("courseId", "");
                            double pct = po.optDouble("percentage", 0.0);

                            Course pc = new Course(cid, cid);
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

            JSONArray aArr = obj.optJSONArray("admins");
            if (aArr != null) {
                for (int i = 0; i < aArr.length(); i++) {
                    JSONObject ad = aArr.getJSONObject(i);
                    Admin admin = new Admin(
                            ad.optString("id", ""),
                            ad.optString("name", ""),
                            ad.optString("password", "")
                    );
                    admins.add(admin);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}