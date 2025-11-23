import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class CreateQuizDialog extends JDialog {
    private Course course;
    private Lesson lesson;
    private Quiz quiz;
    private ArrayList<Question> questions;
    private JPanel Panel;
    private JTextField textFieldQuestion;
    private JTextField a1TextField;
    private JTextField a2TextField;
    private JTextField a3TextField;
    private JTextField a4TextField;
    private JButton addQuestionButton;
    private JButton saveQuizButton;
    private JComboBox comboBoxAnswer;
    private JTable table1;
    private boolean saved = false;

    private DefaultTableModel tableModel;

    public boolean isSaved() { return saved; }

    public CreateQuizDialog(Window owner, Course course, Lesson lesson) {
        super(owner, "Create Quiz", ModalityType.APPLICATION_MODAL);

        setSize(800, 800);
        setLocationRelativeTo(owner);
        setContentPane(Panel);

        tableModel = new DefaultTableModel(
                new Object[]{"Question", "Correct Answer"}, 0
        );
        table1.setModel(tableModel);
        table1.setEnabled(false);

        this.course = course;
        this.lesson = lesson;
        this.questions = new ArrayList<>();

        addQuestionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (questions.size() == 10) {
                    JOptionPane.showMessageDialog(null, "Max number of questions added");
                    return;
                }

                String question = textFieldQuestion.getText();
                if (question.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Enter a Question!");
                    return;
                }
                String c1 = a1TextField.getText();
                if (c1.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Enter Choice 1!");
                    return;
                }
                String c2 = a2TextField.getText();
                if (c2.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Enter Choice 2!");
                    return;
                }
                String c3 = a3TextField.getText();
                if (c3.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Enter Choice 3!");
                    return;
                }
                String c4 = a4TextField.getText();
                if (c4.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Enter Choice 4!");
                    return;
                }

                int correct = Integer.parseInt(comboBoxAnswer.getSelectedItem().toString());

                ArrayList<Option> options = new ArrayList<>();
                options.add(new Option(c1));
                options.add(new Option(c2));
                options.add(new Option(c3));
                options.add(new Option(c4));

                questions.add(new Question(question, options, correct));

                tableModel.addRow(new Object[]{question, correct});

                JOptionPane.showMessageDialog(null, "Question Added! (" + questions.size() + "/10)");

                textFieldQuestion.setText("");
                a1TextField.setText("");
                a2TextField.setText("");
                a3TextField.setText("");
                a4TextField.setText("");
            }
        });

        saveQuizButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (questions.size() != 10) {
                    JOptionPane.showMessageDialog(null, "Quiz Must Have 10 Questions");
                    return;
                } else {
                    quiz = new Quiz(questions);
                    lesson.setQuiz(quiz);
                    saved = true;
                    dispose();
                }
            }
        });
    }
}
