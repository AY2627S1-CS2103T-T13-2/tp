package seedu.address.ui;

import java.util.List;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.student.Student;

/**
 * A UI component that displays information of a {@code Lesson}.
 * This is a basic version: the owner of {@code list} and the UI shapes it further.
 */
public class LessonCard extends UiPart<Region> {

    private static final String FXML = "LessonListCard.fxml";

    public final Lesson lesson;

    @FXML
    private HBox cardPane;
    @FXML
    private Label subject;
    @FXML
    private Label id;
    @FXML
    private Label level;
    @FXML
    private Label timeslot;
    @FXML
    private Label fee;
    @FXML
    private Label students;

    /**
     * Creates a {@code LessonCard} with the given {@code Lesson} and the students enrolled in it.
     */
    public LessonCard(Lesson lesson, List<Student> enrolledStudents) {
        super(FXML);
        this.lesson = lesson;
        subject.setText(lesson.getSubject().value);
        id.setText("Lesson ID: " + lesson.getId());
        level.setText("Level: " + lesson.getLevel());
        timeslot.setText(lesson.getTimeslot().toString());
        fee.setText("Fee: " + lesson.getFee());
        students.setText("Students: " + (enrolledStudents.isEmpty() ? "none"
                : enrolledStudents.stream()
                        .map(student -> student.getId() + " (" + student.getName() + ")")
                        .collect(Collectors.joining(", "))));
    }
}
