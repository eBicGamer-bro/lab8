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

    // NEW: only approved courses for students
    public ArrayList<Course> getApprovedCourses() {
        ArrayList<Course> approved = new ArrayList<>();
        for (Course c : courses) {
            if (c.getApprovalStatus() == Course.ApprovalStatus.APPROVED) {
                approved.add(c);
            }
        }
        return approved;
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
                co.put("approvalStatus", c.getApprovalStatus().name());

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

                    // NEW: quiz serialization
                    Quiz q = l.getQuiz();
                    if (q != null) {
                        JSONObject qObj = new JSONObject();
                        qObj.put("timeLimitMillis", q.getTimeLimitMillis());
                        qObj.put("maxAttempts", q.getMaxAttempts());
                        JSONArray qQuestions = new JSONArray();
                        for (Question qu : q.getQuestions()) {
                            JSONObject qo = new JSONObject();
                            qo.put("text", qu.getText());
                            qo.put("correctIndex", qu.getCorrectIndex());
                            JSONArray opts = new JSONArray();
                            if (qu.getOptions() != null) {
                                for (Option opt : qu.getOptions()) opts.put(opt.getText());
                            }
                            qo.put("options", opts);
                            qQuestions.put(qo);
                        }
                        qObj.put("questions", qQuestions);
                        lo.put("quiz", qObj);
                    }

                    lessonsArr.put(lo);
                }
                co.put("lessons", lessonsArr);

                JSONArray studs = new JSONArray();
                for (Student s : c.getStudents()) studs.put(s.getId());
                co.put("students", studs);

                cArr.put(co);
            }

            // lessons array at root (kept for compatibility)
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

                    String status = c.optString("approvalStatus", "PENDING");
                    course.setApprovalStatus(Course.ApprovalStatus.valueOf(status));

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

                            // NEW: quiz deserialization
                            JSONObject qObj = lo.optJSONObject("quiz");
                            if (qObj != null) {
                                Quiz quiz = new Quiz();
                                quiz.setTimeLimitMillis(qObj.optLong("timeLimitMillis", 30L*3600L*1000L));
                                quiz.setMaxAttempts(qObj.optInt("maxAttempts", 2));
                                JSONArray qQuestions = qObj.optJSONArray("questions");
                                if (qQuestions != null) {
                                    ArrayList<Question> qlist = new ArrayList<>();
                                    for (int qq = 0; qq < qQuestions.length(); qq++) {
                                        JSONObject qo = qQuestions.getJSONObject(qq);
                                        Question question = new Question();
                                        question.setText(qo.optString("text", ""));
                                        question.setCorrectIndex(qo.optInt("correctIndex", -1));
                                        JSONArray opts = qo.optJSONArray("options");
                                        ArrayList<Option> olist = new ArrayList<>();
                                        if (opts != null) {
                                            for (int oi = 0; oi < opts.length(); oi++) {
                                                olist.add(new Option(opts.getString(oi)));
                                            }
                                        }
                                        question.setOptions(olist);
                                        qlist.add(question);
                                    }
                                    quiz.setQuestions(qlist);
                                }
                                lesson.setQuiz(quiz);
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