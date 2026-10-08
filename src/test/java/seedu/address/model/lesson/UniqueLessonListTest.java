package seedu.address.model.lesson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalLessons.ENGLISH;
import static seedu.address.testutil.TypicalLessons.MATHEMATICS;
import static seedu.address.testutil.TypicalLessons.SCIENCE_P6;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.lesson.exceptions.LessonNotFoundException;
import seedu.address.testutil.LessonBuilder;

public class UniqueLessonListTest {

    private final UniqueLessonList list = new UniqueLessonList();

    @Test
    public void contains_andFind() {
        assertFalse(list.contains(MATHEMATICS.getId()));
        assertEquals(Optional.empty(), list.find(MATHEMATICS.getId()));

        list.add(MATHEMATICS);

        assertTrue(list.contains(MATHEMATICS.getId()));
        assertEquals(Optional.of(MATHEMATICS), list.find(MATHEMATICS.getId()));
        assertFalse(list.contains(ENGLISH.getId()));
    }

    @Test
    public void add_keepsAscendingOrderOfId() {
        list.add(ENGLISH);
        list.add(MATHEMATICS);
        list.add(SCIENCE_P6);

        assertEquals(Arrays.asList(MATHEMATICS, SCIENCE_P6, ENGLISH), list.asUnmodifiableObservableList());
    }

    @Test
    public void add_sameId_throwsIllegalArgumentException() {
        list.add(MATHEMATICS);
        Lesson sameId = new LessonBuilder(ENGLISH).withId(MATHEMATICS.getId().value).build();

        assertThrows(IllegalArgumentException.class, UniqueLessonList.MESSAGE_DUPLICATE_ID, () -> list.add(sameId));
    }

    @Test
    public void add_identicalLessonWithDifferentId_isAllowed() {
        list.add(MATHEMATICS);
        Lesson identical = new LessonBuilder(MATHEMATICS).withId(9).build();

        list.add(identical);

        assertEquals(Arrays.asList(MATHEMATICS, identical), list.asUnmodifiableObservableList());
    }

    @Test
    public void remove() {
        list.add(MATHEMATICS);
        list.add(ENGLISH);

        assertEquals(MATHEMATICS, list.remove(MATHEMATICS.getId()));

        assertEquals(Collections.singletonList(ENGLISH), list.asUnmodifiableObservableList());
    }

    @Test
    public void remove_missing_throwsLessonNotFoundException() {
        assertThrows(LessonNotFoundException.class, () -> list.remove(MATHEMATICS.getId()));
    }

    @Test
    public void setLessons_sortsById_andRejectsDuplicateIds() {
        list.setLessons(Arrays.asList(ENGLISH, MATHEMATICS));
        assertEquals(Arrays.asList(MATHEMATICS, ENGLISH), list.asUnmodifiableObservableList());

        assertThrows(IllegalArgumentException.class, () -> list.setLessons(Arrays.asList(MATHEMATICS,
                new LessonBuilder(ENGLISH).withId(MATHEMATICS.getId().value).build())));
    }

    @Test
    public void equals() {
        UniqueLessonList other = new UniqueLessonList();
        assertTrue(list.equals(other));
        assertFalse(list.equals(null));

        list.add(MATHEMATICS);
        assertFalse(list.equals(other));
        other.add(MATHEMATICS);
        assertTrue(list.equals(other));
        assertEquals(list.hashCode(), other.hashCode());
    }
}
