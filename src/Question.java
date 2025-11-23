import java.util.List;

public class Question {
    private String text;
    private List<Option> options;
    private int correctIndex;

    public Question(String text, List<Option> options, int correctIndex) {
        this.text = text;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    public Question() {}

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public List<Option> getOptions() { return options; }
    public void setOptions(List<Option> options) { this.options = options; }

    public int getCorrectIndex() { return correctIndex; }
    public void setCorrectIndex(int correctIndex) { this.correctIndex = correctIndex; }
}
