import java.util.Arrays;

public class Lesson {
    private String id;
    private String title;
    private String content;
    private String[] optionalResources;
    private Quiz quiz;  // NEW

    public Lesson(String id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.optionalResources = new String[0];
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String[] getOptionalResources() { return optionalResources; }

    public void setId(String id) { this.id = id; }
    public void setTitle(String t) { this.title = t; }
    public void setContent(String c) { this.content = c; }
    public void setOptionalResources(String[] r) { this.optionalResources = r; }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public void showLesson() {
        System.out.println(title + "\n" + content);
    }

    public String toString() {
        return title + " | Quiz: " + (quiz != null ? "YES" : "NO") +
                " | Resources: " + Arrays.toString(optionalResources);
    }
}
