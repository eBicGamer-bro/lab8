import java.util.ArrayList;
import java.util.List;

public class Question {
    private String text;
    private int correctIndex;
    private ArrayList<Option> options;

    public Question() {
        options = new ArrayList<>();
    }

    public Question(String text) {
        this.text = text;
        this.options = new ArrayList<>();
    }

    public void addOption(String text) {
        options.add(new Option(text));
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public void setCorrectIndex(int idx) {
        this.correctIndex = idx;
    }

    public ArrayList<Option> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<Option> opts) {
        this.options = opts;
    }
}
