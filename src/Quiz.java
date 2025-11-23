import java.util.ArrayList;

public class Quiz {

    public static final int REQUIRED_QUESTIONS = 10;

    private long timeLimitMillis;
    private int maxAttempts;
    private ArrayList<Question> questions;

    public Quiz() {
        questions = new ArrayList<>();
        timeLimitMillis = 30 * 60 * 1000; // 30 minutes
        maxAttempts = 1;
    }

    public long getTimeLimitMillis() {
        return timeLimitMillis;
    }

    public void setTimeLimitMillis(long timeLimitMillis) {
        this.timeLimitMillis = timeLimitMillis;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public void addQuestion(Question q) {
        if (questions.size() < REQUIRED_QUESTIONS)
            questions.add(q);
    }

    public int grade(int[] answers) {
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (answers[i] == questions.get(i).getCorrectIndex()) score++;
        }
        return score;
    }

    public boolean isPassed(int score) {
        return score >= 5;
    }
}
