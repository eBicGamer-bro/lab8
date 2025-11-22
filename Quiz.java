import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private List<Question> questions;
    private long timeLimitMillis = 30L * 60L * 1000L;
    private int maxAttempts = 2; // Attempt + Retry
    public static final int REQUIRED_QUESTIONS = 10;

    public Quiz() {
        questions = new ArrayList<>();
    }

    public Quiz(List<Question> questions) {
        this.questions = questions;
    }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }

    public long getTimeLimitMillis() { return timeLimitMillis; }
    public void setTimeLimitMillis(long timeLimitMillis) { this.timeLimitMillis = timeLimitMillis; }

    public int getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }

    public int grade(int[] answers) {
        int correct = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (answers[i] == questions.get(i).getCorrectIndex()) {
                correct++;
            }
        }
        return correct;
    }

    public boolean isPassed(int score) {
        return score >= REQUIRED_QUESTIONS / 2;
    }
}
