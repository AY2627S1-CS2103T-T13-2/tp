package seedu.address.model.student;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.student.exceptions.StudentNotFoundException;

/**
 * A list of students in which no two students have the same ID, kept in ascending order of ID.
 * Two students with the same name and contact are allowed here: {@code add} rejects them, but a hand-edited
 * data file can contain them, and they are then listed separately instead of being merged.
 */
public class UniqueStudentList implements Iterable<Student> {

    public static final String MESSAGE_DUPLICATE_ID = "Students must have different IDs.";

    private final ObservableList<Student> internalList = FXCollections.observableArrayList();
    private final ObservableList<Student> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains a student with the given ID.
     */
    public boolean contains(StudentId id) {
        requireNonNull(id);
        return internalList.stream().anyMatch(student -> student.getId().equals(id));
    }

    /**
     * Returns the student with the given ID, if there is one.
     */
    public Optional<Student> find(StudentId id) {
        requireNonNull(id);
        return internalList.stream().filter(student -> student.getId().equals(id)).findFirst();
    }

    /**
     * Adds a student to the list, keeping the list in ascending order of ID.
     *
     * @throws IllegalArgumentException if a student with the same ID is already in the list.
     */
    public void add(Student toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd.getId())) {
            throw new IllegalArgumentException(MESSAGE_DUPLICATE_ID);
        }
        int position = 0;
        while (position < internalList.size() && internalList.get(position).getId().compareTo(toAdd.getId()) < 0) {
            position++;
        }
        internalList.add(position, toAdd);
    }

    /**
     * Replaces {@code target} with {@code edited}, which must have the same ID.
     *
     * @throws StudentNotFoundException if {@code target} is not in the list.
     */
    public void replace(Student target, Student edited) {
        requireNonNull(target);
        requireNonNull(edited);
        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new StudentNotFoundException();
        }
        if (!target.getId().equals(edited.getId())) {
            throw new IllegalArgumentException("A replacement student must keep the same ID.");
        }
        internalList.set(index, edited);
    }

    /**
     * Removes the student with the given ID and returns it.
     *
     * @throws StudentNotFoundException if there is no such student.
     */
    public Student remove(StudentId id) {
        requireNonNull(id);
        Student toRemove = find(id).orElseThrow(StudentNotFoundException::new);
        internalList.remove(toRemove);
        return toRemove;
    }

    /**
     * Replaces the contents of this list with {@code students}, sorted by ID.
     *
     * @throws IllegalArgumentException if {@code students} contains two students with the same ID.
     */
    public void setStudents(List<Student> students) {
        requireNonNull(students);
        Set<StudentId> seen = new HashSet<>();
        for (Student student : students) {
            if (!seen.add(student.getId())) {
                throw new IllegalArgumentException(MESSAGE_DUPLICATE_ID);
            }
        }
        List<Student> sorted = new ArrayList<>(students);
        sorted.sort(Comparator.comparing(Student::getId));
        internalList.setAll(sorted);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Student> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Student> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UniqueStudentList otherList)) {
            return false;
        }

        return internalList.equals(otherList.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }

}
