public class Lesson {
    private String id;
    private String title;
    private String content;
    private String[] optionalResources;

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
    public void setOptionalResources(String[] optionalResources) { this.optionalResources = optionalResources; }
    public void setId(String id) {this.id = id;}
    public void setTitle(String title) {this.title = title;}
    public void setContent(String content) {this.content = content;}
    public void showLesson() {System.out.println("Lesson: " + title + "\nContent: " + content);}
}

