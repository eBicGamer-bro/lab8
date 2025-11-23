public class Analytics {

    public double averageScoreStudent(Student s, Course c) {
        double total = 0;
        int count = 0;
        for (int i = 0; i < s.getQuizAttempts().size(); i++) {
            QuizAttempt a = s.getQuizAttempts().get(i);
            if (a.getCourseId().equals(c.getId())) {
                total += a.getScore();
                count++;
            }
        }
        if (count == 0) return 0;
        return total / count;
    }

    public double averageScoreLesson(Course c, Lesson l) {
        double total = 0;
        int count = 0;
        for (int i = 0; i < c.getStudents().size(); i++) {
            Student s = c.getStudents().get(i);
            for (int j = 0; j < s.getQuizAttempts().size(); j++) {
                QuizAttempt a = s.getQuizAttempts().get(j);
                if (a.getCourseId().equals(c.getId()) && a.getLessonId().equals(l.getId())) {
                    total += a.getScore();
                    count++;
                }
            }
        }
        if (count == 0) return 0;
        return total / count;
    }

    public double averageScoreCourse(Course c) {
        double total = 0;
        int count = 0;
        for (int i = 0; i < c.getLessons().size(); i++) {
            Lesson l = c.getLessons().get(i);
            Quiz q = l.getQuiz();
            if (q == null) continue;
            for (int sIndex = 0; sIndex < c.getStudents().size(); sIndex++) {
                Student s = c.getStudents().get(sIndex);
                for (int aIndex = 0; aIndex < s.getQuizAttempts().size(); aIndex++) {
                    QuizAttempt a = s.getQuizAttempts().get(aIndex);
                    if (a.getCourseId().equals(c.getId()) && a.getLessonId().equals(l.getId())) {
                        total += a.getScore();
                        count++;
                    }
                }
            }
        }
        if (count == 0) return 0;
        return total / count;
    }

    public double courseCompletion(Course c) {
        double total = 0;
        int count = 0;
        for (int i = 0; i < c.getStudents().size(); i++) {
            Student s = c.getStudents().get(i);
            int completed = 0;
            int totalLessons = c.getLessons().size();
            for (int j = 0; j < c.getLessons().size(); j++) {
                Lesson l = c.getLessons().get(j);
                if (s.hasCompletedLesson(c.getId(), l.getId())) completed++;
            }
            if (totalLessons > 0) {
                double percentage = ((double) completed / (double) totalLessons) * 100.0;
                total += percentage;
                count++;
            }
        }
        if (count == 0) return 0;
        return total / count;
    }
    public double lessonCompletion(Course c, Lesson l) {
        int totalStudents = c.getStudents().size();
        if (totalStudents == 0) return 0;

        int completedCount = 0;

        for (Student s : c.getStudents()) {
            if (s.hasCompletedLesson(c.getId(), l.getId())) {
                completedCount++;
            }
        }

        return (completedCount / (double) totalStudents) * 100.0;
    }


}


