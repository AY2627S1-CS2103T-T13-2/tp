package seedu.address.model.lesson;

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
import seedu.address.model.lesson.exceptions.LessonNotFoundException;

/**
 * A list of lessons in which no two lessons have the same ID, kept in ascending order of ID.
 * Two lessons with the same subject, level and timeslot are allowed here: {@code add} rejects them, but a hand-edited
 * data file can contain them, and they are then listed separately instead of being merged.
 */
public class UniqueLessonList implements Iterable<Lesson> {

    public static final String MESSAGE_DUPLICATE_ID = "Lessons must have different IDs.";

    private final ObservableList<Lesson> internalList = FXCollections.observableArrayList();
    private final ObservableList<Lesson> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains a lesson with the given ID.
     */
    public boolean contains(LessonId id) {
        requireNonNull(id);
        return internalList.stream().anyMatch(lesson -> lesson.getId().equals(id));
    }

    /**
     * Returns the lesson with the given ID, if there is one.
     */
    public Optional<Lesson> find(LessonId id) {
        requireNonNull(id);
        return internalList.stream().filter(lesson -> lesson.getId().equals(id)).findFirst();
    }

    /**
     * Adds a lesson to the list, keeping the list in ascending order of ID.
     *
     * @throws IllegalArgumentException if a lesson with the same ID is already in the list.
     */
    public void add(Lesson toAdd) {
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
     * @throws LessonNotFoundException if {@code target} is not in the list.
     */
    public void replace(Lesson target, Lesson edited) {
        requireNonNull(target);
        requireNonNull(edited);
        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new LessonNotFoundException();
        }
        if (!target.getId().equals(edited.getId())) {
            throw new IllegalArgumentException("A replacement lesson must keep the same ID.");
        }
        internalList.set(index, edited);
    }

    /**
     * Removes the lesson with the given ID and returns it.
     *
     * @throws LessonNotFoundException if there is no such lesson.
     */
    public Lesson remove(LessonId id) {
        requireNonNull(id);
        Lesson toRemove = find(id).orElseThrow(LessonNotFoundException::new);
        internalList.remove(toRemove);
        return toRemove;
    }

    /**
     * Replaces the contents of this list with {@code lessons}, sorted by ID.
     *
     * @throws IllegalArgumentException if {@code lessons} contains two lessons with the same ID.
     */
    public void setLessons(List<Lesson> lessons) {
        requireNonNull(lessons);
        Set<LessonId> seen = new HashSet<>();
        for (Lesson lesson : lessons) {
            if (!seen.add(lesson.getId())) {
                throw new IllegalArgumentException(MESSAGE_DUPLICATE_ID);
            }
        }
        List<Lesson> sorted = new ArrayList<>(lessons);
        sorted.sort(Comparator.comparing(Lesson::getId));
        internalList.setAll(sorted);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Lesson> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Lesson> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UniqueLessonList otherList)) {
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
