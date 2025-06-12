import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

class Student {
    String name;
    double grade;

    Student(String name, double grade) {
        this.name = name;
        this.grade = grade;
    }
}

public class StudentGradeTrackerGUI extends JFrame {
    private ArrayList<Student> studentList = new ArrayList<>();
    private JTextField nameField, gradeField;
    private JTextArea outputArea;

    public StudentGradeTrackerGUI() {
        setTitle("📚 Student Grade Tracker");
        setSize(550, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Enter Student Details"));

        inputPanel.add(new JLabel("👤 Student Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("🎯 Grade (0–100):"));
        gradeField = new JTextField();
        inputPanel.add(gradeField);

        JButton addButton = new JButton("➕ Add Student");
        inputPanel.add(addButton);

        JButton summaryButton = new JButton("📊 Show Summary");
        inputPanel.add(summaryButton);

        add(inputPanel, BorderLayout.NORTH);
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setBorder(BorderFactory.createTitledBorder("📋 Student Records"));

        add(new JScrollPane(outputArea), BorderLayout.CENTER);
        addButton.addActionListener(e -> addStudent());
        summaryButton.addActionListener(e -> showSummary());
    }

    private void addStudent() {
        String name = nameField.getText().trim();
        String gradeText = gradeField.getText().trim();

        if (name.isEmpty() || gradeText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Please enter both the student's name and their grade.", "Input Needed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double grade = Double.parseDouble(gradeText);

            if (grade < 0 || grade > 100) {
                JOptionPane.showMessageDialog(this, "🚫 Grade should be between 0 and 100.", "Invalid Grade", JOptionPane.ERROR_MESSAGE);
                return;
            }

            studentList.add(new Student(name, grade));
            outputArea.append("✅ Added: " + name + " with grade " + grade + "\n");

            nameField.setText("");
            gradeField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ Please enter a valid numeric grade.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSummary() {
        if (studentList.isEmpty()) {
            outputArea.setText("📭 No students have been added yet.\nPlease enter some student details first.");
            return;
        }

        double total = 0;
        double highest = Double.MIN_VALUE;
        double lowest = Double.MAX_VALUE;

        StringBuilder summary = new StringBuilder("📊 Student Grade Summary:\n\n");

        for (Student s : studentList) {
            summary.append("👤 ").append(s.name).append(" - ").append(s.grade).append("\n");
            total += s.grade;
            highest = Math.max(highest, s.grade);
            lowest = Math.min(lowest, s.grade);
        }

        double average = total / studentList.size();

        summary.append("\n📌 Average Grade: ").append(String.format("%.2f", average));
        summary.append("\n🏆 Highest Grade: ").append(highest);
        summary.append("\n📉 Lowest Grade: ").append(lowest);

        outputArea.setText(summary.toString());
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentGradeTrackerGUI().setVisible(true);
        });
    }
}
