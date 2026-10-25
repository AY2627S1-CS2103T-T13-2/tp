package seedu.address.model.student;

import java.util.List;

import seedu.address.model.common.UniqueIdList;
import seedu.address.model.student.exceptions.StudentNotFoundException;

/**
 * A list of students in which no two students have the same ID, kept in ascending order of ID.
 * Two students with the same name and contact are allowed here: {@code add} rejects them, but a hand-edited
 * data file can contain them, and they are then listed separately instead of being merged.
 */
public class UniqueStudentList extends UniqueIdList<Student, StudentId> {

    public static final String MESSAGE_DUPLICATE_ID = "Students must have different IDs.";

    private static final String MESSAGE_REPLACEMENT_ID = "A replacement student must keep the same ID.";

    /**
     * Creates an empty list of students.
     */
    public UniqueStudentList() {
        super(Student::getId, StudentNotFoundException::new, MESSAGE_DUPLICATE_ID, MESSAGE_REPLACEMENT_ID);
    }

    /**
     * Replaces the contents of this list with {@code students}, sorted by ID.
     *
     * @throws IllegalArgumentException if {@code students} contains two students with the same ID.
     */
    public void setStudents(List<Student> students) {
        setAll(students);
    }

}
